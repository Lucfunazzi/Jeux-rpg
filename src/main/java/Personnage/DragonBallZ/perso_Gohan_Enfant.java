package Personnage.DragonBallZ;

import Combat.Combat;
import Effets.BuffAttaque;
import Effets.BuffDegatCritique;
import Effets.Etourdissement;
import Personnage.PersonnageBase;
import java.util.List;

public class perso_Gohan_Enfant extends PersonnageBase {

    public perso_Gohan_Enfant() {
        this.nom = "Gohan (enfant)";
        this.type = "Guerrier";
        this.role = "DPS";
        this.rarete = "B";
        this.niveau = 1;
        double multiplicateurRarete = 1.20;
        this.vie = 480 * multiplicateurRarete;
        this.attaque = 155 * multiplicateurRarete;
        this.defense = 90 * multiplicateurRarete;
        this.vitesse = 110 * multiplicateurRarete;
        this.taux_critiques = 0.15;
        this.degat_critiques = 1.50;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.10;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Masenko", "Rage Saiyan", "Eruption de puissance"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gohan utilise Masenko !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        this.ajouterRage(15);
        log.add("Gohan gagne 15 points de rage !");
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gohan entre en Rage Saiyan !");
        // Bonus de degats selon le nombre d'allies KO (rage de Gohan)
        long alliesKO = equipeAlliee.stream().filter(a -> !a.estVivant()).count();
        double bonusRage = 1.0 + (alliesKO * 0.20);
        if (alliesKO > 0) {
            log.add("Gohan est enrage ! +" + (int)(alliesKO * 20) + "% degats pour " + alliesKO + " allie(s) KO !");
        }
        double degats = (this.getAttaque() * 1.40) * bonusRage;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Buff critique sur lui-meme
        Combat.appliquerEffet(this, this, new BuffDegatCritique(0.30, 2), log);
        this.ajouterRage(20);
        log.add("Gohan gagne 20 points de rage !");
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gohan utilise Eruption de puissance !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        // Bonus supplementaire par allie KO
        long alliesKO = equipeAlliee.stream().filter(a -> !a.estVivant()).count();
        double bonusKO = 1.0 + (alliesKO * 0.25);
        if (alliesKO > 0) {
            log.add("La douleur decuple sa puissance ! +" + (int)(alliesKO * 25) + "% degats !");
        }
        PersonnageBase cible = equipeEnnemie.stream()
                .filter(PersonnageBase::estVivant)
                .max(java.util.Comparator.comparingDouble(PersonnageBase::getVie))
                .orElse(null);
        if (cible == null) return;
        double degats = (this.getAttaque() * 2.00) * multiplicateurRage * bonusKO;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (Math.random() < 0.35) {
            Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
        }
        // Buff ATK apres l'explosion
        Combat.appliquerEffet(this, this, new BuffAttaque(0.25, 2), log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Masenko — Inflige 110% ATK a la cible. "
                + "Gohan gagne 15 points de rage.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Rage Saiyan — Inflige 140% ATK a la cible. "
                + "+20% degats par allie KO. "
                + "Gohan gagne +30% degats critiques pendant 2 tours et 20 rage.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Eruption de puissance — Inflige 200% ATK a l'ennemi avec le plus de PV. "
                + "+25% degats par allie KO. "
                + "35% de chance d'Etourdissement. "
                + "Gohan gagne +25% ATK pendant 2 tours. "
                + "Puissance augmentee par la Rage.");
    }
}