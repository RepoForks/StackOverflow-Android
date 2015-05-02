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
public class QuestionORM {

    private static final String TAG = "QuestionORM";
    public static final String TABLE_NAME = "question";//////////////////////
    private static final String COMMA_SEP = ", ";
    private static final String COLUMN_ID_TYPE = "TEXT";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE_TYPE = "TEXT";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_AUTHOR_TYPE = "TEXT";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_VOTES_TYPE = "TEXT";
    private static final String COLUMN_VOTES = "votes";
    private static final String COLUMN_SEARCH_TYPE = "TEXT";
    private static final String COLUMN_SEARCH = "search";
    //    private static SQLiteDatabase myDataBase = null;
    private DatabaseWrapper dw;

    SQLiteDatabase myDataBase;
//    public static final String SQL_CREATE_TABLE =
//            "CREATE TABLE " + TABLE_NAME + "("
//                    + COLUMN_ID + " "
//                    + COLUMN_ID_TYPE + COMMA_SEP
//                    + COLUMN_TITLE  + " "
//                    + COLUMN_TITLE_TYPE
//                    + COMMA_SEP
//                    + COLUMN_AUTHOR + " "
//                    + COLUMN_AUTHOR_TYPE
//                    + COMMA_SEP
//                    + COLUMN_VOTES + " "
//                    + COLUMN_VOTES_TYPE
//                    + COMMA_SEP
//                    + COLUMN_SEARCH + " "
//                    + COLUMN_SEARCH_TYPE +
//                    ")";

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"

                    + COLUMN_TITLE + " TEXT, "


                    + COLUMN_AUTHOR + " TEXT, "


                    + COLUMN_VOTES + " TEXT, "


                    + COLUMN_SEARCH + " TEXT)";

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public int insertQuestion(Context c, Question[] questions, String search) throws JSONException {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(c);
        Log.e("ERROR2", String.valueOf(isDatabaseOpened()));
        myDataBase = databaseWrapper.getWritableDatabase();

        long questionId = 0;
        if (isDatabaseOpened()) {

            ContentValues values = postToContentValues2(questions);
            values.put(QuestionORM.COLUMN_SEARCH, search);
            questionId = myDataBase.insert(QuestionORM.TABLE_NAME, null, values);
            Log.e(TAG, "Inserted new Question with ID: " + questions.length);
            myDataBase.close();
        }
        return (int) questionId;
    }


    private  static ContentValues postToContentValues(Question question) {

        ContentValues values = new ContentValues();
        values.put(QuestionORM.COLUMN_ID, question.getID());
        values.put(QuestionORM.COLUMN_TITLE, question.getTitle());
        values.put(QuestionORM.COLUMN_AUTHOR, question.getAuthor());
        values.put(QuestionORM.COLUMN_VOTES, question.getVotes());
        return values;
    }

    private static ContentValues postToContentValues2(Question[]  questions) throws JSONException {
        ContentValues values = new ContentValues();
        for (int i = 0; i < questions.length; i++) {
            //JSONObject job1 = jsonArray.getJSONObject(i);
            if (questions.length != 0) {
                //JSONObject job2 = job1.getJSONObject("owner");
                values.put(QuestionORM.COLUMN_ID, questions[i].getID());
                values.put(QuestionORM.COLUMN_TITLE, questions[i].getTitle());
                values.put(QuestionORM.COLUMN_AUTHOR, questions[i].getAuthor());
                values.put(QuestionORM.COLUMN_VOTES, questions[i].getVotes());
            }
        }
        return values;
    }

    public  boolean isDatabaseOpened() {
        if (myDataBase == null) {
            return false;
        }
        else {
            Log.e("OPEN", String.valueOf(myDataBase.isOpen()));
            return myDataBase.isOpen();
        }

    }


    /*public boolean verification(String _username) throws SQLException {
        int count = -1;
        Cursor c = null;
        try {
            String query = "SELECT COUNT(*) FROM "
                    + TABLE_NAME + " WHERE " + COLUMN_SEARCH + " = ?"
            c = dataBase.rawQuery(query, new String[] {_username});
            if (c.moveToFirst()) {
                count = c.getInt(0);
            }
            return count > 0;
        }
        finally {
            if (c != null) {
                c.close();
            }
        }
    }*/


}
