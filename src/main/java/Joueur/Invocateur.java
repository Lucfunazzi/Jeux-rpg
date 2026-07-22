package Joueur;

import Personnage.PersonnageBase;
import Effets.*;
import Combat.Combat;
import java.util.Comparator;
import java.util.List;

/**
 * Constellationniste — esprits célestes, inspiré de Lucy Heartfilia.
 *
 * Spéciale de base : Invocation de Caelum  (100% ATK mono-cible)
 * Ultime de base   : Invocation de Taurus  (120% ATK mono-cible + 5% ATK aux DPS alliés)
 * Spéciale Arbre 1 : Invocation : Cancer   (frappe + réduction défense)
 * Spéciale Arbre 2 : Invocation : Virgo    (frappe lourde + étourdissement)
 */
public class Invocateur implements Competences {

    @Override
    public String[] getNomsCompetences() {
        return new String[]{"Invocation de Caelum", "Invocation de Taurus"};
    }

    // ── Spéciale de base ─────────────────────────────────────────────────
    @Override
    public void attaqueSpeciale(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Invocation de Caelum !");
        PersonnageBase cibleFinale = Combat.choisirCible(utilisateur, equipeEnnemie);
        double degats = utilisateur.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(utilisateur, cibleFinale, degats, log);
        Combat.appliquerEffet(utilisateur, new BuffPrecision(1.00, 2), log);
    }

    // ── Ultime de base ───────────────────────────────────────────────────
    @Override
    public void ultime(PersonnageBase utilisateur, List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Invocation de Taurus !");
        PersonnageBase cibleFinale = Combat.choisirCible(utilisateur, equipeEnnemie);
        if (cibleFinale != null) {
            double degats = utilisateur.getAttaque() * 1.20;
            Combat.appliquerDegatsAvecLog(utilisateur, cibleFinale, degats, log);
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
        log.add("Invocation : Cancer!");
        PersonnageBase cibleFinale = Combat.choisirCible(utilisateur, equipeEnnemie);
        double degats = utilisateur.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(utilisateur, cibleFinale, degats, log);
        Combat.appliquerEffet(cibleFinale, new ReductionDefense(0.15,2), log);
    }

    // ── Spéciale Arbre 2 ───────────────────────────────────────────────────
    @Override
    public void competenceArbre2(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Invocation : Virgo!");
        PersonnageBase cibleFinale = Combat.choisirCible(utilisateur, equipeEnnemie);
        double degats = utilisateur.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(utilisateur, cibleFinale, degats, log);
        Combat.appliquerEffet(cibleFinale, new Etourdissement(2), log);
    }

    // ── Spéciale Arbre 3 ───────────────────────────────────────────────────
    @Override
    public void competenceArbre3(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        // TODO
    }

    @Override public void descriptionCompetenceArbre3() {
        // TODO
    }

    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Invocation de Caelum — Inflige 100% ATK au Tank ennemi. +100% Précision au lanceur (2 tours).");
    }
    @Override public void descriptionUltime() {
        System.out.println("Invocation de Taurus — Inflige 120% ATK au Tank ennemi. +5% ATK aux DPS alliés (2 tours).");
    }
    @Override public void descriptionCompetenceArbre() {
        System.out.println("Invocation de Cancer -- Inflige 120% ATK au Tank ennemi. Réduit la défense de la cible de 15% pendants 2 tours");
        
    }
    @Override public void descriptionCompetenceArbre2() {
        System.out.println("Invocation de Virgo -- Inflige 150% ATK au Tank ennemi. Etourdit la cible pendant 2 tours");
    }
}