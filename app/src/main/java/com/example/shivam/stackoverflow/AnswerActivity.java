package com.example.shivam.stackoverflow;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.zip.GZIPInputStream;


public class AnswerActivity extends ActionBarActivity {

    private final String endPoint = "http://api.stackexchange.com/2.2/";
    private String site = "site=stackoverflow";
    String requestUrl=null;
    ///answers?order=desc&sort=activity&filter=!*LVw4pKRvjyBRppf&site=site=stackoverflow
    Answer answers[] = new Answer[20];
    ListView answerList;
    JSONArray mJSONArr;
    AnswersAdapter adapter;
    JSONObject ob3,ob2,answersJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        Bundle extras = getIntent().getExtras();
        String question = extras.getString("QUESTION");
        try {
            question = URLEncoder.encode(question, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //Toast.makeText(AnswerActivity.this,question+"",Toast.LENGTH_SHORT).show();
        answerList = (ListView)findViewById(R.id.answerList);
        //this.url+=
        requestUrl = this.endPoint + "questions/" + question + "/answers?order=desc&sort=activity&filter=!*LVw4pKRvjyBRppf&" + this.site;
        new JSONTask().execute();
        //Toast.makeText(AnswerActivity.this,requestUrl+"",Toast.LENGTH_SHORT).show();
        //Log.e("URL",requestUrl);

    }

    public JSONObject makeRequest(String url) throws IOException, JSONException {

        JSONObject response;
        String jsonString;

        HttpClient httpclient = new DefaultHttpClient();

        // create the request
        HttpUriRequest request = new HttpGet(url);
        request.addHeader("Accept-Encoding", "gzip");

        // execute the request
        HttpResponse resp = httpclient.execute(request);
        StatusLine statusLine = resp.getStatusLine();

        // check the request response status. Should be 200 OK
        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
            Header contentEncoding = resp.getFirstHeader("Content-Encoding");
            InputStream instream = resp.getEntity().getContent();
            // was the returned response gzip'ed?
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
            pDialog = new ProgressDialog(AnswerActivity.this);
            pDialog.setMessage("Getting Answers ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                answersJson = makeRequest(requestUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return answersJson;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                mJSONArr = jsonObject.getJSONArray("items");
                for(int i=0;i<20;i++)
                {
                    ob2 = mJSONArr.getJSONObject(i);
                    ob3 = ob2.getJSONObject("owner");
                    answers[i] = new Answer(ob2.getString("body"),ob3.getString("display_name"),ob2.getString("score"));
                }
                adapter = new AnswersAdapter(AnswerActivity.this,
                        R.layout.answer_list_item, answers);
                answerList.setAdapter(adapter);
                pDialog.dismiss();
                //url = "https://api.stackexchange.com/2.2/search?order=desc&sort=activity&";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answer, menu);
        return true;
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
}
