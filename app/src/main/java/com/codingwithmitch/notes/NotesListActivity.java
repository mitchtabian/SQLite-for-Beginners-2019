package com.codingwithmitch.notes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.codingwithmitch.notes.models.Note;

public class NotesListActivity extends AppCompatActivity {

    private static final String TAG = "NotesListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        Note note = new Note("some title", "some content for the note", null);

        Log.d(TAG, "onCreate: my first note: " + note.toString());
    }
}
