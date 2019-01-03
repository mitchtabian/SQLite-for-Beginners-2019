package com.codingwithmitch.notes.adapters;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codingwithmitch.notes.R;
import com.codingwithmitch.notes.models.Note;
import com.codingwithmitch.notes.util.Utility;

import java.util.ArrayList;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder> {

    private static final String TAG = "NotesRecyclerAdapter";
    private ArrayList<Note> mNotes = new ArrayList<>();
    private OnNoteListener mOnNoteListener;


    public NotesRecyclerAdapter(ArrayList<Note> mNotes, OnNoteListener mOnNoteListener) {
        this.mNotes = mNotes;
        this.mOnNoteListener = mOnNoteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_note_list_item, parent, false);
        return new ViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        try{
            String month = mNotes.get(position).getTimestamp().substring(0,2);
            month = Utility.getMonthFromNumber(month);
            String year = mNotes.get(position).getTimestamp().substring(3);
            String timestamp = month + " " + year;
            holder.timestamp.setText(timestamp);
            holder.title.setText(mNotes.get(position).getTitle());
        }catch (NullPointerException e){
            Log.e(TAG, "onBindViewHolder: Null Pointer: " + e.getMessage() );
        }

    }


    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener
    {
        TextView timestamp, title;
        OnNoteListener clickListener;

        public ViewHolder(View itemView, OnNoteListener clickListener) {
            super(itemView);
            timestamp = itemView.findViewById(R.id.note_timestamp);
            title = itemView.findViewById(R.id.note_title);
            this.clickListener = clickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: called.");
            clickListener.onNoteClick(getAdapterPosition());
        }


    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }

}
