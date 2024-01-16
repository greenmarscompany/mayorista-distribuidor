package com.greenmars.distribuidor;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.greenmars.distribuidor.database.DatabaseHelper;
import com.greenmars.distribuidor.model.Account;

/**
 * @author Cordova, Julian
 * @author Mora Reyes, Orlando
 * @author Anaya Mendoza, Elvis
 */
public class MainActivity extends FragmentActivity {
    private static FragmentManager fragmentManager;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLocationPermission();

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        Account account = db.getAcountToken();
        if (account != null) {
            Intent myIntent;
            if (account.isSupplier()) {
                myIntent = new Intent(getBaseContext(), FabricanteActivity.class);
            } else {
                myIntent = new Intent(getBaseContext(), HomeActivity.class);
            }
            startActivity(myIntent);
            finish();
        }

        fragmentManager = getSupportFragmentManager();
        // If savedinstnacestate is null then replace login fragment
        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frameContainer, new Login_Fragment(),
                            Variable.Login_Fragment).commit();
        }

        // On close icon click finish activity
        findViewById(R.id.close_activity).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        finish();

                    }
                });
    }


    // Replace Login Fragment with animation
    protected void replaceLoginFragment() {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                .replace(R.id.frameContainer, new Login_Fragment(),
                        Variable.Login_Fragment).commit();
    }

    @Override
    public void onBackPressed() {

        // Find the tag of signup and forgot password fragment
        Fragment SignUp_Fragment = fragmentManager
                .findFragmentByTag(Variable.SignUp_Fragment);
        Fragment TipoProveedor_Fragment = fragmentManager
                .findFragmentByTag(Variable.TipoProveedorFragment);
        Fragment ForgotPassword_Fragment = fragmentManager
                .findFragmentByTag(Variable.ForgotPassword_Fragment);

        // Check if both are null or not
        // If both are not null then replace login fragment else do backpressed
        // task
        if (TipoProveedor_Fragment != null)
            replaceLoginFragment();
        else if (SignUp_Fragment != null)
            replaceLoginFragment();
        else if (ForgotPassword_Fragment != null)
            replaceLoginFragment();
        else
            super.onBackPressed();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
}
