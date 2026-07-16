package Personnage.DragonBallZ;

import Combat.Combat;
import Effets.Marquage;
import Effets.ReductionDefense;
import Effets.ReductionAttaque;
import Effets.BuffAttaque;
import Personnage.PersonnageBase;
import java.util.List;

public class perso_Vegeta_detecteur extends PersonnageBase {

    public perso_Vegeta_detecteur() {
        this.nom = "Vegeta";
        this.type = "Guerrier";
        this.role = "DPS";
        this.rarete = "A";
        this.niveau = 1;
        double multiplicateurRarete = 1.40;
        this.vie = 520 * multiplicateurRarete;
        this.attaque = 210 * multiplicateurRarete;
        this.defense = 100 * multiplicateurRarete;
        this.vitesse = 125 * multiplicateurRarete;
        this.taux_critiques = 0.20;
        this.degat_critiques = 1.40;
        this.taux_precisions = 110.00;
        this.taux_esquives = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Galick Gun", "Canon Finale", "Big Bang Attack"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Vegeta utilise Galick Gun !");
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Vegeta detecte et marque sa cible
        Combat.appliquerEffet(this, cible, new Marquage(2, 0.20), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Vegeta utilise Canon Finale !");
        double degats = this.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Reduit l'ATK de la cible (Vegeta exploite la faiblesse detectee)
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.20, 2), log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.20, 2), log);
        // Vegeta se buff si la cible est deja marquee
        if (cible.aEffet(Marquage.class)) {
            Combat.appliquerEffet(this, this, new BuffAttaque(0.20, 2), log);
            log.add("Vegeta exploite le marquage pour se renforcer !");
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Vegeta utilise Big Bang Attack !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        // Cible l'ennemi avec le plus d'ATK (Vegeta elimine la menace principale)
        PersonnageBase cible = equipeEnnemie.stream()
                .filter(PersonnageBase::estVivant)
                .max(java.util.Comparator.comparingDouble(PersonnageBase::getAttaque))
                .orElse(null);
        if (cible == null) return;
        double degats = (this.getAttaque() * 2.00) * multiplicateurRage;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Marquage(3, 0.30), log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.25, 3), log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Galick Gun — Inflige 120% ATK a la cible. "
                + "Applique Marquage (+20% degats recus) pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Canon Finale — Inflige 150% ATK a la cible. "
                + "Reduit son ATK et sa DEF de 20% pendant 2 tours. "
                + "Si la cible est Marquee : Vegeta gagne +20% ATK pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Big Bang Attack — Inflige 200% ATK a l'ennemi avec la plus haute ATK. "
                + "Applique Marquage (+30% degats recus) pendant 3 tours. "
                + "Reduit son ATK de 25% pendant 3 tours. "
                + "Puissance augmentee par la Rage.");
    }
}