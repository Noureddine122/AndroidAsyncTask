package com.example.rssapple;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ShowResults extends AppCompatActivity {
    ArrayList <String> links;
    ArrayList <String> titles;
    ListView mListView;
    Exception ee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_results);
        titles = new ArrayList<>();
        links = new ArrayList<>();
        String url = getIntent().getStringExtra("url");
        new ProcessInBackground().execute(url);
        mListView = (ListView) findViewById(R.id.listv);
        Log.d("yes",links.toString());
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            Uri uri = Uri.parse(links.get(position));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }
    @SuppressLint("StaticFieldLeak")
    public class ProcessInBackground extends AsyncTask<String,Void,Exception> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected  Exception doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                Log.d("url","Hey the url :"+url);
                try {

                    xpp.setInput(url.openConnection().getInputStream(),"utf-8");
                }catch(Exception e){
                    Log.d("url","Err" + e.getMessage());
                }

                Log.d("url","I'm here");

                boolean insideEntry = false;
                xpp.next();
                int eventType =xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT){

                    if(eventType == XmlPullParser.START_TAG){
                        Log.d("url","first start tag");

                        switch (xpp.getName()) {
                            case "entry":
                                insideEntry = true;

                                break;
                            case "title":
                                if (insideEntry) {
                                    titles.add(xpp.nextText());
                                    Log.d("url","Add titles");

                                }
                                break;
                            case "id":
                                if (insideEntry) {
                                    links.add(xpp.nextText());
                                    Log.d("url","Links add");

                                }
                                break;
                        }
                    }else if(eventType == XmlPullParser.END_TAG && xpp.getName().equals("entry")){
                        insideEntry = false;
                    }
                    eventType= xpp.next();
                }
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
                ee=e;
            }

            return ee;
        }
        @Override
        protected void onPostExecute(Exception result) {
            super.onPostExecute(result);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(ShowResults.this, android.R.layout.simple_list_item_1, titles);
            mListView.setAdapter(adapter);
        }
    }

}