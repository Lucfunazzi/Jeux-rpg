/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lancement.Quetes;

import java.util.List;

/**
 *
 * @author Lucas
 */
public abstract class Quete {

    /** Recompense generique par nom, ajoutee a l'inventaire comme un Materiau (ex: "Cle de Coffre de Guilde", 2). */
    public record RecompenseItem(String nom, int quantite) {}

    protected final String id;
    protected final String titre;
    protected final String description;
    protected final int recompenseXP;
    protected final int recompenseOr;
    protected final int recompenseParcheminC;
    protected final List<RecompenseItem> recompensesItems;
    protected boolean reclamee = false;
    protected boolean completee = false;

      public Quete(String id, String titre, String description,
                 int recompenseXP, int recompenseOr, int recompenseParcheminC) {
        this(id, titre, description, recompenseXP, recompenseOr, recompenseParcheminC, List.of());
      }

      public Quete(String id, String titre, String description,
                 int recompenseXP, int recompenseOr, int recompenseParcheminC,
                 List<RecompenseItem> recompensesItems) {
        this.id                  = id;
        this.titre               = titre;
        this.description         = description;
        this.recompenseXP        = recompenseXP;
        this.recompenseOr        = recompenseOr;
        this.recompenseParcheminC = recompenseParcheminC;
        this.recompensesItems    = recompensesItems;
      }

         public String getId()          { return id; }
    public String getTitre()       { return titre; }
    public String getDescription() { return description; }
    public int getRecompenseXP()   { return recompenseXP; }
    public int getRecompenseOr()   { return recompenseOr; }
    public int getRecompenseParcheminC() { return recompenseParcheminC; }
    public List<RecompenseItem> getRecompensesItems() { return recompensesItems; }

    public boolean isCompletee() { return completee; }
    public boolean isReclamee()  { return reclamee; }
    public void setReclamee(boolean v) { reclamee = v; }
    public void setCompletee(boolean v) { completee = v; }

    public abstract String getProgression();

    public String getEtat() {
        if (reclamee)   return "[FAIT]";
        if (completee)  return "[OK]  ";
        return          "[  ]  ";
    }

    public String afficherRecompenses() {
        StringBuilder sb = new StringBuilder();
        if (recompenseXP > 0)         sb.append(recompenseXP).append(" XP  ");
        if (recompenseOr > 0)         sb.append(recompenseOr).append(" or  ");
        if (recompenseParcheminC > 0) sb.append(recompenseParcheminC).append(" parchemins C  ");
        for (RecompenseItem item : recompensesItems) {
            sb.append(item.quantite()).append("x ").append(item.nom()).append("  ");
        }
        return sb.toString().trim();
    }
}

