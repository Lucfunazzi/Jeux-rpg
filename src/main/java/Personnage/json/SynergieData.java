package Personnage.json;

/**
 * Représente une synergie déclenchée si un allié spécifique est vivant.
 *
 * Actions disponibles :
 *   RAGE          — donne "valeur" points de rage à l'allié nommé
 *   RAGE_EQUIPE   — donne "valeur" points de rage à toute l'équipe (allie ignoré)
 *   BUFF_ATK      — applique BuffAttaque(valeur, tours) à l'allié nommé
 *   BUFF_DEF      — applique BuffDefense(valeur, tours) à l'allié nommé
 *   BUFF_ATK_SOI  — applique BuffAttaque(valeur, tours) à soi-même
 *   SOIN_SOI      — se soigne de valeur% ATK
 *   EFFET         — applique un EffetData sur l'allié nommé (voir "effet")
 *
 * Exemples JSON :
 *   { "allie": "Sasuke",  "action": "RAGE",     "valeur": 20 }
 *   { "allie": "Kakuzu",  "action": "BUFF_DEF",  "valeur": 0.15, "tours": 2 }
 *   { "allie": "",        "action": "RAGE_EQUIPE","valeur": 15 }
 */
public class SynergieData {
    /** Nom exact de l'allié déclencheur. Vide si action de groupe. */
    public String allie = "";

    /** Action à effectuer (voir javadoc ci-dessus). */
    public String action;

    /** Valeur principale (rage, pourcentage de buff, etc.). */
    public double valeur = 0.0;

    /** Durée en tours pour les effets. */
    public int tours = 0;

    /** Effet optionnel pour action = EFFET. */
    public EffetData effet;
}