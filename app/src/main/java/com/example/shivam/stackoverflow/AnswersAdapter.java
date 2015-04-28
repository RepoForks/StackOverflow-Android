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
public class AnswersAdapter extends ArrayAdapter<Answer> {

    static Context context;
    static int layoutResourceId;
    Answer data[] = null;

    public AnswersAdapter(Context context, int layoutResourceId, Answer[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AnswerHolder holder = null;
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new AnswerHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.answerText);
            holder.txtTitle2 = (TextView)row.findViewById(R.id.answerAuthor);
            holder.txtTitle3 = (TextView)row.findViewById(R.id.answerVotes);
            row.setTag(holder);
        }
        else
        {
            holder = (AnswerHolder)row.getTag();
        }
        Answer hold = data[position];
        /*int limit = 12;
        if (hold.name.length() > limit) {
            holder.txtTitle.setText(hold.name.substring(0, limit)+"...");
        } else {
            holder.txtTitle.setText(hold.name);
        }*/
        if(hold.text!=null) {
            holder.txtTitle.setText(Html.fromHtml(hold.text));
            holder.txtTitle2.setText(hold.author);
            holder.txtTitle3.setText(hold.votes);
        }
        return row;
    }

    static class AnswerHolder
    {
        TextView txtTitle;
        TextView txtTitle2;
        TextView txtTitle3;
    }
}
