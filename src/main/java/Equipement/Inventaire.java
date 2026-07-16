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

    public static class StackCarteOr {
        private final CarteOr carte;
        private int quantite;

        public StackCarteOr(CarteOr carte, int quantite) {
            this.carte    = carte;
            this.quantite = quantite;
        }

        public CarteOr getCarte()           { return carte; }
        public int getQuantite()            { return quantite; }
        public void ajouterQuantite(int n)  { this.quantite = Math.min(CarteOr.STOCK_MAX, this.quantite + n); }
        public void retirerQuantite(int n)  { this.quantite -= n; }
        public boolean estVide()            { return quantite <= 0; }

        @Override
        public String toString() {
            return carte.nom + " x" + quantite
                    + "  [" + String.format("%,d", carte.valeurOr) + " or/carte]";
        }
    }

    private final ArrayList<StackEquipement> stacks      = new ArrayList<>();
    private final ArrayList<Materiau>        materiaux   = new ArrayList<>();
    private final ArrayList<StackParchemin>  parchemins  = new ArrayList<>();
    private final ArrayList<StackCarteOr>    cartesOr    = new ArrayList<>();

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

    // ── Cartes d'or ───────────────────────────────────────────────────────

    /**
     * Ajoute des cartes d'or au stock (plafonné à 999 par niveau).
     * @return le nombre de cartes réellement ajoutées (peut être < quantite si stock plein)
     */
    public int ajouterCartesOr(CarteOr niveau, int quantite) {
        for (StackCarteOr s : cartesOr) {
            if (s.getCarte() == niveau) {
                int avant = s.getQuantite();
                s.ajouterQuantite(quantite);
                return s.getQuantite() - avant;
            }
        }
        int ajoute = Math.min(quantite, CarteOr.STOCK_MAX);
        cartesOr.add(new StackCarteOr(niveau, ajoute));
        return ajoute;
    }

    /**
     * Retire un certain nombre de cartes d'or du stock.
     * @return true si le stock était suffisant
     */
    public boolean retirerCartesOr(CarteOr niveau, int quantite) {
        for (int i = 0; i < cartesOr.size(); i++) {
            StackCarteOr s = cartesOr.get(i);
            if (s.getCarte() == niveau && s.getQuantite() >= quantite) {
                s.retirerQuantite(quantite);
                if (s.estVide()) cartesOr.remove(i);
                return true;
            }
        }
        return false;
    }

    public int getQuantiteCartesOr(CarteOr niveau) {
        for (StackCarteOr s : cartesOr) {
            if (s.getCarte() == niveau) return s.getQuantite();
        }
        return 0;
    }

    public ArrayList<StackCarteOr> getCartesOr() { return cartesOr; }

    // ── Utilitaire global ─────────────────────────────────────────────────
    public boolean estVide() {
        return stacks.isEmpty() && materiaux.isEmpty()
                && parchemins.isEmpty() && cartesOr.isEmpty();
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
        boolean aucunConso = parchemins.isEmpty() && cartesOr.isEmpty();
        if (aucunConso) {
            System.out.println("  Aucun consommable.");
        } else {
            for (StackParchemin s : parchemins) {
                String nom  = new ParcheminXP(s.getRarete()).toString();
                String ligne = "  " + nom;
                if (s.getQuantite() > 1) ligne += " x" + s.getQuantite();
                System.out.println(ligne);
            }
            for (StackCarteOr s : cartesOr) {
                System.out.println("  " + s);
            }
        }
    }

    private boolean memeStack(Equipement a, Equipement b) {
        return a.getNom().equals(b.getNom()) && a.getRarete() == b.getRarete();
    }
}