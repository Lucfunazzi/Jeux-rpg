package Personnage.pnj.Chapitre2Elite;

import Combat.Combat;
import Effets.BuffAttaque;
import Effets.BuffDegatCritique;
import Effets.Etourdissement;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class EnnemiSakuraElite extends PersonnageBase {

    public EnnemiSakuraElite() {
        this(29);
    }

    public EnnemiSakuraElite(int niveau) {
        this.nom    = "Sakura";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "DPS";
        this.rarete = "A";

        double mult = 1.60;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 257.6 * mult * niv;
        this.attaque =  87.3 * mult * niv;
        this.defense =  41.6 * mult * niv;
        this.vitesse =  76.4 * mult * vit;

        this.taux_critiques    = 0.20;
        this.degat_critiques   = 1.45;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Poings du cerisier", "Impact de la fleur du cerisier", "Force amelioree au chakra"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sakura utilise Poings du cerisier !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sakura utilise Impact de la fleur du cerisier !");
        double degats = this.getAttaque() * 1.40;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (Math.random() < 0.35) {
            Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
        }
        PersonnageBase cibleSoin = null;
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                if (cibleSoin == null || allie.getVie() < cibleSoin.getVie())
                    cibleSoin = allie;
            }
        }
        if (cibleSoin != null) {
            double soin = this.getAttaque() * 0.60;
            cibleSoin.recevoirSoin(soin, log);
            log.add("Sakura soigne " + cibleSoin.getNom() + " de " + String.format("%.1f", soin) + " PV !");
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sakura utilise Force amelioree au chakra !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        Combat.appliquerEffet(this, new BuffAttaque(0.20, 2), log);
        Combat.appliquerEffet(this, new BuffDegatCritique(0.10, 2), log);
        PersonnageBase cible = null;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                if (cible == null || ennemi.getAttaque() > cible.getAttaque())
                    cible = ennemi;
            }
        }
        if (cible == null) return;
        double degats = (this.getAttaque() * 1.30) * multiplicateurRage;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Poings du cerisier — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Impact de la fleur du cerisier — inflige 140% ATK a une cible, 35% de chance d'etourdir 1 tour, soigne l'allie avec le moins de PV a 60% ATK.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Force amelioree au chakra — gagne 20% ATK et 10% degats critiques pendant 2 tours, puis inflige 130% ATK a l'ennemi avec la plus haute ATK.");
    }
}