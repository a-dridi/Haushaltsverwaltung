/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;

/**
 * !!!ACHTUNG getAnhang ist eine benutzerdef. Methode, diese immer ANWENDEN!!!
 *
 * @author A.Dridi
 */
@Entity
@SequenceGenerator(name = "ausgaben_seq", sequenceName = "ausgaben_id_seq", allocationSize = 1)
public class Ausgaben implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ausgaben_seq")
    @Id
    private Integer ausgaben_id;
    private String bezeichnung;
    private String kategorie;
    private Double betrag;
    private String ausgabezeitraum;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date zahlungsdatum;
    @Column(length = 10000)
    private String informationen;
    private boolean anhang = false;
    private String anhangpfad;
    private String anhangname;
    private String anhangtype;
    private boolean deleted;

    public boolean isAnhang() {
        return anhang;
    }

    public void setAnhang(boolean anhang) {
        this.anhang = anhang;
    }

    public Integer getAusgaben_id() {
        return ausgaben_id;
    }

    public void setAusgaben_id(Integer ausgaben_id) {
        this.ausgaben_id = ausgaben_id;
    }

    public String getAnhangtype() {
        return anhangtype;
    }

    public void setAnhangtype(String anhangtype) {
        this.anhangtype = anhangtype;
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

    public Double getBetrag() {
        return betrag;
    }

    public void setBetrag(Double betrag) {
        this.betrag = betrag;
    }

    public String getAusgabezeitraum() {
        return ausgabezeitraum;
    }

    public void setAusgabezeitraum(String ausgabezeitraum) {
        this.ausgabezeitraum = ausgabezeitraum;
    }

    public Date getZahlungsdatum() {
        return zahlungsdatum;
    }

    public void setZahlungsdatum(Date zahlungsdatum) {
        this.zahlungsdatum = zahlungsdatum;
    }

    public String getInformationen() {
        return informationen;
    }

    public void setInformationen(String informationen) {
        this.informationen = informationen;
    }

    public String getAnhangpfad() {
        return anhangpfad;
    }

    public void setAnhangpfad(String anhangpfad) {
        this.anhangpfad = anhangpfad;
    }

    public String getAnhangname() {
        return anhangname;
    }

    public void setAnhangname(String anhangname) {
        this.anhangname = anhangname;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.ausgaben_id);
        hash = 47 * hash + Objects.hashCode(this.bezeichnung);
        hash = 47 * hash + Objects.hashCode(this.kategorie);
        hash = 47 * hash + Objects.hashCode(this.betrag);
        hash = 47 * hash + Objects.hashCode(this.ausgabezeitraum);
        hash = 47 * hash + Objects.hashCode(this.zahlungsdatum);
        hash = 47 * hash + Objects.hashCode(this.informationen);
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
        final Ausgaben other = (Ausgaben) obj;
        if (!Objects.equals(this.bezeichnung, other.bezeichnung)) {
            return false;
        }
        if (!Objects.equals(this.kategorie, other.kategorie)) {
            return false;
        }
        if (!Objects.equals(this.ausgabezeitraum, other.ausgabezeitraum)) {
            return false;
        }
        if (!Objects.equals(this.informationen, other.informationen)) {
            return false;
        }
        if (!Objects.equals(this.ausgaben_id, other.ausgaben_id)) {
            return false;
        }
        if (!Objects.equals(this.betrag, other.betrag)) {
            return false;
        }
        if (!Objects.equals(this.zahlungsdatum, other.zahlungsdatum)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Ausgaben{" + "ausgaben_id=" + ausgaben_id + ", bezeichnung=" + bezeichnung + ", kategorie=" + kategorie + ", betrag=" + betrag + ", ausgabezeitraum=" + ausgabezeitraum + ", zahlungsdatum=" + zahlungsdatum + ", informationen=" + informationen + '}';
    }

}
