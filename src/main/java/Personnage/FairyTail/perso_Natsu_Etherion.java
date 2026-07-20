package Personnage.FairyTail;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;
public class perso_Natsu_Etherion extends PersonnageBase {

    public perso_Natsu_Etherion() {
        this.nom = "Natsu Etherion";
        this.type = "ChasseurDeDragon";
        this.role = "DPS";
        this.rarete = "S";
        this.niveau = 1;
        double multiplicateurRarete = 1.50;
        this.vie = 700 * multiplicateurRarete;        // évolution de 550, cohérent
        this.attaque = 300 * multiplicateurRarete;    // nettement au-dessus de Natsu A
        this.defense = 120 * multiplicateurRarete;    // un peu plus solide
        this.vitesse = 115 * multiplicateurRarete;
        this.taux_critiques = 0.30;
        this.degat_critiques = 1.50;
        this.taux_precisions = 110.00;
        this.taux_esquives = 0.10;
        this.taux_blocage = 0.03;
        this.reduction_blocage = 0.03;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{
            "Ultra poings du dragon de feu",
            "Ailes du dragon de feu",
            "Mode Dragon : Hurlement supreme du dragon de feu"
        };
    }

    
   @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Natsu Etherion utilise Ultra poings du dragon de feu !");
    boolean touche = Combat.attaqueTouche(this, cible);
    if (!touche) {
        log.add(cible.getNom() + " esquive !");
        this.ajouterRage(50);
        return;
    }
    Combat.attaquer(this, cible, log);
    Combat.appliquerEffet(this, cible, new Brulure(1, 0.08), log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Natsu Etherion utilise Ailes du dragon de feu !");
    double degats = this.getAttaque() * 1.60;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, cible, new Brulure(3, 0.15), log);
    if (Math.random() < 0.40) {
        Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
    }
    Combat.appliquerEffet(this, new BuffAttaque(0.20, 2), log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Natsu Etherion utilise Mode Dragon : Hurlement supreme !");
    double multiplicateurRage = 1.0;
    if (this.getRage() > 100) {
        multiplicateurRage += (this.getRage() - 100) / 100.0;
    }
    Combat.appliquerEffet(this, new BuffAttaque(0.20, 2), log);
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = (this.getAttaque() * 1.80) * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Brulure(3, 0.18), log);
        }
    }
}
    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Ultra poings du dragon de feu — inflige 100% ATK a une cible, "
                + "applique Brulure (8% PV) pendant 1 tour si elle touche.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Ailes du dragon de feu — inflige 160% ATK a une cible, "
                + "applique Brulure (15% PV/tour) pendant 3 tours, "
                + "40% de chance d'etourdir pendant 1 tour, "
                + "gagne 20% d'attaque pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Mode Dragon Hurlement supreme — gagne 20% d'attaque pendant 2 tours, "
                + "inflige 180% ATK a tous les ennemis, "
                + "applique Brulure intense (18% PV/tour) pendant 3 tours.");
    }
}