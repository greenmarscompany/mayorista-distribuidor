package com.greenmars.distribuidor;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenmars.distribuidor.database.DatabaseHelper;
import com.greenmars.distribuidor.model.Account;
import com.greenmars.distribuidor.util.ProductRegister;
import com.greenmars.distribuidor.util.SortbyProductRegister;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MIsPedidosFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private RecyclerView recyclerView;
    private ArrayList<ProductRegister> mispedidos_list;
    private MIsPedidosAdapter mIsPedidosAdapter;
    private View view;
    private static final String TAG = Variable.TAG;

    public MIsPedidosFragment() {
        // Required empty public constructor
    }

    public static MIsPedidosFragment newInstance(String param1, String param2) {
        MIsPedidosFragment fragment = new MIsPedidosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mis_pedidos, container, false);
        recyclerView = view.findViewById(R.id.mis_pedidos_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Listar();
        mispedidos_list = new ArrayList<>();
        //---


        return view;
    }

    void Listar() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        final DatabaseHelper db = new DatabaseHelper(getContext());
        StringBuilder sb = new StringBuilder();
        Account cuenta = db.getAcountToken();
        sb.append(Variable.HOST + "/product/staff/").append(cuenta.getCompany_id());
        String url = sb.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    recyclerView.setAdapter(null);
                    Gson gson = new Gson();
                    mispedidos_list = gson.fromJson(response.substring(34, response.length() - 1).trim(), new TypeToken<ArrayList<ProductRegister>>() {
                    }.getType());
                    for (ProductRegister item : mispedidos_list) {

                        item.getProduct().setImage(Variable.HOST_BASE + item.getProduct().getImage());
                        if (item.getProduct().getCategory_id().getId() == 2)
                            item.getProduct().setDescription(item.getProduct().getMarke_id().getName() + " " + item.getProduct().getDetail_measurement_id().getName());
                        item.getProduct().setDescription(item.getProduct().getDescription().substring(0, 1).toUpperCase(Locale.ROOT) + item.getProduct().getDescription().substring(1));
                    }
                    Collections.sort(mispedidos_list, new SortbyProductRegister());
                    mIsPedidosAdapter = new MIsPedidosAdapter(mispedidos_list, MIsPedidosFragment.this);
                    recyclerView.setAdapter(mIsPedidosAdapter);
                },
                error -> {
                    Log.d(Variable.TAG, "Listar: " + error.toString());
                    NetworkResponse response = error.networkResponse;
                    if (response != null) {
                        if (error instanceof ServerError) {
                            try {
                                String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                JSONObject obj = new JSONObject(res);
                                Log.d(TAG, "Voley post: " + obj.toString());
                            } catch (JSONException | UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(getContext(), "Opss timeout, verifique su conectividad", Toast.LENGTH_LONG).show();
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Variable.MY_DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(stringRequest);

        EditText txtFilterProduct = view.findViewById(R.id.txtBuscarProductos);
        txtFilterProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && s.toString() != null) {
                    mIsPedidosAdapter.getFilter().filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
