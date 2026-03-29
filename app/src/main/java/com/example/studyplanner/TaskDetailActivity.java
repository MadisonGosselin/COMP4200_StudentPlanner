package com.example.studyplanner;

import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailActivity extends AppCompatActivity {

    Button markCompleteButton, editTaskButton, deleteTaskButton, homeButton;
    TextView detailTaskTitle, detailSubject, detailDueDate, detailPriority, detailStatus, detailNotes;

    TaskDatabase taskDatabase;
    Task currentTask;
    int taskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        markCompleteButton = findViewById(R.id.markCompleteButton);
        editTaskButton = findViewById(R.id.editTaskButton);
        deleteTaskButton = findViewById(R.id.deleteTaskButton);
        homeButton = findViewById(R.id.homeButton);

        detailTaskTitle = findViewById(R.id.detailTaskTitle);
        detailSubject = findViewById(R.id.detailSubject);
        detailDueDate = findViewById(R.id.detailDueDate);
        detailPriority = findViewById(R.id.detailPriority);
        detailStatus = findViewById(R.id.detailStatus);
        detailNotes = findViewById(R.id.detailNotes);

        taskDatabase = TaskDatabase.getInstance(this);

        taskId = getIntent().getIntExtra("taskId", -1);

        if (taskId != -1) {
            currentTask = taskDatabase.taskDao().getTaskById(taskId);
        }

        if (currentTask != null) {
            displayTaskDetails();
        } else {
            Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        markCompleteButton.setOnClickListener(v -> toggleTaskStatus());

        editTaskButton.setOnClickListener(v -> {
            Intent intent = new Intent(TaskDetailActivity.this, AddEditTaskActivity.class);
            intent.putExtra("taskId", currentTask.getId());
            startActivity(intent);
        });

        deleteTaskButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        taskDatabase.taskDao().delete(currentTask);
                        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        homeButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (taskId != -1) {
            currentTask = taskDatabase.taskDao().getTaskById(taskId);
            if (currentTask != null) {
                displayTaskDetails();
            }
        }
    }

    private void displayTaskDetails() {
        detailTaskTitle.setText(currentTask.getTitle());
        detailSubject.setText("Subject: " + currentTask.getSubject());
        detailDueDate.setText("Due Date: " + currentTask.getDueDate());
        detailPriority.setText("Priority: " + currentTask.getPriority());
        detailStatus.setText("Status: " + currentTask.getStatus());
        detailNotes.setText(currentTask.getNotes());

        if ("High".equalsIgnoreCase(currentTask.getPriority())) {
            detailPriority.setTextColor(Color.parseColor("#C62828"));
        } else if ("Medium".equalsIgnoreCase(currentTask.getPriority())) {
            detailPriority.setTextColor(Color.parseColor("#F9A825"));
        } else {
            detailPriority.setTextColor(Color.parseColor("#2E7D32"));
        }

        if ("Completed".equalsIgnoreCase(currentTask.getStatus())) {
            detailStatus.setTextColor(Color.parseColor("#2E7D32"));
            markCompleteButton.setText("Mark as Pending");
        } else {
            detailStatus.setTextColor(Color.parseColor("#F9A825"));
            markCompleteButton.setText("Mark as Completed");
        }
    }

    private void toggleTaskStatus() {
        if ("Completed".equalsIgnoreCase(currentTask.getStatus())) {
            currentTask.setStatus("Pending");
            Toast.makeText(this, "Task changed to pending", Toast.LENGTH_SHORT).show();
        } else {
            currentTask.setStatus("Completed");
            Toast.makeText(this, "Task marked as completed", Toast.LENGTH_SHORT).show();
        }

        taskDatabase.taskDao().update(currentTask);
        displayTaskDetails();
    }
}