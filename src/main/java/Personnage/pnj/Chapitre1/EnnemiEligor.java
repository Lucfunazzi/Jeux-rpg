package Personnage.pnj.Chapitre1;

import Combat.Combat;
import Effets.Brulure;
import Effets.BuffAttaque;
import Effets.Etourdissement;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Eligor — Démon invoqué par Bora, boss du Chapitre 1, rang C.
 * Stats renforcées par rapport à un C standard (mini-boss de prologue).
 */
public class EnnemiEligor extends PersonnageBase {

    public EnnemiEligor() { this(8); }

    public EnnemiEligor(int niveau) {
        this.nom    = "Eligor";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";

        // Mini-boss : multiplicateur 1.20 sur les stats de base
        double mult = 1.20;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 280.0 * mult * niv;
        this.attaque = 100.0 * mult * niv;
        this.defense =  60.0 * mult * niv;
        this.vitesse =  80.0 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Griffe Démonique", "Charge Infernale", "Rugissement du Démon"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Eligor lacère " + cible.getNom() + " de ses griffes démoniques !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Brulure(1, 0.06), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Eligor charge " + cible.getNom() + " dans une explosion infernale !");
        double degats = this.getAttaque() * 1.40;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (Math.random() < 0.25) {
            Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Eligor pousse un rugissement démoniaque et embrase le champ de bataille !");
        Combat.appliquerEffet(this, new BuffAttaque(0.20, 2), log);
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 0.80;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new Brulure(2, 0.06), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Griffe Démonique — Inflige 100% ATK et brûle la cible 1 tour (6% PV/tour).");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Charge Infernale — Inflige 140% ATK, 25% de chance d'étourdir 1 tour.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Rugissement du Démon — Gagne 20% ATK pendant 2 tours, inflige 80% ATK à tous et les brûle 2 tours.");
    }
}
