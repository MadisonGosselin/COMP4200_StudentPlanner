package com.example.studyplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView welcomeMainText, totalTasksText, completedTasksText, overdueTasksText, emptyTasksText;
    Button addTaskButton;

    RecyclerView taskRecyclerView;
    TaskAdapter taskAdapter;

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
        emptyTasksText = findViewById(R.id.emptyTasksText);

        addTaskButton = findViewById(R.id.addTaskButton);
        taskRecyclerView = findViewById(R.id.taskRecyclerView);

        taskDatabase = TaskDatabase.getInstance(this);

        // RecyclerView setup
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this, null);
        taskRecyclerView.setAdapter(taskAdapter);

        // Welcome message
        String username = getIntent().getStringExtra("username");
        if (username != null && !username.isEmpty()) {
            welcomeMainText.setText("Welcome " + username);
        } else {
            welcomeMainText.setText("Welcome");
        }

        // Add Task button
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
        int overdueTasks = 0;

        for (Task task : taskList) {
            if ("Completed".equalsIgnoreCase(task.getStatus())) {
                completedTasks++;
            } else if (isTaskOverdue(task.getDueDate())) {
                overdueTasks++;
            }
        }

        totalTasksText.setText(String.valueOf(totalTasks));
        completedTasksText.setText(String.valueOf(completedTasks));
        overdueTasksText.setText(String.valueOf(overdueTasks));

        if (taskList.isEmpty()) {
            emptyTasksText.setVisibility(TextView.VISIBLE);
        } else {
            emptyTasksText.setVisibility(TextView.GONE);
        }

        taskAdapter.setTaskList(taskList);
    }

    private boolean isTaskOverdue(String dueDateText) {
        if (dueDateText == null || dueDateText.trim().isEmpty()) {
            return false;
        }

        String cleanedDate = dueDateText.trim();

        String[] patterns = {
                "MMMM d yyyy",
                "MMM d yyyy",
                "MMMM d, yyyy",
                "MMM d, yyyy",
                "M/d/yyyy",
                "MM/dd/yyyy",
                "M/d",
                "MM/dd",
                "MMMM d",
                "MMM d"
        };

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        for (String pattern : patterns) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
                inputFormat.setLenient(false);

                Date dueDate;

                if (pattern.equals("M/d") || pattern.equals("MM/dd")
                        || pattern.equals("MMMM d") || pattern.equals("MMM d")) {

                    dueDate = inputFormat.parse(cleanedDate);
                    if (dueDate == null) continue;

                    Calendar tempCal = Calendar.getInstance();
                    tempCal.setTime(dueDate);
                    tempCal.set(Calendar.YEAR, currentYear);
                    dueDate = tempCal.getTime();

                } else if (!cleanedDate.matches(".*\\d{4}.*")) {
                    dueDate = inputFormat.parse(cleanedDate + " " + currentYear);
                } else {
                    dueDate = inputFormat.parse(cleanedDate);
                }

                if (dueDate == null) continue;

                Calendar dueCalendar = Calendar.getInstance();
                dueCalendar.setTime(dueDate);
                dueCalendar.set(Calendar.HOUR_OF_DAY, 0);
                dueCalendar.set(Calendar.MINUTE, 0);
                dueCalendar.set(Calendar.SECOND, 0);
                dueCalendar.set(Calendar.MILLISECOND, 0);

                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);

                return dueCalendar.before(today);

            } catch (Exception e) {

            }
        }

        return false;
    }
}