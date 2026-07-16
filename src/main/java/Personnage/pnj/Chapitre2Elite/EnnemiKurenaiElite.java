package Personnage.pnj.Chapitre2Elite;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class EnnemiKurenaiElite extends PersonnageBase {

    public EnnemiKurenaiElite() {
        this(31);
    }

    public EnnemiKurenaiElite(int niveau) {
        this.nom    = "Kurenai";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "Support";
        this.rarete = "A";

        double mult = 1.60;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 169.6 * mult * niv;
        this.attaque =  100.2 * mult * niv;
        this.defense =  41.5 * mult * niv;
        this.vitesse =  110.7 * mult * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.15;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Camouflage de la Brume", "Illusion de la Liaison de Mort", "Genjutsu: Mirage Vegetal"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Kurenai utilise Camouflage de la Brume !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Kurenai utilise Illusion de la Liaison de Mort !");
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionVitesse(0.15, 2), log);
        Combat.appliquerEffet(this, cible, new Sommeil(2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Kurenai declenche son Genjutsu: Mirage Vegetal !");
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new BuffTauxEsquive(0.30, 2), log);
                Combat.appliquerEffet(this, allie, new BuffDefense(0.15, 2), log);
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Camouflage de la Brume — Inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Illusion de la Liaison de Mort — Inflige 120% ATK a une cible, reduit sa Vitesse de 15% pendant 2 tours et lui applique Sommeil 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Genjutsu: Mirage Vegetal — Augmente l'Esquive de 30% et la Defense de 15% de tous les allies pendant 2 tours.");
    }
}