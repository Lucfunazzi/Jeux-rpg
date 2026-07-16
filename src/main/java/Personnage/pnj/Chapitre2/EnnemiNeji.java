package Personnage.pnj.Chapitre2;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class EnnemiNeji extends PersonnageBase {

    public EnnemiNeji() {
        this(22);
    }

    public EnnemiNeji(int niveau) {
        this.nom    = "Neji";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "Tank";
        this.rarete = "A";

        double mult = 1.40;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 222.5 * mult * niv;
        this.attaque =  43.1 * mult * niv;
        this.defense =  46.7 * mult * niv;
        this.vitesse =  51.1 * mult * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.15;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = 0.15;
        this.reduction_blocage = 0.40;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Juken: Le Poing Souple", "Les 64 Poings du Hakke", "Hakkesho Kaiten"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Neji frappe les cavites de chakra avec le Juken !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.10, 2), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Neji utilise la technique supreme : Les 64 Poings du Hakke !");
        double degats = this.getAttaque() * 1.30;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.15, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Neji crie : Hakkesho Kaiten ! Il tourbillonne pour repousser toutes les attaques !");
        double montantBouclier = this.getVieMax() * 0.35;
        Combat.appliquerEffet(this, new Bouclier(montantBouclier), log);
        Combat.appliquerEffet(this, new BuffBlocage(0.15, 2), log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Juken: Le Poing Souple — Inflige 100% ATK a une cible et reduit sa Defense de 10% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Les 64 Poings du Hakke — Inflige 130% ATK a une cible et reduit son ATK de 15% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Hakkesho Kaiten — Octroie un Bouclier de 35% des PV max et augmente le Taux de Blocage de 15% pendant 2 tours.");
    }
}