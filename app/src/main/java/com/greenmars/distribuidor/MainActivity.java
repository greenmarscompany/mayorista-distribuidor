package com.greenmars.distribuidor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.greenmars.distribuidor.data.RepositoryProveedorImpl;
import com.greenmars.distribuidor.data.database.DatabaseClient;
import com.greenmars.distribuidor.data.database.dao.ProveedorDao;
import com.greenmars.distribuidor.data.database.entity.User;
import com.greenmars.distribuidor.data.network.FabricanteApi;
import com.greenmars.distribuidor.data.network.ProveedorApi;
import com.greenmars.distribuidor.database.DatabaseHelper;
import com.greenmars.distribuidor.model.Account;
import com.greenmars.distribuidor.ui.login.IniciarSesionFragment;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableMaybeObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author Cordova, Julian
 * @author Mora Reyes, Orlando
 * @author Anaya Mendoza, Elvis
 */
@AndroidEntryPoint
public class MainActivity extends FragmentActivity {
    private static FragmentManager fragmentManager;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();


        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("mi_pref", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        long iduser = sharedPreferences.getLong("iduser", 0L);
        Log.i("mainacti", "token: " + token + " iduser: " + iduser);

        /*DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        Account account = db.getAcountToken();*/
        CircularProgressIndicator progressIndicator = findViewById(R.id.pbLoading);
        progressIndicator.setVisibility(View.GONE);


        if (!token.equals("") && iduser != 0) {
            progressIndicator.setVisibility(View.VISIBLE);
            DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .getProveedorDao()
                    .getUserRx(iduser)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableMaybeObserver<User>() {
                        @Override
                        public void onSuccess(@NonNull User user) {
                            Log.i("mainactivity", "usuario rxhava: " + user);

                            Intent myIntent;
                            if (user.isSupplier().equals("1")) {
                                myIntent = new Intent(getBaseContext(), FabricanteActivity.class);
                            } else {
                                myIntent = new Intent(getBaseContext(), HomeActivity.class);
                            }


                            startActivity(myIntent);
                            finish();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.e("mainactivity", "error al cargar usuario " + e);
                            replaceLoginFragment();
                        }

                        @Override
                        public void onComplete() {
                            progressIndicator.setVisibility(View.GONE);
                        }

                    });
        } else {
            replaceLoginFragment();
        }


        /*if (account != null) {
            Intent myIntent;
            if (account.isSupplier()) {
                myIntent = new Intent(getBaseContext(), FabricanteActivity.class);
            } else {
                myIntent = new Intent(getBaseContext(), HomeActivity.class);
            }
            startActivity(myIntent);
            finish();
        }*/

        /*if (!token.equals("")) {
            Intent myIntent;
            if (account.isSupplier()) {
                myIntent = new Intent(getBaseContext(), FabricanteActivity.class);
            } else {
                myIntent = new Intent(getBaseContext(), HomeActivity.class);
            }
            startActivity(myIntent);
            finish();
        }*/


        // If savedinstnacestate is null then replace login fragment
        /*if (savedInstanceState == null) {
            Log.i("mainacti", "Entro a savedinstancestate null");
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frameContainer, new IniciarSesionFragment(),
                            Variable.LoginFragment).commit();
        }*/

        // On close icon click finish activity
        findViewById(R.id.close_activity).setOnClickListener(arg0 -> finish());
    }


    // Replace Login Fragment with animation
    protected void replaceLoginFragment() {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                .replace(R.id.frameContainer, new IniciarSesionFragment(),
                        Variable.LoginFragment).commit();
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
}
