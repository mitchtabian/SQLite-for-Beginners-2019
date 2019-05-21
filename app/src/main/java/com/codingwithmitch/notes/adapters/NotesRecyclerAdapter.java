package com.codingwithmitch.notes.adapters;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codingwithmitch.notes.R;
import com.codingwithmitch.notes.models.Note;
import com.codingwithmitch.notes.util.ItemTouchHelperAdapter;
import com.codingwithmitch.notes.util.Utility;


import java.util.ArrayList;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder> implements
        ItemTouchHelperAdapter
{

    private static final String TAG = "NotesRecyclerAdapter";

    private ArrayList<Note> mNotes = new ArrayList<>();
    private OnNoteListener mOnNoteListener;
    private ItemTouchHelper mTouchHelper;

    public NotesRecyclerAdapter(ArrayList<Note> mNotes, OnNoteListener onNoteListener) {
        this.mNotes = mNotes;
        this.mOnNoteListener = onNoteListener;
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
            String month = mNotes.get(position).getTimestamp().substring(0, 2);
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


    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Note fromNote = mNotes.get(fromPosition);
        mNotes.remove(fromNote);
        mNotes.add(toPosition, fromNote);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemSwiped(int position) {
        mNotes.remove(position);
        notifyItemRemoved(position);
    }

    public void setTouchHelper(ItemTouchHelper touchHelper){
        this.mTouchHelper = touchHelper;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnTouchListener,
            GestureDetector.OnGestureListener
    {

        TextView timestamp, title;
        OnNoteListener mOnNoteListener;
        GestureDetector mGestureDetector;

        public ViewHolder(View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            timestamp = itemView.findViewById(R.id.note_timestamp);
            title = itemView.findViewById(R.id.note_title);
            mOnNoteListener = onNoteListener;

            mGestureDetector = new GestureDetector(itemView.getContext(), this);
            itemView.setOnTouchListener(this);
        }


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mGestureDetector.onTouchEvent(event);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            mOnNoteListener.onNoteClick(getAdapterPosition());
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            mTouchHelper.startDrag(this);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }
}








