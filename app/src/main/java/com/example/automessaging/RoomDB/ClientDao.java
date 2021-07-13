package com.example.automessaging.RoomDB;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.automessaging.ClientEntity;

import java.util.List;


@Dao
public interface ClientDao extends BaseDao<ClientEntity> {

    @Query("SELECT * FROM client_table")
    List<ClientEntity> getAllClients();

    @Query("SELECT * FROM client_table WHERE send_flag = 0 ORDER BY ID DESC")
    List<ClientEntity> getExpireSoonClients();

    @Query("DELETE FROM client_table")
    void deleteAll();

    @Query("SELECT * FROM client_table WHERE send_flag = 0 ORDER BY until ASC")
    List<ClientEntity> getExpireSoonClientsOrderedByExpiringDate();

    @Query("SELECT * FROM client_table WHERE send_flag = 0 AND (until - :todaysTimestmap) >= 0 ORDER BY until ASC")
    List<ClientEntity> getExpireSoonClientsOrderedByExpiringDate_2(Long todaysTimestmap); // new Date().getTime()

}
