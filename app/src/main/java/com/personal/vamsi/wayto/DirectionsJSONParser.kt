package com.personal.vamsi.wayto

import android.os.AsyncTask
import android.util.Log

import java.util.ArrayList
import java.util.HashMap
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import com.google.android.gms.maps.model.LatLng

class DirectionsJSONParser : AsyncTask<String, Int, DirectionsJSONParser.DirectionsResult>() {

    inner class DirectionsResult {
        internal var routes: List<List<HashMap<String, String>>>? = null
        internal var textDirections: String? = null
    }

    public override fun doInBackground(vararg jsonData: String): DirectionsResult {

        val jObject: JSONObject
        val drs = DirectionsResult()

        try {
            jObject = JSONObject(jsonData[0])
            val parser = DirectionsJSONParser()

            // Starts parsing data
            drs.routes = parser.parse(jObject).routes
            drs.textDirections = parser.parse(jObject).textDirections
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Log.v("textDirections", drs.textDirections)
        return drs
    }

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude  */
    fun parse(jObject: JSONObject): DirectionsResult {

        val routes = ArrayList<List<HashMap<String, String>>>()
        val directions = StringBuilder()
        var jRoutes: JSONArray? = null
        var jLegs: JSONArray? = null
        var jSteps: JSONArray? = null

        try {
            jRoutes = jObject.getJSONArray("routes")
            var jSubsteps: JSONArray

            /** Traversing all routes  */
            for (i in 0..jRoutes!!.length() - 1) {
                jLegs = (jRoutes.get(i) as JSONObject).getJSONArray("legs")
                val path = ArrayList<HashMap<String, String>>()

                /** Traversing all legs  */
                for (j in 0..jLegs!!.length() - 1) {
                    jSteps = (jLegs.get(j) as JSONObject).getJSONArray("steps")
                    if (jLegs.getJSONObject(i).has("departure_time")) {
                        directions.append("<br />" + "Departure Time: " + jLegs.getJSONObject(i).getJSONObject("departure_time").getString("text") + "<br />")
                        directions.append("Arrival Time: " + jLegs.getJSONObject(i).getJSONObject("arrival_time").getString("text") + "<br /><br />")
                    }
                    /** Traversing all steps  */
                    for (k in 0..jSteps!!.length() - 1) {
                        var polyline = ""
                        polyline = ((jSteps.get(k) as JSONObject).get("polyline") as JSONObject).get("points") as String
                        val list = decodePoly(polyline)
                        if (jSteps.getJSONObject(k).getString("travel_mode") == "DRIVING"
                                || jSteps.getJSONObject(k).getString("travel_mode") == "WALKING"
                                || jSteps.getJSONObject(k).getString("travel_mode") == "BICYCLING") {
                            directions.append("" + (k + 1) + ".)" + (jSteps.get(k) as JSONObject).get("html_instructions") as String + "<br />")
                        } else if ((jSteps.get(k) as JSONObject).getString("travel_mode") == "TRANSIT") {
                            directions.append("" + (k + 1) + ".)" + (jSteps.get(k) as JSONObject).getJSONObject("transit_details").getString("headsign") as String + " <br /><b>BUS:  "
                                    + (jSteps.get(k) as JSONObject).getJSONObject("transit_details").getJSONObject("line").getString("name") as String
                                    + " "+(jSteps.get(k) as JSONObject).getJSONObject("transit_details").getJSONObject("line").getString("short_name") as String+"</b> <br />Ride for "
                                    + (jSteps.get(k) as JSONObject).getJSONObject("transit_details").getString("num_stops") as String + " stops and get down at "
                                    + (jSteps.get(k) as JSONObject).getJSONObject("transit_details").getJSONObject("arrival_stop").getString("name") as String + "<br />")
                        } else {
                            jSubsteps = (jSteps.get(k) as JSONObject).getJSONArray("steps")
                            for (m in 0..jSubsteps.length() - 1) {
                                directions.append("" + (k + 1) + "." + m + ".)" + jSubsteps.getJSONObject(m).get("html_instructions") + "<br />")
                            }
                        }
                        /** Traversing all points  */
                        for (l in list.indices) {
                            val hm = HashMap<String, String>()
                            hm.put("lat", java.lang.Double.toString(list[l].latitude))
                            hm.put("lng", java.lang.Double.toString(list[l].longitude))
                            path.add(hm)
                        }
                    }
                    routes.add(path)
                }
            }
            //Log.v("directions",directions.toString());
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val result = DirectionsResult()
        result.routes = routes
        result.textDirections = directions.toString()
        return result
    }

    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private fun decodePoly(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                    lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }


}
