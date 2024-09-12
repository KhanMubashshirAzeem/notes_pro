package com.example.notespro.Utility;

import android.annotation.SuppressLint;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class utility {


    public static CollectionReference getcollectionReferenceForNotes(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return FirebaseFirestore.getInstance().collection("notes")
                .document(Objects.requireNonNull(currentUser).getUid()).collection("myNotes");
    }

    @SuppressLint("SimpleDateFormat")
    public static String timeStampToString(Timestamp timestamp){
        return new SimpleDateFormat("MM,dd,yyyy").format(timestamp.toDate());
    }


}
