package Personnage.pnj.Chapitre2Elite;

import Combat.Combat;
import Effets.Brulure;
import Effets.Malediction;
import Effets.ReductionDefense;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class EnnemiSasukeElite extends PersonnageBase {

    public EnnemiSasukeElite() {
        this(36);
    }

    public EnnemiSasukeElite(int niveau) {
        this.nom    = "Sasuke";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "DPS";
        this.rarete = "S";

        double mult = 1.70;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 162.4 * mult * niv;
        this.attaque =  82.7 * mult * niv;
        this.defense =  26.6 * mult * niv;
        this.vitesse =  71.6 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.40;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Kusanagi", "Katon boule de feu supreme", "Amaterasu"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sasuke attaque avec Kusanagi !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Katon boule de feu supreme !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.35;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new Malediction(2, 0.10), log);
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Amaterasu !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.50) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new Brulure(3, 0.10), log);
                Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.15, 3), log);
            }
        }
        this.ajouterRage(20);
        log.add(this.getNom() + " regagne 20 points de rage !");
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.getNom().equals("Itachi") && allie.estVivant()) {
                allie.ajouterRage(20);
                log.add("Itachi gagne 20 points de rage (synergie Sasuke) !");
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Kusanagi : inflige 100% ATK a la cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Katon boule de feu supreme : inflige 135% ATK a tous les ennemis et applique Malediction (soins reduits de 10%) pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Amaterasu : inflige 150% ATK a tous les ennemis, applique Brulure (10% PV/tour) 3 tours, reduit leur defense de 15% pendant 3 tours, et redonne 20 rage a Sasuke (et a Itachi s'il est dans l'equipe).");
    }
}