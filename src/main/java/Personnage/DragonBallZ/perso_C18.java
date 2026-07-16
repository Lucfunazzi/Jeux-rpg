package Personnage.DragonBallZ;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_C18 extends PersonnageBase {

    public perso_C18() {
        this.nom = "C-18";
        this.type = "Guerrier";
        this.role = "Support";
        this.rarete = "S";
        this.niveau = 1;
        double multiplicateurRarete = 1.50;
        this.vie = 700 * multiplicateurRarete;
        this.attaque = 150 * multiplicateurRarete;
        this.defense = 90 * multiplicateurRarete;
        this.vitesse = 120 * multiplicateurRarete;
        this.taux_critiques = 0.15;
        this.degat_critiques = 1.30;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.05;
        this.taux_blocage = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Kikoha", "Vague d'energie a haute pression", "Balle cosmique"};
    }

    
    @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("C-18 utilise Kikoha !");
    Combat.attaquer(this, cible, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("C-18 utilise Vague d'energie a haute pression !");
    double degats = this.getAttaque() * 1.30;
    boolean auMoinsUneCible = false;
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant() && ennemi.getRole().equalsIgnoreCase("Support")) {
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Marquage(2, 0.20), log);
            auMoinsUneCible = true;
        }
    }
    if (!auMoinsUneCible) {
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new Marquage(2, 0.20), log);
            }
        }
    }
    PersonnageBase cibleSoin = null;
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant()) {
            if (cibleSoin == null || allie.getVie() < cibleSoin.getVie())
                cibleSoin = allie;
        }
    }
    if (cibleSoin != null) {
        double soin = this.getAttaque() * 0.60;
        cibleSoin.recevoirSoin(soin, log);    }
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.getNom().equals("C-17") && allie.estVivant()) {
            allie.ajouterRage(20);
            log.add("C-17 gagne 20 points de rage (synergie) !");
        }
    }
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("C-18 utilise Balle cosmique !");
    double multiplicateurRage = 1.0;
    if (this.getRage() > 100) {
        multiplicateurRage += (this.getRage() - 100) / 100.0;
    }
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = (this.getAttaque() * 1.40) * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            if (ennemi.getRole().equalsIgnoreCase("Support")) {
                Combat.appliquerEffet(this, ennemi, new ReductionVitesse(0.15, 2), log);
            }
        }
    }
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant()) {
            Combat.appliquerEffet(this, allie, new BuffDefense(0.20, 2), log);
        }
    }
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Kikoha — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Vague d'energie a haute pression — inflige 130% ATK a tous les Supports ennemis "
                + "(tous les ennemis si aucun Support), applique Marquage 2 tours "
                + "et soigne l'allie le plus bas de 60% ATK. Donne 20 rage a C-17 si il est allie.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Balle cosmique — inflige 140% ATK a tous les ennemis, "
                + "ralentit les Supports ennemis de 15% pendant 2 tours "
                + "et donne 20% de defense a toute l'equipe alliee pendant 2 tours.");
    }
}