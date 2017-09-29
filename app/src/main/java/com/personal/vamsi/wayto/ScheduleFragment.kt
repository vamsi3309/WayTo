package com.personal.vamsi.wayto


import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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


    class CardSchedule{
        var names = ArrayList<String>()
        var classnames = ArrayList<String>()
        var lat = ArrayList<Double>()
        var lon = ArrayList<Double>()
        var btime = ArrayList<Long>()
        var entime = ArrayList<Long>()
        var details = StringBuilder()

        fun showOnCard(jsonResult: JsonExtracter.JsonResult,day: String,
                       epochstartTime: LongArray,epochendTime: LongArray): String{
            jsonResult!!.schedule!!.startDay!!.forEachIndexed { index, value ->
                if (value.equals(day))
                {
                    this.details.append("Class Name: "+jsonResult!!.schedule!!.classNames!![index]+
                            "\n\tStart time: "+jsonResult!!.schedule!!.startTime!![index]+
                            " || End Time: "+jsonResult!!.schedule!!.endTime!![index]+"\n")
                    this.names.add(jsonResult!!.schedule!!.builNames!![index].toString())
                    this.classnames.add(jsonResult!!.schedule!!.classNames!![index].toString())
                    this.lat.add(jsonResult!!.schedule!!.lat!![index])
                    this.lon.add(jsonResult!!.schedule!!.lon!![index])
                    this.btime.add(epochstartTime!![index])
                    this.entime.add(epochendTime!![index])
                }
            }
            if (this.names.size == 0)
            {
                return "No class for the day"
            }
            else return this.details.toString()
        }

        fun prepareIntent(intent: Intent): Intent{
            intent.putExtra("names", this.names.toArray(arrayOfNulls<String>(this.names.size)))
            intent.putExtra("lat", this.lat.toDoubleArray())
            intent.putExtra("lon", this.lon.toDoubleArray())
            //Log.v("index", "" + stime.size + "")
            intent.putExtra("epochstime", this.btime.toLongArray())
            intent.putExtra("epochetime", this.entime.toLongArray())
            intent.putExtra("class_name",this.classnames.toArray(arrayOfNulls<String>(this.names.size)))
            return intent
        }

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
            }


            val intent = Intent(activity, MapsActivity::class.java)
            intent.putExtra("action","schedule")


            val monday = CardSchedule()
            val mcv = view.findViewById<View>(R.id.mon_card)
            val mtv = view.findViewById<TextView>(R.id.mon_details)
            mtv.setText(monday.showOnCard(jsonResult!!,"Mon",epochstartTime,epochendTime))
            mcv.setOnClickListener {
                if (mtv.text.equals("No class for the day"))
                {
                    Toast.makeText(context,"No classes || No activity to show",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(context,"Opening map view",Toast.LENGTH_SHORT).show()
                    monday.prepareIntent(intent)
                    startActivity(intent)
                }
            }

            val tuesday = CardSchedule()
            val tcv = view.findViewById<View>(R.id.tue_card)
            val ttv = view.findViewById<TextView>(R.id.tue_details)
            ttv.setText(tuesday.showOnCard(jsonResult!!,"Tue",epochstartTime,epochendTime))
            tcv.setOnClickListener {
                if (ttv.text.equals("No class for the day"))
                {
                    Toast.makeText(context,"No classes || No activity to show",Toast.LENGTH_SHORT).show()
                }
                else{
                Toast.makeText(context,"Opening map view",Toast.LENGTH_SHORT).show()
                tuesday.prepareIntent(intent)
                startActivity(intent)
                }
            }

            val wednesday = CardSchedule()
            val wcv = view.findViewById<View>(R.id.wed_card)
            val wtv = view.findViewById<TextView>(R.id.wed_details)
            wtv.setText(wednesday.showOnCard(jsonResult!!,"Wed",epochstartTime,epochendTime))
            wcv.setOnClickListener {
                if (wtv.text.equals("No class for the day"))
                {
                    Toast.makeText(context,"No classes || No activity to show",Toast.LENGTH_SHORT).show()
                }
                else{
                Toast.makeText(context,"Opening map view",Toast.LENGTH_SHORT).show()
                wednesday.prepareIntent(intent)
                startActivity(intent)
                }
            }


            val thursday = CardSchedule()
            val trcv = view.findViewById<View>(R.id.thr_card)
            val trtv = view.findViewById<TextView>(R.id.thr_details)
            trtv.setText(thursday.showOnCard(jsonResult!!,"Thr",epochstartTime,epochendTime))
            trcv.setOnClickListener {
                if (trtv.text.equals("No class for the day"))
                {
                    Toast.makeText(context,"No classes || No activity to show",Toast.LENGTH_SHORT).show()
                }
                else{
                Toast.makeText(context,"Opening map view",Toast.LENGTH_SHORT).show()
                thursday.prepareIntent(intent)
                startActivity(intent)
                }
            }


            val friday = CardSchedule()
            val fcv = view.findViewById<View>(R.id.fri_card)
            val ftv = view.findViewById<TextView>(R.id.fri_details)
            ftv.setText(friday.showOnCard(jsonResult!!,"Fri",epochstartTime,epochendTime))
            fcv.setOnClickListener {
                if (ftv.text.equals("No class for the day"))
                {
                    Toast.makeText(context,"No classes || No activity to show",Toast.LENGTH_SHORT).show()
                }
                else{
                Toast.makeText(context,"Opening map view",Toast.LENGTH_SHORT).show()
                friday.prepareIntent(intent)
                startActivity(intent)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return view
    }


}

