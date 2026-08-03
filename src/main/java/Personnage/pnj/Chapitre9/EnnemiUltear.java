package Personnage.pnj.Chapitre9;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Ultear Milkovich — mage de Grimoire Heart, rang S.
 * Magie de l'Arc du Temps : accélère ou inverse le temps sur ce qu'elle touche.
 * Affronte Gray au Chapitre 9 (stage 9), avant de se révéler bien plus complexe
 * qu'une simple ennemie.
 */
public class EnnemiUltear extends PersonnageBase {

    public EnnemiUltear() { this(109); }

    public EnnemiUltear(int niveau) {
        this.nom    = "Ultear";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "S";

        double mult = 1.72;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 640.0 * mult * niv;
        this.attaque = 245.0 * mult * niv;
        this.defense = 140.0 * mult * niv;
        this.vitesse = 125.0 * mult * vit;

        this.taux_critiques    = 0.16;
        this.degat_critiques   = 1.35;
        this.taux_precisions   = 108.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.12;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Flèche Temporelle", "Marche Arrière", "Last Ages"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ultear transperce " + cible.getNom() + " d'une Flèche Temporelle !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ultear inflige une Marche Arrière à " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.30;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new ReductionAttaque(0.20, 2), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ultear déchaîne son interdit ultime — Last Ages !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.25) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Flèche Temporelle — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Marche Arrière — Inflige 130% ATK et réduit l'ATK de la cible de 20% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Last Ages — Inflige 125% ATK (bonus selon la Rage) à tous les ennemis.");
    }
}
