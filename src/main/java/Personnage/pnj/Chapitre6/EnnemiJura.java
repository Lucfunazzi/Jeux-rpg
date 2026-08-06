package Personnage.pnj.Chapitre6;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Jura Neekis — Chevalier, rang SS. As de Lamia Scale, l'un des Dix Mages Saints.
 * Magie de la Terre : peut manier et ecraser la roche a volonte.
 */
public class EnnemiJura extends PersonnageBase {

    public EnnemiJura() { this(38); }

    public EnnemiJura(int niveau) {
        this.nom    = "Jura";
        this.niveau = niveau;
        this.type   = "Chevalier";
        this.role   = "Tank";
        this.rarete = "SS";

        double mult = 1.75;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 850.0 * mult * niv;
        this.attaque = 350.0 * mult * niv;
        this.defense = 280.0 * mult * niv;
        this.vitesse = 170.0 * mult * vit;

        this.taux_critiques    = 0.08;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.16;
        this.reduction_blocage = 0.20;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Poing de Roc", "Montagne", "Grondement du Mont Fuji"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Jura écrase " + cible.getNom() + " d'un Poing de Roc !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Jura invoque la Montagne sur toute l'équipe ennemie !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.55;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche && Math.random() < 0.25) {
                    Combat.appliquerEffet(this, ennemi, new Etourdissement(1), log);
                }
            }
        }
        Combat.appliquerEffet(this, new BuffDefense(0.30, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Jura invoque le Grondement du Mont Fuji !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 2.00;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche) {
                    Combat.appliquerEffet(this, ennemi, new ReductionAttaque(0.10, 2), log);
                }
            }
        }
        Combat.appliquerEffet(this, new BuffBlocage(0.20, 2), log);
        Combat.appliquerEffet(this, new Regeneration(0.08, 2), log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Poing de Roc — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Montagne — Inflige 155% ATK à tous les ennemis, 25% de chance d'étourdir 1 tour, augmente sa défense de 30%.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Grondement du Mont Fuji — Inflige 200% ATK à tous les ennemis, réduit leur attaque de 10% pendant 2 tours, augmente son blocage de 20% et régénère 8% pendant 2 tours.");
    }
}
