/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import javax.persistence.GenerationType;

/**
 * Ohne Controller
 *
 */
@Entity
@SequenceGenerator(name = "vermoegenjaehrlich_seq", sequenceName = "vermoegenjaehrlich_id_seq", allocationSize = 1)

public class VermoegenJaehrlich implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO, generator = "vermoegenjaehrlich_seq")
    @Id
    private Integer vermoegenjaehrlich_id;
    private Integer jahr;
    private Double einnahmen;
    private Double ausgaben;

    private Double differenz;
    private Double prozentZuwachs;
    private boolean isGewinn;
    private boolean isVerlust;
    @Column(length = 10000)
    private String bemerkungen; //wird durch benutzer hinzugefügt
    //wird durch benutzer hinzugefügt:
    private boolean anhang = false;
    private String anhangpfad;
    private String anhangname;
    private String anhangtype;
    private boolean deleted;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getVermoegenjaehrlich_id() {
        return vermoegenjaehrlich_id;
    }

    public void setVermoegenjaehrlich_id(Integer vermoegenjaehrlich_id) {
        this.vermoegenjaehrlich_id = vermoegenjaehrlich_id;
    }

    public Integer getJahr() {
        return jahr;
    }

    public void setJahr(Integer jahr) {
        this.jahr = jahr;
    }

    public Double getDifferenz() {
        return differenz;
    }

    public void setDifferenz(Double differenz) {
        this.differenz = differenz;
    }

    public Double getProzentZuwachs() {
        return prozentZuwachs;
    }

    public void setProzentZuwachs(Double prozentZuwachs) {
        this.prozentZuwachs = prozentZuwachs;
    }

    public boolean isIsGewinn() {
        return isGewinn;
    }

    public void setIsGewinn(boolean isGewinn) {
        this.isGewinn = isGewinn;
    }

    public boolean isIsVerlust() {
        return isVerlust;
    }

    public void setIsVerlust(boolean isVerlust) {
        this.isVerlust = isVerlust;
    }

    public Double getEinnahmen() {
        return einnahmen;
    }

    public void setEinnahmen(Double einnahmen) {
        this.einnahmen = einnahmen;
    }

    public Double getAusgaben() {
        return ausgaben;
    }

    public void setAusgaben(Double ausgaben) {
        this.ausgaben = ausgaben;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.vermoegenjaehrlich_id);
        hash = 29 * hash + Objects.hashCode(this.jahr);
        hash = 29 * hash + Objects.hashCode(this.einnahmen);
        hash = 29 * hash + Objects.hashCode(this.ausgaben);
        hash = 29 * hash + Objects.hashCode(this.differenz);
        hash = 29 * hash + Objects.hashCode(this.prozentZuwachs);
        hash = 29 * hash + (this.isGewinn ? 1 : 0);
        hash = 29 * hash + (this.isVerlust ? 1 : 0);
        hash = 29 * hash + Objects.hashCode(this.bemerkungen);
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
        final VermoegenJaehrlich other = (VermoegenJaehrlich) obj;
        if (this.isGewinn != other.isGewinn) {
            return false;
        }
        if (this.isVerlust != other.isVerlust) {
            return false;
        }
        if (this.anhang != other.anhang) {
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
        if (!Objects.equals(this.vermoegenjaehrlich_id, other.vermoegenjaehrlich_id)) {
            return false;
        }
        if (!Objects.equals(this.jahr, other.jahr)) {
            return false;
        }
        if (!Objects.equals(this.einnahmen, other.einnahmen)) {
            return false;
        }
        if (!Objects.equals(this.ausgaben, other.ausgaben)) {
            return false;
        }
        if (!Objects.equals(this.differenz, other.differenz)) {
            return false;
        }
        if (!Objects.equals(this.prozentZuwachs, other.prozentZuwachs)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "VermoegenJaehrlich{" + "vermoegenjaehrlich_id=" + vermoegenjaehrlich_id + ", jahr=" + jahr + ", einnahmen=" + einnahmen + ", ausgaben=" + ausgaben + ", differenz=" + differenz + ", prozentZuwachs=" + prozentZuwachs + ", isGewinn=" + isGewinn + ", isVerlust=" + isVerlust + ", bemerkungen=" + bemerkungen + ", anhang=" + anhang + '}';
    }

}
