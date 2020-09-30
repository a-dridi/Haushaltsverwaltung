/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.Temporal;

/**
 * Handel/Tausch von Dienstleistungen und Waren nicht für Gewinn, aber für
 * nicht-geldliche Gegenleistung
 *
 * @author
 */
@Entity
@SequenceGenerator(name = "handel_seq", sequenceName = "handel_id_seq", allocationSize = 1)
public class Handel implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO, generator = "handel_seq")
    @Id
    private Integer id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date datum;
    private String bezeichnung;
    private String handelspartner;
    private String gegenleistung;
    private String ort;
    @Column(length = 10000)
    private String bemerkung;
    private boolean anhang = false;
    private String anhangpfad;
    private String anhangname;
    private String anhangtype;
    private boolean deleted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getHandelspartner() {
        return handelspartner;
    }

    public void setHandelspartner(String handelspartner) {
        this.handelspartner = handelspartner;
    }

    public String getGegenleistung() {
        return gegenleistung;
    }

    public void setGegenleistung(String gegenleistung) {
        this.gegenleistung = gegenleistung;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
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

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
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
        hash = 89 * hash + Objects.hashCode(this.id);
        hash = 89 * hash + Objects.hashCode(this.datum);
        hash = 89 * hash + Objects.hashCode(this.bezeichnung);
        hash = 89 * hash + Objects.hashCode(this.handelspartner);
        hash = 89 * hash + Objects.hashCode(this.gegenleistung);
        hash = 89 * hash + Objects.hashCode(this.bemerkung);
        hash = 89 * hash + (this.anhang ? 1 : 0);
        hash = 89 * hash + Objects.hashCode(this.anhangpfad);
        hash = 89 * hash + Objects.hashCode(this.anhangname);
        hash = 89 * hash + Objects.hashCode(this.anhangtype);
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
        final Handel other = (Handel) obj;
        if (this.anhang != other.anhang) {
            return false;
        }
        if (!Objects.equals(this.bezeichnung, other.bezeichnung)) {
            return false;
        }
        if (!Objects.equals(this.handelspartner, other.handelspartner)) {
            return false;
        }
        if (!Objects.equals(this.gegenleistung, other.gegenleistung)) {
            return false;
        }
        if (!Objects.equals(this.bemerkung, other.bemerkung)) {
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
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.datum, other.datum)) {
            return false;
        }
        return true;
    }

}
