package Personnage.pnj.Chapitre5;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Freed — version ennemie (Chapitre 5, stage 7). Meme identite et memes attaques
 * que perso_Freed, stats de base reprises telles quelles et mises a l'echelle par
 * niveau. Sa speciale soigne/protege aussi les mobs generiques qui l'accompagnent.
 */
public class EnnemiFreed extends PersonnageBase {

    public EnnemiFreed() { this(56); }

    public EnnemiFreed(int niveau) {
        this.nom    = "Freed";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "A";

        double mult = 1.40;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 500.0 * mult * niv;
        this.attaque = 160.0 * mult * niv;
        this.defense = 160.0 * mult * niv;
        this.vitesse = 140.0 * mult * vit;

        this.taux_critiques    = 0.08;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.06;
        this.taux_blocage      = 0.12;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;

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
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Freed utilise Runes protectrices !");
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new Bouclier(allie.getVieMax() * 0.15), log);
                Combat.appliquerEffet(this, allie, new BuffDefense(0.10, 2), log);
            }
        }
        Combat.appliquerEffet(this, new BuffDefense(0.15, 2), log);
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
                + "a toute l'equipe pendant 2 tours, +15% DEF pour Freed.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Runes de confinement — inflige 110% ATK a tous les ennemis (bonus selon la Rage), "
                + "reduit leur attaque de 15% pendant 3 tours, "
                + "30% de chance de Silence sur les DPS ennemis pendant 1 tour.");
    }
}
