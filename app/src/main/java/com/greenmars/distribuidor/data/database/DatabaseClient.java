package com.greenmars.distribuidor.data.database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {
    private static DatabaseClient miInstance;
    private final AppDatabase appDatabase;

    private DatabaseClient(Context context) {
        Context ctx = context.getApplicationContext();
        appDatabase = Room.databaseBuilder(ctx, AppDatabase.class, "db_cart")
                .fallbackToDestructiveMigration()
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (miInstance == null) {
            miInstance = new DatabaseClient(context);
        }
        return miInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
