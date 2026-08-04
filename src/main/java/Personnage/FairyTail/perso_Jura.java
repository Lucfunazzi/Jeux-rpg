package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Jura Neekis — Chevalier, rang S.
 * As de Lamia Scale, l'un des Dix Mages Saints. Magie de la Terre : peut manier
 * et écraser la roche à volonté. Allié de Fairy Tail dès l'arc Oración Seis.
 */
public class perso_Jura extends PersonnageBase {

    public perso_Jura() {
        this.nom    = "Jura";
        this.type   = "Chevalier";
        this.role   = "Tank";
        this.rarete = "SS";
        this.niveau = 1;
        double mult = 1.60;
        this.vie     = 1500 * mult;
        this.attaque =  450 * mult;
        this.defense =  350 * mult;
        this.vitesse =  200* mult;
        this.taux_critiques    = 0.06;
        this.degat_critiques   = 1.15;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.06;
        this.taux_blocage      = 0.16;
        this.reduction_blocage = 0.22;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Poing de Roc", "Mudra du Grondement", "Roc Suprême"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Jura écrase les attaquants et supports ennemis d'un Poing de Roc !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && (ennemi.getRole().equals("DPS") || ennemi.getRole().equals("Support"))) {
                double degats = this.getAttaque() * 1.75;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Jura invoque une Mudra du Grondement sur toute l'équipe ennemie !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.25;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche && Math.random() < 0.25) {
                    Combat.appliquerEffet(this, ennemi, new Etourdissement(1), log);
                }
            }
        }
        Combat.appliquerEffet(this, new BuffDefense(0.15, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Jura invoque le Roc Suprême — la montagne elle-même s'abat !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.60) * multiplicateurRage;
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
        System.out.println("Poing de Roc — Inflige 175% ATK aux attaquants et supports .");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Mudra du Grondement — Inflige 125% ATK à tous les ennemis, 25% de chance d'étourdir chacun 1 tour et augmente sa defense de 15%.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Roc Suprême — Inflige 160% ATK (bonus selon la Rage) à tous les ennemis et augmente son blocage de 20% et reduit l'attaque des adversaires de 10% et s'applique regenration de 8%  pendant 2 tours.");
    }
}
