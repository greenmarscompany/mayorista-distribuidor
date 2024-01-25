package com.greenmars.distribuidor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenmars.distribuidor.adaptadores.SliderAdapter;
import com.greenmars.distribuidor.database.DatabaseHelper;
import com.greenmars.distribuidor.database.Session;
import com.greenmars.distribuidor.location.DirectionJSONParser;
import com.greenmars.distribuidor.location.FetchURL;
import com.greenmars.distribuidor.location.GpsUtils;
import com.greenmars.distribuidor.model.Account;
import com.greenmars.distribuidor.model.Slider;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    //---
    private DatabaseHelper db;
    //---
    public static Socket SOCKET;
    //--
    private final ArrayList<Marker> pedidos = new ArrayList<>();
    //--
    private static final String TAG = "Friibusiness";
    private Marker marcador;
    private GoogleMap mMap;
    private Polyline mPolyline;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 16;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    Context context;
    private Location mLastKnownLocation;
    View view;
    //---
    private boolean stopAnimation;
    //---
    BroadcastReceiver updateUIReciver;
    //---
    private boolean isNoti2;
    private int noti_id2;
    private double noti_lat2, noti_lng2;

    private SliderView sliderView;
    private ArrayList<Slider> listMessage;
    private SliderAdapter sliderAdapter;
    //---
    private ImageView btnSheet;

    public MapsFragment(int id, double lat, double lng) {
        noti_id2 = id;
        noti_lat2 = lat;
        noti_lng2 = lng;
        isNoti2 = true;
    }

    //--
    public MapsFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps, container, false);
        //---
        stopAnimation = false;
        //--
        context = getContext();
        //---
        this.db = new DatabaseHelper(getContext());

        sliderView = view.findViewById(R.id.imageSlider);

        //--
        InitSocketIO();
        //--
        FloatingActionButton fab_refresh = view.findViewById(R.id.fab_refresh);
        fab_refresh.setOnClickListener(view -> {
            Toast.makeText(getContext(), "Consultando pedidos", Toast.LENGTH_SHORT).show();
            getProductCategory();
        });
        FloatingActionButton position = view.findViewById(R.id.position);
        position.setOnClickListener(view -> {
            Intent intent1 = new Intent(getContext(), ActualizarPosicionActivity.class);
            startActivity(intent1);
        });
        // Inflate the layout for this fragment
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.google_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //--- response
        IntentFilter filter = new IntentFilter();

        filter.addAction("com.greenmars.distribuidor");
        updateUIReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //UI update here
                if (intent != null) {
                    if (intent.getStringExtra("toastMessage") != null) {
                        Toast.makeText(getContext(), intent.getStringExtra("toastMessage"), Toast.LENGTH_SHORT).show();
                    }

                    if (intent.getStringExtra("agregarmPedido") != null) {
                        if (Objects.equals(intent.getStringExtra("agregarmPedido"), "si")) {
                            double lat = Double.parseDouble(intent.getStringExtra("lat"));
                            double lng = Double.parseDouble(intent.getStringExtra("lng"));
                            final int id = Integer.parseInt(intent.getStringExtra("id"));
                            final LatLng latLng = new LatLng(lat, lng);
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> agregarPedido(latLng, id, true));
                            }
                        }
                    }
                }

            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().registerReceiver(updateUIReciver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            requireActivity().registerReceiver(updateUIReciver, filter);
        }

        //--

        boolean defaultSlider = new Session(getContext()).getSlider();

        btnSheet = view.findViewById(R.id.btnShowSheet);
        LinearLayout sliderSheet = view.findViewById(R.id.slider_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(sliderSheet);

        if (defaultSlider) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            btnSheet.setImageResource(R.drawable.ic_baseline_close_24);
        }
        Log.d(TAG, "slidervalue: " + defaultSlider);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        btnSheet.setImageResource(R.drawable.ic_baseline_close_24);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        btnSheet.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        btnSheet.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                new Session(getContext()).setSlider(false);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        listarItemsSlider();
        Log.d(TAG, "TOKEN FIREBASE: " + FirebaseMessagingService.getToken(getContext()));
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getProductCategory();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        super.onResume();
        getProductCategory();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (updateUIReciver != null && getActivity() != null) {
            getActivity().unregisterReceiver(updateUIReciver);
        } else {
            Log.i(TAG, "Do not unregister receiver as it was never registered");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        //--------------------------------
        map.setOnMarkerClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = mMap.getUiSettings();

        //Ubicar a cusco por posicion default
        LatLng cusco = new LatLng(-13.5179145, -71.9771895);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cusco));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-13.5179145, -71.9771895), 16));

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker != null && marker.getTag() != null) {
            //   Toast.makeText(getContext(), marker.getTag().toString(), Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(getContext(), PedidoActivity.class);
            myIntent.putExtra("id_pedido", marker.getTag().toString());
            myIntent.putExtra("latitude", String.valueOf(marker.getPosition().latitude));
            myIntent.putExtra("longitude", String.valueOf(marker.getPosition().longitude));
            startActivityForResult(myIntent, 1);
        } else {
            Toast.makeText(getContext(), "Marker null", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), new OnCompleteListener<Location>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                if (marcador != null) marcador.remove();

                                LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                //marcador=mMap.addMarker(new MarkerOptions().position(latLng).title("Estas aqui").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                //marcador.setTag(1);
                                //setMarkerBounce(marcador);
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                                getProductCategory();

                            } else {
                                //Toast.makeText(getApplicationContext(),"GPS Desactivado!",Toast.LENGTH_SHORT).show();
                                new GpsUtils(requireActivity()).turnGPSOn(new GpsUtils.onGpsListener() {
                                    @Override
                                    public void gpsStatus(boolean isGPSEnable) {
                                        // turn on GPS
                                        if (isGPSEnable) {
                                            //---------------------------------
                                            updateLocationUI();
                                            getDeviceLocation();
                                            //LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                                            //mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                                            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(requireActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
        }
        updateLocationUI();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                //getDeviceLocation();
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    private String getStringAddress(Double lat, Double lng) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

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

    //----
    private void setMarkerBounce(final Marker marker) {
        if (!stopAnimation) {
            final Handler handler = new Handler();
            final long startTime = SystemClock.uptimeMillis();
            final long duration = 2000;
            final Interpolator interpolator = new BounceInterpolator();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - startTime;
                    float t = Math.max(1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                    marker.setAnchor(0.5f, 1.0f + t);

                    if (t > 0.0) {
                        handler.postDelayed(this, 16);
                    } else {
                        setMarkerBounce(marker);
                    }
                }
            });
        }

    }

    //----
    private void cargarPedido() {
        if (isNoti2) {
            final LatLng latLng = new LatLng(noti_lat2, noti_lng2);
            Log.d("DATA NOTI", "prepare data");
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        Log.d("DATA NOTI", "add data");
                        agregarPedido(latLng, noti_id2, true);
                    }
                });
            }
            isNoti2 = false;
        }

    }

    //----
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getProductCategory() {
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        JSONObject object = new JSONObject();
        Account cuenta = db.getAcountToken();
        try {
            object.put("staff_id", cuenta.getDni());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Variable.HOST + "/orders/distributor/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            int st = response.getInt("status");
                            if (st == 200) {
                                JSONArray data = response.getJSONArray("data");
                                stopAnimation = true;
                                pedidos.clear();
                                mMap.clear();
                                cargarPedido();
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject pe = data.getJSONObject(i);
                                    double lat = pe.getDouble("latitude");
                                    double lng = pe.getDouble("longitude");
                                    final LatLng latLng = new LatLng(lat, lng);
                                    final int id = pe.getInt("id");
                                    boolean espera = false;
                                    switch (pe.getString("status")) {
                                        case "wait":
                                            espera = true;
                                            break;
                                        case "confirm":
                                            espera = false;
                                            break;
                                    }

                                    final boolean finalEspera = espera;
                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            agregarPedido(latLng, id, finalEspera);
                                        }
                                    });
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                        Toast.makeText(getContext(), msj, Toast.LENGTH_SHORT).show();

                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = db.getToken();
                Log.d("Voley get", token);
                headers.put("Authorization", "JWT " + token);
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    //------------------------------
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
                    } else {
                        Log.e(TAG, "AUTH error");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        SOCKET.on("send order", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (this == null) {
                    return;
                }
                JSONObject jsonObject = (JSONObject) args[0];
                try {

                    //String msg = jsonObject.getString("message");
                    Log.d(TAG, "Confirm Order: " + jsonObject.toString());
                    //JSONObject data = jsonObject.getJSONObject("data");
                    double lat = jsonObject.getDouble("latitude");
                    double lng = jsonObject.getDouble("longitude");
                    final LatLng latLng = new LatLng(lat, lng);
                    final int id = jsonObject.getInt("order_id");
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void run() {
                                agregarPedido(latLng, id, true);
                            }
                        });
                    }
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
                Log.d(TAG, jsonObject.toString());
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
            Log.d(TAG, "conect " + data.toString());
            SOCKET.emit("new connect", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean existePedido(int id) {
        boolean existe = false;
        for (int i = 0; i < pedidos.size(); i++) {
            Marker mrk = pedidos.get(i);
            if (Objects.requireNonNull(mrk.getTag()).toString().equals(String.valueOf(id))) {
                existe = true;
                break;
            }
        }
        return existe;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void agregarPedido(LatLng latLng, int id, boolean espera) {
        //LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
        Account cuenta = db.getAcountToken();
        LatLng location_company = new LatLng(Double.parseDouble(cuenta.getCompany_latitude()), Double.parseDouble(cuenta.getCompany_longitude()));
        if (!existePedido(id)) {
            if (espera) {
                stopAnimation = false;
                marcador = mMap.addMarker(
                        new MarkerOptions()
                                .position(latLng)
                                .title("Pedido: " + id)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                marcador.setTag(id);
                setMarkerBounce(marcador);
            } else {
                marcador = mMap.addMarker(
                        new MarkerOptions()
                                .position(latLng)
                                .title("Pedido: " + id)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                marcador.setTag(id);
                drawRoute(location_company, latLng);
            }
            pedidos.add(marcador);
            new FetchURL(getActivity()).execute(getUrl(location_company, latLng, "driving"), "driving");
        }

    }

    private void listarItemsSlider() {
        if (getContext() == null) return;
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = Variable.HOST + "/messages/proveedor";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Gson gson = new Gson();
                    listMessage = gson.fromJson(response, new TypeToken<ArrayList<Slider>>() {
                    }.getType());

                    sliderAdapter = new SliderAdapter(context);
                    for (Slider value : listMessage) {
                        Slider mensaje = new Slider();
                        mensaje.setImage(Variable.HOST_BASE + "/" + value.getImage());
                        sliderAdapter.addItem(mensaje);
                        System.out.println(Variable.HOST_BASE + "/" + value.getImage());
                    }

                    sliderView.setSliderAdapter(sliderAdapter);
                    sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
                    sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                    sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
                    sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
                    sliderView.startAutoCycle();
                }, error -> Toast.makeText(getContext(), "That didn't work!", Toast.LENGTH_SHORT).show());
        queue.add(stringRequest);
    }

    //------
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
    }

    //-----
    /*public void DibujarRuta(Object... values) {
        Log.d("Maps FRagment", String.valueOf(values[0]));
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }*/

    private void drawRoute(LatLng mOrigin, LatLng mDestination) {

        String url = getURL(mOrigin, mDestination);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }


    // Obtener la ruta
    private String getURL(LatLng origin, LatLng destination) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + destination.latitude + "," + destination.longitude;
        String key = "key=" + getString(R.string.google_maps_key);
        String parameters = str_origin + "&" + str_dest + "&" + key;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }

    private String downloadURL(String URL) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            java.net.URL url = new URL(URL);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } finally {
            assert iStream != null;
            iStream.close();
            urlConnection.disconnect();

        }

        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadURL(url[0]);
                Log.d("Download Task", "DownloadTask: " + data);
            } catch (IOException e) {
                Log.d("Background Task ", e.toString());
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject object;
            List<List<HashMap<String, String>>> routes = null;
            try {
                object = new JSONObject(jsonData[0]);
                DirectionJSONParser parser = new DirectionJSONParser();
                routes = parser.parse(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.parseColor("#35A754"));
            }

            if (lineOptions != null) {
                if (mPolyline != null) {
                    mPolyline.remove();
                }
                mPolyline = mMap.addPolyline(lineOptions);

            } else
                Toast.makeText(getContext(), "No route is found", Toast.LENGTH_LONG).show();
        }
    }
}
