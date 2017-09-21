package com.personal.vamsi.wayto

import android.app.TimePickerDialog
import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker

import java.util.Calendar
import java.util.TimeZone


class RouteFragment : Fragment() {
    internal var buildings: JsonExtracter2.Buildings?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val assetManager = resources.assets
        buildings = JsonExtracter2().doInBackground(assetManager)
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val modes = arrayOf("driving", "walking", "transit", "bicycling")
        val view = inflater!!.inflate(R.layout.route_fragment, container, false)
        val fromView = view.findViewById<View>(R.id.From) as AutoCompleteTextView
        val adapter: ArrayAdapter<String>? =
                ArrayAdapter(
                        activity,
                android.R.layout.simple_list_item_1,
                buildings?.searchText!!)
        val modearray = ArrayAdapter(activity, android.R.layout.simple_list_item_1, modes)

        fromView.setAdapter(adapter)

        val toView = view.findViewById<View>(R.id.To) as AutoCompleteTextView
        toView.setAdapter(adapter)

        val mode = view.findViewById<View>(R.id.mode) as AutoCompleteTextView
        mode.setAdapter(modearray)
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))
        val searchTime = view.findViewById<View>(R.id.time) as EditText
        searchTime.inputType = InputType.TYPE_NULL

        val timePickerDialog = TimePickerDialog(activity, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
        val directions = view.findViewById<Button>(R.id.button2)
        directions.setOnClickListener {
            val fromBuil = buildings!!.searchText
            val intent = Intent(activity, MapsActivityRoute::class.java)

            for (i in 0..fromBuil!!.size-1) {
                if (fromBuil!![i] == fromView.text.toString()) {
                    intent.putExtra("name", buildings!!.name!![i])
                    intent.putExtra("fromlat", buildings!!.locations!![i].latitude)
                    intent.putExtra("fromlong", buildings!!.locations!![i].longitude)
                    intent.putExtra("mode", mode.text.toString())
                }
            }
            for (i in 0..fromBuil.size-1) {
                if (fromBuil!![i] == toView.text.toString()) {
                    intent.putExtra("toname", buildings!!.name!![i])
                    intent.putExtra("tolat", buildings!!.locations!![i].latitude)
                    intent.putExtra("tolong", buildings!!.locations!![i].longitude)
                }
            }

            startActivity(intent)
        }
        return view
    }


}
