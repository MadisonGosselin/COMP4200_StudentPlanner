package com.example.studyplanner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditTaskActivity extends AppCompatActivity {

    EditText taskTitleInput, subjectInput, dueDateInput, notesInput;
    RadioGroup priorityGroup;
    Button saveTaskButton, cancelTaskButton;
    TextView addEditTitle;

    TaskDatabase taskDatabase;
    Task currentTask;
    int taskId = -1;
    boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        taskTitleInput = findViewById(R.id.taskTitleInput);
        subjectInput = findViewById(R.id.subjectInput);
        dueDateInput = findViewById(R.id.dueDateInput);
        notesInput = findViewById(R.id.notesInput);
        priorityGroup = findViewById(R.id.priorityGroup);

        saveTaskButton = findViewById(R.id.saveTaskButton);
        cancelTaskButton = findViewById(R.id.cancelTaskButton);
        addEditTitle = findViewById(R.id.addEditTitle);

        taskDatabase = TaskDatabase.getInstance(this);

        taskId = getIntent().getIntExtra("taskId", -1);

        if (taskId != -1) {
            currentTask = taskDatabase.taskDao().getTaskById(taskId);
            if (currentTask != null) {
                isEditMode = true;
                loadTaskData();
            }
        }

        saveTaskButton.setOnClickListener(v -> saveTask());
        cancelTaskButton.setOnClickListener(v -> finish());
    }

    private void loadTaskData() {
        addEditTitle.setText("Edit Task");
        saveTaskButton.setText("Update Task");

        taskTitleInput.setText(currentTask.getTitle());
        subjectInput.setText(currentTask.getSubject());
        dueDateInput.setText(currentTask.getDueDate());
        notesInput.setText(currentTask.getNotes());

        String priority = currentTask.getPriority();
        if ("Low".equalsIgnoreCase(priority)) {
            priorityGroup.check(R.id.priorityLow);
        } else if ("Medium".equalsIgnoreCase(priority)) {
            priorityGroup.check(R.id.priorityMedium);
        } else if ("High".equalsIgnoreCase(priority)) {
            priorityGroup.check(R.id.priorityHigh);
        }
    }

    private void saveTask() {
        String title = taskTitleInput.getText().toString().trim();
        String subject = subjectInput.getText().toString().trim();
        String dueDate = dueDateInput.getText().toString().trim();
        String notes = notesInput.getText().toString().trim();

        if (title.isEmpty()) {
            taskTitleInput.setError("Task title is required");
            taskTitleInput.requestFocus();
            return;
        }

        String priority = "Medium";
        int selectedPriorityId = priorityGroup.getCheckedRadioButtonId();

        if (selectedPriorityId == R.id.priorityLow) {
            priority = "Low";
        } else if (selectedPriorityId == R.id.priorityMedium) {
            priority = "Medium";
        } else if (selectedPriorityId == R.id.priorityHigh) {
            priority = "High";
        }

        if (isEditMode && currentTask != null) {
            currentTask.setTitle(title);
            currentTask.setSubject(subject);
            currentTask.setDueDate(dueDate);
            currentTask.setPriority(priority);
            currentTask.setNotes(notes);

            taskDatabase.taskDao().update(currentTask);
            Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            String status = "Pending";
            Task newTask = new Task(title, subject, dueDate, priority, notes, status);

            taskDatabase.taskDao().insert(newTask);
            Toast.makeText(this, "Task saved successfully", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}