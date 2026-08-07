package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Ichiya Vandalay Kotobuki — Elementaliste, rang B.
 * Meneur de Blue Pegasus. Magie du Parfum : renforce ses alliés en aspirant
 * des arômes puisés dans son propre corps. Allié de Fairy Tail dès l'arc Oración Seis.
 */
public class perso_Ichiya extends PersonnageBase {

    public perso_Ichiya() {
        this.nom    = "Ichiya";
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "A";
        this.niveau = 1;
        double mult = 1.40;
        this.vie     = 440 * mult;
        this.attaque = 126 * mult;
        this.defense = 126 * mult;
        this.vitesse = 115 * mult;
        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.06;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Parfum de Poing", "Parfum Max", "Orbe du Parfum Masculin"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ichiya frappe " + cible.getNom() + " d'un Parfum de Poing !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ichiya diffuse son Parfum Max sur toute l'équipe !");
        double soin = this.getAttaque() * 0.70;
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new BuffAttaque(0.15, 2), log);
                allie.recevoirSoin(soin, log);
            }
        }
        Purification.purifierEquipe(equipeAlliee, 2, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ichiya libère l'Orbe du Parfum Masculin — un homme, c'est du parfum !");
        double soin = this.getAttaque() * 1.30;
        PersonnageBase cibleSoin = Combat.cibleParRole(equipeAlliee, "Tank");
        if (cibleSoin == null) cibleSoin = Combat.cibleParRole(equipeAlliee, "DPS");
        if (cibleSoin != null) {
            cibleSoin.recevoirSoin(soin, log);
            Purification.purifier(cibleSoin, 3, log);
        }
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new BuffVitesse(0.15, 2), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Parfum de Poing — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Parfum Max — Augmente l'ATK de toute l'équipe de 15% et soigne l'equipe de 70% de l'attaque pendant 2 tours (purifie 2 effets négatifs sur chacun).");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Orbe du Parfum Masculin — Soigne l'allié le plus exposé de 130% ATK (purifie 3 effets négatifs) et augmente la vitesse de l'équipe de 15% pendant 2 tours.");
    }
}
