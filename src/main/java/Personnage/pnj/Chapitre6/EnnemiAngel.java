package Personnage.pnj.Chapitre6;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Angel (Sorano) — version ennemie (Chapitre 6, stage 4). Meme identite et memes
 * attaques que perso_Angel (Epee de Caelum / Caelum / Aries), sans les synergies
 * Lucy/Yukino qui ne s'appliquent pas ici.
 */
public class EnnemiAngel extends PersonnageBase {

    public EnnemiAngel() { this(63); }

    public EnnemiAngel(int niveau) {
        this.nom    = "Angel";
        this.niveau = niveau;
        this.type   = "Invocateur";
        this.role   = "DPS";
        this.rarete = "A";

        double mult = 1.45;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 400.0 * mult * niv;
        this.attaque = 190.0 * mult * niv;
        this.defense =  95.0 * mult * niv;
        this.vitesse = 120.0 * mult * vit;

        this.taux_critiques    = 0.18;
        this.degat_critiques   = 1.35;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Epée de caelum", "Caelum", "Aries"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Angel utilise epée de caelum " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Angel invoque Caelum !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 1.50, log);
        if (Math.random() < 0.30) {
            Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
            log.add("  Le laser aveugle " + cible.getNom() + " !");
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Angel invoque Aries !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (!ennemi.estVivant()) continue;
            double degats = this.getAttaque() * 1.30 * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new ReductionAttaque(0.20, 2), log);
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Epée de caelum — Inflige 120% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Caelum — Inflige 150% ATK, 30% de chance d'étourdir la cible 1 tour.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Aries — Inflige 130% ATK (bonus selon la Rage) à tous les ennemis et réduit leur ATK de 20% pendant 2 tours.");
    }
}
