package com.example.cc03532.racktoswitch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Requests extends AppCompatActivity {

    Spinner spRoom;
    ListView lvRequests;
    ArrayList alRequests;
    ArrayAdapter<String> adRequests;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        queue = Volley.newRequestQueue(this);

        final SwipeDetector swipeDetector = new SwipeDetector();

        spRoom = (Spinner) findViewById(R.id.spRoom);
        lvRequests = (ListView) findViewById(R.id.lvRequests);

        ArrayAdapter<CharSequence> adRoom = ArrayAdapter.createFromResource(this,
                R.array.arRoom, android.R.layout.simple_spinner_item);
        adRoom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spRoom.setAdapter(adRoom);

        alRequests = new ArrayList();

        adRequests = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, alRequests);
        adRequests.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        lvRequests.setAdapter(adRequests);
        lvRequests.setOnTouchListener(swipeDetector);

        spRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                adRequests.clear();
                adRequests.notifyDataSetChanged();

                String url = "https://2c7w9djdrc.execute-api.us-west-2.amazonaws.com/prod/room/request?Room="+spRoom.getSelectedItem().toString();

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray tempArray = response.getJSONArray("Items");
                            if (tempArray.length() > 0) {
                                for (int i = 0; i < tempArray.length(); i++) {

                                    adRequests.add(tempArray.getJSONObject(i).getString("Room")+tempArray.getJSONObject(i).getString("RackPort")+" : "+tempArray.getJSONObject(i).getString("SwitchPortType"));

                                }
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                queue.add(request);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lvRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (swipeDetector.swipeDetected()) {
                    if (swipeDetector.getAction() == SwipeDetector.Action.LR) {

                        final Dialog dialog = new Dialog(Requests.this);
                        dialog.setContentView(R.layout.complete_request_dialog);
                        dialog.setTitle("Complete Request");

                        final TextView tvRackPort = (TextView) dialog.findViewById(R.id.tvRackPort);
                        final TextView tvType = (TextView) dialog.findViewById(R.id.tvType);
                        final Spinner spSwitch = (Spinner) dialog.findViewById(R.id.spSwitch);
                        final Spinner spSwitchPort = (Spinner) dialog.findViewById(R.id.spSwitchPort);
                        final Button bSubmit = (Button) dialog.findViewById(R.id.bSubmit);
                        final Button bCancel = (Button) dialog.findViewById(R.id.bCancel);

                        tvRackPort.setText(adRequests.getItem(position).substring(0,adRequests.getItem(position).indexOf(":")-1));
                        tvType.setText(adRequests.getItem(position).substring(adRequests.getItem(position).indexOf(":")+1));

                        ArrayList alRackAndSwitchPort = new ArrayList();
                        for(int i = 1; i < 49; i++) {
                            alRackAndSwitchPort.add(i);
                        }

                        ArrayAdapter<CharSequence> adSwitch = ArrayAdapter.createFromResource(Requests.this,
                                R.array.arSwitch, android.R.layout.simple_spinner_item);
                        adSwitch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        ArrayAdapter<Integer> adSwitchPort = new ArrayAdapter<>(Requests.this,
                                android.R.layout.simple_spinner_item, alRackAndSwitchPort);
                        adSwitchPort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        spSwitch.setAdapter(adSwitch);
                        spSwitchPort.setAdapter(adSwitchPort);

                        bCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        bSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                String url = "https://2c7w9djdrc.execute-api.us-west-2.amazonaws.com/prod/connection/request";
                                final Map<String, String> hmCompleteRequest = new HashMap<>();
                                hmCompleteRequest.put("Room", adRequests.getItem(position).substring(0,adRequests.getItem(position).indexOf(".")));
                                hmCompleteRequest.put("RackPort", adRequests.getItem(position).substring(adRequests.getItem(position).indexOf("."),adRequests.getItem(position).indexOf(":")-1));
                                hmCompleteRequest.put("SwitchPort", spSwitch.getSelectedItem().toString()+"/0/"+spSwitchPort.getSelectedItem().toString());

                                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(hmCompleteRequest), new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        adRequests.remove(adRequests.getItem(position));
                                        dialog.dismiss();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                                queue.add(request);
                            }
                        });

                        dialog.show();

                    }
                }
            }
        });
    }
}
