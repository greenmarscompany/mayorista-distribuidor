package com.greenmars.distribuidor;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenmars.distribuidor.model.ProductGas;

import java.util.ArrayList;


public class GasFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private RecyclerView recyclerView;
    private ArrayList<ProductGas> productDetails_gases;
    private DetailGasAdapter detailgasAdapter;
    public String TipoGas = "";

    public GasFragment() {
        // Required empty public constructor
    }

    public static GasFragment newInstance(String param1, String param2) {
        GasFragment fragment = new GasFragment();
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
        View view = inflater.inflate(R.layout.fragment_gas, container, false);
        recyclerView = view.findViewById(R.id.gas_detail_container);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        Listar();

        return view;
    }

    private void AsignarBotones() {
        detailgasAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String MarkeId = String.valueOf(productDetails_gases.get(recyclerView.getChildAdapterPosition(v)).getMarke_id().getId());
                GasdetailFragment oGasdetailFragment = new GasdetailFragment();
                oGasdetailFragment.MarcaGas = MarkeId;
                oGasdetailFragment.TipoGas = TipoGas;
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.gas_container, oGasdetailFragment);
                transaction.commit();
            }
        });
    }

    private void Listar() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = Variable.HOST + "/product/gas/" + TipoGas;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    recyclerView.setAdapter(null);
                    Gson gson = new Gson();
                    productDetails_gases = gson.fromJson(response.substring(34, response.length() - 1).trim(),
                            new TypeToken<ArrayList<ProductGas>>() {
                            }.getType());
                    for (ProductGas item : productDetails_gases) {
                        item.setImage(Variable.HOST_BASE + item.getImage());
                    }
                    detailgasAdapter = new DetailGasAdapter(productDetails_gases);

                    recyclerView.setAdapter(detailgasAdapter);

                    AsignarBotones();
                }, error -> Toast.makeText(getContext(), "That didn't work!", Toast.LENGTH_SHORT).show());
        queue.add(stringRequest);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
