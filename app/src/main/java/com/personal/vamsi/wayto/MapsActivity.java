package com.personal.vamsi.wayto;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DirectionsJSONParser.DirectionsResult results;
    JsonExtracter.Schedule schedule;
    JsonExtracter.Buildings buildings;
    StringBuilder textDirections = new StringBuilder();
    LatLng[] locations;
    String[] builNames;
    /*public MapsActivity(JsonExtracter.Schedule sch){
        schedule=sch;
        locations=schedule.schLocations;
        builNames=schedule.builNames;
    }
    public MapsActivity(){

    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       /* AssetManager assetManager = getResources().getAssets();
        JsonExtracter jsonExtracter = new JsonExtracter();
        JsonExtracter.JsonResult jsonResult = jsonExtracter.doInBackground(assetManager);*/
        String[] builNames = getIntent().getStringArrayExtra("names");
        double[] lat = getIntent().getExtras().getDoubleArray("lat");
        double[] lon = getIntent().getDoubleArrayExtra("lon");
        long[] starttime = getIntent().getExtras().getLongArray("epochstime");
        long[] sortTime = starttime;
        Arrays.sort(sortTime);
        //LatLng[] locations = new LatLng[getIntent().getDoubleArrayExtra("lat"),];
        LatLng[] locations = new LatLng[sortTime.length];
        String[] names = new String[sortTime.length];
        for(int i=0;i<sortTime.length;i++){
            for(int j=0;j<sortTime.length;j++){
                if(sortTime[i]==starttime[j]){
                    locations[i] =  new LatLng(lat[j],lon[j]);
                    names[i]=builNames[j];
                }
            }
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        for(int i=0;i<sortTime.length;i++)
            mMap.addMarker(new MarkerOptions().position(locations[i]).title(names[i]));
        plotDirections(getDirections(getRequestString(new LatLng(33.918599,-83.367435),locations[0],"transit", starttime[0])));
        for(int i=0;i<sortTime.length-1;i++){
            plotDirections(getDirections(getRequestString(locations[i],locations[i+1],"transit", starttime[i+1])));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locations[0], 15.0f));
       /* LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location locationGPS=mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/

       /* mMap.addMarker(new MarkerOptions().position(new LatLng(33.918599,-83.367435)).title("Home"));
        plotDirections(getDirections(getRequestString(new LatLng(33.918599,-83.367435),new LatLng(lat[0],lon[0]),"transit", 1508534117)));
        plotDirections(getDirections(getRequestString(new LatLng(lat[0],lon[0]),new LatLng(lat[1],lon[1]),"transit", 1508534117)));*/

        BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetActivity();
        Bundle args = new Bundle();
        args.putString("key", textDirections.toString());
        bottomSheetDialogFragment.setArguments(args);
        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

    }

    @SuppressWarnings("deprecation")
    public String getDirections(String request){
        StringBuilder sb = new StringBuilder();
        JSONObject obj = new JSONObject();
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(request);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream is = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            //Log.v("Directions",sb.toString());
            obj = new JSONObject(sb.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }

        String temp = sb.toString();
        //Log.v("directions",temp);
        //return obj;
        return sb.toString();
    }

    public String getRequestString(LatLng start,LatLng end,String mode, long time){
        String apilink = "https://maps.googleapis.com/maps/api/directions/json";
        String apikey = "&key=AIzaSyBb5uFimk3fSxnf77F81qrFs3I91BAAcjM";
        String source = "?origin=";
        String destin = "&destination=";
        String dtime="&arrival_time=";
        Log.v("url  ",apilink+source+start.latitude+","+start.longitude+destin+end.latitude+","+end.longitude+dtime+time+"&mode="+mode+apikey);
        return apilink+source+start.latitude+","+start.longitude+destin+end.latitude+","+end.longitude+dtime+time+"&mode="+mode+apikey;
    }

    public void plotDirections(String directions){
        DirectionsJSONParser parser = new DirectionsJSONParser();
        results = parser.doInBackground(directions);
        onPostExecute(results.routes);
        textDirections.append(results.textDirections+"<br />");
    }

    public void showDirections(View view){
        BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetActivity();
        Bundle args = new Bundle();
        args.putString("key",textDirections.toString());
        bottomSheetDialogFragment.setArguments(args);
        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }


    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;
        MarkerOptions markerOptions = new MarkerOptions();

        // Traversing through all the routes
        for(int i=0;i<result.size();i++){
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for(int j=0;j<path.size();j++){
                HashMap<String,String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(8);
            lineOptions.color(Color.RED);
        }

        // Drawing polyline in the Google Map for the i-th route
        mMap.addPolyline(lineOptions);
    }


}

