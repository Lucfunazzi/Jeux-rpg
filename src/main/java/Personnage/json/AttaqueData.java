package Personnage.json;

import java.util.List;

/**
 * Représente une attaque désérialisée depuis un fichier JSON.
 * Tous les champs sont publics pour que Gson puisse les remplir directement.
 */
public class AttaqueData {

    /** "BASE", "SPECIALE" ou "ULTIME" */
    public String type;

    /** Nom affiché en combat */
    public String nom;

    /**
     * Ciblage :
     *   CIBLE_UNIQUE        — la cible passée par le moteur de combat (Tank → DPS → Support)
     *   AOE_ENNEMIS         — tous les ennemis vivants
     *   AOE_ALLIES          — tous les alliés vivants
     *   PLUS_HAUTE_ATK      — ennemi avec la plus haute attaque
     *   PLUS_HAUTE_DEF      — ennemi avec la plus haute défense
     *   PLUS_HAUTS_PV       — ennemi avec le plus de PV
     *   PLUS_BAS_PV_ENNEMI  — ennemi avec le moins de PV
     *   PLUS_BAS_PV_ALLIE   — allié vivant avec le moins de PV
     *   ALEATOIRE_ENNEMI    — ennemi aléatoire vivant
     *   ROLE_ENNEMI         — ennemis du rôle spécifié dans "rolesCibles"
     *   SOI                 — le personnage lui-même (buffs / auto-soins)
     */
    public String ciblage = "CIBLE_UNIQUE";

    /**
     * Rôles ciblés quand ciblage = ROLE_ENNEMI (ex: ["Support", "DPS"]).
     * Si aucun ennemi de ce rôle n'est vivant, repli sur tous les ennemis.
     */
    public List<String> rolesCibles;

    /** Multiplicateur de dégâts (ex: 1.30 = 130% ATK). 0 si pas de dégâts. */
    public double multiplicateur = 0.0;

    /** Si true, l'ultime applique le bonus de rage (multiplicateurRage). */
    public boolean bonusRage = false;

    /** Coût en % PV max infligé à soi-même après l'attaque (ex: 0.08 = 8%). */
    public double coutPvSoi = 0.0;

    /** Soin infligé à une cible alliée en % ATK (ex: 0.60 = 60% ATK). */
    public double soinPourcentageAtk = 0.0;

    /**
     * Ciblage du soin :
     *   PLUS_BAS_PV_ALLIE  — allié avec le moins de PV (défaut)
     *   SOI                — se soigne soi-même
     *   AOE_ALLIES         — soigne toute l'équipe
     */
    public String ciblageSOin = "PLUS_BAS_PV_ALLIE";

    /** Si > 0, purifier N effets négatifs sur la cible du soin. */
    public int purifierApresSOin = 0;

    /** Si true, purifier tous les alliés (1 effet chacun) après l'attaque. */
    public boolean purifierEquipe = false;

    /** Gain de rage pour l'attaquant après cette attaque (ex: 20). */
    public double gainRageSoi = 0.0;

    /** Effets appliqués sur la/les cible(s) ennemies. */
    public List<EffetData> effets;

    /** Effets appliqués sur soi ou sur l'équipe alliée. */
    public List<EffetData> effetsSoi;

    /** Synergies déclenchées si un allié spécifique est vivant. */
    public List<SynergieData> synergies;

    /** Description affichée dans les menus. */
    public String description = "";
}