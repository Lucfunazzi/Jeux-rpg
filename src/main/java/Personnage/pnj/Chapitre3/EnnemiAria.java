package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.BuffAttaque;
import Effets.Malediction;
import Effets.ReductionAttaque;
import Effets.Silence;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Alya (Aria) — Element 4 de Phantom Lord, Magie des Espaces Aériens (Airspace), rang A.
 * Le plus puissant des Element 4. Porte un bandeau sur les yeux pour contenir sa puissance.
 * Ses attaques sont INVISIBLES.
 * Metsu : draine complètement la magie de la cible (presque tué Makarof).
 * Zetsu : jets d'air multiples.
 * Zéro : aspire l'air hors de l'ennemi — attaque ultime.
 */
public class EnnemiAria extends PersonnageBase {

    public EnnemiAria() { this(30); }

    public EnnemiAria(int niveau) {
        this.nom    = "Alya";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "A";

        double mult = 1.40;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 300.0 * mult * niv;
        this.attaque = 135.0 * mult * niv;
        this.defense =  70.0 * mult * niv;
        this.vitesse = 100.0 * mult * vit;

        this.taux_critiques    = 0.20;
        this.degat_critiques   = 1.45;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.14;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.08;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Zetsu — Jets d'Air Invisibles", "Metsu — Drain Magique", "Zéro — Vide Absolu"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Alya projette des jets d'air invisible sur " + cible.getNom() + " — l'attaque est indétectable !");
        Combat.attaquer(this, cible, log);
        
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Alya place ses mains ouvertes vers " + cible.getNom() + " — METSU ! Sa magie est aspirée et se dissipe dans l'air !");
        double degats = this.getAttaque() * 1.60;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Malediction(2,0.25), log);
        
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Alya retire son bandeau — ZÉRO ! L'air est aspiré de tout l'espace adverse !");
        Combat.appliquerEffet(this, new BuffAttaque(0.25, 2), log);
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.20;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
               
              
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Zetsu — Jets d'Air Invisibles : Inflige 100% ATK, réduit ATK de 12% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Metsu — Drain Magique : Inflige 160% ATK, maudit et réduit au silence 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Zéro — Vide Absolu : +25% ATK, inflige 120% ATK à tous, silence 3 tours, réduit ATK de 25%.");
    }
}
