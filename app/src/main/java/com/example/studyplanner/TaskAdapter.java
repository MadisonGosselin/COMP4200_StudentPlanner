package com.example.studyplanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<Task> taskList;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.itemTaskTitle.setText(task.getTitle());
        holder.itemTaskPriority.setText(task.getPriority());
        holder.itemTaskDueDate.setText("Due: " + task.getDueDate());
        holder.itemTaskStatus.setText("Status: " + task.getStatus());

        if ("High".equalsIgnoreCase(task.getPriority())) {
            holder.itemTaskPriority.setTextColor(Color.parseColor("#C62828"));
        } else if ("Medium".equalsIgnoreCase(task.getPriority())) {
            holder.itemTaskPriority.setTextColor(Color.parseColor("#F9A825"));
        } else {
            holder.itemTaskPriority.setTextColor(Color.parseColor("#2E7D32"));
        }

        if ("Completed".equalsIgnoreCase(task.getStatus())) {
            holder.itemTaskStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.itemTaskStatus.setTextColor(Color.parseColor("#F9A825"));
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TaskDetailActivity.class);
            intent.putExtra("taskId", task.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return taskList == null ? 0 : taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView itemTaskTitle, itemTaskPriority, itemTaskDueDate, itemTaskStatus;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            itemTaskTitle = itemView.findViewById(R.id.itemTaskTitle);
            itemTaskPriority = itemView.findViewById(R.id.itemTaskPriority);
            itemTaskDueDate = itemView.findViewById(R.id.itemTaskDueDate);
            itemTaskStatus = itemView.findViewById(R.id.itemTaskStatus);
        }
    }
}
