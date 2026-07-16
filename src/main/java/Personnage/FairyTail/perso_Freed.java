package Personnage.FairyTail;

import Combat.Combat;
import Effets.BuffDefense;
import Effets.BuffAttaque;
import Effets.Bouclier;
import Effets.ReductionAttaque;
import Effets.Silence;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Freed extends PersonnageBase {

    public perso_Freed() {
        this.nom = "Freed";
        this.niveau = 1;
        this.type = "Mage";
        this.role = "Support";
        this.rarete = "A";
        double multiplicateurRarete = 1.40;
        this.vie = 500 * multiplicateurRarete;
        this.attaque = 160 * multiplicateurRarete;
        this.defense = 160 * multiplicateurRarete;
        this.vitesse = 140 * multiplicateurRarete;
        this.taux_critiques = 0.08;
        this.degat_critiques = 1.20;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.06;
        this.taux_blocage = 0.12;
        this.reduction_blocage = 0.15;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Tranchant des runes", "Runes protectrices", "Runes de confinement"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Freed utilise Tranchant des runes !");
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Synergie Equipe du Tonnerre : si Bickslow a touché cible ce tour
        // → Freed gagne +5% ATK (géré côté Bickslow)
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Freed pose les Runes protectrices !");

        // Bouclier sur toute l'équipe
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new Bouclier(allie.getVieMax() * 0.15), log);
                Combat.appliquerEffet(this, allie, new BuffDefense(0.10, 2), log);
            }
        }
        // Runes sur lui-même
        Combat.appliquerEffet(this, new BuffDefense(0.15, 2), log);

        // Synergie Equipe du Tonnerre : si Bickslow ou Evergreen vivants → DEF bonus
        boolean tonnerre = false;
        for (PersonnageBase allie : equipeAlliee) {
            if ((allie.getNom().equals("Bickslow") || allie.getNom().equals("Evergreen"))
                    && allie.estVivant()) {
                tonnerre = true;
                break;
            }
        }
        if (tonnerre) {
            Combat.appliquerEffet(this, new BuffDefense(0.05, 2), log);
            log.add("Synergie Equipe du Tonnerre : les runes de Freed se renforcent ! +5% DEF bonus.");
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Freed utilise Runes de confinement !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.10) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new ReductionAttaque(0.15, 3), log);
                if (ennemi.getRole().equalsIgnoreCase("DPS") && Math.random() < 0.30) {
                    Combat.appliquerEffet(this, ennemi, new Silence(1), log);
                }
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Tranchant des runes — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Runes protectrices — applique un bouclier (15% PV max) et +10% DEF "
                + "a toute l'equipe pendant 2 tours, +15% DEF pour Freed. "
                + "[Synergie Equipe du Tonnerre] Bickslow ou Evergreen vivant : +5% DEF bonus.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Runes de confinement — inflige 110% ATK a tous les ennemis, "
                + "reduit leur attaque de 15% pendant 3 tours, "
                + "30% de chance de Silence sur les DPS ennemis pendant 1 tour.");
    }
}
