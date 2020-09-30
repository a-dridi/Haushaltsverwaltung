/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.main;

import haushaltsverwaltung.db.DAO;

/**
 * It is possible to add here data sets that are loaded every program startup
 * Hier ist es möglich Datensätze, die bei jedem Programmstart geladen werden, hinzu zufügen. 
 * 
 * @author A.Dridi
 */
public class Programm {

    /**
     * Erstelllen und Speichern von Testdaten in die DB Wenn Aufgabe 1
     * ausgeführt wurde ist das hier obsolet!
     * 
     * Bsp. zum Testen: Benutzer mit Interessen:
     * skennedyb@gravatar.com   -- Pass: 323435352
     * 
     * 
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DAO dao = new DAO();

      


        System.out.println("******* Das Speichern der Testdaten in die DB wurde abgeschlossen *******");
    }

}
