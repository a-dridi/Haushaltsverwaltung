/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.model;

/**
 *
 * @author A.Dridi
 */
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
@SequenceGenerator(name = "kryptowaehrungenVermoegen_seq", sequenceName = "vermoeg_id_seq", allocationSize = 1)

public class KryptowaehrungenVermoegen implements Serializable {

    //Währungen können mehrmals vorkommen - Bei Verringerung wird der Betrag bearbeitet
    //Gleiche Währungen werden nach lagerort aufgeteilt
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "kryptowaehrungenVermoegen_seq")
    @Id
    private Integer id;
    private String waehrung;
    private Float betrag;
    private String lagerort;
    private Float wertInEuro;
    @Column(length = 10000)
    private String bemerkungen;
    private boolean anhang = false;
    private String anhangpfad;
    private String anhangname;
    private String anhangtype;
    private boolean deleted;

    public KryptowaehrungenVermoegen() {

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

    public String getWaehrung() {
        return waehrung;
    }

    public void setWaehrung(String waehrung) {
        this.waehrung = waehrung;
    }

    public Float getBetrag() {
        return betrag;
    }

    public void setBetrag(Float betrag) {
        this.betrag = betrag;
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

    public Float getWertInEuro() {
        return wertInEuro;
    }

    public void setWertInEuro(Float wertInEuro) {
        this.wertInEuro = wertInEuro;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.id);
        hash = 31 * hash + Objects.hashCode(this.waehrung);
        hash = 31 * hash + Objects.hashCode(this.betrag);
        hash = 31 * hash + Objects.hashCode(this.lagerort);
        hash = 31 * hash + Objects.hashCode(this.bemerkungen);
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
        final KryptowaehrungenVermoegen other = (KryptowaehrungenVermoegen) obj;
        if (!Objects.equals(this.waehrung, other.waehrung)) {
            return false;
        }
        if (!Objects.equals(this.lagerort, other.lagerort)) {
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
        return "KryptowaehrungenVermoegen{" + "id=" + id + ", waehrung=" + waehrung + ", betrag=" + betrag + ", lagerort=" + lagerort + ", bemerkungen=" + bemerkungen + ", anhang=" + anhang + '}';
    }

}
