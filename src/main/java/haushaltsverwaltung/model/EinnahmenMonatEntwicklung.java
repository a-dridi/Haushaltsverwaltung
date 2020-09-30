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
@SequenceGenerator(name = "einnahmenmonat_seq", sequenceName = "einnahmenmonat_id_seq", allocationSize = 1)
public class EinnahmenMonatEntwicklung implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO, generator = "einnahmenmonat_seq")
    @Id
    private Integer einnahmen_id;

    private String monatjahr;
    private Double betrag;
    private String bemerkungen;

    public Integer getEinnahmen_id() {
        return einnahmen_id;
    }

    public void setEinnahmen_id(Integer einnahmen_id) {
        this.einnahmen_id = einnahmen_id;
    }

    public String getMonatjahr() {
        return monatjahr;
    }

    public void setMonatjahr(String monatjahr) {
        this.monatjahr = monatjahr;
    }

    public Double getBetrag() {
        return betrag;
    }

    public void setBetrag(Double betrag) {
        this.betrag = betrag;
    }

    public String getBemerkungen() {
        return bemerkungen;
    }

    public void setBemerkungen(String bemerkungen) {
        this.bemerkungen = bemerkungen;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.einnahmen_id);
        hash = 97 * hash + Objects.hashCode(this.monatjahr);
        hash = 97 * hash + Objects.hashCode(this.betrag);
        hash = 97 * hash + Objects.hashCode(this.bemerkungen);
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
        final EinnahmenMonatEntwicklung other = (EinnahmenMonatEntwicklung) obj;
        if (!Objects.equals(this.monatjahr, other.monatjahr)) {
            return false;
        }
        if (!Objects.equals(this.bemerkungen, other.bemerkungen)) {
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
        return "EinnahmenMonatEntwicklung{" + "einnahmen_id=" + einnahmen_id + ", monatjahr=" + monatjahr + ", betrag=" + betrag + ", bemerkungen=" + bemerkungen + '}';
    }
    
}
