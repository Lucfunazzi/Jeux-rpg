package Joueur;

import Personnage.PersonnageBase;
import Effets.*;
import Combat.Combat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Classe Mage — thème constellationniste / esprits célestes Fairy Tail.
 * Compétences génériques inspirées de la magie des esprits et des éléments.
 * Ultime rang C : puissance modeste, conçue pour progresser via l'arbre.
 */
public class Mage implements Competences {

    @Override
    public String[] getNomsCompetences() {
        return new String[]{
            "Hurlement du dragon de feu",
            "Invocation : Taurus",
            "Geyser de Glace"
        };
    }

    // Comp 1 — Frappe de feu mono-cible + brulure + buff ATK léger
    @Override
    public void choix_competence1(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hurlement du dragon de feu !");
        double degats = utilisateur.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(utilisateur, cible, degats, log);
        Combat.appliquerEffet(utilisateur, cible, new Brulure(2, 0.05), log);
        Combat.appliquerEffet(utilisateur, new BuffAttaque(0.05, 2), log);
    }

    // Comp 2 — Taurus frappe les 2 cibles les plus basses en PV + buff DEF équipe
    @Override
    public void choix_competence2(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Invocation : Taurus !");
        ArrayList<PersonnageBase> ennemisTries = new ArrayList<>(equipeEnnemie);
        ennemisTries.sort(Comparator.comparingDouble(PersonnageBase::getVie));
        int ciblesAttaquees = 0;
        for (PersonnageBase ennemi : ennemisTries) {
            if (ennemi.estVivant() && ciblesAttaquees < 2) {
                double degats = utilisateur.getAttaque() * 0.90;
                Combat.appliquerDegatsAvecLog(utilisateur, ennemi, degats, log);
                if (Math.random() < 0.40) {
                    Combat.appliquerEffet(utilisateur, ennemi, new Etourdissement(1), log);
                }
                ciblesAttaquees++;
            }
        }
        // Taurus protège aussi l'équipe
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(utilisateur, allie, new BuffDefense(0.08, 2), log);
            }
        }
    }

    // Comp 3 — Geyser de glace : ralentissement garanti + chance de gel
    @Override
    public void choix_competence3(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Geyser de Glace !");
        double degats = utilisateur.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(utilisateur, cible, degats, log);
        Combat.appliquerEffet(utilisateur, cible, new ReductionVitesse(0.20, 2), log);
        if (Math.random() < 0.30) {
            Combat.appliquerEffet(utilisateur, cible, new Gel(1), log);
        }
    }

    // Ultime rang C — AoE glace modéré + petit soin équipe, pas surpuissant
    @Override
    public void ultime(PersonnageBase utilisateur, List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Averse stellaire !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = utilisateur.getAttaque() * 0.80;
                Combat.appliquerDegatsAvecLog(utilisateur, ennemi, degats, log);
                Combat.appliquerEffet(utilisateur, ennemi, new ReductionVitesse(0.10, 1), log);
            }
        }
        // Soin léger sur l'allie le plus bas
        PersonnageBase plusBas = equipeAlliee.stream()
                .filter(PersonnageBase::estVivant)
                .min(Comparator.comparingDouble(PersonnageBase::getVie))
                .orElse(null);
        if (plusBas != null) {
            double soin = plusBas.getVieMax() * 0.10;
            plusBas.recevoirSoin(soin, log);
        }
    }

    // Compétence arbre 1 — Nova Arcanique (deja implemente)
    @Override
    public void competenceArbre(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Nova Arcanique !");
        double degats = utilisateur.getAttaque() * 1.80;
        Combat.appliquerDegatsAvecLog(utilisateur, cible, degats, log);
        Combat.appliquerEffet(utilisateur, cible, new Silence(1), log);
    }

    // Compétence arbre 2 — Tempete Cristalline (deja implemente)
    @Override
    public void competenceArbre2(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tempete Cristalline !");
        int touche = 0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && touche < 3) {
                double degats = utilisateur.getAttaque() * 1.60;
                Combat.appliquerDegatsAvecLog(utilisateur, ennemi, degats, log);
                if (Math.random() < 0.40)
                    Combat.appliquerEffet(utilisateur, ennemi, new Gel(1), log);
                Combat.appliquerEffet(utilisateur, ennemi, new ReductionDefense(0.15, 2), log);
                touche++;
            }
        }
        PersonnageBase plusBas = equipeAlliee.stream()
                .filter(PersonnageBase::estVivant)
                .min(Comparator.comparingDouble(PersonnageBase::getVie))
                .orElse(null);
        if (plusBas != null) {
            double soin = plusBas.getVieMax() * 0.20;
            plusBas.recevoirSoin(soin, log);
            log.add(plusBas.getNom() + " recoit " + String.format("%.1f", soin) + " PV !");
        }
    }

    @Override
    public void descriptionCompetence1() {
        System.out.println("Hurlement du dragon de feu — Inflige 120% ATK a 1 cible. "
                + "Applique Brulure (5% PV/tour) pendant 2 tours. "
                + "Augmente l'ATK du lanceur de 5% pendant 2 tours.");
    }

    @Override
    public void descriptionCompetence2() {
        System.out.println("Invocation : Taurus — Inflige 90% ATK aux 2 cibles avec le moins de PV. "
                + "40% de chance d'Etourdissement 1 tour sur chacune. "
                + "Augmente la DEF de toute l'equipe de 8% pendant 2 tours.");
    }

    @Override
    public void descriptionCompetence3() {
        System.out.println("Geyser de Glace — Inflige 120% ATK a 1 cible. "
                + "Reduit sa vitesse de 20% pendant 2 tours. "
                + "30% de chance de Gel pendant 1 tour.");
    }

    @Override
    public void descriptionUltime() {
        System.out.println("Averse stellaire — Inflige 80% ATK a tous les ennemis. "
                + "Reduit leur vitesse de 10% pendant 1 tour. "
                + "Soigne l'allie le plus bas de 10% de ses PV max.");
    }

    @Override
    public void descriptionCompetenceArbre() {
        System.out.println("Nova Arcanique — Inflige 180% ATK a 1 cible. "
                + "Applique Silence pendant 1 tour.");
    }

    @Override
    public void descriptionCompetenceArbre2() {
        System.out.println("Tempete Cristalline — Inflige 160% ATK aux 3 premieres cibles. "
                + "40% de chance de Gel 1 tour. Reduit leur DEF de 15% pendant 2 tours. "
                + "Soigne l'allie le plus bas de 20% de ses PV max.");
    }
}