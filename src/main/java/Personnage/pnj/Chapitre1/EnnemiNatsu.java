package Personnage.pnj.Chapitre1;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class EnnemiNatsu extends PersonnageBase {

    public EnnemiNatsu() {
        this(8);
    }

    public EnnemiNatsu(int niveau) {
        this.nom    = "Natsu";
        this.niveau = niveau;
        this.type   = "Mage";
        this.role   = "DPS";
        this.rarete = "A";

        // Rang A : multiplicateur 1.40
        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 370.0 * niv;
        this.attaque = 155.0 * niv;
        this.defense =  75.0 * niv;
        this.vitesse = 100.0 * vit;

        this.taux_critiques    = 0.25;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.02;
        this.reduction_blocage = 0.02;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Natsu utilise Coup de poings !");
        boolean touche = Combat.attaqueTouche(this, cible);
        if (!touche) {
            log.add(cible.getNom() + " esquive !");
            this.ajouterRage(50);
            return;
        }
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Brulure(1, 0.05), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Natsu utilise Poings d'acier du dragon de feu !");
        double degats = this.getAttaque() * 1.14;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (Math.random() < 0.30) {
            Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
        }
        Combat.appliquerEffet(this, cible, new Brulure(2, 0.10), log);
        Combat.appliquerEffet(this, new BuffAttaque(0.15, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Natsu utilise Lotus pourpre du dragon de feu !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.40) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new Brulure(3, 0.12), log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Coup de poings", "Poings d'acier du dragon de feu", "Lotus pourpre du dragon de feu"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Coup de poings : inflige 100% ATK a la cible, applique Brulure 1 tour si touche.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Poings d'acier : inflige 130% ATK, 30% chance d'etourdir 1 tour, applique Brulure (10% PV/tour) 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Lotus pourpre : inflige 160% ATK a tous les ennemis et applique Brulure (12% PV/tour) 3 tours.");
    }
}
