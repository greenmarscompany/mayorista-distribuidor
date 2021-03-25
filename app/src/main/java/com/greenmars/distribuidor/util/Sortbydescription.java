package com.greenmars.distribuidor.util;

import com.greenmars.distribuidor.model.ProductGas;

import java.util.Comparator;

public class Sortbydescription implements Comparator<ProductGas> {
    // Used for sorting in ascending order of
// roll number
    public int compare(ProductGas a, ProductGas b) {
        return a.getDescription().compareTo(b.getDescription());
    }
}
