/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.db;

import haushaltsverwaltung.model.Ausgaben;
import haushaltsverwaltung.model.AusgabenBudget;
import haushaltsverwaltung.model.Ausgabenausgabezeitraum;
import haushaltsverwaltung.model.Ausgabenkategorie;
import haushaltsverwaltung.model.Benutzer;
import haushaltsverwaltung.model.Buecher;
import haushaltsverwaltung.model.Buecherkategorie;
import haushaltsverwaltung.model.Buecherzustand;
import haushaltsverwaltung.model.DatenbankNotizen;
import haushaltsverwaltung.model.Einnahmen;
import haushaltsverwaltung.model.EinnahmenJahrEntwicklung;
import haushaltsverwaltung.model.EinnahmenKategorie;
import haushaltsverwaltung.model.EinnahmenMonatEntwicklung;
import haushaltsverwaltung.model.Handel;
import haushaltsverwaltung.model.KryptowaehrungenExchange;
import haushaltsverwaltung.model.KryptowaehrungenKaufVerkauf;
import haushaltsverwaltung.model.KryptowaehrungenUeberweisungen;
import haushaltsverwaltung.model.KryptowaehrungenVermoegen;
import haushaltsverwaltung.model.KryptowaehrungenVorgang;
import haushaltsverwaltung.model.KryptowaehrungenWaehrungen;
import haushaltsverwaltung.model.KryptowaehrungenWerteVerz;
import haushaltsverwaltung.model.MedienMusik;
import haushaltsverwaltung.model.MedienMusikGenre;
import haushaltsverwaltung.model.MedienSoftware;
import haushaltsverwaltung.model.MedienSoftwareBetriebssystem;
import haushaltsverwaltung.model.MedienSoftwareHersteller;
import haushaltsverwaltung.model.MedienVideoclips;
import haushaltsverwaltung.model.MedienVideoclipsSprache;
import haushaltsverwaltung.model.MedienVideos;
import haushaltsverwaltung.model.MedienVideosGenre;
import haushaltsverwaltung.model.MedienVideosSprache;
import haushaltsverwaltung.model.Ordnung;
import haushaltsverwaltung.model.Ordnungkategorie;
import haushaltsverwaltung.model.Reserven;
import haushaltsverwaltung.model.ReservenKategorie;
import haushaltsverwaltung.model.ReservenWaehrung;
import haushaltsverwaltung.model.Sparen;
import haushaltsverwaltung.model.Vermoegen;
import haushaltsverwaltung.model.VermoegenJaehrlich;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ardat
 */
public class DAOTest {
    
    public static DAO dao;
    
    public DAOTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void prepareDatabase() {
        dao = new DAO();
    }
    
    @After
    public void closeDatabase() {
        dao.close();
    }


    /**
     * SQLInjection
     * 
     * The row should be added without any deletion on the table
     */
    @Test
    public void testInsertEinnahmen() {
        System.out.println("insertEinnahmen");
        Einnahmen einnahmen = new Einnahmen();
        einnahmen.setInformationen("; delete from Einnahmen");
        
        int expResult = dao.getAllEinnahmen().size()+1;
        dao.insertEinnahmen(einnahmen);
        int result = dao.getAllEinnahmen().size();
        assertEquals(expResult, result);
    }

    
    
}
