package Personnage.pnj.Chapitre2Elite;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class EnnemiAsumaElite extends PersonnageBase {

    public EnnemiAsumaElite() {
        this(33);
    }

    public EnnemiAsumaElite(int niveau) {
        this.nom    = "Asuma";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "Support";
        this.rarete = "A";

        double mult = 1.60;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 205.1 * mult * niv;
        this.attaque =  46.1 * mult * niv;
        this.defense =  39.3 * mult * niv;
        this.vitesse =  57.4 * mult * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.25;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Lames de Chakra", "Nuee de Cendres", "Style de Konoha : Tranche Tactique"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Asuma attaque avec ses Lames de Chakra !");
        boolean touche = Combat.attaqueTouche(this, cible);
        if (!touche) {
            log.add(cible.getNom() + " esquive !");
            this.ajouterRage(50);
            return;
        }
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Saignement(2, 0.05), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Asuma utilise Nuee de Cendres (Katon: Haisekisho) !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.10;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new Brulure(2, 0.05), log);
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Asuma dirige la strategie : Style de Konoha - Tranche Tactique !");
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new BuffAttaque(0.20, 2), log);
                Combat.appliquerEffet(this, allie, new BuffVitesse(0.20, 2), log);
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Lames de Chakra — inflige 100% ATK a une cible et lui inflige Saignement (5% PV/tour) pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Nuee de Cendres — inflige 110% ATK a tous les ennemis et leur applique Brulure (5% PV/tour) pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Tranche Tactique — Accorde +20% ATK et +20% Vitesse a tous les allies pendant 2 tours.");
    }
}