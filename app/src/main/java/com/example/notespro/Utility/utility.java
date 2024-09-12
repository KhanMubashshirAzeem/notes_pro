package com.example.notespro.Utility;

import android.annotation.SuppressLint;

import com.example.notespro.Model.TaskModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class utility {

    // Get reference to the notes collection
    public static CollectionReference getcollectionReferenceForNotes() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return FirebaseFirestore.getInstance().collection("notes")
                .document(Objects.requireNonNull(currentUser).getUid()).collection("myNotes");
    }

    // Get reference to the tasks collection
    public static CollectionReference getcollectionReferenceForTasks(String noteId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return FirebaseFirestore.getInstance().collection("notes")
                .document(Objects.requireNonNull(currentUser).getUid()).collection("myNotes")
                .document(noteId).collection("tasks");
    }

    // Convert Timestamp to String
    @SuppressLint("SimpleDateFormat")
    public static String timeStampToString(Timestamp timestamp) {
        return new SimpleDateFormat("MM,dd,yyyy").format(timestamp.toDate());
    }


}
