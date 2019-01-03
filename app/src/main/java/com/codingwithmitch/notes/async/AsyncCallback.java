package com.codingwithmitch.notes.async;

import com.codingwithmitch.notes.models.Note;

import java.util.List;

public interface AsyncCallback {

    void doneRetrievingNotes(List<Note> notes);
}
