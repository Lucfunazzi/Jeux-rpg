package Personnage.json;

import java.util.List;

/**
 * Représente un personnage complet désérialisé depuis un fichier JSON.
 */
public class PersonnageData {
    public String nom;
    public String type;       // "Ninja", "Mage", "Guerrier"
    public String role;       // "DPS", "Tank", "Support"
    public String rarete;     // "C", "B", "A", "S", "SS", "UR"
    public String univers;    // "Naruto", "DragonBallZ", "FairyTail"

    public StatsData stats;
    public List<AttaqueData> attaques;

    public static class StatsData {
        public double vie        = 500;
        public double attaque    = 100;
        public double defense    = 100;
        public double vitesse    = 100;
        public double tauxCritique     = 0.05;
        public double degatCritique    = 1.10;
        public double precision        = 100.0;
        public double esquive          = 0.05;
        public double blocage          = 0.05;
        public double reductionBlocage = 0.10;
        public double degatsRenvoi     = 0.80;
    }
}