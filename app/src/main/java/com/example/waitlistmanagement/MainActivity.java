package com.example.waitlistmanagement;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText editTextName, editTextCourse;
    private Spinner spinnerPriority;
    private List<Student> studentList;
    private StudentAdapter adapter;
    private Button buttonSave;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // Initialize views
        editTextName = findViewById(R.id.editTextName);
        editTextCourse = findViewById(R.id.editTextCourse);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        buttonSave = findViewById(R.id.buttonSave);

        // Initialize RecyclerView
        RecyclerView recyclerViewStudents = findViewById(R.id.recyclerViewStudents);
        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));
        studentList = new ArrayList<>();
        adapter = new StudentAdapter(studentList);
        recyclerViewStudents.setAdapter(adapter);

        // Set OnClickListener for the Save button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonSave.getText().toString().equals("Save")) {
                    addStudentToWaitingList();
                } else if (buttonSave.getText().toString().equals("Update")) {
                    int position = (int) buttonSave.getTag();
                    updateStudent(position);
                }
            }
        });

        // Set OnItemClickListener for RecyclerView
        adapter.setOnItemClickListener(new StudentAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                editStudent(position);
            }

            @Override
            public void onDeleteClick(int position) {
                deleteStudent(position);
            }
        });

        // Load existing students from database
        loadStudentsFromDatabase();
    }

    private void loadStudentsFromDatabase() {
        studentList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                StudentContract.StudentEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                StudentContract.StudentEntry.COLUMN_NAME_PRIORITY + " ASC" // Order by priority ascending
        );
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(StudentContract.StudentEntry._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(StudentContract.StudentEntry.COLUMN_NAME_NAME));
            String course = cursor.getString(cursor.getColumnIndexOrThrow(StudentContract.StudentEntry.COLUMN_NAME_COURSE));
            String priority = cursor.getString(cursor.getColumnIndexOrThrow(StudentContract.StudentEntry.COLUMN_NAME_PRIORITY));
            studentList.add(new Student(id, name, course, priority));
        }
        cursor.close();

        // Sort the studentList based on priority levels, with FIFO ordering for students with the same priority
        Collections.sort(studentList, new Comparator<Student>() {
            @Override
            public int compare(Student student1, Student student2) {
                // Custom sorting based on priority levels
                int priority1 = getPriorityLevel(student1.getPriority());
                int priority2 = getPriorityLevel(student2.getPriority());
                if (priority1 == priority2) {
                    // If priorities are equal, compare based on insertion order (id)
                    return Long.compare(student1.getId(), student2.getId());
                } else {
                    return Integer.compare(priority1, priority2);
                }
            }
        });

        adapter.notifyDataSetChanged();
    }

    private int getPriorityLevel(String priority) {
        switch (priority) {
            case "Graduate":
                return 1;
            case "4th Year":
                return 2;
            case "3rd Year":
                return 3;
            case "2nd Year":
                return 4;
            default:
                return 5; // Lowest priority for other cases
        }
    }

    private void addStudentToWaitingList() {
        // Retrieve input values
        String studentName = editTextName.getText().toString().trim();
        String courseName = editTextCourse.getText().toString().trim();
        String priority = spinnerPriority.getSelectedItem().toString();

        // Validate input
        if (studentName.isEmpty() || courseName.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add student to the waiting list
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(StudentContract.StudentEntry.COLUMN_NAME_NAME, studentName);
        values.put(StudentContract.StudentEntry.COLUMN_NAME_COURSE, courseName);
        values.put(StudentContract.StudentEntry.COLUMN_NAME_PRIORITY, priority);

        long newRowId = db.insert(StudentContract.StudentEntry.TABLE_NAME, null, values);
        if (newRowId != -1) {
            // Insert successful
            Student newStudent = new Student(newRowId, studentName, courseName, priority);
            studentList.add(newStudent);

            // Sort the studentList based on priority levels, with FIFO ordering for students with the same priority
            Collections.sort(studentList, new Comparator<Student>() {
                @Override
                public int compare(Student student1, Student student2) {
                    // Custom sorting based on priority levels
                    int priority1 = getPriorityLevel(student1.getPriority());
                    int priority2 = getPriorityLevel(student2.getPriority());
                    if (priority1 == priority2) {
                        // If priorities are equal, compare based on insertion order (id)
                        return Long.compare(student1.getId(), student2.getId());
                    } else {
                        return Integer.compare(priority1, priority2);
                    }
                }
            });

            adapter.notifyDataSetChanged();

            // Clear input fields
            editTextName.getText().clear();
            editTextCourse.getText().clear();

            // Show success message
            Toast.makeText(MainActivity.this, "Student added to waiting list", Toast.LENGTH_SHORT).show();
        } else {
            // Insert failed
            Toast.makeText(MainActivity.this, "Failed to add student", Toast.LENGTH_SHORT).show();
        }
    }

    private void editStudent(int position) {
        Student selectedStudent = studentList.get(position);

        // Populate EditText fields and Spinner with the student's details
        editTextName.setText(selectedStudent.getName());
        editTextCourse.setText(selectedStudent.getCourse());

        // Set the selected priority in the Spinner
        String[] priorities = getResources().getStringArray(R.array.priorities);
        for (int i = 0; i < priorities.length; i++) {
            if (selectedStudent.getPriority().equals(priorities[i])) {
                spinnerPriority.setSelection(i);
                break;
            }
        }

        // Update the Save button text to indicate editing mode
        buttonSave.setText("Update");

        // Save the position of the edited student for updating the list later
        buttonSave.setTag(position);
    }

    private void updateStudent(int position) {
        // Retrieve input values
        String studentName = editTextName.getText().toString().trim();
        String courseName = editTextCourse.getText().toString().trim();
        String priority = spinnerPriority.getSelectedItem().toString();

        // Validate input
        if (studentName.isEmpty() || courseName.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the original student's priority
        String originalPriority = studentList.get(position).getPriority();

        // Update student details in the database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(StudentContract.StudentEntry.COLUMN_NAME_NAME, studentName);
        values.put(StudentContract.StudentEntry.COLUMN_NAME_COURSE, courseName);

        // Check if the priority has been modified
        if (!originalPriority.equals(priority)) {
            values.put(StudentContract.StudentEntry.COLUMN_NAME_PRIORITY, priority);
        }

        long studentId = studentList.get(position).getId();
        String selection = StudentContract.StudentEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(studentId) };

        int count = db.update(
                StudentContract.StudentEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if (count > 0) {
            // Update successful
            Student updatedStudent = studentList.get(position);
            updatedStudent.setName(studentName);
            updatedStudent.setCourse(courseName);

            // Update priority only if modified
            if (!originalPriority.equals(priority)) {
                updatedStudent.setPriority(priority);
            }

            // Sort the studentList based on priority levels
            Collections.sort(studentList, new Comparator<Student>() {
                @Override
                public int compare(Student student1, Student student2) {
                    // Custom sorting based on priority levels
                    int priority1 = getPriorityLevel(student1.getPriority());
                    int priority2 = getPriorityLevel(student2.getPriority());
                    if (priority1 == priority2) {
                        // If priorities are equal, compare based on insertion order (id)
                        return Long.compare(student1.getId(), student2.getId());
                    } else {
                        return Integer.compare(priority1, priority2);
                    }
                }
            });

            adapter.notifyDataSetChanged();

            // Clear input fields
            editTextName.getText().clear();
            editTextCourse.getText().clear();
            spinnerPriority.setSelection(0);

            // Reset the Save button text
            buttonSave.setText("Save");

            // Show success message
            Toast.makeText(MainActivity.this, "Student updated", Toast.LENGTH_SHORT).show();
        } else {
            // Update failed
            Toast.makeText(MainActivity.this, "Failed to update student", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteStudent(int position) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long studentId = studentList.get(position).getId();
        String selection = StudentContract.StudentEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(studentId) };

        int deletedRows = db.delete(StudentContract.StudentEntry.TABLE_NAME, selection, selectionArgs);
        if (deletedRows > 0) {
            // Deletion successful
            studentList.remove(position);
            adapter.notifyItemRemoved(position);

            // Show success message
            Toast.makeText(MainActivity.this, "Student deleted", Toast.LENGTH_SHORT).show();
        } else {
            // Deletion failed
            Toast.makeText(MainActivity.this, "Failed to delete student", Toast.LENGTH_SHORT).show();
        }
    }
}


