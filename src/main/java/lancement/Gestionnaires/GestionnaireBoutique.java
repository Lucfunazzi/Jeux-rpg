package lancement.Gestionnaires;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Suivi des achats limites a 1 fois par jour dans l'onglet Consommables de la Boutique.
 * Reset a minuit, meme principe que GestionnaireEnergie/GestionnaireDonjon.
 */
public class GestionnaireBoutique {

    private Set<String> achetesAujourdhui = new HashSet<>();
    private LocalDate   dernierReset      = LocalDate.now();

    private void resetSiNecessaire() {
        LocalDate aujourdhui = LocalDate.now();
        if (!aujourdhui.equals(dernierReset)) {
            achetesAujourdhui.clear();
            dernierReset = aujourdhui;
        }
    }

    /** Vrai si {@code idItem} a deja ete achete aujourd'hui (limite "1 fois par jour"). */
    public boolean dejaAchete(String idItem) {
        resetSiNecessaire();
        return achetesAujourdhui.contains(idItem);
    }

    public void marquerAchete(String idItem) {
        resetSiNecessaire();
        achetesAujourdhui.add(idItem);
    }

    // ── Sauvegarde ───────────────────────────────────────────────────────
    public Set<String> getAchetesAujourdhui()              { return achetesAujourdhui; }
    public void        setAchetesAujourdhui(Set<String> s) { this.achetesAujourdhui = s; }
    public LocalDate   getDernierReset()                   { return dernierReset; }
    public void        setDernierReset(LocalDate d)        { this.dernierReset = d; }
}
