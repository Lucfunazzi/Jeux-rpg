package Personnage.Naruto_Shippuden;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Gaara extends PersonnageBase {

    public perso_Gaara() {
        this.nom = "Gaara";
        this.niveau = 1;
        this.type = "Ninja";
        this.role = "Tank";
        this.rarete = "S";

        double multiplicateurRarete = 1.50;
        this.vie = 500 * multiplicateurRarete;
        this.attaque = 95 * multiplicateurRarete;
        this.defense = 120 * multiplicateurRarete;
        this.vitesse = 90 * multiplicateurRarete;

        this.taux_critiques = 0.05;
        this.degat_critiques = 1.50;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.05;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Bouclier de Sable Impénétrable", "Armure de Sable protectrice", "Tombeau du Désert Éternel"};
    }

    // Attaque de base — Inflige 100% ATK et augmente son propre Blocage
    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " projette une vague de sable lourd sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.00;

        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, new BuffBlocage(0.20, 2), log);
    }

    // Spéciale — Donne un Bouclier à TOUTE l'équipe (30% des PV Max de Gaara)
    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " dresse un rempart de sable gigantesque devant ses alliés !");

        double montantBouclier = this.getVieMax() * 0.30;

        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new Bouclier(montantBouclier), log);
            }
        }
        log.add("Toute l'équipe reçoit un Bouclier protecteur de "
                + String.format("%.1f", montantBouclier) + " PV pour 3 tours !");
    }

    // Ultime — Attaque de Zone. Dégâts augmentés si ennemi empoisonné ou affaibli
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("\n[ULTIME] " + this.getNom() + " déclenche les Funérailles Impériales du Désert !");

        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }

        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double ratioDegats = 1.10;

                boolean aPoison = ennemi.getEffet(Poison.class) != null;
                boolean aReductionDef = ennemi.getEffet(ReductionDefense.class) != null;

                if (aPoison || aReductionDef) {
                    ratioDegats += 0.40;
                    log.add("Le sable s'engouffre dans les failles de " + ennemi.getNom() + " ! Dégâts augmentés !");
                }

                double degats = (this.getAttaque() * ratioDegats) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new ReductionVitesse(0.20, 2), log);
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Bouclier de Sable Impénétrable — Inflige 100% ATK à un ennemi et augmente le Taux de Blocage de Gaara de 20% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Armure de Sable protectrice — Protège toute l'équipe en octroyant un Bouclier égal à 30% des PV max de Gaara pendant 3 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Tombeau du Désert Éternel — Attaque de zone à 110% ATK. Les dégâts passent à 150% sur les cibles souffrant de Poison ou de Réduction de Défense. Réduit la Vitesse des cibles de 20% pendant 2 tours.");
    }
}