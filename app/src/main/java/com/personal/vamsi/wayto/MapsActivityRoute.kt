package com.personal.vamsi.wayto

import android.content.Intent
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
import com.google.android.gms.maps.model.BitmapDescriptor
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
import java.util.Calendar
import java.util.HashMap
import java.util.TimeZone


class MapsActivityRoute : FragmentActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    internal var results: DirectionsJSONParser.DirectionsResult? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_route)
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
        val fromName = intent.extras.getString("fromname")
        val fromLocation = LatLng(intent.getDoubleExtra("fromlat", 0.0), intent.getDoubleExtra("fromlong", 0.0))
        val toName = intent.getStringExtra("toname")
        val toLocation = LatLng(intent.getDoubleExtra("tolat", 0.0), intent.getDoubleExtra("tolong", 0.0))
        val mode = intent.getStringExtra("mode")
        mMap!!.addMarker(MarkerOptions().position(fromLocation).title(fromName))
        mMap!!.addMarker(MarkerOptions().position(toLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(toName))
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(fromLocation))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(fromLocation, 15.0f))
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))
        calendar.clear()
        calendar.set(2017, 9, 20, 17, 15)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        plotDirections(getDirections(getRequestString(fromLocation, toLocation, mode, calendar.timeInMillis / 1000L)))
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
            var line: String? = reader.readLine()
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
        Log.v("url  ", apilink + source + start.latitude + "," + start.longitude + destin + end.latitude + "," + end.longitude + "&departure_time=" + time + "&mode=" + mode + apikey)
        return apilink + source + start.latitude + "," + start.longitude + destin + end.latitude + "," + end.longitude + "&departure_time=" + time + "&mode=" + mode + apikey
    }

    fun plotDirections(directions: String) {
        val parser = DirectionsJSONParser()
        results = parser.doInBackground(directions)
        onPostExecute(results!!.routes!!)
        val bottomSheetDialogFragment = BottomSheetActivity()
        val args = Bundle()
        args.putString("key", results!!.textDirections!!)
        bottomSheetDialogFragment.arguments = args
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    fun showDirections(view: View) {
        val bottomSheetDialogFragment = BottomSheetActivity()
        val args = Bundle()
        args.putString("key", results!!.textDirections)
        bottomSheetDialogFragment.arguments = args
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }


    protected fun onPostExecute(result: List<List<HashMap<String, String>>>) {
        var points: ArrayList<LatLng>? = null
        var lineOptions: PolylineOptions? = null
        val markerOptions = MarkerOptions()

        // Traversing through all the routes
        for (i in result.indices) {
            points = ArrayList()
            lineOptions = PolylineOptions()

            // Fetching i-th route
            val path = result[i]

            // Fetching all the points in i-th route
            for (j in path.indices) {
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
