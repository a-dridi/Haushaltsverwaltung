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

/**
 * Kryptowaehrungen WerteVerzeichnis um Kryptowaehrungen nach Zeitpunkten (z.b.:
 * Monatlich) mit den Wert in Euro zu gruppieren
 *
 */
@Entity
@SequenceGenerator(name = "kryptowerteverz_seq", sequenceName = "kryptowerteverz_id_seq", allocationSize = 1)
public class KryptowaehrungenWerteVerz implements Serializable {

    @GeneratedValue(generator = "kryptowerteverz_seq")
    @Id
    private Integer id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date datum;
    private String waehrung;
    private Float betrag;
    private String lagerort;
    private Float wertineuro;

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

    public Float getWertineuro() {
        return wertineuro;
    }

    public void setWertineuro(Float wertineuro) {
        this.wertineuro = wertineuro;
    }

    public Float getBetrag() {
        return betrag;
    }

    public void setBetrag(Float betrag) {
        this.betrag = betrag;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.id);
        hash = 29 * hash + Objects.hashCode(this.datum);
        hash = 29 * hash + Objects.hashCode(this.waehrung);
        hash = 29 * hash + Objects.hashCode(this.lagerort);
        hash = 29 * hash + Objects.hashCode(this.wertineuro);
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
        final KryptowaehrungenWerteVerz other = (KryptowaehrungenWerteVerz) obj;
        if (!Objects.equals(this.datum, other.datum)) {
            return false;
        }
        if (!Objects.equals(this.waehrung, other.waehrung)) {
            return false;
        }
        if (!Objects.equals(this.lagerort, other.lagerort)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.wertineuro, other.wertineuro)) {
            return false;
        }
        return true;
    }

}
