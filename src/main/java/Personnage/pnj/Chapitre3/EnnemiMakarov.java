package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.ReductionDefense;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Makarov Dreyar — Maître de la guilde Fairy Tail, mage élémentaire de rang SS.
 * Utilisée uniquement pour le combat scripté du Chapitre 3, stage 9
 * ("Makarov contre José — La Loi des Fées") : la formation du joueur n'intervient
 * pas, seul Makarov affronte José. Son ultime, la Loi des Fées (Fairy Law),
 * élimine instantanément quiconque lui fait face, quels que soient ses PV ou sa
 * défense — fidèle à l'anime. Voir Chapitre3.lancerStage9AvecMakarov.
 */
public class EnnemiMakarov extends PersonnageBase {

    public EnnemiMakarov() {
        this.nom    = "Makarov";
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "SS";
        this.niveau = 1;

        // Stats demesurees : maitre de guilde au sommet de sa puissance.
        this.vie     = 8_000.0;
        this.attaque = 3_000.0;
        this.defense = 1_200.0;
        this.vitesse = 250.0;

        this.taux_critiques    = 0.25;
        this.degat_critiques   = 1.60;
        this.taux_precisions   = 120.00;
        this.taux_esquives     = 0.15;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.20;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Poigne du Titan", "Magie Titan — Ecrasement", "La Loi des Fées (Fairy Law)"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Makarov agrandit son bras et frappe " + cible.getNom() + " de sa Poigne du Titan !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Makarov se transforme en Titan et ecrase " + cible.getNom() + " de tout son poids !");
        double degats = this.getAttaque() * 1.60;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.30, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Makarov rassemble toute sa magie et invoque la Loi des Fées (Fairy Law) !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                log.add(ennemi.getNom() + " est efface d'un coup, incapable de resister a la volonte du maitre de guilde !");
                ennemi.retirerVie(ennemi.getVie(), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Poigne du Titan — inflige 100% ATK a la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Magie Titan — Ecrasement — inflige 160% ATK, reduit la DEF de la cible de 30% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("La Loi des Fées (Fairy Law) — elimine instantanement tous les ennemis, quels que soient leurs PV ou leur defense.");
    }
}
