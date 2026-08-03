package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Hoteye (Richard) — Elementaliste, rang A.
 * Ancien membre d'Oración Seis, Magie de la Terre et du Sable.
 */
public class perso_Hoteye extends PersonnageBase {

    public perso_Hoteye() {
        this.nom    = "Hoteye";
        this.type   = "Elementaliste";
        this.role   = "Tank";
        this.rarete = "A";
        this.niveau = 1;
        double mult = 1.40;
        this.vie     = 500 * mult;
        this.attaque = 140 * mult;
        this.defense = 140 * mult;
        this.vitesse =  95 * mult;
        this.taux_critiques    = 0.08;
        this.degat_critiques   = 1.15;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.06;
        this.taux_blocage      = 0.14;
        this.reduction_blocage = 0.18;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Poing de Sable", "Doigt du Sable Dévorant", "Grande Muraille de Sable"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hoteye écrase " + cible.getNom() + " d'un Poing de Sable !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hoteye dévore la magie de " + cible.getNom() + " avec son Doigt du Sable !");
        double degats = this.getAttaque() * 1.15;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new ReductionAttaque(0.15, 2), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hoteye érige une Grande Muraille de Sable !");
        Combat.appliquerEffet(this, new BuffBlocage(0.20, 2), log);
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.80;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Poing de Sable — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Doigt du Sable Dévorant — Inflige 115% ATK et réduit l'ATK de la cible de 15% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Grande Muraille de Sable — Augmente son taux de blocage de 20% pendant 2 tours et inflige 80% ATK à tous les ennemis.");
    }
}
