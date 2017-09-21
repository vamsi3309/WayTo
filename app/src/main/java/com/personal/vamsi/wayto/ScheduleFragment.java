package com.personal.vamsi.wayto;


import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ScheduleFragment extends Fragment {

        public ScheduleFragment() {
            // Required empty public constructor
        }

        public class ScheduleResult{
            String[] name;
            LatLng[] locations;
            Long[] time;
        }

        JsonExtracter.JsonResult jsonResult;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            AssetManager assetManager = getResources().getAssets();
            JsonExtracter jsonExtracter = new JsonExtracter();
           jsonResult = jsonExtracter.doInBackground(assetManager);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = null;
            try {
                view = inflater.inflate(R.layout.schedule_fragment, container, false);
                final long[] epochstartTime = new long[jsonResult.schedule.startDay.length];
                final long[] epochendTime = new long[jsonResult.schedule.startDay.length];
                for(int i=0;i<jsonResult.schedule.startDay.length;i++){
                    Calendar scal =Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
                    Calendar ecal  =Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
                    String stime = jsonResult.schedule.startTime[i];
                    String etime = jsonResult.schedule.endTime[i];
                    if(stime.endsWith("am"))
                        scal.set(Calendar.AM_PM,0);
                    else
                        scal.set(Calendar.AM_PM,1);
                    scal.set(Calendar.HOUR,Integer.parseInt(stime.substring(0,stime.indexOf(":"))));
                    scal.set(Calendar.MINUTE,Integer.parseInt(stime.substring(stime.indexOf(":")+1,stime.indexOf(":")+2)));

                 if(etime.endsWith("am"))
                        ecal.set(Calendar.AM_PM,0);
                    else
                        ecal.set(Calendar.AM_PM,1);
                    ecal.set(Calendar.HOUR,Integer.parseInt(etime.substring(0,etime.indexOf(":"))));
                    ecal.set(Calendar.MINUTE,Integer.parseInt(etime.substring(etime.indexOf(":")+1,etime.indexOf(":")+2)));
                    epochstartTime[i]=scal.getTimeInMillis()/1000L;
                    epochendTime[i]=ecal.getTimeInMillis()/1000L;
                    Log.d("start time:   ",""+scal.getTimeInMillis()/1000L);
                    Log.d("end time:   ",""+ecal.getTimeInMillis()/1000L);
                }

                Button newPage = (Button)view.findViewById(R.id.mon);
                newPage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getActivity(), MapsActivity.class);
                        List<String> names = new ArrayList<String>();
                        List<Double> lat = new ArrayList<Double>();
                        List<Double> lon = new ArrayList<Double>();
                        List<Long> time = new ArrayList<Long>();
                        for(int i=0 ;i<jsonResult.schedule.startDay.length;i++){
                            if (jsonResult.schedule.startDay[i].equals("Mon")){
                                names.add(jsonResult.schedule.builNames[i]);
                                lat.add(jsonResult.schedule.lat[i]);
                                lon.add(jsonResult.schedule.lon[i]);
                                time.add(epochstartTime[i]);
                        /*intent.putExtra("names"+i,jsonResult.schedule.builNames[i]);
                        intent.putExtra("lat"+i,jsonResult.schedule.lat[i]);
                        intent.putExtra("long"+i,jsonResult.schedule.lon[i]);*/
                            }
                        }
                        String[] name = new String[names.size()];
                        double[] latit =  new double[name.length];
                        double[] longit = new double[name.length];
                        long[] stime = new long[name.length];
                        for(int i=0;i<name.length;i++){
                            name[i]=names.get(i);
                            latit[i]=lat.get(i);
                            longit[i]=lon.get(i);
                            stime[i]=time.get(i);
                        }
                        intent.putExtra("names",name);
                        intent.putExtra("lat",latit);
                        intent.putExtra("lon",longit);
                        Log.v("index",""+stime.length+"");
                        intent.putExtra("epochstime",stime);
                        intent.putExtra("epochetime",epochendTime);
                        startActivity(intent);
                    }
                });


                Button tue = (Button)view.findViewById(R.id.tue);
                tue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getActivity(), MapsActivity.class);
                        List<String> names = new ArrayList<String>();
                        List<Double> lat = new ArrayList<Double>();
                        List<Double> lon = new ArrayList<Double>();
                        List<Long> time = new ArrayList<Long>();
                        for(int i=0 ;i<jsonResult.schedule.startDay.length;i++){
                            if (jsonResult.schedule.startDay[i].equals("Tue")){
                                names.add(jsonResult.schedule.builNames[i]);
                                lat.add(jsonResult.schedule.lat[i]);
                                lon.add(jsonResult.schedule.lon[i]);
                                time.add(epochstartTime[i]);
                        /*intent.putExtra("names"+i,jsonResult.schedule.builNames[i]);
                        intent.putExtra("lat"+i,jsonResult.schedule.lat[i]);
                        intent.putExtra("long"+i,jsonResult.schedule.lon[i]);*/
                            }
                        }
                        String[] name = new String[names.size()];
                        double[] latit =  new double[name.length];
                        double[] longit = new double[name.length];
                        long[] stime = new long[name.length];
                        for(int i=0;i<name.length;i++){
                            name[i]=names.get(i);
                            latit[i]=lat.get(i);
                            longit[i]=lon.get(i);
                            stime[i]=time.get(i);
                        }
                        intent.putExtra("names",name);
                        intent.putExtra("lat",latit);
                        intent.putExtra("lon",longit);
                        Log.v("index",""+stime.length+"");
                        intent.putExtra("epochstime",stime);
                        intent.putExtra("epochetime",epochendTime);
                        startActivity(intent);
                    }
                });

                Button wed = (Button)view.findViewById(R.id.wed);
                wed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getActivity(), MapsActivity.class);
                        List<String> names = new ArrayList<String>();
                        List<Double> lat = new ArrayList<Double>();
                        List<Double> lon = new ArrayList<Double>();
                        List<Long> time = new ArrayList<Long>();
                        for(int i=0 ;i<jsonResult.schedule.startDay.length;i++){
                            if (jsonResult.schedule.startDay[i].equals("Wed")){
                                names.add(jsonResult.schedule.builNames[i]);
                                lat.add(jsonResult.schedule.lat[i]);
                                lon.add(jsonResult.schedule.lon[i]);
                                time.add(epochstartTime[i]);
                        /*intent.putExtra("names"+i,jsonResult.schedule.builNames[i]);
                        intent.putExtra("lat"+i,jsonResult.schedule.lat[i]);
                        intent.putExtra("long"+i,jsonResult.schedule.lon[i]);*/
                            }
                        }
                        String[] name = new String[names.size()];
                        double[] latit =  new double[name.length];
                        double[] longit = new double[name.length];
                        long[] stime = new long[name.length];
                        for(int i=0;i<name.length;i++){
                            name[i]=names.get(i);
                            latit[i]=lat.get(i);
                            longit[i]=lon.get(i);
                            stime[i]=time.get(i);
                        }
                        intent.putExtra("names",name);
                        intent.putExtra("lat",latit);
                        intent.putExtra("lon",longit);
                        Log.v("index",""+stime.length+"");
                        intent.putExtra("epochstime",stime);
                        intent.putExtra("epochetime",epochendTime);
                        startActivity(intent);
                    }
                });

                Button thr = (Button)view.findViewById(R.id.thr);
                thr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getActivity(), MapsActivity.class);
                        List<String> names = new ArrayList<String>();
                        List<Double> lat = new ArrayList<Double>();
                        List<Double> lon = new ArrayList<Double>();
                        List<Long> time = new ArrayList<Long>();
                        for(int i=0 ;i<jsonResult.schedule.startDay.length;i++){
                            if (jsonResult.schedule.startDay[i].equals("Thr")){
                                names.add(jsonResult.schedule.builNames[i]);
                                lat.add(jsonResult.schedule.lat[i]);
                                lon.add(jsonResult.schedule.lon[i]);
                                time.add(epochstartTime[i]);
                        /*intent.putExtra("names"+i,jsonResult.schedule.builNames[i]);
                        intent.putExtra("lat"+i,jsonResult.schedule.lat[i]);
                        intent.putExtra("long"+i,jsonResult.schedule.lon[i]);*/
                            }
                        }
                        String[] name = new String[names.size()];
                        double[] latit =  new double[name.length];
                        double[] longit = new double[name.length];
                        long[] stime = new long[name.length];
                        for(int i=0;i<name.length;i++){
                            name[i]=names.get(i);
                            latit[i]=lat.get(i);
                            longit[i]=lon.get(i);
                            stime[i]=time.get(i);
                        }
                        intent.putExtra("names",name);
                        intent.putExtra("lat",latit);
                        intent.putExtra("lon",longit);
                        Log.v("index",""+stime.length+"");
                        intent.putExtra("epochstime",stime);
                        intent.putExtra("epochetime",epochendTime);
                        startActivity(intent);
                    }
                });


                Button fri = (Button)view.findViewById(R.id.fri);
                fri.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getActivity(), MapsActivity.class);
                        List<String> names = new ArrayList<String>();
                        List<Double> lat = new ArrayList<Double>();
                        List<Double> lon = new ArrayList<Double>();
                        List<Long> time = new ArrayList<Long>();
                        for(int i=0 ;i<jsonResult.schedule.startDay.length;i++){
                            if (jsonResult.schedule.startDay[i].equals("Fri")){
                                names.add(jsonResult.schedule.builNames[i]);
                                lat.add(jsonResult.schedule.lat[i]);
                                lon.add(jsonResult.schedule.lon[i]);
                                time.add(epochstartTime[i]);
                        /*intent.putExtra("names"+i,jsonResult.schedule.builNames[i]);
                        intent.putExtra("lat"+i,jsonResult.schedule.lat[i]);
                        intent.putExtra("long"+i,jsonResult.schedule.lon[i]);*/
                            }
                        }
                        String[] name = new String[names.size()];
                        double[] latit =  new double[name.length];
                        double[] longit = new double[name.length];
                        long[] stime = new long[name.length];
                        for(int i=0;i<name.length;i++){
                            name[i]=names.get(i);
                            latit[i]=lat.get(i);
                            longit[i]=lon.get(i);
                            stime[i]=time.get(i);
                        }
                        intent.putExtra("names",name);
                        intent.putExtra("lat",latit);
                        intent.putExtra("lon",longit);
                        Log.v("index",""+stime.length+"");
                        intent.putExtra("epochstime",stime);
                        intent.putExtra("epochetime",epochendTime);
                        startActivity(intent);
                    }
                });




               /* Intent intent = new Intent(getActivity(), MapsActivity.class);
                List<String> names = new ArrayList<String>();
                List<Double> lat = new ArrayList<Double>();
                List<Double> lon = new ArrayList<Double>();
                List<Long> time = new ArrayList<Long>();
                for(int i=0 ;i<jsonResult.schedule.startDay.length;i++){
                    if (jsonResult.schedule.startDay[i].equals("Wed")){
                        names.add(jsonResult.schedule.builNames[i]);
                        lat.add(jsonResult.schedule.lat[i]);
                        lon.add(jsonResult.schedule.lon[i]);
                        time.add(epochstartTime[i]);
                        *//*intent.putExtra("names"+i,jsonResult.schedule.builNames[i]);
                        intent.putExtra("lat"+i,jsonResult.schedule.lat[i]);
                        intent.putExtra("long"+i,jsonResult.schedule.lon[i]);*//*
                    }
                }
                String[] name = new String[names.size()];
                double[] latit =  new double[name.length];
                double[] longit = new double[name.length];
                long[] stime = new long[name.length];
                for(int i=0;i<name.length;i++){
                    name[i]=names.get(i);
                    latit[i]=lat.get(i);
                    longit[i]=lon.get(i);
                    stime[i]=time.get(i);
                }
                intent.putExtra("names",name);
                intent.putExtra("lat",latit);
                intent.putExtra("lon",longit);
                Log.v("index",""+stime.length+"");
                intent.putExtra("epochstime",stime);
                intent.putExtra("epochetime",epochendTime);
                startActivity(intent);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
            return view;
        }



}

