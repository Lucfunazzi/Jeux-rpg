package lancement.Gestionnaires;

import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gere les liens entre personnages.
 * Un lien s'active si tous ses membres sont presents dans la formation.
 * Le bonus s'applique a toute l'equipe en formation.
 */
public class GestionnaireLiens {

    // ── Definition d'un lien ──────────────────────────────────────────────
    public static class Lien {
        public final String   nom;
        public final String   description;
        public final String[] membres;
        public final double   bonusATK;
        public final double   bonusDEF;
        public final double   bonusPV;
        public final double   bonusVIT;

        public Lien(String nom, String description, String[] membres,
                    double bonusATK, double bonusDEF, double bonusPV, double bonusVIT) {
            this.nom         = nom;
            this.description = description;
            this.membres     = membres;
            this.bonusATK    = bonusATK;
            this.bonusDEF    = bonusDEF;
            this.bonusPV     = bonusPV;
            this.bonusVIT    = bonusVIT;
        }
    }

    // ── Objet résultat pour éviter de recalculer les liens 4 fois ─────────
    public static class BonusLiens {
        public final double atk;
        public final double def;
        public final double pv;
        public final double vit;

        private BonusLiens(double atk, double def, double pv, double vit) {
            this.atk = atk;
            this.def = def;
            this.pv  = pv;
            this.vit = vit;
        }

        public static final BonusLiens VIDE = new BonusLiens(0, 0, 0, 0);
    }

    // ── Catalogue de tous les liens (non modifiable) ───────────────────────
    private static final List<Lien> TOUS_LES_LIENS;

    static {
        List<Lien> liens = new ArrayList<>();

        liens.add(new Lien(
            "Androïds jumeaux",
            "Rogue et Sting ensemble : toute l'equipe gagne +8% ATK et +10% VIT",
            new String[]{"Rogue", "Sting"},
            0.05, 0.00, 0.00, 0.15
        ));
        liens.add(new Lien(
            "Freres aux destins tragiques",
            "Natsu et Gray ensemble : toute l'equipe gagne +10% ATK et +5% DEF",
            new String[]{"Natsu", "Gray"},
            0.10, 0.05, 0.00, 0.00
        ));
        liens.add(new Lien(
            "Les reines des fees",
            "Erza et Mirajane ensemble : toute l'equipe gagne +10% PV, +2% DEF et +2% VIT",
            new String[]{"Erza", "Mirajane"},
            0.00, 0.02, 0.10, 0.02
        ));
        liens.add(new Lien(
            "Protagonistes Fairy Tail",
            "Natsu et Lucy ensemble : toute l'equipe gagne +3% ATK, +2% DEF et +2% PV",
            new String[]{"Natsu", "Lucy"},
            0.03, 0.02, 0.02, 0.00
        ));
        liens.add(new Lien(
            "Guerrier team Z 1",
            "Alzack et Bisca ensemble : toute l'equipe gagne +5% ATK et +8% VIT",
            new String[]{"Alzack", "Bisca"},
            0.02, 0.00, 0.05, 0.00
        ));

        liens.add(new Lien(
            "Chasseurs de dragon",
            "Natsu, Wendy et Gajeel ensemble : toute l'equipe gagne +8% ATK, +5% PV et +3% VIT",
            new String[]{"Natsu", "Wendy", "Gajeel"},
            0.08, 0.00, 0.05, 0.03
        ));

        liens.add(new Lien(
            "Duo Akatsuki",
            "Sasori et Deidara ensemble : toute l'equipe gagne +8% VIT et +5% ATK",
            new String[]{"Sasori", "Deidara"},
            0.05, 0.00, 0.00, 0.08
        ));

        // Ajouter de nouveaux liens ici :
        // liens.add(new Lien("Nom", "Description", new String[]{"Perso1","Perso2"}, atk, def, pv, vit));

        liens.add(new Lien(
            "Constellationnistes",
            "Lucy, Yukino et Angel ensemble : toute l'equipe gagne +5% a toutes les stats",
            new String[]{"Lucy", "Yukino", "Angel"},
            0.05, 0.05, 0.05, 0.05
        ));

        TOUS_LES_LIENS = Collections.unmodifiableList(liens);
    }

    // ── Liens actifs ──────────────────────────────────────────────────────
    public List<Lien> getLiensActifs(List<PersonnageBase> equipe) {
        List<Lien> actifs = new ArrayList<>();
        for (Lien lien : TOUS_LES_LIENS)
            if (estActif(lien, equipe)) actifs.add(lien);
        return actifs;
    }

    private boolean estActif(Lien lien, List<PersonnageBase> equipe) {
        for (String membre : lien.membres) {
            if (equipe.stream().noneMatch(p -> p.getNom().equals(membre)))
                return false;
        }
        return true;
    }

    // ── Bonus agrégés en une seule passe ─────────────────────────────────
    public BonusLiens getBonusTous(List<PersonnageBase> equipe) {
        List<Lien> actifs = getLiensActifs(equipe);
        if (actifs.isEmpty()) return BonusLiens.VIDE;

        double atk = 0, def = 0, pv = 0, vit = 0;
        for (Lien l : actifs) {
            atk += l.bonusATK;
            def += l.bonusDEF;
            pv  += l.bonusPV;
            vit += l.bonusVIT;
        }
        return new BonusLiens(atk, def, pv, vit);
    }

    // ── Getters individuels (conservés pour compatibilité) ─────────────────
    public double getBonusATK(List<PersonnageBase> equipe) { return getBonusTous(equipe).atk; }
    public double getBonusDEF(List<PersonnageBase> equipe) { return getBonusTous(equipe).def; }
    public double getBonusPV (List<PersonnageBase> equipe) { return getBonusTous(equipe).pv;  }
    public double getBonusVIT(List<PersonnageBase> equipe) { return getBonusTous(equipe).vit; }

    public static List<Lien> getTousLesLiens() { return TOUS_LES_LIENS; }
}