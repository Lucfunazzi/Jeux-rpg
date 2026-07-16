package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class Saignement implements Effet {
    private int toursRestants;
    private double pourcentageDegats;

    /**
     * @param toursRestants     Nombre de tours pendant lesquels la cible saigne.
     * @param pourcentageDegats Pourcentage des PV MAX retiré par tour (ex: 0.02 pour 2%).
     */
    public Saignement(int toursRestants, double pourcentageDegats) {
        this.toursRestants = toursRestants;
        this.pourcentageDegats = pourcentageDegats;
    }

   @Override
public void appliquer(PersonnageBase cible) {
    // Vide — affiché par Combat.appliquerEffet()
}

@Override
public void tick(PersonnageBase cible) {
    if (toursRestants > 0) {
        double degats = cible.getVieMax() * pourcentageDegats;
        toursRestants--;
        cible.retirerVie(degats); // ✅
        System.out.println(cible.getNom() + " perd " + String.format("%.1f", degats)
                + " PV (saignement, " + toursRestants + " tours restants)");
    }
}

    @Override
    public boolean estTermine() { return toursRestants <= 0; }

    @Override
    public String getNom() { return "Saignement"; }

    public int getToursRestants() { return toursRestants; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        // Le message d'application est géré par Combat.appliquerEffet()
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        if (toursRestants > 0) {
            double degats = cible.getVieMax() * pourcentageDegats;
            toursRestants--;
            cible.retirerVie(degats);
            log.add(cible.getNom() + " perd " + String.format("%.1f", degats)
                    + " PV (saignement, " + toursRestants + " tours restants)");
        }
    }

}
