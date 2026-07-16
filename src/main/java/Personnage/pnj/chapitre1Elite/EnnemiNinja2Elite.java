package Personnage.pnj.chapitre1Elite;

import Personnage.PersonnageBase;
import Combat.Combat;
import Effets.Poison;
import Effets.ReductionDefense;
import java.util.ArrayList;
import java.util.List;

public class EnnemiNinja2Elite extends PersonnageBase {

    public EnnemiNinja2Elite() {
        this(16);
    }

    public EnnemiNinja2Elite(int niveau) {
        this.nom    = "Assassin Furtif";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 240.5 * niv;
        this.attaque = 120.3 * niv;
        this.defense =  28.9 * niv;
        this.vitesse =  96.3 * vit;

        this.taux_critiques    = 0.30;
        this.degat_critiques   = 1.50;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.15;
        this.taux_blocage      = 0.02;
        this.reduction_blocage = 0.02;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " frappe " + cible.getNom() + " dans l'ombre !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " surgit de l'ombre sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.70;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Poison(2, 0.04), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " disparait et frappe tous les ennemis !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.20;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new ReductionDefense(0.10, 2), log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Frappe de l'Ombre", "Surgissement", "Disparition"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Frappe de l'Ombre : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Surgissement : inflige 170% ATK a la cible et applique Poison (4% PV max/tour) 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Disparition : inflige 120% ATK a toute l'equipe ennemie et reduit leur DEF de 10% pendant 2 tours.");
    }
}