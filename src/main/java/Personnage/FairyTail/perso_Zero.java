package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Zero — véritable identité et forme ultime de Brain, rang SS.
 * Magie des ténèbres capable d'effacer purement et simplement ce qu'elle touche.
 */
public class perso_Zero extends PersonnageBase {

    public perso_Zero() {
        this.nom    = "Zero";
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "SS";
        this.niveau = 1;
        double mult = 1.75;
        this.vie     = 700 * mult;
        this.attaque = 324 * mult;
        this.defense = 155 * mult;
        this.vitesse = 161 * mult;
        this.taux_critiques    = 0.18;
        this.degat_critiques   = 1.40;
        this.taux_precisions   = 110.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.12;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Souffle des Ténèbres", "Cauchemar", "Néant Absolu"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Zero enveloppe " + cible.getNom() + " d'un Souffle des Ténèbres !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Zero plonge " + cible.getNom() + " dans un Cauchemar sans fin !");
        double degats = this.getAttaque() * 1.40;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new Silence(2), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Zero ouvre le Néant Absolu — la lumière elle-même s'efface !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.10) * multiplicateurRage;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche) {
                    Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.20, 2), log);
                }
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Souffle des Ténèbres — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Cauchemar — Inflige 140% ATK et réduit la cible au silence pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Néant Absolu — Inflige 110% ATK (bonus selon la Rage) à tous les ennemis et réduit leur défense de 20% pendant 2 tours.");
    }
}
