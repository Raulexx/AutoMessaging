package com.example.automessaging;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "client_table")
public class ClientEntity {

    @PrimaryKey (autoGenerate = true)
    @ColumnInfo(name = "ID")
    public int ID;

    @ColumnInfo(name = "Serie")
    public String serie;

    @ColumnInfo (name = "nr_inmatriculare")
    public String nr_inmatriculare;

    @ColumnInfo (name = "asigurat")
    public String nume_asigurat;

    @ColumnInfo (name = "nr_telefon")
    public String nr_telefon;

    @ColumnInfo (name = "from")
    public Long from;

    @ColumnInfo (name = "until") // fosta valabilitate
    public Long until;

    @ColumnInfo (name = "send_flag")
    public  boolean sent;
}
