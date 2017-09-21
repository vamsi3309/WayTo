package com.personal.vamsi.wayto;

import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;


public class JsonExtracter extends AsyncTask<AssetManager,Integer,JsonExtracter.JsonResult> {
    public class JsonResult {
        Schedule schedule;
        Buildings buildings;
    }
    public class Schedule{
        public String[] builNames,classNames,roomNos,startDay,startTime,endTime,endDay;
        double[] lat,lon;
        JSONArray scharray,bularray;
        LatLng[] schLocations;
    }
    public class Buildings{
        int[] id;
        LatLng[] locations;
        String[] name,code,searchText;
        JSONArray jsonArray;
    }
    private LatLng[] locations;
    private String[] builNames;
    private JsonResult getExtractedData(AssetManager ass){
        JsonResult res = new JsonResult();
        try {
            AssetManager assetManager = ass;
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

            double[] latCord = new double[sch_data.length()];
            double[] longCord = new double[sch_data.length()];
            builNames = new String[sch_data.length()];
            int[] BulNos = new int[sch_data.length()];
            for(int i=0;i<sch_data.length();i++) {
                BulNos[i]=sch_data.getJSONObject(i).getInt("buildingID");
                //Log.v("Building NO",""+BulNos[i]);
            }

            locations = new LatLng[sch_data.length()];

            for (int i=0;i<bul_data.length();i++)
            {
                for (int j=0;j<BulNos.length;j++)
                {
                    if(bul_data.getJSONObject(i).getInt("id")==BulNos[j])
                    {
                        builNames[j]=bul_data.getJSONObject(i).getString("name");
                        //Log.v("Building name",builNames[j]);
                        latCord[j]=bul_data.getJSONObject(i).getJSONObject("location").getDouble("lat");
                        //Log.v("Lat Cord","["+j+"]"+" = "+latCord[j]);
                        longCord[j]=bul_data.getJSONObject(i).getJSONObject("location").getDouble("lng");
                        //Log.v("Long Cord","["+j+"]"+" = "+longCord[j]);
                        locations[j]= new LatLng(latCord[j],longCord[j]);

                    }
                }
            }
            String[] temp1 = new String[sch_data.length()];
            String[] temp2 = new String[sch_data.length()];
            String[] temp3 = new String[sch_data.length()];
            String[] temp4 = new String[sch_data.length()];
            String[] temp5 = new String[sch_data.length()];
            String[] temp6 = new String[sch_data.length()];
            for(int i=0;i<sch_data.length();i++)
            {
                temp1[i]=sch_data.getJSONObject(i).getString("Name");
                temp2[i]=sch_data.getJSONObject(i).getString("Room");
                temp3[i]=sch_data.getJSONObject(i).getJSONObject("start").getString("time");
                temp4[i]=sch_data.getJSONObject(i).getJSONObject("start").getString("day");
                temp5[i]=sch_data.getJSONObject(i).getJSONObject("end").getString("time");
                temp6[i]=sch_data.getJSONObject(i).getJSONObject("end").getString("day");
            }


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

            Buildings buildings = new Buildings();
            buildings.id=tempid;
            buildings.name=tempname;
            buildings.code=tempcode;
            buildings.locations=templocations;
            buildings.searchText=tempsearch;
            buildings.jsonArray=bul_data;

            Schedule sch = new Schedule();
            sch.schLocations=locations;
            sch.builNames=builNames;
            sch.scharray=sch_data;
            sch.bularray=bul_data;
            sch.lat=latCord;
            sch.lon=longCord;
            sch.classNames=temp1;
            sch.roomNos=temp2;
            sch.startTime=temp3;
            sch.startDay=temp4;
            sch.endTime=temp5;
            sch.endDay=temp6;

            res.buildings=buildings;
            res.schedule=sch;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public JsonResult doInBackground(AssetManager... ass) {
        return getExtractedData(ass[0]);
    }
}
