package com.codingwithmitch.notes.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.codingwithmitch.notes.models.Note;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase mInstance;
    public abstract NoteDao noteDataDao();
    public static final String DATABASE_NAME = "notes_db";

    public static AppDatabase getDatabase(Context context) {
        if (mInstance == null) {
            mInstance =
                    Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                            .build();
        }
        return mInstance;
    }

    public static void destroyInstance() {
        mInstance = null;
    }
}
