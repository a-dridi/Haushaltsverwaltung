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
import javax.persistence.Temporal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GenerationType;

/**
 *
 * @author A.Dridi
 */
@Entity
@SequenceGenerator(name = "kryptowaehrungenKaufVerkauf_seq", sequenceName = "kaufverkauf_id_seq", allocationSize = 1)
public class KryptowaehrungenKaufVerkauf implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO, generator = "kryptowaehrungenKaufVerkauf_seq")
    @Id
    private Integer id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date datum;
    private String vorgang; //Kauf  od. Verkauf

//Beides inkl. deren Währung
    private Float ausgangsbetrag;
    private String ausgangswaehrung;

    private Float endbetrag;
    private String endwaehrung;

    private Double wertineuro;
    private String exchange;
    @Column(length = 10000)
    private String bemerkungen;

    private boolean anhang = false;
    private String anhangpfad;
    private String anhangname;
    private String anhangtype;
    private boolean deleted;

    public KryptowaehrungenKaufVerkauf() {

    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

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

    public Float getAusgangsbetrag() {
        return ausgangsbetrag;
    }

    public void setAusgangsbetrag(Float ausgangsbetrag) {
        this.ausgangsbetrag = ausgangsbetrag;
    }

    public String getAusgangswaehrung() {
        return ausgangswaehrung;
    }

    public void setAusgangswaehrung(String ausgangswaehrung) {
        this.ausgangswaehrung = ausgangswaehrung;
    }

    public Float getEndbetrag() {
        return endbetrag;
    }

    public void setEndbetrag(Float endbetrag) {
        this.endbetrag = endbetrag;
    }

    public String getEndwaehrung() {
        return endwaehrung;
    }

    public void setEndwaehrung(String endwaehrung) {
        this.endwaehrung = endwaehrung;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getBemerkungen() {
        return bemerkungen;
    }

    public void setBemerkungen(String bemerkungen) {
        this.bemerkungen = bemerkungen;
    }

    public String getVorgang() {
        return vorgang;
    }

    public void setVorgang(String vorgang) {
        this.vorgang = vorgang;
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

    public Double getWertineuro() {
        return wertineuro;
    }

    public void setWertineuro(Double wertineuro) {
        this.wertineuro = wertineuro;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
        hash = 37 * hash + Objects.hashCode(this.datum);
        hash = 37 * hash + Objects.hashCode(this.vorgang);
        hash = 37 * hash + Objects.hashCode(this.ausgangsbetrag);
        hash = 37 * hash + Objects.hashCode(this.ausgangswaehrung);
        hash = 37 * hash + Objects.hashCode(this.endbetrag);
        hash = 37 * hash + Objects.hashCode(this.endwaehrung);
        hash = 37 * hash + Objects.hashCode(this.exchange);
        hash = 37 * hash + Objects.hashCode(this.bemerkungen);
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
        final KryptowaehrungenKaufVerkauf other = (KryptowaehrungenKaufVerkauf) obj;
        if (!Objects.equals(this.datum, other.datum)) {
            return false;
        }
        if (!Objects.equals(this.vorgang, other.vorgang)) {
            return false;
        }
        if (!Objects.equals(this.ausgangswaehrung, other.ausgangswaehrung)) {
            return false;
        }
        if (!Objects.equals(this.endwaehrung, other.endwaehrung)) {
            return false;
        }
        if (!Objects.equals(this.exchange, other.exchange)) {
            return false;
        }
        if (!Objects.equals(this.bemerkungen, other.bemerkungen)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.ausgangsbetrag, other.ausgangsbetrag)) {
            return false;
        }
        if (!Objects.equals(this.endbetrag, other.endbetrag)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "KryptowaehrungenKaufVerkauf{" + "id=" + id + ", datum=" + datum + ", vorgang=" + vorgang + ", ausgangsbetrag=" + ausgangsbetrag + ", ausgangswaehrung=" + ausgangswaehrung + ", endbetrag=" + endbetrag + ", endwaehrung=" + endwaehrung + ", exchange=" + exchange + ", bemerkungen=" + bemerkungen + '}';
    }

}
