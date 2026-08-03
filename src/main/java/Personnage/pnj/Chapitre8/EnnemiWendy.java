package Personnage.pnj.Chapitre8;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wendy — version examinatrice (Chapitre 8, stage 6 : examen de rang S,
 * duel contre Gray et Loki aux côtés de Mest). Meme identite et memes
 * attaques que perso_Wendy (support pur, aucun sort offensif).
 */
public class EnnemiWendy extends PersonnageBase {

    public EnnemiWendy() { this(95); }

    public EnnemiWendy(int niveau) {
        this.nom    = "Wendy";
        this.niveau = niveau;
        this.type   = "ChasseurDeDragon";
        this.role   = "Support";
        this.rarete = "A";

        double mult = 1.50;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 500.0 * mult * niv;
        this.attaque = 165.0 * mult * niv;
        this.defense = 115.0 * mult * niv;
        this.vitesse = 120.0 * mult * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.06;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Brise de soin", "Vent de guérison", "Tempête céleste"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Wendy utilise Brise de soin !");
        PersonnageBase cibleSoin = equipeAlliee.stream()
                .filter(PersonnageBase::estVivant)
                .min(Comparator.comparingDouble(PersonnageBase::getVie))
                .orElse(null);
        if (cibleSoin == null) return;
        double montantSoin = this.getAttaque() * 0.40;
        cibleSoin.recevoirSoin(montantSoin, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Wendy utilise Vent de guérison !");
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
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Wendy utilise Tempête céleste !");
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                double montantSoin = this.getAttaque() * 0.50;
                allie.recevoirSoin(montantSoin, log);
                Purification.purifier(allie, 1, log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Brise de soin — Soigne un allié de 40% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Vent de guérison — Soigne les 2 alliés avec le moins de PV de 70% ATK. Purifie 2 états négatifs sur chacun.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Tempête céleste — Soigne toute l'équipe de 50% ATK. Purifie 1 état négatif sur chaque allié.");
    }
}
