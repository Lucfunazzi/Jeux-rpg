package Personnage.pnj.Chapitre2Elite;


import Personnage.PersonnageBase;
import Combat.Combat;
import Effets.*;
import java.util.ArrayList;
import java.util.List;

public class EnnemiMage8DPS extends PersonnageBase {

    public EnnemiMage8DPS() {
        this(24);
    }

    public EnnemiMage8DPS(int niveau) {
        this.nom    = "Chasseur de Neige";
        this.niveau = niveau;
        this.type="ChasseurDeDragon";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 350.0 * niv;
        this.attaque =  45.0 * niv;
        this.defense =  80.0 * niv;
        this.vitesse =  40.0 * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = 0.20;
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
        log.add(this.nom + " hurlement du dragon de neige " + cible.getNom() + " avec son arme !");
        double degats = this.getAttaque() * 1.40;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (Math.random() <0.20){
            Combat.appliquerEffet(cible, new Gel(2), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " Torrent de neige du dragon  !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 0.70;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);

            }
            if (Math.random() < 0.30){
                Combat.appliquerEffet(cible, new Gel(2), log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Coup de Bouclier", "Frappe Brutale", "Charge Devastatrice"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Coup de Bouclier : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Frappe Brutale : inflige 140% ATK a la cible.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Charge Devastatrice : inflige 120% ATK a toute l'equipe ennemie.");
    }
}
