package com.example.shivam.stackoverflow;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.zip.GZIPInputStream;


public class MainActivity extends ActionBarActivity implements OnQueryTextListener {

    SearchView mSearchView;
    StringBuilder builder = new StringBuilder();
    JSONArray mJSONArr;
    JSONObject ob1,ob2,questionsJson;
    String holder=null;
    TextView tv;
    String url = "https://api.stackexchange.com/2.2/search?order=desc&sort=activity&intitle=android&site=stackoverflow";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView)findViewById(R.id.tv);
        new JSONTask().execute();
    }

//    public String executeHttpGet(String URL) throws Exception
//    {
//        BufferedReader in = null;
//            HttpClient client = new DefaultHttpClient();
//            client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "android");
//            HttpGet request = new HttpGet();
//            request.setHeader("Content-Type", "text/plain; charset=utf-8");
//            request.setURI(new URI(URL));
//            HttpResponse response = client.execute(request);
//            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//
//            StringBuffer sb = new StringBuffer("");
//            String line = "";
//
//            String NL = System.getProperty("line.separator");
//            while ((line = in.readLine()) != null)
//            {
//                sb.append(line + NL);
//            }
//            in.close();
//            String page = sb.toString();
//            //System.out.println(page);
//            return page;
//
//    }

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
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                //String s = readJSON();
                //ob1 = new JSONObject(s);
                questionsJson = makeRequest(url);
//                Toast.makeText(MainActivity.this,String.valueOf(ob1),Toast.LENGTH_SHORT).show();
                //Log.i("myJson", String.valueOf(ob1));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return questionsJson;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                //if(ob1!=null&&!ob1.isNull("items")){
                mJSONArr = jsonObject.getJSONArray("items");
                for(int i=0;i<20;i++)
                {
                    ob2 = mJSONArr.getJSONObject(i);
                    holder+=ob2.getString("title")+"\n";
                }
                tv.setText(holder);
                pDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search for questions");
        mSearchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    public String readJSON() {
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        HttpResponse response = null;
        try {
            response = client.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if (statusCode == 200) {
            HttpEntity entity = response.getEntity();
            InputStream content = null;
            try {
                content = entity.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //Log.e(re.class.toString(), "Failed to download file");
        }
        return builder.toString();
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
        else if(id == R.id.action_settings)
        {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        Toast.makeText(MainActivity.this,"You searched for "+s,Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}
