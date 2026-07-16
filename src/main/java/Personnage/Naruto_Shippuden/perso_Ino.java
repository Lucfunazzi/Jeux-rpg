package Personnage.Naruto_Shippuden;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class perso_Ino extends PersonnageBase {

    public perso_Ino() {
        this.nom = "Ino";
        this.niveau = 1;
        this.type = "Ninja";
        this.role = "Support";
        this.rarete = "B";

        double multiplicateurRarete = 1.30;
        this.vie     = 450 * multiplicateurRarete;
        this.attaque =  95 * multiplicateurRarete;
        this.defense =  90 * multiplicateurRarete;
        this.vitesse = 100 * multiplicateurRarete;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Claque de la Fleur", "Grande Transposition", "Ninjutsu Medical"};
    }

    // ── Attaque de base — 100% ATK
    @Override
     public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log){
        log.add("Ino utilise Claque de la Fleur !");
        Combat.attaquer(this, cible, log);
    }

    // ── Spéciale — 60% ATK + Confusion 2 tours
    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ino utilise Grande Transposition !");
        double degats = this.getAttaque() * 0.60;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Confusion(2), log);
    }

    // ── Ultime — Soin 130% ATK sur l'allié le plus bas en PV
    // Synergie : si Choji ou Shikamaru vivants → soigne les 2 alliés les plus bas
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ino utilise Ninjutsu Medical !");

        boolean synergieEquipe10 = false;
        for (PersonnageBase allie : equipeAlliee) {
            if ((allie.getNom().equals("Choji") || allie.getNom().equals("Shikamaru"))
                    && allie.estVivant()) {
                synergieEquipe10 = true;
                break;
            }
        }

        double soin = this.getAttaque() * 1.30;
        int nbCibles = synergieEquipe10 ? 2 : 1;

        if (synergieEquipe10) {
            log.add("[Synergie Equipe 10] Ino soigne les " + nbCibles + " allies les plus blesses !");
        }

        // Trier les alliés vivants par PV croissants
        List<PersonnageBase> vivants = new ArrayList<>();
        for (PersonnageBase allie : equipeAlliee)
            if (allie.estVivant()) vivants.add(allie);
        vivants.sort(Comparator.comparingDouble(PersonnageBase::getVie));

        int soignes = 0;
        for (PersonnageBase cible : vivants) {
            if (soignes >= nbCibles) break;
            cible.recevoirSoin(soin, log);            soignes++;
        }
        Purification.purifierEquipe(equipeAlliee, 1, log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Claque de la Fleur — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Grande Transposition — inflige 60% ATK a une cible et lui applique Confusion pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Ninjutsu Medical — soigne l'allie le plus blesse de 130% ATK. "
                + "[Synergie Equipe 10] Si Choji ou Shikamaru sont vivants : soigne les 2 allies les plus blesses.");
    }
}