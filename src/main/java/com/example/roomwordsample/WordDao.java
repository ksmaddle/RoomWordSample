package com.example.roomwordsample;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/* Implement a DAO that provides queries for:
    Get all words(abc ordered)
    Inserting a word
    Deleting all words*/


@Dao    //identifies it as a DAO class for Room.
public interface WordDao { //declared as interface (must be interface or abstract class)
	//@Insert @Delete and @Update annotations take care of providingSQL
	
	
	@Insert(onConflict = OnConflictStrategy.IGNORE) // ignores a new word if already in the list
	void insert(Word word);     //Declares method to insert one word
	
	@Query("DELETE FROM word_table") // @Query requires a SQL query as a string parameter.
	void deleteAll();   //declares a method to delete all the words
	
	@Query("SELECT * FROM word_table ORDER BY word ASC") //Returns a list of words sorted in ascending order.
	LiveData<List<Word>> getAlphabetizedWords(); //returned List<Word> is wrapped with LiveData
	
	
}
