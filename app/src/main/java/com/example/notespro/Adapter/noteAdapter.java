package com.example.notespro.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notespro.Activity.NotesDetailsActivity;
import com.example.notespro.Model.NotesModel;
import com.example.notespro.R;
import com.example.notespro.Utility.utility;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class noteAdapter extends FirestoreRecyclerAdapter<NotesModel, noteAdapter.viewHolder> {

    Context context;
    private int lastPosition = -1;

    public noteAdapter(@NonNull FirestoreRecyclerOptions<NotesModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull NotesModel model) {
        holder.titleTv.setText(model.title);
        holder.contentTv.setText(model.content);
        holder.timestampTv.setText(utility.timeStampToString(model.timestamp));

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(context, NotesDetailsActivity.class);
            intent.putExtra("title", model.title);
            intent.putExtra("content", model.content);
            String docId = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId", docId);
            context.startActivity(intent);
        });

        setAnimation(holder.itemView, position);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note_item, parent, false);
        return new viewHolder(view);
    }

    static class viewHolder extends RecyclerView.ViewHolder {
        TextView titleTv, contentTv, timestampTv;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.title);
            contentTv = itemView.findViewById(R.id.content);
            timestampTv = itemView.findViewById(R.id.timeStamp);


        }
    }


    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale_in);
            view.startAnimation(animation);
            lastPosition = position;
        }
    }


}
