package Personnage.pnj.Chapitre2;

import Combat.Combat;
import Effets.Paralysie;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Tobi Ololta (Toby) — Mage aux Griffes Paralysantes, rang C.
 * Magie des Griffes Paralysantes : griffes magiques au bout des doigts qui paralysent au contact.
 * Attention : ses propres griffes peuvent l'atteindre ! (il s'est blessé lui-même contre Natsu)
 * Coéquipier de Yuka dans l'équipe de Lyon.
 */
public class EnnemiTobi extends PersonnageBase {

    public EnnemiTobi() { this(12); }

    public EnnemiTobi(int niveau) {
        this.nom    = "Tobi";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 220.0 * niv;
        this.attaque =  80.0 * niv;
        this.defense =  55.0 * niv;
        this.vitesse =  85.0 * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.04;
        this.reduction_blocage = 0.05;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Griffe Paralysante", "Griffe Paralysante Renforcée", "Assaut de Griffes"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tobi lacère " + cible.getNom() + " de ses griffes paralysantes !");
        Combat.attaquer(this, cible, log);
        
        
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tobi concentre toute sa magie dans ses griffes et frappe " + cible.getNom() + " avec une puissance redoublée !");
        double degats = this.getAttaque() * 1.35;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Paralysie(1,0.20), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tobi se lance dans un assaut frénétique et lacère toute l'équipe ennemie de ses griffes !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 0.70;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                if (Math.random() < 0.35) {
                    Combat.appliquerEffet(this, cible, new Paralysie(1,0.20), log);
                }
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Griffe Paralysante — Inflige 100% ATK, 25% de chance de paralyser 1 tour.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Griffe Paralysante Renforcée — Inflige 135% ATK, paralyse la cible 1 tour.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Assaut de Griffes — Inflige 70% ATK à tous, 35% de chance de paralyser chacun 1 tour.");
    }
}
