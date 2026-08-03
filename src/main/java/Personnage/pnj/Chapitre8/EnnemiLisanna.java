package Personnage.pnj.Chapitre8;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Lisanna — version examinatrice (Chapitre 8, stage 5 : examen de rang S,
 * duel contre Erza aux côtés de Jubia). Meme identite et memes attaques
 * que perso_Lisanna.
 */
public class EnnemiLisanna extends PersonnageBase {

    public EnnemiLisanna() { this(94); }

    public EnnemiLisanna(int niveau) {
        this.nom    = "Lisanna";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "B";

        double mult = 1.35;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 420.0 * mult * niv;
        this.attaque = 110.0 * mult * niv;
        this.defense = 105.0 * mult * niv;
        this.vitesse = 150.0 * mult * vit;

        this.taux_critiques    = 0.08;
        this.degat_critiques   = 1.15;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.06;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.12;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Griffe animale", "Soin du coeur", "Forme de colombe"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lisanna attaque avec ses griffes animales !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lisanna prodigue un soin du coeur à ses alliés !");
        for (PersonnageBase al : equipeAlliee) {
            if (al.estVivant()) {
                double soin = this.getAttaque() * 0.80;
                al.recevoirSoin(soin, log);
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lisanna prend sa forme de colombe et régénère l'équipe !");
        for (PersonnageBase al : equipeAlliee) {
            if (al.estVivant()) Combat.appliquerEffet(this, al, new Regeneration(0.08, 2), log);
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Griffe animale — 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Soin du coeur — soigne toute l'équipe de 80% ATK.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Forme de colombe — régénère toute l'équipe 2 tours (8% PV max/tour).");
    }
}
