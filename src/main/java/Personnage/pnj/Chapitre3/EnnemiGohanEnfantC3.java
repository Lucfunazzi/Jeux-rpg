package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiGohanEnfantC3 extends PersonnageBase {

    public EnnemiGohanEnfantC3() { this(30); }

    public EnnemiGohanEnfantC3(int niveau) {
        this.nom    = "Gohan Enfant";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "DPS";
        this.rarete = "B";

        double mult = 1.3;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 200.0 * mult * niv;
        this.attaque = 155.0 * mult * niv;
        this.defense = 90.0 * mult * niv;
        this.vitesse = 110.0 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.3;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.1;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Masenko", "Rage Saiyan", "Eruption de puissance"};
    }

    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gohan utilise Masenko sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        this.ajouterRage(15);
        log.add("Gohan gagne 15 points de rage !");
    }

    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gohan entre en Rage Saiyan !");
        long alliesKO = equipeAlliee.stream().filter(a -> !a.estVivant()).count();
        double bonusRage = 1.0 + (alliesKO * 0.20);
        if (alliesKO > 0) {
            log.add("Gohan est enrage ! +" + (int)(alliesKO * 20) + "% degats pour " + alliesKO + " allie(s) KO !");
        }
        double degats = (this.getAttaque() * 1.40) * bonusRage;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, this, new BuffDegatCritique(0.30, 2), log);
        this.ajouterRage(20);
        log.add("Gohan gagne 20 points de rage !");
    }

    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gohan utilise Eruption de puissance !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        long alliesKO = equipeAlliee.stream().filter(a -> !a.estVivant()).count();
        double bonusKO = 1.0 + (alliesKO * 0.25);
        if (alliesKO > 0) {
            log.add("La douleur decuple sa puissance ! +" + (int)(alliesKO * 25) + "% degats !");
        }
        PersonnageBase cible = equipeEnnemie.stream()
                .filter(PersonnageBase::estVivant)
                .max(java.util.Comparator.comparingDouble(PersonnageBase::getVie))
                .orElse(null);
        if (cible != null) {
            double degats = (this.getAttaque() * 1.80) * multiplicateurRage * bonusKO;
            Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        }
    }

    @Override public void descriptionAttaqueBase()     { System.out.println("Masenko : inflige 110% ATK a une cible et gagne 15 rage."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Rage Saiyan : inflige 140% ATK a une cible (+20% par allie KO), gagne un buff critique de 30% pendant 2 tours et 20 rage."); }
    @Override public void descriptionAttaqueUltime()   { System.out.println("Eruption de puissance : inflige 180% ATK a l'ennemi avec le plus de PV, amplifie par la rage et le nombre d'allies KO (+25% chacun)."); }
}
