package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class perso_Yukino extends PersonnageBase {

    public perso_Yukino() {
        this.nom    = "Yukino";
        this.type   = "Mage";
        this.role   = "Support";
        this.rarete = "S";
        this.niveau = 1;
        double multiplicateurRarete = 1.50;
        this.vie     = 580 * multiplicateurRarete;
        this.attaque = 190 * multiplicateurRarete;
        this.defense = 110 * multiplicateurRarete;
        this.vitesse = 115 * multiplicateurRarete;
        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.35;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Voie Celeste", "Pisces & Libra", "Ophiuchus"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yukino utilise Voie Celeste sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);

        // Synergie Angel : applique Marquage
        boolean angelAlliee = equipeAlliee.stream()
                .anyMatch(a -> a.estVivant() && a.getNom().equals("Angel"));
        if (angelAlliee) {
            Combat.appliquerEffet(this, cible, new Marquage(2, 0.15), log);
            log.add("  Angel guide la frappe — Marquage applique !");
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yukino invoque Pisces & Libra !");

        boolean lucyAlliee  = equipeAlliee.stream().anyMatch(a -> a.estVivant() && a.getNom().equals("Lucy"));
        boolean angelAlliee = equipeAlliee.stream().anyMatch(a -> a.estVivant() && a.getNom().equals("Angel"));

        double mult = 1.00;
        if (lucyAlliee) {
            mult += 0.20;
            log.add("  Lucy amplifie l'invocation ! +20% degats.");
        }

        // Pisces : AoE léger
        log.add("  Pisces s'elance sur toute l'equipe ennemie !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (!ennemi.estVivant()) continue;
            Combat.appliquerDegatsAvecLog(this, ennemi, this.getAttaque() * 1.00 * mult, log);
            // Synergie Angel : Saignement sur chaque ennemi
            if (angelAlliee) {
                Combat.appliquerEffet(this, ennemi, new Saignement(2, 0.05), log);
            }
        }

        // Libra : ReductionDEF sur tous les ennemis
        log.add("  Libra fait pencher la balance — DEF ennemie reduite !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.15, 2), log);
            }
        }

        if (angelAlliee) log.add("  Angel renforce l'invocation — Saignement inflige !");
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yukino ouvre la porte d'Ophiuchus !");

        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }

        boolean lucyAlliee  = equipeAlliee.stream().anyMatch(a -> a.estVivant() && a.getNom().equals("Lucy"));
        boolean angelAlliee = equipeAlliee.stream().anyMatch(a -> a.estVivant() && a.getNom().equals("Angel"));

        if (lucyAlliee && angelAlliee) {
            // Frappe TOUTE l'équipe ennemie à 180%
            log.add("  Lucy & Angel ouvrent les portes ensemble — Ophiuchus frappe tous les ennemis !");
            for (PersonnageBase ennemi : equipeEnnemie) {
                if (!ennemi.estVivant()) continue;
                double degats = this.getAttaque() * 1.80 * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                // Debuff aléatoire
                int r = (int)(Math.random() * 3);
                switch (r) {
                    case 0 -> Combat.appliquerEffet(this, ennemi, new ReductionAttaque(0.15, 2), log);
                    case 1 -> Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.15, 2), log);
                    case 2 -> Combat.appliquerEffet(this, ennemi, new ReductionVitesse(0.15, 2), log);
                }
            }
        } else {
            // Frappe la cible avec le plus de PV à 180%, puis AoE 80% sur le reste
            PersonnageBase ciblePrincipale = equipeEnnemie.stream()
                    .filter(PersonnageBase::estVivant)
                    .max(java.util.Comparator.comparingDouble(PersonnageBase::getVie))
                    .orElse(null);

            if (ciblePrincipale != null) {
                log.add("  Ophiuchus cible " + ciblePrincipale.getNom() + " !");
                double degats = this.getAttaque() * 1.80 * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ciblePrincipale, degats, log);
                int r = (int)(Math.random() * 3);
                switch (r) {
                    case 0 -> Combat.appliquerEffet(this, ciblePrincipale, new ReductionAttaque(0.15, 2), log);
                    case 1 -> Combat.appliquerEffet(this, ciblePrincipale, new ReductionDefense(0.15, 2), log);
                    case 2 -> Combat.appliquerEffet(this, ciblePrincipale, new ReductionVitesse(0.15, 2), log);
                }
            }
            // AoE 80% sur le reste
            for (PersonnageBase ennemi : equipeEnnemie) {
                if (!ennemi.estVivant() || ennemi == ciblePrincipale) continue;
                Combat.appliquerDegatsAvecLog(this, ennemi, this.getAttaque() * 0.80 * multiplicateurRage, log);
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Voie Celeste — inflige 100% ATK a une cible."
                + " Si Angel est alliee : applique Marquage 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Pisces & Libra — Pisces inflige 100% ATK AoE a tous les ennemis,"
                + " Libra reduit leur DEF de 15% pendant 2 tours."
                + " Si Lucy alliee : +20% degats. Si Angel alliee : applique aussi Saignement.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Ophiuchus — inflige 180% ATK a l'ennemi avec le plus de PV"
                + " + AoE 80% ATK sur le reste, avec debuff aleatoire."
                + " Si Lucy ET Angel alliees : frappe toute l'equipe ennemie a 180% ATK.");
    }
}
