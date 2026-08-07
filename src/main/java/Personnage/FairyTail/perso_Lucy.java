package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class perso_Lucy extends PersonnageBase {

    public perso_Lucy() {
        this.nom    = "Lucy";
        this.type="Invocateur";
        this.role   = "Support";
        this.rarete = "A";
        this.niveau = 1;
        double multiplicateurRarete = 1.56;
        this.vie     = 700 * multiplicateurRarete;
        this.attaque = 120 * multiplicateurRarete;
        this.defense = 90  * multiplicateurRarete;
        this.vitesse = 160 * multiplicateurRarete;
        this.taux_critiques    = 0.08;
        this.degat_critiques   = 1.15;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Lucy Kick", "Invocation Taurus", "Invocation Aquarius"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lucy utilise Lucy Kick sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lucy invoque Taurus !");

        boolean angelAlliee  = equipeAlliee.stream().anyMatch(a -> a.estVivant() && a.getNom().equals("Angel"));
        boolean yukinoAlliee = equipeAlliee.stream().anyMatch(a -> a.estVivant() && a.getNom().equals("Yukino"));

        // Taurus frappe les 2 ennemis avec le moins de PV
        ArrayList<PersonnageBase> ennemisVivants = new ArrayList<>();
        for (PersonnageBase ennemi : equipeEnnemie)
            if (ennemi.estVivant()) ennemisVivants.add(ennemi);
        ennemisVivants.sort(Comparator.comparingDouble(PersonnageBase::getVie));

        double mult = 1.20;
        if (yukinoAlliee) {
            mult += 0.20;
        }

        int count = 0;
        for (PersonnageBase ennemi : ennemisVivants) {
            if (count >= 2) break;
            Combat.appliquerDegatsAvecLog(this, ennemi, this.getAttaque() * mult, log);
            Combat.appliquerEffet(this, ennemi, new Saignement(2, 0.02), log);
            if (angelAlliee) {
                Combat.appliquerEffet(this, ennemi, new Marquage(), log);
            }
            count++;
        }

        // Buff DEF équipe
        for (PersonnageBase allie : equipeAlliee)
            if (allie.estVivant()) Combat.appliquerEffet(this, allie, new BuffTauxCritique(0.10, 3), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lucy invoque Aquarius !");

        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }

        boolean angelAlliee  = equipeAlliee.stream().anyMatch(a -> a.estVivant() && a.getNom().equals("Angel"));
        boolean yukinoAlliee = equipeAlliee.stream().anyMatch(a -> a.estVivant() && a.getNom().equals("Yukino"));

        double mult = 1.30;
        if (angelAlliee && yukinoAlliee) {
            mult = 1.40;
        } else if (yukinoAlliee || angelAlliee) {
            mult = 1.20;
        }

        for (PersonnageBase ennemi : equipeEnnemie) {
            if (!ennemi.estVivant()) continue;
            Combat.appliquerDegatsAvecLog(this, ennemi, this.getAttaque() * mult * multiplicateurRage, log);
            Combat.appliquerEffet(this, ennemi, new Trempe(3), log);
            if (Math.random() < 0.40) {
                Combat.appliquerEffet(this, ennemi, new Etourdissement(1), log);
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Lucy Kick — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Invocation Taurus — inflige 120% ATK aux 2 ennemis avec le moins de PV"
                + " + Saignement 2 tours + Buff Taux Critiques de 10% pour l'equipe pendant 3 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Invocation Aquarius — AoE 130% ATK + Trempe 3 tours + 40% chance Etourdissement.");
    }
}