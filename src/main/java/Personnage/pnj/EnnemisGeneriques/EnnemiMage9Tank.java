package Personnage.pnj.EnnemisGeneriques;

import Personnage.PersonnageBase;
import Combat.Combat;
import Effets.*;
import java.util.List;

public class EnnemiMage9Tank extends PersonnageBase {

    public EnnemiMage9Tank(Variante variante, int niveau) {
        this.nom    = "Chevalier de l'ordre magique";
        this.niveau = niveau;
        this.type="Chevalier";
        this.role   = "Tank";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        // Ecart historique entre variantes, preserve tel quel : chapitre1Elite (def 50) et
        // Chapitre2/2Elite (def 70, plus tanky) different du profil de base (def 45) utilise
        // par Chapitre1/Chapitre3/Chapitre4. Le blocage suit Chapitre1 seul a 0.10, 0.22 ailleurs.
        double defenseBase = switch (variante) {
            case CHAPITRE_1_ELITE -> 50.0;
            case CHAPITRE_2, CHAPITRE_2_ELITE -> 70.0;
            default -> 45.0;
        };
        this.vie     = 400.0 * niv;
        this.attaque =  52.0 * niv;
        this.defense = defenseBase * niv;
        this.vitesse =  40.0 * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = (variante == Variante.CHAPITRE_1) ? 0.10 : 0.22;
        this.reduction_blocage = 0.25;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " Rune de solidification ");
        Combat.appliquerEffet(this, new BuffDefense(0.20,2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " Epée runique!");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 0.70;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);

            }
        }

        Combat.appliquerEffet(this, new Regeneration(0.2,2), log);
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Coup de Bouclier", "Rune de Solidification", "Charge Devastatrice"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Coup de Bouclier : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Rune de Solidification : augmente sa DEF de 20% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Charge Devastatrice : inflige 70% ATK a toute l'equipe ennemie et se régénère de 20% des PV max pendant 2 tours.");
    }
}
