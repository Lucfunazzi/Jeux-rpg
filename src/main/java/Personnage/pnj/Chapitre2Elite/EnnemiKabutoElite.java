package Personnage.pnj.Chapitre2Elite;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class EnnemiKabutoElite extends PersonnageBase {

    public EnnemiKabutoElite() {
        this(28);
    }

    public EnnemiKabutoElite(int niveau) {
        this.nom    = "Kabuto";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "Support";
        this.rarete = "B";

        double mult = 1.50;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 450.2 * mult * niv;
        this.attaque =  120.4 * mult * niv;
        this.defense =  50.2 * mult * niv;
        this.vitesse =  220.7 * mult * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Scalpel Chakra", "Analyse Medicale", "Regeneration du Ryuchi"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Kabuto tranche avec son scalpel de chakra !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.08, 2), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Kabuto analyse son allie et canalise son chakra medical !");
        PersonnageBase cibleSoin = null;
        double vieLaPlusBasse = Double.MAX_VALUE;
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant() && allie.getVie() < vieLaPlusBasse) {
                vieLaPlusBasse = allie.getVie();
                cibleSoin = allie;
            }
        }
        if (cibleSoin != null) {
            double soin = this.getAttaque() * 0.30;
            cibleSoin.recevoirSoin(soin, log);
            Combat.appliquerEffet(this, cibleSoin, new BuffDefense(0.10, 2), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Kabuto canalise le chakra du Ryuchi — il soigne toute l'equipe !");
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                double soin = this.getAttaque() * 0.20;
                allie.recevoirSoin(soin, log);
                Combat.appliquerEffet(this, allie, new BuffAttaque(0.10, 2), log);
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Scalpel Chakra — Inflige 100% ATK a une cible et reduit son ATK de 8% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Analyse Medicale — Soigne l'allie le plus bas en PV de 30% ATK et augmente sa Defense de 10% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Regeneration du Ryuchi — Soigne toute l'equipe de 20% ATK et augmente leur ATK de 10% pendant 2 tours.");
    }
}