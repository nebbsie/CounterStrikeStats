package com.nebbs.counterstrikestats.handlers;

import android.os.AsyncTask;

import com.nebbs.counterstrikestats.API_AUTH;
import com.nebbs.counterstrikestats.objects.SteamUserAccount;

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


public class GetSteamAccount extends AsyncTask {

    private boolean vanity;
    private String input;

    public GetSteamAccount(String input){
        this.input = input;
        if(input.matches("\\d+")) {
            vanity = false;
        }else{
            vanity = true;
        }
    }

    // Returns the SteamUserAccount.
    @Override
    protected SteamUserAccount doInBackground(Object[] objects) {

        if(vanity){
            try {
                JSONObject object = callAPI(new URL("http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key="+API_AUTH.API_KEY+"&vanityurl="+ input));
                return resolveSteamID(parseVanity(object));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else {
            return resolveSteamID(input);
        }
        return null;
    }

    // Retrieves information about the steam account using the steamid.
    private SteamUserAccount resolveSteamID(String id){
        try {
            JSONObject JSONResponse = callAPI(new URL("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key="+ API_AUTH.API_KEY+"&steamids="+ id));
            return parseData(JSONResponse);
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

    // Returns the steam ID from the JSON object when searching with vanity id.
    private String parseVanity(JSONObject object){
        try {
            JSONObject res = object.getJSONObject("response");
            return res.getString("steamid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Returns the SteamUserAccount after parsing information into object.
    private SteamUserAccount parseData(JSONObject object){
        SteamUserAccount account = new SteamUserAccount();
        try {
            JSONObject res = object.getJSONObject("response");
            JSONArray play = res.getJSONArray("players");
            JSONObject user = play.getJSONObject(0);
            account.setID(user.getString("steamid"));
            account.setVanityID(user.getString("personaname"));
            account.setAvatar(user.getString("avatarfull"));
            account.setCurrentState(user.getString("personastate"));
            account.setComVisibility(user.getString("communityvisibilitystate"));
            account.setProfileState(user.getString("profilestate"));
            account.setLastLogOff(user.getString("lastlogoff"));
            return account;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
