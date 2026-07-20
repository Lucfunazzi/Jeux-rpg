package Joueur;

import Personnage.PersonnageBase;
import Effets.*;
import Combat.Combat;
import java.util.Comparator;
import java.util.List;

/**
 * Constellationniste — esprits célestes, inspiré de Lucy Heartfilia.
 *
 * Spéciale de base : Invocation de Caelum   (100% ATK mono-cible Tank + 100% précision)
 * Ultime de base   : Invocation de Taurus   (120% ATK mono-cible Tank + 5% ATK aux DPS alliés)
 * Spéciale Arbre 1 : Invocation : Leo       (frappe + Silence + buff ATK équipe)
 * Ultime  Arbre 2  : Portes des Étoiles     (Taurus + Sagittarius + Leo simultanés)
 */
public class Constellationniste implements Competences {

    @Override
    public String[] getNomsCompetences() {
        return new String[]{"Invocation de Caelum", "Invocation de Taurus"};
    }

    // ── Spéciale de base ─────────────────────────────────────────────────
    @Override
    public void attaqueSpeciale(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Invocation de Caelum !");
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
        log.add("Invocation de Taurus !");
        PersonnageBase tank = equipeEnnemie.stream()
                .filter(e -> e.estVivant() && e.getRole().equals("Tank"))
                .findFirst().orElse(
                    equipeEnnemie.stream().filter(PersonnageBase::estVivant).findFirst().orElse(null)
                );
        if (tank != null) {
            double degats = utilisateur.getAttaque() * 1.20;
            Combat.appliquerDegatsAvecLog(utilisateur, tank, degats, log);
        }
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
        log.add("Invocation : Leo — Le Lion des Étoiles rugit !");
        double degats = utilisateur.getAttaque() * 1.80;
        Combat.appliquerDegatsAvecLog(utilisateur, cible, degats, log);
        Combat.appliquerEffet(utilisateur, cible, new Silence(1), log);
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(utilisateur, allie, new BuffAttaque(0.12, 2), log);
            }
        }
    }

    // ── Ultime Arbre 2 ───────────────────────────────────────────────────
    @Override
    public void competenceArbre2(Personnage_principale utilisateur,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Portes des Étoiles — Invocation Multiple !");
        // Taurus sur l'ennemi le plus résistant
        PersonnageBase cible1 = equipeEnnemie.stream()
                .filter(PersonnageBase::estVivant)
                .max(Comparator.comparingDouble(PersonnageBase::getVie))
                .orElse(null);
        if (cible1 != null) {
            Combat.appliquerDegatsAvecLog(utilisateur, cible1, utilisateur.getAttaque() * 1.20, log);
            Combat.appliquerEffet(utilisateur, cible1, new ReductionDefense(0.20, 2), log);
        }
        // Sagittarius sur une 2e cible
        PersonnageBase cible2 = equipeEnnemie.stream()
                .filter(e -> e.estVivant() && e != cible1)
                .findFirst().orElse(null);
        if (cible2 != null) {
            Combat.appliquerDegatsAvecLog(utilisateur, cible2, utilisateur.getAttaque() * 1.00, log);
            if (Math.random() < 0.50) {
                Combat.appliquerEffet(utilisateur, cible2, new Saignement(2, 0.06), log);
            }
        }
        // Leo buff équipe
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(utilisateur, allie, new BuffAttaque(0.15, 2), log);
            }
        }
    }

    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Invocation de Caelum — Inflige 100% ATK au Tank ennemi. +100% Précision au lanceur (2 tours).");
    }
    @Override public void descriptionUltime() {
        System.out.println("Invocation de Taurus — Inflige 120% ATK au Tank ennemi. +5% ATK aux DPS alliés (2 tours).");
    }
    @Override public void descriptionCompetenceArbre() {
        System.out.println("Invocation : Leo [Arbre 1] — Inflige 180% ATK à 1 cible + Silence 1 tour. +12% ATK à toute l'équipe (2 tours).");
    }
    @Override public void descriptionCompetenceArbre2() {
        System.out.println("Portes des Étoiles [Arbre 2] — Taurus : 120% ATK + -20% DEF sur ennemi le plus résistant. Sagittarius : 100% ATK + 50% Saignement sur ennemi 2. Leo : +15% ATK équipe (2 tours).");
    }
}