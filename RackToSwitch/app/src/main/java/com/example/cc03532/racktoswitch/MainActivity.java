package com.example.cc03532.racktoswitch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Spinner spRoom;
    Spinner spRack;
    Spinner spRackSlot;
    Spinner spRackPort;
    Spinner spSwitch;
    Spinner spSwitchPort;
    Button bSubmit;
    Button bRoomConnections;
    Button bRequests;
    TextView tvResponse;
    ArrayList alRackAndSwitchPort;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);

        //Initialize xml objects
        spRoom = (Spinner) findViewById(R.id.spRoom);
        spRack = (Spinner) findViewById(R.id.spRack);
        spRackSlot = (Spinner) findViewById(R.id.spRackSlot);
        spRackPort = (Spinner) findViewById(R.id.spRackPort);
        spSwitch = (Spinner) findViewById(R.id.spSwitch);
        spSwitchPort = (Spinner) findViewById(R.id.spSwitchPort);
        bSubmit = (Button) findViewById(R.id.bSubmit);
        bRoomConnections = (Button) findViewById(R.id.bRoomConnections);
        bRequests = (Button) findViewById(R.id.bRequests);
        tvResponse = (TextView) findViewById(R.id.tvResponse);

        //Create array of 48 for rack and switch spinners
        alRackAndSwitchPort = new ArrayList();
        for(int i = 1; i < 49; i++) {
            alRackAndSwitchPort.add(i);
        }

        //Create array adapters
        ArrayAdapter<CharSequence> adRoom = ArrayAdapter.createFromResource(this,
                R.array.arRoom, android.R.layout.simple_spinner_item);
        adRoom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adRack = ArrayAdapter.createFromResource(this,
                R.array.arRack, android.R.layout.simple_spinner_item);
        adRack.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adRackSlot = ArrayAdapter.createFromResource(this,
                R.array.arRackSlot, android.R.layout.simple_spinner_item);
        adRackSlot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<Integer> adRackPort = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, alRackAndSwitchPort);
        adRackPort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adSwitch = ArrayAdapter.createFromResource(this,
                R.array.arSwitch, android.R.layout.simple_spinner_item);
        adSwitch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<Integer> adSwitchPort = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, alRackAndSwitchPort);
        adSwitchPort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Set array adapters to spinners
        spRoom.setAdapter(adRoom);
        spRack.setAdapter(adRack);
        spRackSlot.setAdapter(adRackSlot);
        spRackPort.setAdapter(adRackPort);
        spSwitch.setAdapter(adSwitch);
        spSwitchPort.setAdapter(adSwitchPort);

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://2c7w9djdrc.execute-api.us-west-2.amazonaws.com/prod/connection";

                Map<String, String> hmPost = new HashMap<>();
                hmPost.put("Room", spRoom.getSelectedItem().toString());
                hmPost.put("RackPort", spRack.getSelectedItem().toString()+spRackSlot.getSelectedItem().toString()+spRackPort.getSelectedItem().toString());
                hmPost.put("SwitchPort", spSwitch.getSelectedItem().toString()+"/0/"+spSwitchPort.getSelectedItem().toString());


                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(hmPost), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.getString("Response").equals("0")) {
                                CharSequence text = "Connection Already Exists";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toastExists = Toast.makeText(getApplicationContext(), text, duration);
                                toastExists.show();
                            } else {
                                CharSequence text = "Connection Created";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toastCreated = Toast.makeText(getApplicationContext(), text, duration);
                                toastCreated.show();
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
        });

        bRoomConnections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindConnection.class);
                startActivity(intent);
            }
        });

        bRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Requests.class);
                startActivity(intent);
            }
        });
    }
}
