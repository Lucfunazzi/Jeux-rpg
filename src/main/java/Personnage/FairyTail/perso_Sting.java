package Personnage.FairyTail;

import Combat.Combat;
import Effets.Marquage;
import Effets.BuffAttaque;
import Effets.BuffTauxCritique;
import Personnage.PersonnageBase;
import java.util.List;

public class perso_Sting extends PersonnageBase {

    public perso_Sting() {
        this.nom = "Sting";
        this.type = "Mage";
        this.role = "DPS";
        this.rarete = "S";
        this.niveau = 1;
        double multiplicateurRarete = 1.50;
        this.vie = 560 * multiplicateurRarete;
        this.attaque = 240 * multiplicateurRarete;
        this.defense = 115 * multiplicateurRarete;
        this.vitesse = 140 * multiplicateurRarete;
        this.taux_critiques = 0.25;
        this.degat_critiques = 1.50;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Griffe du dragon blanc", "Rugissement du dragon sacré", "Frappe de lumière absolue"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sting utilise Griffe du dragon blanc !");
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Marque la cible pour que Rogue fasse plus de dégâts dessus
        Combat.appliquerEffet(this, cible, new Marquage(2, 0.25), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sting utilise Rugissement du dragon sacré !");
        double degats = this.getAttaque() * 1.60;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Marque la cible + bonus dégâts critiques à Sting
        Combat.appliquerEffet(this, cible, new Marquage(2, 0.35), log);
        Combat.appliquerEffet(this, this, new BuffTauxCritique(0.20, 2), log);
        // Synergie : si Rogue est allié vivant, le marquage dure 1 tour de plus
        for (PersonnageBase allie : equipeAlliee) {
            if (allie instanceof perso_Rogue && allie.estVivant()) {
                log.add("Synergie Lumière & Ombre : le marquage est renforcé par la présence de Rogue !");
                Combat.appliquerEffet(this, cible, new Marquage(1, 0.10), log);
                break;
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sting utilise Frappe de lumière absolue !");
        // Cible l'ennemi avec le plus de PV (burst sur le tank)
        PersonnageBase cible = equipeEnnemie.stream()
                .filter(PersonnageBase::estVivant)
                .max(java.util.Comparator.comparingDouble(PersonnageBase::getVie))
                .orElse(null);
        if (cible == null) return;

        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        double degats = (this.getAttaque() * 2.20) * multiplicateurRage;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Marquage(3, 0.40), log);
        // Buff ATK à Sting après l'ultime
        Combat.appliquerEffet(this, this, new BuffAttaque(0.20, 2), log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Griffe du dragon blanc — Inflige 120% ATK a la cible. "
                + "Applique Marquage (+25% degats recus) pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Rugissement du dragon sacre — Inflige 160% ATK a la cible. "
                + "Applique Marquage (+35% degats recus) pendant 2 tours. "
                + "Sting gagne +20% taux critique pendant 2 tours. "
                + "Si Rogue est allie : marquage renforce.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Frappe de lumiere absolue — Inflige 220% ATK a l'ennemi avec le plus de PV. "
                + "Applique Marquage (+40% degats recus) pendant 3 tours. "
                + "Sting gagne +20% ATK pendant 2 tours. "
                + "Puissance augmentee par la Rage.");
    }
}