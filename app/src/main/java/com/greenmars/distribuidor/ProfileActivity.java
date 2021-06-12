package com.greenmars.distribuidor;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.greenmars.distribuidor.database.DatabaseHelper;
import com.greenmars.distribuidor.model.Account;
import com.greenmars.distribuidor.util.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    //---
    private DatabaseHelper db;
    //---
    TextInputEditText et_company_name, et_company_phone, et_company_address, et_company_lat,
            et_company_lng, et_company_ruc, et_url_facturacion;
    TextInputEditText et_dni, et_correo, et_celular, et_direccion, et_company_id, et_nombre;
    TextView tv_profil_title;
    CardView card_company;
    private Account cuenta = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //---
        this.db = new DatabaseHelper(getBaseContext());
        cuenta = db.getAcountToken();
        Log.d(Variable.TAG, "onCreate: " + cuenta.getToken());
        //--
        card_company = findViewById(R.id.card_empresa);
        Button btnEditar = findViewById(R.id.btnEditar);
        //--
        et_company_name = findViewById(R.id.ti_company_name);
        et_company_phone = findViewById(R.id.ti_company_phone);
        et_company_address = findViewById(R.id.ti_company_address);
        et_company_lat = findViewById(R.id.ti_company_latitude);
        et_company_lng = findViewById(R.id.ti_company_longitude);
        et_company_ruc = findViewById(R.id.ti_company_ruc);
        et_url_facturacion = findViewById(R.id.ti_url_facturacion);
        //--
        tv_profil_title = findViewById(R.id.tv_perfil);
        et_dni = findViewById(R.id.ti_dni);
        et_correo = findViewById(R.id.ti_correo);
        et_celular = findViewById(R.id.ti_celular);
        et_direccion = findViewById(R.id.ti_direccion);
        et_company_id = findViewById(R.id.ti_company_id);
        et_nombre = findViewById(R.id.ti_nombre);
        //---
        getProfile();
        isActive();

        if (cuenta.getType() == 1) {
            btnEditar.setOnClickListener(view -> editarPerfil());
        } else {
            btnEditar.setVisibility(View.GONE);
        }

    }

    private void getProfile() {

        if (cuenta.getType() == 1) {
            card_company.setVisibility(View.VISIBLE);
            //----
            et_company_name.setText(cuenta.getCompany_name());
            et_company_phone.setText(cuenta.getCompany_phone());
            et_company_address.setText(cuenta.getCompany_address());
            et_company_lat.setText(cuenta.getCompany_latitude());
            et_company_lng.setText(cuenta.getCompany_longitude());
            et_company_ruc.setText(cuenta.getCompany_ruc());
            //----
            et_dni.setText(cuenta.getDni());
            et_correo.setText(cuenta.getEmail());
            et_celular.setText(cuenta.getTelefono());
            et_direccion.setText(cuenta.getDireccion());
            et_company_id.setText(cuenta.getCompany_id());
            et_nombre.setText(cuenta.getNombre());
            et_url_facturacion.setText(cuenta.getUrl_facturacion());
        } else {
            card_company.setVisibility(View.GONE);
            //----
            et_dni.setText(cuenta.getDni());
            et_correo.setText(cuenta.getEmail());
            et_celular.setText(cuenta.getTelefono());
            et_direccion.setText(cuenta.getDireccion());
            et_company_id.setText(cuenta.getCompany_id());
            et_nombre.setText(cuenta.getNombre());
        }
    }

    private void isActive() {
        et_company_ruc.setEnabled(false);
        et_company_address.setEnabled(false);
        et_company_lat.setEnabled(false);
        et_company_lng.setEnabled(false);

        et_dni.setEnabled(false);
        et_correo.setEnabled(false);
        et_direccion.setEnabled(false);
        et_company_id.setEnabled(false);
    }

    private void editarPerfil() {
        JSONObject object = new JSONObject();
        try {
            object.put("phone_company", et_company_phone.getText().toString());
            object.put("name_company", et_company_name.getText().toString());
            object.put("url_facturacion", et_url_facturacion.getText().toString());
            object.put("name_staff", et_nombre.getText().toString());
            object.put("phone_staff", et_celular.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = Variable.HOST + "/company/owner/perfil/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, object,
                response -> {
                    try {
                        int status = response.getInt("status");
                        if (status == 200) {
                            cuenta.setCompany_phone(et_company_phone.getText().toString());
                            cuenta.setCompany_name(et_company_name.getText().toString());
                            cuenta.setUrl_facturacion(et_url_facturacion.getText().toString());
                            cuenta.setTelefono(et_celular.getText().toString());
                            cuenta.setNombre(et_celular.getText().toString());
                            if (this.db.updateData(cuenta)) {
                                Toast.makeText(getApplicationContext(), "Se actualizo correctamente tu perfil", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> {
            Log.d(Variable.TAG, "editarPerfil: " + error.toString());
            NetworkResponse response = error.networkResponse;
            if (error instanceof ServerError && error != null) {
                String res;
                try {
                    res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    Log.d(Variable.TAG, "error: " + res);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                Log.d(Variable.TAG, "getHeaders: " + cuenta.getToken());
                headers.put("Authorization", "JWT " + cuenta.getToken());
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
