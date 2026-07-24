package Equipement;

/** Potions consommables qui restaurent instantanement de l'energie. */
public enum PotionEnergie {
    PETITE ("Petite Potion d'Energie", 5),
    MOYENNE("Potion d'Energie",        20),
    GRANDE ("Grande Potion d'Energie", 50);

    public final String nom;
    public final int    energie;

    PotionEnergie(String nom, int energie) {
        this.nom     = nom;
        this.energie = energie;
    }

    @Override
    public String toString() {
        return nom + " (+" + energie + " energie)";
    }
}
