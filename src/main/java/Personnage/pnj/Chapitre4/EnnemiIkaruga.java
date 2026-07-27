package Personnage.pnj.Chapitre4;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Ikaruga — surnommee "l'Epee du Ciel", garde d'elite de la Tour du Paradis, rang A.
 */
public class EnnemiIkaruga extends PersonnageBase {

    public EnnemiIkaruga() { this(49); }

    public EnnemiIkaruga(int niveau) {
        this.nom    = "Ikaruga";
        this.niveau = niveau;
        this.type   = "Chevalier";
        this.role   = "Tank";
        this.rarete = "A";

        double mult = 1.40;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 560.0 * mult * niv;
        this.attaque = 155.0 * mult * niv;
        this.defense = 140.0 * mult * niv;
        this.vitesse = 100.0 * mult * vit;

        this.taux_critiques    = 0.08;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.18;
        this.reduction_blocage = 0.22;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de Sabre", "Flammes du Garuda", "Eclats des Esprits"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ikaruga utilise Coup de Sabre sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ikaruga utilise Flammes du Garuda !");
        PersonnageBase cibleDPS = equipeEnnemie.stream()
                .filter(e -> e.estVivant() && e.getRole().equals("DPS"))
                .findFirst()
                .orElse(cible);

        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cibleDPS, degats, log);
        Combat.appliquerEffet(this, cibleDPS, new Brulure(2, 0.06), log);
        Combat.appliquerEffet(this, new ContreAttaque(2, 0.20), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ikaruga utilise Eclats des Esprits !");
        Combat.appliquerEffet(this, new BuffDefense(0.15, 2), log);

        String roleCible = Combat.rolePrioritaireVivant(equipeEnnemie);
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && ennemi.getRole().equals(roleCible)) {
                double degats = this.getAttaque() * 0.90;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.12, 2), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Coup de Sabre — inflige 100% ATK a une cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Flammes du Garuda — cible le DPS ennemi en priorite, inflige 120% ATK, applique "
                + "Brulure (6% PV) pendant 2 tours, Ikaruga gagne Contre-Attaque 20% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Eclats des Esprits — augmente sa defense de 15% pendant 2 tours, puis inflige "
                + "90% ATK au role prioritaire ennemi avec Reduction de defense -12% pendant 2 tours.");
    }
}
