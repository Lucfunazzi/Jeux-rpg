package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class Poison implements Effet {
    private int toursRestant;
    private double pourcentageDegats;
    private int stacks;

    /**
     * @param toursRestant      Nombre de tours pendant lesquels la cible est empoisonnee.
     * @param pourcentageDegats Pourcentage des PV max perdus par tour par stack (ex: 0.05 pour 5%).
     */
    public Poison(int toursRestant, double pourcentageDegats) {
        this.toursRestant = toursRestant;
        this.pourcentageDegats = pourcentageDegats;
        this.stacks = 1;
    }

    @Override
public void appliquer(PersonnageBase cible) {
    // Vide — affiché par Combat.appliquerEffet()
}

@Override
public void tick(PersonnageBase cible) {
    if (toursRestant > 0) {
        double degats = cible.getVieMax() * pourcentageDegats * stacks;
        toursRestant--;
        cible.retirerVie(degats); // ✅
        System.out.println(cible.getNom() + " perd " + String.format("%.1f", degats)
                + " PV (poison x" + stacks + ", " + toursRestant + " tours restants)");
    }
}

    @Override
    public boolean estTermine() { return toursRestant <= 0; }

    @Override
    public String getNom() { return "Poison"; }

    public void ajouterStack() { this.stacks++; }
    public int getStacks() { return stacks; }
    public int getToursRestant() { return toursRestant; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        // Le message d'application est géré par Combat.appliquerEffet()
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        if (toursRestant > 0) {
            double degats = cible.getVieMax() * pourcentageDegats * stacks;
            toursRestant--;
            cible.retirerVie(degats, log);
            log.add(cible.getNom() + " perd " + String.format("%.1f", degats)
                    + " PV (poison x" + stacks + ", " + toursRestant + " tours restants)");
        }
    }

}
