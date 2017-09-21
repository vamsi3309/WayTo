package com.personal.vamsi.wayto

import android.content.res.AssetManager
import android.os.AsyncTask

import com.google.android.gms.maps.model.LatLng

import org.json.JSONArray
import org.json.JSONException

import java.io.IOException
import java.io.InputStream

class JsonExtracter2 : AsyncTask<AssetManager, Int, JsonExtracter2.Buildings>() {
    inner class Buildings {
        internal var id: IntArray? = null
        internal var locations: Array<LatLng>? = null
        internal var name: Array<String>? = null
        internal var code: Array<String>? = null
        internal var searchText: Array<String>? = null
        internal var jsonArray: JSONArray?=null
    }

    public override fun doInBackground(vararg ass: AssetManager): JsonExtracter2.Buildings {
        val assetManager = ass[0]
        val buildings = Buildings()
        try {
            val bl_read = assetManager.open("buildings.json")
            val bl_size = bl_read.available()
            val buf_bl = ByteArray(bl_size)
            bl_read.read(buf_bl)
            bl_read.close()
            val bul_read = String(buf_bl)
            val bul_data = JSONArray(bul_read)

            val tempid = IntArray(bul_data.length())
            val tempname = Array(bul_data.length()){ "n" }
            val tempcode = Array(bul_data.length()){ "n" }
            val tempsearch = Array(bul_data.length()){ "n" }
            val templocations= Array(bul_data.length()){ LatLng(0.0,0.0) }

            for (i in 0..bul_data.length() - 1) {
                tempname?.set(i, bul_data.getJSONObject(i).getString("name"))
                tempid[i] = bul_data.getJSONObject(i).getInt("id")
                templocations?.set(i, LatLng(bul_data.getJSONObject(i).getJSONObject("location").getDouble("lat"),
                        bul_data.getJSONObject(i).getJSONObject("location").getDouble("lng")))
                tempcode?.set(i, bul_data.getJSONObject(i).getString("code"))
                tempsearch?.set(i, bul_data.getJSONObject(i).getString("searchText"))
            }


            buildings.id = tempid
            buildings.name = tempname
            buildings.code = tempcode
            buildings.locations = templocations
            buildings.searchText = tempsearch
            buildings.jsonArray = bul_data


        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return buildings
    }
}
