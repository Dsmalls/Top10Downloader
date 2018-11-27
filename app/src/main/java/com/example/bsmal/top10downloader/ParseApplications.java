package com.example.bsmal.top10downloader;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseApplications {
    private static final String TAG = "ParseApplications";
    private ArrayList<FeedEntry> applications;

    public ParseApplications() {
        this.applications = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    public boolean parse(String xmlData) {
        boolean status = true;
        FeedEntry currentRecord = null; // store data
        boolean inEntry = false; // processing an entry or not
        String textValue = "";

        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); // the first 3 lines responsible for the Java xml parse
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser(); // apart of the parsing class factory, use to parse XML for Java
            xpp.setInput(new StringReader(xmlData)); // string reader treats a class like a stream
            int eventType = xpp.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){ // while loop, inside the loop will be getting the values what I am interested in
                String tagName = xpp.getName(); // getting the name of the tag from the xml, trying the get the entry tag
                switch (eventType) {
                    case XmlPullParser.START_TAG:
//                        Log.d(TAG, "parse: Starting tag for " + tagName);
                        if ("entry".equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            currentRecord = new FeedEntry();
                        }
                        break;

                        case XmlPullParser.TEXT: // stores the text
                            textValue = xpp.getText();
                            break;

                        case XmlPullParser.END_TAG:
//                            Log.d(TAG, "parse: Ending tag for " + tagName);
                            if (inEntry) { //avoid null tag name
                                if ("entry".equalsIgnoreCase(tagName)) {
                                    applications.add(currentRecord);
                                    inEntry = false;
                                } else if ("name".equalsIgnoreCase(tagName)) { // setting the value for each tag name,
                                    currentRecord.setName(textValue);
                                } else if ("artist".equalsIgnoreCase(tagName)){
                                    currentRecord.setArtist(textValue);
                                } else if ("releaseDate".equalsIgnoreCase(tagName)) {
                                    currentRecord.setReleaseDate(textValue);
                                } else if ("summary".equalsIgnoreCase(tagName)) {
                                    currentRecord.setSummary(textValue);
                                } else if ("image".equalsIgnoreCase(tagName)) {
                                    currentRecord.setImageURL(textValue);
                                }
                            }

                            break;

                        default:
                            // at the end, nothing else to do
                }
                eventType = xpp.next(); // this tells the parse to continue to the xml which will continue to go around the loop, unless its at the end of the document
            }
//            for (FeedEntry app: applications){
//                Log.d(TAG, "****************************");
//                Log.d(TAG, app.toString());
//            }

        } catch (Exception e) { // all exception are caught in this try block
            status = false;
            e.printStackTrace();
        }

        return status;
    }
}
