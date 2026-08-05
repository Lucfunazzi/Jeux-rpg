package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Sugarboy — Chevalier, rang A.
 * Ancien soldat de l'Armée Royale d'Edolas, capable d'avaler et d'absorber la magie.
 */
public class perso_Sugarboy extends PersonnageBase {

    public perso_Sugarboy() {
        this.nom    = "Sugarboy";
        this.type   = "Chevalier";
        this.role   = "Tank";
        this.rarete = "A";
        this.niveau = 1;
        double mult = 1.48;
        this.vie     = 580 * mult;
        this.attaque = 140 * mult;
        this.defense = 160 * mult;
        this.vitesse =  90 * mult;
        this.taux_critiques    = 0.06;
        this.degat_critiques   = 1.15;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = 0.18;
        this.reduction_blocage = 0.22;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de Bouclier", "Absorption Magique", "Vague Dévorante"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sugarboy percute l'équipe ennemie d'un Coup de Bouclier !");
        List<PersonnageBase> vivants = new java.util.ArrayList<>();
        for (PersonnageBase ennemi : equipeEnnemie) if (ennemi.estVivant()) vivants.add(ennemi);
        java.util.Collections.shuffle(vivants);
        for (int i = 0; i < Math.min(2, vivants.size()); i++) {
            double degats = this.getAttaque() * 1.50;
            Combat.appliquerDegatsAvecLog(this, vivants.get(i), degats, log);
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sugarboy avale la magie ambiante — Absorption Magique !");
        Combat.appliquerEffet(this, new Bouclier(this.getVieMax() * 0.12), log);
        double degats = this.getAttaque() * 1.05;
        if (cible.aEffet(Saignement.class)) degats *= 1.15;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && ennemi.getRole().equals("DPS")) {
                Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.15, 2), log);
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sugarboy recrache tout ce qu'il a avalé — Vague Dévorante !");
        double degatsTotaux = 0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.90;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                degatsTotaux += degats;
            }
        }
        double soin = degatsTotaux * 0.15;
        if (soin > 0) this.recevoirSoin(soin, log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Espanas — Inflige 150% ATK a deux ennemis.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Absorption Magique — Se protège d'un bouclier (12% PV max) et inflige 105% ATK. et reduit la defense des attaquants de 15%");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Vague Dévorante — Inflige 90% ATK à tous les ennemis et se soigne de 15% des dégâts infligés. ");
    }
}
