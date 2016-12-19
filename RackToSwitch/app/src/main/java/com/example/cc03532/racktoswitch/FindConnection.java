package com.example.cc03532.racktoswitch;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
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

public class FindConnection extends AppCompatActivity {

    Spinner spRoom;
    ListView lvConnections;
    Button bSendConnections;
    ArrayList alConnections;
    ArrayAdapter<String> adConnections;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_connection);

        queue = Volley.newRequestQueue(this);

        final SwipeDetector swipeDetector = new SwipeDetector();

        spRoom = (Spinner) findViewById(R.id.spRoom);
        lvConnections = (ListView) findViewById(R.id.lvConnections);
        bSendConnections = (Button) findViewById(R.id.bSendConnections);

        ArrayAdapter<CharSequence> adRoom = ArrayAdapter.createFromResource(this,
                R.array.arRoom, android.R.layout.simple_spinner_item);
        adRoom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spRoom.setAdapter(adRoom);

        alConnections = new ArrayList();

        adConnections = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, alConnections);
        adConnections.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        lvConnections.setAdapter(adConnections);
        lvConnections.setOnTouchListener(swipeDetector);

        spRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                adConnections.clear();
                adConnections.notifyDataSetChanged();

                String url = "https://2c7w9djdrc.execute-api.us-west-2.amazonaws.com/prod/room?Room="+spRoom.getSelectedItem().toString();

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray tempArray = response.getJSONArray("Items");
                            if (tempArray.length() > 0) {
                                for (int i = 0; i < tempArray.length(); i++) {

                                    adConnections.add(tempArray.getJSONObject(i).getString("Room")+tempArray.getJSONObject(i).getString("RackPort")+" : "+tempArray.getJSONObject(i).getString("SwitchPort"));

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

        bSendConnections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SendConnections.class);
                intent.putExtra("Connections", spRoom.getSelectedItem().toString());
                startActivity(intent);
            }
        });

        lvConnections.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (swipeDetector.swipeDetected()) {
                    if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
                        String vRoom = adConnections.getItem(position);
                        String url = "https://2c7w9djdrc.execute-api.us-west-2.amazonaws.com/prod/connection?Room="+vRoom.substring(0,vRoom.indexOf("."))+"&RackPort="+vRoom.substring(vRoom.indexOf("."),vRoom.indexOf(":"));
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                adConnections.remove(adConnections.getItem(position));
                                CharSequence text = "Connection Deleted";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                                toast.show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }){

                            /**
                             * Passing some request headers
                             */
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                return headers;
                            }

                        };

                        queue.add(request);
                    }
                }
            }
        });
    }
}
