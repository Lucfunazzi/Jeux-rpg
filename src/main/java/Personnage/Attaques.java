/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Personnage;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Lucas
 */

public interface Attaques {
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log);
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log);
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log);

    public String[] getNomsAttaques();
    public void descriptionAttaqueBase();
    public void descriptionAttaqueSpeciale();
    public void descriptionAttaqueUltime();
}