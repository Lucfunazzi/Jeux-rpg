package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiC17C3 extends PersonnageBase {

    public EnnemiC17C3() { this(36); }

    public EnnemiC17C3(int niveau) {
        this.nom    = "C-17";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "DPS";
        this.rarete = "S";

        double mult = 1.5;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 600.0 * mult * niv;
        this.attaque = 200.0 * mult * niv;
        this.defense = 120.0 * mult * niv;
        this.vitesse = 110.0 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.4;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.1;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.1;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Kikoha", "Boule d'energie a pleine puissance", "Flash Photonique"};
    }

    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("C-17 utilise Kikoha sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("C-17 utilise Boule d'energie a pleine puissance !");
        double degats = this.getAttaque() * 1.60;
        boolean auMoinsUneCible = false;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && ennemi.getRole().equalsIgnoreCase("DPS")) {
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
        this.ajouterRage(30);
        log.add("C-17 gagne 30 points de rage !");
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.getNom().equals("C-18") && allie.estVivant()) {
                allie.ajouterRage(20);
                log.add("C-18 gagne 20 points de rage (synergie) !");
            }
        }
    }

    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("C-17 utilise Flash Photonique !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.60) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (ennemi.getRole().equalsIgnoreCase("DPS")) {
                    Combat.appliquerEffet(this, ennemi, new ReductionVitesse(0.15, 2), log);
                }
            }
        }
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant() && allie.getRole().equalsIgnoreCase("DPS")) {
                Combat.appliquerEffet(this, allie, new BuffAttaque(0.20, 2), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase()     { System.out.println("Kikoha : inflige 100% ATK a une cible."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Boule d'energie a pleine puissance : inflige 160% ATK a tous les DPS ennemis, applique Marquage 2 tours et gagne 30 rage. Donne 20 rage a C-18 si alliee."); }
    @Override public void descriptionAttaqueUltime()   { System.out.println("Flash Photonique : inflige 160% ATK a tous les ennemis, ralentit les DPS ennemis de 15% et donne +20% ATK aux DPS allies pendant 2 tours."); }
}
