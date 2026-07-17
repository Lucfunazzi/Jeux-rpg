package Personnage.json;

import Personnage.PersonnageBase;
import java.util.List;

/**
 * Personnage entièrement piloté par les données JSON.
 * Le comportement de toutes ses attaques est délégué à MoteurAttaque.
 *
 * Usage :
 *   PersonnageBase gaara = ChargeurPersonnage.charger("Naruto/gaara.json");
 */
public class PersonnageJson extends PersonnageBase {

    private final AttaqueData attaqueBaseData;
    private final AttaqueData attaqueSpecialeData;
    private final AttaqueData attaqueUltimeData;

    public PersonnageJson(PersonnageData data) {
        this.nom      = data.nom;
        this.type     = data.type;
        this.role     = data.role;
        this.rarete   = data.rarete;
        this.niveau   = 1;

        // Stats de base × multiplicateur de rareté
        double mult = multiplicateurRarete(data.rarete);
        this.vie      = data.stats.vie      * mult;
        this.attaque  = data.stats.attaque  * mult;
        this.defense  = data.stats.defense  * mult;
        this.vitesse  = data.stats.vitesse  * mult;

        this.taux_critiques    = data.stats.tauxCritique;
        this.degat_critiques   = data.stats.degatCritique;
        this.taux_precisions   = data.stats.precision;
        this.taux_esquives     = data.stats.esquive;
        this.taux_blocage      = data.stats.blocage;
        this.reduction_blocage = data.stats.reductionBlocage;
        this.degats_renvoi     = data.stats.degatsRenvoi;

        initialiserVieMax();

        // Récupérer les trois attaques
        attaqueBaseData     = trouverAttaque(data, "BASE");
        attaqueSpecialeData = trouverAttaque(data, "SPECIALE");
        attaqueUltimeData   = trouverAttaque(data, "ULTIME");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ATTAQUES — déléguées au MoteurAttaque
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void attaqueBase(PersonnageBase cible,
                            List<PersonnageBase> allies,
                            List<PersonnageBase> ennemis,
                            List<String> log) {
        if (attaqueBaseData != null)
            MoteurAttaque.executerBase(attaqueBaseData, this, cible, allies, ennemis, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible,
                                List<PersonnageBase> allies,
                                List<PersonnageBase> ennemis,
                                List<String> log) {
        if (attaqueSpecialeData != null)
            MoteurAttaque.executerSpeciale(attaqueSpecialeData, this, cible, allies, ennemis, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> allies,
                              List<PersonnageBase> ennemis,
                              List<String> log) {
        if (attaqueUltimeData != null)
            MoteurAttaque.executerUltime(attaqueUltimeData, this, allies, ennemis, log);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DESCRIPTIONS
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public String[] getNomsAttaques() {
        return new String[]{
            attaqueBaseData     != null ? attaqueBaseData.nom     : "?",
            attaqueSpecialeData != null ? attaqueSpecialeData.nom : "?",
            attaqueUltimeData   != null ? attaqueUltimeData.nom   : "?"
        };
    }

    @Override
    public void descriptionAttaqueBase() {
        if (attaqueBaseData != null)
            System.out.println(attaqueBaseData.nom + " — " + attaqueBaseData.description);
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        if (attaqueSpecialeData != null)
            System.out.println(attaqueSpecialeData.nom + " — " + attaqueSpecialeData.description);
    }

    @Override
    public void descriptionAttaqueUltime() {
        if (attaqueUltimeData != null)
            System.out.println(attaqueUltimeData.nom + " — " + attaqueUltimeData.description);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UTILITAIRES
    // ─────────────────────────────────────────────────────────────────────────

    private static AttaqueData trouverAttaque(PersonnageData data, String type) {
        if (data.attaques == null) return null;
        for (AttaqueData a : data.attaques)
            if (type.equalsIgnoreCase(a.type)) return a;
        return null;
    }

    /**
     * Multiplicateurs identiques à ceux utilisés dans les classes Java existantes.
     * C=1.00, B=1.30, A=1.40, S=1.50, SS=1.75, UR=2.00
     */
    private static double multiplicateurRarete(String rarete) {
        return switch (rarete) {
            case "B"  -> 1.30;
            case "A"  -> 1.40;
            case "S"  -> 1.50;
            case "SS" -> 1.75;
            case "UR" -> 2.00;
            default   -> 1.00; // C
        };
    }
}