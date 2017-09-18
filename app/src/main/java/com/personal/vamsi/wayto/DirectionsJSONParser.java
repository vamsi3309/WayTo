package com.personal.vamsi.wayto;

/**
 * Created by navat on 9/17/2017.
 */

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class DirectionsJSONParser extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {

    @Override
    public List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try{
            jObject = new JSONObject(jsonData[0]);
            DirectionsJSONParser parser = new DirectionsJSONParser();

            // Starts parsing data
            routes = parser.parse(jObject);
        }catch(Exception e){
            e.printStackTrace();
        }
        return routes;
    }
    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String,String>>> parse(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {
            jRoutes = jObject.getJSONArray("routes");
            JSONArray jSubsteps;
            StringBuilder directions = new StringBuilder();
            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ((JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);
                        if(jSteps.getJSONObject(k).getString("travel_mode").equals("DRIVING")
                                ||jSteps.getJSONObject(k).getString("travel_mode").equals("WALKING")
                                ||jSteps.getJSONObject(k).getString("travel_mode").equals("BICYCLING"))
                        {
                            directions.append(""+k+".)"+(String)(((JSONObject)jSteps.get(k)).get("html_instructions"))+"\n");
                        }
                        else if (((JSONObject) jSteps.get(k)).getString("travel_mode").equals("TRANSIT"))
                        {
                            directions.append((String)((JSONObject) jSteps.get(k)).getJSONObject("transit_details").getString("headsign")+"    BUS:  "
                            +(String)(((JSONObject) jSteps.get(k)).getJSONObject("transit_details").getJSONObject("line").getString("name"))+"   ride for "
                            +(String)(((JSONObject) jSteps.get(k)).getJSONObject("transit_details").getString("num_stops"))+" stops and get down at "
                                    +(String)(((JSONObject) jSteps.get(k)).getJSONObject("transit_details").getJSONObject("arrival_stop").getString("name"))+"\n");
                        }
                            else {
                                jSubsteps = ((JSONObject) jSteps.get(k)).getJSONArray("steps");
                                for(int m=0;m<jSubsteps.length();m++)
                                {
                                    directions.append(""+k+"."+m+".)"+jSubsteps.getJSONObject(m).get("html_instructions")+"\n");
                                }
                            }

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }
            Log.v("directions",directions.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        return routes;
    }

    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


}