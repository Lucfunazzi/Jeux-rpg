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
        double multiplicateurRarete = 1.30;
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

        // Synergie Yukino : applique ReductionDEF
        boolean yukinoAlliee = equipeAlliee.stream()
                .anyMatch(a -> a.estVivant() && a.getNom().equals("Yukino"));
        if (yukinoAlliee) {
            Combat.appliquerEffet(this, cible, new ReductionDefense(0.08, 1), log);
            log.add("  Yukino affaiblit la defense de " + cible.getNom() + " !");
        }
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

        double mult = 0.80;
        if (yukinoAlliee) {
            mult += 0.20;
            log.add("  Yukino renforce l'invocation ! +20% degats.");
        }

        int count = 0;
        for (PersonnageBase ennemi : ennemisVivants) {
            if (count >= 2) break;
            Combat.appliquerDegatsAvecLog(this, ennemi, this.getAttaque() * mult, log);
            Combat.appliquerEffet(this, ennemi, new Saignement(2, 0.02), log);
            // Synergie Angel : Marquage en plus
            if (angelAlliee) {
                Combat.appliquerEffet(this, ennemi, new Marquage(2, 0.15), log);
            }
            count++;
        }

        // Buff DEF équipe
        for (PersonnageBase allie : equipeAlliee)
            if (allie.estVivant()) Combat.appliquerEffet(this, allie, new BuffDefense(0.10, 3), log);

        if (angelAlliee) log.add("  Angel guide Taurus — Marquage applique !");
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

        double mult = 1.05;
        // Synergie Angel ET Yukino : AoE renforcée
        if (angelAlliee && yukinoAlliee) {
            mult = 1.40;
            log.add("  Angel & Yukino ouvrent leurs portes — Aquarius se dechaine ! +35% degats !");
        } else if (yukinoAlliee) {
            mult = 1.20;
            log.add("  Yukino amplifie le torrent ! +15% degats.");
        } else if (angelAlliee) {
            mult = 1.20;
            log.add("  Angel guide les eaux d'Aquarius ! +15% degats.");
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
        System.out.println("Lucy Kick — inflige 100% ATK a une cible."
                + " Si Yukino alliee : reduit la DEF de la cible de 8% pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Invocation Taurus — inflige 80% ATK aux 2 ennemis avec le moins de PV"
                + " + Saignement 2 tours + BuffDEF 10% equipe 3 tours."
                + " Si Yukino alliee : +20% degats. Si Angel alliee : applique aussi Marquage.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Invocation Aquarius — AoE 105% ATK + Trempe 3 tours + 40% chance Etourdissement."
                + " Si Angel ou Yukino alliee : +15% degats."
                + " Si Angel ET Yukino alliees : +35% degats.");
    }
}