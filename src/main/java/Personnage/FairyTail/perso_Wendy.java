package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class perso_Wendy extends PersonnageBase {

    public perso_Wendy() {
        this.nom = "Wendy";
        this.type = "ChasseurDeDragon";
        this.role = "Support";
        this.rarete = "A";
        this.niveau = 1;
        double multiplicateurRarete = 1.30;
        this.vie = 520 * multiplicateurRarete;
        this.attaque = 140 * multiplicateurRarete;
        this.defense = 100 * multiplicateurRarete;
        this.vitesse = 135 * multiplicateurRarete;
        this.taux_critiques = 0.10;
        this.degat_critiques = 1.20;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.12;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Brise de soin", "Vent de guérison", "Tempête céleste"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Wendy utilise Brise de soin !");
        // Soin très léger sur l'allié le plus bas en vie (cible est un ennemi, jamais un allié)
        PersonnageBase cibleSoin = equipeAlliee.stream()
                .filter(PersonnageBase::estVivant)
                .min(Comparator.comparingDouble(PersonnageBase::getVie))
                .orElse(null);
        if (cibleSoin == null) return;
        double montantSoin = this.getAttaque() * 0.40;
        cibleSoin.recevoirSoin(montantSoin, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Wendy utilise Vent de guérison !");
        // Soin moyen sur les 2 alliés avec le moins de PV + purification 2 états négatifs
        List<PersonnageBase> vivants = equipeAlliee.stream()
                .filter(PersonnageBase::estVivant)
                .sorted(Comparator.comparingDouble(PersonnageBase::getVie))
                .collect(Collectors.toList());

        int soignes = 0;
        for (PersonnageBase allie : vivants) {
            if (soignes >= 2) break;
            double montantSoin = this.getAttaque() * 0.70;
            allie.recevoirSoin(montantSoin, log);
            Purification.purifier(allie, 2, log);
            soignes++;
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Wendy utilise Tempête céleste !");
        // Soin léger sur toute l'équipe + purification 1 état négatif
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                double montantSoin = this.getAttaque() * 0.50;
                allie.recevoirSoin(montantSoin, log);
                Purification.purifier(allie, 1, log);
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Brise de soin — Soigne un allie de 40% ATK.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Vent de guerison — Soigne les 2 allies avec le moins de PV de 70% ATK. "
                + "Purifie 2 etats negatifs sur chacun.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Tempete celeste — Soigne toute l'equipe de 50% ATK. "
                + "Purifie 1 etat negatif sur chaque allie.");
    }
}