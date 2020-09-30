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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 *
 * @author A.Dridi
 */
@Entity
@SequenceGenerator(name = "ausgabenbudget_seq", sequenceName = "ausgabenbudget_id_seq", allocationSize = 1)
public class AusgabenBudget implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ausgabenbudget_seq")
    @Id
    private Integer ausgabenbudget_id;

    private String kategorie;
    private Double betrag=0.0;
    private Double tatsaechlicheAusgaben;
    private Double differenz=0.0;
    //Symbol für Differenz -- + Gewinn  - Verlust
    private String s="0";
    private String bemerkungen;

    public Integer getAusgabenbudget_id() {
        return ausgabenbudget_id;
    }

    public void setAusgabenbudget_id(Integer ausgabenbudget_id) {
        this.ausgabenbudget_id = ausgabenbudget_id;
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

    public Double getTatsaechlicheAusgaben() {
        return tatsaechlicheAusgaben;
    }

    public void setTatsaechlicheAusgaben(Double tatsaechlicheAusgaben) {
        this.tatsaechlicheAusgaben = tatsaechlicheAusgaben;
    }

    public Double getDifferenz() {
        return differenz;
    }

    public void setDifferenz(Double differenz) {
        this.differenz = differenz;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getBemerkungen() {
        return bemerkungen;
    }

    public void setBemerkungen(String bemerkungen) {
        this.bemerkungen = bemerkungen;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.ausgabenbudget_id);
        hash = 29 * hash + Objects.hashCode(this.kategorie);
        hash = 29 * hash + Objects.hashCode(this.betrag);
        hash = 29 * hash + Objects.hashCode(this.tatsaechlicheAusgaben);
        hash = 29 * hash + Objects.hashCode(this.differenz);
        hash = 29 * hash + Objects.hashCode(this.s);
        hash = 29 * hash + Objects.hashCode(this.bemerkungen);
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
        final AusgabenBudget other = (AusgabenBudget) obj;
        if (!Objects.equals(this.kategorie, other.kategorie)) {
            return false;
        }
        if (!Objects.equals(this.s, other.s)) {
            return false;
        }
        if (!Objects.equals(this.bemerkungen, other.bemerkungen)) {
            return false;
        }
        if (!Objects.equals(this.ausgabenbudget_id, other.ausgabenbudget_id)) {
            return false;
        }
        if (!Objects.equals(this.betrag, other.betrag)) {
            return false;
        }
        if (!Objects.equals(this.tatsaechlicheAusgaben, other.tatsaechlicheAusgaben)) {
            return false;
        }
        if (!Objects.equals(this.differenz, other.differenz)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AusgabenBudget{" + "ausgabenbudget_id=" + ausgabenbudget_id + ", kategorie=" + kategorie + ", betrag=" + betrag + ", tatsaechlicheAusgaben=" + tatsaechlicheAusgaben + ", differenz=" + differenz + ", s=" + s + ", bemerkungen=" + bemerkungen + '}';
    }


    
    
}
