/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lancement.Quetes;

/**
 *
 * @author Lucas
 */
public abstract class Quete {
    protected final String id;
    protected final String titre;
    protected final String description;
    protected final int recompenseXP;
    protected final int recompenseOr;
    protected final int recompenseParcheminC;
    protected boolean reclamee = false;
    protected boolean completee = false;
    
      public Quete(String id, String titre, String description,
                 int recompenseXP, int recompenseOr, int recompenseParcheminC) {
        this.id                  = id;
        this.titre               = titre;
        this.description         = description;
        this.recompenseXP        = recompenseXP;
        this.recompenseOr        = recompenseOr;
        this.recompenseParcheminC = recompenseParcheminC;
      }
      
         public String getId()          { return id; }
    public String getTitre()       { return titre; }
    public String getDescription() { return description; }
    public int getRecompenseXP()   { return recompenseXP; }
    public int getRecompenseOr()   { return recompenseOr; }
    public int getRecompenseParcheminC() { return recompenseParcheminC; }

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
        if (recompenseParcheminC > 0) sb.append(recompenseParcheminC).append(" parchemins C");
        return sb.toString().trim();
    }
}

