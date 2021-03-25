package com.greenmars.distribuidor.util;

import com.greenmars.distribuidor.model.ProductMarca;

import java.util.Comparator;

public class SortMarkbyName implements Comparator<ProductMarca> {
    public int compare(ProductMarca a, ProductMarca b) {
        return a.getName().compareTo(b.getName());
    }
}
