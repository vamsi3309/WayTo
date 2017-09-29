package com.personal.vamsi.wayto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.graphics.Color
import android.location.*
import android.os.StrictMode
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.FragmentActivity
import android.os.Bundle
import android.os.SystemClock.sleep
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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

 class MapsActivity : FragmentActivity(), OnMapReadyCallback, LocationListener {

     override fun onLocationChanged(p0: Location?) {

     }

     override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

     }

     override fun onProviderEnabled(p0: String?) {

     }

     override fun onProviderDisabled(p0: String?) {

     }

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
        //val mGeoDataClient
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
        (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Log.v("Nopermission","no permission")

            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
        else if(intent.getStringExtra("action").equals("schedule")){
            plotSchedule()
        }
        else if (intent.getStringExtra("action").equals("navigation"))
        {
            plotNavigation()
        }
    }

    fun plotNavigation(){
        val crit = Criteria()

        var mLocationManager : LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        mLocationManager.requestLocationUpdates(mLocationManager.getBestProvider(crit,true),0,0f,this)
        var locationGPS=mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(crit,true))
        try{
        if (locationGPS == null){
            locationGPS=mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        }
        catch (e: NullPointerException){
            Toast.makeText(this,"Problem with accessing the location " +
                    "please try again after a while",Toast.LENGTH_LONG).show()
        }
        var fromName=""
        var fromLocation=LatLng(0.0,0.0)
        if(intent.getStringExtra("fromname").equals("My location")){
             fromName = "My Location"
             fromLocation = LatLng(locationGPS.latitude, locationGPS.longitude)
        }
        else {
            fromName = intent.extras.getString("fromname")
            fromLocation = LatLng(intent.getDoubleExtra("fromlat", 0.0), intent.getDoubleExtra("fromlong", 0.0))
        }
        val toName = intent.getStringExtra("toname")
        val toLocation = LatLng(intent.getDoubleExtra("tolat", 0.0), intent.getDoubleExtra("tolong", 0.0))
        val mode = intent.getStringExtra("mode")


        mMap!!.addMarker(MarkerOptions().position(fromLocation).title(fromName))
        mMap!!.addMarker(MarkerOptions().position(toLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(toName))
        // Add a marker in Sydney and move the camera
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(fromLocation))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(fromLocation, 15.0f))
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                intent.getIntExtra("hours",calendar.get(Calendar.HOUR_OF_DAY)), intent.getIntExtra("mins",calendar.get(Calendar.MINUTE)))
        Log.v("time ",""+calendar.timeInMillis)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        plotDirections(getDirections(getRequestString(fromLocation, toLocation, mode, "&departure_time="+calendar.timeInMillis / 1000L)))
        val bottomSheetDialogFragment = BottomSheetActivity()
        val args = Bundle()
        args.putString("key", textDirections.toString())
        bottomSheetDialogFragment.arguments = args
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
             1 -> {
                 Log.v("onrequest","request")
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(intent.getStringExtra("action").equals("schedule")){
                        Log.v("permision","permission granted")
                        plotSchedule()
                    }
                    else if (intent.getStringExtra("action").equals("navigation"))
                    {
                        plotNavigation()
                    }
                }

                 else
                {
                    Toast.makeText(this,"Location permissions denied",Toast.LENGTH_LONG).show()
                    val startMain = Intent(this,MainActivity::class.java)
                    startActivity(startMain)
                }
            }

            -1 ->{
                Log.v("request","request failure")
            }
        }

    }


    fun plotSchedule(){
        val mLocationManager : LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        var locationGPS=mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        try{
            if (locationGPS == null){
                locationGPS=mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
        }
        catch (e: NullPointerException){
            Toast.makeText(this,"Problem with accessing the location " +
                    "please try again after a while",Toast.LENGTH_LONG).show()
        }
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
        Log.v("location lat",""+locationGPS.latitude)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        mMap!!.addMarker(MarkerOptions().position(LatLng(locationGPS.latitude,locationGPS.longitude)!!).title("My Location"))
        for (i in 0..starttime.size-1)
            mMap!!.addMarker(MarkerOptions().position(locations[i]!!).title(names[i]))
        textDirections.append("Class "+1+" :: Class name "+cnames[0]+"<br /><br />")
        plotDirections(getDirections(getRequestString(LatLng(locationGPS.latitude,locationGPS.longitude), locations[0]!!, "transit", "&arrival_time="+starttime[0])))
        for (i in 0..starttime.size - 2) {
            textDirections.append("Class "+(i+2)+" :: Class name "+cnames[i+1]+"<br /><br />")
            plotDirections(getDirections(getRequestString(locations[i]!!, locations[i + 1]!!, "transit", "&arrival_time="+starttime[i + 1])))
        }
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(locations[0], 15.0f))

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

    fun getRequestString(start: LatLng, end: LatLng, mode: String, time: String): String {
        val apilink = "https://maps.googleapis.com/maps/api/directions/json"
        val apikey = "&key=AIzaSyBb5uFimk3fSxnf77F81qrFs3I91BAAcjM"
        val source = "?origin="
        val destin = "&destination="
        Log.v("url  ", apilink + source + start.latitude + "," + start.longitude + destin + end.latitude + "," + end.longitude + time + "&mode=" + mode + apikey)
        return apilink + source + start.latitude + "," + start.longitude + destin + end.latitude + "," + end.longitude + time + time + "&mode=" + mode + apikey
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

