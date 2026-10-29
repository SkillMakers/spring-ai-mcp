package com.example.boutique;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

/** Donnees en memoire : dans un vrai projet, ce serait le service metier existant. */
@Service
public class Boutique {

	private final List<Produit> produits = List.of(
			new Produit("CLV-01", "Clavier mecanique", new BigDecimal("89.90"), 12),
			new Produit("SOU-02", "Souris sans fil", new BigDecimal("29.90"), 0),
			new Produit("ECR-27", "Ecran 27 pouces", new BigDecimal("249.00"), 4));

	private final Map<String, Commande> commandes = new ConcurrentHashMap<>(Map.of(
			"C-1001", new Commande("C-1001", "alice@exemple.fr", LocalDate.of(2026, 10, 12), "EXPEDIEE", new BigDecimal("119.80")),
			"C-1002", new Commande("C-1002", "bob@exemple.fr", LocalDate.of(2026, 10, 17), "PAYEE", new BigDecimal("249.00"))));

	public List<Produit> rechercher(String motCle) {
		String m = motCle.toLowerCase();
		return produits.stream().filter(p -> p.nom().toLowerCase().contains(m)).toList();
	}

	public Optional<Commande> commande(String numero) {
		return Optional.ofNullable(commandes.get(numero));
	}

	public Commande annuler(String numero) {
		Commande c = commande(numero).orElseThrow(() -> new IllegalArgumentException("Commande inconnue : " + numero));
		if (c.statut().equals("EXPEDIEE")) {
			throw new IllegalStateException("La commande " + numero + " est deja expediee : annulation impossible");
		}
		Commande annulee = c.annulee();
		commandes.put(numero, annulee);
		return annulee;
	}
}
