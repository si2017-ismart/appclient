package com.CieParabole.CieParaboleSNotificatio1Uv;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jadhaddad on 1/11/17.
 *
 * Classe qui sert a fournir toutes les communications avec les web services
 *
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


    /**
     *
     * Mise à jour du beacon de la session
     *
     * @param token le token de la session
     * @param beaconID l'identifiant du beacon
     * @return un booleen true en cas de succès
     *
     * */
    public boolean newPosition(final String token, final String beaconID){
        boolean newPosition = false;
        urlStr = ip+":3000/api/etablissements/sessions/setBeacon";
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject parameters = new JSONObject();
                try {
                    parameters.put("id_beacon", beaconID);
                    parameters.put("token", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    /**
     * Vérifie l'existence d'une session
     *
     * @param token le token de la session
     * @return un booleen true si le token est toujours valide
     * */
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


    /**
     *
     * Ajout de la satisfaction du user au log de sa session
     *
     * @param rating un entier de 1 à 5 representant la satisfaction
     * @param token le token de la session
     *
     * */
    public void sendRating(final String token, final int rating){
        urlStr = ip+":3000/api/interventions/satisfaction";

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject parameters   = new JSONObject();
                try {
                    parameters.put("token", token);
                    parameters.put("satisfaction", rating);
                    Log.d("RATINGGGG","token: "+token+"\nrating: "+rating);
                    sendPost(parameters);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("RATINGGGG", postResult);
    }

    /**
     *
     * Vérifie l'existance d'un beacon et renvoie l'établissement qui le possède
     *
     * @param beaconID l'identifiant du beacon
     * @return un Arraylist de String contenant: si le beacon existe, ID du beacon, nom de l'etablissement, ID de l'etablissement, mail de l'etablissement
     *
     * */
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
            beaconExistsString  = jsonObject.getString("success");
            beaconId = jsonObject.getJSONObject("data").getString("id_beacon");
            nomEtablissement    = jsonObject.getJSONObject("data").getJSONObject("etablissement").getString("nom");
            idEtablissement     = jsonObject.getJSONObject("data").getJSONObject("etablissement").getString("id");
            mailEtablissement   = jsonObject.getJSONObject("data").getJSONObject("etablissement").getString("mail");

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

        /**
         *
         * Liste de tous les beacons d'un etablissement
         *
         *@param IDEtablissement l'identifiant de l'etablissement
         *@return un Arraylist qui contient les informations des beacons representés par des Arraylist contenant : l'identifiant du beacon,
         * nom du beacon, la portee du beacon, l'identifiant de l'etablissement, le mail de l'etablissement, le nom de l'etablissement, la position X, la position Y
         *
         *
         * */
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



    /**
     * Demande un jeton d'aide pour le client
     *
     * @param context le contexte de l'activité
     * @param beaconID l'identifiant du beacon
     * @return le token de la session
     *
     * */
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

    /**
     *
     * Liste de tous les beacons
     * @return un Arraylist contenant les identifiants des beacons
     *
     * */
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


    /**
     *
     * Executer la requête GET et recevoir le resultat de la requête
     * @param arraylist un Arraylist qui sert à recuperer le resultat de la requête GET
     *
     * */
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

    /**
     *
     * Envoie la requete POST et stock l'information dans l'attribut postResult
     *
     * @param parameters un Object JSON contenant les parametres de la requete POST
     *
     * */
    private void sendPost(JSONObject parameters){

        URL url;
        try {
            url = new URL(urlStr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            postResult="";
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            Log.d("json",parameters.toString());
            writer.write(parameters.toString());

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    postResult+=line;
                    Log.d("testResult",postResult);
                }
            }
            else {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                while ((line=br.readLine()) != null) {
                    postResult+=line;
                    Log.d("resuslt",postResult);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}