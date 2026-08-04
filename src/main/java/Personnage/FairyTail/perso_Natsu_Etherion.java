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
        this.defense = 138 * multiplicateurRarete;    // un peu plus solide
        this.vitesse = 132 * multiplicateurRarete;
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
    boolean touche = Combat.attaquer(this, cible, log);
    if (!touche) {
        this.ajouterRage(50);
    }
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Natsu utilise Ultra Poings d'acier du dragon de feu !");
    double degats = this.getAttaque() * 1.60;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, cible, new Brulure(3, 0.15), log);
    if (Math.random() < 0.40) {
        Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
    }
    Combat.appliquerEffet(this, new BuffAttaque(0.20, 2), log);
    // Rage bonus : avec les +50 (ou +100 critique) de l'attaque de base precedente,
    // Natsu est directement pret pour son ultime au tour suivant.
    this.ajouterRage(50);
    log.add("[RAGE] Natsu : " + String.format("%.0f", this.getRage()) + "/100");
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Natsu utilise Hurlement suprême du dragon de feu !");
    Combat.appliquerEffet(this, new BuffAttaque(0.20, 2), log);
    double degats = this.getAttaque() * 1.80;
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Brulure(3, 0.18), log);
        }
    }
}
    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Ultra poings du dragon de feu — inflige 100% ATK a une cible.");
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
    
    @Override
protected double rageApresUltime() { return 50; }

/** Les "attaquants" ennemis vivants (role DPS) — cible des competences de Natsu. */
private List<PersonnageBase> ciblerAttaquants(List<PersonnageBase> equipeEnnemie) {
    List<PersonnageBase> attaquants = new ArrayList<>();
    for (PersonnageBase p : equipeEnnemie) {
        if (p.estVivant() && p.getRole().equals("DPS")) attaquants.add(p);
    }
    return attaquants;
}
}