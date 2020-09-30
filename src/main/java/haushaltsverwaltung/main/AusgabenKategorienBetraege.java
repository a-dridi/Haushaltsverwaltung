/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.main;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author A.Dridi
 */
public class AusgabenKategorienBetraege implements Serializable{
    
    
    private String bezeichnung;
    private Double betrag;

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public Double getBetrag() {
        return betrag;
    }

    public void setBetrag(Double betrag) {
        this.betrag = betrag;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + Objects.hashCode(this.bezeichnung);
        hash = 61 * hash + Objects.hashCode(this.betrag);
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
        final AusgabenKategorienBetraege other = (AusgabenKategorienBetraege) obj;
        if (!Objects.equals(this.bezeichnung, other.bezeichnung)) {
            return false;
        }
        if (!Objects.equals(this.betrag, other.betrag)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AusgabenKategorienBetraege{" + "bezeichnung=" + bezeichnung + ", betrag=" + betrag + '}';
    }
    
    
}
