package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Azuma — Elementaliste, rang S.
 * Ancien des Sept Enfants de la Purgation de Grimoire Heart, Magie du Grand Arbre.
 */
public class perso_Azuma extends PersonnageBase {

    public perso_Azuma() {
        this.nom    = "Azuma";
        this.type   = "Elementaliste";
        this.role   = "Tank";
        this.rarete = "S";
        this.niveau = 1;
        double mult = 1.72;
        this.vie     = 805 * mult;
        this.attaque = 185 * mult;
        this.defense = 184 * mult;
        this.vitesse = 110 * mult;
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
        log.add("Azuma transperce l'équipe ennemie d'une Branche Perçante !");
        List<PersonnageBase> vivants = new java.util.ArrayList<>();
        for (PersonnageBase ennemi : equipeEnnemie) if (ennemi.estVivant()) vivants.add(ennemi);
        java.util.Collections.shuffle(vivants);
        for (int i = 0; i < Math.min(3, vivants.size()); i++) {
            double degats = this.getAttaque() * 1.25;
            Combat.appliquerDegatsAvecLog(this, vivants.get(i), degats, log);
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Azuma draine la vie de l'équipe ennemie avec l'Arc du Grand Arbre !");
        double degatsTotaux = 0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.25;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche) degatsTotaux += degats;
            }
        }
        if (degatsTotaux > 0) this.recevoirSoin(degatsTotaux * 0.30, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Azuma déchaîne une Forêt Dévorante sur toute l'équipe ennemie !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.50) * multiplicateurRage;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche) {
                    Combat.appliquerEffet(this, ennemi, new Ralentissement(2, 0.15), log);
                }
            }
        }
        Combat.appliquerEffet(this, new BuffDefense(0.15, 2), log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Branche Perçante — Inflige 125% ATK a 3 ennemis aleatoire.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Arc du Grand Arbre — Inflige 125% ATK à tout les ennemis et se soigne de 30% des dégâts infligés.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Forêt Dévorante — Inflige 150% ATK (bonus selon la Rage) à tous les ennemis et inflige ralentissement et sa defense de 15% pendant 2 tours.");
    }
}
