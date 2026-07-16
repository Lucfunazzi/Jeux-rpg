package Joueur;

import Personnage.PersonnageBase;
import Effets.*;
import Combat.Combat;
import java.util.List;

/**
 * Classe Ninja — thème ninjutsu élémentaire Naruto.
 * Compétences génériques : feu, eau, foudre. Pas liées à un personnage précis.
 * Ultime rang C : puissance modeste, conçue pour progresser via l'arbre.
 */
public class Ninja implements Competences {

    @Override
    public String[] getNomsCompetences() {
        return new String[]{
            "Katon : Boule de feu supreme",
            "Suiton : Dragon aqueux",
            "Raiton : Eclair de precision"
        };
    }

    // Comp 1 — Katon : dégâts feu + brulure 50%
    @Override
    public void choix_competence1(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Katon : Boule de feu supreme !");
        double degats = utilisateur.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(utilisateur, cible, degats, log);
        if (Math.random() < 0.50) {
            Combat.appliquerEffet(utilisateur, cible, new Brulure(2, 0.05), log);
        }
    }

    // Comp 2 — Suiton : dégâts eau + ralentissement garanti
    @Override
    public void choix_competence2(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Suiton : Dragon aqueux !");
        double degats = utilisateur.getAttaque() * 1.30;
        Combat.appliquerDegatsAvecLog(utilisateur, cible, degats, log);
        Combat.appliquerEffet(utilisateur, cible, new ReductionVitesse(0.15, 3), log);
    }

    // Comp 3 — Raiton : frappe précise + buff précision + chance paralysie
    @Override
    public void choix_competence3(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Raiton : Eclair de precision !");
        double degats = utilisateur.getAttaque() * 1.40;
        Combat.appliquerDegatsAvecLog(utilisateur, cible, degats, log);
        Combat.appliquerEffet(utilisateur, new BuffPrecision(1.00, 2), log);
        if (Math.random() < 0.25) {
            Combat.appliquerEffet(utilisateur, cible, new Paralysie(1,0.30), log);
        }
    }

    // Ultime rang C — Multi-frappe modeste sur une cible + buff ATK léger
    @Override
    public void ultime(PersonnageBase utilisateur, List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Mille coups silencieux !");
        // Cible priorité : Tank > DPS > Support
        PersonnageBase cible = null;
        for (String role : new String[]{"Tank", "DPS", "Support"}) {
            for (PersonnageBase ennemi : equipeEnnemie) {
                if (ennemi.estVivant() && ennemi.getRole().equals(role)) {
                    cible = ennemi;
                    break;
                }
            }
            if (cible != null) break;
        }

        if (cible != null) {
            for (int i = 0; i < 3; i++) {
                if (!cible.estVivant()) break;
                double degats = utilisateur.getAttaque() * 0.70;
                Combat.appliquerDegatsAvecLog(utilisateur, cible, degats, log);
            }
        } else {
            log.add("Aucune cible ennemie valide !");
        }
        Combat.appliquerEffet(utilisateur, new BuffAttaque(0.15, 2), log);
    }

    // Compétence arbre 1 — Substitution Meurtrière (deja implemente)
    @Override
    public void competenceArbre(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Substitution Meurtriere !");
        double degats = utilisateur.getAttaque() * 1.60;
        Combat.appliquerDegatsAvecLog(utilisateur, cible, degats, log);
        Combat.appliquerEffet(utilisateur, new BuffTauxEsquive(1.00, 1), log);
    }

    // Compétence arbre 2 — Tourbillon de Lames (deja implemente)
    @Override
    public void competenceArbre2(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tourbillon de Lames !");
        for (int i = 0; i < 3; i++) {
            if (!cible.estVivant()) break;
            double degats = utilisateur.getAttaque() * 0.90;
            Combat.appliquerDegatsAvecLog(utilisateur, cible, degats, log);
        }
        if (cible.estVivant()) {
            Combat.appliquerEffet(utilisateur, cible, new Saignement(3, 0.06), log);
            Combat.appliquerEffet(utilisateur, cible, new ReductionAttaque(0.15, 2), log);
        }
        Combat.appliquerEffet(utilisateur, new BuffTauxEsquive(0.30, 2), log);
    }

    @Override
    public void descriptionCompetence1() {
        System.out.println("Katon : Boule de feu supreme — Inflige 120% ATK a 1 cible. "
                + "50% de chance d'infliger Brulure (5% PV/tour) pendant 2 tours.");
    }

    @Override
    public void descriptionCompetence2() {
        System.out.println("Suiton : Dragon aqueux — Inflige 130% ATK a 1 cible. "
                + "Reduit sa vitesse de 15% pendant 3 tours.");
    }

    @Override
    public void descriptionCompetence3() {
        System.out.println("Raiton : Eclair de precision — Inflige 140% ATK a 1 cible. "
                + "Augmente la precision du lanceur de 100% pendant 2 tours. "
                + "25% de chance de Paralysie pendant 1 tour.");
    }

    @Override
    public void descriptionUltime() {
        System.out.println("Mille coups silencieux — Inflige 3 frappes de 70% ATK a une cible. "
                + "Augmente l'ATK du lanceur de 15% pendant 2 tours.");
    }

    @Override
    public void descriptionCompetenceArbre() {
        System.out.println("Substitution Meurtriere — Inflige 160% ATK a 1 cible. "
                + "Accorde une esquive garantie le tour suivant.");
    }

    @Override
    public void descriptionCompetenceArbre2() {
        System.out.println("Tourbillon de Lames — Inflige 3 frappes de 90% ATK a 1 cible. "
                + "Applique Saignement (6% PV/tour) pendant 3 tours. "
                + "Reduit l'ATK de la cible de 15% pendant 2 tours. "
                + "Augmente l'esquive du lanceur de 30% pendant 2 tours.");
    }
}