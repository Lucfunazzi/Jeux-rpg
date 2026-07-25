package Effets;

import Personnage.PersonnageBase;
import java.util.List;

/**
 * Immunite de controle — protege la cible contre Etourdissement, Paralysie, Sommeil
 * et Petrification pendant N tours (n'affecte pas les autres effets negatifs comme
 * les DoT ou les debuffs de stats — contrairement a Immunite qui bloque tout).
 */
public class ImmuniteControle implements Effet {

    private int toursRestants;

    public ImmuniteControle(int tours) {
        this.toursRestants = tours;
    }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add("🛡 " + cible.getNom() + " est immunise aux effets de controle pendant "
                + toursRestants + " tour(s) !");
    }

    @Override public void appliquer(PersonnageBase cible) { /* gere par appliquer(cible, log) */ }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        if (toursRestants > 0) {
            toursRestants--;
            if (toursRestants > 0) {
                log.add("🛡 " + cible.getNom() + " : Immunite de controle active (" + toursRestants + " tour(s) restant(s)).");
            } else {
                log.add("🛡 " + cible.getNom() + " : Immunite de controle expiree.");
            }
        }
    }

    @Override public void tick(PersonnageBase cible) { if (toursRestants > 0) toursRestants--; }

    @Override public boolean estTermine() { return toursRestants <= 0; }
    @Override public String getNom() { return "ImmuniteControle"; }

    public int getToursRestants() { return toursRestants; }
}
