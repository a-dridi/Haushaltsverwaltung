/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.main;

import java.util.Objects;

/**
 * Java Modell für Vergleich (Zuwachs vorher jetziger EuroWert) von
 * Kryptowaehrungen Woechentliche Gruppierung
 */
public class KryptowaehrungWaehrungWertGruppe {

    private String waehrung;
    private Double betrag;
    private Double wertineuroAlt;
    private Double wertineuroNeu;
    private Double zuwachsProzent;
    private boolean isZuwachs;
    private boolean isVerringerung;

    public boolean isIsVerringerung() {
        return isVerringerung;
    }

    public void setIsVerringerung(boolean isVerringerung) {
        this.isVerringerung = isVerringerung;
    }
    
    public String getWaehrung() {
        return waehrung;
    }

    public void setWaehrung(String waehrung) {
        this.waehrung = waehrung;
    }

    public Double getWertineuroAlt() {
        return wertineuroAlt;
    }

    public void setWertineuroAlt(Double wertineuroAlt) {
        this.wertineuroAlt = wertineuroAlt;
    }

    public Double getWertineuroNeu() {
        return wertineuroNeu;
    }

    public void setWertineuroNeu(Double wertineuroNeu) {
        this.wertineuroNeu = wertineuroNeu;
    }

    public Double getZuwachsProzent() {
        return zuwachsProzent;
    }

    public void setZuwachsProzent(Double zuwachsProzent) {
        this.zuwachsProzent = zuwachsProzent;
    }

   
    public boolean isIsZuwachs() {
        return isZuwachs;
    }

    public void setIsZuwachs(boolean isZuwachs) {
        this.isZuwachs = isZuwachs;
    }

    public Double getBetrag() {
        return betrag;
    }

    public void setBetrag(Double betrag) {
        this.betrag = betrag;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.waehrung);
        hash = 47 * hash + Objects.hashCode(this.betrag);
        hash = 47 * hash + Objects.hashCode(this.wertineuroAlt);
        hash = 47 * hash + Objects.hashCode(this.wertineuroNeu);
        hash = 47 * hash + Objects.hashCode(this.zuwachsProzent);
        hash = 47 * hash + (this.isZuwachs ? 1 : 0);
        hash = 47 * hash + (this.isVerringerung ? 1 : 0);
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
        final KryptowaehrungWaehrungWertGruppe other = (KryptowaehrungWaehrungWertGruppe) obj;
        if (this.isZuwachs != other.isZuwachs) {
            return false;
        }
        if (this.isVerringerung != other.isVerringerung) {
            return false;
        }
        if (!Objects.equals(this.waehrung, other.waehrung)) {
            return false;
        }
        if (!Objects.equals(this.betrag, other.betrag)) {
            return false;
        }
        if (!Objects.equals(this.wertineuroAlt, other.wertineuroAlt)) {
            return false;
        }
        if (!Objects.equals(this.wertineuroNeu, other.wertineuroNeu)) {
            return false;
        }
        if (!Objects.equals(this.zuwachsProzent, other.zuwachsProzent)) {
            return false;
        }
        return true;
    }

    
    
   

}
