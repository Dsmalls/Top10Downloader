package com.example.bsmal.top10downloader;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listApps;
    // %d is is specifying an integer value by an actual value using the String.format method
    private String feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit = 10;
    // defining my 2 constants
    private String feedCachedURL = "INVALIDATED";
    public static final String STATE_URL = "feedURL";
    public static final String STATE_LIMIT = "feedLimit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps = (ListView) findViewById(R.id.xmlListView);

        if(savedInstanceState != null) {
            feedURL = savedInstanceState.getString(STATE_URL);
            feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        }

//        Log.d(TAG, "onCreate: starting AsyncTask ");
//        DownLoadData downLoadData = new DownLoadData();
        downloadURL(String.format(feedURL, feedLimit)); // passing the String on line 24
//        downLoadData.execute();
//        Log.d(TAG, "onCreate: completed");
    }

    // Inner Class...you the extends method
    // AsyncTask is an abstract Android class which helps the Android applications to handle the Main UI thread in efficient way.
    // AsyncTask class allows us to perform long lasting tasks/background operations and show the result on
    // the UI thread without affecting the main thread.
    // will set up 3 parameters for the AsyncClass
    // Information that I provide to the task is type String, where I pass URL to the ISS feed
    // Void parameter 2 is being used since I am not using a progress bar if my data was larger
    // Parameter 3 type String is where the type of the result I will be getting back


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        if(feedLimit == 10) {
            menu.findItem(R.id.mnu10).setChecked(true);
        } else {
            menu.findItem(R.id.mnu25).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        String feedURL;

        switch(id) {
            case R.id.mnuFree:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnu10:
            case R.id.mnu25:
                if(!item.isChecked()){
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit; // calculating the sum of the tops break down
                    Log.d(TAG, "onOptionsItemSelectedA: " + item.getTitle() + " setting feedLimit to " + feedLimit);
                    
                } else {
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " feedLimit unchanged");
                }
                break;
            case R.id.mnuRefresh:
                feedCachedURL = "INVALIDATED";
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        downloadURL(String.format(feedURL, feedLimit));
        return true;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL, feedURL);
        outState.putInt(STATE_LIMIT, feedLimit);
        super.onSaveInstanceState(outState);
    }

    private void downloadURL(String feedURL) {
        if(!feedURL.equalsIgnoreCase(feedCachedURL)) {
            Log.d(TAG, "downloadURL: starting AsyncTask ");
            DownLoadData downLoadData = new DownLoadData();
            downLoadData.execute(feedURL);
            feedCachedURL = feedURL;
            Log.d(TAG, "downloadURL: completed");
        } else {
            Log.d(TAG, "downloadURL: URL not changed");
        }
    }

    private class DownLoadData extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownLoadData";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.d(TAG, "onPostExecute: parameter is " + s);
            ParseApplications parseApplications = new ParseApplications();
            parseApplications.parse(s);

//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<FeedEntry>(
//                    MainActivity.this, R.layout.list_item, parseApplications.getApplications());
//            listApps.setAdapter(arrayAdapter);
            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.list_record,
                    parseApplications.getApplications());
            listApps.setAdapter(feedAdapter);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with " + strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if(rssFeed == null){
                Log.e(TAG, "doInBackground: Error downloading");
            }
            return rssFeed;
        }


        private String downloadXML(String urlPath){
            StringBuilder xmlResult = new StringBuilder();

            try{
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: The response code was" + response);
//                InputStream inputStream = connection.getInputStream();
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader reader = new BufferedReader(inputStreamReader);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                int charsRead;
                char[] inputBuffer =  new char[500];
                while(true){
                    charsRead = reader.read(inputBuffer);
                    if(charsRead < 0) {
                        break;
                    }
                    if(charsRead > 0){
                        xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                    }
                }
                reader.close();

                return  xmlResult.toString();
            } catch (MalformedURLException e){
                Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "downloadXML: IO Exception reading data: " + e.getMessage());
            } catch(SecurityException e){
                Log.e(TAG, "downloadXML: Security Exception. Needs permission? " + e.getMessage());
//                e.printStackTrace(); good line for debugging
            }

            return null;
        }
    }
}
