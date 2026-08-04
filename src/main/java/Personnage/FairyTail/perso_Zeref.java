package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Zeref Dragnir — Elementaliste, rang UR.
 * Le Mage Noir, maudit par Ankhseram. le tank Ultime
 */
public class perso_Zeref extends PersonnageBase {

    public perso_Zeref() {
        this.nom    = "Zeref";
        this.type   = "Elementaliste";
        this.role   = "Tank";
        this.rarete = "UR";
        this.niveau = 1;
        double mult = 3;
        this.vie     = 4500 * mult;
        this.attaque = 2500 * mult;
        this.defense = 1200 * mult;
        this.vitesse = 800 * mult;
        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.40;
        this.taux_precisions   = 112.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.16;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Toucher de la Mort", "Malédiction d'Ankhseram", "Fin de Toute Vie"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Zeref effleure toute l'équipe ennemie d'un Toucher de la Mort !");
        List<PersonnageBase> touches = new java.util.ArrayList<>();
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 2.00;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche) {
                    Combat.appliquerEffet(this, ennemi, new Malediction(2, 0.20), log);
                    touches.add(ennemi);
                }
            }
        }
        if (!touches.isEmpty()) {
            PersonnageBase cibleSilence = touches.get((int) (Math.random() * touches.size()));
            Combat.appliquerEffet(this, cibleSilence, new Silence(2), log);
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("La Malédiction d'Ankhseram s'abat sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 2.30;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new Malediction(2, 0.40), log);
        }
        Combat.appliquerEffet(this, new Regeneration(0.15, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Zeref déchaîne la Fin de Toute Vie sur toute l'équipe ennemie !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 2.50) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
        Combat.appliquerEffet(this, new Immunite(2), log);
        Purification.purifier(this, Integer.MAX_VALUE, log);
        Combat.appliquerEffet(this, new BuffDefense(0.15, 2), log);
        Combat.appliquerEffet(this, new BuffBlocage(0.15, 2), log);
        Combat.appliquerEffet(this, new BuffTauxEsquive(0.10, 2), log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Toucher de la Mort — Inflige 200% ATK à tout les ennemis et inflige malédiction 20% et inflige silence a 1 une des cibles aleatoirement");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Malédiction d'Ankhseram — Inflige 230% ATK les ennemis et réduit les soins reçus par la cible de 40% pendant 2 tours et applique regenaration de 15% pendants 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Fin de Toute Vie — Inflige 250% ATK (bonus selon la Rage) à tous les ennemis s'applique immunité et purification et Buff de defense de 15% de blocage de 15% d'esquive de 10%.");
    }
}
