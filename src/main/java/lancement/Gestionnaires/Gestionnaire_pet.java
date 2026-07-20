package lancement.Gestionnaires;

import Personnage.PersonnageBase;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Gère la Créature Sacrée du joueur (univers Fairy Tail).
 *
 * Progression :
 *   - Débloqué au niveau 30 du joueur.
 *   - L'œuf est obtenu en terminant le Chapitre 2 Élite.
 *   - Niveaux 1 → 20 via entraînement (8h / 12h / 24h) qui donnent de l'XP.
 *   - XP requise : 200 au niv 1, ×1.10 par niveau suivant.
 *   - Au niveau 20 : évolution → remplace par la créature suivante, repasse niv 1.
 *   - Bonus FLAT sur ATK / PV / DEF / VIT de toute la formation.
 *
 * Chaîne : Œuf → Bébé Happy → Happy → Ichiya (Chat d'Edolas)
 *        → Virgo → Cancer → Taurus → Leo
 *        → Bébé Dragon de Feu → Atlas Flame → Igneel
 */
public class Gestionnaire_pet {

    public static final int NIVEAU_DEBLOCAGE = 30;
    public static final int NIVEAU_MAX       = 20;

    // ── Entraînement ─────────────────────────────────────────────────────
    public enum Entrainement {
        COURT ("8h",   8,  40),
        MOYEN ("12h", 12,  65),
        LONG  ("24h", 24, 140);

        public final String libelle;
        public final int    dureeHeures;
        public final int    xpRecompense;

        Entrainement(String libelle, int dureeHeures, int xpRecompense) {
            this.libelle      = libelle;
            this.dureeHeures  = dureeHeures;
            this.xpRecompense = xpRecompense;
        }
    }

    // ── État ──────────────────────────────────────────────────────────────
    private pet_Types type;
    private int                niveau;
    private int                experience;
    private int                experienceMax;

    private boolean            oeufDebloque      = false;
    private Entrainement       entrainementActif = null;
    private LocalDateTime      debutEntrainement = null;

    public Gestionnaire_pet() {
        this.type          = pet_Types.OEUF;
        this.niveau        = 1;
        this.experience    = 0;
        this.experienceMax = xpRequise(1);
    }

    // ── XP requise par niveau ─────────────────────────────────────────────
    private int xpRequise(int niv) {
        return (int)(200 * Math.pow(1.10, niv - 1));
    }

    // ── Déblocage de l'œuf ───────────────────────────────────────────────
    public void debloquerOeuf() {
        this.oeufDebloque = true;
    }

    public boolean isOeufDebloque() { return oeufDebloque; }

    // ── Entraînement ─────────────────────────────────────────────────────

    /** Lance un entraînement. Retourne false si un autre est déjà en cours. */
    public boolean lancerEntrainement(Entrainement e) {
        if (!oeufDebloque)                           return false;
        if (entrainementActif != null && !entrainementTermine()) return false;
        if (estAuNiveauMax())                        return false;
        entrainementActif = e;
        debutEntrainement = LocalDateTime.now();
        return true;
    }

    /** True si l'entraînement est terminé (temps écoulé). */
    public boolean entrainementTermine() {
        if (entrainementActif == null || debutEntrainement == null) return false;
        long heuresEcoulees = Duration.between(debutEntrainement, LocalDateTime.now()).toHours();
        return heuresEcoulees >= entrainementActif.dureeHeures;
    }

    /** True si un entraînement est en cours (temps pas encore écoulé). */
    public boolean entrainementEnCours() {
        return entrainementActif != null && !entrainementTermine();
    }

    /**
     * Réclame l'XP de l'entraînement terminé.
     * @return message résultat, ou null si rien à réclamer.
     */
    public String reclamerEntrainement() {
        if (entrainementActif == null) return null;
        if (!entrainementTermine())    return "L'entraînement n'est pas encore terminé !";

        int          xp = entrainementActif.xpRecompense;
        Entrainement e  = entrainementActif;
        entrainementActif = null;
        debutEntrainement = null;

        return ajouterXP(xp, e.libelle);
    }

    /** Ajoute de l'XP et monte de niveau si nécessaire. Retourne le message log. */
    private String ajouterXP(int xp, String source) {
        StringBuilder sb = new StringBuilder();
        sb.append("Entraînement (").append(source).append(") : +").append(xp).append(" XP !\n");
        experience += xp;

        while (experience >= experienceMax && !estAuNiveauMax()) {
            experience    -= experienceMax;
            niveau++;
            experienceMax  = xpRequise(niveau);
            sb.append(type.nom).append(" passe au niveau ").append(niveau).append(" !\n");
            sb.append("  ATK +").append((int) type.getATK(niveau))
              .append(" | PV +").append((int) type.getPV(niveau))
              .append(" | DEF +").append((int) type.getDEF(niveau))
              .append(" | VIT +").append((int) type.getVIT(niveau)).append("\n");
        }

        if (estAuNiveauMax()) {
            experience = experienceMax;
            sb.append(type.nom).append(" est au niveau maximum ! Faites-le évoluer !\n");
        }

        return sb.toString().trim();
    }

    // ── Évolution ─────────────────────────────────────────────────────────
    public String evoluer() {
        if (!estAuNiveauMax())   return type.nom + " doit être niveau 20 pour évoluer.";
        if (!type.peutEvoluer()) return type.nom + " est déjà la forme finale — Igneel veille sur votre équipe !";

        pet_Types ancien = type;
        type          = type.suivant();
        niveau        = 1;
        experience    = 0;
        experienceMax = xpRequise(1);

        return "✨ " + ancien.nom + " a évolué en " + type.nom + " !\n"
             + "Niveau remis à 1. Bonus équipe : ATK +" + (int) type.getATK(1)
             + " | PV +" + (int) type.getPV(1)
             + " | DEF +" + (int) type.getDEF(1)
             + " | VIT +" + (int) type.getVIT(1);
    }

    // ── Bonus sur la formation ────────────────────────────────────────────
    public void appliquerBonus(List<PersonnageBase> equipe) {
        if (!oeufDebloque) return;
        double atk = type.getATK(niveau);
        double pv  = type.getPV(niveau);
        double def = type.getDEF(niveau);
        double vit = type.getVIT(niveau);
        for (PersonnageBase p : equipe) {
            p.setBonusCreatureATK(atk);
            p.setBonusCreaturePV(pv);
            p.setBonusCreatureDEF(def);
            p.setBonusCreatureVIT(vit);
        }
    }

    public void retirerBonus(PersonnageBase p) {
        p.setBonusCreatureATK(0);
        p.setBonusCreaturePV(0);
        p.setBonusCreatureDEF(0);
        p.setBonusCreatureVIT(0);
    }

    // ── Affichage console ─────────────────────────────────────────────────
    public void afficher(int niveauJoueur) {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║         CRÉATURES SACRÉES                ║");
        System.out.println("╠══════════════════════════════════════════╣");

        if (niveauJoueur < NIVEAU_DEBLOCAGE) {
            System.out.println("║  Débloqué au niveau " + NIVEAU_DEBLOCAGE + " du joueur.      ║");
            System.out.println("╚══════════════════════════════════════════╝");
            return;
        }

        if (!oeufDebloque) {
            System.out.println("║  Aucun œuf obtenu pour l'instant.        ║");
            System.out.println("║  Terminez le Chapitre 2 Élite pour       ║");
            System.out.println("║  recevoir un Œuf Mystérieux !            ║");
            System.out.println("║  [0] Retour                              ║");
            System.out.println("╚══════════════════════════════════════════╝");
            return;
        }

        System.out.println("║  Créature : " + type.nom);
        System.out.println("║  Niveau   : " + niveau + " / " + NIVEAU_MAX);
        System.out.printf ("║  XP       : %d / %d%n", experience, experienceMax);
        System.out.printf ("║  Bonus équipe : ATK +%.0f | PV +%.0f | DEF +%.0f | VIT +%.0f%n",
                type.getATK(niveau), type.getPV(niveau), type.getDEF(niveau), type.getVIT(niveau));

        // Afficher la prochaine évolution si disponible
        if (type.peutEvoluer()) {
            System.out.println("║  Prochaine évolution : " + type.suivant().nom);
        } else {
            System.out.println("║  ★ Forme finale : Igneel veille sur vous !");
        }

        System.out.println("╠══════════════════════════════════════════╣");

        if (entrainementEnCours()) {
            long heuresEcoulees   = Duration.between(debutEntrainement, LocalDateTime.now()).toHours();
            long minutesEcoulees  = Duration.between(debutEntrainement, LocalDateTime.now()).toMinutes() % 60;
            long heuresRestantes  = entrainementActif.dureeHeures - heuresEcoulees;
            long minutesRestantes = 60 - minutesEcoulees;
            if (minutesRestantes == 60) { heuresRestantes++; minutesRestantes = 0; }
            System.out.printf("║  Entraînement (%s) en cours...%n", entrainementActif.libelle);
            System.out.printf("║  Temps restant : ~%dh%02dm%n", heuresRestantes, minutesRestantes);
            System.out.println("║  [0] Retour");

        } else if (entrainementTermine()) {
            System.out.println("║  ✅ Entraînement terminé ! Réclamez votre XP.");
            System.out.println("║  [1] Réclamer l'XP");
            System.out.println("║  [0] Retour");

        } else if (estAuNiveauMax() && type.peutEvoluer()) {
            System.out.println("║  Niveau max atteint !");
            System.out.println("║  [2] Évoluer → " + type.suivant().nom);
            System.out.println("║  [0] Retour");

        } else if (estAuNiveauMax()) {
            System.out.println("║  ★ Igneel est à son niveau maximum !");
            System.out.println("║  [0] Retour");

        } else {
            System.out.println("║  Choisissez un entraînement :");
            for (Entrainement e : Entrainement.values()) {
                System.out.printf("║  [%d] Entraînement %s → +%d XP%n",
                        e.ordinal() + 1, e.libelle, e.xpRecompense);
            }
            System.out.println("║  [0] Retour");
        }

        System.out.println("╚══════════════════════════════════════════╝");
    }

    // ── Utilitaires ───────────────────────────────────────────────────────
    public boolean estAuNiveauMax()             { return niveau >= NIVEAU_MAX; }
    public pet_Types getType()         { return type; }
    public int getNiveau()                      { return niveau; }
    public int getExperience()                  { return experience; }
    public int getExperienceMax()               { return experienceMax; }
    public Entrainement getEntrainementActif()  { return entrainementActif; }
    public LocalDateTime getDebutEntrainement() { return debutEntrainement; }

    // ── Restauration depuis sauvegarde ────────────────────────────────────
    public void restaurer(String typeNom, int niv, int xp, boolean oeuf,
                          String entrainementNom, String debutStr) {
        try {
            this.type = pet_Types.valueOf(typeNom);
        } catch (IllegalArgumentException e) {
            // Ancienne valeur DBZ (SHENRON, OOZARU, etc.) → repart de l'Œuf
            this.type = pet_Types.OEUF;
        }
        this.niveau        = Math.max(1, Math.min(NIVEAU_MAX, niv));
        this.experienceMax = xpRequise(this.niveau);
        this.experience    = Math.min(xp, this.experienceMax);
        this.oeufDebloque  = oeuf;

        if (entrainementNom != null && debutStr != null) {
            try {
                this.entrainementActif = Entrainement.valueOf(entrainementNom);
                this.debutEntrainement = LocalDateTime.parse(debutStr);
            } catch (Exception ex) {
                this.entrainementActif = null;
                this.debutEntrainement = null;
            }
        }
    }
}