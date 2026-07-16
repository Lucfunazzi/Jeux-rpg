package Effets;

import Personnage.PersonnageBase;

public class BuffTitre implements Effet {

    private final double bonus; // ex: 0.03 = +3%
    // Pas de durée : actif pour tout le combat, retiré après

    public BuffTitre(double bonus) {
        this.bonus = bonus;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        // Pas de message au moment de l'application individuelle
        // (le message global est dans Combat)
    }

    @Override
    public void tick(PersonnageBase cible) {
        // Ne se decremente pas — dure tout le combat
    }

    @Override
    public boolean estTermine() {
        return false; // Retiré manuellement après le combat dans Stage
    }

    @Override
    public String getNom() { return "BuffTitre"; }

    public double getBonus() { return bonus; }
}