package Personnage.pnj.Chapitre2;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class EnnemiKankuro extends PersonnageBase {

    public EnnemiKankuro() {
        this(17);
    }

    public EnnemiKankuro(int niveau) {
        this.nom    = "Kankuro";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "DPS";
        this.rarete = "B";

        double mult = 1.20;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 160.3 * mult * niv;
        this.attaque =  73.3 * mult * niv;
        this.defense =  43.5 * mult * niv;
        this.vitesse =  62.3 * mult * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.50;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Attaque Directe de Karasu", "Brume Empoisonnee de Sanshouo", "Technique Secrete : Arcane Noire"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " manipule Karasu pour entailler " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        boolean defenseReduite = cible.getEffet(ReductionDefense.class) != null;
        if (defenseReduite) {
            Combat.appliquerEffet(this, cible, new Poison(3, 0.08), log);
            log.add("Synergie ! La garde de l'ennemi etait brisee, le poison penetre profondement (8%/tour) !");
        } else {
            Combat.appliquerEffet(this, cible, new Poison(2, 0.05), log);
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " fait ejecter un gaz toxique violet par sa marionnette Kuroari !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.60;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new Poison(2, 0.06), log);
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " active l'Arcane Noire : Triple Chambre de Fer !");
        PersonnageBase cible = null;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && ennemi.getEffet(Poison.class) != null) {
                cible = ennemi;
                break;
            }
        }
        if (cible == null) {
            for (PersonnageBase ennemi : equipeEnnemie) {
                if (ennemi.estVivant()) { cible = ennemi; break; }
            }
        }
        if (cible == null) return;
        double multiplicateur = cible.getEffet(Poison.class) != null ? 1.90 : 1.40;
        if (multiplicateur == 1.90) log.add("L'ennemi souffre deja du poison ! Les aiguilles font d'affreux ravages !");
        double degats = this.getAttaque() * multiplicateur;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Attaque Directe de Karasu — Inflige 100% ATK. Si la cible a sa Defense Reduite, applique Poison (8%/tour) 3 tours. Sinon Poison (5%/tour) 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Brume Empoisonnee de Sanshouo — Inflige 60% ATK a tous les ennemis et applique Poison (6%/tour) 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Arcane Noire — Inflige 140% ATK a un ennemi. Les degats passent a 190% si la cible est deja Empoisonnee.");
    }
}