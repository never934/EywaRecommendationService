package com.eywa_kitchen.eywarecommendationservice.RecommendationsGetTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetRecommendationsList extends AsyncTask<Void, Void, Void> {


    private final RecommendationsCallback callback;
    private String TAG = "GetRecResources";
    private String[] VideoURL = new String[255];
    private int VideosCount;
    private List<RecommendationDetail> Recommendations;


    public GetRecommendationsList(RecommendationsCallback callback) {

        this.callback = callback;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Recommendations = new ArrayList<RecommendationDetail>();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            String Data = GetIds();
            ParseVideos(Data);
            getVideoRes();
        }catch (Exception e){
            callback.Error();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Collections.shuffle(Recommendations);
        callback.Received(Recommendations);
    }


    private String GetIds(){
        String path = "http://birja-kuhon.ru/eywa_videos/recommendations.txt";
        URL url = null;
        String stream = "";
        try {
            url = new URL(path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                stream += line;

            }
            br.close();
            Log.e(TAG, stream);
            return stream;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (ProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void ParseVideos(String Data) {
        String[] words = Data.split(" ");
        int CountOfVideos = 0;
        for (String word : words) {
            word = word.replace("}", "");
            word = word.replace("{", "");
            word = word.replace(",", "");
            word = word.replace("Video", "");
            word = word.replace("=", "");
            int number = 0;
            char firstChar = word.charAt(0);
            char secondChar = word.charAt(1);
            String FirstNumberString = Character.toString(firstChar);
            String SecNumberString = Character.toString(secondChar);
            if (tryParseInt(SecNumberString)) {
                word = word.substring(2);
                String AllNumber = FirstNumberString + SecNumberString;
                number = Integer.parseInt(AllNumber);
            } else {
                word = word.substring(1);
                number = Integer.parseInt(FirstNumberString);
            }
            VideoURL[number] = word;
            Log.e(TAG, word);
            CountOfVideos++;
            VideosCount = CountOfVideos;
        }
        for (int Count = 1; Count <= CountOfVideos; Count++) {
            Log.e(TAG, VideoURL[Count]);
        }
    }

    private void getVideoRes() {
        for (int VideoNumber = 1; VideoNumber <= VideosCount; VideoNumber++) {
            GetVideoResJSON("https://www.youtube.com/oembed?url=http://www.youtube.com/watch?v=" + VideoURL[VideoNumber] + "&format=json", VideoURL[VideoNumber]);
        }
    }

    private void GetVideoResJSON(final String URL, final String id){
        URL url = null;
        String stream = "";
        try {
            url = new URL(URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = br.readLine()) != null) {
                stream += line;
            }

            br.close();
            String JsonInput = stream;

            try{
                RecommendationDetail recom = new RecommendationDetail();
                JSONObject jsonObject = new JSONObject(JsonInput);
                recom.title = jsonObject.getString("title");
                recom.author = jsonObject.getString("author_name");
                recom.preview = getBitmapFromURL("https://img.youtube.com/vi/"+ id +"/maxresdefault.jpg");
                recom.VideoId = id;
                Recommendations.add(recom);
                Log.e(TAG, "Тайтл " + recom.title);
            }catch (JSONException e){
                Log.e(TAG, "JSON error");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            connection.disconnect();
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }

}
