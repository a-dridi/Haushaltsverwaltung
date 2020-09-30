/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.Temporal;

/**
 *
 * @author
 */
@Entity
@SequenceGenerator(name = "einnahmen_seq", sequenceName = "einnahmen_id_seq", allocationSize = 1)

public class Einnahmen implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO, generator = "einnahmen_seq")
    @Id
    private Integer einnahmen_id;
    private String bezeichnung;
    // EINE Kategorie
    private String kategorie;
    private Double betrag;
    // !!! greift auf ausgabezeitraum von Tabelle Ausgaben:
    private String haeufigkeit;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date eingangsdatum; //(Überschrift: Datum (Eingangsdatum)
    @Column(length = 10000)
    private String informationen;
    private boolean anhang = false;
    private String anhangpfad;
    private String anhangname;
    private String anhangtype;
    private boolean deleted;

    public Integer getEinnahmen_id() {
        return einnahmen_id;
    }

    public void setEinnahmen_id(Integer einnahmen_id) {
        this.einnahmen_id = einnahmen_id;
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

    public String getHaeufigkeit() {
        return haeufigkeit;
    }

    public void setHaeufigkeit(String haeufigkeit) {
        this.haeufigkeit = haeufigkeit;
    }

    public Date getEingangsdatum() {
        return eingangsdatum;
    }

    public void setEingangsdatum(Date eingangsdatum) {
        this.eingangsdatum = eingangsdatum;
    }

    public String getInformationen() {
        return informationen;
    }

    public void setInformationen(String informationen) {
        this.informationen = informationen;
    }

    public boolean isAnhang() {
        return anhang;
    }

    public void setAnhang(boolean anhang) {
        this.anhang = anhang;
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

    public String getAnhangtype() {
        return anhangtype;
    }

    public void setAnhangtype(String anhangtype) {
        this.anhangtype = anhangtype;
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
        hash = 29 * hash + Objects.hashCode(this.einnahmen_id);
        hash = 29 * hash + Objects.hashCode(this.bezeichnung);
        hash = 29 * hash + Objects.hashCode(this.kategorie);
        hash = 29 * hash + Objects.hashCode(this.betrag);
        hash = 29 * hash + Objects.hashCode(this.haeufigkeit);
        hash = 29 * hash + Objects.hashCode(this.eingangsdatum);
        hash = 29 * hash + Objects.hashCode(this.informationen);
        hash = 29 * hash + (this.anhang ? 1 : 0);
        hash = 29 * hash + Objects.hashCode(this.anhangpfad);
        hash = 29 * hash + Objects.hashCode(this.anhangname);
        hash = 29 * hash + Objects.hashCode(this.anhangtype);
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
        final Einnahmen other = (Einnahmen) obj;
        if (this.anhang != other.anhang) {
            return false;
        }
        if (!Objects.equals(this.bezeichnung, other.bezeichnung)) {
            return false;
        }
        if (!Objects.equals(this.kategorie, other.kategorie)) {
            return false;
        }
        if (!Objects.equals(this.haeufigkeit, other.haeufigkeit)) {
            return false;
        }
        if (!Objects.equals(this.eingangsdatum, other.eingangsdatum)) {
            return false;
        }
        if (!Objects.equals(this.informationen, other.informationen)) {
            return false;
        }
        if (!Objects.equals(this.anhangpfad, other.anhangpfad)) {
            return false;
        }
        if (!Objects.equals(this.anhangname, other.anhangname)) {
            return false;
        }
        if (!Objects.equals(this.anhangtype, other.anhangtype)) {
            return false;
        }
        if (!Objects.equals(this.einnahmen_id, other.einnahmen_id)) {
            return false;
        }
        if (!Objects.equals(this.betrag, other.betrag)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Einnahmen{" + "einnahmen_id=" + einnahmen_id + ", bezeichnung=" + bezeichnung + ", kategorie=" + kategorie + ", betrag=" + betrag + ", haeufigkeit=" + haeufigkeit + ", eingangsdatum=" + eingangsdatum + ", informationen=" + informationen + ", anhang=" + anhang + ", anhangpfad=" + anhangpfad + ", anhangname=" + anhangname + ", anhangtype=" + anhangtype + '}';
    }

}
