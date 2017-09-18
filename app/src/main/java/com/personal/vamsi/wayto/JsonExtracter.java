package com.personal.vamsi.wayto;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;


/**
 * Created by navat on 9/17/2017.
 */

public class JsonExtracter extends AsyncTask<AssetManager,Integer,JsonExtracter.Result> {
    public class Result{
        public LatLng[] locations;
        public String[] builNames;
    }
    private LatLng[] locations;
    private String[] builNames;
    private Result getExtractedData(AssetManager ass){
        Result res = new Result();
        try {
            AssetManager assetManager= ass;
            InputStream sc_read = assetManager.open("schedule.json");
            int sc_size = sc_read.available();
            byte[] buf_sc = new byte[sc_size];
            sc_read.read(buf_sc);
            sc_read.close();
            String sch_read = new String(buf_sc, "UTF-8");
            JSONArray sch_data = new JSONArray(sch_read);

            InputStream bl_read = assetManager.open("buildings.json");
            int bl_size = bl_read.available();
            byte[] buf_bl = new byte[bl_size];
            bl_read.read(buf_bl);
            bl_read.close();
            String bul_read = new String(buf_bl, "UTF-8");
            JSONArray bul_data = new JSONArray(bul_read);


            Double[] latCord = new Double[sch_data.length()];
            Double[] longCord = new Double[sch_data.length()];
            builNames = new String[sch_data.length()];
            int[] BulNos = new int[sch_data.length()];
            for(int i=0;i<sch_data.length();i++) {
                BulNos[i]=sch_data.getJSONObject(i).getInt("buildingID");
                //Log.v("Building NO",""+BulNos[i]);
            }

            locations = new LatLng[sch_data.length()];
            for (int j=0;j<BulNos.length;j++)
            {
                for (int i=0;i<bul_data.length();i++)
                {
                    if(bul_data.getJSONObject(i).getInt("id")==BulNos[j])
                    {
                        builNames[j]=bul_data.getJSONObject(i).getString("name");
                        Log.v("Building name",builNames[j]);
                        latCord[j]=bul_data.getJSONObject(i).getJSONObject("location").getDouble("lat");
                        Log.v("Lat Cord","["+j+"]"+" = "+latCord[j]);
                        longCord[j]=bul_data.getJSONObject(i).getJSONObject("location").getDouble("lng");
                        Log.v("Long Cord","["+j+"]"+" = "+longCord[j]);
                        locations[j]= new LatLng(latCord[j],longCord[j]);

                    }
                }
            }

            res.locations= locations;
            res.builNames= builNames;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public JsonExtracter.Result doInBackground(AssetManager... ass) {
        return getExtractedData(ass[0]);
    }
}
