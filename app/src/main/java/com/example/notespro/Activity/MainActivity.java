package com.example.notespro.Activity;

import android.content.DialogInterface; // Importing DialogInterface to handle dialog box interactions
import android.content.Intent; // Importing Intent to handle activities and data transfer
import android.graphics.PorterDuff; // Importing PorterDuff for handling color filters
import android.os.Bundle; // Importing Bundle to pass data between Android activities
import android.view.MenuItem; // Importing MenuItem to handle menu item interactions
import android.view.View; // Importing View to handle UI elements
import android.widget.ImageView; // Importing ImageView to handle image display
import android.widget.PopupMenu; // Importing PopupMenu to show a menu as a popup
import android.widget.Toast; // Importing Toast to show brief messages

import androidx.appcompat.app.AlertDialog; // Importing AlertDialog to show alert dialog boxes
import androidx.appcompat.app.AppCompatActivity; // Importing AppCompatActivity as the base class for activities
import androidx.core.content.ContextCompat; // Importing ContextCompat to access resources
import androidx.recyclerview.widget.LinearLayoutManager; // Importing LinearLayoutManager to manage RecyclerView
import androidx.recyclerview.widget.RecyclerView; // Importing RecyclerView to handle lists

import com.example.notespro.Model.NotesModel;
import com.example.notespro.R;
import com.example.notespro.databinding.ActivityMainBinding; // Importing ActivityMainBinding for view binding
import com.example.notespro.Adapter.noteAdapter;
import com.example.notespro.Utility.utility;
import com.firebase.ui.firestore.FirestoreRecyclerOptions; // Importing FirestoreRecyclerOptions for Firestore integration
import com.google.firebase.auth.FirebaseAuth; // Importing FirebaseAuth for authentication
import com.google.firebase.firestore.Query; // Importing Query to query Firestore database

public class MainActivity extends AppCompatActivity {

    // Variable for binding views
    ActivityMainBinding binding;
    // RecyclerView to display notes
    RecyclerView recyclerView;
    // ImageView for the menu
    ImageView menuView;
    // Adapter for RecyclerView
    noteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Binding the layout to the activity
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initializing menuView and recyclerView
        menuView = binding.menu;
        recyclerView = binding.recyclerView;

        // Setting color filter for the add note button
        ImageView addNoteImageView = findViewById(R.id.addNoteFloatingBtn);
        addNoteImageView.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);

        // Setting click listener for the add note button to open NotesDetailsActivity
        binding.addNoteFloatingBtn.setOnClickListener((v) -> startActivity(new Intent(MainActivity.this, NotesDetailsActivity.class)));
        // Setting click listener for the menu to show popup menu
        menuView.setOnClickListener((V) -> showMenu());
        // Setting up the RecyclerView with Firestore data
        setUpRecyclerView();
    }

    // Method to show popup menu for logout
    void showMenu() {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, menuView);
        popupMenu.getMenu().add("Logout"); // Adding logout option
        popupMenu.show();
        // Handling menu item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle() == "Logout") {
                    FirebaseAuth.getInstance().signOut(); // Signing out from Firebase
                    Toast.makeText(MainActivity.this, "Logout Successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class)); // Redirecting to login activity
                    finish(); // Finishing current activity
                    return true;
                }
                return false;
            }
        });
    }

    // Method to set up RecyclerView with Firestore data
    void setUpRecyclerView() {
        // Query to get notes collection ordered by timestamp
        Query query = utility.getcollectionReferenceForNotes()
                .orderBy("timestamp", Query.Direction.DESCENDING);
        // Configuring FirestoreRecyclerOptions with the query and NotesModel class
        FirestoreRecyclerOptions<NotesModel> options = new FirestoreRecyclerOptions.Builder<NotesModel>()
                .setQuery(query, NotesModel.class).build();
        // Setting layout manager for RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Creating adapter with Firestore options
        adapter = new noteAdapter(options, this);
        // Setting adapter for RecyclerView
        recyclerView.setAdapter(adapter);
    }

    // Lifecycle method called when activity starts
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening(); // Start listening for Firestore data
    }

    // Lifecycle method called when activity stops
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening(); // Stop listening for Firestore data
    }

    // Lifecycle method called when activity resumes
    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged(); // Notify adapter about data changes
    }

    // Handling back press to show a confirmation dialog
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Alert !");
        builder.setMessage("Do you want to exit?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish(); // Finish activity if user confirms
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "You clicked No.", Toast.LENGTH_SHORT).show();
                        dialog.cancel(); // Cancel dialog if user declines
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show(); // Show the confirmation dialog
    }

    // Method to handle back button click in the layout
    public void BackPress(View view) {
        onBackPressed(); // Call the onBackPressed method
    }
}
