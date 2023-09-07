package com.example.notespro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notespro.databinding.ActivityNotesDetailsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NotesDetailsActivity extends AppCompatActivity {

    ActivityNotesDetailsBinding binding;

    EditText etTitle, etContent;
    ImageView add;
    TextView PageTitle;
    String Title, Content, docId;
    boolean isEditMode = false;

    Button deleteNoteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotesDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        etTitle = binding.AddTitle;
        etContent = binding.AddContent;
        add = binding.saveNote;
        PageTitle = binding.pageTitle;
        deleteNoteBtn = binding.deleteNote;

        // Receive Data
        Title = getIntent().getStringExtra("title");
        Content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if (docId != null && !docId.isEmpty()) {
            isEditMode = true;

        }

        etTitle.setText(Title);
        etContent.setText(Content);

        if (isEditMode) {
            PageTitle.setText("Edit your notes");
            deleteNoteBtn.setVisibility(View.VISIBLE);
        }


        binding.saveNote.setOnClickListener((V) -> saveNotesMethod());

        deleteNoteBtn.setOnClickListener((V) -> deleteNoteFromFireBase());

    }

    void deleteNoteFromFireBase() {
        DocumentReference documentReference;
            documentReference = utility.getcollectionReferenceForNotes().document(docId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(NotesDetailsActivity.this, "Notes deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(NotesDetailsActivity.this, "Failed while deleting a note", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void saveNotesMethod() {
        String notesTitle = etTitle.getText().toString();
        String notesContent = etContent.getText().toString();
        if (notesTitle == null || notesTitle.isEmpty()) {
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

    void saveNoteToFirebase(NotesModel notesModel) {
        DocumentReference documentReference;
        if (isEditMode) {
            // Update the note
            documentReference = utility.getcollectionReferenceForNotes().document(docId);
        } else {
            // create new note
            documentReference = utility.getcollectionReferenceForNotes().document();
        }
        documentReference.set(notesModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(NotesDetailsActivity.this, "Notes added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(NotesDetailsActivity.this, "Failed while adding note", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}