package com.codingwithmitch.notes.persistence;


import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.codingwithmitch.notes.models.Note;

import java.util.List;

public class NoteRepository {

    private NoteDatabase mNoteDatabase;

    public NoteRepository(Context context) {
        mNoteDatabase = NoteDatabase.getInstance(context);
    }

    public void insertNoteTask(Note note){

    }

    public void updateNoteTask(Note note){

    }

    public LiveData<List<Note>> retrieveNotesTask() {
        return mNoteDatabase.getNoteDao().getNotes();
    }

    public void deleteNoteTask(Note note){

    }
}













