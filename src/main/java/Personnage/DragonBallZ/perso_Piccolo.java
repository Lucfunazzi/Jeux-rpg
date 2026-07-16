package Personnage.DragonBallZ;

import Combat.Combat;
import Effets.Silence;
import Effets.ReductionDefense;
import Effets.BuffDefense;
import Effets.Regeneration;
import Personnage.PersonnageBase;
import java.util.List;

public class perso_Piccolo extends PersonnageBase {

    public perso_Piccolo() {
        this.nom = "Piccolo";
        this.type = "Guerrier";
        this.role = "Support";
        this.rarete = "A";
        this.niveau = 1;
        double multiplicateurRarete = 1.40;
        this.vie = 560 * multiplicateurRarete;
        this.attaque = 180 * multiplicateurRarete;
        this.defense = 130 * multiplicateurRarete;
        this.vitesse = 115 * multiplicateurRarete;
        this.taux_critiques = 0.12;
        this.degat_critiques = 1.30;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.08;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Rayon Makankosappo", "Canon Special Beam", "Explosion Namek"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Piccolo utilise Rayon Makankosappo !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Regeneration Namek sur Piccolo
        Combat.appliquerEffet(this, this, new Regeneration(0.05, 2), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Piccolo utilise Canon Special Beam !");
        double degats = this.getAttaque() * 1.60;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Percee la defense + silence (attaque de precision Namek)
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.25, 2), log);
        if (Math.random() < 0.40) {
            Combat.appliquerEffet(this, cible, new Silence(1), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Piccolo utilise Explosion Namek !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        // Degats AoE moderes
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.30) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.15, 2), log);
            }
        }
        // Buff DEF sur toute l'equipe (Piccolo protege ses allies)
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new BuffDefense(0.20, 2), log);
            }
        }
        // Regeneration sur lui-meme
        Combat.appliquerEffet(this, this, new Regeneration(0.08, 3), log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Rayon Makankosappo — Inflige 110% ATK a la cible. "
                + "Piccolo se regenere de 5% PV max pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Canon Special Beam — Inflige 160% ATK a la cible. "
                + "Reduit sa DEF de 25% pendant 2 tours. "
                + "40% de chance de Silence pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Explosion Namek — Inflige 130% ATK a tous les ennemis. "
                + "Reduit leur DEF de 15% pendant 2 tours. "
                + "Toute l'equipe gagne +20% DEF pendant 2 tours. "
                + "Piccolo se regenere de 8% PV max pendant 3 tours. "
                + "Puissance augmentee par la Rage.");
    }
}