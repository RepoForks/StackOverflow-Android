package com.example.shivam.stackoverflow;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;


public class MainActivity extends ActionBarActivity implements OnQueryTextListener {

    SearchView mSearchView;
    StringBuilder builder = new StringBuilder();
    JSONArray mJSONArr;
    JSONObject ob3,ob2,questionsJson;
    JSONObject holdid,holdauthor,holdtitle,holdvotes;
    ArrayList<ArrayList<String>> listHolder = new ArrayList<ArrayList<String>>();
    String holder=null;
    TextView tv;
    QuestionsAdapter adapter;
    OfflineAdapter adapter2;
    ListView questionList;
    ImageView img;
    String val;
    ArrayList<String> sqlids,sqlauthors,sqltitles,sqlvotes;
    boolean exists;
    QuestionORM q = new QuestionORM();
    String url = "https://api.stackexchange.com/2.2/search?order=desc&sort=activity&";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sqlids = new ArrayList<>(20);
        sqlauthors = new ArrayList<>(20);
        sqltitles = new ArrayList<>(20);
        sqlvotes = new ArrayList<>(20);

        questionList = (ListView)findViewById(R.id.questionList);
        tv = (TextView)findViewById(R.id.introText);
        img = (ImageView)findViewById(R.id.introImage);
        questionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedQuestion = ((TextView)(view.findViewById(R.id.questionID))).getText().toString();
                Intent i = new Intent(MainActivity.this,AnswerActivity.class);
                i.putExtra("QUESTION",selectedQuestion);
                startActivity(i);
            }
        });
        Resources r=getResources();
        Drawable d=r.getDrawable(R.color.primary);
        getSupportActionBar().setBackgroundDrawable(d);

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }



    public JSONObject makeRequest(String url) throws IOException, JSONException {

        JSONObject response;
        String jsonString;

        HttpClient httpclient = new DefaultHttpClient();

        HttpUriRequest request = new HttpGet(url);
        request.addHeader("Accept-Encoding", "gzip");

        HttpResponse resp = httpclient.execute(request);
        StatusLine statusLine = resp.getStatusLine();

        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
            Header contentEncoding = resp.getFirstHeader("Content-Encoding");
            InputStream instream = resp.getEntity().getContent();
            if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                instream = new GZIPInputStream(instream);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
            StringBuilder responseString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseString.append(line);
            }
            jsonString = responseString.toString();
            response = new JSONObject(jsonString);
        } else {
            resp.getEntity().getContent().close();
            throw new IOException(statusLine.getReasonPhrase());
        }

        return response;
    }



    public class JSONTask extends AsyncTask<String,String,JSONObject>
    {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                questionsJson = makeRequest(url);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return questionsJson;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                try {
                    ArrayList<String> ids,authors,titles,votes;
                    ids = new ArrayList<String>(20);
                    authors = new ArrayList<String>(20);
                    titles = new ArrayList<String>(20);
                    votes = new ArrayList<String>(20);
                    mJSONArr = jsonObject.getJSONArray("items");
                    img.setVisibility(View.GONE);
                    tv.setVisibility(View.GONE);
                    questionList.setVisibility(View.VISIBLE);
                    Question question[] = new Question[mJSONArr.length()];
                    if (question.length == 0) {
                        img.setVisibility(View.VISIBLE);
                        tv.setVisibility(View.VISIBLE);
                        questionList.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "No Questions found!", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    } else {
                        for (int i = 0; i < mJSONArr.length(); i++) {
                            ob2 = mJSONArr.getJSONObject(i);
                            if (ob2 != null) {
                                ob3 = ob2.getJSONObject("owner");
                                question[i] = new Question(ob2.getString("title"), ob3.getString("display_name"), ob2.getString("score"), ob2.getString("question_id"));
                                ids.add(ob2.getString("question_id"));
                                authors.add(ob3.getString("display_name"));
                                titles.add(ob2.getString("title"));
                                votes.add(ob2.getString("score"));
                            }
                        }
                        adapter = new QuestionsAdapter(MainActivity.this,
                                R.layout.question_list_item, question);

                        holdid = new JSONObject();
                        holdid.put("uniqueIDs", new JSONArray(ids));
                        String _id = holdid.toString();
                        Log.e("VALUE5", _id);


                        holdauthor = new JSONObject();
                        holdauthor.put("uniqueAuthors", new JSONArray(authors));
                        String _auth = holdauthor.toString();
                        Log.e("VALUE4", _auth);

                        holdtitle = new JSONObject();
                        holdtitle.put("uniqueTitles", new JSONArray(titles));
                        String _title = holdtitle.toString();
                        Log.e("VALUE2", _title);

                        holdvotes = new JSONObject();
                        holdvotes.put("uniqueVotes", new JSONArray(votes));
                        String _vote = holdvotes.toString();
                        Log.e("VALUE", _vote);

                        q.insertQuestion3(MainActivity.this, _id, _title, _auth, _vote, val);
                        questionList.setAdapter(adapter);
                        pDialog.dismiss();
                        url = "https://api.stackexchange.com/2.2/search?order=desc&sort=activity&";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class SQLTask extends AsyncTask<String,String,ArrayList<ArrayList<String>>>
    {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected ArrayList<ArrayList<String>> doInBackground(String... params) {
            try {
                sqlauthors = q.getAuthorDetails(MainActivity.this,val);
                sqlids = q.getIDDetails(MainActivity.this,val);
                sqltitles = q.getTitleDetails(MainActivity.this,val);
                sqlvotes = q.getVoteDetails(MainActivity.this,val);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //System.out.println(sqltitles);
            listHolder.add(sqlids);
            listHolder.add(sqltitles);
            listHolder.add(sqlauthors);
            listHolder.add(sqlvotes);
            val = null;
            return listHolder;
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<String>> arrayLists) {
            //System.out.println(listHolder.get(1));
            //System.out.println(sqltitles);
            ArrayList<String> ids = sqlids;
            ArrayList<String> title = sqltitles;
            ArrayList<String> author = sqlauthors;
            ArrayList<String> vote = sqlvotes;
            //System.out.println(title);
            img.setVisibility(View.GONE);
            tv.setVisibility(View.GONE);
            questionList.setVisibility(View.VISIBLE);
            adapter2 = new OfflineAdapter(MainActivity.this,R.layout.question_list_item,ids,author,title,vote);
            questionList.setAdapter(adapter2);
            pDialog.dismiss();
            val = null;
            Toast.makeText(MainActivity.this,"Loaded from DB",Toast.LENGTH_SHORT).show();
            //Log.e("NEWSTRING","value is"+val+" and nothing");
            //url = "https://api.stackexchange.com/2.2/search?order=desc&sort=activity&";

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search for questions");
        mSearchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        mSearchView.setQuery("", false);
        mSearchView.clearFocus();
        mSearchView.setIconified(true);
        try {
            s = URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.e("STRING",s);
        val = s.trim();
        Log.e("VAL",val);
        exists = q.doesExist(MainActivity.this, val);
        Log.e("EXISTS",String.valueOf(exists));

        url+="intitle="+s+"&site=stackoverflow";
        img.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);

        questionList.setVisibility(View.VISIBLE);
        if(exists==true)
        {
            //Toast.makeText(MainActivity.this,val,Toast.LENGTH_SHORT).show();
            new SQLTask().execute();
        }
        else {
            if(isNetworkAvailable()) {
                new JSONTask().execute();
            }
            else
            {
                img.setVisibility(View.VISIBLE);
                tv.setVisibility(View.VISIBLE);
                questionList.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this,"Internet Connection is needed to search for Questions which are not saved offline!",Toast.LENGTH_SHORT).show();

            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}
