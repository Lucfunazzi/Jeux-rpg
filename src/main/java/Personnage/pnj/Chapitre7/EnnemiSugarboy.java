package Personnage.pnj.Chapitre7;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Sugarboy — soldat de l'Armée Royale d'Edolas, rang A.
 * Capable d'avaler et d'absorber la magie adverse pour s'en servir de bouclier.
 */
public class EnnemiSugarboy extends PersonnageBase {

    public EnnemiSugarboy() { this(76); }

    public EnnemiSugarboy(int niveau) {
        this.nom    = "Sugarboy";
        this.niveau = niveau;
        this.type   = "Chevalier";
        this.role   = "Tank";
        this.rarete = "A";

        double mult = 1.45;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 600.0 * mult * niv;
        this.attaque = 150.0 * mult * niv;
        this.defense = 170.0 * mult * niv;
        this.vitesse =  90.0 * mult * vit;

        this.taux_critiques    = 0.06;
        this.degat_critiques   = 1.15;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = 0.18;
        this.reduction_blocage = 0.22;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de Bouclier", "Absorption Magique", "Vague Dévorante"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sugarboy percute " + cible.getNom() + " d'un Coup de Bouclier !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sugarboy avale la magie ambiante — Absorption Magique !");
        Combat.appliquerEffet(this, new Bouclier(this.getVieMax() * 0.12), log);
        double degats = this.getAttaque() * 1.05;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sugarboy recrache tout ce qu'il a avalé — Vague Dévorante !");
        double degatsTotaux = 0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.90;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                degatsTotaux += degats;
            }
        }
        double soin = degatsTotaux * 0.15;
        if (soin > 0) this.recevoirSoin(soin, log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Coup de Bouclier — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Absorption Magique — Se protège d'un bouclier (12% PV max) et inflige 105% ATK.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Vague Dévorante — Inflige 90% ATK à tous les ennemis et se soigne de 15% des dégâts infligés.");
    }
}
