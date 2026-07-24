package Equipement;

/** Friandise pour la Creature Sacree — consommable donnant de l'XP instantanee (mirroring les tiers d'entrainement). */
public enum FriandiseFamilier {

    PETITE ("Petite Friandise",  40),
    MOYENNE("Friandise",         65),
    GRANDE ("Grande Friandise", 140);

    public final String nom;
    public final int    xp;

    FriandiseFamilier(String nom, int xp) {
        this.nom = nom;
        this.xp  = xp;
    }

    @Override
    public String toString() {
        return nom + " (+" + xp + " XP)";
    }
}
