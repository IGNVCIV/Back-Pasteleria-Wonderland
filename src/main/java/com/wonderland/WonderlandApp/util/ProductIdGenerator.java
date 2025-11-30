package com.wonderland.WonderlandApp.util;

import java.util.HashMap;
import java.util.Map;

public class ProductIdGenerator {

    private static final Map<String, String> CATEGORY_PREFIXES = new HashMap<>() {{
        put("Tortas Cuadradas", "TC");
        put("Tortas Circulares", "TT");
        put("Postres Individuales", "PI");
        put("Productos Sin Azúcar", "PSA");
        put("Pastelería Tradicional", "PT");
        put("Productos Sin Gluten", "PG");
        put("Productos Veganos", "PV");
        put("Tortas Especiales", "TE");
    }};
    
    public static String generateId(String category, int nextNumber) {
        String prefix = CATEGORY_PREFIXES.getOrDefault(category, "XX");
        return prefix + String.format("%03d", nextNumber);
    }
}
