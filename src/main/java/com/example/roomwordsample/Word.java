package com.example.roomwordsample;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/* @Annotations identify how each part of this class relates to
an entry in the database. Room uses this information to generate code.*/
@Entity(tableName = "word_table")   //Each @Entity class represents a SQLite table.
public class Word { //class describes the Entity (which represents the SQLite table) for your words.
	@PrimaryKey //(autoGenerate = true)  //Every entity needs a @PrimaryKey(autoGenerate Optional)
	@NonNull        // return value can never be null.
	@ColumnInfo(name = "word") // Assigns (Optional) table column name(s)
	private String mWord;
	public Word(@NonNull String word) {this.mWord = word;}
	public String getWord() {return this.mWord;}  //getter: ALL db fields must be public or use a getter method
	
}
