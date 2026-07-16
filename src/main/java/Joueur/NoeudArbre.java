package Joueur;

public class NoeudArbre {

    public enum TypeBonus { ATK, DEF, PV, VIT, COMPETENCE_SPECIALE }

    private final int    index;
    private final String description;
    private final int    coutPoints;
    private final TypeBonus typeBonus;
    private final double valeurBonus; // ex: 0.02 = +2%
    private boolean debloque = false;

    public NoeudArbre(int index, String description, int coutPoints,
                      TypeBonus typeBonus, double valeurBonus) {
        this.index       = index;
        this.description = description;
        this.coutPoints  = coutPoints;
        this.typeBonus   = typeBonus;
        this.valeurBonus = valeurBonus;
    }

    public int       getIndex()       { return index; }
    public String    getDescription() { return description; }
    public int       getCoutPoints()  { return coutPoints; }
    public TypeBonus getTypeBonus()   { return typeBonus; }
    public double    getValeurBonus() { return valeurBonus; }
    public boolean   isDebloque()     { return debloque; }
    public void      debloquer()      { this.debloque = true; }
}