package com.example.notespro.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notespro.Model.TaskModel;
import com.example.notespro.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<TaskModel> taskModelList;

    public TaskAdapter(List<TaskModel> taskModelList) {
        this.taskModelList = taskModelList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_task_layout, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskModel task = taskModelList.get(position);
        holder.taskTitle.setText(task.getTaskTitle());
        holder.checkBoxTask.setChecked(task.isCompleted());

        // Handle check/uncheck
        holder.checkBoxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked); // Update task completed status
        });

        // Handle task deletion
        holder.deleteTaskBtn.setOnClickListener(v -> {
            task.setMarkedForDeletion(true); // Mark task for deletion instead of removing immediately
            notifyItemChanged(position);     // Notify adapter of the change
        });
    }


    @Override
    public int getItemCount() {
        return taskModelList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        CheckBox checkBoxTask;
        ImageButton deleteTaskBtn; // Delete task button

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.titleTask);
            checkBoxTask = itemView.findViewById(R.id.checkBoxTask);
            deleteTaskBtn = itemView.findViewById(R.id.deleteTask); // Reference the delete button
        }
    }
}
