package com.example.studyplanner;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Task.class}, version = 1, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();

    private static TaskDatabase instance;

    public static synchronized TaskDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            TaskDatabase.class,
                            "task_database"
                    )
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}