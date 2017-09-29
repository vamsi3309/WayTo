package com.personal.vamsi.wayto

import android.app.TimePickerDialog
import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import java.util.Calendar
import java.util.TimeZone


class RouteFragment : Fragment() {
    internal var buildings: JsonExtracter.Buildings?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val assetManager = resources.assets
        buildings = JsonExtracter().doInBackground(assetManager).buildings
    }


    @Suppress("DEPRECATION")
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val modes = arrayOf("driving", "walking", "transit", "bicycling")
        val view = inflater!!.inflate(R.layout.route_fragment, container, false)
        val fromView = view.findViewById<View>(R.id.From) as AutoCompleteTextView
        var locationSuggestions = buildings?.searchText!!.plus("My location")

        // array adapter for from textview
        val fromadapter: ArrayAdapter<String>? =
                ArrayAdapter(
                        activity,
                android.R.layout.simple_list_item_1,
                locationSuggestions)
        // array adapter for to textview
        val toadapter: ArrayAdapter<String>? =
                ArrayAdapter(
                        activity,
                android.R.layout.simple_list_item_1,
                        buildings?.searchText!!)
        // array adapter for mode textView
        val modearray = ArrayAdapter(activity, android.R.layout.simple_list_item_1, modes)

        fromView.setText("From: My location")
        fromView.setAdapter(fromadapter)

        val toView = view.findViewById<View>(R.id.To) as AutoCompleteTextView
        toView.setAdapter(toadapter)

        val mode = view.findViewById<View>(R.id.mode) as AutoCompleteTextView
        mode.setAdapter(modearray)



        var calendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))
        val timeclick = view.findViewById<EditText>(R.id.timeView)
        timeclick.setText("${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}")

        val timePickerDialog = TimePickerDialog(activity, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            //val calendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            //Log.v("time in am and pm",""+calendar.timeInMillis)
            timeclick.setText("${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}")
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)

        timeclick.setOnClickListener {
            timePickerDialog.show()
        }

        val directions = view.findViewById<Button>(R.id.button2)
        directions.setOnClickListener {
            val fromBuil = buildings!!.searchText
            val intent = Intent(activity, MapsActivity::class.java)
            intent.putExtra("action","navigation")
            if (fromView.text.toString().equals("From: My location")||fromView.text.toString().equals("My location")){
                intent.putExtra("fromname","My location")
            }
            else if(fromBuil!!.contains(fromView.text.toString())){
                intent.putExtra("fromname", buildings!!.name!![fromBuil!!.indexOf(fromView.text.toString())])
                intent.putExtra("fromlat", buildings!!.locations!![fromBuil!!.indexOf(fromView.text.toString())].latitude)
                intent.putExtra("fromlong", buildings!!.locations!![fromBuil!!.indexOf(fromView.text.toString())].longitude)
            }
            else{
                Toast.makeText(context,"Invalid from location. Using CURRENT GPS LOCATION!!!",Toast.LENGTH_LONG).show()
                intent.putExtra("fromname","My location")
            }

            if(fromBuil!!.contains(toView.text.toString())) {
                intent.putExtra("toname", buildings!!.name!![fromBuil!!.indexOf(toView.text.toString())])
                intent.putExtra("tolat", buildings!!.locations!![fromBuil!!.indexOf(toView.text.toString())].latitude)
                intent.putExtra("tolong", buildings!!.locations!![fromBuil!!.indexOf(toView.text.toString())].longitude)

            }
            else{
                Toast.makeText(context,"Invalid 'To Location'. Choosing UGA LOCATION!!!",Toast.LENGTH_LONG).show()
                intent.putExtra("toname", "UGA")
                intent.putExtra("tolat", 33.9480053)
                intent.putExtra("tolong",-83.3773221)
            }
            if (modes.contains(mode.text.toString())){
                intent.putExtra("mode", mode.text.toString())
                intent.putExtra("hours",calendar.get(Calendar.HOUR_OF_DAY))
                intent.putExtra("mins",calendar.get(Calendar.MINUTE))
                //intent.putExtra("hours",calendar.get())
                startActivity(intent)
            }
            else{
                Toast.makeText(context,"Invalid 'Transport Mode'. Using driving as default!!!",Toast.LENGTH_LONG).show()
                intent.putExtra("mode","driving")
                intent.putExtra("hours",calendar.get(Calendar.HOUR_OF_DAY))
                intent.putExtra("mins",calendar.get(Calendar.MINUTE))
                startActivity(intent)
            }

        }
        return view
    }


}
