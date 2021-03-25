package com.greenmars.distribuidor;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class TipoProveedorFragment extends Fragment {
    View view;
    private static FragmentManager fragmentManager;
    //-----


    public TipoProveedorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tipo_proveedor, container, false);
        fragmentManager = getActivity().getSupportFragmentManager();
        Button btn_propietario = view.findViewById(R.id.btn_propietario_id);
        btn_propietario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, new SignUp_Fragment("t1"), Variable.SignUp_Fragment)
                        .commit();
            }
        });

        return view;
    }

}
