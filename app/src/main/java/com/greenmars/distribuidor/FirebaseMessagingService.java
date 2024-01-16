package com.greenmars.distribuidor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.RemoteMessage;
import com.greenmars.distribuidor.database.DatabaseHelper;
import com.greenmars.distribuidor.model.Mpedido_detalle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(Variable.TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", token).apply();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(Variable.TAG, "onMessageReceived: " + remoteMessage);
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        if (db.getToken() != null && !db.getToken().equals("") && !db.getToken().equals("-1")) {
            JSONObject jsonObject = new JSONObject(remoteMessage.getData());
            Log.d(Variable.TAG, "onMessageReceived: " + jsonObject);
            try {
                String type = jsonObject.getString("type");
                if (type.equals("pedido") && !type.equals("") && type != null) {
                    JSONObject pedido = new JSONObject(jsonObject.getString("pedido"));
                    double latitude = pedido.getDouble("latitude");
                    double longitude = pedido.getDouble("longitude");
                    String distancia = pedido.getString("distancia");

                    int id = pedido.getInt("id");
                    String time = pedido.getString("time");
                    List<Mpedido_detalle> detail = new ArrayList<>();
                    JSONArray detalle = new JSONArray(pedido.getString("detail"));
                    for (int i = 0; i < detalle.length(); i++) {
                        JSONObject dataDetail = detalle.getJSONObject(i);
                        Mpedido_detalle mpedido_detalle = new Mpedido_detalle(dataDetail.getInt("quantity"),
                                dataDetail.getJSONObject("product_id").getString("description"),
                                dataDetail.getDouble("unit_price"));
                        detail.add(mpedido_detalle);
                    }
                    sendNotification(latitude, longitude, id, time, distancia, detail);
                } else if (type.equals("pedido-cancelado") && !type.equals("") && type != null) {
                    JSONObject orden = new JSONObject(jsonObject.getString("orden"));

                    // "order_id":{"id":426,"date":"2020-11-29","time":"21:51:44.759559","voucher":"boleta","latitude":-13.512035343183001,"longitude":-71.9872897490859,"status":"cancel","calification":0,"client_id":36,"staff_id":"71847207"}
                    double latitude = orden.getDouble("latitude");
                    double longitude = orden.getDouble("longitude");
                    sendNotificationCancelado(latitude, longitude);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    int idNotification = 0;
    String gruupName = "com.greenmars.distribuidor.WORK_EMAIL";

    private void sendNotification(double lat, double lng, int id, String time, String distance, List<Mpedido_detalle> detail) {
        String titulo = "Pedido a " + distance + " metros\nReferencia " + getStringAddress(lat, lng);

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_ONE_SHOT);

        Intent broadcastIn = new Intent(this, NotificationReceiver.class);
        broadcastIn.putExtra("id_pedido", String.valueOf(id));
        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0, broadcastIn, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent broadcastIn2 = new Intent(this, PedidoActivity.class);
        broadcastIn2.putExtra("id_pedido", String.valueOf(id));
        broadcastIn2.putExtra("latitude", String.valueOf(lat));
        broadcastIn2.putExtra("longitude", String.valueOf(lng));
        PendingIntent actionIntent2 = PendingIntent.getActivity(this, 1, broadcastIn2, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_directions_bike)
                        .setContentTitle(titulo)
                        .setContentText("Tiempo Aprox " + time)
                        .setStyle(dataNotification(detail).setSummaryText("Tiempo Aprox " + time + " Min"))
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setLights(Color.WHITE, 3000, 3000)
                        .setColor(Color.BLUE)
                        .setContentIntent(actionIntent2)
                        .setGroupSummary(true)
                        .setGroup(gruupName)
                        .setShortcutId("summary_" + gruupName)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .addAction(R.drawable.ic_playlist_add_check_white_24dp, "Ver Detalle", actionIntent2)
                        .addAction(R.drawable.ic_check_white_24dp, "Confirmar", actionIntent)
                        .setSound(defaultSoundUri)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Notificación para pedidos",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(idNotification++, notificationBuilder.build());
    }

    private void sendNotificationCancelado(double lat, double lon) {
        String address = getStringAddress(lat, lon);
        String mensaje = "Se cancelo un pedido, referencia: " + address + ", (toque para abrir la app)";
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_directions_bike)
                        .setContentTitle("Mayorista Distribuidor")
                        .setContentText(mensaje)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setLights(Color.WHITE, 3000, 3000)
                        .setColor(Color.BLUE)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setSound(defaultSoundUri)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Notificación para pedidos",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    //--Registrar en servers
    private void sendRegistrationToServer(String token) {
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        if (db.getToken() != null && !db.getToken().equals("")) {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = Variable.HOST + "/staffs/device/update-token/";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("token_device", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
                try {
                    // int status = response.getInt("status");
                    Log.d(Variable.TAG, "sendRegistrationToServer: " + response.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
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
                        Toast.makeText(getApplicationContext(), "Error en operacion", Toast.LENGTH_SHORT).show();
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
                    headers.put("Authorization", "JWT " + db.getToken());
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };
            queue.add(jsonObjectRequest);
        }
    }

    private NotificationCompat.InboxStyle dataNotification(List<Mpedido_detalle> detalles) {
        NotificationCompat.InboxStyle notiImbox = new NotificationCompat.InboxStyle();
        for (Mpedido_detalle mpedido_detalle : detalles) {
            notiImbox.addLine(mpedido_detalle.getCantidad() + " " + mpedido_detalle.getDescripcion());
        }

        return notiImbox;
    }

    private String getStringAddress(Double lat, Double lng) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString().substring(0, strReturnedAddress.toString().lastIndexOf(","));
                Log.w(Variable.TAG, strReturnedAddress.toString());
            } else {
                Log.w(Variable.TAG, "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(Variable.TAG, "Canont get Address!");
        }
        if (strAdd.contains(","))
            return strAdd.substring(0, strAdd.lastIndexOf(","));
        else
            return "";
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }
}
