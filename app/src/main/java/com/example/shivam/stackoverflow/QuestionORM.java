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
    private static final String TABLE_NAME = "question";
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
    private static SQLiteDatabase myDataBase = null;

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " " + COLUMN_ID_TYPE + COMMA_SEP +
                    COLUMN_TITLE  + " " + COLUMN_TITLE_TYPE + COMMA_SEP +
                    COLUMN_AUTHOR + " " + COLUMN_AUTHOR_TYPE + COMMA_SEP +
                    COLUMN_VOTES + " " + COLUMN_VOTES_TYPE + COMMA_SEP +
                    COLUMN_SEARCH + " " + COLUMN_SEARCH_TYPE +
                    ")";

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public void insertQuestion(Context c,JSONArray jarr,String search) throws JSONException {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(c);
        Log.e("ERROR2",String.valueOf(isDatabaseOpened()));
        if(isDatabaseOpened())
        {
            myDataBase = databaseWrapper.getWritableDatabase();
            ContentValues values = postToContentValues2(jarr);
            values.put(QuestionORM.COLUMN_SEARCH,search);
            long questionId = myDataBase.insert(QuestionORM.TABLE_NAME, null, values);
            Log.e(TAG, "Inserted new Question with ID: " + questionId);
            //myDataBase.close();
        }
    }

    private static ContentValues postToContentValues(Question question) {
        ContentValues values = new ContentValues();
        values.put(QuestionORM.COLUMN_ID, question.getID());
        values.put(QuestionORM.COLUMN_TITLE, question.getTitle());
        values.put(QuestionORM.COLUMN_AUTHOR, question.getAuthor());
        values.put(QuestionORM.COLUMN_VOTES, question.getVotes());
        return values;
    }

    private static ContentValues postToContentValues2(JSONArray jsonArray) throws JSONException {
        ContentValues values = new ContentValues();
        for(int i=0;i<jsonArray.length();i++)
        {
            JSONObject job1 = jsonArray.getJSONObject(i);
            if(job1!=null)
            {
                JSONObject job2 = job1.getJSONObject("owner");
                values.put(QuestionORM.COLUMN_ID, job1.getString("question_id"));
                values.put(QuestionORM.COLUMN_TITLE,job1.getString("title"));
                values.put(QuestionORM.COLUMN_AUTHOR,job2.getString("display_name"));
                values.put(QuestionORM.COLUMN_VOTES,job1.getString("score"));
            }
        }
        return values;
    }

    public static boolean isDatabaseOpened() {
        if (myDataBase == null) {
            return false;
        }
        Log.e("OPEN",String.valueOf(myDataBase.isOpen()));
        return myDataBase.isOpen();

    }
}
