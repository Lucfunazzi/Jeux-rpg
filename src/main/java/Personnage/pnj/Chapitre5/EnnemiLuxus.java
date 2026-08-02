package Personnage.pnj.Chapitre5;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Luxus — version ennemie (boss recurrent Chapitre 5, stages 8/9/10).
 * Meme identite que perso_Luxus, mais pourcentages et effets adoucis : la version
 * joueur (SS, rarete 1.75) laisserait le joueur totalement impuissant en boss.
 */
public class EnnemiLuxus extends PersonnageBase {

    public EnnemiLuxus() { this(55); }

    public EnnemiLuxus(int niveau) {
        this.nom    = "Luxus";
        this.niveau = niveau;
        this.type   = "ChasseurDeDragon";
        this.role   = "DPS";
        this.rarete = "S";

        double mult = 1.60;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 700.0 * mult * niv;
        this.attaque = 400.0 * mult * niv;
        this.defense = 160.0 * mult * niv;
        this.vitesse = 200.0 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.40;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Rugissement du Dragon de Foudre", "Poing Destructeur du Dragon de Foudre", "Palais du Tonnerre"};
    }

    // ── Attaque de base : 70% ATK a tous les ennemis (vs 100% pour perso_Luxus) ──
    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Luxus utilise Rugissement du Dragon de Foudre !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.70;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    // ── Attaque speciale : 130% ATK sur le Tank et les Supports (vs 250%), etourdissement a chance reduite ──
    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Luxus utilise Poing Destructeur du Dragon de Foudre !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && (ennemi.getRole().equals("Tank") || ennemi.getRole().equals("Support"))) {
                double degats = this.getAttaque() * 1.30;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche && Math.random() < 0.30) {
                    Combat.appliquerEffet(this, ennemi, new Etourdissement(1), log);
                }
            }
        }
    }

    // ── Attaque ultime : 130% ATK a tous les ennemis (vs 200%), paralysie plus courte et plus facile a briser ──
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Luxus utilise Palais du Tonnerre !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.30) * multiplicateurRage;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche) {
                    Combat.appliquerEffet(this, ennemi, new Paralysie(1, 0.65), log);
                }
            }
        }
        Combat.appliquerEffet(this, new BuffAttaque(0.10, 2), log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Rugissement du Dragon de Foudre — inflige 70% ATK a tous les ennemis.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Poing Destructeur du Dragon de Foudre — inflige 130% ATK au Tank et aux Supports ennemis, "
                + "30% de chance de les etourdir pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Palais du Tonnerre — inflige 130% ATK a tous les ennemis (bonus selon la Rage), "
                + "les paralyse pendant 1 tour (65% chance de liberation), "
                + "et Luxus gagne 10% d'attaque pendant 2 tours.");
    }
}
