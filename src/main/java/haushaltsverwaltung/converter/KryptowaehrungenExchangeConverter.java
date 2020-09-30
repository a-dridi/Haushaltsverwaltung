/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.converter;

import haushaltsverwaltung.db.DAO;
import haushaltsverwaltung.model.KryptowaehrungenExchange;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.List;

/**
 *
 * @author A.Dridi
 */
@FacesConverter("kryptowaehrungenExchangeConverter")
public class KryptowaehrungenExchangeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";

        } else {

            DAO dao = new DAO();
            List<KryptowaehrungenExchange> liste = dao.getAllKryptowaehrungenExchange();
            KryptowaehrungenExchange kategorieAusgewaehlt = null;

            for (KryptowaehrungenExchange a : liste) {
                if ((a.getExchangeName()).equals(value)) {
                    kategorieAusgewaehlt = a;
                }
            }
            return kategorieAusgewaehlt;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        } else {

            return value.toString();
        }
    }

}
