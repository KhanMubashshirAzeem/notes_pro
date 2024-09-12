package com.example.notespro.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notespro.Adapter.TaskAdapter;
import com.example.notespro.Model.NotesModel;
import com.example.notespro.Model.TaskModel;
import com.example.notespro.R;
import com.example.notespro.Utility.utility;
import com.example.notespro.databinding.ActivityNotesDetailsBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

public class NotesDetailsActivity extends AppCompatActivity {

    // Binding for layout views
    ActivityNotesDetailsBinding binding;

    // Views for note title, content, and buttons
    EditText etTitle, etContent;
    ImageView add;
    TextView PageTitle;
    Button deleteNoteBtn;

    // Note and task data
    String Title, Content, docId;
    boolean isEditMode = false;

    // RecyclerView for displaying tasks
    RecyclerView taskRecyclerView;
    TaskAdapter taskAdapter;
    List<TaskModel> taskModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize binding and set content view
        binding = ActivityNotesDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize views
        etTitle = binding.AddTitle;
        etContent = binding.AddContent;
        add = binding.saveNote;
        PageTitle = binding.pageTitle;
        deleteNoteBtn = binding.deleteNote;

        // Retrieve note details from intent extras
        Title = getIntent().getStringExtra("title");
        Content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        // Check if we're in edit mode
        if (docId != null && !docId.isEmpty()) {
            isEditMode = true;
        }

        // Set initial values and visibility
        etTitle.setText(Title);
        etContent.setText(Content);
        if (isEditMode) {
            PageTitle.setText("Edit your notes");
            deleteNoteBtn.setVisibility(View.VISIBLE);
        }

        // Set click listeners
        binding.saveNote.setOnClickListener(v -> saveNotesMethod());
        deleteNoteBtn.setOnClickListener(v -> deleteNoteFromFireBase());

        // Initialize task list and adapter
        taskModelList = new ArrayList<>();
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskModelList);
        binding.recyclerView2.setAdapter(taskAdapter);

        // Load existing tasks from Firestore
        loadTasksFromFirestore();

        // Add new task button listener
        binding.addTaskBtn.setOnClickListener(v -> addNewTaskMethod());
    }

    // Method to display a dialog to add a new task
    private void addNewTaskMethod() {
        // Inflate the custom layout for adding a new task
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_task_layout, null);

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Add New Task")
                .setPositiveButton("Save", (dialog, which) -> {
                    // Handle the Save button click
                    EditText taskTitle = dialogView.findViewById(R.id.titleTask);
                    CheckBox taskCompleted = dialogView.findViewById(R.id.checkBoxTask);

                    String title = taskTitle.getText().toString();
                    boolean isCompleted = taskCompleted.isChecked();

                    if (!title.isEmpty()) {
                        // Create new TaskModel
                        TaskModel taskModel = new TaskModel(title, isCompleted);
                        // Add task to Firestore
                        addTaskToFirestore(taskModel);
                    } else {
                        Toast.makeText(this, "Task title is required", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    // Method to add a new task to Firestore
    private void addTaskToFirestore(TaskModel taskModel) {
        if (docId == null || docId.isEmpty()) {
            Toast.makeText(this, "Save the note first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the reference to the current note's tasks collection
        DocumentReference notesDocRef = utility.getcollectionReferenceForNotes().document(docId);

        // Add the task to Firestore
        notesDocRef.collection("tasks").add(taskModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Task successfully added
                taskModelList.add(taskModel);
                taskAdapter.notifyDataSetChanged();
                Toast.makeText(NotesDetailsActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Task failed to add
                Toast.makeText(NotesDetailsActivity.this, "Failed to add task", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to load tasks from Firestore
    private void loadTasksFromFirestore() {
        if (docId == null || docId.isEmpty()) {
            return;
        }

        // Get the reference to the current note's tasks collection
        DocumentReference notesDocRef = utility.getcollectionReferenceForNotes().document(docId);

        // Load tasks from Firestore
        notesDocRef.collection("tasks").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                taskModelList.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    TaskModel taskModel = document.toObject(TaskModel.class);
                    taskModelList.add(taskModel);
                }
                taskAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(NotesDetailsActivity.this, "Failed to load tasks", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteNoteFromFireBase() {
        // Get the reference to the note document
        DocumentReference documentReference = utility.getcollectionReferenceForNotes().document(docId);

        // Delete the note from Firestore
        documentReference.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(NotesDetailsActivity.this, "Notes deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(NotesDetailsActivity.this, "Failed while deleting a note", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveNotesMethod() {
        String notesTitle = etTitle.getText().toString();
        String notesContent = etContent.getText().toString();

        // Validate input
        if (notesTitle.isEmpty()) {
            etTitle.setError("Title is required");
            return;
        } else if (notesContent.isEmpty()) {
            etContent.setError("Content is required");
            return;
        }

        NotesModel notesModel = new NotesModel();
        notesModel.setTitle(notesTitle);
        notesModel.setContent(notesContent);
        notesModel.setTimestamp(Timestamp.now());

        saveNoteToFirebase(notesModel);
    }

    private void saveNoteToFirebase(NotesModel notesModel) {
        DocumentReference documentReference;
        if (isEditMode) {
            documentReference = utility.getcollectionReferenceForNotes().document(docId);
        } else {
            documentReference = utility.getcollectionReferenceForNotes().document();
        }

        documentReference.set(notesModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                saveTasksToFirebase(documentReference);  // Call method to save tasks
            } else {
                Toast.makeText(NotesDetailsActivity.this, "Failed while adding note", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTasksToFirebase(DocumentReference notesDocRef) {
        for (TaskModel taskModel : taskModelList) {
            if (taskModel.isMarkedForDeletion()) {
                // Task is marked for deletion, remove it from Firebase
                notesDocRef.collection("tasks")
                        .whereEqualTo("taskTitle", taskModel.getTaskTitle())
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    document.getReference().delete();  // Delete task
                                }
                            }
                        });
            } else {
                // Add or update task
                notesDocRef.collection("tasks")
                        .whereEqualTo("taskTitle", taskModel.getTaskTitle())
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    document.getReference().set(taskModel, SetOptions.merge());
                                }
                            } else {
                                // Add new task
                                notesDocRef.collection("tasks").add(taskModel);
                            }
                        });
            }
        }

        // Display success message after all tasks are processed
        Toast.makeText(NotesDetailsActivity.this, "Notes and tasks saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    // Handle back button press
    public void BackPress(View view) {
        onBackPressed();
    }
}
