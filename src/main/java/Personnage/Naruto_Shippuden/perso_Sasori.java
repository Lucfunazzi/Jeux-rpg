package Personnage.Naruto_Shippuden;

import Combat.Combat;
import Effets.Poison;
import Effets.Paralysie;
import Effets.ReductionDefense;
import Effets.ReductionAttaque;
import Effets.BuffVitesse;
import Personnage.PersonnageBase;
import java.util.List;

public class perso_Sasori extends PersonnageBase {

    public perso_Sasori() {
        this.nom = "Sasori";
        this.type = "Ninja";
        this.role = "Support";
        this.rarete = "A";
        this.niveau = 1;
        double multiplicateurRarete = 1.40;
        this.vie = 510 * multiplicateurRarete;
        this.attaque = 160 * multiplicateurRarete;
        this.defense = 90 * multiplicateurRarete;
        this.vitesse = 145 * multiplicateurRarete;
        this.taux_critiques = 0.15;
        this.degat_critiques = 1.30;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.10;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Fil empoisonne", "Marionnette du Scorpion", "Hiruko"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sasori utilise Fil empoisonne !");
        double degats = this.getAttaque() * 0.90;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Poison garanti — prepare les cibles pour Deidara
        Combat.appliquerEffet(this, cible, new Poison(2, 0.06), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sasori utilise Marionnette du Scorpion !");
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Poison + Paralysie sur la cible principale
        Combat.appliquerEffet(this, cible, new Poison(3, 0.08), log);
        if (Math.random() < 0.50) {
            Combat.appliquerEffet(this, cible, new Paralysie(1,0.30), log);
        }
        // Reduit la DEF de toute l'equipe ennemie (Sasori controle le terrain)
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && ennemi != cible) {
                Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.10, 2), log);
            }
        }
        // Synergie : si Deidara est allie, Sasori lui booste la vitesse pour frapper en premier
        for (PersonnageBase allie : equipeAlliee) {
            if (allie instanceof perso_Deidara && allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new BuffVitesse(0.25, 2), log);
                log.add("Synergie Akatsuki : Sasori accelere Deidara pour l'explosion !");
                break;
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sasori utilise Hiruko !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        // Poison AoE + ReductionATK sur toute l'equipe ennemie
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                Combat.appliquerEffet(this, ennemi, new Poison(3, 0.10), log);
                Combat.appliquerEffet(this, ennemi, new ReductionAttaque(0.20, 3), log);
                Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.15, 3), log);
            }
        }
        // Frappe lourde sur la cible la plus dangereuse (plus haute ATK)
        PersonnageBase cible = equipeEnnemie.stream()
                .filter(PersonnageBase::estVivant)
                .max(java.util.Comparator.comparingDouble(PersonnageBase::getAttaque))
                .orElse(null);
        if (cible == null) return;
        double degats = (this.getAttaque() * 1.60) * multiplicateurRage;
        log.add("Sasori concentre son poison sur " + cible.getNom() + " !");
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Fil empoisonne — Inflige 90% ATK a la cible. "
                + "Applique Poison (6% PV max/tour) pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Marionnette du Scorpion — Inflige 120% ATK a la cible. "
                + "Applique Poison (8% PV max/tour) pendant 3 tours. "
                + "50% de chance de Paralysie pendant 1 tour. "
                + "Reduit la DEF de tous les autres ennemis de 10% pendant 2 tours. "
                + "Si Deidara est allie : lui donne +25% VIT pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Hiruko — Applique Poison (10% PV max/tour), -20% ATK et -15% DEF "
                + "a tous les ennemis pendant 3 tours. "
                + "Inflige 160% ATK a l'ennemi avec la plus haute ATK. "
                + "Puissance augmentee par la Rage.");
    }
}