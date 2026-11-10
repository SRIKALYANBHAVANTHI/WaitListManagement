# WaitListManagement 📋

An Android application project for maintaining a student waitlist with local SQLite storage.

## Features represented in the source

- Enter student name and course
- Assign a priority using a selection control
- Display student records in a RecyclerView
- Add new students to the waitlist
- Edit existing student entries
- Delete student entries
- Store student data using SQLite database helper classes

## Tech Stack

- Java
- Android SDK (compile / target SDK 34; minimum SDK 24)
- AndroidX AppCompat
- RecyclerView
- SQLite / SQLiteOpenHelper
- Android XML layouts and resources

## Run Locally

1. Clone the repository:
   ```bash
   git clone https://github.com/SRIKALYANBHAVANTHI/WaitListManagement.git
   ```
2. Open the project in Android Studio.
3. Allow Gradle sync to complete.
4. Run the **app** configuration on an Android emulator or device.

Build a debug APK:

```bash
./gradlew assembleDebug
```

Windows:

```bat
gradlew.bat assembleDebug
```

## Project Structure

- `MainActivity.java` — waitlist screen and add/update/delete interactions
- `Student.java` — student data model
- `StudentAdapter.java` — RecyclerView adapter
- `StudentContract.java` — database schema contract
- `DatabaseHelper.java` / `StudentDBHelper.java` — SQLite helper classes
- `app/src/main/res/` — Android layouts and resources

## Notes

The repository includes both `DatabaseHelper` and `StudentDBHelper` classes. Review whether both are needed and consolidate database access if appropriate before extending the project.

---

Created by [Srikalyan Bhavanthi](https://github.com/SRIKALYANBHAVANTHI).
