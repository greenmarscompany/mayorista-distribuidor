package com.greenmars.distribuidor;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.greenmars.distribuidor.database.DatabaseHelper;
import com.greenmars.distribuidor.location.TaskLoadedCallback;
import com.greenmars.distribuidor.model.Account;
import com.greenmars.distribuidor.ui.store.StoreActivity;
import com.greenmars.distribuidor.util.ViewPageAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements TaskLoadedCallback {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private boolean isNoti = false;
    private int noti_id;
    private double noti_lat, noti_lng;
    //--
    private DatabaseHelper db;
    Account cuenta;

    //---
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //----
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        //-----
        this.db = new DatabaseHelper(getApplicationContext());
        setContentView(R.layout.activity_home);

        //---
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getExtras() != null) {
                noti_lat = Double.parseDouble(intent.getStringExtra("lat"));
                noti_lng = Double.parseDouble(intent.getStringExtra("lng"));
                noti_id = Integer.parseInt(intent.getStringExtra("id"));
                isNoti = true;
            }
        }
        Log.d("ON CREATED", "CREANDO....");
        //-----
        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager());

        if (isNoti) {
            Log.d("Intent home", "recibido id " + noti_id);
            adapter.AddFragment(new MapsFragment(noti_id, noti_lat, noti_lng), "Map");
            isNoti = false;
        } else
            adapter.AddFragment(new MapsFragment(), "Map");

        adapter.AddFragment(new OrderFragment(), "Pedidos");
        cuenta = db.getAcountToken();
        if (cuenta.getType() == 1)
            adapter.AddFragment(new ProductFragment(), "Productos");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        if (getSupportActionBar() != null) {
            //getSupportActionBar().setTitle("Proveedor");
            getSupportActionBar().setElevation(0);
        }

        // Log.d(Variable.TAG, "token: " + FirebaseMessagingService.getToken(getApplicationContext()));

    }

    @Override
    protected void onResume() {
        super.onResume();

        TabLayout.Tab tab = tabLayout.getTabAt(0);
        assert tab != null;
        tab.select();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        Account cuenta = db.getAcountToken();
        //--
        MenuItem usuarioItem = menu.findItem(R.id.item_usuarios);
        MenuItem actionFacturacion = menu.findItem(R.id.action_facturacion);

        usuarioItem.setVisible(cuenta.getType() == 1);


        return true;
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_perfil:
                Toast.makeText(getApplicationContext(), "Perfil", Toast.LENGTH_SHORT).show();
                Intent myIntentPro = new Intent(getBaseContext(), ProfileActivity.class);
                startActivity(myIntentPro);
                return true;

            case R.id.item_usuarios:
                Intent intent = new Intent(getBaseContext(), CreateUserActivity.class);
                startActivity(intent);
                return true;

            case R.id.item_vaciar_caches:
                deleteCache(getBaseContext());
                Toast.makeText(getApplicationContext(), "Caché vaciadas correctamente", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.item_session:
                desloguearTokenDevice();
                db.clearToken();
                deleteCache(getBaseContext());
                finishAffinity();
                return true;

            case R.id.action_facturacion:
                Intent intent1 = new Intent(getBaseContext(), StoreActivity.class);
                startActivity(intent1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onTaskDone(Object... values) {
        /* if (currentPolyline != null)
            currentPolyline.remove(); */
        //Log.d("Route",values.toString());
        EnviarToMaps(values);
        //currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    private void EnviarToMaps(Object... values) {
        ViewPageAdapter viewPageAdapter = (ViewPageAdapter) viewPager.getAdapter();
        assert viewPageAdapter != null;
        MapsFragment maps = (MapsFragment) viewPageAdapter.getItem(0);
        // maps.DibujarRuta(values);
    }

    public void desloguearTokenDevice() {
        String url = Variable.HOST + "/staff/cerrar-sesion";
        String token = db.getToken();
        db.deleteLogin();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("mi_pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", "");
        editor.putLong("iduser", 0L);
        editor.apply();

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try {
                int status = response.getInt("status");
                if (status == 200) {
                    Log.d(Variable.TAG, "desloguearTokenDevice: se cerro sesion correctamente");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d(Variable.TAG, "desloguearTokenDevice: " + error.toString());
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null) {
                if (error instanceof ServerError) {
                    try {
                        String res = new String(
                                networkResponse.data,
                                HttpHeaderParser.parseCharset(networkResponse.headers, "utf-8")
                        );
                        Log.d(Variable.TAG, "desloguearTokenDevice: " + res);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "Opsss Timeout!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "JWT " + token);
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);

    }

    //-- Vaciar las caches
    public void deleteCache(Context context) {
        try {
            File file = context.getCacheDir();
            deleteDir(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteDir(File file) {
        if (file != null && file.isDirectory()) {
            String[] children = file.list();
            if (children == null) return false;
            for (String child : children) {
                boolean success = deleteDir(new File(file, child));
                if (!success) {
                    return false;
                }
            }
            return file.delete();
        } else if (file != null && file.isFile()) {
            return file.delete();
        } else {
            return false;
        }
    }

}
