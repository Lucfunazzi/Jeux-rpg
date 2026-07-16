package Personnage.pnj.chapitre1Elite;

import Personnage.PersonnageBase;
import Combat.Combat;
import Effets.Fragilite;
import Effets.BuffDefense;
import java.util.ArrayList;
import java.util.List;

public class EnnemiSoigneur1Elite extends PersonnageBase {

    public EnnemiSoigneur1Elite() {
        this(14);
    }

    public EnnemiSoigneur1Elite(int niveau) {
        this.nom    = "Clerc des Ruines";
        this.niveau = niveau;
        this.type   = "Mage";
        this.role   = "Support";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 318.2 * niv;
        this.attaque =  63.6 * niv;
        this.defense =  63.6 * niv;
        this.vitesse = 136.2 * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.02;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " lance un sort faible sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        PersonnageBase cibleSoin = null;
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                if (cibleSoin == null || allie.getVie() < cibleSoin.getVie())
                    cibleSoin = allie;
            }
        }
        if (cibleSoin != null) {
            double soin = this.getAttaque() * 1.00;
            cibleSoin.recevoirSoin(soin, log);
            log.add(this.nom + " soigne " + cibleSoin.getNom()
                    + " de " + String.format("%.1f", soin) + " PV !");
        }
        PersonnageBase cibleFragilite = null;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                if (cibleFragilite == null || ennemi.getVie() < cibleFragilite.getVie())
                    cibleFragilite = ennemi;
            }
        }
        if (cibleFragilite != null) {
            Combat.appliquerEffet(this, cibleFragilite, new Fragilite(2, 0.20), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " lance une vague de soins et renforce ses allies !");
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                double soin = this.getAttaque() * 0.80;
                allie.recevoirSoin(soin, log);
                log.add(allie.getNom() + " recoit " + String.format("%.1f", soin) + " PV !");
                Combat.appliquerEffet(this, allie, new BuffDefense(0.10, 2), log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Sort Faible", "Soin et Fragilisation", "Vague de Soins"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Sort Faible : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Soin et Fragilisation : soigne l'allie le plus bas a 100% ATK et applique Fragilite 2 tours sur l'ennemi le plus faible.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Vague de Soins : soigne toute l'equipe a 80% ATK et augmente leur DEF de 10% pendant 2 tours.");
    }
}