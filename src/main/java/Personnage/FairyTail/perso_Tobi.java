package Personnage.FairyTail;

import Combat.Combat;
import Effets.Paralysie;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Tobi Ololta (Toby) — Elementaliste, rang C.
 * Magie des Griffes Paralysantes : griffes qui paralysent au contact.
 * Coéquipier fidèle de Yuka, membre de Lamia Scale.
 */
public class perso_Tobi extends PersonnageBase {

    public perso_Tobi() {
        this.nom    = "Tobi";
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";
        this.niveau = 1;
        double mult = 1.00;
        this.vie     = 300 * mult;
        this.attaque = 105 * mult;
        this.defense =  70 * mult;
        this.vitesse = 105 * mult;
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
        if (Math.random() < 0.25) Combat.appliquerEffet(this, cible, new Paralysie(1,0.60), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tobi concentre toute sa magie et frappe " + cible.getNom() + " avec une griffe renforcée !");
        double degats = this.getAttaque() * 1.35;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Paralysie(1,0.30), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tobi se lance dans un assaut frénétique et lacère toute l'équipe ennemie !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = (this.getAttaque() * 0.80) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                if (Math.random() < 0.35) Combat.appliquerEffet(this, cible, new Paralysie(1,0.30), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() { System.out.println("Griffe Paralysante — 100% ATK, 25% de paralysie 1 tour."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Griffe Renforcée — 135% ATK, paralyse 1 tour."); }
    @Override public void descriptionAttaqueUltime() { System.out.println("Assaut de Griffes — 80% ATK à tous (x rage), 35% de paralysie chacun."); }
}
