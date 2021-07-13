package com.example.automessaging.RoomDB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

@Dao
public interface BaseDao<Entity> {

    @Insert
    public void insert(Entity entity);

    @Delete
    public void delete(Entity entity);

    @Update
    public void update(Entity entity);

}

