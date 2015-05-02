package com.example.shivam.stackoverflow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by Shivam on 26/04/15.
 */
public class QuestionsAdapter extends ArrayAdapter<Question> {

    static Context context;
    static int layoutResourceId;
    Question data[] = null;

    public QuestionsAdapter(Context context, int layoutResourceId, Question[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return 20;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        QuestionHolder holder = null;
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new QuestionHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.questionTitle);
            holder.txtTitle2 = (TextView)row.findViewById(R.id.questionAuthor);
            holder.txtTitle3 = (TextView)row.findViewById(R.id.questionVotes);
            holder.txtTitle4 = (TextView)row.findViewById(R.id.questionID);
            row.setTag(holder);
        }
        else
        {
            holder = (QuestionHolder)row.getTag();
        }
        Question hold = data[position];
        if(hold!=null) {
            holder.txtTitle.setText(Html.fromHtml(hold.title));
            holder.txtTitle2.setText(hold.author);
            holder.txtTitle3.setText(hold.votes);
            holder.txtTitle4.setText(hold.id);
        }
        return row;
    }

    static class QuestionHolder
    {
        TextView txtTitle;
        TextView txtTitle2;
        TextView txtTitle3;
        TextView txtTitle4;
    }
}
