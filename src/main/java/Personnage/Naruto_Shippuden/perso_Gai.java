package Personnage.Naruto_Shippuden;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Gai extends PersonnageBase {

    public perso_Gai() {
        this.nom = "Gai";
        this.niveau = 1;
        this.type = "Ninja";
        this.role = "DPS";
        this.rarete = "S";

        double multiplicateurRarete = 1.60;

        this.vie = 620 * multiplicateurRarete;
        this.attaque = 150 * multiplicateurRarete;
        this.defense = 110 * multiplicateurRarete;
        this.vitesse = 140 * multiplicateurRarete;

        this.taux_critiques = 0.20;
        this.degat_critiques = 1.50;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.15;
        this.taux_blocage = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"La Bourrasque de Konoha", "La Tornade de Konoha", "Le Paon du Matin"};
    }

    // Attaque de base — 100% ATK + gagne 5% Taux Critique pendant 2 tours
    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gai utilise La Bourrasque de Konoha !");
        boolean touche = Combat.attaqueTouche(this, cible);
        if (!touche) {
            log.add(cible.getNom() + " esquive !");
            this.ajouterRage(50);
            return;
        }
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, new BuffTauxCritique(0.05, 2), log);
    }

    // Spéciale — 140% ATK + ReductionVitesse 20% pendant 2 tours
    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gai ouvre les Portes et lance La Tornade de Konoha !");
        double degats = this.getAttaque() * 1.40;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionVitesse(0.20, 2), log);
    }

    // Ultime — 180% ATK sur la cible la plus faible + contrecoup 10% PV + BuffAttaque 30%
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gai déclenche l'Ultime : LE PAON DU MATIN !");

        PersonnageBase cible = null;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                if (cible == null || ennemi.getVie() < cible.getVie()) {
                    cible = ennemi;
                }
            }
        }

        if (cible == null) return;

        double degats = this.getAttaque() * 1.80;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);

        // Contrecoup des Portes Célestes
        double contrecoup = this.getVie() * 0.10;
        this.setVie(Math.max(1, this.getVie() - contrecoup));
        log.add("Le contrecoup de la technique inflige " + String.format("%.1f", contrecoup) + " dégâts à Gai !");

        Combat.appliquerEffet(this, new BuffAttaque(0.30, 2), log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("La Bourrasque de Konoha — inflige 100% ATK à une cible et augmente le Taux Critique de Gai de 5% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("La Tornade de Konoha — inflige 140% ATK à une cible et réduit sa Vitesse de 20% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Le Paon du Matin — inflige 180% ATK à l'ennemi le plus faible. Gai perd 10% de ses PV actuels mais gagne +30% ATK pendant 2 tours.");
    }
}