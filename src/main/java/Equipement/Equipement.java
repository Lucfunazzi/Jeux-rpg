package Equipement;

public class Equipement {
    public enum Slot {
        ARME, COUVRE_CHEF, TORSE, MAINS, JAMBIERES, BOTTES
    }
    public enum Rarete {
        C, B, A, S
    }
    public enum TypeArme {
        EPEE, LANCE, BATON, GANTS, FOUET, AUCUN
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

    // ── Pierres (sockets) ───────────────────────────────────────────────────
    public static final int NB_EMPLACEMENTS_PIERRES = 5;
    private final Pierre[] pierres = new Pierre[NB_EMPLACEMENTS_PIERRES];

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

    // ── Pierres (sockets) ────────────────────────────────────────────────
    public Pierre getPierre(int emplacement) {
        verifierEmplacement(emplacement);
        return pierres[emplacement];
    }

    public Pierre[] getPierres() { return pierres; }

    /**
     * Insere une pierre dans l'emplacement donne, en remplacant celle deja presente le cas echeant.
     * Refuse si une pierre du meme type occupe deja un autre emplacement de cette piece.
     * @return "OK" si l'insertion a reussi, un message d'erreur sinon.
     */
    public String insererPierre(int emplacement, Pierre pierre) {
        verifierEmplacement(emplacement);
        for (int i = 0; i < NB_EMPLACEMENTS_PIERRES; i++) {
            if (i != emplacement && pierres[i] != null && pierres[i].getType() == pierre.getType()) {
                return "Une pierre de " + pierre.getNomType() + " est deja inseree sur cette piece.";
            }
        }
        pierres[emplacement] = pierre;
        return "OK";
    }

    public Pierre retirerPierre(int emplacement) {
        verifierEmplacement(emplacement);
        Pierre ancienne = pierres[emplacement];
        pierres[emplacement] = null;
        return ancienne;
    }

    private void verifierEmplacement(int emplacement) {
        if (emplacement < 0 || emplacement >= NB_EMPLACEMENTS_PIERRES) {
            throw new IllegalArgumentException("Emplacement de pierre invalide : " + emplacement);
        }
    }

    /** Somme des bonus (%) de toutes les pierres du type donne inserees sur cette piece. */
    public double getBonusPierre(Pierre.Type type) {
        double total = 0;
        for (Pierre p : pierres) {
            if (p != null && p.getType() == type) total += p.getBonusPourcent();
        }
        return total;
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

    /** Nom affiche du type d'arme (ex. "Lance", "Baton"). Vide si ce n'est pas une arme. */
    public String getNomTypeArme() {
        return switch (typeArme) {
            case LANCE -> "Lance";
            case BATON -> "Baton";
            case GANTS -> "Gants";
            case FOUET -> "Fouet";
            case EPEE  -> "Epee";
            case AUCUN -> "";
        };
    }

    /** Classe pouvant equiper ce type d'arme. Vide si ce n'est pas une arme. */
    public String getClasseEquipable() {
        return switch (typeArme) {
            case LANCE -> "Chevalier";
            case BATON -> "Mage";
            case GANTS -> "Chasseur de Dragon";
            case FOUET -> "Invocateur";
            case EPEE, AUCUN -> "";
        };
    }

    /** Description simple et lisible des effets de la piece (bonus + classe pour les armes). */
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        if (bonusATKActuel > 0) sb.append("Augmente l'attaque de ").append((int) bonusATKActuel).append(". ");
        if (bonusDEFActuel > 0) sb.append("Augmente la defense de ").append((int) bonusDEFActuel).append(". ");
        if (bonusPVActuel  > 0) sb.append("Augmente les PV de ").append((int) bonusPVActuel).append(". ");
        if (bonusVITActuel > 0) sb.append("Augmente la vitesse de ").append((int) bonusVITActuel).append(". ");
        if (typeArme != TypeArme.AUCUN && !getClasseEquipable().isEmpty()) {
            sb.append("Equipable par : ").append(getClasseEquipable())
              .append(" (").append(getNomTypeArme()).append(").");
        }
        return sb.toString().trim();
    }

    @Override
    public String toString() {
        return getNomRarete() + " " + getNomAffiche()
                + " (" + getNomSlot() + ") — " + getDescription();
    }
}