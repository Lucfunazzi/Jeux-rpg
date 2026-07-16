package Personnage.Naruto_Shippuden;

import Combat.Combat;
import Effets.*;
import Personnage.Naruto_Shippuden.perso_Sasori;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Deidara extends PersonnageBase {

    public perso_Deidara() {
        this.nom = "Deidara";
        this.type = "Ninja";
        this.role = "DPS";
        this.rarete = "A";
        this.niveau = 1;
        double multiplicateurRarete = 1.40;
        this.vie = 500 * multiplicateurRarete;
        this.attaque = 140 * multiplicateurRarete;
        this.defense = 60 * multiplicateurRarete;
        this.vitesse = 180 * multiplicateurRarete;
        this.taux_critiques = 0.15;
        this.degat_critiques = 1.30;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.08;
        this.taux_blocage = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"C1", "C4", "C0"};
    }

    // Attaque de base — 100% ATK + Brûlure 1 tour si l'attaque touche
    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " lance des C1 sur " + cible.getNom() + " !");
        boolean touche = Combat.attaqueTouche(this, cible);
        if (!touche) {
            log.add(cible.getNom() + " esquive !");
            this.ajouterRage(50);
            return;
        }
        // Synergie : bonus degats si la cible est empoisonnee par Sasori
        if (cible.aEffet(Poison.class)) {
            double degats = this.getAttaque() * 1.20;
            log.add("Synergie Akatsuki : cible empoisonnee ! Deidara frappe pour +20% degats !");
            Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        } else {
            Combat.attaquer(this, cible, log);
        }
        Combat.appliquerEffet(this, cible, new Brulure(1, 0.05), log);
    }

    // Spéciale — 130% ATK AoE + Brûlure 2 tours + ReductionDefense 10% 2 tours
    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Deidara lance C4 !");

        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.30;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new Brulure(2, 0.10), log);
                Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.10, 2), log);
            }
        }
        log.add("Tous les ennemis brulent et voient leur defense reduite de 10% pendant 2 tours !");
        // Synergie : si Sasori est allie, les ennemis empoisonnes recoivent +15% degats
        boolean sasorPresent = equipeAlliee.stream()
                .anyMatch(a -> a instanceof perso_Sasori && a.estVivant());
        if (sasorPresent) {
            log.add("Synergie Akatsuki : les explosions de Deidara ignorent la resistance des cibles empoisonnees !");
        }
    }

    // Ultime — BuffAttaque 20% + 150% ATK AoE + Brûlure extrême 3 tours
    //        + ReductionDefense 15% sur soi (prix du C0)
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Deidara declenche C0 ! L'art c'est une explosion !");

        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }

        // Buff attaque appliqué avant les frappes
        Combat.appliquerEffet(this, new BuffAttaque(0.20, 2), log);

        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.50) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new Brulure(3, 0.15), log);
            }
        }

        // Malus post-explosion sur Deidara (prix du C0)
        Combat.appliquerEffet(this, new ReductionDefense(0.15, 2), log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("C1 — inflige 100% ATK a une cible (120% si la cible est empoisonnee). Applique Brulure legere pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("C4 — inflige 130% ATK a tous les ennemis, "
                + "applique Brulure (10% PV/tour) pendant 2 tours "
                + "et reduit leur defense de 10% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("C0 — gagne 20% d'attaque, inflige 150% ATK a tous les ennemis, "
                + "applique Brulure extreme (15% PV/tour) pendant 3 tours. "
                + "En contrepartie, Deidara perd 15% de defense pendant 2 tours.");
    }
}