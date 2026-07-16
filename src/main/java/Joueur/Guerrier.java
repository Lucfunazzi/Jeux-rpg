package Joueur;

import Personnage.PersonnageBase;
import Effets.*;
import java.util.List;
import Combat.Combat;

/**
 * Classe Guerrier — thème ki et energie DBZ.
 * Compétences génériques inspirées de l'univers Dragon Ball Z.
 * Ultime rang C : puissance modeste, conçue pour progresser via l'arbre.
 */
public class Guerrier implements Competences {

    @Override
    public String[] getNomsCompetences() {
        return new String[]{"Kamehameha", "Onde d'impact", "Barrage de ki"};
    }

    // Comp 1 — Frappe de ki concentrée sur une cible, brulure 30%
    @Override
    public void choix_competence1(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Kamehameha !");
        double degats = utilisateur.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(utilisateur, cible, degats, log);
        if (Math.random() < 0.30) {
            Combat.appliquerEffet(utilisateur, cible, new Brulure(2, 0.05), log);
        }
    }

    // Comp 2 — Onde de choc sur les 2 premières cibles + réduction DEF
    @Override
    public void choix_competence2(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Onde d'impact !");
        int ciblesAttaquees = 0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && ciblesAttaquees < 2) {
                double degats = utilisateur.getAttaque() * 1.10;
                Combat.appliquerDegatsAvecLog(utilisateur, ennemi, degats, log);
                Combat.appliquerEffet(utilisateur, ennemi, new ReductionDefense(0.10, 2), log);
                ciblesAttaquees++;
            }
        }
    }

    // Comp 3 — Frappe lourde mono-cible + buff ATK personnel
    @Override
    public void choix_competence3(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Barrage de ki !");
        double degats = utilisateur.getAttaque() * 1.60;
        Combat.appliquerDegatsAvecLog(utilisateur, cible, degats, log);
        Combat.appliquerEffet(utilisateur, new BuffAttaque(0.15, 2), log);
    }

    // Ultime rang C — AoE modéré avec légère brulure, pas surpuissant
    @Override
    public void ultime(PersonnageBase utilisateur,
            List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Pluie de ki !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = utilisateur.getAttaque() * 1.00;
                Combat.appliquerDegatsAvecLog(utilisateur, ennemi, degats, log);
                if (Math.random() < 0.25) {
                    Combat.appliquerEffet(utilisateur, ennemi, new Brulure(1, 0.05), log);
                }
            }
        }
    }

    // Compétence arbre 1 — Brise-Armure (deja implemente)
    @Override
    public void competenceArbre(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Brise-Armure !");
        double degats = utilisateur.getAttaque() * 1.90;
        Combat.appliquerDegatsAvecLog(utilisateur, cible, degats, log);
        Combat.appliquerEffet(utilisateur, cible, new ReductionDefense(0.20, 3), log);
    }

    // Compétence arbre 2 — Frappe Sismique (deja implemente)
    @Override
    public void competenceArbre2(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Frappe Sismique !");
        double degats = utilisateur.getAttaque() * 2.00;
        Combat.appliquerDegatsAvecLog(utilisateur, cible, degats, log);
        Combat.appliquerEffet(utilisateur, cible, new ReductionDefense(0.25, 3), log);
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && ennemi != cible) {
                double splash = utilisateur.getAttaque() * 0.80;
                Combat.appliquerDegatsAvecLog(utilisateur, ennemi, splash, log);
                Combat.appliquerEffet(utilisateur, ennemi, new Brulure(2, 0.06), log);
            }
        }
        Combat.appliquerEffet(utilisateur, new BuffAttaque(0.15, 2), log);
    }

    @Override
    public void descriptionCompetence1() {
        System.out.println("Kamehameha — Inflige 120% ATK a 1 cible. "
                + "30% de chance d'infliger Brulure (5% PV/tour) pendant 2 tours.");
    }

    @Override
    public void descriptionCompetence2() {
        System.out.println("Onde d'impact — Inflige 110% ATK aux 2 premieres cibles. "
                + "Reduit leur DEF de 10% pendant 2 tours.");
    }

    @Override
    public void descriptionCompetence3() {
        System.out.println("Barrage de ki — Inflige 160% ATK a 1 cible. "
                + "Augmente l'ATK du lanceur de 15% pendant 2 tours.");
    }

    @Override
    public void descriptionUltime() {
        System.out.println("Pluie de ki — Inflige 100% ATK a tous les ennemis. "
                + "25% de chance d'infliger Brulure (5% PV/tour) pendant 1 tour sur chacun.");
    }

    @Override
    public void descriptionCompetenceArbre() {
        System.out.println("Brise-Armure — Inflige 190% ATK a 1 cible. "
                + "Reduit sa DEF de 20% pendant 3 tours.");
    }

    @Override
    public void descriptionCompetenceArbre2() {
        System.out.println("Frappe Sismique — Inflige 200% ATK a 1 cible principale, "
                + "reduit sa DEF de 25% pendant 3 tours. "
                + "Onde de choc : 80% ATK sur tous les autres ennemis avec Brulure (6%) 2 tours. "
                + "Augmente l'ATK du lanceur de 15% pendant 2 tours.");
    }
}