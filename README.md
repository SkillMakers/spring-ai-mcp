# Serveur MCP en Java avec Spring AI

Code de la vidéo **« MCP en Java : créer un serveur MCP avec Spring AI (guide complet) »** de la chaîne
[IT Skills Maker](https://www.youtube.com/@ItSkillsMaker).

Un serveur MCP pour une petite boutique en ligne : trois outils, une ressource et un prompt, branchés
sur un service métier ordinaire.

- Spring Boot 4.1.1, Java 21
- Spring AI 2.0.1 (`spring-ai-starter-mcp-server-webmvc`), MCP Java SDK 2.0.0

## Lancer le serveur

```bash
./mvnw spring-boot:run
```

Le serveur écoute sur `http://localhost:8080/mcp` (transport streamable HTTP).

## Ce que le serveur expose

| Type | Nom | Rôle |
|---|---|---|
| Outil | `rechercher_produits` | recherche dans le catalogue par mot-clé (lecture) |
| Outil | `consulter_commande` | statut, date et total d'une commande (lecture) |
| Outil | `annuler_commande` | annule une commande non expédiée (destructeur) |
| Ressource | `boutique://politique-retour` | politique de retour, en texte |
| Prompt | `reponse_client` | consigne pour répondre à un client au sujet de sa commande |

Données de démonstration en mémoire : commandes `C-1001` (expédiée) et `C-1002` (payée).

## Tester avec MCP Inspector

```bash
npx @modelcontextprotocol/inspector --cli http://localhost:8080/mcp --transport http --method tools/list

npx @modelcontextprotocol/inspector --cli http://localhost:8080/mcp --transport http \
    --method tools/call --tool-name consulter_commande --tool-arg numero=C-1001
```

## Brancher Claude Code

```bash
claude mcp add --transport http boutique http://localhost:8080/mcp
```

## Les deux pièges de la vidéo

1. **Le transport.** Avec ce starter, sans réglage, c'est l'ancien transport SSE qui démarre
   (`/sse`), et `POST /mcp` répond 404. La ligne qui corrige, dans `application.properties` :

   ```properties
   spring.ai.mcp.server.protocol=STREAMABLE
   ```

2. **Les annotations de sécurité.** Sans annotation, un outil est déclaré destructeur
   (`destructiveHint = true`, `readOnlyHint = false`). Les outils de lecture sont donc marqués
   explicitement dans `BoutiqueOutils`. Ces annotations informent le client, elles ne protègent rien :
   la règle qui protège vraiment les commandes est dans le service `Boutique` (une commande expédiée
   ne peut pas être annulée).
