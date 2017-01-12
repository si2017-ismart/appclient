package com.CieParabole.CieParaboleSNotificatio1Uv;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private static String ip = "http://192.168.12.228";
    private static String urlStr = "";
    private static final char PARAMETER_DELIMITER = '&';
    private static final char PARAMETER_EQUALS_CHAR = '=';
    private String postResult = "";
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String keyFirstName = "firstNameKey";
    public static final String keyGender = "genderKey";
    //fix this
    private String profil = "moteur";




    public WebService(){

    }

    public boolean newPosition(final String token, final String beaconID){
        boolean newPosition = false;
        urlStr = ip+":3000/api/etablissements/sessions/setBeacon";
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("id_beacon", beaconID);
                parameters.put("token", token);
                sendPost(parameters);
            }
        });
        t1.start();
        try{
            t1.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (postResult.equals("true")){
            newPosition = true;
        }

        return newPosition;
    }


    public boolean checkToken(String token){
        boolean tokenValid = false;
        Log.d("tokennnnnnnn", token);
        urlStr = ip+":3000/api/etablissements/sessions/checkToken/"+token.substring(1, token.length()-1);
        final ArrayList<String> tokenList = new ArrayList<String>();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Get(tokenList);
            }
        });

        t1.start();
        try{
            t1.join();
            String result = tokenList.get(0);
            Log.d("result", result);
            if(result.trim().equals("true")){
                tokenValid = true;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return tokenValid;
    }
    public ArrayList<String> beaconExist(String beaconID){
        boolean beaconExists = false;
        String beaconId, nomEtablissement, idEtablissement, mailEtablissement, beaconExistsString;
        ArrayList<String> resultArray = new ArrayList<String>();

        urlStr = ip+":3000/api/beacons/existId/"+beaconID;
        final ArrayList<String> beaconResult = new ArrayList<String>();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Get(beaconResult);
            }
        });

        t1.start();
        try{
            t1.join();
            String jsonStr = beaconResult.get(0);

            JSONObject jsonObject = new JSONObject(jsonStr);
            beaconExistsString = jsonObject.getString("success");
            beaconId = jsonObject.getJSONObject("data").getString("id_beacon");
            nomEtablissement = jsonObject.getJSONObject("data").getJSONObject("etablissement").getString("nom");
            idEtablissement = jsonObject.getJSONObject("data").getJSONObject("etablissement").getString("id");
            mailEtablissement = jsonObject.getJSONObject("data").getJSONObject("etablissement").getString("mail");

            resultArray.add(beaconExistsString);
            resultArray.add(beaconId);
            resultArray.add(nomEtablissement);
            resultArray.add(idEtablissement);
            resultArray.add(mailEtablissement);

            Log.d("etablissement", "exist = "+beaconExistsString);
            Log.d("etablissement", "beaconID = "+beaconId);
            Log.d("etablissement", "nom etablissement = "+nomEtablissement);
            Log.d("etablissement", "id etablissement = "+idEtablissement);
            Log.d("etablissement", "mail etablissement = "+mailEtablissement);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultArray;
    }


        public ArrayList<ArrayList<String>> getBeaconsByEtablissement(String IDEtablissement){
        boolean beaconExists = false;
        String beaconId, nomBeacon, nomEtablissement, idEtablissement, mailEtablissement, positionX, positionY, portee;
        ArrayList<ArrayList<String>> resultArray = new ArrayList<ArrayList<String>>();

        urlStr = ip+":3000/api/beacons/getByEtablissement/"+ IDEtablissement;
        final ArrayList<String> beaconResult = new ArrayList<String>();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Get(beaconResult);
            }
        });

        t1.start();
        try{
            t1.join();
            String jsonStr = beaconResult.get(0);
            
            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                beaconId = jsonObject.getString("id_beacon");
                nomBeacon = jsonObject.getString("nom");
                portee = jsonObject.getString("portee");

                idEtablissement = jsonObject.getJSONObject("etablissement").getString("id");
                mailEtablissement = jsonObject.getJSONObject("etablissement").getString("mail");
                nomEtablissement = jsonObject.getJSONObject("etablissement").getString("nom");

                positionX = jsonObject.getJSONObject("position").getString("x");
                positionY = jsonObject.getJSONObject("position").getString("y");

                ArrayList<String> beaconData = new ArrayList<String>();
                beaconData.add(beaconId);
                beaconData.add(nomBeacon);
                beaconData.add(portee);
                beaconData.add(idEtablissement);
                beaconData.add(mailEtablissement);
                beaconData.add(nomEtablissement);
                beaconData.add(positionX);
                beaconData.add(positionY);

                resultArray.add(beaconData);

                Log.d("BeaconsByEtablissement", "beaconID = "+beaconId);
                Log.d("BeaconsByEtablissement", "beaconNom = "+nomBeacon);
                Log.d("BeaconsByEtablissement", "portee = "+portee);
                Log.d("BeaconsByEtablissement", "nom etablissement = "+nomEtablissement);
                Log.d("BeaconsByEtablissement", "id etablissement = "+idEtablissement);
                Log.d("BeaconsByEtablissement", "mail etablissement = "+mailEtablissement);
                Log.d("BeaconsByEtablissement", "mail etablissement = "+mailEtablissement);
                Log.d("BeaconsByEtablissement", "positionX = " + positionX);
                Log.d("BeaconsByEtablissement", "positionY = " + positionY);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultArray;
    }


    public void addBeacon(final String idEtablissement, final String id, final String nom){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("id_etablissement", idEtablissement);
                parameters.put("id", id);
                parameters.put("nom", nom);
                urlStr = ip+":3000/api/beacons/add";
                sendPost(parameters);
            }
        }).start();
    }

    public String requestHelp(Context context, String beaconID){
        Log.d("requestHelp","begin request help");
        final ArrayList<String> token = new ArrayList<String>();
        String tokenString = null;
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String name = sharedPreferences.getString(keyFirstName,null);
        String gender = sharedPreferences.getString(keyGender,null);
        urlStr = ip+":3000/api/beacons/needHelp/"+profil+"/"+name+"/"+gender+"/"+beaconID;
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Get(token);
            }
        });
        t1.start();
        Log.d("requestHelp","begin after thread start");

        try{
            t1.join();
            tokenString = token.get(0);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("requestHelp", "before return "+tokenString);
        return tokenString;
    }

    public ArrayList<String> getAllBeacons(){
        ArrayList<String> beacons = new ArrayList<String>();
        urlStr = ip+":3000/api/beacons/";
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
            postResult = str;
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