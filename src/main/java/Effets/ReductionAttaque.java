package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class ReductionAttaque implements Effet {
    private int toursRestants;
    private double pourcentage;

    /**
     * @param pourcentage Pourcentage d'attaque de base retiré (ex: 0.20 pour -20%).
     * @param tours       Nombre de tours.
     */
    public ReductionAttaque(double pourcentage, int tours) {
        this.pourcentage = pourcentage;
        this.toursRestants = tours;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " perd " + (int)(pourcentage * 100)
                + "% d'attaque pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        toursRestants--;
        if (toursRestants <= 0) {
            System.out.println("La réduction d'attaque de " + cible.getNom() + " se termine !");
        }
    }

    @Override
    public boolean estTermine() { return toursRestants <= 0; }

    @Override
    public String getNom() { return "ReductionAttaque"; }

    public double getPourcentage() { return pourcentage; }
    public int getToursRestants() { return toursRestants; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " perd " + (int)(pourcentage * 100)
                + "% d'attaque pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestants--;
        if (toursRestants <= 0) {
            log.add("La reduction d'attaque de " + cible.getNom() + " se termine !");
        }
    }

}
