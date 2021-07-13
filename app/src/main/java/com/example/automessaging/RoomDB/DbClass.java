package com.example.automessaging.RoomDB;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.automessaging.ClientEntity;

@Database(entities = {ClientEntity.class} , version = 5 , exportSchema = false)
public abstract class DbClass extends RoomDatabase {

    public abstract ClientDao getClientDao();

    private static DbClass INSTANCE = null;
    private static  String DB_NAME = "ClientDataBase.db";

    public static DbClass getInstance(Context contxt) {
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(contxt.getApplicationContext(), DbClass.class, DB_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

}
