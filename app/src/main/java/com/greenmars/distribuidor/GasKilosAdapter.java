package com.greenmars.distribuidor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.greenmars.distribuidor.database.DatabaseHelper;
import com.greenmars.distribuidor.model.ProductGas;
import com.greenmars.distribuidor.util.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GasKilosAdapter extends RecyclerView.Adapter<GasKilosAdapter.viewHolder> implements View.OnClickListener {


    private View.OnClickListener listener;
    private Context context;
    private DatabaseHelper db;
    List<ProductGas> Product_list;
    GasdetailFragment oGasdetailFragment;

    public GasKilosAdapter(List<ProductGas> product_list, GasdetailFragment oGasdetailFragment) {
        this.Product_list = product_list;
        this.oGasdetailFragment = oGasdetailFragment;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_gas, parent, false);
        view.setOnClickListener(this);
        context = parent.getContext();
        this.db = new DatabaseHelper(context);
        return new viewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {

        this.listener = onClickListener;

    }

    @Override
    public void onBindViewHolder(@NonNull GasKilosAdapter.viewHolder holder, int position) {
        holder.bind(Product_list.get(position));
    }

    @Override
    public int getItemCount() {
        return Product_list.size();
    }

    public interface InterfaceListar {
        void Listar();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView product_name;
        TextView txtPesoGas;
        EditText etxtPrecioUnitario;
        Button btnguardar_misproductos;
        ImageView image_product;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.gas_camion_name);
            txtPesoGas = itemView.findViewById(R.id.txtPesoGas);
            etxtPrecioUnitario = itemView.findViewById(R.id.editar_precio_misproductos);
            btnguardar_misproductos = itemView.findViewById(R.id.guardar_misproductos);
            image_product = itemView.findViewById(R.id.marcas_product);
        }

        void bind(final ProductGas products) {

            product_name.setText(products.getMarke_id().getName() + " " + products.getDetail_measurement_id().getName());
            txtPesoGas.setText(products.getMeasurement() + " " + products.getUnit_measurement_id().getName());
            etxtPrecioUnitario.setText("0.00");
            Picasso.get().load(products.getImage()).into(image_product);
            btnguardar_misproductos.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (etxtPrecioUnitario.getText().toString().equals("") || Double.valueOf(etxtPrecioUnitario.getText().toString()) < 1)
                        Toast.makeText(context, "El precio debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                    else {
                        JSONObject object = new JSONObject();
                        try {
                            object.put("product_id", products.getId());
                            object.put("price", Double.valueOf(etxtPrecioUnitario.getText().toString()) + 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String url = Variable.HOST + "/product/staff/register/";
                        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Toast.makeText(context, "Se agrego el producto " + product_name.getText(), Toast.LENGTH_LONG).show();
                                    // Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show();
                                    oGasdetailFragment.Listar();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

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

                        VolleySingleton.getInstance(context).addToRequestQueue(objectRequest);
                    }
                }
            });
        }
    }
}
