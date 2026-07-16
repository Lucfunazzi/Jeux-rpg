package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class Regeneration implements Effet {
    private int toursRestants;
    private double pourcentageSoin;

    /**
     * @param pourcentageSoin Pourcentage des PV max restaure par tour (ex: 0.05 pour 5%).
     * @param toursRestants   Nombre de tours.
     */
    public Regeneration(double pourcentageSoin, int toursRestants) {
        this.pourcentageSoin = pourcentageSoin;
        this.toursRestants = toursRestants;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " regenere ses PV pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        if (toursRestants > 0) {
            double soin = cible.getVieMax() * pourcentageSoin;
            toursRestants--;
            // La régénération bypasse la Brûlure (soin naturel du corps)
            cible.restaurerPv(soin);
            System.out.println(cible.getNom() + " recupere " + String.format("%.1f", soin)
                    + " PV grace a la regeneration (" + toursRestants + " tours restants)");
        }
    }

    @Override
    public boolean estTermine() { return toursRestants <= 0; }

    @Override
    public String getNom() { return "Regeneration"; }

    public int getToursRestants() { return toursRestants; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " regenere ses PV pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        if (toursRestants > 0) {
            double soin = cible.getVieMax() * pourcentageSoin;
            toursRestants--;
            cible.restaurerPv(soin);
            log.add(cible.getNom() + " recupere " + String.format("%.1f", soin)
                    + " PV grace a la regeneration (" + toursRestants + " tours restants)");
        }
    }

}
