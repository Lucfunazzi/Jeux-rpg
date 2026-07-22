package Personnage.FairyTail;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;
public class perso_Mirajane extends PersonnageBase {

    public perso_Mirajane() {
        this.nom = "Mirajane";
        this.type="Elementaliste";
        this.role = "DPS";
        this.rarete = "S";
        this.niveau = 1;
        double multiplicateurRarete = 1.50;
        this.vie = 650 * multiplicateurRarete;       // solide mais pas hors norme
        this.attaque = 260 * multiplicateurRarete;   // DPS S, légèrement sous Sasuke
        this.defense = 100 * multiplicateurRarete;   // raisonnable, pas un Tank
        this.vitesse = 140 * multiplicateurRarete;
        this.taux_critiques = 0.20;
        this.degat_critiques = 1.40;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.10;
        this.taux_blocage = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Attaque du demon", "Devoreur d'ame", "Tourbillon du mal"};
    }

    
    @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Mirajane utilise Attaque du demon !");
    boolean touche = Combat.attaquer(this, cible, log);
    if (!touche) {
        this.ajouterRage(50);
    }
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Mirajane utilise Devoreur d'ame !");
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = this.getAttaque() * 1.40;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Malediction(2, 0.10), log);
        }
    }
    Combat.appliquerEffet(this, new BuffTauxCritique(0.10, 2), log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Mirajane utilise Tourbillon du mal !");
    double multiplicateurRage = 1.0;
    if (this.getRage() > 100) {
        multiplicateurRage += (this.getRage() - 100) / 100.0;
    }
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = (this.getAttaque() * 1.60) * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Paralysie(2, 0.40), log);
        }
    }
    Combat.appliquerEffet(this, new Absorption(2, 0.15), log);
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Attaque du demon — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Devoreur d'ame — inflige 140% ATK a tous les ennemis, "
                + "applique Malediction (soins reduits de 10%) pendant 2 tours, "
                + "gagne 10% de taux critique pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Tourbillon du mal — inflige 160% ATK a tous les ennemis, "
                + "paralyse pendant 2 tours (40% de chance de liberation), "
                + "applique Absorption (15% vol de vie) pendant 2 tours.");
    }
}