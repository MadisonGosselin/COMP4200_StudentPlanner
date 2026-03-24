package com.example.comp4200_studentplanner;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import kotlinx.coroutines.scheduling.Task;

public class AddEditTaskActivity extends AppCompatActivity {

    private EditText etSubject, etDate, etStartTime, etEndTime, etNotes;
    private TaskDatabase db;
    private int taskId = -1; // -1 means create mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        etSubject   = findViewById(R.id.etSubject);
        etDate      = findViewById(R.id.etDate);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime   = findViewById(R.id.etEndTime);
        etNotes     = findViewById(R.id.etNotes);

        db = TaskDatabase.getInstance(this);

        // Check if we're in edit mode
        if (getIntent().hasExtra("TASK_ID")) {
            taskId = getIntent().getIntExtra("TASK_ID", -1);
            setTitle("Edit Task");
            loadTask(taskId);
        } else {
            setTitle("Add Task");
        }

        findViewById(R.id.btnSave).setOnClickListener(v -> saveTask());
    }

    private void loadTask(int id) {
        new Thread(() -> {
            Task task = db.taskDao().getTaskById(id);
            runOnUiThread(() -> {
                etSubject.setText(task.subject);
                etDate.setText(task.date);
                etStartTime.setText(task.startTime);
                etEndTime.setText(task.endTime);
                etNotes.setText(task.notes);
            });
        }).start();
    }

    private void saveTask() {
        String subject   = etSubject.getText().toString().trim();
        String date      = etDate.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String endTime   = etEndTime.getText().toString().trim();
        String notes     = etNotes.getText().toString().trim();

        // Basic validation
        if (subject.isEmpty()) {
            etSubject.setError("Subject is required");
            return;
        }
        if (date.isEmpty()) {
            etDate.setError("Date is required");
            return;
        }
        if (startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Please enter start and end times", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            if (taskId == -1) {
                // Create mode — insert new task
                Task task = new Task(subject, date, startTime, endTime, notes, false);
                db.taskDao().insert(task);
            } else {
                // Edit mode — update existing task
                Task task = db.taskDao().getTaskById(taskId);
                task.subject   = subject;
                task.date      = date;
                task.startTime = startTime;
                task.endTime   = endTime;
                task.notes     = notes;
                db.taskDao().update(task);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                finish(); // go back to previous screen
            });
        }).start();
    }
}
