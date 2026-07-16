package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class BuffDegatCritique implements Effet {
    private int toursRestants;
    private double bonus;

    public BuffDegatCritique(double bonus, int toursRestants) {
        this.bonus = bonus;
        this.toursRestants = toursRestants;
    }

    @Override
public void appliquer(PersonnageBase cible) {
    System.out.println(cible.getNom() + " gagne " + (int)(bonus * 100)
            + "% de dégâts critiques pendant " + toursRestants + " tour(s) !");
}

    @Override
    public void tick(PersonnageBase cible) {
    toursRestants--;
    if (toursRestants <= 0)
        System.out.println("Le buff de dégâts critiques de " + cible.getNom() + " se termine !");
}
    @Override
    public boolean estTermine() { return toursRestants <= 0; }

    @Override
    public String getNom() { return "BuffDegatCritique"; }

    public double getBonus() { return bonus; }
    public int getToursRestants() { return toursRestants; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " gagne " + (int)(bonus * 100)
                + "% de degats critiques pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestants--;
        if (toursRestants <= 0)
            log.add("Le buff de degats critiques de " + cible.getNom() + " se termine !");
    }

}
