package Personnage.FairyTail;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;
public class perso_Natsu extends PersonnageBase {

    public perso_Natsu() {
        this.nom = "Natsu";
        this.type = "ChasseurDeDragon";
        this.role = "DPS";
        this.rarete = "A";
        this.niveau = 1;
        double multiplicateurRarete = 1.40;
        this.vie = 550 * multiplicateurRarete;
        this.attaque = 200 * multiplicateurRarete;
        this.defense = 100 * multiplicateurRarete;
        this.vitesse = 100 * multiplicateurRarete;
        this.taux_critiques = 0.25;
        this.degat_critiques = 1.20;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.08;
        this.taux_blocage = 0.02;
        this.reduction_blocage = 0.02;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de poings", "Poings d'acier du dragon de feu",
                "Lotus pourpre du dragon de feu"};
    }

    
    @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Natsu utilise Coup de poings !");
    boolean touche = Combat.attaquer(this, cible, log);
    if (!touche) {
        this.ajouterRage(50);
    }
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Natsu utilise Poings d'acier du dragon de feu !");
    List<PersonnageBase> attaquants = ciblerAttaquants(equipeEnnemie);
    if (attaquants.isEmpty()) {
        log.add("Aucun attaquant ennemi a cibler !");
    } else {
        double degats = this.getAttaque() * 0.80;
        for (PersonnageBase ennemi : attaquants) {
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Brulure(2, 0.10), log);
        }
    }
    // Rage bonus : avec les +50 (ou +100 critique) de l'attaque de base precedente,
    // Natsu est directement pret pour son ultime au tour suivant.
    this.ajouterRage(50);
    log.add("[RAGE] Natsu : " + String.format("%.0f", this.getRage()) + "/100");
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Natsu utilise Lotus pourpre du dragon de feu !");
    List<PersonnageBase> attaquants = ciblerAttaquants(equipeEnnemie);
    if (attaquants.isEmpty()) {
        log.add("Aucun attaquant ennemi a cibler !");
    } else {
        double degats = this.getAttaque() * 1.05;
        for (PersonnageBase ennemi : attaquants) {
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
        }
    }
}

/** Rage conservee juste apres l'ultime : Natsu repart directement a 50 au lieu de 0. */
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

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Coup de poings — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Poings d'acier du dragon de feu — inflige 80% ATK a tous les attaquants ennemis "
                + "et applique Brulure (10% PV/tour) pendant 2 tours. Natsu gagne 50 rage.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Lotus pourpre du dragon de feu — inflige 105% ATK a tous les attaquants ennemis. "
                + "Natsu conserve 50 rage apres avoir declenche son ultime.");
    }
}