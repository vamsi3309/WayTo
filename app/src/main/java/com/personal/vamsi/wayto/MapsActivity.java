package com.personal.vamsi.wayto;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DirectionsJSONParser.DirectionsResult results;

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
        AssetManager assetManager = getResources().getAssets();
        JsonExtracter jsonExtracter = new JsonExtracter();
        JsonExtracter.Result result = jsonExtracter.doInBackground(assetManager);
        String[] builNames = result.builNames;
        LatLng[] locations = result.locations;
        for (int j = 0; j < builNames.length; j++)
            mMap.addMarker(new MarkerOptions().position(locations[j]).title(builNames[j]));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locations[0], 15.0f));
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();

        calendar.set(2017,9,18, 10, 15);
        long secondsSinceEpoch = calendar.getTimeInMillis() / 1000L;
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
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mMap.addMarker(new MarkerOptions().position(new LatLng(33.918599,-83.367435)).title("Home"));
        plotDirections(getDirections(getRequestString(new LatLng(33.918599,-83.367435),locations[1],"driving", 1505728800)));

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

        //return obj;
        return sb.toString();
    }

    public String getRequestString(LatLng start,LatLng end,String mode, int time){
        String apilink = "https://maps.googleapis.com/maps/api/directions/json";
        String apikey = "&key=AIzaSyBb5uFimk3fSxnf77F81qrFs3I91BAAcjM";
        String source = "?origin=";
        String destin = "&destination=";
        Log.v("url  ",apilink+source+start.latitude+","+start.longitude+destin+end.latitude+","+end.longitude+"&mode="+mode+"&departure_time="+time+apikey);
        return apilink+source+start.latitude+","+start.longitude+destin+end.latitude+","+end.longitude+"&mode="+mode+apikey;
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

