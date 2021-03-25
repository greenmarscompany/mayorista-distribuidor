package com.greenmars.distribuidor.model;

public class Account {
    private int ID;
    private String dni;
    private String email;
    private String telefono;
    private String direccion;
    private String password;
    private String token;
    private int type;
    private String company_id;
    //--------
    private String company_name;
    private String company_phone;
    private String company_address;
    private String company_latitude;
    private String company_longitude;
    private String url_facturacion;
    //---
    private String nombre;
    private String company_ruc;

    public Account() {
        this.ID = -1;
        this.dni = "";
        this.email = "";
        this.telefono = "";
        this.direccion = "";
        this.password = "";
        this.token = "";
        this.type = 1;
        this.company_id = "";
        this.company_name = "";
        this.company_phone = "";
        this.company_address = "";
        this.company_latitude = "";
        this.company_longitude = "";
        this.nombre = "";
        this.company_ruc = "";
        this.url_facturacion = "";
    }


    public Account(int ID, String dni, String email, String telefono, String direccion, String password,
                   String token, int type, String company_id, String nombre) {
        this.ID = ID;
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.password = password;
        this.token = token;
        this.type = type;//1, 2
        this.company_id = company_id;
        this.company_name = "";
        this.company_phone = "";
        this.company_address = "";
        this.company_latitude = "";
        this.company_longitude = "";
        this.nombre = nombre;
        this.company_ruc = "";
        this.url_facturacion = "";
    }

    public Account(int ID, String dni, String email, String telefono, String direccion, String password, String token, int type, String company_id, String company_name, String company_phone,
                   String company_address, String company_latitude, String company_longitude, String nombre, String company_ruc, String url_facturacion) {
        this.ID = ID;
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.password = password;
        this.token = token;
        this.type = type;
        this.company_id = company_id;
        this.company_name = company_name;
        this.company_phone = company_phone;
        this.company_address = company_address;
        this.company_latitude = company_latitude;
        this.company_longitude = company_longitude;
        this.nombre = nombre;
        this.company_ruc = company_ruc;
        this.url_facturacion = url_facturacion;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_phone() {
        return company_phone;
    }

    public void setCompany_phone(String company_phone) {
        this.company_phone = company_phone;
    }

    public String getCompany_address() {
        return company_address;
    }

    public void setCompany_address(String company_address) {
        this.company_address = company_address;
    }

    public String getCompany_latitude() {
        return company_latitude;
    }

    public void setCompany_latitude(String company_latitude) {
        this.company_latitude = company_latitude;
    }

    public String getCompany_longitude() {
        return company_longitude;
    }

    public void setCompany_longitude(String company_longitude) {
        this.company_longitude = company_longitude;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCompany_ruc() {
        return company_ruc;
    }

    public void setCompany_ruc(String company_ruc) {
        this.company_ruc = company_ruc;
    }

    public String getUrl_facturacion() {
        return url_facturacion;
    }

    public void setUrl_facturacion(String url_facturacion) {
        this.url_facturacion = url_facturacion;
    }
}
