package Personnage.pnj.Chapitre5;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Evergreen — version ennemie (Chapitre 5, stages 3 et 6). Meme identite et memes
 * attaques que perso_Evergreen, stats de base reprises telles quelles et mises a
 * l'echelle par niveau.
 */
public class EnnemiEvergreen extends PersonnageBase {

    public EnnemiEvergreen() { this(52); }

    public EnnemiEvergreen(int niveau) {
        this.nom    = "Evergreen";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "A";

        double mult = 1.40;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 380.0 * mult * niv;
        this.attaque = 140.0 * mult * niv;
        this.defense = 100.0 * mult * niv;
        this.vitesse = 110.0 * mult * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Stone Eyes", "Mitrailleuse féerique Leprechaun", "Explosion Féerique du Gremlin"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Evergreen utilise Stone Eyes !");
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Evergreen utilise Mitrailleuse féerique Leprechaun !");
        PersonnageBase cibleSupport = equipeEnnemie.stream()
                .filter(e -> e.estVivant() && e.getRole().equals("Support"))
                .findFirst()
                .orElse(cible);
        double degats = this.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(this, cibleSupport, degats, log);
        Combat.appliquerEffet(this, cibleSupport, new ReductionDefense(0.10, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Evergreen utilise Explosion féerique du Gremlin !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 0.90) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.15, 2), log);
                if (Math.random() < 0.25) {
                    Combat.appliquerEffet(this, ennemi, new Brulure(2, 0.05), log);
                }
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Stone Eyes — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Mitrailleuse féerique Leprechaun — inflige 150% ATK au Support ennemi "
                + "(cible normale en repli), reduit sa defense de 10% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Explosion féerique du Gremlin — inflige 90% ATK a tous les ennemis (bonus selon la Rage), "
                + "reduit leur defense de 15% pendant 2 tours, "
                + "25% de chance de Brulure (5% PV/tour) sur chaque cible pendant 2 tours.");
    }
}
