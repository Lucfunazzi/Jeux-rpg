package Personnage.Naruto_Shippuden;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Shikamaru extends PersonnageBase {

    public perso_Shikamaru() {
        this.nom = "Shikamaru";
        this.niveau = 1;
        this.type = "Ninja";
        this.role = "Support";
        this.rarete = "A";

        double multiplicateurRarete = 1.40;
        this.vie     = 500 * multiplicateurRarete;
        this.attaque = 110 * multiplicateurRarete;
        this.defense = 100 * multiplicateurRarete;
        this.vitesse = 115 * multiplicateurRarete;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 110.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Manipulation des Ombres", "Entrave par l'Ombre", "Strategie du Maitre"};
    }

    // ── Attaque de base — 100% ATK + ReductionVitesse 5%
    // Synergie : si Choji vivant → dure 2 tours au lieu de 1
    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Shikamaru utilise Manipulation des Ombres !");
        Combat.attaquer(this, cible, log);

        boolean chojiVivant = false;
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.getNom().equals("Choji") && allie.estVivant()) {
                chojiVivant = true;
                break;
            }
        }

        int duree = chojiVivant ? 2 : 1;
        if (chojiVivant) {
            log.add("[Synergie Equipe 10] Shikamaru prolonge l'immobilisation grace a Choji ! (2 tours)");
        }
        Combat.appliquerEffet(this, cible, new ReductionVitesse(0.05, duree), log);
    }

    // ── Spéciale — 90% ATK + ReductionAttaque 15% 2 tours
    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Shikamaru utilise Entrave par l'Ombre !");
        double degats = this.getAttaque() * 0.90;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.15, 2), log);
    }

    // ── Ultime — BuffAttaque 15% + BuffPrecision 15 sur tous les alliés 2 tours
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Shikamaru utilise Strategie du Maitre !");
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new BuffAttaque(0.15, 2), log);
                Combat.appliquerEffet(this, allie, new BuffPrecision(15.00, 2), log);
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Manipulation des Ombres — inflige 100% ATK a une cible et reduit sa vitesse de 5% pendant 1 tour. "
                + "[Synergie Equipe 10] Si Choji est vivant : la reduction dure 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Entrave par l'Ombre — inflige 90% ATK a une cible et reduit son ATK de 15% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Strategie du Maitre — donne +15% ATK et +15 Precision a tous les allies pendant 2 tours.");
    }
}