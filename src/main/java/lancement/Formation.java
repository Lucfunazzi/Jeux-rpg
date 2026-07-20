package lancement;

import Personnage.PersonnageBase;
import Joueur.Personnage_principale;
import lancement.Gestionnaires.GestionnaireLiens;
import lancement.Gestionnaires.GestionnaireCompagnons;
import lancement.Gestionnaires.Gestionnaire_pet;
import java.util.ArrayList;
import java.util.List;

public class Formation {
    private Personnage_principale joueur;
    private PersonnageBase tank = null;
    private ArrayList<PersonnageBase> attaquants = new ArrayList<>();
    private ArrayList<PersonnageBase> supports   = new ArrayList<>();

    private static final GestionnaireLiens gestionnaireLiens = new GestionnaireLiens();
    private GestionnaireCompagnons       gestionnaireCompagnons;
    private Gestionnaire_pet gestionnaireCreaturesSacrees;

    public Formation(Personnage_principale joueur) {
        this.joueur = joueur;
        this.gestionnaireCompagnons = null;
        this.gestionnaireCreaturesSacrees = null;
    }

    public Formation(Personnage_principale joueur, GestionnaireCompagnons gestionnaireCompagnons) {
        this.joueur = joueur;
        this.gestionnaireCompagnons = gestionnaireCompagnons;
        this.gestionnaireCreaturesSacrees = null;
    }

    public Formation(Personnage_principale joueur, GestionnaireCompagnons gestionnaireCompagnons,
                     Gestionnaire_pet gestionnaireCreaturesSacrees) {
        this.joueur = joueur;
        this.gestionnaireCompagnons = gestionnaireCompagnons;
        this.gestionnaireCreaturesSacrees = gestionnaireCreaturesSacrees;
    }

    /** Permet d'injecter le gestionnaire après construction (ex: chargement de sauvegarde). */
    public void setGestionnaireCompagnons(GestionnaireCompagnons gc) {
        this.gestionnaireCompagnons = gc;
    }

    public void setGestionnaireCreaturesSacrees(Gestionnaire_pet gc) {
        this.gestionnaireCreaturesSacrees = gc;
    }

    // ── Ajout / Retrait ───────────────────────────────────────────────────
    public String ajouterPersonnage(PersonnageBase p) {
        String msg;
        switch (p.getRole()) {
            case "Tank" -> {
                if (tank != null) return "Le slot Tank est deja occupe.";
                tank = p;
                msg = p.getNom() + " place en Tank.";
            }
            case "DPS" -> {
                if (attaquants.size() >= 2) return "Les slots Attaquants sont pleins (2/2 hors joueur).";
                attaquants.add(p);
                msg = p.getNom() + " place en Attaquant.";
            }
            case "Support" -> {
                if (supports.size() >= 3) return "Les slots Support sont pleins (3/3).";
                supports.add(p);
                msg = p.getNom() + " place en Support.";
            }
            default -> { return "Role inconnu : " + p.getRole(); }
        }
        appliquerBonusLiens();
        return msg;
    }

    public String retirerPersonnage(PersonnageBase p) {
        String msg;
        if (p == tank)             { tank = null;          msg = p.getNom() + " retire du slot Tank."; }
        else if (attaquants.remove(p)) msg = p.getNom() + " retire des Attaquants.";
        else if (supports.remove(p))   msg = p.getNom() + " retire des Supports.";
        else return p.getNom() + " n'est pas dans la formation.";
        appliquerBonusLiens();
        return msg;
    }

    // ── Application des bonus de liens sur toute l'equipe ─────────────────
    /**
     * Recalcule et applique les bonus de liens a tous les membres de l'equipe.
     * Appele a chaque changement de formation.
     */
    public void appliquerBonusLiens() {
        ArrayList<PersonnageBase> equipe = getEquipe();

        // Bonus de liens
        double bonusATK = gestionnaireLiens.getBonusATK(equipe);
        double bonusDEF = gestionnaireLiens.getBonusDEF(equipe);
        double bonusPV  = gestionnaireLiens.getBonusPV(equipe);
        double bonusVIT = gestionnaireLiens.getBonusVIT(equipe);

        for (PersonnageBase p : equipe) {
            p.setBonusLienATK(bonusATK);
            p.setBonusLienDEF(bonusDEF);
            p.setBonusLienPV(bonusPV);
            p.setBonusLienVIT(bonusVIT);
        }

        // Bonus compagnons (appliqué séparément sur chaque membre)
        if (gestionnaireCompagnons != null) {
            gestionnaireCompagnons.appliquerBonus(equipe);
        }
        // Bonus créatures sacrées (flat)
        if (gestionnaireCreaturesSacrees != null) {
            gestionnaireCreaturesSacrees.appliquerBonus(equipe);
        }
    }

    // ── Liens actifs (pour affichage) ─────────────────────────────────────
    public List<GestionnaireLiens.Lien> getLiensActifs() {
        return gestionnaireLiens.getLiensActifs(getEquipe());
    }

    // ── Utilitaires ───────────────────────────────────────────────────────
    public int getTailleEquipe() {
        int total = 1;
        if (tank != null) total++;
        total += attaquants.size();
        total += supports.size();
        return total;
    }

    public boolean estPleine() { return getTailleEquipe() >= 5; }

    public ArrayList<PersonnageBase> getEquipe() {
        ArrayList<PersonnageBase> equipe = new ArrayList<>();
        equipe.add(joueur);
        if (tank != null) equipe.add(tank);
        equipe.addAll(attaquants);
        equipe.addAll(supports);
        return equipe;
    }

    public void afficherFormation() {
        System.out.println("\n--- Formation actuelle (" + getTailleEquipe() + "/5) ---");
        System.out.println("[Tank]       : " + (tank != null ? tank.getNom() : "vide"));
        System.out.print("[Attaquants] : " + joueur.getNom() + " (vous)");
        if (!attaquants.isEmpty())
            for (PersonnageBase a : attaquants) System.out.print(", " + a.getNom());
        System.out.println();
        System.out.print("[Supports]   : ");
        if (supports.isEmpty()) {
            System.out.println("vide");
        } else {
            for (int i = 0; i < supports.size(); i++) {
                if (i > 0) System.out.print(", ");
                System.out.print(supports.get(i).getNom());
            }
            System.out.println();
        }

        // Afficher les liens actifs
        List<GestionnaireLiens.Lien> liens = getLiensActifs();
        if (!liens.isEmpty()) {
            System.out.println("\n[ Liens actifs ]");
            for (GestionnaireLiens.Lien l : liens) {
                System.out.println("  ✦ " + l.nom + " — " + l.description);
            }
        }
    }

    public PersonnageBase getTank()                        { return tank; }
    public ArrayList<PersonnageBase> getAttaquants()       { return attaquants; }
    public ArrayList<PersonnageBase> getSupports()         { return supports; }
}