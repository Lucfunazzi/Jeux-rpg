package Equipement;

/**
 * Carte d'or — consommable convertissant la carte en or.
 * 5 niveaux disponibles, stockées dans l'inventaire (max 999 par niveau).
 */
public enum CarteOr {

    NIVEAU_1("Carte d'Or Lv.1",   1_000),
    NIVEAU_2("Carte d'Or Lv.2",   5_000),
    NIVEAU_3("Carte d'Or Lv.3",  15_000),
    NIVEAU_4("Carte d'Or Lv.4", 100_000),
    NIVEAU_5("Carte d'Or Lv.5", 500_000);

    public static final int STOCK_MAX = 999;

    public final String nom;
    public final int    valeurOr;

    CarteOr(String nom, int valeurOr) {
        this.nom      = nom;
        this.valeurOr = valeurOr;
    }

    /** Retourne le niveau (1–5) de la carte. */
    public int getNiveau() { return this.ordinal() + 1; }
}
