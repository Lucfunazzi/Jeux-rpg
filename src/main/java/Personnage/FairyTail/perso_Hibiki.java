package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Hibiki Lates — Elementaliste, rang B.
 * Membre des Trimens de Blue Pegasus. Magie des Archives : projette un clavier
 * holographique pour analyser et frapper à distance. Allié dès l'arc Oración Seis.
 */
public class perso_Hibiki extends PersonnageBase {

    public perso_Hibiki() {
        this.nom    = "Hibiki";
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "A";
        this.niveau = 1;
        double mult = 1.44;
        this.vie     = 414 * mult;
        this.attaque = 165 * mult;
        this.defense = 109 * mult;
        this.vitesse = 125 * mult;
        this.taux_critiques    = 0.12;
        this.degat_critiques   = 1.25;
        this.taux_precisions   = 108.00;
        this.taux_esquives     = 0.07;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.08;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Frappe d'Archive", "Analyse Tactique", "Hexadecagon"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hibiki frappe " + cible.getNom() + " d'une Frappe d'Archive !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hibiki analyse " + cible.getNom() + " et expose ses failles !");
        double degats = this.getAttaque() * 1.25;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new BuffDefense(0.10, 2), log);
                Combat.appliquerEffet(this, allie, new BuffVitesse(0.10, 2), log);
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hibiki déploie un Hexadecagon de lumière !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.20) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant() && allie.getRole().equals("DPS")) {
                Combat.appliquerEffet(this, allie, new BuffAttaque(0.15, 2), log);
                Combat.appliquerEffet(this, allie, new BuffTauxCritique(0.10, 2), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Frappe d'Archive — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Analyse Tactique — Inflige 125% ATK et augmente la defense de l'equipe de 10% et la vitesse de 10%.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Hexadecagon — Inflige 120% ATK (bonus selon la Rage) à tous les ennemis et augmente l'attaque de 15% des attaquants et le taux critiques des attaquants de 10%.");
    }
}
