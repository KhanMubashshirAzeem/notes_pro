package com.example.notespro;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class noteAdapter extends FirestoreRecyclerAdapter<NotesModel, noteAdapter.viewHolder> {

    Context context;
    public noteAdapter(@NonNull FirestoreRecyclerOptions<NotesModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull NotesModel model) {
        holder.titleTv.setText(model.title);
        holder.contentTv.setText(model.content);
        holder.timestampTv.setText(utility.timeStampToString(model.timestamp));

        holder.itemView.setOnClickListener((v)->{
            Intent intent = new Intent(context,NotesDetailsActivity.class);
            intent.putExtra("title",model.title);
            intent.putExtra("content",model.content);
            String docId = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId",docId);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note_item,parent,false);
        return new viewHolder(view);
    }

    static class viewHolder extends RecyclerView.ViewHolder{
        TextView titleTv, contentTv, timestampTv;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.title);
            contentTv = itemView.findViewById(R.id.content);
            timestampTv = itemView.findViewById(R.id.timeStamp);


        }
    }

}
