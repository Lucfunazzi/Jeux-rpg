package Personnage.pnj.Chapitre8;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Azuma — l'un des Sept Enfants de la Purgation de Grimoire Heart, rang S.
 * Magie du Grand Arbre : draine la force vitale de la nature environnante.
 * Apparaît au Chapitre 8 (stage 8) puis de nouveau au Chapitre 9.
 */
public class EnnemiAzuma extends PersonnageBase {

    public EnnemiAzuma() { this(98); }

    public EnnemiAzuma(int niveau) {
        this.nom    = "Azuma";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "Tank";
        this.rarete = "S";

        double mult = 1.65;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 720.0 * mult * niv;
        this.attaque = 200.0 * mult * niv;
        this.defense = 170.0 * mult * niv;
        this.vitesse = 110.0 * mult * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.12;
        this.reduction_blocage = 0.16;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Branche Perçante", "Arc du Grand Arbre", "Forêt Dévorante"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Azuma transperce " + cible.getNom() + " d'une Branche Perçante !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Azuma draine la vie de " + cible.getNom() + " avec l'Arc du Grand Arbre !");
        double degats = this.getAttaque() * 1.25;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) this.recevoirSoin(degats * 0.30, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Azuma déchaîne une Forêt Dévorante sur toute l'équipe ennemie !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.10) * multiplicateurRage;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche) {
                    Combat.appliquerEffet(this, ennemi, new ReductionVitesse(0.15, 2), log);
                }
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Branche Perçante — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Arc du Grand Arbre — Inflige 125% ATK et se soigne de 30% des dégâts infligés.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Forêt Dévorante — Inflige 110% ATK (bonus selon la Rage) à tous les ennemis et réduit leur vitesse de 15% pendant 2 tours.");
    }
}
