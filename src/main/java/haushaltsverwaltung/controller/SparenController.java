/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.controller;

import haushaltsverwaltung.db.DAO;
import haushaltsverwaltung.model.Sparen;
import haushaltsverwaltung.model.DatenbankNotizen;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.inject.Named;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import org.primefaces.PrimeFaces;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;

/**
 *
 * Savings accounts - Sparkonten
 *
 * @author A.Dridi
 */
@Named(value = "sparenController")
@ViewScoped
public class SparenController implements Serializable {

    private List<Sparen> sparenList = new ArrayList<>();
    private List<Sparen> filteredSparenList;
    //Cache Liste um gelöschte Datensätze rückgängig zu machen (nur innerhalb einer Session)
    private List<Sparen> deletedSparenList = new ArrayList<>();

    private List<String> haeufigkeitList = new ArrayList<>();
    private String tabellenname = "Sparen";

    //immer Ändern - OHNE / (SLASH) AM ENDE:
    private String baseUrl = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/dav/files/haushaltsverwaltung/Sparen";
    private String downloadUrl = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/webdav/Sparen";
    private final String cloudUsername = "CLOUDUSERNAME";
    private final String cloudPassword = "CLOUDPASSWORD";

    //ExportColumns - WICHTIG ANZAHL AN SPALTENANZAHL ANPASSEN (ausgenommen Anhang/D Spalte)!!!:
    private List<Boolean> columnList = Arrays.asList(true, true, true, true, true, true, true, true, true);

    private String bezeichnung;
    private Double schrittbetrag;
    private Double sparzielbetrag;
    private String bemerkungen;
    private String sparenHaeufigkeit;

    private DAO dao;
    private Integer rownumbers = 15;
    private Integer insert_rownumber;
    private String anhangname;
    private String anhangtype;
    private byte[] anhang;
    private DatenbankNotizen dbnotizEintrag = null;
    private String notiztext;
    private String datensaetzeAnzahlText;
    private String deleteID;
    private String anhangID;

    public SparenController() {
        this.dao = new DAO();
    }

    @PostConstruct
    private void init() {

        List<DatenbankNotizen> notizList = dao.getDatenbankNotiz(this.tabellenname);
        if (notizList != null && !notizList.isEmpty()) {
            this.notiztext = notizList.get(0).getNotiztext();
            this.dbnotizEintrag = notizList.get(0);
        }

        this.sparenList = dao.getAllSparen();
        this.filteredSparenList = new ArrayList<>(this.sparenList);
        //this.haeufigkeitList = dao.getSparenHaeufigkeit();
        this.haeufigkeitList.add("individuell");
        this.haeufigkeitList.add("täglich");
        this.haeufigkeitList.add("monatlich");
        this.haeufigkeitList.add("jährlich");

        flushAnhang();
        this.datensaetzeAnzahlText = ("Insgesamt: " + this.sparenList.size() + " Sparkonto/Sparkonten erstellt");
    }

    /**
     * Methode nach dem Speichern
     */
    public void flushAnhang() {
        this.anhang = null;
        this.anhangname = "";
        this.anhangtype = "";

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
        this.sparenList = dao.getAllSparen();
        this.filteredSparenList = new ArrayList<>(this.sparenList);
        flushAnhang();

        this.datensaetzeAnzahlText = ("Insgesamt: " + this.sparenList.size() + " Sparkonto(s) erstellt");
    }

    /**
     * Anhang bearbeiten: Aber bei Übergabe eines leeren Anhangs wird der Anhang
     * für die betroffene Zeile gelöscht
     */
    public void editAnhang() {
        try {
            int zeilenID = Integer.parseInt(this.anhangID);
            boolean id_existiert = false;
            List<Sparen> liste = new ArrayList<>(this.sparenList);
            gefunden:
            for (Sparen a : liste) {
                if (a.getId().equals(zeilenID)) {
                    Integer extPos = this.anhangname.lastIndexOf(".");
                    String dateiext = this.anhangname.substring(extPos + 1);
                    HttpClient client = new HttpClient();

                    Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    if (this.anhang != null) {
                        a.setAnhang(true);
                        a.setAnhangname((a.getId()) + "." + dateiext);
                        a.setAnhangtype(this.anhangtype);
                        a.setAnhangpfad(this.downloadUrl + "/" + ((a.getId()) + "." + dateiext));

                        InputStream ins = new ByteArrayInputStream(this.anhang);
                        PutMethod method = new PutMethod(this.baseUrl + "/" + ((a.getId()) + "." + dateiext));
                        RequestEntity requestEntity = new InputStreamRequestEntity(ins);
                        method.setRequestEntity(requestEntity);
                        client.executeMethod(method);
                        System.out.println(method.getStatusCode() + " " + method.getStatusText());
                        dao.updateSparen(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getId() + " wurde aktualisiert ", " "));
                    } else {
                        //Anhang loeschen und nicht ersetzen
                        DeleteMethod m = new DeleteMethod(this.baseUrl + "/" + ((a.getId()) + "." + dateiext));
                        client.executeMethod(m);
                        a.setAnhang(false);
                        a.setAnhangname("");
                        a.setAnhangtype("");
                        a.setAnhangpfad("");
                        dao.updateSparen(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getId() + " wurde gelöscht ", "Die phys. Datei muss dann manuell auf der Cloud von Ihnen gelöscht werden"));
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

            Sparen a = (this.dao.getSingleSparen((Integer) tabelle.getRowKey())).get(0);
            //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "zeile rowindex: ", "" + event.getNewValue()));
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            Date d = new Date();

            if (spaltenname.equals("Bezeichnung")) {
                if (event.getNewValue() != null) {
                    a.setBezeichnung((String) event.getNewValue());
                    FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Die Bezeichnung von dem Sparkonto wurde aktualisiert.", ""));
                    dao.updateSparen(a);
                    updateData();
                }
            }

            if (spaltenname.equals("SCHRITT-BETRAG")) {
                if (event.getNewValue() != null) {
                    a.setSchrittbetrag((Double) event.getNewValue());

                    if (a.getSparzielbetrag() <= 0.0) {
                        //Ohne Sparziel

                        if (a.getEinsparhaeufigkeit().equals("individuell")) {
                            a.setLetzterteildatum(d);
                            a.setLetzterteilbetrag((Double) event.getNewValue() + a.getLetzterteilbetrag());

                            //Sparziel Info erstellen 
                            a.setSparzielinfo("Bis jetzt angespart: " + (a.getLetzterteilbetrag()) + "e)");
                            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Neues Geld zu diesem Sparkonto hinzugefügt. BITTE laden Sie diese Seite neu!", ""));
                            dao.updateSparen(a);
                            updateData();
                        } else {
                            //a.setLetzterteildatum(d);

                            //Sparziel Info erstellen 
                            a.setSparzielinfo("Bis jetzt angespart: " + (a.getLetzterteilbetrag()) + "e)");
                            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Ab den nächsten Zyklus (nächster Monat, Jahr, etc.) wird der neue regemäßige Sparbetrag (Sparschritt-Betrag) angewandt.", ""));
                            dao.updateSparen(a);
                            updateData();
                        }

                    } else {
                        //Mit Sparziel

                        if (a.getEinsparhaeufigkeit().equals("individuell")) {
                            //Anzahl der Jahre, Monate und Tage zum Sparziel, wenn der jetzt hinzugefuegte Sparbetrag monatlich gespart wird.
                            double tagemonate = 0.0;
                            //Wieviel man diesen Teilbetrag sparen muss, um auf das Sparziel zu erreichen. Ergebnis entspricht die Anzahl der Monate (Ganzzahliger Wert) und Anzahl der Tagen (Wert in den Nachkommastellen)
                            tagemonate = ((a.getSparzielbetrag() - (a.getLetzterteilbetrag())) / (Double) event.getNewValue());
                            //Überprüfen ob Sparziel erreicht wurde
                            if (tagemonate < 1.0) {
                                //Schrittbetrag ist falsch
                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Spar-Schrittbetrag darf nicht höher sein, als der Sparbetrag des Sparziels", ""));

                            } else if (tagemonate == 1.0) {
                                a.setLetzterteildatum(d);
                                a.setLetzterteilbetrag((Double) event.getNewValue() + a.getLetzterteilbetrag());
                                //Sparziel erreicht
                                a.setSparzielinfo("OK - Fertig gespart am " + sdf.format(a.getLetzterteildatum()));
                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Neues Geld zu diesem Sparkonto hinzugefügt. Glückwunsch! Das Sparziel wurde erreicht!", ""));
                                dao.updateSparen(a);
                                updateData();
                            } else {
                                a.setLetzterteildatum(d);
                                a.setLetzterteilbetrag((Double) event.getNewValue() + a.getLetzterteilbetrag());
                                int monate = (int) tagemonate;
                                //Tageanteil ist ein Prozentsatz der für die Berechnung der Anzahl von Tagen eines Monats verwendet wird
                                double tageAnteil = Double.parseDouble(String.format("%.2f", tagemonate));
                                tageAnteil = ((tageAnteil * 100) % 100) / 100;
                                //Ungefähr. Taganteil
                                int tage = 28 * (int) tageAnteil;
                                //Monate und Tage (bis noch zum Sparziel) hinzufügen, Jahre werden auto. hinzugefügt
                                LocalDate sparzielDatum = LocalDate.now().plusMonths(monate);
                                if (tage > 0) {
                                    sparzielDatum = sparzielDatum.plusDays(tage);
                                }

                                //Sparziel Info erstellen 
                                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                                a.setSparzielinfo("Noch " + (a.getSparzielbetrag() - a.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df) + " (bei monatl. Sparen von " + (Double) event.getNewValue() + "€)");
                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Neues Geld zu diesem Sparkonto hinzugefügt. BITTE laden Sie diese Seite neu!", ""));
                                dao.updateSparen(a);
                                updateData();
                            }
                        } else if (a.getEinsparhaeufigkeit().equals("täglich")) {
                            //Code wie beim Abschnitt mit "individuell" nur auf die Häufigkeit (hier: täglich) angepasst

                            double tage = 0.0;
                            tage = ((a.getSparzielbetrag() - (a.getLetzterteilbetrag())) / (Double) event.getNewValue());

                            //Überprüfen ob Sparziel erreicht wurde
                            if (tage < 1.0) {
                                //Schrittbetrag ist falsch
                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Spar-Schrittbetrag darf nicht höher sein, als der Sparbetrag des Sparziels", ""));

                            } else if (tage == 1.0) {
                                //a.setLetzterteildatum(d);
                                //a.setLetzterteilbetrag((Double) event.getNewValue() + a.getLetzterteilbetrag());

                                //Sparziel erreicht
                                a.setSparzielinfo("OK - Fertig gespart am " + sdf.format(a.getLetzterteildatum()));
                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Neues Geld zu diesem Sparkonto hinzugefügt. Glückwunsch! Das Sparziel wurde erreicht!", ""));
                                dao.updateSparen(a);
                                updateData();
                            } else {
                                //a.setLetzterteildatum(d);

                                //Ungefähr. Taganteil
                                //Tage (bis noch zum Sparziel) hinzufügen, Jahre werden auto. hinzugefügt
                                LocalDate sparzielDatum = LocalDate.now().plusDays((int) tage);

                                //Sparziel Info erstellen 
                                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                                a.setSparzielinfo("Noch " + (a.getSparzielbetrag() - a.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df));

                                a.setLetzterteildatum(d);
                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Ab den nächsten Zyklus (Tag) wird der neue regemäßige Sparbetrag (Sparschritt-Betrag) angewandt.", ""));
                                dao.updateSparen(a);
                                updateData();
                            }
                        } else if (a.getEinsparhaeufigkeit().equals("monatlich")) {
                            //Anzahl der Jahre, Monate und Tage zum Sparziel, wenn der jetzt hinzugefuegte Sparbetrag monatlich gespart wird.
                            double tagemonate = 0.0;
                            //Wieviel man diesen Teilbetrag sparen muss, um auf das Sparziel zu erreichen. Ergebnis entspricht die Anzahl der Monate (Ganzzahliger Wert) und Anzahl der Tagen (Wert in den Nachkommastellen)
                            tagemonate = ((a.getSparzielbetrag() - (a.getLetzterteilbetrag())) / (Double) event.getNewValue());

                            //Überprüfen ob Sparziel erreicht wurde
                            if (tagemonate < 1.0) {
                                //Schrittbetrag ist falsch
                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Spar-Schrittbetrag darf nicht höher sein, als der Sparbetrag des Sparziels", ""));

                            } else if (tagemonate == 1.0) {
                                //a.setLetzterteildatum(d);
                                //a.setLetzterteilbetrag((Double) event.getNewValue() + a.getLetzterteilbetrag());

                                //Sparziel erreicht
                                a.setSparzielinfo("OK - Fertig gespart am " + sdf.format(a.getLetzterteildatum()));
                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Neues Geld zu diesem Sparkonto hinzugefügt. Glückwunsch! Das Sparziel wurde erreicht!", ""));
                                dao.updateSparen(a);
                                updateData();
                            } else {
                                //a.setLetzterteildatum(d);

                                int monate = (int) tagemonate;
                                //Tageanteil ist ein Prozentsatz der für die Berechnung der Anzahl von Tagen eines Monats verwendet wird
                                double tageAnteil = Double.parseDouble(String.format("%.2f", tagemonate));
                                tageAnteil = ((tageAnteil * 100) % 100) / 100;
                                //Ungefähr. Taganteil
                                int tage = 28 * (int) tageAnteil;
                                //Monate und Tage (bis noch zum Sparziel) hinzufügen, Jahre werden auto. hinzugefügt
                                LocalDate sparzielDatum = LocalDate.now().plusMonths(monate);
                                if (tage > 0) {
                                    sparzielDatum = sparzielDatum.plusDays(tage);
                                }

                                //Sparziel Info erstellen 
                                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                                a.setSparzielinfo("Noch " + (a.getSparzielbetrag() - a.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df));

                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Neues Geld zu diesem Sparkonto hinzugefügt", ""));
                                dao.updateSparen(a);
                                updateData();
                            }
                        } else if (a.getEinsparhaeufigkeit().equals("jährlich")) {
                            //Anzahl der Jahre, wenn der jetzt hinzugefuegte Sparbetrag jährlich gespart wird.
                            double jahremonate = 0.0;
                            //Wieviel man diesen Teilbetrag sparen muss, um auf das Sparziel zu erreichen. Ergebnis entspricht die Anzahl der Monate (Ganzzahliger Wert) und Anzahl der Tagen (Wert in den Nachkommastellen)
                            jahremonate = ((a.getSparzielbetrag() - (a.getLetzterteilbetrag())) / (Double) event.getNewValue());

                            //Überprüfen ob Sparziel erreicht wurde
                            if (jahremonate < 1.0) {
                                //Schrittbetrag ist falsch
                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Spar-Schrittbetrag darf nicht höher sein, als der Sparbetrag des Sparziels", ""));

                            } else if (jahremonate == 1.0) {
                                //a.setLetzterteildatum(d);
                                //a.setLetzterteilbetrag((Double) event.getNewValue() + a.getLetzterteilbetrag());

                                //Sparziel erreicht
                                a.setSparzielinfo("OK - Fertig gespart am " + sdf.format(a.getLetzterteildatum()));
                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Neues Geld zu diesem Sparkonto hinzugefügt. Glückwunsch! Das Sparziel wurde erreicht!", ""));
                                dao.updateSparen(a);
                                updateData();
                            } else {
                                //a.setLetzterteildatum(d);

                                //Jahre aufrunden, da man nur jährlich bezahlt
                                int jahre = (int) Math.ceil(jahremonate);
                                //Jahre werden . hinzugefügt
                                LocalDate sparzielDatum = LocalDate.now();
                                if (jahre > 0) {
                                    sparzielDatum = sparzielDatum.plusYears(jahre);
                                }

                                //Sparziel Info erstellen 
                                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                                a.setSparzielinfo("Noch " + (a.getSparzielbetrag() - a.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df));

                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Neues Geld zu diesem Sparkonto hinzugefügt. BITTE laden Sie diese Seite neu!", ""));
                                dao.updateSparen(a);
                                updateData();
                            }
                        }
                    }
                }
            } else if (spaltenname.equals("Sparziel")) {

                if (event.getNewValue() != null) {
                    a.setSparzielbetrag((Double) event.getNewValue());

                    if (a.getSparzielbetrag() <= 0.0) {
                        //Ohne Sparziel

                        a.setSparzielinfo("Bis jetzt angespart: " + (a.getLetzterteilbetrag()) + "e)");
                        FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Das Sparziel (der Zielbetrag zum Sparen) wurde geändert. BITTE laden Sie diese Seite neu!", ""));
                        dao.updateSparen(a);
                        updateData();

                    } else {
                        //Mit Sparziel

                        if (a.getEinsparhaeufigkeit().equals("individuell")) {
                            //Anzahl der Jahre, Monate und Tage zum Sparziel, wenn der jetzt hinzugefuegte Sparbetrag monatlich gespart wird.
                            double tagemonate = ((a.getSparzielbetrag() - (a.getLetzterteilbetrag())) / a.getSchrittbetrag());
                            //Überprüfen ob Sparziele erreicht wurde
                            if (tagemonate < 1.0) {
                                //Schrittbetrag ist falsc
                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Spar-Schrittbetrag darf nicht höher sein als der Sparbetrag des Sparziels", ""));

                            } else if (tagemonate == 1.0) {
                                //Sparziel erreicht
                                a.setSparzielinfo("OK - Fertig gespart am " + sdf.format(a.getLetzterteildatum()));
                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Das Sparziel (der Zielbetrag zum Sparen) wurde geändert. BITTE laden Sie diese Seite neu!", ""));
                                dao.updateSparen(a);
                                updateData();
                            } else {
                                int monate = (int) tagemonate;
                                //Tageanteil ist ein Prozentsatz der für die Berechnung der Anzahl von Tagen eines Monats verwendet wird
                                double tageAnteil = Double.parseDouble(String.format("%.2f", tagemonate));
                                tageAnteil = ((tageAnteil * 100) % 100) / 100;
                                //Ungefähr. Taganteil
                                int tage = 28 * (int) tageAnteil;
                                //Monate und Tage (bis noch zum Sparziel) hinzufügen, Jahre werden auto. hinzugefügt
                                LocalDate sparzielDatum = LocalDate.now().plusMonths(monate);
                                sparzielDatum = sparzielDatum.plusDays(tage);

                                //Sparziel Info erstellen 
                                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                                a.setSparzielinfo("Noch " + (a.getSparzielbetrag() - a.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df) + " (bei monatl. Sparen von " + a.getSchrittbetrag() + "€)");
                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Das Sparziel (der Zielbetrag zum Sparen) wurde geändert. BITTE laden Sie diese Seite neu!", ""));
                                dao.updateSparen(a);
                                updateData();
                            }
                        } else if (a.getEinsparhaeufigkeit().equals("täglich")) {
                            //Code wie beim Abschnitt mit "individuell" nur auf die Häufigkeit (hier: täglich) angepasst
                            double tage = ((a.getSparzielbetrag() - (a.getLetzterteilbetrag())) / a.getSchrittbetrag());
                            //Überprüfen ob Sparziel erreicht wurde
                            if (tage == 1.0) {
                                //Sparziel erreicht
                                a.setSparzielinfo("OK - Fertig gespart am " + sdf.format(a.getLetzterteildatum()));
                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Das Sparziel (der Zielbetrag zum Sparen) wurde geändert. BITTE laden Sie diese Seite neu!", ""));
                                dao.updateSparen(a);
                                updateData();
                            } else {
                                //Ungefähr. Taganteil
                                //Tage (bis noch zum Sparziel) hinzufügen, Jahre werden auto. hinzugefügt
                                LocalDate sparzielDatum = LocalDate.now().plusDays((int) tage);
                                //Sparziel Info erstellen 
                                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                                a.setSparzielinfo("Noch " + (a.getSparzielbetrag() - a.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df));
                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Das Sparziel (der Zielbetrag zum Sparen) wurde geändert. BITTE laden Sie diese Seite neu!", ""));
                                dao.updateSparen(a);
                                updateData();
                            }
                        } else if (a.getEinsparhaeufigkeit().equals("monatlich")) {
                            //Anzahl der Jahre, Monate und Tage zum Sparziel, wenn der jetzt hinzugefuegte Sparbetrag monatlich gespart wird.
                            double tagemonate = 0.0;
                            //Wieviel man diesen Teilbetrag sparen muss, um auf das Sparziel zu erreichen. Ergebnis entspricht die Anzahl der Monate (Ganzzahliger Wert) und Anzahl der Tagen (Wert in den Nachkommastellen)
                            tagemonate = ((a.getSparzielbetrag() - (a.getLetzterteilbetrag())) / a.getSchrittbetrag());
                            if (tagemonate == 1.0) {
                                //Sparziel erreicht
                                a.setSparzielinfo("OK - Fertig gespart am " + sdf.format(a.getLetzterteildatum()));
                                dao.updateSparen(a);
                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Das Sparziel (der Zielbetrag zum Sparen) wurde geändert. BITTE laden Sie diese Seite neu!", ""));
                                updateData();
                            } else {
                                int monate = (int) tagemonate;
                                //Tageanteil ist ein Prozentsatz der für die Berechnung der Anzahl von Tagen eines Monats verwendet wird
                                double tageAnteil = Double.parseDouble(String.format("%.2f", tagemonate));
                                tageAnteil = ((tageAnteil * 100) % 100) / 100;
                                //Ungefähr. Taganteil
                                int tage = 28 * (int) tageAnteil;
                                //Monate und Tage (bis noch zum Sparziel) hinzufügen, Jahre werden auto. hinzugefügt
                                LocalDate sparzielDatum = LocalDate.now().plusMonths(monate);
                                if (tage > 0) {
                                    sparzielDatum = sparzielDatum.plusDays(tage);
                                }
                                //Sparziel Info erstellen 
                                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                                a.setSparzielinfo("Noch " + (a.getSparzielbetrag() - a.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df));
                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Das Sparziel (der Zielbetrag zum Sparen) wurde geändert. BITTE laden Sie diese Seite neu!", ""));
                                dao.updateSparen(a);
                                updateData();
                            }

                        } else if (a.getEinsparhaeufigkeit().equals("jährlich")) {
                            //Anzahl der Jahre, wenn der jetzt hinzugefuegte Sparbetrag jährlich gespart wird.
                            double jahremonate = 0.0;
                            //Wieviel man diesen Teilbetrag sparen muss, um auf das Sparziel zu erreichen. Ergebnis entspricht die Anzahl der Monate (Ganzzahliger Wert) und Anzahl der Tagen (Wert in den Nachkommastellen)
                            jahremonate = ((a.getSparzielbetrag() - (a.getLetzterteilbetrag())) / a.getSchrittbetrag());

                            if (jahremonate == 1.0) {
                                //Sparziel erreicht
                                a.setSparzielinfo("OK - Fertig gespart am " + sdf.format(a.getLetzterteildatum()));
                                dao.updateSparen(a);
                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Das Sparziel (der Zielbetrag zum Sparen) wurde geändert. BITTE laden Sie diese Seite neu!", ""));
                                updateData();
                            } else {
                                //Jahre aufrunden, da man nur jährlich bezahlt
                                int jahre = (int) Math.ceil(jahremonate);
                                //Jahre werden . hinzugefügt
                                LocalDate sparzielDatum = LocalDate.now();
                                if (jahre > 0) {
                                    sparzielDatum = sparzielDatum.plusYears(jahre);
                                }
                                //Sparziel Info erstellen 
                                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                                a.setSparzielinfo("Noch " + (a.getSparzielbetrag() - a.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df));

                                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Das Sparziel (der Zielbetrag zum Sparen) wurde geändert. BITTE laden Sie diese Seite neu!", ""));
                                dao.updateSparen(a);
                                updateData();
                            }
                        }
                    }
                }
            } else if (spaltenname.equals("Spar-Häufigkeit")) {
                a.setEinsparhaeufigkeit((String) event.getNewValue());

                if (a.getEinsparhaeufigkeit().equals("individuell")) {
                    //Anzahl der Jahre, Monate und Tage zum Sparziel, wenn der jetzt hinzugefuegte Sparbetrag monatlich gespart wird.
                    double tagemonate = 0.0;
                    tagemonate = ((a.getSparzielbetrag() - (a.getLetzterteilbetrag())) / a.getSchrittbetrag());

                    int monate = (int) tagemonate;
                    //Tageanteil ist ein Prozentsatz der für die Berechnung der Anzahl von Tagen eines Monats verwendet wird
                    double tageAnteil = Double.parseDouble(String.format("%.2f", tagemonate));
                    tageAnteil = ((tageAnteil * 100) % 100) / 100;
                    //Ungefähr. Taganteil
                    int tage = 28 * (int) tageAnteil;
                    //Monate und Tage (bis noch zum Sparziel) hinzufügen, Jahre werden auto. hinzugefügt
                    LocalDate sparzielDatum = LocalDate.now().plusMonths(monate);
                    sparzielDatum = sparzielDatum.plusDays(tage);

                    //Sparziel Info erstellen 
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    a.setSparzielinfo("Noch " + (a.getSparzielbetrag() - a.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df) + " (bei monatl. Sparen von " + a.getSchrittbetrag() + "€)");
                    FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Das Häufigkeit (Sparhäufigkeit) wurde geändert. BITTE laden Sie diese Seite neu!", ""));
                    dao.updateSparen(a);
                    updateData();

                } else if (a.getEinsparhaeufigkeit().equals("täglich")) {
                    //Code wie beim Abschnitt mit "individuell" nur auf die Häufigkeit (hier: täglich) angepasst
                    double tage = ((a.getSparzielbetrag() - (a.getLetzterteilbetrag())) / a.getSchrittbetrag());
                    //Überprüfen ob Sparziel erreicht wurde
                    if (tage == 1.0) {
                        //Sparziel erreicht
                        a.setSparzielinfo("OK - Fertig gespart am " + sdf.format(a.getLetzterteildatum()));
                        FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Das Häufigkeit (Sparhäufigkeit) wurde geändert. BITTE laden Sie diese Seite neu!", ""));
                        dao.updateSparen(a);
                        updateData();
                    } else {
                        //Ungefähr. Taganteil
                        //Tage (bis noch zum Sparziel) hinzufügen, Jahre werden auto. hinzugefügt
                        LocalDate sparzielDatum = LocalDate.now().plusDays((int) tage);
                        //Sparziel Info erstellen 
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                        a.setSparzielinfo("Noch " + (a.getSparzielbetrag() - a.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df));
                        FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Das Häufigkeit (Sparhäufigkeit) wurde geändert. BITTE laden Sie diese Seite neu!", ""));
                        dao.updateSparen(a);
                        updateData();
                    }
                } else if (a.getEinsparhaeufigkeit().equals("monatlich")) {
                    //Anzahl der Jahre, Monate und Tage zum Sparziel, wenn der jetzt hinzugefuegte Sparbetrag monatlich gespart wird.
                    double tagemonate = 0.0;
                    //Wieviel man diesen Teilbetrag sparen muss, um auf das Sparziel zu erreichen. Ergebnis entspricht die Anzahl der Monate (Ganzzahliger Wert) und Anzahl der Tagen (Wert in den Nachkommastellen)
                    tagemonate = ((a.getSparzielbetrag() - (a.getLetzterteilbetrag())) / a.getSchrittbetrag());
                    if (tagemonate == 1.0) {
                        //Sparziel erreicht
                        a.setSparzielinfo("OK - Fertig gespart am " + sdf.format(a.getLetzterteildatum()));
                        dao.updateSparen(a);
                        FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Das Häufigkeit (Sparhäufigkeit) wurde geändert. BITTE laden Sie diese Seite neu!", ""));
                        updateData();
                    } else {
                        int monate = (int) tagemonate;
                        //Tageanteil ist ein Prozentsatz der für die Berechnung der Anzahl von Tagen eines Monats verwendet wird
                        double tageAnteil = Double.parseDouble(String.format("%.2f", tagemonate));
                        tageAnteil = ((tageAnteil * 100) % 100) / 100;
                        //Ungefähr. Taganteil
                        int tage = 28 * (int) tageAnteil;
                        //Monate und Tage (bis noch zum Sparziel) hinzufügen, Jahre werden auto. hinzugefügt
                        LocalDate sparzielDatum = LocalDate.now().plusMonths(monate);
                        if (tage > 0) {
                            sparzielDatum = sparzielDatum.plusDays(tage);
                        }
                        //Sparziel Info erstellen 
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                        a.setSparzielinfo("Noch " + (a.getSparzielbetrag() - a.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df));
                        FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Das Häufigkeit (Sparhäufigkeit) wurde geändert. BITTE laden Sie diese Seite neu!", ""));
                        dao.updateSparen(a);
                        updateData();
                    }
                } else if (a.getEinsparhaeufigkeit().equals("jährlich")) {
                    //Anzahl der Jahre, wenn der jetzt hinzugefuegte Sparbetrag jährlich gespart wird.
                    double jahremonate = 0.0;
                    //Wieviel man diesen Teilbetrag sparen muss, um auf das Sparziel zu erreichen. Ergebnis entspricht die Anzahl der Monate (Ganzzahliger Wert) und Anzahl der Tagen (Wert in den Nachkommastellen)
                    jahremonate = ((a.getSparzielbetrag() - (a.getLetzterteilbetrag())) / a.getSchrittbetrag());

                    if (jahremonate == 1.0) {
                        //Sparziel erreicht
                        a.setSparzielinfo("OK - Fertig gespart am " + sdf.format(a.getLetzterteildatum()));
                        dao.updateSparen(a);
                        FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Das Häufigkeit (Sparhäufigkeit) wurde geändert. BITTE laden Sie diese Seite neu!", ""));
                        updateData();
                    } else {
                        //Jahre aufrunden, da man nur jährlich bezahlt
                        int jahre = (int) Math.ceil(jahremonate);
                        //Jahre werden . hinzugefügt
                        LocalDate sparzielDatum = LocalDate.now();
                        if (jahre > 0) {
                            sparzielDatum = sparzielDatum.plusYears(jahre);
                        }
                        //Sparziel Info erstellen 
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                        a.setSparzielinfo("Noch " + (a.getSparzielbetrag() - a.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df));

                        FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Das Häufigkeit (Sparhäufigkeit) wurde geändert. BITTE laden Sie diese Seite neu!", ""));
                        dao.updateSparen(a);
                        updateData();
                    }
                }
            } else if (spaltenname.equals("Bemerkungen")) {
                a.setBemerkungen((String) event.getNewValue());
                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Die Bemerkungen von dem Sparkonto wurden aktualisiert. ", ""));
                dao.updateSparen(a);
                updateData();
            }

            // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "old value: ", "" + event.getNewValue()));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", e.toString()));
        }
    }

    /**
     * Datum und noch übriger Betrag zum Sparen berechnen und dann in die
     * Datenbanl abspeichern.
     */
    public void speichern() {
        Sparen sparkonto = new Sparen();
        sparkonto.setDeleted(false);
        if (this.bezeichnung != null) {
            sparkonto.setBezeichnung(bezeichnung);
        }
        sparkonto.setDatum(new Date());
        if (this.sparenHaeufigkeit != null) {
            if (this.sparenHaeufigkeit.equals("")) {
                sparkonto.setEinsparhaeufigkeit("individuell");
            } else {
                sparkonto.setEinsparhaeufigkeit(this.sparenHaeufigkeit);
            }
        } else {
            sparkonto.setEinsparhaeufigkeit("individuell");
        }
        if (this.schrittbetrag != null) {
            sparkonto.setSchrittbetrag(schrittbetrag);
        }

        if (this.sparzielbetrag != null) {
            sparkonto.setSparzielbetrag(sparzielbetrag);
        }
        if (this.bemerkungen != null) {
            sparkonto.setBemerkungen(bemerkungen);
        }

        //Datum und Beträge (Noch zum Sparziel übriger Betrag) berechnen
        sparkonto.setLetzterteilbetrag(schrittbetrag);
        sparkonto.setLetzterteildatum(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        if (sparkonto.getEinsparhaeufigkeit().equals("individuell")) {
            //Anzahl der Jahre, Monate und Tage zum Sparziel, wenn der jetzt hinzugefuegte Sparbetrag monatlich gespart wird.
            double tagemonate = 0.0;
            tagemonate = ((sparkonto.getSparzielbetrag() - (sparkonto.getLetzterteilbetrag())) / sparkonto.getSchrittbetrag());

            int monate = (int) tagemonate;
            //Tageanteil ist ein Prozentsatz der für die Berechnung der Anzahl von Tagen eines Monats verwendet wird
            double tageAnteil = Double.parseDouble(String.format("%.2f", tagemonate));
            tageAnteil = ((tageAnteil * 100) % 100) / 100;
            //Ungefähr. Taganteil
            int tage = 28 * (int) tageAnteil;
            //Monate und Tage (bis noch zum Sparziel) hinzufügen, Jahre werden auto. hinzugefügt
            LocalDate sparzielDatum = LocalDate.now().plusMonths(monate);
            sparzielDatum = sparzielDatum.plusDays(tage);

            //Sparziel Info erstellen 
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            sparkonto.setSparzielinfo("Noch " + (sparkonto.getSparzielbetrag() - sparkonto.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df) + " (bei monatl. Sparen von " + sparkonto.getSchrittbetrag() + "€)");
        } else if (sparkonto.getEinsparhaeufigkeit().equals("täglich")) {
            //Code wie beim Abschnitt mit "individuell" nur auf die Häufigkeit (hier: täglich) angepasst
            double tage = ((sparkonto.getSparzielbetrag() - (sparkonto.getLetzterteilbetrag())) / sparkonto.getSchrittbetrag());
            //Überprüfen ob Sparziel erreicht wurde
            if (tage == 1.0) {
                //Sparziel erreicht
                sparkonto.setSparzielinfo("OK - Fertig gespart am " + sdf.format(sparkonto.getLetzterteildatum()));
            } else {
                //Ungefähr. Taganteil
                //Tage (bis noch zum Sparziel) hinzufügen, Jahre werden auto. hinzugefügt
                LocalDate sparzielDatum = LocalDate.now().plusDays((int) tage);
                //Sparziel Info erstellen 
                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                sparkonto.setSparzielinfo("Noch " + (sparkonto.getSparzielbetrag() - sparkonto.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df));
            }
        } else if (sparkonto.getEinsparhaeufigkeit().equals("monatlich")) {
            //Anzahl der Jahre, Monate und Tage zum Sparziel, wenn der jetzt hinzugefuegte Sparbetrag monatlich gespart wird.
            double tagemonate = 0.0;
            //Wieviel man diesen Teilbetrag sparen muss, um auf das Sparziel zu erreichen. Ergebnis entspricht die Anzahl der Monate (Ganzzahliger Wert) und Anzahl der Tagen (Wert in den Nachkommastellen)
            tagemonate = ((sparkonto.getSparzielbetrag() - (sparkonto.getLetzterteilbetrag())) / sparkonto.getSchrittbetrag());
            if (tagemonate == 1.0) {
                //Sparziel erreicht
                sparkonto.setSparzielinfo("OK - Fertig gespart am " + sdf.format(sparkonto.getLetzterteildatum()));
            } else {
                int monate = (int) tagemonate;
                //Tageanteil ist ein Prozentsatz der für die Berechnung der Anzahl von Tagen eines Monats verwendet wird
                double tageAnteil = Double.parseDouble(String.format("%.2f", tagemonate));
                tageAnteil = ((tageAnteil * 100) % 100) / 100;
                //Ungefähr. Taganteil
                int tage = 28 * (int) tageAnteil;
                //Monate und Tage (bis noch zum Sparziel) hinzufügen, Jahre werden auto. hinzugefügt
                LocalDate sparzielDatum = LocalDate.now().plusMonths(monate);
                if (tage > 0) {
                    sparzielDatum = sparzielDatum.plusDays(tage);
                }
                //Sparziel Info erstellen 
                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                sparkonto.setSparzielinfo("Noch " + (sparkonto.getSparzielbetrag() - sparkonto.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df));
            }
        } else if (sparkonto.getEinsparhaeufigkeit().equals("jährlich")) {
            //Anzahl der Jahre, Monate zum Sparziel, wenn der jetzt hinzugefuegte Sparbetrag monatlich gespart wird.
            double jahremonate = 0.0;
            //Wieviel man diesen Teilbetrag sparen muss, um auf das Sparziel zu erreichen. Ergebnis entspricht die Anzahl der Monate (Ganzzahliger Wert) und Anzahl der Tagen (Wert in den Nachkommastellen)
            jahremonate = ((sparkonto.getSparzielbetrag() - (sparkonto.getLetzterteilbetrag())) / sparkonto.getSchrittbetrag());

            if (jahremonate == 1.0) {
                //Sparziel erreicht
                sparkonto.setSparzielinfo("OK - Fertig gespart am " + sdf.format(sparkonto.getLetzterteildatum()));
            } else {
                //Jahre aufrunden, da man nur jährlich bezahlt
                int jahre = (int) Math.ceil(jahremonate);
                //Jahre werden . hinzugefügt
                LocalDate sparzielDatum = LocalDate.now();
                if (jahre > 0) {
                    sparzielDatum = sparzielDatum.plusYears(jahre);
                }
                //Sparziel Info erstellen 
                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                sparkonto.setSparzielinfo("Noch " + (sparkonto.getSparzielbetrag() - sparkonto.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df));
            }
        }

        if (this.anhang != null && !this.anhangname.isEmpty()) {
            sparkonto.setAnhang(true);
            //IMMER VOR DEM INSERT BEFEHL
            //Neuen Datensatz direkt auch in der Tabelle ohne neuladen der Seite anzeigen
            this.sparenList.add(sparkonto);
            this.filteredSparenList.add(sparkonto);
            dao.insertSparen(sparkonto);

            List<Sparen> sparenListe = new ArrayList<>(this.sparenList);
            int letzteNr = sparenListe.size() - 1;
            if (letzteNr >= 0) {
                int neueID = sparenListe.get(letzteNr).getId();
                try {
                    Sparen a = sparenListe.get(letzteNr);
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

                    dao.updateSparen(a);

                } catch (HttpException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anhang: Upload Fehler ", "" + ex));
                } catch (Exception ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", "" + ex));
                }
                updateData();
            }
        } else {
            sparkonto.setAnhang(false);
            this.sparenList.add(sparkonto);
            this.filteredSparenList.add(sparkonto);
            dao.insertSparen(sparkonto);
            updateData();
        }
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

    public void datensatzLoeschen() {
        try {
            if (this.deleteID != null) {
                for (Sparen a : this.sparenList) {
                    if (a.getId().equals(Integer.parseInt(this.deleteID))) {
                        dao.deleteSparen(a);
                        this.deletedSparenList.add(a);
                        this.sparenList.remove(a);
                        this.filteredSparenList.remove(a);
                    }
                }
                updateData();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }
    }
    
        public void datensatzLoeschen(Integer id) {
        try {
            if (id != null) {
                for (Sparen a : this.sparenList) {
                    if (a.getId().equals(id)) {
                        dao.deleteSparen(a);
                        this.deletedSparenList.add(a);
                        this.sparenList.remove(a);
                        this.filteredSparenList.remove(a);
                    }
                }
                updateData();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }
    }

    public void datensatzLoeschenRueckgangigMachen() {

        if (!this.deletedSparenList.isEmpty()) {
            for (Sparen a : this.deletedSparenList) {
                this.deletedSparenList.add(a);
                this.filteredSparenList.add(a);
                a.setDeleted(false);
                dao.updateSparen(a);
            }
            updateData();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Gelöschte Datensätze wurden wiederhergestellt", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: Cache ist leer!", "Bitte manuell den Wert der Spalte delete auf false ändern!"));
        }
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

    public void scrollAnhangEdit() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("neuAnhangForm:AnhangEditPanel");
        PrimeFaces.current().scrollTo("neuAnhangForm:AnhangEditPanel");

    }

    public void onToggle(ToggleEvent e) {
        this.columnList.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public List<Sparen> getSparenList() {
        return sparenList;
    }

    public void setSparenList(List<Sparen> sparenList) {
        this.sparenList = sparenList;
    }

    public List<Sparen> getFilteredSparenList() {
        return filteredSparenList;
    }

    public void setFilteredSparenList(List<Sparen> filteredSparenList) {
        this.filteredSparenList = filteredSparenList;
    }

    public List<Sparen> getDeletedSparenList() {
        return deletedSparenList;
    }

    public void setDeletedSparenList(List<Sparen> deletedSparenList) {
        this.deletedSparenList = deletedSparenList;
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

    public List<Boolean> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<Boolean> columnList) {
        this.columnList = columnList;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public Double getSchrittbetrag() {
        return schrittbetrag;
    }

    public void setSchrittbetrag(Double schrittbetrag) {
        this.schrittbetrag = schrittbetrag;
    }

    public Double getSparzielbetrag() {
        return sparzielbetrag;
    }

    public void setSparzielbetrag(Double sparzielbetrag) {
        this.sparzielbetrag = sparzielbetrag;
    }

    public String getBemerkungen() {
        return bemerkungen;
    }

    public void setBemerkungen(String bemerkungen) {
        this.bemerkungen = bemerkungen;
    }

    public DAO getDao() {
        return dao;
    }

    public void setDao(DAO dao) {
        this.dao = dao;
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

    public String getDatensaetzeAnzahlText() {
        return datensaetzeAnzahlText;
    }

    public void setDatensaetzeAnzahlText(String datensaetzeAnzahlText) {
        this.datensaetzeAnzahlText = datensaetzeAnzahlText;
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

    public List<String> getHaeufigkeitList() {
        return haeufigkeitList;
    }

    public void setHaeufigkeitList(List<String> haeufigkeitList) {
        this.haeufigkeitList = haeufigkeitList;
    }

    public String getSparenHaeufigkeit() {
        return sparenHaeufigkeit;
    }

    public void setSparenHaeufigkeit(String sparenHaeufigkeit) {
        this.sparenHaeufigkeit = sparenHaeufigkeit;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.sparenList);
        hash = 71 * hash + Objects.hashCode(this.filteredSparenList);
        hash = 71 * hash + Objects.hashCode(this.deletedSparenList);
        hash = 71 * hash + Objects.hashCode(this.haeufigkeitList);
        hash = 71 * hash + Objects.hashCode(this.tabellenname);
        hash = 71 * hash + Objects.hashCode(this.baseUrl);
        hash = 71 * hash + Objects.hashCode(this.downloadUrl);
        hash = 71 * hash + Objects.hashCode(this.columnList);
        hash = 71 * hash + Objects.hashCode(this.bezeichnung);
        hash = 71 * hash + Objects.hashCode(this.schrittbetrag);
        hash = 71 * hash + Objects.hashCode(this.sparzielbetrag);
        hash = 71 * hash + Objects.hashCode(this.bemerkungen);
        hash = 71 * hash + Objects.hashCode(this.sparenHaeufigkeit);
        hash = 71 * hash + Objects.hashCode(this.dao);
        hash = 71 * hash + Objects.hashCode(this.rownumbers);
        hash = 71 * hash + Objects.hashCode(this.insert_rownumber);
        hash = 71 * hash + Objects.hashCode(this.anhangname);
        hash = 71 * hash + Objects.hashCode(this.anhangtype);
        hash = 71 * hash + Arrays.hashCode(this.anhang);
        hash = 71 * hash + Objects.hashCode(this.dbnotizEintrag);
        hash = 71 * hash + Objects.hashCode(this.notiztext);
        hash = 71 * hash + Objects.hashCode(this.datensaetzeAnzahlText);
        hash = 71 * hash + Objects.hashCode(this.deleteID);
        hash = 71 * hash + Objects.hashCode(this.anhangID);
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
        final SparenController other = (SparenController) obj;
        if (!Objects.equals(this.tabellenname, other.tabellenname)) {
            return false;
        }
        if (!Objects.equals(this.baseUrl, other.baseUrl)) {
            return false;
        }
        if (!Objects.equals(this.downloadUrl, other.downloadUrl)) {
            return false;
        }
        if (!Objects.equals(this.bezeichnung, other.bezeichnung)) {
            return false;
        }
        if (!Objects.equals(this.bemerkungen, other.bemerkungen)) {
            return false;
        }
        if (!Objects.equals(this.anhangname, other.anhangname)) {
            return false;
        }
        if (!Objects.equals(this.anhangtype, other.anhangtype)) {
            return false;
        }
        if (!Objects.equals(this.notiztext, other.notiztext)) {
            return false;
        }
        if (!Objects.equals(this.datensaetzeAnzahlText, other.datensaetzeAnzahlText)) {
            return false;
        }
        if (!Objects.equals(this.deleteID, other.deleteID)) {
            return false;
        }
        if (!Objects.equals(this.anhangID, other.anhangID)) {
            return false;
        }
        if (!Objects.equals(this.sparenList, other.sparenList)) {
            return false;
        }
        if (!Objects.equals(this.filteredSparenList, other.filteredSparenList)) {
            return false;
        }
        if (!Objects.equals(this.deletedSparenList, other.deletedSparenList)) {
            return false;
        }
        if (!Objects.equals(this.haeufigkeitList, other.haeufigkeitList)) {
            return false;
        }
        if (!Objects.equals(this.columnList, other.columnList)) {
            return false;
        }
        if (!Objects.equals(this.schrittbetrag, other.schrittbetrag)) {
            return false;
        }
        if (!Objects.equals(this.sparzielbetrag, other.sparzielbetrag)) {
            return false;
        }
        if (!Objects.equals(this.sparenHaeufigkeit, other.sparenHaeufigkeit)) {
            return false;
        }
        if (!Objects.equals(this.dao, other.dao)) {
            return false;
        }
        if (!Objects.equals(this.rownumbers, other.rownumbers)) {
            return false;
        }
        if (!Objects.equals(this.insert_rownumber, other.insert_rownumber)) {
            return false;
        }
        if (!Arrays.equals(this.anhang, other.anhang)) {
            return false;
        }
        if (!Objects.equals(this.dbnotizEintrag, other.dbnotizEintrag)) {
            return false;
        }
        return true;
    }

}
