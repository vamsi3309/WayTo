package com.personal.vamsi.wayto

import android.content.res.AssetManager
import android.os.AsyncTask
import android.util.Log

import com.google.android.gms.maps.model.LatLng

import org.json.JSONArray
import org.json.JSONException

import java.io.IOException
import java.io.InputStream
import java.util.*


class JsonExtracter : AsyncTask<AssetManager, Int, JsonExtracter.JsonResult>() {
    inner class JsonResult {
         var schedule: Schedule? = null
         var buildings: Buildings? = null
    }

     inner class Schedule {
        var builNames: Array<String>?=null
        var classNames: Array<String>? = null
        var roomNos: Array<String>? = null
        var startDay: Array<String>? = null
        var startTime: Array<String>? = null
        var endTime: Array<String>? = null
        var endDay: Array<String>? = null
        internal var lat: DoubleArray? = null
        internal var lon: DoubleArray? = null
        internal var scharray: JSONArray? = null
        internal var bularray: JSONArray? = null
        internal var schLocations: Array<LatLng>? = null
    }

     inner class Buildings {
        internal var id: IntArray? = null
        internal var locations: Array<LatLng>? = null
        internal var name: Array<String>? = null
        internal var code: Array<String>? = null
        internal var searchText: Array<String>? = null
        internal var jsonArray: JSONArray? = null
    }

    /*private var locations: Array<LatLng?>? = null
    private var builNames: Array<String?>? = null*/
    private fun getExtractedData(ass: AssetManager): JsonResult {

        val res = JsonResult()
        try {

            var sch_read:String?=null
            val sc_read = ass.open("schedule.json")
            val sc_size = sc_read.available()
            val buf_sc = ByteArray(sc_size)
            sc_read.read(buf_sc)
            sc_read.close()
            sch_read = String(buf_sc)
            val sch_data = JSONArray(sch_read)

            val bl_read = ass.open("buildings.json")
            val bl_size = bl_read.available()
            val buf_bl = ByteArray(bl_size)
            bl_read.read(buf_bl)
            bl_read.close()
            var bul_read = String(buf_bl)
            var bul_data = JSONArray(bul_read)

            var latCord = DoubleArray(sch_data.length())
            var longCord = DoubleArray(sch_data.length())
            //builNames = arrayOfNulls(sch_data.length())
            var BulNos = IntArray(sch_data.length())
            for (i in 0..sch_data.length() - 1) {
                BulNos[i] = sch_data.getJSONObject(i).getInt("buildingID")
                Log.v("Building NO",""+BulNos[i]);
            }
            var locations= Array(bul_data.length()){ LatLng(0.0,0.0) }
            var builNames= Array(sch_data.length()){ "n" }
            //locations = arrayOfNulls(sch_data.length())

            for (i in 0..bul_data.length()-1)
            {
                for (j in 0..BulNos.size -1) {
                    Log.v("index",i.toString()+"  "+j.toString())
                    if (bul_data.getJSONObject(i).getInt("id") == BulNos[j]) {
                        builNames?.set(j, bul_data.getJSONObject(i).getString("name"))
                        //Log.v("Building name",builNames[j]);
                        latCord[j] = bul_data!!.getJSONObject(i)!!.getJSONObject("location")!!.getDouble("lat")
                        //Log.v("Lat Cord","["+j+"]"+" = "+latCord[j]);
                        longCord[j] = bul_data!!.getJSONObject(i)!!.getJSONObject("location")!!.getDouble("lng")
                        //Log.v("Long Cord","["+j+"]"+" = "+longCord[j]);
                        locations?.set(j, LatLng(latCord[j], longCord[j]))

                    }
                }
            }
            var temp1 = Array(sch_data.length()){ "n" }
            var temp2 = Array(sch_data.length()){ "n" }
            var temp3= Array(sch_data.length()){ "n" }
            var temp4 = Array(sch_data.length()){ "n" }
            var temp5= Array(sch_data.length()){ "n" }
            var temp6 = Array(sch_data.length()){ "n" }
            var temp:String?=sch_data.getJSONObject(0).getString("Name")
            for (i in 0..sch_data.length() - 1) {
                temp1[i] = sch_data.getJSONObject(i).getString("Name")
                temp2[i] = sch_data.getJSONObject(i).getString("Room")
                temp3?.set(i, sch_data.getJSONObject(i).getJSONObject("start").getString("time"))
                temp4?.set(i, sch_data.getJSONObject(i).getJSONObject("start").getString("day"))
                temp5?.set(i, sch_data.getJSONObject(i).getJSONObject("end").getString("time"))
                temp6?.set(i, sch_data.getJSONObject(i).getJSONObject("end").getString("day"))
            }


            var tempid = IntArray(bul_data.length())
            var tempname = Array(bul_data.length()){ "n" }
          //  val tempname = arrayOfNulls<String>(bul_data.length())
            var tempcode= Array(bul_data.length()){ "n" }
            var tempsearch= Array(bul_data.length()){ "n" }
            var templocations= Array(bul_data.length()){ LatLng(0.0,0.0) }
            for (i in 0..bul_data.length() - 1) {
                tempname?.set(i, bul_data.getJSONObject(i).getString("name"))
                tempid[i] = bul_data.getJSONObject(i).getInt("id")
                templocations?.set(i, LatLng(bul_data.getJSONObject(i).getJSONObject("location").getDouble("lat"),
                        bul_data.getJSONObject(i).getJSONObject("location").getDouble("lng")))
                tempcode?.set(i, bul_data.getJSONObject(i).getString("code"))
                tempsearch?.set(i, bul_data.getJSONObject(i).getString("searchText"))
            }

            val buildings = Buildings()
            buildings.id = tempid
            buildings.name = tempname
            buildings.code = tempcode
            buildings.locations = templocations
            buildings.searchText = tempsearch
            buildings.jsonArray = bul_data

            val sch = Schedule()
            sch.schLocations = locations
            sch.builNames = builNames
            sch.scharray = sch_data
            sch.bularray = bul_data
            sch.lat = latCord
            sch.lon = longCord
            sch.classNames = temp1
            sch.roomNos = temp2
            sch.startTime = temp3
            sch.startDay = temp4
            sch.endTime = temp5
            sch.endDay = temp6

            res.buildings = buildings
            res.schedule = sch
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return res
    }

    public override fun doInBackground(vararg ass: AssetManager): JsonResult {
        return getExtractedData(ass[0])
    }
}
