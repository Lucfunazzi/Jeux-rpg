package Equipement;

import java.util.ArrayList;

public class Inventaire {

    public static class StackEquipement {
        private final Equipement equipement;
        private int quantite;

        public StackEquipement(Equipement equipement, int quantite) {
            this.equipement = equipement;
            this.quantite   = quantite;
        }

        public Equipement getEquipement() { return equipement; }
        public int getQuantite()          { return quantite; }
        public void ajouterQuantite(int n) { this.quantite += n; }
        public void retirerQuantite(int n) { this.quantite -= n; }
        public boolean estVide()           { return quantite <= 0; }
    }

    public static class StackParchemin {
        private final ParcheminXP.Rarete rarete;
        private int quantite;

        public StackParchemin(ParcheminXP.Rarete rarete, int quantite) {
            this.rarete   = rarete;
            this.quantite = quantite;
        }

        public ParcheminXP.Rarete getRarete()   { return rarete; }
        public int getQuantite()                 { return quantite; }
        public void ajouterQuantite(int n)       { this.quantite += n; }
        public void retirerQuantite(int n)       { this.quantite -= n; }
        public boolean estVide()                 { return quantite <= 0; }

        @Override
        public String toString() {
            return new ParcheminXP(rarete).toString() + " x" + quantite;
        }
    }

    private final ArrayList<StackEquipement> stacks       = new ArrayList<>();
    private final ArrayList<Materiau>        materiaux    = new ArrayList<>();
    private final ArrayList<StackParchemin>  parchemins   = new ArrayList<>();

    // ── Équipements ───────────────────────────────────────────────────────
    public void ajouterEquipement(Equipement e) {
        for (StackEquipement s : stacks) {
            if (memeStack(s.getEquipement(), e)) {
                s.ajouterQuantite(1);
                return;
            }
        }
        stacks.add(new StackEquipement(e, 1));
    }

    public boolean retirerEquipement(Equipement e) {
        for (int i = 0; i < stacks.size(); i++) {
            StackEquipement s = stacks.get(i);
            if (memeStack(s.getEquipement(), e)) {
                s.retirerQuantite(1);
                if (s.estVide()) stacks.remove(i);
                return true;
            }
        }
        return false;
    }

    public ArrayList<StackEquipement> getStacks() { return stacks; }

    public ArrayList<StackEquipement> getStacksParSlot(Equipement.Slot slot) {
        ArrayList<StackEquipement> liste = new ArrayList<>();
        for (StackEquipement s : stacks) {
            if (s.getEquipement().getSlot() == slot) liste.add(s);
        }
        return liste;
    }

    public ArrayList<Equipement> getEquipements() {
        ArrayList<Equipement> liste = new ArrayList<>();
        for (StackEquipement s : stacks) liste.add(s.getEquipement());
        return liste;
    }

    // ── Matériaux ─────────────────────────────────────────────────────────
    public void ajouterMateriau(String nom, int quantite) {
        for (Materiau m : materiaux) {
            if (m.getNom().equals(nom)) {
                m.ajouterQuantite(quantite);
                return;
            }
        }
        materiaux.add(new Materiau(nom, quantite));
    }

    public boolean retirerMateriau(String nom, int quantite) {
        for (Materiau m : materiaux) {
            if (m.getNom().equals(nom) && m.getQuantite() >= quantite) {
                m.retirerQuantite(quantite);
                if (m.estVide()) materiaux.remove(m);
                return true;
            }
        }
        return false;
    }

    public int getQuantiteMateriau(String nom) {
        for (Materiau m : materiaux) {
            if (m.getNom().equals(nom)) return m.getQuantite();
        }
        return 0;
    }

    public ArrayList<Materiau> getMateriaux() { return materiaux; }

    // ── Parchemins XP (consommables) ──────────────────────────────────────
    public void ajouterParcheminXP(ParcheminXP.Rarete rarete, int quantite) {
        for (StackParchemin s : parchemins) {
            if (s.getRarete() == rarete) {
                s.ajouterQuantite(quantite);
                return;
            }
        }
        parchemins.add(new StackParchemin(rarete, quantite));
    }

    /**
     * Retire un parchemin XP de la rareté donnée.
     * @return true si le retrait a réussi (stock suffisant)
     */
    public boolean retirerParcheminXP(ParcheminXP.Rarete rarete) {
        for (int i = 0; i < parchemins.size(); i++) {
            StackParchemin s = parchemins.get(i);
            if (s.getRarete() == rarete) {
                s.retirerQuantite(1);
                if (s.estVide()) parchemins.remove(i);
                return true;
            }
        }
        return false;
    }

    public int getQuantiteParcheminXP(ParcheminXP.Rarete rarete) {
        for (StackParchemin s : parchemins) {
            if (s.getRarete() == rarete) return s.getQuantite();
        }
        return 0;
    }

    public ArrayList<StackParchemin> getParchemins() { return parchemins; }

    // ── Utilitaire global ─────────────────────────────────────────────────
    public boolean estVide() {
        return stacks.isEmpty() && materiaux.isEmpty() && parchemins.isEmpty();
    }

    // ── Affichage ─────────────────────────────────────────────────────────
    public void afficher() {
        System.out.println("\n========================================");
        System.out.println("            INVENTAIRE");
        System.out.println("========================================");

        System.out.println("\n[ Equipements (" + stacks.size() + " types) ]");
        if (stacks.isEmpty()) {
            System.out.println("  Aucun equipement.");
        } else {
            for (int i = 0; i < stacks.size(); i++) {
                StackEquipement s = stacks.get(i);
                String ligne = "  " + (i + 1) + ". " + s.getEquipement();
                if (s.getQuantite() > 1) ligne += " x" + s.getQuantite();
                System.out.println(ligne);
            }
        }

        System.out.println("\n[ Materiaux (" + materiaux.size() + " types) ]");
        if (materiaux.isEmpty()) {
            System.out.println("  Aucun materiau.");
        } else {
            for (int i = 0; i < materiaux.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + materiaux.get(i));
            }
        }

        System.out.println("\n[ Consommables ]");
        if (parchemins.isEmpty()) {
            System.out.println("  Aucun consommable.");
        } else {
            for (StackParchemin s : parchemins) {
                // Afficher sans le "x1" si quantité = 1 pour la lisibilité
                String nom = new ParcheminXP(s.getRarete()).toString();
                String ligne = "  " + nom;
                if (s.getQuantite() > 1) ligne += " x" + s.getQuantite();
                System.out.println(ligne);
            }
        }
    }

    private boolean memeStack(Equipement a, Equipement b) {
        return a.getNom().equals(b.getNom()) && a.getRarete() == b.getRarete();
    }
}