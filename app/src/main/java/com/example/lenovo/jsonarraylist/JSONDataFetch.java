package com.example.lenovo.jsonarraylist;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Lenovo on 06-Jul-16.
 */
public class JSONDataFetch extends AsyncTask<String,Void,ArrayList<JSONData>> {
    private PostExecuteListener pExL;
    private ProgressDialog pd;

    public JSONDataFetch(PostExecuteListener pExL) {
        this.pExL = pExL;
        Log.d(MainActivity.TAG,"JSONDataFetch constructor invoked");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(pExL.getContext());
        pd.setTitle("Downloading data");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please wait...");
        pd.setIndeterminate(false);
        pd.show();
    }

    @Override
    protected ArrayList<JSONData> doInBackground(String... strings) {
        ArrayList<JSONData> jsonDataList=new ArrayList<>();
        try {
            URL url=new URL(strings[0]);
            HttpsURLConnection connectURL= (HttpsURLConnection) url.openConnection();
            connectURL.setReadTimeout(15000);
            connectURL.setConnectTimeout(10000);
            connectURL.connect();
            Log.d(MainActivity.TAG,"Response code :"+connectURL.getResponseCode());
            if (connectURL.getResponseCode()==200) {
                InputStream is=connectURL.getInputStream();
                String data=convertToString(is);
                jsonDataList = retrieveFromJson(data);
            }
        } catch (IOException|JSONException e) {
            e.printStackTrace();
        }
        return jsonDataList;
    }

    @Override
    protected void onPostExecute(ArrayList<JSONData> jsonData) {
        pd.dismiss();
        super.onPostExecute(jsonData);
        Log.d(MainActivity.TAG,"onPostExecute called");
        pExL.dataFetcher(jsonData);
    }

    public ArrayList<JSONData> retrieveFromJson(String data) throws JSONException {
        JSONData.JSONSubData jsonSubData;
        ArrayList<JSONData> dataList=new ArrayList<>();
        JSONArray jArr=new JSONArray(data);
        JSONObject jObj;
        Log.d(MainActivity.TAG,"Getting arrayList from the String having json "+jArr.length());
        for(int i=0;i<jArr.length();i++) {
            jObj=jArr.getJSONObject(i);
                    jsonSubData=new JSONData.JSONSubData(
                    jObj.getString("start_time"),
                    jObj.getString("end_time"),
                    jObj.getString("state"));
            dataList.add(new JSONData(jObj.getString("title"),jsonSubData));
            Log.d(MainActivity.TAG,"dataList size"+dataList.size());
        }
        Log.d(MainActivity.TAG,"Data List");
        return dataList;
    }



    public String convertToString(InputStream is) throws IOException {
        StringBuilder sb=new StringBuilder();
        String temp;
        Log.d(MainActivity.TAG,"Reading API into String");
        BufferedReader br=new BufferedReader(new InputStreamReader(is,"utf-8"));
        temp=br.readLine();
        while(temp!=null && !temp.isEmpty()) {
            sb.append(temp);
            temp=br.readLine();
        }
        return sb.toString();
    }


    interface PostExecuteListener {
        void dataFetcher(ArrayList<JSONData> jData);
        Context getContext();
    }
}
