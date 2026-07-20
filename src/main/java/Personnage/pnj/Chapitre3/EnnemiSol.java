package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.BuffDefense;
import Effets.Etourdissement;
import Effets.ReductionAttaque;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Sol — Element 4 de Phantom Lord, magie de la terre, rang B.
 * Tank défensif qui étourdit et affaiblit les attaquants.
 * Multiplicateur rang B : 1.30
 */
public class EnnemiSol extends PersonnageBase {

    public EnnemiSol() { this(26); }

    public EnnemiSol(int niveau) {
        this.nom    = "Sol";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "Tank";
        this.rarete = "B";

        double mult = 1.30;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 310.0 * mult * niv;
        this.attaque =  85.0 * mult * niv;
        this.defense =  95.0 * mult * niv;
        this.vitesse =  70.0 * mult * vit;

        this.taux_critiques    = 0.08;
        this.degat_critiques   = 1.15;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.04;
        this.taux_blocage      = 0.20;
        this.reduction_blocage = 0.22;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Poing de Terre", "Séisme", "Forteresse de Granit"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sol frappe " + cible.getNom() + " d'un lourd poing de terre !");
        Combat.attaquer(this, cible, log);
        if (Math.random() < 0.15) {
            Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sol déclenche un séisme sous les pieds de " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.20, 2), log);
        if (Math.random() < 0.30) {
            Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sol érige une forteresse de granit — sa défense devient impénétrable !");
        Combat.appliquerEffet(this, new BuffDefense(0.30, 3), log);
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant() && allie != this) {
                Combat.appliquerEffet(this, allie, new BuffDefense(0.15, 2), log);
            }
        }
        double soin = this.getVieMax() * 0.12;
        this.recevoirSoin(soin, log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Poing de Terre — Inflige 100% ATK, 15% de chance d'étourdir 1 tour.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Séisme — Inflige 120% ATK, réduit ATK de 20% pendant 2 tours, 30% de chance d'étourdir.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Forteresse de Granit — Gagne 30% DEF pendant 3 tours, alliés +15% DEF 2 tours, se soigne de 12% PV max.");
    }
}
