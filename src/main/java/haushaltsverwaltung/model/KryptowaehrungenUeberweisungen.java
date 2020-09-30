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
@SequenceGenerator(name = "kryptowaehrungenUeberweisungen_seq", sequenceName = "ueberweisungen_id_seq", allocationSize = 1)
public class KryptowaehrungenUeberweisungen implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO, generator = "kryptowaehrungenUeberweisungen_seq")
    @Id
    private Integer id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date datum;

    private String sender;
    private Float betrag;
    private String waehrung;
    private String empfaenger;
    private String zustand;
    @Column(length = 10000)
    private String bemerkungen;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Float getBetrag() {
        return betrag;
    }

    public void setBetrag(Float betrag) {
        this.betrag = betrag;
    }

    public String getWaehrung() {
        return waehrung;
    }

    public void setWaehrung(String waehrung) {
        this.waehrung = waehrung;
    }

    public String getEmpfaenger() {
        return empfaenger;
    }

    public void setEmpfaenger(String empfaenger) {
        this.empfaenger = empfaenger;
    }

    public String getZustand() {
        return zustand;
    }

    public void setZustand(String zustand) {
        this.zustand = zustand;
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

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.id);
        hash = 41 * hash + Objects.hashCode(this.sender);
        hash = 41 * hash + Objects.hashCode(this.betrag);
        hash = 41 * hash + Objects.hashCode(this.waehrung);
        hash = 41 * hash + Objects.hashCode(this.empfaenger);
        hash = 41 * hash + Objects.hashCode(this.zustand);
        hash = 41 * hash + Objects.hashCode(this.bemerkungen);
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
        final KryptowaehrungenUeberweisungen other = (KryptowaehrungenUeberweisungen) obj;
        if (!Objects.equals(this.sender, other.sender)) {
            return false;
        }
        if (!Objects.equals(this.waehrung, other.waehrung)) {
            return false;
        }
        if (!Objects.equals(this.empfaenger, other.empfaenger)) {
            return false;
        }
        if (!Objects.equals(this.zustand, other.zustand)) {
            return false;
        }
        if (!Objects.equals(this.bemerkungen, other.bemerkungen)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.betrag, other.betrag)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "KryptowaehrungenUeberweisungen{" + "id=" + id + ", datum=" + datum + ", sender=" + sender + ", betrag=" + betrag + ", waehrung=" + waehrung + ", empfaenger=" + empfaenger + ", zustand=" + zustand + ", bemerkungen=" + bemerkungen + ", anhang=" + anhang + '}';
    }

}
