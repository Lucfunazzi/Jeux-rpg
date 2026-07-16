package Personnage.Naruto_Shippuden;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Asuma extends PersonnageBase {

    public perso_Asuma() {
        this.nom = "Asuma";
        this.niveau = 1;
        this.type = "Ninja";
        this.role = "Support";
        this.rarete = "A";

        double multiplicateurRarete = 1.50;

        this.vie = 550 * multiplicateurRarete;
        this.attaque = 125 * multiplicateurRarete;
        this.defense = 115 * multiplicateurRarete;
        this.vitesse = 105 * multiplicateurRarete;

        this.taux_critiques = 0.10;
        this.degat_critiques = 1.25;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.10;
        this.taux_blocage = 0.10;
        this.reduction_blocage = 0.15;
        this.degats_renvoi = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Lames de Chakra", "Nuée de Cendres", "Style de Konoha : Tranche Tactique"};
    }

    // Attaque de base — 100% ATK + Saignement 5% pendant 2 tours
    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Asuma attaque avec ses Lames de Chakra !");
        boolean touche = Combat.attaqueTouche(this, cible);
        if (!touche) {
            log.add(cible.getNom() + " esquive !");
            this.ajouterRage(50);
            return;
        }
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Saignement(2, 0.05), log);
    }

    // Spéciale — 110% ATK de zone + Brûlure 5% pendant 2 tours
    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Asuma utilise Nuée de Cendres (Katon: Haisekisho) !");

        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.10;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new Brulure(2, 0.05), log);
            }
        }
    }

    // Ultime — Buff Attaque + Vitesse sur toute l'équipe pendant 2 tours
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Asuma dirige la stratégie : Style de Konoha - Tranche Tactique !");

        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new BuffAttaque(0.20, 2), log);
                Combat.appliquerEffet(this, allie, new BuffVitesse(0.20, 2), log);
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Lames de Chakra — inflige 100% ATK à une cible et lui inflige Saignement (5% PV) pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Nuée de Cendres — inflige 110% ATK à tous les ennemis vivants et leur applique Brûlure (5% PV) pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Tranche Tactique — Accorde un bonus de +20% d'Attaque et +20% de Vitesse à tous les alliés pendant 2 tours.");
    }
}