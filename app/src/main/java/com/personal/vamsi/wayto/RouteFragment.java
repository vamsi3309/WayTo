package com.personal.vamsi.wayto;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.TimeZone;


public class RouteFragment extends Fragment {
    JsonExtracter2.Buildings buildings;
    public RouteFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AssetManager assetManager = getResources().getAssets();
         buildings = new JsonExtracter2().doInBackground(assetManager);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String[] modes = {"driving","walking","transit","bicycling"};
        View view = inflater.inflate(R.layout.route_fragment, container, false);
        final AutoCompleteTextView fromView = (AutoCompleteTextView) view.findViewById(R.id.From);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,
                buildings.searchText);
         ArrayAdapter<String> modearray = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, modes);

        fromView.setAdapter(adapter);

        final AutoCompleteTextView toView = (AutoCompleteTextView) view.findViewById(R.id.To);
        toView.setAdapter(adapter);

        final AutoCompleteTextView mode = (AutoCompleteTextView) view.findViewById(R.id.mode);
        mode.setAdapter(modearray);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        EditText searchTime = (EditText)view.findViewById(R.id.time);
        searchTime.setInputType(InputType.TYPE_NULL);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

            }
        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE), true);
        Button directions = view.findViewById(R.id.button2);
        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] fromBuil = buildings.searchText;
                Intent intent = new Intent(getActivity(), MapsActivityRoute.class);

                for(int i=0;i<fromBuil.length;i++)
                {
                    if(fromBuil[i].equals(fromView.getText().toString())){
                        intent.putExtra("name",buildings.name[i]);
                        intent.putExtra("fromlat",buildings.locations[i].latitude);
                        intent.putExtra("fromlong",buildings.locations[i].longitude);
                        intent.putExtra("mode",mode.getText().toString());
                    }
                }
                 for(int i=0;i<fromBuil.length;i++)
                {
                    if(fromBuil[i].equals(toView.getText().toString())){
                        intent.putExtra("toname",buildings.name[i]);
                        intent.putExtra("tolat",buildings.locations[i].latitude);
                        intent.putExtra("tolong",buildings.locations[i].longitude);
                    }
                }

                startActivity(intent);
            }
        });
        return view;
    }




}
