package com.personal.vamsi.wayto


import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

import com.google.android.gms.maps.model.LatLng

import java.lang.reflect.Array
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Arrays
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class ScheduleFragment : Fragment() {


     var jsonResult: JsonExtracter.JsonResult?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val assetManager = resources.assets
        val jsonExtracter = JsonExtracter()
        jsonResult = jsonExtracter.doInBackground(assetManager)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View? = null
        try {
            view = inflater!!.inflate(R.layout.schedule_fragment, container, false)
            var epochstartTime = LongArray(jsonResult?.schedule?.startDay?.size!!)
            var epochendTime = LongArray(jsonResult?.schedule?.startDay?.size!!)
            for (i in 0..(jsonResult?.schedule?.startDay?.size?.minus(1) ?: 0)) {
                val scal = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))
                val ecal = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))
                val stime = jsonResult?.schedule?.startTime?.get(i).toString()
                //Log.v("start time ",stime)
                val etime = jsonResult?.schedule?.endTime?.get(i)
                if (stime?.endsWith("am")!!)
                    scal.set(Calendar.AM_PM, 0)
                else
                    scal.set(Calendar.AM_PM, 1)
                scal.set(Calendar.HOUR, Integer.parseInt(stime.substring(0, stime.indexOf(":"))))
                scal.set(Calendar.MINUTE, Integer.parseInt(stime.substring(stime.indexOf(":") + 1, stime.indexOf(":") + 2)))

                if (etime!!.endsWith("am"))
                    ecal.set(Calendar.AM_PM, 0)
                else
                    ecal.set(Calendar.AM_PM, 1)
                ecal.set(Calendar.HOUR, Integer.parseInt(etime.substring(0, etime.indexOf(":"))))
                ecal.set(Calendar.MINUTE, Integer.parseInt(etime.substring(etime.indexOf(":") + 1, etime.indexOf(":") + 2)))
                epochstartTime!![i] = scal.timeInMillis / 1000L
                epochendTime!![i] = ecal.timeInMillis / 1000L
                Log.d("start time:   ", "" + scal.timeInMillis / 1000L)
                Log.d("end time:   ", "" + ecal.timeInMillis / 1000L)
            }

            val newPage = view!!.findViewById<View>(R.id.mon) as Button
            newPage.setOnClickListener {
                val intent = Intent(activity, MapsActivity::class.java)
                val names = ArrayList<String>()
                val classnames = ArrayList<String>()
                val lat = ArrayList<Double>()
                val lon = ArrayList<Double>()
                val btime = ArrayList<Long>()
                val entime = ArrayList<Long>()
                for (i in 0..jsonResult!!.schedule!!.startDay!!.size-1) {
                    if (jsonResult!!.schedule!!.startDay!![i].equals("Mon")) {
                        names.add(jsonResult!!.schedule!!.builNames!![i].toString())
                        classnames.add(jsonResult!!.schedule!!.classNames!![i].toString())
                        lat.add(jsonResult!!.schedule!!.lat!![i])
                        lon.add(jsonResult!!.schedule!!.lon!![i])
                        btime.add(epochstartTime!![i])
                        entime.add(epochendTime!![i])
                    }
                }
                val name = arrayOfNulls<String>(names.size)
                val latit = DoubleArray(name.size)
                val longit = DoubleArray(name.size)
                val stime = LongArray(name.size)
                val etime = LongArray(name.size)
                val cnames = arrayOfNulls<String>(names.size)
                for (i in name.indices) {
                    name[i] = names[i]
                    latit[i] = lat[i]
                    longit[i] = lon[i]
                    stime[i] = btime[i]
                    etime[i] = entime[i]
                    cnames[i]= classnames[i]
                }
                if (names.size == 0){
                    Toast.makeText(context,"No classes for today",Toast.LENGTH_LONG).show()
                }
                else {
                    intent.putExtra("names", name)
                    intent.putExtra("lat", latit)
                    intent.putExtra("lon", longit)
                    Log.v("index", "" + stime.size + "")
                    intent.putExtra("epochstime", stime)
                    intent.putExtra("epochetime", etime)
                    intent.putExtra("class_name",cnames)
                    startActivity(intent)
                }
            }


            val tue = view.findViewById<View>(R.id.tue) as Button
            tue.setOnClickListener {
                val intent = Intent(activity, MapsActivity::class.java)
                val names = ArrayList<String>()
                val classnames = ArrayList<String>()
                val lat = ArrayList<Double>()
                val lon = ArrayList<Double>()
                val btime = ArrayList<Long>()
                val entime = ArrayList<Long>()
                for (i in 0..jsonResult!!.schedule!!.startDay!!.size-1) {
                    if (jsonResult!!.schedule!!.startDay!![i].equals("Tue")) {
                        names.add(jsonResult!!.schedule!!.builNames!![i].toString())
                        classnames.add(jsonResult!!.schedule!!.classNames!![i].toString())
                        lat.add(jsonResult!!.schedule!!.lat!![i])
                        lon.add(jsonResult!!.schedule!!.lon!![i])
                        btime.add(epochstartTime!![i])
                        entime.add(epochendTime!![i])
                    }
                }
                val name = arrayOfNulls<String>(names.size)
                val latit = DoubleArray(name.size)
                val longit = DoubleArray(name.size)
                val stime = LongArray(name.size)
                val etime = LongArray(name.size)
                val cnames = arrayOfNulls<String>(names.size)
                for (i in name.indices) {
                    name[i] = names[i]
                    latit[i] = lat[i]
                    longit[i] = lon[i]
                    stime[i] = btime[i]
                    etime[i] = entime[i]
                    cnames[i]= classnames[i]
                }
                if (names.size == 0){
                    Toast.makeText(context,"No classes for today",Toast.LENGTH_LONG).show()
                }
                else {
                    intent.putExtra("names", name)
                    intent.putExtra("lat", latit)
                    intent.putExtra("lon", longit)
                    Log.v("index", "" + stime.size + "")
                    intent.putExtra("epochstime", stime)
                    intent.putExtra("epochetime", etime)
                    intent.putExtra("class_name",cnames)
                    startActivity(intent)
                }
            }

            val wed = view.findViewById<View>(R.id.wed) as Button
            wed.setOnClickListener {
                val intent = Intent(activity, MapsActivity::class.java)
                val names = ArrayList<String>()
                val classnames = ArrayList<String>()
                val lat = ArrayList<Double>()
                val lon = ArrayList<Double>()
                val btime = ArrayList<Long>()
                val entime = ArrayList<Long>()
                for (i in 0..jsonResult!!.schedule!!.startDay!!.size-1) {
                    if (jsonResult!!.schedule!!.startDay!![i].equals("Wed")) {
                        names.add(jsonResult!!.schedule!!.builNames!![i].toString())
                        classnames.add(jsonResult!!.schedule!!.classNames!![i].toString())
                        lat.add(jsonResult!!.schedule!!.lat!![i])
                        lon.add(jsonResult!!.schedule!!.lon!![i])
                        btime.add(epochstartTime!![i])
                        entime.add(epochendTime!![i])
                    }
                }
                val name = arrayOfNulls<String>(names.size)
                val latit = DoubleArray(name.size)
                val longit = DoubleArray(name.size)
                val stime = LongArray(name.size)
                val etime = LongArray(name.size)
                val cnames = arrayOfNulls<String>(names.size)
                for (i in name.indices) {
                    name[i] = names[i]
                    latit[i] = lat[i]
                    longit[i] = lon[i]
                    stime[i] = btime[i]
                    etime[i] = entime[i]
                    cnames[i]= classnames[i]
                }
                if (names.size == 0){
                    Toast.makeText(context,"No classes for today",Toast.LENGTH_LONG).show()
                }
                else {
                    intent.putExtra("names", name)
                    intent.putExtra("lat", latit)
                    intent.putExtra("lon", longit)
                    Log.v("index", "" + stime.size + "")
                    intent.putExtra("epochstime", stime)
                    intent.putExtra("epochetime", etime)
                    intent.putExtra("class_name",cnames)
                    startActivity(intent)
                }
            }

            val thr = view.findViewById<View>(R.id.thr) as Button
            thr.setOnClickListener {
                val intent = Intent(activity, MapsActivity::class.java)
                val names = ArrayList<String>()
                val classnames = ArrayList<String>()
                val lat = ArrayList<Double>()
                val lon = ArrayList<Double>()
                val btime = ArrayList<Long>()
                val entime = ArrayList<Long>()
                for (i in 0..jsonResult!!.schedule!!.startDay!!.size-1) {
                    if (jsonResult!!.schedule!!.startDay!![i].equals("Thr")) {
                        names.add(jsonResult!!.schedule!!.builNames!![i].toString())
                        classnames.add(jsonResult!!.schedule!!.classNames!![i].toString())
                        lat.add(jsonResult!!.schedule!!.lat!![i])
                        lon.add(jsonResult!!.schedule!!.lon!![i])
                        btime.add(epochstartTime!![i])
                        entime.add(epochendTime!![i])
                    }
                }
                val name = arrayOfNulls<String>(names.size)
                val latit = DoubleArray(name.size)
                val longit = DoubleArray(name.size)
                val stime = LongArray(name.size)
                val etime = LongArray(name.size)
                val cnames = arrayOfNulls<String>(names.size)
                for (i in name.indices) {
                    name[i] = names[i]
                    latit[i] = lat[i]
                    longit[i] = lon[i]
                    stime[i] = btime[i]
                    etime[i] = entime[i]
                    cnames[i]= classnames[i]
                }
                if (names.size == 0){
                    Toast.makeText(context,"No classes for today",Toast.LENGTH_LONG).show()
                }
                else {
                    intent.putExtra("names", name)
                    intent.putExtra("lat", latit)
                    intent.putExtra("lon", longit)
                    Log.v("index", "" + stime.size + "")
                    intent.putExtra("epochstime", stime)
                    intent.putExtra("epochetime", etime)
                    intent.putExtra("class_name",cnames)
                    startActivity(intent)
                }
            }


            val fri = view.findViewById<View>(R.id.fri) as Button
            fri.setOnClickListener {

                val intent = Intent(activity, MapsActivity::class.java)
                val names = ArrayList<String>()
                val classnames = ArrayList<String>()
                val lat = ArrayList<Double>()
                val lon = ArrayList<Double>()
                val btime = ArrayList<Long>()
                val entime = ArrayList<Long>()
                for (i in 0..jsonResult!!.schedule!!.startDay!!.size-1) {
                    if (jsonResult!!.schedule!!.startDay!![i].equals("Fri")) {
                        names.add(jsonResult!!.schedule!!.builNames!![i].toString())
                        classnames.add(jsonResult!!.schedule!!.classNames!![i].toString())
                        lat.add(jsonResult!!.schedule!!.lat!![i])
                        lon.add(jsonResult!!.schedule!!.lon!![i])
                        btime.add(epochstartTime!![i])
                        entime.add(epochendTime!![i])
                    }
                }
                val name = arrayOfNulls<String>(names.size)
                val latit = DoubleArray(name.size)
                val longit = DoubleArray(name.size)
                val stime = LongArray(name.size)
                val etime = LongArray(name.size)
                val cnames = arrayOfNulls<String>(names.size)
                for (i in name.indices) {
                    name[i] = names[i]
                    latit[i] = lat[i]
                    longit[i] = lon[i]
                    stime[i] = btime[i]
                    etime[i] = entime[i]
                    cnames[i]= classnames[i]
                }
                if (names.size == 0){
                    Toast.makeText(context,"No classes for today",Toast.LENGTH_LONG).show()
                }
                else {
                    intent.putExtra("names", name)
                    intent.putExtra("lat", latit)
                    intent.putExtra("lon", longit)
                    Log.v("index", "" + stime.size + "")
                    intent.putExtra("epochstime", stime)
                    intent.putExtra("epochetime", etime)
                    intent.putExtra("class_name",cnames)
                    startActivity(intent)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return view
    }


}// Required empty public constructor

