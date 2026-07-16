package Personnage.FairyTail;

import Combat.Combat;
import Effets.Saignement;
import Effets.BuffDefense;
import Effets.Trempe;
import Effets.Etourdissement;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
public class perso_Lucy extends PersonnageBase {

    public perso_Lucy() {
        this.nom = "Lucy";
        this.type = "Mage";
        this.role = "Support";
        this.rarete = "A";
        this.niveau = 1;
        double multiplicateurRarete = 1.30;
        this.vie = 700 * multiplicateurRarete;
        this.attaque = 120 * multiplicateurRarete;
        this.defense = 90 * multiplicateurRarete;
        this.vitesse = 160 * multiplicateurRarete;
        this.taux_critiques = 0.08;
        this.degat_critiques = 1.15;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Lucy Kick", "Invocation Taurus", "Invocation Aquarius"};
    }

    @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Lucy attaque !");
    Combat.attaquer(this, cible, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Lucy invoque Taurus !");
    ArrayList<PersonnageBase> ennemisVivants = new ArrayList<>();
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) ennemisVivants.add(ennemi);
    }
    ennemisVivants.sort(Comparator.comparingDouble(PersonnageBase::getVie));
    int ciblesAttaquees = 0;
    for (PersonnageBase ennemi : ennemisVivants) {
        if (ciblesAttaquees < 2) {
            double degats = this.getAttaque() * 0.80;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Saignement(2, 0.02), log);
            ciblesAttaquees++;
        }
    }
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant()) {
            Combat.appliquerEffet(this, allie, new BuffDefense(0.10, 3), log);
        }
    }
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Lucy invoque Aquarius !");
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = this.getAttaque() * 1.05;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Trempe(3), log);
            if (Math.random() < 0.40) {
                Combat.appliquerEffet(this, ennemi, new Etourdissement(1), log);
            }
        }
    }
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Lucy Kick : inflige 100% de degats a la cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Invocation Taurus : inflige 80% de degats aux 2 ennemis ayant le moins de PV,\n"
                + "applique Saignement (2% PV/tour) pendant 2 tours,\n"
                + "et augmente la defense de toute l'equipe de 10% pendant 3 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Invocation Aquarius : inflige 105% de degats a tous les ennemis,\n"
                + "les trempe pendant 3 tours,\n"
                + "et a 40% de chance de les etourdir pendant 1 tour.");
    }
}