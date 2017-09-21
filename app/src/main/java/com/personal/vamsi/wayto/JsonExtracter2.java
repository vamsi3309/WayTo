package com.personal.vamsi.wayto;

import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

public class JsonExtracter2 extends AsyncTask<AssetManager,Integer,JsonExtracter2.Buildings> {
    public class Buildings{
        int[] id;
        LatLng[] locations;
        String[] name,code,searchText;
        JSONArray jsonArray;
    }
    @Override
    public JsonExtracter2.Buildings doInBackground(AssetManager... ass){
        AssetManager assetManager=ass[0];
        Buildings buildings = new Buildings();
        try {
            InputStream bl_read = assetManager.open("buildings.json");
            int bl_size = bl_read.available();
            byte[] buf_bl = new byte[bl_size];
            bl_read.read(buf_bl);
            bl_read.close();
            String bul_read = new String(buf_bl, "UTF-8");
            JSONArray bul_data = new JSONArray(bul_read);

            int[] tempid=new int[bul_data.length()];
            String[] tempname= new String[bul_data.length()];
            String[] tempcode= new String[bul_data.length()];
            String[] tempsearch= new String[bul_data.length()];
            LatLng[] templocations = new LatLng[bul_data.length()];

            for(int i=0;i<bul_data.length();i++)
            {
                tempname[i]=bul_data.getJSONObject(i).getString("name");
                tempid[i]=bul_data.getJSONObject(i).getInt("id");
                templocations[i]=new LatLng(bul_data.getJSONObject(i).getJSONObject("location").getDouble("lat"),
                        bul_data.getJSONObject(i).getJSONObject("location").getDouble("lng"));
                tempcode[i]=bul_data.getJSONObject(i).getString("code");
                tempsearch[i]=bul_data.getJSONObject(i).getString("searchText");
            }


            buildings.id=tempid;
            buildings.name=tempname;
            buildings.code=tempcode;
            buildings.locations=templocations;
            buildings.searchText=tempsearch;
            buildings.jsonArray=bul_data;


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return buildings;
    }
}
