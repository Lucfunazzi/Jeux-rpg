package Personnage.pnj.Chapitre2Elite;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class EnnemiGaaraElite extends PersonnageBase {

    public EnnemiGaaraElite() {
        this(37);
    }

    public EnnemiGaaraElite(int niveau) {
        this.nom    = "Gaara";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "Tank";
        this.rarete = "S";

        double mult = 1.70;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 400.6 * mult * niv;
        this.attaque =  45.7 * mult * niv;
        this.defense =  120.7 * mult * niv;
        this.vitesse =  41.7 * mult * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.50;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = 0.20;
        this.reduction_blocage = 0.30;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Bouclier de Sable Impenetrable", "Armure de Sable protectrice", "Tombeau du Desert Eternel"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " projette une vague de sable lourd sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, new BuffBlocage(0.20, 2), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " dresse un rempart de sable gigantesque devant ses allies !");
        double montantBouclier = this.getVieMax() * 0.30;
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new Bouclier(montantBouclier), log);
            }
        }
        log.add("Toute l'equipe recoit un Bouclier de " + String.format("%.1f", montantBouclier) + " PV !");
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " declenche les Funerailles Imperiales du Desert !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double ratioDegats = 1.10;
                boolean aPoison = ennemi.getEffet(Poison.class) != null;
                boolean aReductionDef = ennemi.getEffet(ReductionDefense.class) != null;
                if (aPoison || aReductionDef) {
                    ratioDegats += 0.40;
                    log.add("Le sable s'engouffre dans les failles de " + ennemi.getNom() + " ! Degats augmentes !");
                }
                double degats = (this.getAttaque() * ratioDegats) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new ReductionVitesse(0.20, 2), log);
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Bouclier de Sable Impenetrable — Inflige 100% ATK a un ennemi et augmente le Taux de Blocage de Gaara de 20% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Armure de Sable protectrice — Protege toute l'equipe avec un Bouclier de 30% des PV max de Gaara.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Tombeau du Desert Eternel — Attaque de zone 110% ATK. Degats passes a 150% sur les cibles avec Poison ou ReductionDefense. Reduit la Vitesse de 20% pendant 2 tours.");
    }
}