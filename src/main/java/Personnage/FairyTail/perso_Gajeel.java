package Personnage.FairyTail;

import Combat.Combat;
import Effets.ReductionDefense;
import Personnage.PersonnageBase;
import java.util.List;

public class perso_Gajeel extends PersonnageBase {

    public perso_Gajeel() {
        this.nom = "Gajeel";
        this.type = "ChasseurDeDragon";
        this.role = "DPS";
        this.rarete = "A";
        this.niveau = 1;
        double multiplicateurRarete = 1.30;
        this.vie = 580 * multiplicateurRarete;
        this.attaque = 220 * multiplicateurRarete;
        this.defense = 130 * multiplicateurRarete;
        this.vitesse = 110 * multiplicateurRarete;
        this.taux_critiques = 0.20;
        this.degat_critiques = 1.40;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.05;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Griffes de fer", "Lance de fer du dragon", "Rugissement du dragon de fer"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gajeel utilise Griffes de fer !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // 30% de chance de réduire la DEF
        if (Math.random() < 0.30) {
            Combat.appliquerEffet(this, cible, new ReductionDefense(0.15, 2), log);
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gajeel utilise Lance de fer du dragon !");
        double degats = this.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Réduction DEF garantie
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.25, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gajeel utilise Rugissement du dragon de fer !");
        // Cible l'ennemi avec le plus de DEF
        PersonnageBase cible = equipeEnnemie.stream()
                .filter(PersonnageBase::estVivant)
                .max(java.util.Comparator.comparingDouble(PersonnageBase::getDefense))
                .orElse(null);

        if (cible == null) return;

        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        double degats = (this.getAttaque() * 2.00) * multiplicateurRage;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Réduction DEF lourde garantie
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.40, 3), log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Griffes de fer — Inflige 110% ATK a la cible. "
                + "30% de chance de reduire sa DEF de 15% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Lance de fer du dragon — Inflige 150% ATK a la cible. "
                + "Reduit sa DEF de 25% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Rugissement du dragon de fer — Inflige 200% ATK a l'ennemi avec le plus de DEF. "
                + "Reduit sa DEF de 40% pendant 3 tours. "
                + "Puissance augmentee par la Rage.");
    }
}