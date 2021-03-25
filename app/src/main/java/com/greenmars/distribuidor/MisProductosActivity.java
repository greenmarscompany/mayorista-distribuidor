package com.greenmars.distribuidor;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MisProductosActivity extends AppCompatActivity implements MIsPedidosFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_productos);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.mis_pedidos_container, new MIsPedidosFragment());
        transaction.commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
