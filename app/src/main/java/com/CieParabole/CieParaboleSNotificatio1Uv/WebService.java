package com.CieParabole.CieParaboleSNotificatio1Uv;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by jadhaddad on 1/11/17.
 */

public class WebService {
    private static String urlStr = "";
    private static final char PARAMETER_DELIMITER = '&';
    private static final char PARAMETER_EQUALS_CHAR = '=';
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String keyFirstName = "firstNameKey";
    public static final String keyGender = "genderKey";
    //fix this
    private String profil = "moteur";




    public WebService(){

    }

    public void addBeacon(final String idEtablissement, final String id, final String nom){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("id_etablissement", idEtablissement);
                parameters.put("id", id);
                parameters.put("nom", nom);
                urlStr = "http://10.0.2.2:3000/api/beacons/add";
                sendPost(parameters);
            }
        }).start();
    }

    public String requestHelp(Context context, String beaconID){
        final ArrayList<String> token = new ArrayList<String>();
        String tokenString = null;
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String name = sharedPreferences.getString(keyFirstName,null);
        String gender = sharedPreferences.getString(keyGender,null);
        urlStr = "http://10.0.2.2:3000/api/beacons/needHelp/"+profil+"/"+name+"/"+gender+"/"+beaconID;
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Get(token);
            }
        });
        t1.start();
        try{
            t1.join();
            tokenString = token.get(0);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return tokenString;
    }

    public ArrayList<String> getAllBeacons(){
        ArrayList<String> beacons = new ArrayList<String>();
        urlStr = "http://10.0.2.2:3000/api/beacons/";
        final ArrayList<String> beaconIds = new ArrayList<String>();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Get(beaconIds);
            }
        });
        t1.start();
        try {
            t1.join();
            String jsonStr = beaconIds.get(0);
            JSONArray jsonArray = new JSONArray(jsonStr);

            for (int i = 0; i < jsonArray.length(); i++) {
                Log.d("BEACON", jsonArray.getJSONObject(i).getString("id_beacon"));
                beacons.add(jsonArray.getJSONObject(i).getString("id_beacon"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return beacons;
    }

    public boolean beaconExist(String BeaconId){
        boolean exists = false;
        urlStr = "http://10.0.2.2:3000/api/beacons/existId/"+BeaconId;
        final ArrayList<String> existList = new ArrayList<String>();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Get(existList);
            }
        });
        t1.start();
        try{
            t1.join();
            String result = existList.get(0);
            if(result.equals("true")){
                exists = true;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return exists;
    }

    private void Get(ArrayList<String> arraylist){
        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urlStr);

            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = urlConnection.getInputStream();

            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            result = "";
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                result+= current;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        arraylist.add(result);
    }

    private void sendPost(Map<String, String> parameters){

        try {
            URL urlToRequest = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String postParameters = createQueryStringForParameters(parameters);
            urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);

            //send the POST
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(postParameters);
            out.close();

            String str = readInputStreamToString(urlConnection);
            Log.d("post response", str);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private String readInputStreamToString(HttpURLConnection connection) {
        String result = null;
        StringBuffer sb = new StringBuffer();
        InputStream is = null;

        try {
            is = new BufferedInputStream(connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
        }
        catch (Exception e) {
            Log.i("webservcie", "Error reading InputStream");
            result = null;
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    Log.i("webservice", "Error closing InputStream");
                }
            }
        }

        return result;
    }

    private static String createQueryStringForParameters(Map<String, String> parameters) {
        StringBuilder parametersAsQueryString = new StringBuilder();
        if (parameters != null) {
            boolean firstParameter = true;

            for (String parameterName : parameters.keySet()) {
                if (!firstParameter) {
                    parametersAsQueryString.append(PARAMETER_DELIMITER);
                }

                try {
                    parametersAsQueryString.append(parameterName)
                            .append(PARAMETER_EQUALS_CHAR)
                            .append(URLEncoder.encode(parameters.get(parameterName), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                firstParameter = false;
            }
        }
        return parametersAsQueryString.toString();
    }
}