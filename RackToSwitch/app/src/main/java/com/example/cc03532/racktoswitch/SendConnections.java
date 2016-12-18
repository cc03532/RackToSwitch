package com.example.cc03532.racktoswitch;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SendConnections extends AppCompatActivity {

    EditText etEmail;
    Button bSendEmail;
    String vRoom;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_connections);

        queue = Volley.newRequestQueue(this);
        vRoom =  getIntent().getStringExtra("Connections");

        etEmail = (EditText) findViewById(R.id.etEmail);
        bSendEmail = (Button) findViewById(R.id.bSendEmail);

        bSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://2c7w9djdrc.execute-api.us-west-2.amazonaws.com/prod/room?Room="+vRoom;

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        String body = "";

                        try {
                            JSONArray tempArray = response.getJSONArray("Items");
                            if (tempArray.length() > 0) {
                                for (int i = 0; i < tempArray.length(); i++) {

                                    body += tempArray.getJSONObject(i).getString("Room")+tempArray.getJSONObject(i).getString("RackPort")+" : "+tempArray.getJSONObject(i).getString("SwitchPort")+"\n";

                                }

                                sendEmail(etEmail.getText().toString(), vRoom+" Connections", body);
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

    protected void sendEmail(String to,String subject, String body) {

        String[] TO = {to};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SendConnections.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
