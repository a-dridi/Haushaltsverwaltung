/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.converter;
import haushaltsverwaltung.db.DAO;
import haushaltsverwaltung.model.KryptowaehrungenWaehrungen;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author A.Dridi
 */
@FacesConverter("kryptowaehrungenWaehrungConverter")
public class KryptowaehrungenWaehrungConverter implements Converter{
     @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        if (value == null || value.trim().isEmpty()) {
            return "";

        } else {

            DAO dao = new DAO();
            List<KryptowaehrungenWaehrungen> liste = dao.getAllKryptowaehrungenWaehrungen();
            KryptowaehrungenWaehrungen kategorieAusgewaehlt = null;

            for (KryptowaehrungenWaehrungen a : liste) {
                if ((a.getWaehrungsname()).equals(value)) {
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
