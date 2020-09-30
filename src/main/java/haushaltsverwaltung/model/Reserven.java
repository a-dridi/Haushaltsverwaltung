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
 *
 * @author A.Dridi
 */
@Entity
@SequenceGenerator(name = "reserven_seq", sequenceName = "reserven_id_seq", allocationSize = 1)
public class Reserven implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO, generator = "reserven_seq")
    @Id
    private Integer reserven_id;

    private String kategorie;
    private String beschreibung;
    private Double betrag;
    private String waehrung;
    private String lagerort;
    @Column(length = 10000)
    private String bemerkungen;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date datum;
    private boolean anhang = false;
    private String anhangpfad;
    private String anhangname;
    private String anhangtype;
    private boolean deleted;

    public Integer getReserven_id() {
        return reserven_id;
    }

    public void setReserven_id(Integer reserven_id) {
        this.reserven_id = reserven_id;
    }

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
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

    public String getWaehrung() {
        return waehrung;
    }

    public void setWaehrung(String waehrung) {
        this.waehrung = waehrung;
    }

    public String getLagerort() {
        return lagerort;
    }

    public void setLagerort(String lagerort) {
        this.lagerort = lagerort;
    }

    public String getBemerkungen() {
        return bemerkungen;
    }

    public void setBemerkungen(String bemerkungen) {
        this.bemerkungen = bemerkungen;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
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
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.reserven_id);
        hash = 97 * hash + Objects.hashCode(this.kategorie);
        hash = 97 * hash + Objects.hashCode(this.beschreibung);
        hash = 97 * hash + Objects.hashCode(this.betrag);
        hash = 97 * hash + Objects.hashCode(this.waehrung);
        hash = 97 * hash + Objects.hashCode(this.lagerort);
        hash = 97 * hash + Objects.hashCode(this.bemerkungen);
        hash = 97 * hash + Objects.hashCode(this.datum);
        hash = 97 * hash + (this.anhang ? 1 : 0);
        hash = 97 * hash + Objects.hashCode(this.anhangpfad);
        hash = 97 * hash + Objects.hashCode(this.anhangname);
        hash = 97 * hash + Objects.hashCode(this.anhangtype);
        hash = 97 * hash + (this.deleted ? 1 : 0);
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
        final Reserven other = (Reserven) obj;
        if (this.anhang != other.anhang) {
            return false;
        }
        if (this.deleted != other.deleted) {
            return false;
        }
        if (!Objects.equals(this.kategorie, other.kategorie)) {
            return false;
        }
        if (!Objects.equals(this.beschreibung, other.beschreibung)) {
            return false;
        }
        if (!Objects.equals(this.waehrung, other.waehrung)) {
            return false;
        }
        if (!Objects.equals(this.lagerort, other.lagerort)) {
            return false;
        }
        if (!Objects.equals(this.bemerkungen, other.bemerkungen)) {
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
        if (!Objects.equals(this.reserven_id, other.reserven_id)) {
            return false;
        }
        if (!Objects.equals(this.betrag, other.betrag)) {
            return false;
        }
        if (!Objects.equals(this.datum, other.datum)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Reserven{" + "reserven_id=" + reserven_id + ", kategorie=" + kategorie + ", beschreibung=" + beschreibung + ", betrag=" + betrag + ", waehrung=" + waehrung + ", lagerort=" + lagerort + ", bemerkungen=" + bemerkungen + ", datum=" + datum + ", anhang=" + anhang + ", anhangpfad=" + anhangpfad + ", anhangname=" + anhangname + ", anhangtype=" + anhangtype + ", deleted=" + deleted + '}';
    }

}
