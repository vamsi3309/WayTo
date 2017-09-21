package com.personal.vamsi.wayto;

import android.content.Intent;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;


public class MapsActivityRoute extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DirectionsJSONParser.DirectionsResult results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_route);
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
        String fromName=getIntent().getExtras().getString("fromname");
        LatLng fromLocation = new LatLng(getIntent().getDoubleExtra("fromlat",0.0),getIntent().getDoubleExtra("fromlong",0.0));
        String toName=getIntent().getStringExtra("toname");
        LatLng toLocation = new LatLng(getIntent().getDoubleExtra("tolat",0.0),getIntent().getDoubleExtra("tolong",0.0));
        String mode = getIntent().getStringExtra("mode");
        mMap.addMarker(new MarkerOptions().position(fromLocation).title(fromName));
        mMap.addMarker(new MarkerOptions().position(toLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(toName));
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(fromLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fromLocation, 15.0f));
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        calendar.clear();
        calendar.set(2017,9,20, 17, 15);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        plotDirections(getDirections(getRequestString(fromLocation,toLocation,mode, calendar.getTimeInMillis()/1000L)));
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
        Log.v("url  ",apilink+source+start.latitude+","+start.longitude+destin+end.latitude+","+end.longitude+"&departure_time="+time+"&mode="+mode+apikey);
        return apilink+source+start.latitude+","+start.longitude+destin+end.latitude+","+end.longitude+"&departure_time="+time+"&mode="+mode+apikey;
    }

    public void plotDirections(String directions){
        DirectionsJSONParser parser = new DirectionsJSONParser();
        results = parser.doInBackground(directions);
        onPostExecute(results.routes);
        BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetActivity();
        Bundle args = new Bundle();
        args.putString("key", results.textDirections);
        bottomSheetDialogFragment.setArguments(args);
        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    public void showDirections(View view){
        BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetActivity();
        Bundle args = new Bundle();
        args.putString("key",results.textDirections);
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
