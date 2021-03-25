package com.greenmars.distribuidor;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.greenmars.distribuidor.database.DatabaseHelper;
import com.greenmars.distribuidor.model.Account;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ActualizarPosicionActivity extends AppCompatActivity {

    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseHelper db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actulizar_pocicion);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        this.db = new DatabaseHelper(getApplicationContext());
        getLocationPermission();
        getDeviceLocation();
    }

    // MI UBICACION
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getDeviceLocation() {

        final Account cuenta = db.getAcountToken();

        if (mLocationPermissionGranted) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                /* here to request the missing permissions, and then overriding
                   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                          int[] grantResults)
                 to handle the case where the user grants the permission. See the documentation
                 for ActivityCompat#requestPermissions for more details. */
                return;
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        JSONObject data = new JSONObject();
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        String url = Variable.HOST + "/staff/coordinates/";
                        if (location != null) {
                            try {
                                data.put("latitude", location.getLatitude());
                                data.put("longitude", location.getLongitude());
                                data.put("company_id", cuenta.getCompany_id());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data,
                                    response -> {
                                        try {
                                            int status = response.getInt("status");
                                            if (status == 200) {
                                                String message = response.getString("message");
                                                if (db.actualizarPosicion(cuenta.getCompany_id(), location.getLatitude(), location.getLongitude())) {
                                                    Log.i("Business", "Actualizado posicion sqllite");
                                                } else {
                                                    Log.i("Business", "No se actualizo la pocicion sqllite");
                                                }
                                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }, error -> {
                                Log.d("Volley get", "error voley" + error.toString());
                                NetworkResponse response = error.networkResponse;
                                if (error instanceof ServerError && response != null) {
                                    try {
                                        String res = new String(response.data,
                                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                        // Now you can use any deserializer to make sense of data
                                        JSONObject obj = new JSONObject(res);
                                        Log.d("Voley post", obj.toString());
                                        String msj = obj.getString("message");
                                        Toast.makeText(getApplicationContext(), msj, Toast.LENGTH_SHORT).show();

                                    } catch (UnsupportedEncodingException e1) {
                                        e1.printStackTrace();
                                    } catch (JSONException e2) {
                                        e2.printStackTrace();
                                    }
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() {
                                    Map<String, String> headers = new HashMap<>();
                                    String token = db.getToken();
                                    Log.d("Voley get", token);
                                    headers.put("Authorization", "JWT " + token);
                                    headers.put("Content-Type", "application/json");
                                    return headers;
                                }
                            };
                            requestQueue.add(jsonObjectRequest);
                        }
                    });
        }
    }
}
