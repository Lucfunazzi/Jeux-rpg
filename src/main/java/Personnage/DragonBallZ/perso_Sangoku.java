package Personnage.DragonBallZ;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Sangoku extends PersonnageBase {

    public perso_Sangoku() {
        this.nom = "Sangoku";
        this.type = "Guerrier";
        this.role = "DPS";
        this.rarete = "A";
        this.niveau = 1;
        double multiplicateurRarete = 1.40;
        this.vie = 550 * multiplicateurRarete;
        this.attaque = 200 * multiplicateurRarete;   // ramené à niveau rang A cohérent
        this.defense = 60 * multiplicateurRarete;
        this.vitesse = 130 * multiplicateurRarete;
        this.taux_critiques = 0.15;
        this.degat_critiques = 1.30;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.08;
        this.taux_blocage = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de poing", "Kamehameha", "Genkidama"};
    }

    
    @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Sangoku utilise Coup de poing !");
    Combat.attaquer(this, cible, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Sangoku charge son Kamehameha !");
    PersonnageBase cibleKameh = null;
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            if (cibleKameh == null || ennemi.getAttaque() > cibleKameh.getAttaque())
                cibleKameh = ennemi;
        }
    }
    if (cibleKameh == null) return;
    double degats = this.getAttaque() * 1.30;
    Combat.appliquerDegatsAvecLog(this, cibleKameh, degats, log);
    Combat.appliquerEffet(this, cibleKameh, new Marquage(2, 0.15), log);
    this.ajouterRage(20);
    log.add(this.getNom() + " recupere 20 points de rage !");
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Sangoku invoque la Genkidama !");
    double multiplicateurRage = 1.0;
    if (this.getRage() > 100) {
        multiplicateurRage += (this.getRage() - 100) / 100.0;
    }
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = (this.getAttaque() * 1.40) * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            if (ennemi.getRole().equalsIgnoreCase("DPS")) {
                Combat.appliquerEffet(this, ennemi, new Silence(1), log);
            }
        }
    }
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant() && allie.getRole().equalsIgnoreCase("DPS")) {
            allie.ajouterRage(15);
        }
    }
    log.add("Tous les DPS allies gagnent 15 points de rage !");
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Coup de poing — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Kamehameha — inflige 130% ATK a l'ennemi avec la plus haute attaque, "
                + "lui applique Marquage (+15% degats recus) pendant 2 tours "
                + "et recupere 20 points de rage.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Genkidama — inflige 140% ATK a tous les ennemis, "
                + "reduit au silence les DPS ennemis pendant 1 tour "
                + "et donne 15 points de rage a tous les DPS allies.");
    }
}