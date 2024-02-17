package com.greenmars.distribuidor;

public final class Variable {

    public static final String TAG = "MayoristaDistribuidor";

    //Todo !important cambiar a desarrollo

    //Production
    // public static final String HOST_BASE = "http://143.198.54.16";

    //Desarrollo
    public static final String HOST_BASE = "http://192.168.1.4:8000";

    //Produccion
    // public static final String HOST_BASE_MEDIA = "http://143.198.54.16/media/";

    //Desarrollo
    public static final String HOST_BASE_MEDIA = "http://192.168.1.4:8000/media/";

    //Production
    // public static final String HOST = "http://143.198.54.16/api";

    //Desarrollo
    public static final String HOST = "http://192.168.1.4:8000/api";

    //Produccion
    // public static final String HOST_RETROFIT = "http://143.198.54.16/api/";

    //Desarrollo
    public static final String HOST_RETROFIT = "http://192.168.1.4:8000/api/";


    public static final String HOST_NODE = "http://143.198.54.16:9000";

    //Email Validation pattern
    public static final String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";

    //Fragments Tags
    public static final String Login_Fragment = "Login_Fragment";
    public static final String LoginFragment = "LoginFragment";
    public static final String SignUp_Fragment = "SignUp_Fragment";
    public static final String ForgotPassword_Fragment = "ForgotPassword_Fragment";
    public static final String TipoProveedorFragment = "TipoProveedorFragment";

    public static final String RESTART_INTENT = "com.mayorista.appproveedorgas.RestartServiceBroadcastReceiver";

    public static final int MY_DEFAULT_TIMEOUT = 15000;

}
