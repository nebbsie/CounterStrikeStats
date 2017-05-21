package com.nebbs.counterstrikestats.handlers;

import android.os.AsyncTask;
import com.nebbs.counterstrikestats.API_AUTH;
import com.nebbs.counterstrikestats.objects.CSGOStats;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class getCSGOStats extends AsyncTask<String, Void, CSGOStats> {

    private URL url;

    @Override
    protected CSGOStats doInBackground(String... urls) {
        String userSteamID = urls[0];
        try {
            url = new URL("http://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v0002/?appid=730&key=" + API_AUTH.API_KEY + userSteamID);
            JSONObject a = callAPI(url);
            System.out.println(a);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Returns a JSONObject from the API that is sent into the function.
    private JSONObject callAPI(URL url){
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return (JSONObject) new JSONTokener(stringBuilder.toString()).nextValue();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                urlConnection.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Returns the SteamUserAccount after parsing information into object.
    private CSGOStats parseData(JSONObject object){
        CSGOStats account = new CSGOStats();
        try {
            JSONObject play = object.getJSONObject("playerstats");
            JSONArray stats = play.getJSONArray("stats");
            JSONObject user = stats.getJSONObject(0);
            account.setTotalKills(user.getInt("value"));
            return account;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
