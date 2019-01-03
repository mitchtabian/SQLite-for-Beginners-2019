package com.codingwithmitch.notes.async;

import android.os.AsyncTask;
import android.util.Log;

import com.codingwithmitch.notes.models.Note;
import com.codingwithmitch.notes.persistence.NoteDao;

import java.util.List;

public class RetrieveAsyncTask extends AsyncTask<Void, Void, List<Note>> {

    private static final String TAG = "RetrieveAsyncTask";

    private NoteDao mNoteDao;
    private AsyncCallback mAsyncCallback;

    public RetrieveAsyncTask(NoteDao dao, AsyncCallback callback) {
        mNoteDao = dao;
        mAsyncCallback = callback;
    }

    @Override
    protected List<Note> doInBackground(Void... voids) {
        return mNoteDao.getAllNotes();
    }

    @Override
    protected void onPostExecute(List<Note> notes) {
        super.onPostExecute(notes);
        for(Note note: notes){
            Log.d(TAG, "onPostExecute: " + note.getTitle());
        }
        mAsyncCallback.doneRetrievingNotes(notes);
    }

    public void cancelTask(){
        mAsyncCallback = null;
    }
}