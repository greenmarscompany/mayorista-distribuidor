package com.greenmars.distribuidor.util;

import com.greenmars.distribuidor.model.ProductGas;
import com.greenmars.distribuidor.model.Register;

public class ProductRegister {
    private ProductGas product;
    private Register register;

    public ProductGas getProduct() {
        return product;
    }

    public void setProduct(ProductGas value) {
        this.product = value;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register value) {
        this.register = value;
    }
}

