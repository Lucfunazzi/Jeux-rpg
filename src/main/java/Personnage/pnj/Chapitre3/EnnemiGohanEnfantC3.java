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
        this.vie     = 480.0 * mult * niv;
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
        return new String[]{"Masenko", "Rage Saiyan", "Kamehameha de la Colere"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gohan tire un Masenko sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("La rage saiyan de Gohan explose !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 1.80, log);
        Combat.appliquerEffet(this, new BuffAttaque(0.25, 2), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gohan libere un Kamehameha de la Colere !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.40, log);
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Masenko : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Rage Saiyan : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Kamehameha de la Colere : attaque ultime."); }
}
