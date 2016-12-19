package com.example.cc03532.racktoswitchrequest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
    Spinner spType;
    Button bSubmit;
    ArrayList alRackPort;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);

        spRoom = (Spinner) findViewById(R.id.spRoom);
        spRack = (Spinner) findViewById(R.id.spRack);
        spRackSlot = (Spinner) findViewById(R.id.spRackSlot);
        spRackPort = (Spinner) findViewById(R.id.spRackPort);
        spType = (Spinner) findViewById(R.id.spType);
        bSubmit = (Button) findViewById(R.id.bSubmit);

        //Create array of 48 for rack spinner
        alRackPort = new ArrayList();
        for(int i = 1; i < 49; i++) {
            alRackPort.add(i);
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
                android.R.layout.simple_spinner_item, alRackPort);
        adRackPort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adType = ArrayAdapter.createFromResource(this,
                R.array.arType, android.R.layout.simple_spinner_item);
        adType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spRoom.setAdapter(adRoom);
        spRack.setAdapter(adRack);
        spRackSlot.setAdapter(adRackSlot);
        spRackPort.setAdapter(adRackPort);
        spType.setAdapter(adType);

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://2c7w9djdrc.execute-api.us-west-2.amazonaws.com/prod/connection/request";

                Map<String, String> hmPost = new HashMap<>();
                hmPost.put("Room", spRoom.getSelectedItem().toString());
                hmPost.put("RackPort", spRack.getSelectedItem().toString()+spRackSlot.getSelectedItem().toString()+spRackPort.getSelectedItem().toString());
                hmPost.put("SwitchPortType", spType.getSelectedItem().toString());

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
                                CharSequence text = "Connection Request Created";
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
    }
}
