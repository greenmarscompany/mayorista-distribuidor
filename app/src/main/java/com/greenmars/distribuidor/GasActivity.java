package com.greenmars.distribuidor;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GasActivity extends AppCompatActivity implements GasFragment.OnFragmentInteractionListener,
        //  NavigationView.OnNavigationItemSelectedListener,
        GasdetailFragment.OnFragmentInteractionListener,
        GasCamionFragment.OnFragmentInteractionListener {

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas);

        GasFragment oGasFragment = new GasFragment();
        oGasFragment.TipoGas = "gas-premium";

        BottomNavigationView bottomNavigationView = findViewById(R.id.BottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.gas_container, oGasFragment)
                .commit();
        bottomNavigationView.setItemIconTintList(null);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = menuItem -> {

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        GasFragment oGasFragment = new GasFragment();
        FragmentProductDetail oFragmentProductDetail = new FragmentProductDetail();
        oFragmentProductDetail.MarcasId = "2";
        switch (menuItem.getItemId()) {
            case R.id.Gas_Premium:

                oGasFragment.TipoGas = "gas-premium";
                transaction.replace(R.id.gas_container, oGasFragment);
                transaction.commit();
                transaction.addToBackStack(null);
                Toast.makeText(getBaseContext(), "Gas Premium", Toast.LENGTH_SHORT).show();
                break;
            case R.id.Gas_Normal:
                oGasFragment.TipoGas = "gas-normal";
                transaction.replace(R.id.gas_container, oGasFragment);
                transaction.commit();
                transaction.addToBackStack(null);
                Toast.makeText(getBaseContext(), "Gas Normal", Toast.LENGTH_SHORT).show();
                break;
            case R.id.Camion:
                transaction.replace(R.id.gas_container, new GasCamionFragment());
                transaction.commit();
                transaction.addToBackStack(null);
                Toast.makeText(getBaseContext(), "Camión", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    };

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
