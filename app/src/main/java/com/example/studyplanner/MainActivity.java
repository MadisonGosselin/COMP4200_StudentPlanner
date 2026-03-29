package com.example.studyplanner;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView welcomeMainText, totalTasksText, completedTasksText, overdueTasksText;
    TextView task1Title, task1Priority, task1DueDate, task1Status;
    TextView task2Title, task2Priority, task2DueDate, task2Status;
    TextView emptyTasksText;
    Button addTaskButton;
    LinearLayout taskCard1, taskCard2;

    TaskDatabase taskDatabase;
    List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeMainText = findViewById(R.id.welcomeMainText);
        totalTasksText = findViewById(R.id.totalTasksText);
        completedTasksText = findViewById(R.id.completedTasksText);
        overdueTasksText = findViewById(R.id.overdueTasksText);

        task1Title = findViewById(R.id.task1Title);
        task1Priority = findViewById(R.id.task1Priority);
        task1DueDate = findViewById(R.id.task1DueDate);
        task1Status = findViewById(R.id.task1Status);

        task2Title = findViewById(R.id.task2Title);
        task2Priority = findViewById(R.id.task2Priority);
        task2DueDate = findViewById(R.id.task2DueDate);
        task2Status = findViewById(R.id.task2Status);

        emptyTasksText = findViewById(R.id.emptyTasksText);

        addTaskButton = findViewById(R.id.addTaskButton);
        taskCard1 = findViewById(R.id.taskCard1);
        taskCard2 = findViewById(R.id.taskCard2);

        taskDatabase = TaskDatabase.getInstance(this);

        String username = getIntent().getStringExtra("username");
        if (username != null && !username.isEmpty()) {
            welcomeMainText.setText("Welcome " + username);
        } else {
            welcomeMainText.setText("Welcome");
        }

        addTaskButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivity(intent);
        });

        loadTasks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
        taskList = taskDatabase.taskDao().getAllTasks();

        int totalTasks = taskList.size();
        int completedTasks = 0;

        for (Task task : taskList) {
            if ("Completed".equalsIgnoreCase(task.getStatus())) {
                completedTasks++;
            }
        }

        int overdueTasks = 0;

        totalTasksText.setText(String.valueOf(totalTasks));
        completedTasksText.setText(String.valueOf(completedTasks));
        overdueTasksText.setText(String.valueOf(overdueTasks));

        if (taskList.isEmpty()) {
            taskCard1.setVisibility(LinearLayout.GONE);
            taskCard2.setVisibility(LinearLayout.GONE);
            emptyTasksText.setText("No tasks available");
            return;
        }

        emptyTasksText.setText("");

        if (taskList.size() >= 1) {
            Task task1 = taskList.get(0);
            taskCard1.setVisibility(LinearLayout.VISIBLE);
            displayTaskInCard(task1, task1Title, task1Priority, task1DueDate, task1Status);

            taskCard1.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
                intent.putExtra("taskId", task1.getId());
                startActivity(intent);
            });
        } else {
            taskCard1.setVisibility(LinearLayout.GONE);
        }

        if (taskList.size() >= 2) {
            Task task2 = taskList.get(1);
            taskCard2.setVisibility(LinearLayout.VISIBLE);
            displayTaskInCard(task2, task2Title, task2Priority, task2DueDate, task2Status);

            taskCard2.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
                intent.putExtra("taskId", task2.getId());
                startActivity(intent);
            });
        } else {
            taskCard2.setVisibility(LinearLayout.GONE);
        }
    }

    private void displayTaskInCard(Task task, TextView titleView, TextView priorityView,
                                   TextView dueDateView, TextView statusView) {

        titleView.setText(task.getTitle());
        priorityView.setText(task.getPriority());
        dueDateView.setText("Due: " + task.getDueDate());
        statusView.setText("Status: " + task.getStatus());

        String priority = task.getPriority();
        if ("High".equalsIgnoreCase(priority)) {
            priorityView.setTextColor(Color.parseColor("#C62828"));
        } else if ("Medium".equalsIgnoreCase(priority)) {
            priorityView.setTextColor(Color.parseColor("#F9A825"));
        } else {
            priorityView.setTextColor(Color.parseColor("#2E7D32"));
        }

        if ("Completed".equalsIgnoreCase(task.getStatus())) {
            statusView.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            statusView.setTextColor(Color.parseColor("#F9A825"));
        }
    }
}