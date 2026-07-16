package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiRaditzC3 extends PersonnageBase {

    public EnnemiRaditzC3() { this(30); }

    public EnnemiRaditzC3(int niveau) {
        this.nom    = "Raditz";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "DPS";
        this.rarete = "B";

        double mult = 1.3;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 400.0 * mult * niv;
        this.attaque = 150.0 * mult * niv;
        this.defense = 100.0 * mult * niv;
        this.vitesse = 110.0 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.3;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.1;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Kikoha rapide", "Double dimanche", "Cadeau"};
    }

    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Raditz lance un Kikoha rapide sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Raditz utilise Double dimanche !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, new BuffTauxCritique(0.10, 2), log);
    }

    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Raditz utilise Cadeau !");
        PersonnageBase cible = null;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                if (cible == null || ennemi.getAttaque() > cible.getAttaque())
                    cible = ennemi;
            }
        }
        if (cible == null) return;
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Brulure(2, 0.08), log);
        if (Math.random() < 0.25) {
            Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
        }
    }

    @Override public void descriptionAttaqueBase()     { System.out.println("Kikoha rapide : inflige 100% ATK a une cible."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Double dimanche : inflige 110% ATK a une cible et augmente son taux critique de 10% pendant 2 tours."); }
    @Override public void descriptionAttaqueUltime()   { System.out.println("Cadeau : inflige 120% ATK a l'ennemi avec la plus haute attaque, applique Brulure (8% PV/tour) 2 tours, 25% de chance d'etourdir 1 tour."); }
}
