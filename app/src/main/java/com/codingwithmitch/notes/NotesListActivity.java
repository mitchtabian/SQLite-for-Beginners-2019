package com.codingwithmitch.notes;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.codingwithmitch.notes.adapters.NotesRecyclerAdapter;
import com.codingwithmitch.notes.async.AsyncCallback;
import com.codingwithmitch.notes.async.DeleteAsyncTask;
import com.codingwithmitch.notes.async.RetrieveAsyncTask;
import com.codingwithmitch.notes.models.Note;
import com.codingwithmitch.notes.persistence.AppDatabase;
import com.codingwithmitch.notes.persistence.NoteDao;
import com.codingwithmitch.notes.util.Utility;
import com.codingwithmitch.notes.util.VerticalSpacingItemDecorator;


import java.util.ArrayList;
import java.util.List;


public class NotesListActivity extends AppCompatActivity implements
        NotesRecyclerAdapter.OnNoteListener,
        FloatingActionButton.OnClickListener
{

    private static final String TAG = "NotesListActivity";

    //ui components
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    //vars
    private ArrayList<Note> mNotes = new ArrayList<>();
    private NotesRecyclerAdapter mNoteRecyclerAdapter;
    private FloatingActionButton mFab;
    private NoteDao mNoteDao;
    private RetrieveAsyncTask mRetrieveAsyncTask;
    private DeleteAsyncTask mDeleteAsyncTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        mRecyclerView = findViewById(R.id.recyclerView);
        mFab = findViewById(R.id.fab);
        mProgressBar = findViewById(R.id.progress_bar);

        mFab.setOnClickListener(this);

        setupRecyclerView();
        mNoteDao = AppDatabase.getDatabase(this).noteDataDao();

        setSupportActionBar((Toolbar)findViewById(R.id.notes_toolbar));
        setTitle("Notes");
    }


    @Override
    protected void onResume() {
        super.onResume();
//        retrieveNotes();
        insertFakeNotes();
    }

    private void insertFakeNotes(){

        for(int i = 0; i < 1000; i++){
            Note note = new Note();
            note.setTitle("title #" + i);
            note.setContent("content #: " + i);
            note.setTimestamp(Utility.getCurrentTimeStamp());
            mNotes.add(note);
        }
        mNoteRecyclerAdapter.notifyDataSetChanged();
    }

    private void retrieveNotes() {
        Log.d(TAG, "retrieveNotes: called.");
        if(mNotes.size() > 0){
            mNotes.clear();
        }
        showProgressbar();
        mRetrieveAsyncTask = new RetrieveAsyncTask(mNoteDao, new AsyncCallback() {
            @Override
            public void doneRetrievingNotes(List<Note> notes) {
                if(notes.size() > 0){
                    mNotes.addAll(notes);
                }
                mNoteRecyclerAdapter.notifyDataSetChanged();
                hideProgressBar();
            }
        });
        mRetrieveAsyncTask.execute();
    }

    private void deleteNote(Note note) {
        Log.d(TAG, "deleteNote: called.");
        mNotes.remove(note);
        mNoteRecyclerAdapter.notifyDataSetChanged();

        mDeleteAsyncTask = new DeleteAsyncTask(mNoteDao);
        mDeleteAsyncTask.execute(note);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mRetrieveAsyncTask != null){
            mRetrieveAsyncTask.cancelTask();
            mRetrieveAsyncTask.cancel(true);
            mRetrieveAsyncTask = null;
        }
        if(mDeleteAsyncTask != null){
            mDeleteAsyncTask.cancel(true);
            mDeleteAsyncTask = null;
        }
    }


    private void setupRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        mRecyclerView.addItemDecoration(itemDecorator);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        mNoteRecyclerAdapter = new NotesRecyclerAdapter(mNotes, this);
        mRecyclerView.setAdapter(mNoteRecyclerAdapter);
    }

    @Override
    public void onNoteClick(int position) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("selected_note", mNotes.get(position));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, NoteActivity.class);
        startActivity(intent);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            deleteNote(mNotes.get(viewHolder.getAdapterPosition()));
        }
    };

    private void showProgressbar(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        mProgressBar.setVisibility(View.INVISIBLE);
    }

}









