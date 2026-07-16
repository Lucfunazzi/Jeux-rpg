package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class BuffBlocage implements Effet {
    private int toursRestants;
    private double pourcentage;

    /**
     * @param pourcentage Bonus de taux de blocage ajoute (ex: 0.15 pour +15%).
     * @param tours       Nombre de tours.
     */
    public BuffBlocage(double pourcentage, int tours) {
        this.pourcentage = pourcentage;
        this.toursRestants = tours;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " gagne " + (int)(pourcentage * 100)
                + "% de taux de blocage pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        toursRestants--;
        if (toursRestants <= 0)
            System.out.println("Le buff de blocage de " + cible.getNom() + " se termine !");
    }

    @Override
    public boolean estTermine() { return toursRestants <= 0; }

    @Override
    public String getNom() { return "BuffBlocage"; }

    public double getPourcentage() { return pourcentage; }
    public int getToursRestants() { return toursRestants; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " gagne " + (int)(pourcentage * 100)
                + "% de taux de blocage pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestants--;
        if (toursRestants <= 0)
            log.add("Le buff de blocage de " + cible.getNom() + " se termine !");
    }

}
