/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.controller;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import haushaltsverwaltung.db.DAO;
import haushaltsverwaltung.model.DatenbankNotizen;
import haushaltsverwaltung.model.Ordnung;
import haushaltsverwaltung.model.Ordnungkategorie;
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
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.jackrabbit.webdav.client.methods.DeleteMethod;
import org.apache.jackrabbit.webdav.client.methods.PutMethod;
import org.apache.poi.util.IOUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;

/**
 *
 * Managment of physical objects (Documents, products, ...( - Verwaltung von
 * phys. Objekten (Dokumente, Waren, etc.)
 *
 * @author A.Dridi
 */
@Named(value = "ordnungController")
@ViewScoped
public class OrdnungController implements Serializable {

    private Ordnungkategorie ordnungskategorie;
    private List<Ordnungkategorie> ordnungkategorien = new ArrayList<>();
    private List<Ordnung> ordnungListenSQL = new ArrayList<>();
    private List<Ordnung> filteredOrdnungListenSQL;
    private String tabellenname = "Ordnung";

    //immer Ändern - OHNE / (SLASH) AM ENDE:
    private String baseUrl = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/dav/files/haushaltsverwaltung/Ordnung";
    private String downloadUrl = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/webdav/Ordnung";
    private final String cloudUsername = "CLOUDUSERNAME";
    private final String cloudPassword = "CLOUDPASSWORD";

    private String datensaetzeAnzahlText;
    private List<Ordnung> deletedOrdnungListenSQL = new ArrayList<>();

    //ExportColumns - WICHTIG ANZAHL AN SPALTENANZAHL ANPASSEN (ausgenommen Anhang/D Spalte)!!!:
    private List<Boolean> columnList = Arrays.asList(true, true, true, true, true, true);

    private String bezeichnung;
    private String kategorie;
    private String deleteID;
    private String lagerort;
    private String zustand;
    private String informationen;
    private DatenbankNotizen dbnotizEintrag = null;
    private String notiztext;

    private DAO dao;
    private String neuOrdnungkategorie;

    private String change_Kategorie;
    private Ordnungkategorie deleteOrdnungkategorie;

    private Integer rownumbers = 15;
    private Integer insert_rownumber;

    private String anhangname;
    private String anhangtype;
    private String anhangID;
    private byte[] anhang;

    /**
     * Creates a new instance of AusgabenController
     */
    public OrdnungController() {
        this.dao = new DAO();
    }

    @PostConstruct
    private void init() {
        List<DatenbankNotizen> notizList = dao.getDatenbankNotiz(this.tabellenname);
        if (notizList != null && !notizList.isEmpty()) {
            this.notiztext = notizList.get(0).getNotiztext();
            this.dbnotizEintrag = notizList.get(0);
        }

        this.dao = new DAO();
        this.ordnungkategorien = dao.getAllOrdnungskategorie();
        this.ordnungListenSQL = dao.getAllOrdnung();
        this.filteredOrdnungListenSQL = new ArrayList<>(ordnungListenSQL);

        flushAnhang();
        this.datensaetzeAnzahlText = ("Insgesamt: " + this.ordnungListenSQL.size() + " Datensaetze in der DB gespeichert");
    }

    /**
     * Anhang bearbeiten: Aber bei Übergabe eines leeren Anhangs wird der Anhang
     * für die betroffene Zeile gelöscht
     */
    public void editAnhang() {
        try {
            int zeilenID = Integer.parseInt(this.anhangID);
            boolean id_existiert = false;
            List<Ordnung> liste = new ArrayList<>(this.ordnungListenSQL);
            gefunden:
            for (Ordnung a : liste) {
                if (a.getOrdnung_id().equals(zeilenID)) {
                    Integer extPos = this.anhangname.lastIndexOf(".");
                    String dateiext = this.anhangname.substring(extPos + 1);
                    HttpClient client = new HttpClient();

                    Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    if (this.anhang != null) {
                        a.setAnhang(true);
                        a.setAnhangname((a.getOrdnung_id()) + "." + dateiext);
                        a.setAnhangtype(this.anhangtype);
                        a.setAnhangpfad(this.downloadUrl + "/" + ((a.getOrdnung_id()) + "." + dateiext));

                        InputStream ins = new ByteArrayInputStream(this.anhang);
                        PutMethod method = new PutMethod(this.baseUrl + "/" + ((a.getOrdnung_id()) + "." + dateiext));
                        RequestEntity requestEntity = new InputStreamRequestEntity(ins);
                        method.setRequestEntity(requestEntity);
                        client.executeMethod(method);
                        System.out.println(method.getStatusCode() + " " + method.getStatusText());
                        dao.updateOrdnung(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getOrdnung_id() + " wurde aktualisiert ", " "));
                    } else {
                        //Anhang loeschen und nicht ersetzen
                        DeleteMethod m = new DeleteMethod(this.baseUrl + "/" + ((a.getOrdnung_id()) + "." + dateiext));
                        client.executeMethod(m);
                        a.setAnhang(false);
                        a.setAnhangname("");
                        a.setAnhangtype("");
                        a.setAnhangpfad("");
                        dao.updateOrdnung(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getOrdnung_id() + " wurde gelöscht ", "Die phys. Datei muss dann manuell auf der Cloud von Ihnen gelöscht werden"));
                    }
                    id_existiert = true;
                    flushAnhang();

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

    public void preProcessPDF(Object document) {
        Document pdf = (Document) document;
        pdf.open();
        pdf.setPageSize(PageSize.A4.rotate());
    }

    /**
     * Editor für Zeile aufrufen
     */
    public void editRow(CellEditEvent event) {
        try {
            DataTable tabelle = (DataTable) event.getSource();
            String spaltenname = event.getColumn().getHeaderText();
            this.dao = new DAO();

            Ordnung a = (this.dao.getSingleOrdnung((Integer) tabelle.getRowKey())).get(0);
            //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Spalte: ", "" + spaltenname));

            if (spaltenname.equals("Bezeichnung")) {
                a.setBezeichnung((String) event.getNewValue());
            }

            if (spaltenname.equals("Kategorie")) {
                String auswahl = (String) event.getNewValue();
                gefunden:
                for (Ordnungkategorie m : this.ordnungkategorien) {
                    if (m.getKategoriebezeichnung().equals(auswahl)) {
                        a.setKategorie((String) event.getNewValue());
                        break gefunden;
                    }
                }
            }

            if (spaltenname.equals("Lagerort")) {
                a.setLagerort((String) event.getNewValue());
            }

            if (spaltenname.equals("Zustand")) {
                a.setZustand((String) event.getNewValue());
            }

            if (spaltenname.equals("Informationen")) {
                a.setInformationen((String) event.getNewValue());
            }
            // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "old value: ", "" + event.getNewValue()));

            dao.updateOrdnung(a);
            updateData();
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde aktualisiert", ""));

            //DEBUG:
            //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kategorie: ", kategorie));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", e.toString()));
        }
    }

    public void updateData() {
        this.dao = new DAO();
        this.ordnungkategorien = dao.getAllOrdnungskategorie();
        this.ordnungListenSQL = dao.getAllOrdnung();
        this.filteredOrdnungListenSQL = dao.getAllOrdnung();
        flushAnhang();
        this.datensaetzeAnzahlText = ("Insgesamt: " + this.ordnungListenSQL.size() + " Datensaetze in der DB gespeichert");
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
        Ordnung ordnung = new Ordnung();
        ordnung.setDeleted(false);

        if (this.bezeichnung != null) {
            ordnung.setBezeichnung(bezeichnung);
        }

        if (this.ordnungskategorie != null) {
            ordnung.setKategorie(this.ordnungskategorie.getKategoriebezeichnung());
        }

        ordnung.setLagerort(lagerort);

        ordnung.setZustand(zustand);

        ordnung.setInformationen(informationen);

        if (this.anhang != null && !this.anhangname.isEmpty()) {
            ordnung.setAnhang(true);
            this.ordnungListenSQL.add(ordnung);
            this.filteredOrdnungListenSQL.add(ordnung);

            dao.insertOrdnung(ordnung);

            List<Ordnung> ausgabenListe = new ArrayList<>(this.ordnungListenSQL);
            int letzteNr = ausgabenListe.size() - 1;
            if (letzteNr >= 0) {
                int neueID = ausgabenListe.get(letzteNr).getOrdnung_id();
                try {
                    Ordnung a = ausgabenListe.get(letzteNr);
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
                    dao.updateOrdnung(a);

                } catch (HttpException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anhang: Upload Fehler ", "" + ex));
                } catch (Exception ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", "" + ex));
                }

                updateData();
            }
        } else {
            ordnung.setAnhang(false);
            this.ordnungListenSQL.add(ordnung);
            this.filteredOrdnungListenSQL.add(ordnung);
            dao.insertOrdnung(ordnung);
            updateData();
            flushAnhang();
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

        if (!this.neuOrdnungkategorie.isEmpty()) {
            Ordnungkategorie ak = new Ordnungkategorie();
            ak.setKategoriebezeichnung(this.neuOrdnungkategorie);
            dao.insertOrdnungkategorie(ak);
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateData();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void kategorieLoeschen() {

        if (this.deleteOrdnungkategorie != null) {

            List<Ordnungkategorie> akList = dao.getAllOrdnungskategorie();
            List<Ordnung> ausgabenList = dao.getAllOrdnung();
            boolean kategorieExist = false;

            for (Ordnungkategorie a : akList) {
                if ((a.getKategoriebezeichnung().toLowerCase()).equals(this.deleteOrdnungkategorie.getKategoriebezeichnung().toLowerCase())) {
                    dao.deleteOrdnungkategorie(a);
                    for (Ordnung ausgabe : ausgabenList) {
                        if ((ausgabe.getKategorie().toLowerCase()).equals(this.deleteOrdnungkategorie.getKategoriebezeichnung().toLowerCase())) {
                            this.ordnungListenSQL.remove(ausgabe);
                            this.filteredOrdnungListenSQL.remove(ausgabe);

                            ausgabe.setKategorie(this.change_Kategorie);
                            dao.updateOrdnung(ausgabe);
                            this.ordnungListenSQL.add(ausgabe);
                            this.filteredOrdnungListenSQL.add(ausgabe);

                        }
                    }
                }
                if ((a.getKategoriebezeichnung().toLowerCase()).equals(this.change_Kategorie.toLowerCase())) {
                    kategorieExist = true;
                }
            }
            if (!kategorieExist) {
                Ordnungkategorie neu = new Ordnungkategorie();
                neu.setKategoriebezeichnung(this.change_Kategorie);
                dao.insertOrdnungkategorie(neu);
            }
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateData();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void datensatzLoeschen() {
        try {
            if (this.deleteID != null) {
                for (Ordnung a : this.ordnungListenSQL) {
                    if (a.getOrdnung_id().equals(Integer.parseInt(this.deleteID))) {
                        dao.deleteOrdnung(a);
                        this.deletedOrdnungListenSQL.add(a);
                        this.ordnungListenSQL.remove(a);
                        this.filteredOrdnungListenSQL.remove(a);
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
                for (Ordnung a : this.ordnungListenSQL) {
                    if (a.getOrdnung_id().equals(id)) {
                        dao.deleteOrdnung(a);
                        this.deletedOrdnungListenSQL.add(a);
                        this.ordnungListenSQL.remove(a);
                        this.filteredOrdnungListenSQL.remove(a);
                    }
                }
                updateData();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }

    }

    public void datensatzLoeschenRueckgangigMachen() {

        if (!this.deletedOrdnungListenSQL.isEmpty()) {
            for (Ordnung a : this.deletedOrdnungListenSQL) {
                this.ordnungListenSQL.add(a);
                this.filteredOrdnungListenSQL.add(a);
                a.setDeleted(false);
                dao.updateOrdnung(a);
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

    public void onToggle(ToggleEvent e) {
        this.columnList.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public Ordnungkategorie getOrdnungskategorie() {
        return ordnungskategorie;
    }

    public void setOrdnungskategorie(Ordnungkategorie ordnungskategorie) {
        this.ordnungskategorie = ordnungskategorie;
    }

    public List<Ordnungkategorie> getOrdnungkategorien() {
        return ordnungkategorien;
    }

    public void setOrdnungkategorien(List<Ordnungkategorie> ordnungkategorien) {
        this.ordnungkategorien = ordnungkategorien;
    }

    public List<Ordnung> getOrdnungListenSQL() {
        return ordnungListenSQL;
    }

    public void setOrdnungListenSQL(List<Ordnung> ordnungListenSQL) {
        this.ordnungListenSQL = ordnungListenSQL;
    }

    public List<Ordnung> getFilteredOrdnungListenSQL() {
        return filteredOrdnungListenSQL;
    }

    public void setFilteredOrdnungListenSQL(List<Ordnung> filteredOrdnungListenSQL) {
        this.filteredOrdnungListenSQL = filteredOrdnungListenSQL;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }

    public String getDeleteID() {
        return deleteID;
    }

    public void setDeleteID(String deleteID) {
        this.deleteID = deleteID;
    }

    public String getLagerort() {
        return lagerort;
    }

    public void setLagerort(String lagerort) {
        this.lagerort = lagerort;
    }

    public String getZustand() {
        return zustand;
    }

    public void setZustand(String zustand) {
        this.zustand = zustand;
    }

    public String getInformationen() {
        return informationen;
    }

    public void setInformationen(String informationen) {
        this.informationen = informationen;
    }

    public String getNeuOrdnungkategorie() {
        return neuOrdnungkategorie;
    }

    public void setNeuOrdnungkategorie(String neuOrdnungkategorie) {
        this.neuOrdnungkategorie = neuOrdnungkategorie;
    }

    public String getChange_Kategorie() {
        return change_Kategorie;
    }

    public void setChange_Kategorie(String change_Kategorie) {
        this.change_Kategorie = change_Kategorie;
    }

    public Ordnungkategorie getDeleteOrdnungkategorie() {
        return deleteOrdnungkategorie;
    }

    public void setDeleteOrdnungkategorie(Ordnungkategorie deleteOrdnungkategorie) {
        this.deleteOrdnungkategorie = deleteOrdnungkategorie;
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

    public String getAnhangID() {
        return anhangID;
    }

    public void setAnhangID(String anhangID) {
        this.anhangID = anhangID;
    }

    public String getTabellenname() {
        return tabellenname;
    }

    public void setTabellenname(String tabellenname) {
        this.tabellenname = tabellenname;
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

    public byte[] getAnhang() {
        return anhang;
    }

    public void setAnhang(byte[] anhang) {
        this.anhang = anhang;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.ordnungskategorie);
        hash = 19 * hash + Objects.hashCode(this.ordnungkategorien);
        hash = 19 * hash + Objects.hashCode(this.ordnungListenSQL);
        hash = 19 * hash + Objects.hashCode(this.filteredOrdnungListenSQL);
        hash = 19 * hash + Objects.hashCode(this.bezeichnung);
        hash = 19 * hash + Objects.hashCode(this.kategorie);
        hash = 19 * hash + Objects.hashCode(this.deleteID);
        hash = 19 * hash + Objects.hashCode(this.lagerort);
        hash = 19 * hash + Objects.hashCode(this.zustand);
        hash = 19 * hash + Objects.hashCode(this.informationen);
        hash = 19 * hash + Objects.hashCode(this.neuOrdnungkategorie);
        hash = 19 * hash + Objects.hashCode(this.change_Kategorie);
        hash = 19 * hash + Objects.hashCode(this.deleteOrdnungkategorie);
        hash = 19 * hash + Objects.hashCode(this.rownumbers);
        hash = 19 * hash + Objects.hashCode(this.insert_rownumber);
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
        final OrdnungController other = (OrdnungController) obj;
        if (!Objects.equals(this.bezeichnung, other.bezeichnung)) {
            return false;
        }
        if (!Objects.equals(this.kategorie, other.kategorie)) {
            return false;
        }
        if (!Objects.equals(this.deleteID, other.deleteID)) {
            return false;
        }
        if (!Objects.equals(this.lagerort, other.lagerort)) {
            return false;
        }
        if (!Objects.equals(this.zustand, other.zustand)) {
            return false;
        }
        if (!Objects.equals(this.informationen, other.informationen)) {
            return false;
        }
        if (!Objects.equals(this.neuOrdnungkategorie, other.neuOrdnungkategorie)) {
            return false;
        }
        if (!Objects.equals(this.change_Kategorie, other.change_Kategorie)) {
            return false;
        }
        if (!Objects.equals(this.ordnungskategorie, other.ordnungskategorie)) {
            return false;
        }
        if (!Objects.equals(this.ordnungkategorien, other.ordnungkategorien)) {
            return false;
        }
        if (!Objects.equals(this.ordnungListenSQL, other.ordnungListenSQL)) {
            return false;
        }
        if (!Objects.equals(this.filteredOrdnungListenSQL, other.filteredOrdnungListenSQL)) {
            return false;
        }
        if (!Objects.equals(this.deleteOrdnungkategorie, other.deleteOrdnungkategorie)) {
            return false;
        }
        if (!Objects.equals(this.rownumbers, other.rownumbers)) {
            return false;
        }
        if (!Objects.equals(this.insert_rownumber, other.insert_rownumber)) {
            return false;
        }
        return true;
    }

}
