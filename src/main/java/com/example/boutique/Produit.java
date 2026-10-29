package com.example.boutique;

import java.math.BigDecimal;

public record Produit(String reference, String nom, BigDecimal prix, int stock) {
}
