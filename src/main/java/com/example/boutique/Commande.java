package com.example.boutique;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Commande(String numero, String client, LocalDate date, String statut, BigDecimal total) {

	public Commande annulee() {
		return new Commande(numero, client, date, "ANNULEE", total);
	}
}
