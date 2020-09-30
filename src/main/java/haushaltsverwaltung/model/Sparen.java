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
import javax.persistence.GenerationType;
import javax.persistence.Temporal;

/**
 *
 * @author
 */
@Entity
@SequenceGenerator(name = "sparen_seq", sequenceName = "sparen_id_seq", allocationSize = 1)

public class Sparen implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO, generator = "sparen_seq")
    @Id
    private Integer id;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date datum;
    private String bezeichnung;
    private Double schrittbetrag;
    private Double sparzielbetrag;
    private String einsparhaeufigkeit;
    //Speichert alle bis jetzt angesparten Geldbetrag
    private Double letzterteilbetrag;
    private Date letzterteildatum;
    private String sparzielinfo;
    private String bemerkungen;
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

    public String getEinsparhaeufigkeit() {
        return einsparhaeufigkeit;
    }

    public void setEinsparhaeufigkeit(String einsparhaeufigkeit) {
        this.einsparhaeufigkeit = einsparhaeufigkeit;
    }

    public Double getLetzterteilbetrag() {
        return letzterteilbetrag;
    }

    public void setLetzterteilbetrag(Double letzterteilbetrag) {
        this.letzterteilbetrag = letzterteilbetrag;
    }

    public Date getLetzterteildatum() {
        return letzterteildatum;
    }

    public void setLetzterteildatum(Date letzterteildatum) {
        this.letzterteildatum = letzterteildatum;
    }

    public String getSparzielinfo() {
        return sparzielinfo;
    }

    public void setSparzielinfo(String sparzielinfo) {
        this.sparzielinfo = sparzielinfo;
    }

    public String getBemerkungen() {
        return bemerkungen;
    }

    public void setBemerkungen(String bemerkungen) {
        this.bemerkungen = bemerkungen;
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

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.id);
        hash = 83 * hash + Objects.hashCode(this.datum);
        hash = 83 * hash + Objects.hashCode(this.bezeichnung);
        hash = 83 * hash + Objects.hashCode(this.schrittbetrag);
        hash = 83 * hash + Objects.hashCode(this.sparzielbetrag);
        hash = 83 * hash + Objects.hashCode(this.einsparhaeufigkeit);
        hash = 83 * hash + Objects.hashCode(this.letzterteilbetrag);
        hash = 83 * hash + Objects.hashCode(this.letzterteildatum);
        hash = 83 * hash + Objects.hashCode(this.sparzielinfo);
        hash = 83 * hash + Objects.hashCode(this.bemerkungen);
        hash = 83 * hash + (this.anhang ? 1 : 0);
        hash = 83 * hash + Objects.hashCode(this.anhangpfad);
        hash = 83 * hash + Objects.hashCode(this.anhangname);
        hash = 83 * hash + Objects.hashCode(this.anhangtype);
        hash = 83 * hash + (this.deleted ? 1 : 0);
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
        final Sparen other = (Sparen) obj;
        if (this.anhang != other.anhang) {
            return false;
        }
        if (this.deleted != other.deleted) {
            return false;
        }
        if (!Objects.equals(this.bezeichnung, other.bezeichnung)) {
            return false;
        }
        if (!Objects.equals(this.einsparhaeufigkeit, other.einsparhaeufigkeit)) {
            return false;
        }
        if (!Objects.equals(this.sparzielinfo, other.sparzielinfo)) {
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
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.datum, other.datum)) {
            return false;
        }
        if (!Objects.equals(this.schrittbetrag, other.schrittbetrag)) {
            return false;
        }
        if (!Objects.equals(this.sparzielbetrag, other.sparzielbetrag)) {
            return false;
        }
        if (!Objects.equals(this.letzterteilbetrag, other.letzterteilbetrag)) {
            return false;
        }
        if (!Objects.equals(this.letzterteildatum, other.letzterteildatum)) {
            return false;
        }
        return true;
    }

}
