package com.codingwithmitch.notes.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.codingwithmitch.notes.models.Note;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes")
    LiveData<List<Note>> getNotes();

    @Insert
    long[] insertNotes(Note... notes);

    @Delete
    int delete(Note note);

    @Query("UPDATE notes SET title = :title, content = :content, timestamp = :timestamp WHERE id = :id")
    int updateNote(String title, String content, String timestamp, int id);
}
