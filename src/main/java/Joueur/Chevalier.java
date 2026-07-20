package Joueur;

import Personnage.PersonnageBase;
import Effets.*;
import Combat.Combat;
import java.util.List;

/**
 * Chevalier — inspiré d'Erza Scarlet.
 *
 * Spéciale de base : Lance Électrique      (100% ATK mono-cible Tank + 100% précision)
 * Ultime de base   : Lance des Ténèbres    (120% ATK mono-cible + 5% ATK aux DPS alliés)
 * Spéciale Arbre 1 : Changement d'Armure   (frappe lourde + buff DEF)
 * Ultime  Arbre 2  : Armure du Ciel Brillant (frappe dévastatrice + protection équipe)
 */
public class Chevalier implements Competences {

    @Override
    public String[] getNomsCompetences() {
        return new String[]{"Lance Électrique", "Lance des Ténèbres"};
    }

    // ── Spéciale de base ─────────────────────────────────────────────────
    @Override
    public void attaqueSpeciale(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lance Électrique !");
        // Cible prioritaire : Tank ennemi, sinon la cible par défaut
        PersonnageBase tank = equipeEnnemie.stream()
                .filter(e -> e.estVivant() && e.getRole().equals("Tank"))
                .findFirst().orElse(cible);
        double degats = utilisateur.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(utilisateur, tank, degats, log);
        Combat.appliquerEffet(utilisateur, new BuffPrecision(1.00, 2), log);
    }

    // ── Ultime de base ───────────────────────────────────────────────────
    @Override
    public void ultime(PersonnageBase utilisateur, List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lance des Ténèbres !");
        // Cible prioritaire : Tank ennemi
        PersonnageBase tank = equipeEnnemie.stream()
                .filter(e -> e.estVivant() && e.getRole().equals("Tank"))
                .findFirst().orElse(
                    equipeEnnemie.stream().filter(PersonnageBase::estVivant).findFirst().orElse(null)
                );
        if (tank != null) {
            double degats = utilisateur.getAttaque() * 1.20;
            Combat.appliquerDegatsAvecLog(utilisateur, tank, degats, log);
        }
        // +5% ATK aux DPS alliés
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant() && allie.getRole().equals("DPS")) {
                Combat.appliquerEffet(utilisateur, allie, new BuffAttaque(0.05, 2), log);
            }
        }
    }

    // ── Spéciale Arbre 1 ─────────────────────────────────────────────────
    @Override
    public void competenceArbre(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Changement d'Armure !");
        double degats = utilisateur.getAttaque() * 1.90;
        Combat.appliquerDegatsAvecLog(utilisateur, cible, degats, log);
        Combat.appliquerEffet(utilisateur, new BuffDefense(0.30, 2), log);
        Combat.appliquerEffet(utilisateur, cible, new ReductionDefense(0.20, 3), log);
    }

    // ── Ultime Arbre 2 ───────────────────────────────────────────────────
    @Override
    public void competenceArbre2(Personnage_principale utilisateur,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Armure du Ciel Brillant !");
        PersonnageBase cible = equipeEnnemie.stream()
                .filter(PersonnageBase::estVivant)
                .max(java.util.Comparator.comparingDouble(PersonnageBase::getVie))
                .orElse(null);
        if (cible != null) {
            double degats = utilisateur.getAttaque() * 2.20;
            Combat.appliquerDegatsAvecLog(utilisateur, cible, degats, log);
            Combat.appliquerEffet(utilisateur, cible, new Saignement(3, 0.07), log);
        }
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(utilisateur, allie, new BuffDefense(0.25, 2), log);
            }
        }
        Combat.appliquerEffet(utilisateur, new BuffAttaque(0.20, 2), log);
    }

    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Lance Électrique — Inflige 100% ATK au Tank ennemi. +100% Précision au lanceur (2 tours).");
    }
    @Override public void descriptionUltime() {
        System.out.println("Lance des Ténèbres — Inflige 120% ATK au Tank ennemi. +5% ATK aux DPS alliés (2 tours).");
    }
    @Override public void descriptionCompetenceArbre() {
        System.out.println("Changement d'Armure [Arbre 1] — Inflige 190% ATK à 1 cible, -20% DEF cible (3 tours). +30% DEF au lanceur (2 tours).");
    }
    @Override public void descriptionCompetenceArbre2() {
        System.out.println("Armure du Ciel Brillant [Arbre 2] — Inflige 220% ATK à l'ennemi le plus résistant + Saignement 7% (3 tours). +25% DEF équipe + +20% ATK lanceur (2 tours).");
    }
}