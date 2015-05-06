package com.example.shivam.stackoverflow;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Shivam on 29/04/15 at 3:34 PM.
 */

/*This class contains function to insert and retrieve items from the database
*trim ensures that leading and trailing blankspaces are removed.
*COLLATE NOCASE ensures case insensitivity.
*/

public class QuestionORM {

    private static final String TAG = "QuestionORM";
    public static final String TABLE_NAME = "question";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_VOTES = "votes";
    private static final String COLUMN_SEARCH = "search";
    private static final String KEY_PRIMARY = "pk";
    private DatabaseWrapper dw;

    SQLiteDatabase myDataBase;
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + KEY_PRIMARY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_ID + " TEXT, "
                    + COLUMN_TITLE + " TEXT, "
                    + COLUMN_AUTHOR + " TEXT, "
                    + COLUMN_VOTES + " TEXT, "
                    + COLUMN_SEARCH + " TEXT UNIQUE)"; //ensures that a particular search query can only be present once in the database.

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    public int insertQuestion(Context c, String ids, String titles, String authors, String votes, String search) {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(c);
        myDataBase = databaseWrapper.getWritableDatabase();
        long questionId = 0;
        if (isDatabaseOpened()) {
            ContentValues values = new ContentValues();
            values.put(QuestionORM.COLUMN_ID, ids);
            values.put(QuestionORM.COLUMN_TITLE, titles);
            values.put(QuestionORM.COLUMN_AUTHOR, authors);
            values.put(QuestionORM.COLUMN_VOTES, votes);
            values.put(QuestionORM.COLUMN_SEARCH, search);
            questionId = myDataBase.insert(QuestionORM.TABLE_NAME, "null", values);
            Log.e(TAG, "Inserted new Question with ID: " + questionId);
            myDataBase.close();
        }
        return (int) questionId;
    }

    //does a particular search string exist in the database
    public boolean doesExist(Context c, String search) {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(c);
        myDataBase = databaseWrapper.getWritableDatabase();
        Cursor cur = myDataBase.rawQuery("SELECT * FROM question WHERE search" + "= '" + search.trim() + "'" + " COLLATE NOCASE", null);
        if (cur.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    //gets an array list of titles corresponding to a particular search query
    public ArrayList<String> getTitleDetails(Context c, String search) throws JSONException {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(c);
        myDataBase = databaseWrapper.getWritableDatabase();
        ArrayList<String> items = new ArrayList<String>();
        Cursor cur = myDataBase.rawQuery("SELECT * FROM question WHERE search" + "= '" + search.trim() + "'"+ " COLLATE NOCASE", null);
        for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()) {
            if(cur.getString(5).trim().equalsIgnoreCase(search.trim())) {
                JSONObject json = new JSONObject(cur.getString(2));
                JSONArray jarr = json.optJSONArray("uniqueTitles");
                for (int i = 0; i < jarr.length(); i++) {
                    items.add(jarr.getString(i));
                }
            }
        }
        cur.moveToFirst();
        return items;

    }

    //gets an array list of authors corresponding to a particular search query
    public ArrayList<String> getAuthorDetails(Context c, String search) throws JSONException {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(c);
        myDataBase = databaseWrapper.getWritableDatabase();
        ArrayList<String> items = new ArrayList<String>();
        Cursor cur = myDataBase.rawQuery("SELECT * FROM question WHERE search" + "= '" + search.trim() + "'"+ " COLLATE NOCASE", null);
        for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()) {
            if(cur.getString(5).trim().equalsIgnoreCase(search.trim())) {
                JSONObject json = new JSONObject(cur.getString(3));
                JSONArray jarr = json.optJSONArray("uniqueAuthors");
                for (int i = 0; i < jarr.length(); i++) {
                    items.add(jarr.getString(i));
                }
            }
        }
        cur.moveToFirst();
        return items;
    }

    //gets an array list of votes corresponding to a particular search query
    public ArrayList<String> getVoteDetails(Context c, String search) throws JSONException {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(c);
        myDataBase = databaseWrapper.getWritableDatabase();
        ArrayList<String> items = new ArrayList<String>();
        Cursor cur = myDataBase.rawQuery("SELECT * FROM question WHERE search" + "= '" + search.trim() + "'"+ " COLLATE NOCASE", null);
        for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()) {
            if (cur.getString(5).trim().equalsIgnoreCase(search.trim())) {
                JSONObject json = new JSONObject(cur.getString(4));
                JSONArray jarr = json.optJSONArray("uniqueVotes");
                for (int i = 0; i < jarr.length(); i++) {
                    items.add(jarr.getString(i));
                }
            }
        }
        cur.moveToFirst();
        return items;
    }

    //gets an array list of question ids corresponding to a particular search query
    public ArrayList<String> getIDDetails(Context c, String search) throws JSONException {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(c);
        myDataBase = databaseWrapper.getWritableDatabase();
        ArrayList<String> items = new ArrayList<String>();
        Cursor cur = myDataBase.rawQuery("SELECT * FROM question WHERE search" + "= '" + search.trim() + "'"+ " COLLATE NOCASE", null);
        for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()) {
            if (cur.getString(5).trim().equalsIgnoreCase(search.trim())) {
                JSONObject json = new JSONObject(cur.getString(1));
                JSONArray jarr = json.optJSONArray("uniqueIDs");
                for (int i = 0; i < jarr.length(); i++) {
                    items.add(jarr.getString(i));
                }
            }
        }
        cur.moveToFirst();
        return items;
    }

    //Checks whether database is open or its instance already exists. Fixes a very big error !!
    public boolean isDatabaseOpened() {
        if (myDataBase == null) {
            return false;
        } else {
            return myDataBase.isOpen();
        }

    }

}
