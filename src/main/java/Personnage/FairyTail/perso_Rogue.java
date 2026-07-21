package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class perso_Rogue extends PersonnageBase {

    public perso_Rogue() {
        this.nom = "Rogue";
        this.type = "ChasseurDeDragon";
        this.role = "Tank";
        this.rarete = "S";
        this.niveau = 1;
        double multiplicateurRarete = 1.50;
        this.vie = 640 * multiplicateurRarete;
        this.attaque = 200 * multiplicateurRarete;
        this.defense = 150 * multiplicateurRarete;
        this.vitesse = 125 * multiplicateurRarete;
        this.taux_critiques = 0.20;
        this.degat_critiques = 1.40;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.18;
        this.taux_blocage      = 0.15;
        this.reduction_blocage = 0.20;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Griffe de l'ombre", "Tranchant des tenebres", "Eclipse du dragon noir"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Rogue utilise Griffe de l'ombre !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Rogue utilise Tranchant des tenebres !");
        double degats = this.getAttaque() * 1.40;
        if (cible.aEffet(Marquage.class)) {
            degats *= 1.40;
            log.add("La cible est marquee ! Rogue dechire les ombres pour +40% de degats !");
        }
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, this, new ContreAttaque(2, 0.60), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Rogue utilise Eclipse du dragon noir !");
        PersonnageBase cible = equipeEnnemie.stream()
                .filter(PersonnageBase::estVivant)
                .max(java.util.Comparator.comparingDouble(PersonnageBase::getVie))
                .orElse(null);
        if (cible == null) return;

        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        double degats = (this.getAttaque() * 1.80) * multiplicateurRage;
        if (cible.aEffet(Marquage.class)) {
            degats *= 1.50;
            log.add("Lumiere et Ombre fusionnent ! Degats multiplies par 1.5 sur la cible marquee !");
        }
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, this, new Invincibilite(1), log);
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new BuffTauxEsquive(0.10, 2), log);
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Griffe de l'ombre — Inflige 110% ATK a la cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Tranchant des tenebres — Inflige 140% ATK a la cible. "
                + "Si la cible est Marquee : +40% degats. "
                + "Rogue gagne Contre-Attaque a 60% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Eclipse du dragon noir — Inflige 180% ATK a l'ennemi avec le plus de PV. "
                + "Si la cible est Marquee : degats multiplies par 1.5. "
                + "Rogue devient invincible 1 tour. "
                + "Toute l'equipe gagne +10% esquive pendant 2 tours. "
                + "Puissance augmentee par la Rage.");
    }
}