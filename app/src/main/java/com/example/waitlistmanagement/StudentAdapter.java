package com.example.waitlistmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public StudentAdapter(List<Student> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item, parent, false);
        return new StudentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.textViewStudentName.setText(student.getName());
        holder.textViewCourse.setText(student.getCourse());
        holder.textViewPriority.setText(student.getPriority());
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewStudentName, textViewCourse, textViewPriority;
        public ImageButton buttonEdit, buttonDelete;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStudentName = itemView.findViewById(R.id.textViewStudentName);
            textViewCourse = itemView.findViewById(R.id.textViewCourse);
            textViewPriority = itemView.findViewById(R.id.textViewPriority);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);

            buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onEditClick(position);
                        }
                    }
                }
            });

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }
}
