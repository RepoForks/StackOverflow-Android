package com.example.shivam.stackoverflow;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Shivam on 04/05/15 at 9:52 PM.
 */
public class OfflineAdapter extends BaseAdapter {

    static Context context;
    static int layoutResourceId;
    ArrayList<String> qid = new ArrayList<String>();
    ArrayList<String> author = new ArrayList<String>();
    ArrayList<String> title = new ArrayList<String>();
    ArrayList<String> vote = new ArrayList<String>();

    public OfflineAdapter(Context c,int layoutResourceId,ArrayList<String> qid,ArrayList<String> author,ArrayList<String> title,ArrayList<String> vote)
    {

        this.context = c;
        this.layoutResourceId = layoutResourceId;
        this.qid = qid;
        this.author = author;
        this.title = title;
        this.vote = vote;
    }

    @Override
    public int getCount() {
        return 20;
    }

    @Override
    public Object getItem(int position) {
        return qid.get(position);
    }

    @Override
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
            holder.txtTitle.setText(Html.fromHtml(title.get(position)));
            holder.txtTitle2.setText(author.get(position));
            holder.txtTitle3.setText(vote.get(position));
            holder.txtTitle4.setText(qid.get(position));
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
