package com.greenmars.distribuidor;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.greenmars.distribuidor.database.DatabaseHelper;
import com.greenmars.distribuidor.model.Account;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {

    public static Socket SOCKET;
    private DatabaseHelper db;

    public static final String ACTION = "com.mayorista.appproveedorgas.mapsfragment";
    public static final String ACTION2 = "com.greenmars.distribuidor.pedidoactivity";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (db == null) this.db = new DatabaseHelper(context);
        if (SOCKET == null) initSocketIO();

        if (intent != null) {
            if (intent.getStringExtra("id_pedido") != null) {
                int id_pedido = Integer.parseInt(intent.getStringExtra("id_pedido"));

                JSONObject data = new JSONObject();
                try {
                    data.put("order_id", id_pedido);
                    Log.d("Confirm Pedido", "Broatcast Receiver" + data.toString());
                    SOCKET.emit("confirm order provider", data);

                    NotificationManager notificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();

                    SOCKET.on("confirm order provider", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            if (this == null) {
                                return;
                            }
                            JSONObject jsonObject = (JSONObject) args[0];
                            try {
                                int sts = jsonObject.getInt("status");
                                confirm_order(context, sts);
                                Log.d(Variable.TAG, "Confirm Order: " + jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void initSocketIO() {
        final JSONObject json_connect = new JSONObject();
        IO.Options opts = new IO.Options();
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
            Log.d(Variable.TAG, "Node connect ok");
        } catch (URISyntaxException e) {
            Log.d(Variable.TAG, "Node connect error");
        }

        SOCKET.on(Socket.EVENT_CONNECT, args -> {
            conect();
            String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            Log.d(Variable.TAG, "SERVER connect " + date);
        });

        SOCKET.on(Socket.EVENT_DISCONNECT, args -> {
            String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            Log.d(Variable.TAG, "SERVER disconnect " + date);
        });

        SOCKET.on(Socket.EVENT_RECONNECT, args -> {
            String my_date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            Log.d(Variable.TAG, "SERVER reconnect " + my_date);
        });

        SOCKET.on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
            String my_date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            Log.d(Variable.TAG, "SERVER timeout " + my_date);
        });

        SOCKET.on(Socket.EVENT_RECONNECTING, args -> {
            String my_date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            Log.d(Variable.TAG, "SERVER reconnecting " + my_date);
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
                    Log.d(Variable.TAG, jsonObject.toString());
                    if (msg.equals("ok")) {
                        Log.d(Variable.TAG, "AUTH ok");
                    } else {
                        Log.e(Variable.TAG, "AUTH error");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //-----
    private void conect() {
        Log.d(Variable.TAG, "emitiendo new conect");
        JSONObject data = new JSONObject();
        Account cuenta = db.getAcountToken();
        try {
            data.put("ID", cuenta.getDni());
            data.put("type", "provider");
            data.put("company_id", cuenta.getCompany_id());
            Log.d(Variable.TAG, "conect " + data.toString());
            SOCKET.emit("new connect", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void confirm_order(Context context, int st) {
        //----
        Intent toastIntent = new Intent(ACTION2);
        toastIntent.putExtra("confirm_order", "si");
        toastIntent.putExtra("status", String.valueOf(st));
        context.sendBroadcast(toastIntent);
        Log.d(Variable.TAG, "confirm_order: " + st);
    }
}
