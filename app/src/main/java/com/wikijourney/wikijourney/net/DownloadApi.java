package com.wikijourney.wikijourney.net;

import android.os.AsyncTask;

import com.wikijourney.wikijourney.functions.Map;
import com.wikijourney.wikijourney.views.MapFragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * Created by Thomas on 29/07/2015.
 */

// Uses AsyncTask to create a task away from the main UI thread. This task takes a
// URL string and uses it to create an HttpUrlConnection. Once the connection
// has been established, the AsyncTask downloads the contents of the webpage as
// an InputStream. Finally, the InputStream is converted into a string, which is
// displayed in the UI by the AsyncTask's onPostExecute method.
public class DownloadApi extends AsyncTask<String, Void, String> {

    private MapFragment mapFragment;

    public DownloadApi(MapFragment pMapFragment) {
        this.mapFragment = pMapFragment;
    }

    @Override
    protected String doInBackground(String... urls) {
        // urls comes from the execute() call: urls[0] is the url.
        try {
            return downloadUrl(urls[0]);
        } catch (IOException e) {
            return "Unable to contact API. Something must have went wrong";
        }
    }
    //onPostExecute displays the results of the AsyncTask

    @Override
    protected void onPostExecute(String result) {
        Map.drawPOI(mapFragment, result);
        super.onPostExecute(result);
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl(String myUrl) throws IOException {

        InputStream stream = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 50000; // TODO This is an arbitrary big number, we should find a way to read and parse the whole response

        try {
            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1200000 /* milliseconds */); // TODO The timeouts are huge, since the WikiJourney API is really slow
            conn.setConnectTimeout(1200000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int responseCode = conn.getResponseCode();
            stream = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(stream, len);
            return contentAsString;

        // Makes sure that the InputStream is closed after the app is
        // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}



