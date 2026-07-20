package Personnage.pnj.Chapitre2;

import Personnage.pnj.Chapitre1.*;
import Personnage.PersonnageBase;
import Combat.Combat;
import java.util.ArrayList;
import java.util.List;

public class EnnemiSoigneur1 extends PersonnageBase {

    public EnnemiSoigneur1() {
        this(12);
    }

    public EnnemiSoigneur1(int niveau) {
        this.nom    = "Clerc des Ruines";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "Support";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 235.0 * niv;
        this.attaque =  70.0 * niv;
        this.defense =  40.0 * niv;
        this.vitesse =  78.0 * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.06;
        this.taux_blocage      = 0.02;
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
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " lance une vague de soins sur toute l'equipe !");
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                double soin = this.getAttaque() * 0.80;
                allie.recevoirSoin(soin, log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Sort Faible", "Soin Cible", "Vague de Soins"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Sort Faible : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Soin Cible : soigne l'allie le plus bas en PV a 100% ATK.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Vague de Soins : soigne toute l'equipe a 80% ATK.");
    }
}
