package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class perso_Angel extends PersonnageBase {

    public perso_Angel() {
        this.nom    = "Angel";
        this.type   = "Mage";
        this.role   = "DPS";
        this.rarete = "A";
        this.niveau = 1;
        double multiplicateurRarete = 1.40;
        this.vie     = 480 * multiplicateurRarete;
        this.attaque = 200 * multiplicateurRarete;
        this.defense = 90  * multiplicateurRarete;
        this.vitesse = 125 * multiplicateurRarete;
        this.taux_critiques    = 0.20;
        this.degat_critiques   = 1.40;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Aile Celeste", "Caelum", "Aries"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Angel utilise Aile Celeste sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);

        // Synergie Yukino : ReductionATK
        boolean yukinoAlliee = equipeAlliee.stream()
                .anyMatch(a -> a.estVivant() && a.getNom().equals("Yukino"));
        if (yukinoAlliee) {
            Combat.appliquerEffet(this, cible, new ReductionAttaque(0.10, 1), log);
            log.add("  Yukino guide l'aile — ATK de " + cible.getNom() + " reduite !");
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Angel invoque Caelum — le laser celeste !");

        boolean lucyAlliee   = equipeAlliee.stream().anyMatch(a -> a.estVivant() && a.getNom().equals("Lucy"));
        boolean yukinoAlliee = equipeAlliee.stream().anyMatch(a -> a.estVivant() && a.getNom().equals("Yukino"));

        double mult = 1.50;
        if (yukinoAlliee) {
            mult += 0.20;
            log.add("  Yukino amplifie Caelum ! +20% degats.");
        }

        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * mult, log);

        // 30% chance d'étourdir
        if (Math.random() < 0.30) {
            Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
            log.add("  Le laser aveugle " + cible.getNom() + " !");
        }

        // Synergie Lucy : Marquage en plus
        if (lucyAlliee) {
            Combat.appliquerEffet(this, cible, new Marquage(2, 0.15), log);
            log.add("  Lucy cible avec precision — Marquage applique !");
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Angel invoque Aries — le Belier celeste !");

        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }

        boolean lucyAlliee   = equipeAlliee.stream().anyMatch(a -> a.estVivant() && a.getNom().equals("Lucy"));
        boolean yukinoAlliee = equipeAlliee.stream().anyMatch(a -> a.estVivant() && a.getNom().equals("Yukino"));

        double reductionATK = 0.20;
        if (lucyAlliee && yukinoAlliee) {
            reductionATK = 0.40;
            log.add("  Lucy & Yukino unissent leurs portes — reduction ATK doublee !");
        }

        for (PersonnageBase ennemi : equipeEnnemie) {
            if (!ennemi.estVivant()) continue;
            double degats = this.getAttaque() * 1.30 * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new ReductionAttaque(reductionATK, 2), log);
        }

        log.add("  Aries charge sur toute l'equipe ennemie !");
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Aile Celeste — inflige 120% ATK a une cible."
                + " Si Yukino est alliee : reduit l'ATK de la cible de 10% pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Caelum — inflige 150% ATK a une cible, 30% de chance d'etourdir 1 tour."
                + " Si Lucy alliee : applique Marquage 2 tours."
                + " Si Yukino alliee : +20% degats.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Aries — AoE 130% ATK sur toute l'equipe ennemie + ReductionATK 20% 2 tours."
                + " Si Lucy ET Yukino alliees : ReductionATK doublee a 40%.");
    }
}
