package Personnage.Naruto_Shippuden;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Itachi extends PersonnageBase {

    public perso_Itachi() {
        this.nom = "Itachi";
        this.type = "Ninja";
        this.role = "DPS";
        this.rarete = "S";
        this.niveau = 1;
        double multiplicateurRarete = 1.50;
        this.vie = 500 * multiplicateurRarete;
        this.attaque = 260 * multiplicateurRarete;
        this.defense = 80 * multiplicateurRarete;
        this.vitesse = 160 * multiplicateurRarete;
        this.taux_critiques = 0.18;
        this.degat_critiques = 1.35;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.12;
        this.taux_blocage = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Kunai des Ombres", "Genjutsu : Sharingan", "Tsukuyomi"};
    }

    // Attaque de base — 100% ATK + 15% de chance de Sommeil 1 tour
    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Itachi utilise Kunai des Ombres !");
        Combat.attaquer(this, cible, log);
        if (Math.random() < 0.15) {
            Combat.appliquerEffet(this, cible, new Sommeil(1), log);
        }
    }

    // Spéciale — 120% ATK sur l'ennemi avec la plus haute ATK + Silence 2 tours + ReductionAttaque 20%
    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Itachi utilise Genjutsu : Sharingan !");

        PersonnageBase cibleGenjutsu = null;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                if (cibleGenjutsu == null || ennemi.getAttaque() > cibleGenjutsu.getAttaque())
                    cibleGenjutsu = ennemi;
            }
        }
        if (cibleGenjutsu == null) return;

        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cibleGenjutsu, degats, log);
        Combat.appliquerEffet(this, cibleGenjutsu, new Silence(2), log);
        Combat.appliquerEffet(this, cibleGenjutsu, new ReductionAttaque(0.20, 2), log);
    }

    // Ultime — 180% ATK sur l'ennemi avec la plus haute ATK + Sommeil 2 tours
    //        + ReductionDefense 20% 3 tours + synergie Sasuke : +20 rage
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Itachi utilise Tsukuyomi !");

        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }

        PersonnageBase cible = null;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                if (cible == null || ennemi.getAttaque() > cible.getAttaque())
                    cible = ennemi;
            }
        }
        if (cible == null) return;

        double degats = (this.getAttaque() * 1.80) * multiplicateurRage;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Sommeil(2), log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.20, 3), log);

        // Synergie Sasuke
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.getNom().equals("Sasuke") && allie.estVivant()) {
                allie.ajouterRage(20);
                log.add("Sasuke gagne 20 points de rage (synergie Itachi) !");
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Kunai des Ombres : inflige 100% ATK a la cible, "
                + "15% de chance d'endormir la cible pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Genjutsu Sharingan : inflige 120% ATK a l'ennemi avec la plus haute attaque,\n"
                + "applique Silence pendant 2 tours et reduit son attaque de 20% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Tsukuyomi : inflige 180% ATK a l'ennemi avec la plus haute attaque,\n"
                + "endort la cible pendant 2 tours, reduit sa defense de 20% pendant 3 tours.\n"
                + "Si Sasuke est dans l'equipe, il gagne 20 points de rage.");
    }
}