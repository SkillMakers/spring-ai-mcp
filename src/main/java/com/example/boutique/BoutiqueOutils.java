package com.example.boutique;

import java.util.List;

import org.springframework.ai.mcp.annotation.McpArg;
import org.springframework.ai.mcp.annotation.McpPrompt;
import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class BoutiqueOutils {

	private final Boutique boutique;

	public BoutiqueOutils(Boutique boutique) {
		this.boutique = boutique;
	}

	@McpTool(name = "rechercher_produits",
			description = "Recherche des produits du catalogue par mot-cle dans le nom. Renvoie reference, nom, prix et stock.",
			annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false))
	public List<Produit> rechercherProduits(
			@McpToolParam(description = "Mot-cle a chercher dans le nom du produit, par exemple 'clavier'") String motCle) {
		return boutique.rechercher(motCle);
	}

	@McpTool(name = "consulter_commande",
			description = "Donne le statut, la date et le total d'une commande a partir de son numero (format C-1234).",
			annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false))
	public Commande consulterCommande(
			@McpToolParam(description = "Numero de commande, par exemple C-1001") String numero) {
		return boutique.commande(numero)
				.orElseThrow(() -> new IllegalArgumentException("Aucune commande " + numero));
	}

	@McpTool(name = "annuler_commande",
			description = "Annule une commande qui n'est pas encore expediee. Action irreversible.",
			annotations = @McpTool.McpAnnotations(destructiveHint = true, idempotentHint = true))
	public Commande annulerCommande(
			@McpToolParam(description = "Numero de la commande a annuler") String numero) {
		return boutique.annuler(numero);
	}

	@McpResource(uri = "boutique://politique-retour", name = "politique-retour",
			description = "Politique de retour et de remboursement de la boutique", mimeType = "text/plain")
	public String politiqueRetour() {
		return "Retour accepte sous 30 jours. Remboursement sous 14 jours apres reception. Produits ouverts : frais de 10 %.";
	}

	@McpPrompt(name = "reponse_client", description = "Prepare une reponse polie a un client au sujet de sa commande")
	public String reponseClient(@McpArg(name = "numero", description = "Numero de commande", required = true) String numero) {
		return "Consulte la commande " + numero + " avec l'outil consulter_commande, puis redige une reponse courte et polie "
				+ "au client, en francais, qui donne son statut. N'annule rien sans confirmation.";
	}
}
