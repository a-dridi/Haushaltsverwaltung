/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.controller;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import haushaltsverwaltung.db.DAO;
import haushaltsverwaltung.model.Ausgaben;
import haushaltsverwaltung.model.Ausgabenausgabezeitraum;
import haushaltsverwaltung.model.Ausgabenkategorie;
import haushaltsverwaltung.model.Benutzer;
import haushaltsverwaltung.model.Buecher;
import haushaltsverwaltung.model.Buecherkategorie;
import haushaltsverwaltung.model.Buecherzustand;
import haushaltsverwaltung.model.Ordnung;
import haushaltsverwaltung.model.Ordnungkategorie;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.primefaces.event.CellEditEvent;

/**
 * Testcontroller für spezifische Abfragen
 *
 * @author A.Dridi
 */
@Named(value = "benutzerdefiniertController")
@SessionScoped
public class BenutzerdefiniertController implements Serializable {

    private Benutzer benutzer = null;

    private boolean isAusgaben;
    private boolean isBuecher;
    private boolean isOrdnung;

    private String listenSQL;
    //Ergebnis der Abfrage:
    private List<Buecher> buecherListenSQL = new ArrayList<>();
    private List<Ausgaben> ausgabenListenSQL = new ArrayList<>();
    private List<Ordnung> ordnungListenSQL = new ArrayList<>();

    //Filtered:
    private List<Buecher> filteredBuecherListenSQL = new ArrayList<>();
    private List<Ausgaben> filteredAusgabenListenSQL = new ArrayList<>();
    private List<Ordnung> filteredOrdnungListenSQL = new ArrayList<>();

    private List<Buecherkategorie> buecherkategorien = new ArrayList<>();
    private List<Buecherzustand> buecherzustaende = new ArrayList<>();
    private List<Ordnungkategorie> ordnungkategorien = new ArrayList<>();
    private List<Ausgabenkategorie> ausgabenkategorien = new ArrayList<>();
    private List<Ausgabenausgabezeitraum> ausgabenausgabezeitraeume = new ArrayList<>();

    private String werteSQL;
    private List<String> werteabfragenListe = new ArrayList<>();
    private List<String> filteredWerteabfragenListe = new ArrayList<>();

    //Gewählte Kateogorien:
    private Buecherkategorie buecherkategorie;
    private Buecherzustand buecherzustand;
    private Ausgabenausgabezeitraum ausgabenausgabezeitraum;
    private Ausgabenkategorie ausgabenkategorie;
    private Ordnungkategorie ordnungkategorie;

    /**
     * Creates a new instance of BenutzerController
     */
    public BenutzerdefiniertController() {
        DAO dao = new DAO();
    }

    @PostConstruct
    private void init() {

    }

    public void updateData() {
        DAO dao = new DAO();
        buecherkategorien = dao.getAllBuecherkategorie();
        buecherzustaende = dao.getAllBuecherzustand();
        ordnungkategorien = dao.getAllOrdnungskategorie();
        ausgabenkategorien = dao.getAllAusgabenkategorie();
        ausgabenausgabezeitraeume = dao.getAllAusgabenausgabezeitraum();

        filteredAusgabenListenSQL = dao.getAllAusgaben();
        ausgabenListenSQL = dao.getAllAusgaben();
        filteredBuecherListenSQL = dao.getAllBuecher();
        buecherListenSQL = dao.getAllBuecher();
        ordnungListenSQL = dao.getAllOrdnung();
        filteredOrdnungListenSQL = dao.getAllOrdnung();

    }

    public void preProcessPDF(Object document) {
        Document pdf = (Document) document;
        pdf.setPageSize(PageSize.A4.rotate());
        pdf.open();
    }

    public void onCellEdit(CellEditEvent event) {
        DAO dao = new DAO();

        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        int zeile = event.getRowIndex();
        if (newValue != null && !newValue.equals(oldValue)) {
            if (isAusgaben) {

                dao.updateAusgaben(this.ausgabenListenSQL.get(zeile));

            } else if (isOrdnung) {
                dao.updateOrdnung(this.ordnungListenSQL.get(zeile));

            } else if (isBuecher) {
                dao.updateBuecher(this.buecherListenSQL.get(zeile));

            } else {

            }
        }

    }

    //Ajax Abfragen:
    /**
     * Abfrage von Listen z.B.: Bücher deren Namen mit A beginnen
     *
     * @return
     */
    public void abfrageListenSQL() {
        DAO dao = new DAO();
        FacesContext context = FacesContext.getCurrentInstance();
        this.isAusgaben = this.isBuecher = this.isOrdnung = false;

        if (this.listenSQL.contains("insert into") || this.listenSQL.contains("delete from")) {

            dao.customGetAll(listenSQL);

            return;
        }

        if (this.listenSQL.contains("Buecher") || this.listenSQL.contains("buecher")) {
            this.isBuecher = true;
            buecherkategorien = dao.getAllBuecherkategorie();
            buecherzustaende = dao.getAllBuecherzustand();

            this.buecherListenSQL = dao.customGetAllBuecher(listenSQL);
            this.filteredBuecherListenSQL = dao.customGetAllBuecher(listenSQL);

        } else if (this.listenSQL.contains("Ausgaben") || this.listenSQL.contains("ausgaben")) {
            this.isAusgaben = true;
            ausgabenkategorien = dao.getAllAusgabenkategorie();
            ausgabenausgabezeitraeume = dao.getAllAusgabenausgabezeitraum();

            this.filteredAusgabenListenSQL = dao.customGetAllAusgaben(listenSQL);
            this.ausgabenListenSQL = dao.customGetAllAusgaben(listenSQL);

        } else if (this.listenSQL.contains("Ordnung") || this.listenSQL.contains("ordnung")) {
            this.isOrdnung = true;
            ordnungkategorien = dao.getAllOrdnungskategorie();

            this.ordnungListenSQL = dao.customGetAllOrdnung(listenSQL);
            this.filteredOrdnungListenSQL = dao.customGetAllOrdnung(listenSQL);

        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nur Abfragen von Tabellen möglich! ", "Bitte verwenden Sie für solche Art von Abfragen das untere Textfeld (Abfrage von Werten)."));

        }

        // return "benutzerdefiniert.xhtml";
    }

    public void abfragenWerteSQL() {
        DAO dao = new DAO();

        this.werteabfragenListe = dao.customGetValue(werteSQL);
        this.filteredWerteabfragenListe = dao.customGetValue(werteSQL);
    }

    /**
     * Ausloggen des Benutzers. Aktualisieren der Benutzerdaten Wird aufgerufen,
     * wenn der Logout-Button vom Benutzer getätigt wird. ODER: Beim Menüpunkt
     * Profil
     *
     * @return String Retourniert Loginseite als String
     */
    public String logout() {
        benutzer = null;
        HttpSession s = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        // Session holen und für ungültig erklären
        s.invalidate();
        return "index.xhtml";
    }

    /**
     * Prüft ob ein gültiges Login besteht.
     *
     * @return true wenn ein Benutzer eingeloggt ist, sonst false
     *
     */
    public boolean isLoggedIn() {
        return benutzer != null;

    }

    public Benutzer getBenutzer() {
        return benutzer;
    }

    public void setBenutzer(Benutzer benutzer) {
        this.benutzer = benutzer;
    }

    public String getWerteSQL() {
        return werteSQL;
    }

    public void setWerteSQL(String werteSQL) {
        this.werteSQL = werteSQL;
    }

    public String getListenSQL() {
        return listenSQL;
    }

    public void setListenSQL(String listenSQL) {
        this.listenSQL = listenSQL;
    }

    public boolean isIsAusgaben() {
        return isAusgaben;
    }

    public void setIsAusgaben(boolean isAusgaben) {
        this.isAusgaben = isAusgaben;
    }

    public boolean isIsBuecher() {
        return isBuecher;
    }

    public void setIsBuecher(boolean isBuecher) {
        this.isBuecher = isBuecher;
    }

    public boolean isIsOrdnung() {
        return isOrdnung;
    }

    public void setIsOrdnung(boolean isOrdnung) {
        this.isOrdnung = isOrdnung;
    }

    public List<Buecher> getBuecherListenSQL() {
        return buecherListenSQL;
    }

    public void setBuecherListenSQL(List<Buecher> buecherListenSQL) {
        this.buecherListenSQL = buecherListenSQL;
    }

    public List<Ausgaben> getAusgabenListenSQL() {
        return ausgabenListenSQL;
    }

    public void setAusgabenListenSQL(List<Ausgaben> ausgabenListenSQL) {
        this.ausgabenListenSQL = ausgabenListenSQL;
    }

    public List<Ordnung> getOrdnungListenSQL() {
        return ordnungListenSQL;
    }

    public Buecherzustand getBuecherzustand() {
        return buecherzustand;
    }

    public void setBuecherzustand(Buecherzustand buecherzustand) {
        this.buecherzustand = buecherzustand;
    }

    public Ausgabenausgabezeitraum getAusgabenausgabezeitraum() {
        return ausgabenausgabezeitraum;
    }

    public void setAusgabenausgabezeitraum(Ausgabenausgabezeitraum ausgabenausgabezeitraum) {
        this.ausgabenausgabezeitraum = ausgabenausgabezeitraum;
    }

    public Ausgabenkategorie getAusgabenkategorie() {
        return ausgabenkategorie;
    }

    public void setAusgabenkategorie(Ausgabenkategorie ausgabenkategorie) {
        this.ausgabenkategorie = ausgabenkategorie;
    }

    public Ordnungkategorie getOrdnungkategorie() {
        return ordnungkategorie;
    }

    public void setOrdnungkategorie(Ordnungkategorie ordnungkategorie) {
        this.ordnungkategorie = ordnungkategorie;
    }

    public void setOrdnungListenSQL(List<Ordnung> ordnungListenSQL) {
        this.ordnungListenSQL = ordnungListenSQL;
    }

    public List<Buecher> getFilteredBuecherListenSQL() {
        return filteredBuecherListenSQL;
    }

    public void setFilteredBuecherListenSQL(List<Buecher> filteredBuecherListenSQL) {
        this.filteredBuecherListenSQL = filteredBuecherListenSQL;
    }

    public Buecherkategorie getBuecherkategorie() {
        return buecherkategorie;
    }

    public void setBuecherkategorie(Buecherkategorie buecherkategorie) {
        this.buecherkategorie = buecherkategorie;
    }

    public List<Ausgaben> getFilteredAusgabenListenSQL() {
        return filteredAusgabenListenSQL;
    }

    public void setFilteredAusgabenListenSQL(List<Ausgaben> filteredAusgabenListenSQL) {
        this.filteredAusgabenListenSQL = filteredAusgabenListenSQL;
    }

    public List<Ordnung> getFilteredOrdnungListenSQL() {
        return filteredOrdnungListenSQL;
    }

    public void setFilteredOrdnungListenSQL(List<Ordnung> filteredOrdnungListenSQL) {
        this.filteredOrdnungListenSQL = filteredOrdnungListenSQL;
    }

    public List<Buecherkategorie> getBuecherkategorien() {
        return buecherkategorien;
    }

    public void setBuecherkategorien(List<Buecherkategorie> buecherkategorien) {
        this.buecherkategorien = buecherkategorien;
    }

    public List<Buecherzustand> getBuecherzustaende() {
        return buecherzustaende;
    }

    public void setBuecherzustaende(List<Buecherzustand> buecherzustaende) {
        this.buecherzustaende = buecherzustaende;
    }

    public List<Ordnungkategorie> getOrdnungkategorien() {
        return ordnungkategorien;
    }

    public void setOrdnungkategorien(List<Ordnungkategorie> ordnungkategorien) {
        this.ordnungkategorien = ordnungkategorien;
    }

    public List<Ausgabenkategorie> getAusgabenkategorien() {
        return ausgabenkategorien;
    }

    public void setAusgabenkategorien(List<Ausgabenkategorie> ausgabenkategorien) {
        this.ausgabenkategorien = ausgabenkategorien;
    }

    public List<Ausgabenausgabezeitraum> getAusgabenausgabezeitraeume() {
        return ausgabenausgabezeitraeume;
    }

    public void setAusgabenausgabezeitraeume(List<Ausgabenausgabezeitraum> ausgabenausgabezeitraeume) {
        this.ausgabenausgabezeitraeume = ausgabenausgabezeitraeume;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.benutzer);
        hash = 79 * hash + (this.isAusgaben ? 1 : 0);
        hash = 79 * hash + (this.isBuecher ? 1 : 0);
        hash = 79 * hash + (this.isOrdnung ? 1 : 0);
        hash = 79 * hash + Objects.hashCode(this.listenSQL);
        hash = 79 * hash + Objects.hashCode(this.buecherListenSQL);
        hash = 79 * hash + Objects.hashCode(this.ausgabenListenSQL);
        hash = 79 * hash + Objects.hashCode(this.ordnungListenSQL);
        hash = 79 * hash + Objects.hashCode(this.filteredBuecherListenSQL);
        hash = 79 * hash + Objects.hashCode(this.filteredAusgabenListenSQL);
        hash = 79 * hash + Objects.hashCode(this.filteredOrdnungListenSQL);
        hash = 79 * hash + Objects.hashCode(this.buecherkategorien);
        hash = 79 * hash + Objects.hashCode(this.buecherzustaende);
        hash = 79 * hash + Objects.hashCode(this.werteSQL);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BenutzerdefiniertController other = (BenutzerdefiniertController) obj;
        if (this.isAusgaben != other.isAusgaben) {
            return false;
        }
        if (this.isBuecher != other.isBuecher) {
            return false;
        }
        if (this.isOrdnung != other.isOrdnung) {
            return false;
        }
        if (!Objects.equals(this.listenSQL, other.listenSQL)) {
            return false;
        }
        if (!Objects.equals(this.werteSQL, other.werteSQL)) {
            return false;
        }
        if (!Objects.equals(this.benutzer, other.benutzer)) {
            return false;
        }
        if (!Objects.equals(this.buecherListenSQL, other.buecherListenSQL)) {
            return false;
        }
        if (!Objects.equals(this.ausgabenListenSQL, other.ausgabenListenSQL)) {
            return false;
        }
        if (!Objects.equals(this.ordnungListenSQL, other.ordnungListenSQL)) {
            return false;
        }
        if (!Objects.equals(this.filteredBuecherListenSQL, other.filteredBuecherListenSQL)) {
            return false;
        }
        if (!Objects.equals(this.filteredAusgabenListenSQL, other.filteredAusgabenListenSQL)) {
            return false;
        }
        if (!Objects.equals(this.filteredOrdnungListenSQL, other.filteredOrdnungListenSQL)) {
            return false;
        }
        if (!Objects.equals(this.buecherkategorien, other.buecherkategorien)) {
            return false;
        }
        if (!Objects.equals(this.buecherzustaende, other.buecherzustaende)) {
            return false;
        }
        return true;
    }

    public List<String> getWerteabfragenListe() {
        return werteabfragenListe;
    }

    public void setWerteabfragenListe(List<String> werteabfragenListe) {
        this.werteabfragenListe = werteabfragenListe;
    }

    public List<String> getFilteredWerteabfragenListe() {
        return filteredWerteabfragenListe;
    }

    public void setFilteredWerteabfragenListe(List<String> filteredWerteabfragenListe) {
        this.filteredWerteabfragenListe = filteredWerteabfragenListe;
    }

}
