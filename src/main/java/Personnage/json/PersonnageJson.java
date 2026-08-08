package Personnage.json;

import Personnage.PersonnageBase;
import java.util.List;

/**
 * Personnage minimal construit depuis PersonnageData (stats seulement, sans attaques
 * scriptees). Sert de fixture de test pour exercer PersonnageBase/Combat avec des stats
 * arbitraires sans passer par une vraie classe perso_*.
 */
public class PersonnageJson extends PersonnageBase {

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
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ATTAQUES — sans effet : PersonnageJson ne sert qu'a exercer les stats/formules.
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void attaqueBase(PersonnageBase cible,
                            List<PersonnageBase> allies,
                            List<PersonnageBase> ennemis,
                            List<String> log) {
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible,
                                List<PersonnageBase> allies,
                                List<PersonnageBase> ennemis,
                                List<String> log) {
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> allies,
                              List<PersonnageBase> ennemis,
                              List<String> log) {
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"?", "?", "?"};
    }

    @Override
    public void descriptionAttaqueBase() {
    }

    @Override
    public void descriptionAttaqueSpeciale() {
    }

    @Override
    public void descriptionAttaqueUltime() {
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UTILITAIRES
    // ─────────────────────────────────────────────────────────────────────────

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
