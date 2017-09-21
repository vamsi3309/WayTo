package com.personal.vamsi.wayto

import android.content.res.AssetManager
import android.graphics.Color
import android.os.StrictMode
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.FragmentActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONObject

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.util.ArrayList
import java.util.Arrays
import java.util.Calendar
import java.util.HashMap
import java.util.TimeZone

class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    internal var schedule: JsonExtracter.Schedule? = null
    //internal var results: DirectionsJSONParser.DirectionsResult?=null
    internal var buildings: JsonExtracter.Buildings? = null
    internal var textDirections = StringBuilder()
    internal var locations: Array<LatLng>? = null
    internal var builNames: Array<String>? = null
    /*public MapsActivity(JsonExtracter.Schedule sch){
        schedule=sch;
        locations=schedule.schLocations;
        builNames=schedule.builNames;
    }
    public MapsActivity(){

    }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        /* AssetManager assetManager = getResources().getAssets();
        JsonExtracter jsonExtracter = new JsonExtracter();
        JsonExtracter.JsonResult jsonResult = jsonExtracter.doInBackground(assetManager);*/
        val builNames = intent.getStringArrayExtra("names")
        val lat = intent.extras.getDoubleArray("lat")
        val lon = intent.getDoubleArrayExtra("lon")
        val starttime = intent.extras.getLongArray("epochstime")
        val etime =  intent.extras.getLongArray("epochetime")
        val classnames = intent.extras.getStringArray("class_name")
        val sortTime = starttime
        Arrays.sort(sortTime!!)
        //LatLng[] locations = new LatLng[getIntent().getDoubleArrayExtra("lat"),];
        val locations = arrayOfNulls<LatLng>(starttime.size)
        val names = arrayOfNulls<String>(starttime.size)
        val endtime = arrayOfNulls<Long>(starttime.size)
        val cnames = arrayOfNulls<String>(starttime.size)
        for (i in 0..sortTime.size-1) {
            for (j in 0..starttime.size-1) {
                if (sortTime[i] == starttime[j]) {
                    locations[i] = LatLng(lat[j], lon[j])
                    names[i] = builNames[j]
                    endtime[i] = etime[i]
                    cnames[i] = classnames[i]
                }
            }
        }
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        for (i in 0..starttime.size-1)
            mMap!!.addMarker(MarkerOptions().position(locations[i]!!).title(names[i]))
        textDirections.append("Class "+1+" :: Class name "+cnames[0]+"<br /><br />")
        plotDirections(getDirections(getRequestString(LatLng(33.937877,-83.367305), locations[0]!!, "transit", starttime[0])))
        for (i in 0..starttime.size - 2) {
            textDirections.append("Class "+(i+2)+" :: Class name "+cnames[i+1]+"<br /><br />")
            plotDirections(getDirections(getRequestString(locations[i]!!, locations[i + 1]!!, "transit", starttime[i + 1])))
        }
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(locations[0], 15.0f))
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

        val bottomSheetDialogFragment = BottomSheetActivity()
        val args = Bundle()
        args.putString("key", textDirections.toString())
        bottomSheetDialogFragment.arguments = args
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)

    }

    fun getDirections(request: String): String {
        val sb = StringBuilder()
        var obj = JSONObject()
        try {
            val httpClient = DefaultHttpClient()
            val httpPost = HttpPost(request)
            val httpResponse = httpClient.execute(httpPost)
            val httpEntity = httpResponse.entity
            val `is` = httpEntity.content
            val reader = BufferedReader(InputStreamReader(`is`, "iso-8859-1"), 8)
            var line: String?  = reader.readLine()
            while (line != null) {
                sb.append(line!! + "\n")
                line = reader.readLine()
            }
            `is`.close()
            //Log.v("Directions",sb.toString());
            obj = JSONObject(sb.toString())

        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val temp = sb.toString()
        //Log.v("directions",temp);
        //return obj;
        return sb.toString()
    }

    fun getRequestString(start: LatLng, end: LatLng, mode: String, time: Long): String {
        val apilink = "https://maps.googleapis.com/maps/api/directions/json"
        val apikey = "&key=AIzaSyBb5uFimk3fSxnf77F81qrFs3I91BAAcjM"
        val source = "?origin="
        val destin = "&destination="
        val dtime = "&arrival_time="
        Log.v("url  ", apilink + source + start.latitude + "," + start.longitude + destin + end.latitude + "," + end.longitude + dtime + time + "&mode=" + mode + apikey)
        return apilink + source + start.latitude + "," + start.longitude + destin + end.latitude + "," + end.longitude + dtime + time + "&mode=" + mode + apikey
    }

    fun plotDirections(directions: String) {
        var results: DirectionsJSONParser.DirectionsResult?=null
        var parser = DirectionsJSONParser()
        results = parser.doInBackground(directions)
        onPostExecute(results!!.routes!!)
        textDirections.append(results.textDirections + "<br />")
    }

    fun showDirections(view: View) {
        val bottomSheetDialogFragment = BottomSheetActivity()
        val args = Bundle()
        args.putString("key", textDirections.toString())
        bottomSheetDialogFragment.arguments = args
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }


    protected fun onPostExecute(result: List<List<HashMap<String, String>>>) {
        var points: ArrayList<LatLng>? = null
        var lineOptions: PolylineOptions? = null
        val markerOptions = MarkerOptions()

        // Traversing through all the routes
        for (i in 0..result.size-1) {
            points = ArrayList()
            lineOptions = PolylineOptions()

            // Fetching i-th route
            val path = result[i]

            // Fetching all the points in i-th route
            for (j in 0..path.size-1) {
                val point = path[j]

                val lat = java.lang.Double.parseDouble(point["lat"])
                val lng = java.lang.Double.parseDouble(point["lng"])
                val position = LatLng(lat, lng)

                points.add(position)
            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points)
            lineOptions.width(8f)
            lineOptions.color(Color.RED)
        }

        // Drawing polyline in the Google Map for the i-th route
        mMap!!.addPolyline(lineOptions)
    }


}

