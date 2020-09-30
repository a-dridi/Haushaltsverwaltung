/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.controller;

import haushaltsverwaltung.db.DAO;
import haushaltsverwaltung.db.HibernateUtil;
import haushaltsverwaltung.model.DatenbankNotizen;
import haushaltsverwaltung.model.Reserven;
import haushaltsverwaltung.model.ReservenKategorie;
import haushaltsverwaltung.model.ReservenWaehrung;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.poi.util.IOUtils;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.jackrabbit.webdav.client.methods.DeleteMethod;
import org.apache.jackrabbit.webdav.client.methods.PutMethod;
import org.hibernate.Query;
import org.hibernate.Session;
import org.primefaces.PrimeFaces;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;

/**
 *
 * Currency and asset reservers - Vermögensreserven
 *
 * @author A.Dridi
 */
@Named(value = "reservenController")
@ViewScoped
public class ReservenController implements Serializable {

    private ReservenKategorie reservenkategorie;
    private ReservenWaehrung reservenwaehrung;
    private List<ReservenKategorie> reservenkategorien = new ArrayList<>();
    private List<ReservenWaehrung> reservenwaehrungen = new ArrayList<>();
    private List<Reserven> reservenListenSQL = new ArrayList<>();
    private List<Reserven> filteredReservenListenSQL;
    //Cache Liste um gelöschte Datensätze rückgängig zu machen (nur innerhalb einer Session)
    private List<Reserven> deletedReservenListenSQL = new ArrayList<>();

    private String datensaetzeAnzahlText;

    //ExportColumns - WICHTIG ANZAHL AN SPALTENANZAHL ANPASSEN!!!:
    private List<Boolean> columnList = Arrays.asList(true, true, true, true, true, true, true, true, true);
    private String tabellenname = "Reserven";

    //immer Ändern - OHNE / (SLASH) AM ENDE:
    private String baseUrl = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/dav/files/haushaltsverwaltung/Reserven";
    private String downloadUrl = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/webdav/Reserven";
    private final String cloudUsername = "CLOUDUSERNAME";
    private final String cloudPassword = "CLOUDPASSWORD";

    private String beschreibung;
    private Double betrag;
    private String lagerort;
    private String deleteID;
    private String anhangID;

    private Date datum;
    private String bemerkungen;
    private byte[] anhang;
    private DatenbankNotizen dbnotizEintrag = null;
    private String notiztext;

    private DAO dao;
    private String neuReservenKategorie;
    private String neuReservenWaehrung;
    private String datumEingabe;

    private String change_Kategorie;
    private String change_waehrung;
    private ReservenKategorie deleteReservenKategorie;
    private ReservenWaehrung deleteReservenWaehrung;

    private Integer rownumbers = 15;
    private Integer insert_rownumber;
    private String anhangname;
    private String anhangtype;

    private Double geldEuroSumme;
    private Double geldUSDSumme;
    private Double geldCHFSumme;

    private Double unzeSumme;

    private String datumNotiztext;

    /**
     * Creates a new instance of AusgabenController
     */
    public ReservenController() {
        this.dao = new DAO();

    }

    @PostConstruct
    private void init() {
        List<DatenbankNotizen> notizList = dao.getDatenbankNotiz(this.tabellenname);
        if (notizList != null && !notizList.isEmpty()) {
            this.notiztext = notizList.get(0).getNotiztext();
            this.dbnotizEintrag = notizList.get(0);
            this.datumNotiztext = "Zuletzt akualisiert: " + notizList.get(0).getDatum();

        }

        this.reservenListenSQL = dao.getAllReserven();
        this.filteredReservenListenSQL = new ArrayList<>(dao.getAllReserven());

        this.reservenkategorien = dao.getAllReservenKategorie();
        this.reservenwaehrungen = dao.getAllReservenWaehrung();

        /*
        HttpClient client = new HttpClient();
        Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
        client.getState().setCredentials(AuthScope.ANY, creds);
        GetMethod method = new GetMethod(this.downloadUrl);
        try {
            client.executeMethod(method);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Verb. mit Cloud: ", "" + e));
        }*/
        //Aufgerufene Tabellenwebseite überprüfen
        flushAnhang();
        calculateAllGeldUnzeSumme();
        this.datensaetzeAnzahlText = ("Insgesamt: " + this.reservenListenSQL.size() + " Datensaetze in der DB gespeichert");

    }

    public void calculateAllGeldUnzeSumme() {
        this.geldEuroSumme = 0.0;
        this.geldUSDSumme = 0.0;
        this.geldCHFSumme = 0.0;

        this.unzeSumme = 0.0;

        //Einzelne Summen von Waehrungen (Euro,USD,CHF) und Unze (Edelmetalle) berechnen
        Session s = HibernateUtil.getSessionFactory().openSession();
        String sqlstring = "Select sum(betrag) FROM Reserven where waehrung='Euro' and deleted=false";
        Query qu = s.createQuery(sqlstring);
        List<Double> waehrunggruppe = qu.list();

        if (waehrunggruppe.get(0) != null) {
            this.geldEuroSumme = waehrunggruppe.get(0);
        }

        sqlstring = "Select sum(betrag) FROM Reserven where waehrung='USD' and deleted=false";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();

        if (waehrunggruppe.get(0) != null) {
            this.geldUSDSumme = waehrunggruppe.get(0);
        }

        sqlstring = "Select sum(betrag) FROM Reserven where waehrung='CHF' and deleted=false";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();

        if (waehrunggruppe.get(0) != null) {
            this.geldCHFSumme = waehrunggruppe.get(0);
        }

        sqlstring = "Select sum(betrag) FROM Reserven where waehrung='Unze' and deleted=false";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();

        if (waehrunggruppe.get(0) != null) {
            this.unzeSumme = waehrunggruppe.get(0);
        }

        s.close();

    }

    /**
     * Anhang bearbeiten: Aber bei Übergabe eines leeren Anhangs wird der Anhang
     * für die betroffene Zeile gelöscht
     */
    public void editAnhang() {
        try {
            int zeilenID = Integer.parseInt(this.anhangID);
            boolean id_existiert = false;
            List<Reserven> liste = new ArrayList<>(this.reservenListenSQL);
            gefunden:
            for (Reserven a : liste) {
                if (a.getReserven_id().equals(zeilenID)) {
                    Integer extPos = this.anhangname.lastIndexOf(".");
                    String dateiext = this.anhangname.substring(extPos + 1);
                    HttpClient client = new HttpClient();

                    Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    if (this.anhang != null) {
                        a.setAnhang(true);
                        a.setAnhangname((a.getReserven_id()) + "." + dateiext);
                        a.setAnhangtype(this.anhangtype);
                        a.setAnhangpfad(this.downloadUrl + "/" + ((a.getReserven_id()) + "." + dateiext));

                        InputStream ins = new ByteArrayInputStream(this.anhang);
                        PutMethod method = new PutMethod(this.baseUrl + "/" + ((a.getReserven_id()) + "." + dateiext));
                        RequestEntity requestEntity = new InputStreamRequestEntity(ins);
                        method.setRequestEntity(requestEntity);
                        client.executeMethod(method);
                        System.out.println(method.getStatusCode() + " " + method.getStatusText());
                        dao.updateReserven(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getReserven_id() + " wurde aktualisiert ", " "));
                    } else {
                        //Anhang loeschen und nicht ersetzen
                        DeleteMethod m = new DeleteMethod(this.baseUrl + "/" + ((a.getReserven_id()) + "." + dateiext));
                        client.executeMethod(m);
                        a.setAnhang(false);
                        a.setAnhangname("");
                        a.setAnhangtype("");
                        a.setAnhangpfad("");
                        dao.updateReserven(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getReserven_id() + " wurde gelöscht ", "Die phys. Datei muss dann manuell auf der Cloud von Ihnen gelöscht werden"));
                    }
                    id_existiert = true;
                    flushAnhang();
                    this.anhangID = "";
                    break gefunden;
                }
            }
            if (!id_existiert) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ID existiert nicht ", "Bitte geben Sie eine existierende ID eines Datensatzes ein"));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Keine ID übergeben ", "" + e));

        }
    }

    /**
     * Editor für Zeile aufrufen
     */
    public void editRow(CellEditEvent event) {
        try {
            DataTable tabelle = (DataTable) event.getSource();
            String spaltenname = event.getColumn().getHeaderText();
            this.dao = new DAO();

            //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tabelle Rowkey: ", "" + tabelle.getRowKey()));
            //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Rowindex: ", "" + event.getRowIndex()));
            Reserven a = this.dao.getSingleReserven((Integer) tabelle.getRowKey()).get(0);
            //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "zeile rowindex: ", "" + event.getNewValue()));

            if (spaltenname.equals("Kategorie")) {
                String auswahl = (String) event.getNewValue();
                gefunden:
                for (ReservenKategorie m : this.reservenkategorien) {
                    if (m.getKategoriebezeichnung().equals(auswahl)) {
                        a.setKategorie((String) event.getNewValue());
                        break gefunden;
                    }
                }
            }

            if (spaltenname.equals("Beschreibung")) {
                a.setBeschreibung((String) event.getNewValue());
            }
            if (spaltenname.equals("Betrag")) {
                a.setBetrag((Double) event.getNewValue());
            }

            if (spaltenname.equals("Waehrung")) {
                String auswahl = (String) event.getNewValue();
                gefunden:
                for (ReservenWaehrung m : this.reservenwaehrungen) {
                    if (m.getWaehrungsname().equals(auswahl)) {
                        a.setWaehrung((String) event.getNewValue());
                        break gefunden;
                    }
                }
            }

            if (spaltenname.equals("Lagerort")) {
                a.setLagerort((String) event.getNewValue());
            }

            if (spaltenname.equals("Bemerkungen")) {
                a.setBemerkungen((String) event.getNewValue());
            }

            if (spaltenname.equals("Datum")) {
                if (event.getNewValue() != null) {
                    a.setDatum((Date) event.getNewValue());
                }
            }

            // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "old value: ", "" + event.getNewValue()));
            dao.updateReserven(a);
            updateData();
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde aktualisiert", ""));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", e.toString()));
        }
    }

    public void updateData() {
        this.dao = new DAO();
        HttpClient client = new HttpClient();
        Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
        client.getState().setCredentials(AuthScope.ANY, creds);
        GetMethod method = new GetMethod(this.downloadUrl);
        try {
            client.executeMethod(method);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Verb. mit Cloud: ", "" + e));
        }
        flushAnhang();
        calculateAllGeldUnzeSumme();
        this.reservenkategorien = dao.getAllReservenKategorie();
        this.reservenwaehrungen = dao.getAllReservenWaehrung();
        this.reservenListenSQL = dao.getAllReserven();
        this.filteredReservenListenSQL = new ArrayList<>(dao.getAllReserven());

        this.datensaetzeAnzahlText = ("Insgesamt: " + this.reservenListenSQL.size() + " Datensaetze in der DB gespeichert");

    }

    public void change_rownumber() {
        flushAnhang();

        FacesContext context = FacesContext.getCurrentInstance();
        Map valueMap = context.getExternalContext().getRequestParameterMap();
        this.rownumbers = Integer.parseInt((String) valueMap.get("newRowsAmount"));
    }

    public void anhangSpeichern(FileUploadEvent event) {
        try {
            InputStream input = event.getFile().getInputstream();
            if (input != null) {

                this.anhang = IOUtils.toByteArray(input);
                this.anhangname = event.getFile().getFileName();
                this.anhangtype = event.getFile().getContentType();

            } else {
                this.anhang = null;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", "" + e));
        }

    }

    public void speichernNotiz() {
        List<DatenbankNotizen> notizList = dao.getDatenbankNotiz(this.tabellenname);
        if (notizList != null && !notizList.isEmpty()) {
            this.dbnotizEintrag = notizList.get(0);
        }

        //Notiz-DB Eintrag für diese Tabelle schon zuvor erstellt wurde
        if (this.dbnotizEintrag != null) {
            //Notiz-Eintrag aktualisieren
            this.dbnotizEintrag.setTabelle(this.tabellenname);
            this.dbnotizEintrag.setDatum(new Date());
            this.dbnotizEintrag.setNotiztext(notiztext);
            this.dao.updateDatenbankNotizen(this.dbnotizEintrag);

        } else {
            //Neuen Notiz-Eintrag erstellen
            DatenbankNotizen n = new DatenbankNotizen();
            n.setTabelle(this.tabellenname);
            n.setDatum(new Date());
            n.setNotiztext(notiztext);
            this.dao.insertDatenbankNotizen(n);
        }
    }

    public void clearNotizen() {

        //Notiz-DB Eintrag für diese Tabelle schon zuvor erstellt wurde
        if (this.notiztext != null) {
            //Notiz-Eintrag als leer speichern
            DatenbankNotizen n = new DatenbankNotizen();
            n.setTabelle(this.tabellenname);
            n.setDatum(new Date());
            n.setNotiztext("");
            this.dao.updateDatenbankNotizen(n);
        } else {
            //Neuen Notiz-Eintrag erstellen und als leer speichern
            DatenbankNotizen n = new DatenbankNotizen();
            n.setTabelle(this.tabellenname);
            n.setDatum(new Date());
            n.setNotiztext("");
            this.dao.insertDatenbankNotizen(n);
        }
    }

    public void speichern() {
        Reserven reserve = new Reserven();
        reserve.setDeleted(false);
        if (this.beschreibung != null) {
            reserve.setBeschreibung(beschreibung);
        }

        reserve.setBetrag(betrag);

        if (this.reservenkategorie != null) {
            reserve.setKategorie(this.reservenkategorie.getKategoriebezeichnung());
        }

        if (this.reservenwaehrung != null) {
            reserve.setWaehrung(this.reservenwaehrung.getWaehrungsname());
        }

        if (this.lagerort != null) {
            reserve.setLagerort(this.lagerort);
        }

        if (this.datum != null) {
            reserve.setDatum(datum);
        } else { //Keine Eingabe durch Benutzer, aktuelles Datum speichern
            reserve.setDatum(new Date());
        }

        if (this.bemerkungen != null) {
            reserve.setBemerkungen(bemerkungen);
        }

        if (this.anhang != null && !this.anhangname.isEmpty()) {
            reserve.setAnhang(true);
            //IMMER VOR DEM INSERT BEFEHL
            //Neuen Datensatz direkt auch in der Tabelle ohne neuladen der Seite anzeigen
            this.reservenListenSQL.add(reserve);
            this.filteredReservenListenSQL.add(reserve);

            dao.insertReserven(reserve);

            List<Reserven> reservenListe = new ArrayList<>(this.reservenListenSQL);
            int letzteNr = reservenListe.size() - 1;
            if (letzteNr >= 0) {
                int neueID = reservenListe.get(letzteNr).getReserven_id();
                try {
                    Reserven a = reservenListe.get(letzteNr);
                    HttpClient client = new HttpClient();

                    Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    InputStream ins = new ByteArrayInputStream(this.anhang);
                    Integer extPos = this.anhangname.lastIndexOf(".");
                    String dateiext = this.anhangname.substring(extPos + 1);

                    PutMethod method = new PutMethod(this.baseUrl + "/" + (neueID) + "." + dateiext);
                    RequestEntity requestEntity = new InputStreamRequestEntity(ins);
                    method.setRequestEntity(requestEntity);
                    client.executeMethod(method);
                    System.out.println(method.getStatusCode() + " " + method.getStatusText());
                    a.setAnhangpfad(this.downloadUrl + "/" + (neueID) + "." + dateiext);

                    a.setAnhang(true);
                    a.setAnhangname((neueID) + "." + dateiext);
                    a.setAnhangtype(anhangtype);

                    dao.updateReserven(a);

                } catch (HttpException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anhang: Upload Fehler ", "" + ex));
                } catch (Exception ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", "" + ex));
                }
                updateData();

            }

        } else {
            reserve.setAnhang(false);
            this.reservenListenSQL.add(reserve);
            this.filteredReservenListenSQL.add(reserve);

            dao.insertReserven(reserve);
            updateData();
        }
    }

    /**
     * Methode nach dem Speichern
     */
    public void flushAnhang() {
        this.anhang = null;
        this.anhangname = "";
        this.anhangtype = "";

    }

    public void kategorieSpeichern() {

        if (!this.neuReservenKategorie.isEmpty()) {
            ReservenKategorie ak = new ReservenKategorie();
            ak.setKategoriebezeichnung(this.neuReservenKategorie);
            dao.insertReservenKategorie(ak);
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateData();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void kategorieLoeschen() {

        if (this.deleteReservenKategorie != null) {

            List<ReservenKategorie> akList = dao.getAllReservenKategorie();
            List<Reserven> reservenList = dao.getAllReserven();
            boolean kategorieExist = false;

            for (ReservenKategorie a : akList) {
                if ((a.getKategoriebezeichnung().toLowerCase()).equals(this.deleteReservenKategorie.getKategoriebezeichnung().toLowerCase())) {
                    dao.deleteReservenKategorie(a);
                    for (Reserven reserve : reservenList) {
                        if ((reserve.getKategorie().toLowerCase()).equals(this.deleteReservenKategorie.getKategoriebezeichnung().toLowerCase())) {
                            this.reservenListenSQL.remove(reserve);
                            this.filteredReservenListenSQL.remove(reserve);
                            reserve.setKategorie(this.change_Kategorie);
                            dao.updateReserven(reserve);
                            this.reservenListenSQL.add(reserve);
                            this.filteredReservenListenSQL.add(reserve);
                        }
                    }
                }
                if ((a.getKategoriebezeichnung().toLowerCase()).equals(this.change_Kategorie.toLowerCase())) {
                    kategorieExist = true;
                }
            }
            if (!kategorieExist) {
                ReservenKategorie neu = new ReservenKategorie();
                neu.setKategoriebezeichnung(this.change_Kategorie);
                dao.insertReservenKategorie(neu);
            }
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateData();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void reservenWaehrungLoeschen() {

        if (this.deleteReservenWaehrung != null) {

            List<ReservenWaehrung> akList = dao.getAllReservenWaehrung();
            List<Reserven> reservenList = dao.getAllReserven();
            boolean kategorieExist = false;
            for (ReservenWaehrung a : akList) {
                if ((a.getWaehrungsname().toLowerCase()).equals(this.deleteReservenWaehrung.getWaehrungsname().toLowerCase())) {

                    dao.deleteReservenWaehrung(a);
                    for (Reserven reserve : reservenList) {
                        if ((reserve.getWaehrung().toLowerCase()).equals(this.deleteReservenWaehrung.getWaehrungsname().toLowerCase())) {
                            this.reservenListenSQL.remove(reserve);
                            this.filteredReservenListenSQL.remove(reserve);
                            reserve.setWaehrung(this.change_waehrung);
                            dao.updateReserven(reserve);
                            this.reservenListenSQL.add(reserve);
                            this.filteredReservenListenSQL.add(reserve);
                        }
                    }

                }
                if ((a.getWaehrungsname().toLowerCase()).equals(this.change_waehrung.toLowerCase())) {
                    kategorieExist = true;
                }
            }
            if (!kategorieExist) {
                ReservenWaehrung neu = new ReservenWaehrung();
                neu.setWaehrungsname(this.change_waehrung);
                dao.insertReservenWaehrung(neu);
            }

            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));
            updateData();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void reservenWaehrungspeichern() {

        if (!this.neuReservenWaehrung.isEmpty()) {
            ReservenWaehrung aaz = new ReservenWaehrung();
            aaz.setWaehrungsname(neuReservenWaehrung);
            dao.insertReservenWaehrung(aaz);
            updateData();
            //        throw new RuntimeException("DEBUG Zeitraum: : " + this.neuAusgabenausgabezeitraum);

        }
    }

    public void datensatzLoeschenRueckgangigMachen() {

        if (!this.deletedReservenListenSQL.isEmpty()) {
            for (Reserven a : this.deletedReservenListenSQL) {
                this.reservenListenSQL.add(a);
                this.filteredReservenListenSQL.add(a);
                a.setDeleted(false);
                dao.updateReserven(a);
            }
            updateData();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Gelöschte Datensätze wurden wiederhergestellt", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: Cache ist leer!", "Bitte manuell den Wert der Spalte delete auf false ändern!"));

        }
        //  return "ausgaben.xhtml";

    }

    public void datensatzLoeschen() {
        try {
            if (this.deleteID != null) {
                gefunden:
                for (Reserven a : this.reservenListenSQL) {
                    if (a.getReserven_id().equals(Integer.parseInt(this.deleteID))) {
                        this.deletedReservenListenSQL.add(a);
                        dao.deleteReserven(a);
                        this.reservenListenSQL.remove(a);
                        this.filteredReservenListenSQL.remove(a);
                        break gefunden;
                    }
                }
                updateData();
                this.deleteID = "";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }
        //     return "ausgaben.xhtml";

    }

    public void datensatzLoeschen(Integer id) {
        try {
            if (id != null) {
                gefunden:
                for (Reserven a : this.reservenListenSQL) {
                    if (a.getReserven_id().equals(id)) {
                        this.deletedReservenListenSQL.add(a);
                        dao.deleteReserven(a);
                        this.reservenListenSQL.remove(a);
                        this.filteredReservenListenSQL.remove(a);
                        break gefunden;
                    }
                }
                updateData();
                this.deleteID = "";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }
        //     return "ausgaben.xhtml";

    }

    public void onToggle(ToggleEvent e) {
        this.columnList.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    //BIETE DIESE IMMER ÜBERPRÜFEN:
    public void scrollTop() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("ueberschriftPanel");
        PrimeFaces.current().scrollTo("ueberschriftPanel");

    }

    public void scrollTabelle() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("listenForm:tabelleausgabenPanel");
        PrimeFaces.current().scrollTo("listenForm:tabelleausgabenPanel");

    }

    public void scrollHinzufuegen() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("hinzufuegenForm:neuenDatensatzFormular");
        PrimeFaces.current().scrollTo("hinzufuegenForm:neuenDatensatzFormular");

    }

    public void scrollKategorieAdd() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("kategorieForm:kategorieAddPanel");
        PrimeFaces.current().scrollTo("kategorieForm:kategorieAddPanel");
    }

    public void scrollKategorieDel() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("del_kategorieForm:kategorieDelPanel");
        PrimeFaces.current().scrollTo("del_kategorieForm:kategorieDelPanel");

    }

    public void scrollAnhangEdit() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("neuAnhangForm:AnhangEditPanel");
        PrimeFaces.current().scrollTo("neuAnhangForm:AnhangEditPanel");

    }

    public ReservenKategorie getReservenkategorie() {
        return reservenkategorie;
    }

    public void setReservenkategorie(ReservenKategorie reservenkategorie) {
        this.reservenkategorie = reservenkategorie;
    }

    public ReservenWaehrung getReservenwaehrung() {
        return reservenwaehrung;
    }

    public void setReservenwaehrung(ReservenWaehrung reservenwaehrung) {
        this.reservenwaehrung = reservenwaehrung;
    }

    public List<ReservenKategorie> getReservenkategorien() {
        return reservenkategorien;
    }

    public void setReservenkategorien(List<ReservenKategorie> reservenkategorien) {
        this.reservenkategorien = reservenkategorien;
    }

    public List<ReservenWaehrung> getReservenwaehrungen() {
        return reservenwaehrungen;
    }

    public void setReservenwaehrungen(List<ReservenWaehrung> reservenwaehrungen) {
        this.reservenwaehrungen = reservenwaehrungen;
    }

    public List<Reserven> getReservenListenSQL() {
        return reservenListenSQL;
    }

    public void setReservenListenSQL(List<Reserven> reservenListenSQL) {
        this.reservenListenSQL = reservenListenSQL;
    }

    public List<Reserven> getFilteredReservenListenSQL() {
        return filteredReservenListenSQL;
    }

    public void setFilteredReservenListenSQL(List<Reserven> filteredReservenListenSQL) {
        this.filteredReservenListenSQL = filteredReservenListenSQL;
    }

    public List<Reserven> getDeletedReservenListenSQL() {
        return deletedReservenListenSQL;
    }

    public void setDeletedReservenListenSQL(List<Reserven> deletedReservenListenSQL) {
        this.deletedReservenListenSQL = deletedReservenListenSQL;
    }

    public String getDatensaetzeAnzahlText() {
        return datensaetzeAnzahlText;
    }

    public void setDatensaetzeAnzahlText(String datensaetzeAnzahlText) {
        this.datensaetzeAnzahlText = datensaetzeAnzahlText;
    }

    public List<Boolean> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<Boolean> columnList) {
        this.columnList = columnList;
    }

    public String getTabellenname() {
        return tabellenname;
    }

    public void setTabellenname(String tabellenname) {
        this.tabellenname = tabellenname;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public Double getBetrag() {
        return betrag;
    }

    public void setBetrag(Double betrag) {
        this.betrag = betrag;
    }

    public String getLagerort() {
        return lagerort;
    }

    public void setLagerort(String lagerort) {
        this.lagerort = lagerort;
    }

    public String getDeleteID() {
        return deleteID;
    }

    public void setDeleteID(String deleteID) {
        this.deleteID = deleteID;
    }

    public String getAnhangID() {
        return anhangID;
    }

    public void setAnhangID(String anhangID) {
        this.anhangID = anhangID;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getBemerkungen() {
        return bemerkungen;
    }

    public void setBemerkungen(String bemerkungen) {
        this.bemerkungen = bemerkungen;
    }

    public byte[] getAnhang() {
        return anhang;
    }

    public void setAnhang(byte[] anhang) {
        this.anhang = anhang;
    }

    public DatenbankNotizen getDbnotizEintrag() {
        return dbnotizEintrag;
    }

    public void setDbnotizEintrag(DatenbankNotizen dbnotizEintrag) {
        this.dbnotizEintrag = dbnotizEintrag;
    }

    public String getNotiztext() {
        return notiztext;
    }

    public void setNotiztext(String notiztext) {
        this.notiztext = notiztext;
    }

    public DAO getDao() {
        return dao;
    }

    public void setDao(DAO dao) {
        this.dao = dao;
    }

    public String getNeuReservenKategorie() {
        return neuReservenKategorie;
    }

    public void setNeuReservenKategorie(String neuReservenKategorie) {
        this.neuReservenKategorie = neuReservenKategorie;
    }

    public String getNeuReservenWaehrung() {
        return neuReservenWaehrung;
    }

    public void setNeuReservenWaehrung(String neuReservenWaehrung) {
        this.neuReservenWaehrung = neuReservenWaehrung;
    }

    public String getDatumEingabe() {
        return datumEingabe;
    }

    public void setDatumEingabe(String datumEingabe) {
        this.datumEingabe = datumEingabe;
    }

    public String getChange_Kategorie() {
        return change_Kategorie;
    }

    public void setChange_Kategorie(String change_Kategorie) {
        this.change_Kategorie = change_Kategorie;
    }

    public String getChange_waehrung() {
        return change_waehrung;
    }

    public void setChange_waehrung(String change_waehrung) {
        this.change_waehrung = change_waehrung;
    }

    public ReservenKategorie getDeleteReservenKategorie() {
        return deleteReservenKategorie;
    }

    public void setDeleteReservenKategorie(ReservenKategorie deleteReservenKategorie) {
        this.deleteReservenKategorie = deleteReservenKategorie;
    }

    public ReservenWaehrung getDeleteReservenWaehrung() {
        return deleteReservenWaehrung;
    }

    public void setDeleteReservenWaehrung(ReservenWaehrung deleteReservenWaehrung) {
        this.deleteReservenWaehrung = deleteReservenWaehrung;
    }

    public Integer getRownumbers() {
        return rownumbers;
    }

    public void setRownumbers(Integer rownumbers) {
        this.rownumbers = rownumbers;
    }

    public Integer getInsert_rownumber() {
        return insert_rownumber;
    }

    public void setInsert_rownumber(Integer insert_rownumber) {
        this.insert_rownumber = insert_rownumber;
    }

    public String getAnhangname() {
        return anhangname;
    }

    public void setAnhangname(String anhangname) {
        this.anhangname = anhangname;
    }

    public String getAnhangtype() {
        return anhangtype;
    }

    public void setAnhangtype(String anhangtype) {
        this.anhangtype = anhangtype;
    }

    public Double getGeldEuroSumme() {
        return geldEuroSumme;
    }

    public void setGeldEuroSumme(Double geldEuroSumme) {
        this.geldEuroSumme = geldEuroSumme;
    }

    public Double getGeldUSDSumme() {
        return geldUSDSumme;
    }

    public void setGeldUSDSumme(Double geldUSDSumme) {
        this.geldUSDSumme = geldUSDSumme;
    }

    public Double getGeldCHFSumme() {
        return geldCHFSumme;
    }

    public void setGeldCHFSumme(Double geldCHFSumme) {
        this.geldCHFSumme = geldCHFSumme;
    }

    public Double getUnzeSumme() {
        return unzeSumme;
    }

    public void setUnzeSumme(Double unzeSumme) {
        this.unzeSumme = unzeSumme;
    }

    public String getDatumNotiztext() {
        return datumNotiztext;
    }

    public void setDatumNotiztext(String datumNotiztext) {
        this.datumNotiztext = datumNotiztext;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.reservenkategorie);
        hash = 59 * hash + Objects.hashCode(this.reservenwaehrung);
        hash = 59 * hash + Objects.hashCode(this.reservenkategorien);
        hash = 59 * hash + Objects.hashCode(this.reservenwaehrungen);
        hash = 59 * hash + Objects.hashCode(this.reservenListenSQL);
        hash = 59 * hash + Objects.hashCode(this.filteredReservenListenSQL);
        hash = 59 * hash + Objects.hashCode(this.deletedReservenListenSQL);
        hash = 59 * hash + Objects.hashCode(this.datensaetzeAnzahlText);
        hash = 59 * hash + Objects.hashCode(this.columnList);
        hash = 59 * hash + Objects.hashCode(this.tabellenname);
        hash = 59 * hash + Objects.hashCode(this.baseUrl);
        hash = 59 * hash + Objects.hashCode(this.downloadUrl);
        hash = 59 * hash + Objects.hashCode(this.beschreibung);
        hash = 59 * hash + Objects.hashCode(this.betrag);
        hash = 59 * hash + Objects.hashCode(this.lagerort);
        hash = 59 * hash + Objects.hashCode(this.deleteID);
        hash = 59 * hash + Objects.hashCode(this.anhangID);
        hash = 59 * hash + Objects.hashCode(this.datum);
        hash = 59 * hash + Objects.hashCode(this.bemerkungen);
        hash = 59 * hash + Arrays.hashCode(this.anhang);
        hash = 59 * hash + Objects.hashCode(this.dbnotizEintrag);
        hash = 59 * hash + Objects.hashCode(this.notiztext);
        hash = 59 * hash + Objects.hashCode(this.dao);
        hash = 59 * hash + Objects.hashCode(this.neuReservenKategorie);
        hash = 59 * hash + Objects.hashCode(this.neuReservenWaehrung);
        hash = 59 * hash + Objects.hashCode(this.datumEingabe);
        hash = 59 * hash + Objects.hashCode(this.change_Kategorie);
        hash = 59 * hash + Objects.hashCode(this.change_waehrung);
        hash = 59 * hash + Objects.hashCode(this.deleteReservenKategorie);
        hash = 59 * hash + Objects.hashCode(this.deleteReservenWaehrung);
        hash = 59 * hash + Objects.hashCode(this.rownumbers);
        hash = 59 * hash + Objects.hashCode(this.insert_rownumber);
        hash = 59 * hash + Objects.hashCode(this.anhangname);
        hash = 59 * hash + Objects.hashCode(this.anhangtype);
        hash = 59 * hash + Objects.hashCode(this.geldEuroSumme);
        hash = 59 * hash + Objects.hashCode(this.geldUSDSumme);
        hash = 59 * hash + Objects.hashCode(this.geldCHFSumme);
        hash = 59 * hash + Objects.hashCode(this.unzeSumme);
        hash = 59 * hash + Objects.hashCode(this.datumNotiztext);
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
        final ReservenController other = (ReservenController) obj;
        if (!Objects.equals(this.datensaetzeAnzahlText, other.datensaetzeAnzahlText)) {
            return false;
        }
        if (!Objects.equals(this.tabellenname, other.tabellenname)) {
            return false;
        }
        if (!Objects.equals(this.baseUrl, other.baseUrl)) {
            return false;
        }
        if (!Objects.equals(this.downloadUrl, other.downloadUrl)) {
            return false;
        }
        if (!Objects.equals(this.beschreibung, other.beschreibung)) {
            return false;
        }
        if (!Objects.equals(this.lagerort, other.lagerort)) {
            return false;
        }
        if (!Objects.equals(this.deleteID, other.deleteID)) {
            return false;
        }
        if (!Objects.equals(this.anhangID, other.anhangID)) {
            return false;
        }
        if (!Objects.equals(this.bemerkungen, other.bemerkungen)) {
            return false;
        }
        if (!Objects.equals(this.notiztext, other.notiztext)) {
            return false;
        }
        if (!Objects.equals(this.neuReservenKategorie, other.neuReservenKategorie)) {
            return false;
        }
        if (!Objects.equals(this.neuReservenWaehrung, other.neuReservenWaehrung)) {
            return false;
        }
        if (!Objects.equals(this.datumEingabe, other.datumEingabe)) {
            return false;
        }
        if (!Objects.equals(this.change_Kategorie, other.change_Kategorie)) {
            return false;
        }
        if (!Objects.equals(this.change_waehrung, other.change_waehrung)) {
            return false;
        }
        if (!Objects.equals(this.anhangname, other.anhangname)) {
            return false;
        }
        if (!Objects.equals(this.anhangtype, other.anhangtype)) {
            return false;
        }
        if (!Objects.equals(this.datumNotiztext, other.datumNotiztext)) {
            return false;
        }
        if (!Objects.equals(this.reservenkategorie, other.reservenkategorie)) {
            return false;
        }
        if (!Objects.equals(this.reservenwaehrung, other.reservenwaehrung)) {
            return false;
        }
        if (!Objects.equals(this.reservenkategorien, other.reservenkategorien)) {
            return false;
        }
        if (!Objects.equals(this.reservenwaehrungen, other.reservenwaehrungen)) {
            return false;
        }
        if (!Objects.equals(this.reservenListenSQL, other.reservenListenSQL)) {
            return false;
        }
        if (!Objects.equals(this.filteredReservenListenSQL, other.filteredReservenListenSQL)) {
            return false;
        }
        if (!Objects.equals(this.deletedReservenListenSQL, other.deletedReservenListenSQL)) {
            return false;
        }
        if (!Objects.equals(this.columnList, other.columnList)) {
            return false;
        }
        if (!Objects.equals(this.betrag, other.betrag)) {
            return false;
        }
        if (!Objects.equals(this.datum, other.datum)) {
            return false;
        }
        if (!Arrays.equals(this.anhang, other.anhang)) {
            return false;
        }
        if (!Objects.equals(this.dbnotizEintrag, other.dbnotizEintrag)) {
            return false;
        }
        if (!Objects.equals(this.dao, other.dao)) {
            return false;
        }
        if (!Objects.equals(this.deleteReservenKategorie, other.deleteReservenKategorie)) {
            return false;
        }
        if (!Objects.equals(this.deleteReservenWaehrung, other.deleteReservenWaehrung)) {
            return false;
        }
        if (!Objects.equals(this.rownumbers, other.rownumbers)) {
            return false;
        }
        if (!Objects.equals(this.insert_rownumber, other.insert_rownumber)) {
            return false;
        }
        if (!Objects.equals(this.geldEuroSumme, other.geldEuroSumme)) {
            return false;
        }
        if (!Objects.equals(this.geldUSDSumme, other.geldUSDSumme)) {
            return false;
        }
        if (!Objects.equals(this.geldCHFSumme, other.geldCHFSumme)) {
            return false;
        }
        if (!Objects.equals(this.unzeSumme, other.unzeSumme)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ReservenController{" + "reservenkategorie=" + reservenkategorie + ", reservenwaehrung=" + reservenwaehrung + ", reservenkategorien=" + reservenkategorien + ", reservenwaehrungen=" + reservenwaehrungen + ", reservenListenSQL=" + reservenListenSQL + ", filteredReservenListenSQL=" + filteredReservenListenSQL + ", deletedReservenListenSQL=" + deletedReservenListenSQL + ", datensaetzeAnzahlText=" + datensaetzeAnzahlText + ", columnList=" + columnList + ", tabellenname=" + tabellenname + ", baseUrl=" + baseUrl + ", downloadUrl=" + downloadUrl + ", beschreibung=" + beschreibung + ", betrag=" + betrag + ", lagerort=" + lagerort + ", deleteID=" + deleteID + ", anhangID=" + anhangID + ", datum=" + datum + ", bemerkungen=" + bemerkungen + ", anhang=" + anhang + ", dbnotizEintrag=" + dbnotizEintrag + ", notiztext=" + notiztext + ", dao=" + dao + ", neuReservenKategorie=" + neuReservenKategorie + ", neuReservenWaehrung=" + neuReservenWaehrung + ", datumEingabe=" + datumEingabe + ", change_Kategorie=" + change_Kategorie + ", change_waehrung=" + change_waehrung + ", deleteReservenKategorie=" + deleteReservenKategorie + ", deleteReservenWaehrung=" + deleteReservenWaehrung + ", rownumbers=" + rownumbers + ", insert_rownumber=" + insert_rownumber + ", anhangname=" + anhangname + ", anhangtype=" + anhangtype + ", geldEuroSumme=" + geldEuroSumme + ", geldUSDSumme=" + geldUSDSumme + ", geldCHFSumme=" + geldCHFSumme + ", unzeSumme=" + unzeSumme + ", datumNotiztext=" + datumNotiztext + '}';
    }

}
