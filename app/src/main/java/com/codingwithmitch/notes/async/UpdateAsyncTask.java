package com.codingwithmitch.notes.async;

import android.os.AsyncTask;

import com.codingwithmitch.notes.models.Note;
import com.codingwithmitch.notes.persistence.NoteDao;
import com.codingwithmitch.notes.util.Utility;

public class UpdateAsyncTask extends AsyncTask<Note, Void, Void> {

    private NoteDao mNoteDao;

    public UpdateAsyncTask(NoteDao dao) {
        mNoteDao = dao;
    }

    @Override
    protected Void doInBackground(Note... notes) {
        Note note = notes[0];
        mNoteDao.updateNote(note.getTitle(), note.getContent(), note.getTimestamp(), note.getId());
        return null;
    }

}