package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Hades (Precht Gaebolg) — Elementaliste, rang SSS.
 * Ancien second maître de Fairy Tail et maître de Grimoire Heart.
 */
public class perso_Hades extends PersonnageBase {

    public perso_Hades() {
        this.nom    = "Hades";
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "SSS";
        this.niveau = 1;
        double mult = 1.85;
        this.vie     = 820 * mult;
        this.attaque = 265 * mult;
        this.defense = 175 * mult;
        this.vitesse = 125 * mult;
        this.taux_critiques    = 0.18;
        this.degat_critiques   = 1.40;
        this.taux_precisions   = 110.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.16;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Choc des Ténèbres", "Amaterasu", "Genèse Zéro"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hades frappe " + cible.getNom() + " d'un Choc des Ténèbres !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hades embrase " + cible.getNom() + " d'un Amaterasu implacable !");
        double degats = this.getAttaque() * 1.50;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new Brulure(2, 0.08), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hades ouvre la Genèse Zéro — le néant absorbe tout !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.25) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Choc des Ténèbres — Inflige 200% ATK à tout les ennemis et inflige ralentissement pendants 2 tours.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Amaterasu — Inflige 220% ATK aux supports et brûle les cibles pendant 2 tours (10% PV/tour). silence aux support et malediction aux support");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Genèse Zéro — Inflige 400% ATK (bonus selon la Rage) à l'ennemi avec le plus de défense et inflige fragilité a 50% pendants 1 tour.");
    }
}
