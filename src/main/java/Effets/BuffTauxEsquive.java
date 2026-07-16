package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class BuffTauxEsquive implements Effet {
    private int toursRestants;
    private double bonus;

    /**
     * @param bonus         Bonus de taux d'esquive ajoute (ex: 0.10 pour +10%).
     * @param toursRestants Nombre de tours.
     */
    public BuffTauxEsquive(double bonus, int toursRestants) {
        this.bonus = bonus;
        this.toursRestants = toursRestants;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " gagne " + (int)(bonus * 100)
                + "% d'esquive pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        toursRestants--;
        if (toursRestants <= 0)
            System.out.println("Le buff d'esquive de " + cible.getNom() + " se termine !");
    }

    @Override
    public boolean estTermine() { return toursRestants <= 0; }

    @Override
    public String getNom() { return "BuffTauxEsquive"; }

    public double getBonus() { return bonus; }
    public int getToursRestants() { return toursRestants; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " gagne " + (int)(bonus * 100)
                + "% d'esquive pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestants--;
        if (toursRestants <= 0)
            log.add("Le buff d'esquive de " + cible.getNom() + " se termine !");
    }

}
