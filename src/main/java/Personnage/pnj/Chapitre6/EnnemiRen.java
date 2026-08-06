package Personnage.pnj.Chapitre6;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Ren Akatsuki — Elementaliste, rang A. Membre des Trimens de Blue Pegasus.
 * Magie de l'Air : tranche et perce le vent lui-même.
 */
public class EnnemiRen extends PersonnageBase {

    public EnnemiRen() { this(36); }

    public EnnemiRen(int niveau) {
        this.nom    = "Ren";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "A";

        double mult = 1.45;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 400.0 * mult * niv;
        this.attaque = 180.0 * mult * niv;
        this.defense = 100.0 * mult * niv;
        this.vitesse = 135.0 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.06;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Lame de Vent", "Air Rondo", "Void"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ren tranche " + cible.getNom() + " d'une Lame de Vent !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ren enchaîne un Air Rondo sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ren ouvre le Void — le vide aspire tout l'air autour de la cible !");
        PersonnageBase cible = Combat.choisirCible(this, equipeEnnemie);
        if (cible != null) {
            double degats = this.getAttaque() * 2.20;
            boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
            if (touche) {
                Combat.appliquerEffet(this, cible, new ReductionVitesse(0.20, 2), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Lame de Vent — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Air Rondo — Inflige 150% ATK à une cible.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Void — Inflige 220% ATK à une cible et réduit sa vitesse de 20% pendant 2 tours.");
    }
}
