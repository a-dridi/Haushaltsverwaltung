/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.controller;

import haushaltsverwaltung.db.DAO;
import haushaltsverwaltung.model.DatenbankNotizen;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 * FÜR Startseite Notizen-> Bemerkungen / Ankündigungen
 *
 * @author A.Dridi
 */
@Named(value = "haushaltsverwaltungController")
@ViewScoped
public class HaushaltsverwaltungController implements Serializable {

    private DAO dao;
    private DatenbankNotizen dbnotizEintrag = null;
    private String notiztext = "";
    private String datumNotiztext;

    /**
     * Creates a new instance of HaushaltsverwaltungController
     */
    public HaushaltsverwaltungController() {
        this.dao = new DAO();
    }

    @PostConstruct
    private void init() {
        DAO dao = new DAO();
        List<DatenbankNotizen> notizList = dao.getDatenbankNotiz("Hausverwaltung_Notizen_Bemerkungen");
        if (notizList != null && !notizList.isEmpty()) {
            this.notiztext = notizList.get(0).getNotiztext();
            this.dbnotizEintrag = notizList.get(0);
            this.datumNotiztext = "Zuletzt akualisiert: " + notizList.get(0).getDatum();
        }
    }

    public void speichernNotiz() {
        DAO dao = new DAO();
        List<DatenbankNotizen> notizList = dao.getDatenbankNotiz("Hausverwaltung_Notizen_Bemerkungen");
        if (notizList != null && !notizList.isEmpty()) {
            this.dbnotizEintrag = notizList.get(0);
        }
        //Notiz-DB Eintrag für diese Tabelle schon zuvor erstellt wurde
        if (this.dbnotizEintrag != null) {
            //Notiz-Eintrag aktualisieren
            this.dbnotizEintrag.setTabelle("Hausverwaltung_Notizen_Bemerkungen");
            this.dbnotizEintrag.setDatum(new Date());
            this.dbnotizEintrag.setNotiztext(notiztext);
            dao.updateDatenbankNotizen(this.dbnotizEintrag);
        } else {
            //Neuen Notiz-Eintrag erstellen
            DatenbankNotizen n = new DatenbankNotizen();
            n.setTabelle("Hausverwaltung_Notizen_Bemerkungen");
            n.setDatum(new Date());
            n.setNotiztext(notiztext);
            dao.insertDatenbankNotizen(n);
        }
    }

    public void clearNotizen() {
        DAO dao = new DAO();

        //Notiz-DB Eintrag für diese Tabelle schon zuvor erstellt wurde
        if (this.notiztext != null) {
            //Notiz-Eintrag als leer speichern
            DatenbankNotizen n = new DatenbankNotizen();
            n.setTabelle("Hausverwaltung_Notizen_Bemerkungen");
            n.setDatum(new Date());
            n.setNotiztext("");
            dao.updateDatenbankNotizen(this.dbnotizEintrag);
        } else {
            //Neuen Notiz-Eintrag erstellen und als leer speichern
            DatenbankNotizen n = new DatenbankNotizen();
            n.setTabelle("Hausverwaltung_Notizen_Bemerkungen");
            n.setDatum(new Date());
            n.setNotiztext("");
            dao.insertDatenbankNotizen(n);
        }
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

    public String getDatumNotiztext() {
        return datumNotiztext;
    }

    public void setDatumNotiztext(String datumNotiztext) {
        this.datumNotiztext = datumNotiztext;
    }

}
