package Equipement;

public class Equipement {
    public enum Slot {
        ARME, COUVRE_CHEF, TORSE, MAINS, JAMBIERES, BOTTES
    }
    public enum Rarete {
        C, B, A, S
    }
    public enum TypeArme {
        EPEE, LANCE, BATON, KUNAI, GANTS, FOUET, AUCUN
    }

    private final String   nom;
    private final Slot     slot;
    private final Rarete   rarete;
    private final TypeArme typeArme;

    private final double bonusATKBase;
    private final double bonusDEFBase;
    private final double bonusPVBase;
    private final double bonusVITBase;

    private double bonusATKActuel;
    private double bonusDEFActuel;
    private double bonusPVActuel;
    private double bonusVITActuel;

    private int niveauFortification = 0;
    private int niveauAffinage      = 0;

    public Equipement(String nom, Slot slot, Rarete rarete, TypeArme typeArme,
                      double bonusATK, double bonusDEF, double bonusPV, double bonusVIT) {
        this.nom          = nom;
        this.slot         = slot;
        this.rarete       = rarete;
        this.typeArme     = typeArme;
        this.bonusATKBase = bonusATK;
        this.bonusDEFBase = bonusDEF;
        this.bonusPVBase  = bonusPV;
        this.bonusVITBase = bonusVIT;
        this.bonusATKActuel = bonusATK;
        this.bonusDEFActuel = bonusDEF;
        this.bonusPVActuel  = bonusPV;
        this.bonusVITActuel = bonusVIT;
    }

    public String getNom()        { return nom; }
    public Slot getSlot()         { return slot; }
    public Rarete getRarete()     { return rarete; }
    public TypeArme getTypeArme() { return typeArme; }

    public double getBonusATK() { return bonusATKActuel; }
    public double getBonusDEF() { return bonusDEFActuel; }
    public double getBonusPV()  { return bonusPVActuel; }
    public double getBonusVIT() { return bonusVITActuel; }

    public double getBonusATKBase() { return bonusATKBase; }
    public double getBonusDEFBase() { return bonusDEFBase; }
    public double getBonusPVBase()  { return bonusPVBase; }
    public double getBonusVITBase() { return bonusVITBase; }

    // ── Fortification ─────────────────────────────────────────────────────
    public int getNiveauFortification() { return niveauFortification; }

    public void setNiveauFortification(int niveau) {
        this.niveauFortification = niveau;
        recalculerStats();
    }

    public void fortifier() {
        this.niveauFortification++;
        recalculerStats();
    }

    public int getCoutFortification() {
        // Coût linéaire : 200 × (niveau_actuel + 1)
        // Fort.0→1 : 200 or | Fort.9→10 : 2000 or | Fort.19→20 : 4000 or
        return 200 * (niveauFortification + 1);
    }

    // ── Affinage ──────────────────────────────────────────────────────────
    public int getNiveauAffinage() { return niveauAffinage; }

    public void setNiveauAffinage(int niveau) {
        this.niveauAffinage = niveau;
        recalculerStats();
    }

    public void affiner() {
        this.niveauAffinage++;
        recalculerStats();
    }

    /**
     * Cout pour passer du niveau actuel au niveau suivant : (niveauAffinage + 1) * 2
     * Ex : passer de 0 a 1 coute 2 pierres, de 1 a 2 coute 4 pierres, etc.
     */
    public int getCoutAffinageProchainNiveau() {
        return (niveauAffinage + 1) * 2;
    }

    /**
     * Multiplicateur d'affinage : +niveauAffinage% sur les stats natives de la piece.
     * S'applique APRES la fortification.
     */
    public double getMultiplicateurAffinage() {
        return 1.0 + (niveauAffinage / 100.0);
    }

    // ── Recalcul interne ──────────────────────────────────────────────────
    /**
     * Ordre : base -> x(1 + fortification) -> x(1 + affinage%)
     * L'affinage s'applique uniquement sur les stats que la piece ameliore nativement
     * (stat de base > 0).
     */
    private void recalculerStats() {
        // Fort.N = base × (1 + N × 0.08) → +8% par niveau de fortification
        double multFort     = 1.0 + niveauFortification * 0.08;
        double multAffinage = getMultiplicateurAffinage();

        bonusATKActuel = bonusATKBase * multFort * (bonusATKBase > 0 ? multAffinage : 1.0);
        bonusDEFActuel = bonusDEFBase * multFort * (bonusDEFBase > 0 ? multAffinage : 1.0);
        bonusPVActuel  = bonusPVBase  * multFort * (bonusPVBase  > 0 ? multAffinage : 1.0);
        bonusVITActuel = bonusVITBase * multFort * (bonusVITBase > 0 ? multAffinage : 1.0);
    }

    // ── Affichage ─────────────────────────────────────────────────────────
    public String getNomAffiche() {
        String s = niveauFortification > 0 ? nom + " +" + niveauFortification : nom;
        if (niveauAffinage > 0) s += " [Aff." + niveauAffinage + "]";
        return s;
    }

    public String getNomSlot() {
        return switch (slot) {
            case ARME        -> "Arme";
            case COUVRE_CHEF -> "Couvre-chef";
            case TORSE       -> "Torse";
            case MAINS       -> "Mains";
            case JAMBIERES   -> "Jambieres";
            case BOTTES      -> "Bottes";
        };
    }

    public String getNomRarete() {
        return switch (rarete) {
            case C -> "[C]";
            case B -> "[B]";
            case A -> "[A]";
            case S -> "[S]";
        };
    }

    public String getDescriptionBonus() {
        StringBuilder sb = new StringBuilder();
        if (bonusATKActuel > 0) sb.append("ATK +").append((int) bonusATKActuel).append(" ");
        if (bonusDEFActuel > 0) sb.append("DEF +").append((int) bonusDEFActuel).append(" ");
        if (bonusPVActuel  > 0) sb.append("PV +").append((int) bonusPVActuel).append(" ");
        if (bonusVITActuel > 0) sb.append("VIT +").append((int) bonusVITActuel).append(" ");
        return sb.toString().trim();
    }

    @Override
    public String toString() {
        return getNomRarete() + " " + getNomAffiche()
                + " (" + getNomSlot() + ") — " + getDescriptionBonus();
    }
}