package com.greenmars.distribuidor;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.greenmars.distribuidor.database.DatabaseHelper;
import com.greenmars.distribuidor.model.Account;
import com.greenmars.distribuidor.model.DetailOrder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class PedidoActivity extends AppCompatActivity {
    //--
    ProgressBar progressBar;
    TextView tv_total;
    TextView textViewProducto;
    FloatingActionButton fab_accept;
    FloatingActionButton fab_denied;
    FloatingActionButton fab_notification;
    FloatingActionButton fab_delibered;
    //--
    TextView txtComprobante;
    TextView textTotal;
    TextView textSubtotal;

    //--
    RecyclerView recyclerDetailOrderList;
    TextView tv_espera;
    ProgressBar progressBarCon;
    //----
    private int id_pedido = 0;
    //--
    public static Socket SOCKET;
    private static final String TAG = "Confirm Pedido";
    //----
    private DatabaseHelper db;
    //--
    //TextInputEditText et_tiempo;
    //----- table -----
    private final ArrayList<JSONObject> productos = new ArrayList<>();
    //-------------
    BroadcastReceiver updateUIReciver;

    //-----
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        //---
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        //---
        this.db = new DatabaseHelper(getApplicationContext());
        //--
        progressBar = findViewById(R.id.pb_detalle);
        tv_total = findViewById(R.id.lbl_subtotal_id);
        //--
        tv_espera = findViewById(R.id.tv_espera);
        progressBarCon = findViewById(R.id.pb_pedido);
        txtComprobante = findViewById(R.id.txtComprobante);
        textTotal = findViewById(R.id.textTotal);
        textSubtotal = findViewById(R.id.textSubtotal);

        ocultarProgressConf();
        //--
        //et_tiempo = findViewById(R.id.txt_tiempo);
        //--
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            id_pedido = Integer.parseInt(intent.getStringExtra("id_pedido"));
            double lat = Double.parseDouble(intent.getStringExtra("latitude"));
            double lon = Double.parseDouble(intent.getStringExtra("longitude"));
            String referencia = getStringAddress(lat, lon);
            TextView txtReferencia = findViewById(R.id.txtReferencia);
            txtReferencia.setText(referencia);
            postOrderDetail(id_pedido);
        }

        // Inicializa el socket
        InitSocketIO();

        //---
        recyclerDetailOrderList = findViewById(R.id.recyclerDetailOrder);
        recyclerDetailOrderList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //   recyclerDetailOrderList.setLayoutManager(new LinearLayoutManager(this));
        textViewProducto = findViewById(R.id.textViewProducto);
        fab_accept = findViewById(R.id.fab_accept);
        fab_accept.setOnClickListener(view -> confirmOrder());
        fab_denied = findViewById(R.id.fab_denied);
        fab_denied.setOnClickListener(view -> {
            finish();
        });
        //--
        fab_notification = findViewById(R.id.fab_notification);
        fab_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                bottomSheetDialog.show(getSupportFragmentManager(),"Dialog Inferior");

                 */
                sendNotification();
            }
        });

        fab_delibered = findViewById(R.id.fab_delibered);
        fab_delibered.setOnClickListener(view -> showDeliberedAlertDialog());

        disabledAllFab();
        disableNotificationBtn();
        //
        //--- response
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.greenmars.distribuidor.pedidoactivity");
        updateUIReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    if (intent.getStringExtra("confirm_order") != null) {
                        if (intent.getStringExtra("confirm_order").equals("si")) {
                            int st_co = Integer.parseInt(intent.getStringExtra("status"));
                            ocultarProgressConf();
                            if (st_co == 200) {
                                enableNotificationBtn();
                            }
                            if (st_co == 400) {
                                disabledAllFab();
                                // Toast.makeText(getApplicationContext(), "Ocurrio un error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if (intent.getStringExtra("cancel_order") != null) {
                        if (intent.getStringExtra("cancel_order").equals("si")) {
                            int st_ca = Integer.parseInt(intent.getStringExtra("status"));
                            if (st_ca == 200) {
                                runOnUiThread(() -> {
                                    disabledAllFab();
                                    Toast.makeText(getApplicationContext(), "Pedido Cancelado", Toast.LENGTH_SHORT).show();
                                });
                            }
                        }
                    }

                    if (intent.getStringExtra("delivered_order") != null) {
                        if (intent.getStringExtra("delivered_order").equals("si")) {
                            int st_de = Integer.parseInt(intent.getStringExtra("status"));
                            if (st_de == 200) {
                                runOnUiThread(() -> {
                                    Toast.makeText(getApplicationContext(), "Pedido completado", Toast.LENGTH_SHORT).show();
                                    finish();
                                });

                            } else {
                                runOnUiThread(() -> {
                                    Toast.makeText(getApplicationContext(), "Error delivered", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                            }
                        }
                    }
                }

            }
        };
        registerReceiver(updateUIReciver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateUIReciver != null)
            unregisterReceiver(updateUIReciver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //----
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        //-----
    }

    private void mostrarProgress() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);

    }

    private void ocultarProgress() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
    //---

    private void mostrarProgressConf() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBarCon.setVisibility(View.VISIBLE);
        tv_espera.setVisibility(View.VISIBLE);

    }

    private void ocultarProgressConf() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBarCon.setVisibility(View.INVISIBLE);
        tv_espera.setVisibility(View.INVISIBLE);
    }

    //--
    private void enableNotificationBtn() {
        fab_notification.show();
        fab_accept.hide();
        fab_denied.hide();
        fab_delibered.show();
    }

    private void disableNotificationBtn() {
        fab_notification.hide();
        fab_accept.show();
        fab_denied.show();
        fab_delibered.hide();
    }

    private void disabledAllFab() {
        fab_notification.hide();
        fab_accept.hide();
        fab_denied.hide();
        fab_delibered.hide();
    }

    /*private void enableDeliberedBtn() {
        fab_delibered.show();
    }*/

    //---

    private void showDeliberedAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PedidoActivity.this);
        //builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Pedido completado");
        builder.setMessage("¿El pedido esta completado?");
        builder.setPositiveButton("Si", (dialogInterface, i) -> sendDelibered());

        builder.setNegativeButton("No", (dialogInterface, i) -> {

        });

        builder.setCancelable(false);
        builder.show();

    }

    //-- calculate total
    private void calculateTotal() {
        double total = 0.0;
        try {
            for (JSONObject obj : productos) {
                double st = obj.getDouble("subtotal");
                total += Math.round(st * 100d) / 100d;
            }
            if (total <= 0) {
                tv_total.setText("");
                textTotal.setText("");
            } else {
                textTotal.setText(getString(R.string.pedido_total));
                tv_total.setText("S/." + total);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //-------
    private void postOrderDetail(int id) {
        mostrarProgress();
        productos.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        Account cuenta = db.getAcountToken();
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("order_id", id);
            object.put("staff_id", cuenta.getDni());
            //object.put("selected",select);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Variable.HOST + "/orderdetail/distributor/";
        final ArrayList<DetailOrder> ListDetailOrder = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                response -> {
                    try {

                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            JSONObject prod = new JSONObject();
                            System.out.println(obj);
                            String vaucher = obj.getJSONObject("order_id").getString("voucher");
                            switch (vaucher) {
                                case "boleta":
                                    txtComprobante.setText("Efectivo con Boleta \n");
                                    break;
                                case "factura":
                                    txtComprobante.setText("Efectivo con Factura \n");
                                    break;
                                case "tarjeta-factura":
                                    txtComprobante.setText("Pagara con Tarjeta (Factura) \n");
                                    break;
                                case "tarjeta-boleta":
                                    txtComprobante.setText("Pagara con Tarjeta (Boleta) \n");
                                    break;
                                case "yape-boleta":
                                    txtComprobante.setText("Yape (Boleta) \n");
                                    break;
                                case "yape-factura":
                                    txtComprobante.setText("Yape (Factura) \n");
                                    break;
                                case "tunki-factura":
                                    txtComprobante.setText("Tunki (Factura) \n");
                                    break;
                                case "tunki-boleta":
                                    txtComprobante.setText("Tunki (Boleta) \n");
                                    break;
                            }

                            prod.put("producto", obj.getJSONObject("product_id").getString("description"));
                            prod.put("cantidad", obj.getInt("quantity"));
                            prod.put("preciou", obj.getDouble("unit_price"));
                            prod.put("subtotal", (obj.getInt("quantity") * obj.getDouble("unit_price")));
                            prod.put("id", obj.getInt("id"));
                            productos.add(prod);
                            DetailOrder oDetailOrder = new DetailOrder();
                            oDetailOrder.setProducto(obj.getJSONObject("product_id").getString("description"));
                            oDetailOrder.setCantidad(obj.getInt("quantity"));
                            oDetailOrder.setPrecioU(obj.getDouble("unit_price"));
                            oDetailOrder.setSubTotal(obj.getInt("quantity") * obj.getDouble("unit_price"));
                            ListDetailOrder.add(oDetailOrder);
                        }

                        recyclerDetailOrderList.setAdapter(null);
                        DetailOrderAdapter adapterDetailOrder = new DetailOrderAdapter(ListDetailOrder, textViewProducto.getWidth());
                        recyclerDetailOrderList.setAdapter(adapterDetailOrder);
                        calculateTotal();
                        ocultarProgress();
                        if (data.length() > 0) {
                            String sta = data.getJSONObject(0).getJSONObject("order_id").getString("status");
                            if (sta.equals("wait")) {
                                disableNotificationBtn();
                            } else {
                                if (sta.equals("confirm")) {
                                    enableNotificationBtn();
                                } else {
                                    disabledAllFab();
                                }

                            }
                        } else {
                            disabledAllFab();
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
                    // Couldn't properly decode data to string
                    e1.printStackTrace();
                } catch (JSONException e2) {
                    // returned data is not JSONObject?
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

    void InitSocketIO() {
        final JSONObject json_connect = new JSONObject();
        IO.Options opts = new IO.Options();
        // opts.forceNew = true;
        opts.reconnection = true;
        opts.query = "auth_token=thisgo77";
        try {
            json_connect.put("ID", "US01");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            SOCKET = IO.socket(Variable.HOST_NODE, opts);
            SOCKET.connect();
            // SOCKET.io().reconnectionDelay(10000);
            Log.d(TAG, "Node connect ok");
        } catch (URISyntaxException e) {
            Log.d(TAG, "Node connect error");
        }

        SOCKET.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //SOCKET.emit("new connect", json_connect);
                conect();
                String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                Log.d(TAG, "SERVER connect " + date);
            }
        });

        SOCKET.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                Log.d(TAG, "SERVER disconnect " + date);
            }
        });

        SOCKET.on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String my_date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                Log.d(TAG, "SERVER reconnect " + my_date);
            }
        });

        SOCKET.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String my_date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                Log.d(TAG, "SERVER timeout " + my_date);
            }
        });

        SOCKET.on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String my_date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                Log.d(TAG, "SERVER reconnecting " + my_date);
            }
        });

        SOCKET.on("login", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (this == null) {
                    return;
                }
                JSONObject jsonObject = (JSONObject) args[0];
                try {
                    String msg = jsonObject.getString("message");
                    Log.d(TAG, jsonObject.toString());
                    if (msg.equals("ok")) {
                        Log.d(TAG, "AUTH ok");

                        // Snackbar.make(getWindow().getDecorView().findViewById(R.id.container), "Conexión Exitosa", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        /*
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Realizando pedido", Toast.LENGTH_SHORT).show();

                            }
                        });

                         */
                    } else {
                        Log.e(TAG, "AUTH error");
                        /*
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Verifique Conexión", Toast.LENGTH_SHORT).show();
                            }
                        });

                         */
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        SOCKET.on("confirm order provider", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (this == null) {
                    return;
                }
                JSONObject jsonObject = (JSONObject) args[0];
                try {

                    final int sts = jsonObject.getInt("status");
                    runOnUiThread(() -> {
                        ocultarProgressConf();
                        if (sts == 200) {
                            enableNotificationBtn();
                        }
                        if (sts == 400) {
                            disabledAllFab();
                            // Toast.makeText(getApplicationContext(), "Ocurrio un error", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.d(TAG, "Confirm Order: " + jsonObject.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        SOCKET.on("cancel order provider", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (this == null) {
                    return;
                }
                JSONObject jsonObject = (JSONObject) args[0];
                Log.d(TAG, "Cancel Order: " + jsonObject.toString());
                try {
                    int status = jsonObject.getInt("status");
                    if (status == 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                disabledAllFab();
                                Toast.makeText(getApplicationContext(), "Pedido Cancelado", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        SOCKET.on("delivered order provider", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (this == null) {
                    return;
                }
                JSONObject jsonObject = (JSONObject) args[0];
                Log.d(TAG, "delivered Order: " + jsonObject.toString());
                try {
                    int status = jsonObject.getInt("status");
                    if (status == 200) {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Pedido completado", Toast.LENGTH_SHORT).show();
                            finish();
                        });

                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Error delivered", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void conect() {
        Log.d(TAG, "emitiendo new conect");
        JSONObject data = new JSONObject();
        Account cuenta = db.getAcountToken();
        try {
            data.put("ID", cuenta.getDni());
            data.put("type", "provider");
            data.put("company_id", cuenta.getCompany_id());
            SOCKET.emit("new connect", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void confirmOrder() {
        JSONObject data = new JSONObject();
        try {
            data.put("order_id", id_pedido);
            Log.d("Confirm Pedido", data.toString());

            Intent NxtAct = new Intent(this, HomeActivity.class);
            NxtAct.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(NxtAct);
            SOCKET.emit("confirm order provider", data);
            // sendDataService("confirm order provider", data);
            runOnUiThread(this::mostrarProgressConf);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification() {
        JSONObject data = new JSONObject();
        try {
            data.put("order_id", id_pedido);
            SOCKET.emit("send alert", data);
            //sendDataService("send alert", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendDelibered() {
        JSONObject data = new JSONObject();
        try {
            data.put("order_id", id_pedido);
            SOCKET.emit("delivered order provider", data);
            //sendDataService("delivered order provider", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getStringAddress(Double lat, Double lng) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString().substring(0, strReturnedAddress.toString().lastIndexOf(","));
                Log.w(TAG, strReturnedAddress.toString());
            } else {
                Log.w(TAG, "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "Canont get Address!");
        }
        if (strAdd.contains(","))
            return strAdd.substring(0, strAdd.lastIndexOf(","));
        else
            return "";
    }
}
