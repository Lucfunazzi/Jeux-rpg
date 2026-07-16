package lancement.Quetes;

public class QueteJournaliere extends Quete {

    public enum TypeObjectif {
        FORTIFIER,
        GAGNER_OR,
        TERMINER_STAGE
    }

    private final TypeObjectif typeObjectif;
    private final int          objectifCible;
    private int                progression = 0;

    public QueteJournaliere(String id, String titre, String description,
                            TypeObjectif typeObjectif, int objectifCible,
                            int recompenseXP, int recompenseOr) {
        super(id, titre, description, recompenseXP, recompenseOr, 0);
        this.typeObjectif  = typeObjectif;
        this.objectifCible = objectifCible;
    }

    public TypeObjectif getTypeObjectif() { return typeObjectif; }
    public int getObjectifCible()         { return objectifCible; }
   public int getProgressionValeur()  { return progression; }

    public void ajouterProgression(int montant) {
        if (reclamee) return;
        progression = Math.min(progression + montant, objectifCible);
        if (progression >= objectifCible) completee = true;
    }

    public void reinitialiser() {
        progression = 0;
        completee   = false;
        reclamee    = false;
    }

   @Override
public String getProgression() {
    return progression + " / " + objectifCible;
}
}