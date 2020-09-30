/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.db;

import haushaltsverwaltung.model.Ausgaben;
import haushaltsverwaltung.model.AusgabenBudget;
import haushaltsverwaltung.model.Ausgabenausgabezeitraum;
import haushaltsverwaltung.model.Ausgabenkategorie;
import haushaltsverwaltung.model.Appsettings;
import haushaltsverwaltung.model.Buecher;
import haushaltsverwaltung.model.Buecherkategorie;
import haushaltsverwaltung.model.Buecherzustand;
import haushaltsverwaltung.model.DatenbankNotizen;
import haushaltsverwaltung.model.Einnahmen;
import haushaltsverwaltung.model.EinnahmenJahrEntwicklung;
import haushaltsverwaltung.model.EinnahmenKategorie;
import haushaltsverwaltung.model.EinnahmenMonatEntwicklung;
import haushaltsverwaltung.model.Handel;
import haushaltsverwaltung.model.KryptowaehrungenExchange;
import haushaltsverwaltung.model.KryptowaehrungenKaufVerkauf;
import haushaltsverwaltung.model.KryptowaehrungenUeberweisungen;
import haushaltsverwaltung.model.KryptowaehrungenVermoegen;
import haushaltsverwaltung.model.KryptowaehrungenVorgang;
import haushaltsverwaltung.model.KryptowaehrungenWaehrungen;
import haushaltsverwaltung.model.KryptowaehrungenWerteVerz;
import haushaltsverwaltung.model.MedienMusik;
import haushaltsverwaltung.model.MedienMusikGenre;
import haushaltsverwaltung.model.MedienSoftware;
import haushaltsverwaltung.model.MedienSoftwareBetriebssystem;
import haushaltsverwaltung.model.MedienSoftwareHersteller;
import haushaltsverwaltung.model.MedienVideoclips;
import haushaltsverwaltung.model.MedienVideoclipsSprache;
import haushaltsverwaltung.model.MedienVideos;
import haushaltsverwaltung.model.MedienVideosGenre;
import haushaltsverwaltung.model.MedienVideosSprache;
import haushaltsverwaltung.model.Ordnung;
import haushaltsverwaltung.model.Ordnungkategorie;
import haushaltsverwaltung.model.Reserven;
import haushaltsverwaltung.model.ReservenKategorie;
import haushaltsverwaltung.model.ReservenWaehrung;
import haushaltsverwaltung.model.Sparen;
import haushaltsverwaltung.model.SparenHaeufigkeit;
import haushaltsverwaltung.model.Vermoegen;
import haushaltsverwaltung.model.VermoegenJaehrlich;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

/**
 *
 * @author A.Dridi
 */
public class DAO implements AutoCloseable, Serializable {

    public DAO() {

    }

    public boolean insertKryptowaehrungenVorgang(KryptowaehrungenVorgang v) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(v);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Vorgang wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertKryptowaehrungenVorgang: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertEinnahmen(Einnahmen v) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(v);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertEinnahmen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertEinnahmenKategorie(EinnahmenKategorie v) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(v);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertEinnahmenKategorie: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertKryptowaehrungenExchange(KryptowaehrungenExchange v) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(v);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Exchange wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertKryptowaehrungenExchange: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertDatenbankNotizen(DatenbankNotizen v) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(v);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Notiz wurde gespeichert.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertKryptowaehrungenVorgang: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Notiz SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertKryptowaehrungenKaufVerkauf(KryptowaehrungenKaufVerkauf v) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(v);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", "! BITTE AUCH IM KRYPTOWÄHRUNG VERMÖGEN SPEICHERN!!!"));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertKryptowaehrungenKaufVerkauf: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertKryptowaehrungenUeberweisungen(KryptowaehrungenUeberweisungen v) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(v);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertKryptowaehrungenUeberweisungen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertKryptowaehrungenWerteVerz(KryptowaehrungenWerteVerz v) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(v);

            tx.commit();
            ret = true;
            //FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertKryptowaehrungenWerteVerz: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertKryptowaehrungenVermoegen(KryptowaehrungenVermoegen v) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(v);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in KryptowaehrungenVermoegen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertKryptowaehrungenWaehrungen(KryptowaehrungenWaehrungen b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Die Kategorie wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertBuecher: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertReservenWaehrung(ReservenWaehrung b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Die Waehrung wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertReservenWaehrung: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertReservenKategorie(ReservenKategorie b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Die Kategorie wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertReservenKategorie: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertReserven(Reserven b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertReserven: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteReserven(Reserven b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            Reserven bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Der Datensatz wurde gelöscht. BITTE SEITE NEULADEN!", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteReserven: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteReservenKategorie(ReservenKategorie b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Die Kategorie wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteReservenKategorie: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteReservenWaehrung(ReservenWaehrung b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Die Waehrung wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteReservenKategorie: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteKryptowaehrungenExchange(KryptowaehrungenExchange b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Die Kategorie wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteKryptowaehrungenExchange: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteDatenbankNotizen(DatenbankNotizen b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Die Notiz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteDatenbankNotiz: " + ex);
            // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Notiz SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }
        return ret;
    }

    public boolean deleteKryptowaehrungenKaufVerkauf(KryptowaehrungenKaufVerkauf b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            KryptowaehrungenKaufVerkauf bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteKryptowaehrungenKaufVerkauf: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteKryptowaehrungenUeberweisungen(KryptowaehrungenUeberweisungen b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            KryptowaehrungenUeberweisungen bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteKryptowaehrungenUeberweisungen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteKryptowaehrungenVermoegen(KryptowaehrungenVermoegen b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            KryptowaehrungenVermoegen bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteKryptowaehrungenVermoegen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteKryptowaehrungenVorgang(KryptowaehrungenVorgang b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Die Kategorie wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteKryptowaehrungenVorgang: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteKryptowaehrungenWaehrungen(KryptowaehrungenWaehrungen b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Waehrung wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteKryptowaehrungenWaehrungen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertBuecher(Buecher b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertBuecher: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteEinnahmen(Einnahmen b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            Einnahmen bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteEinnahmen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteEinnahmenKategorie(EinnahmenKategorie b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteEinnahmenKategorie: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteBuecher(Buecher b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            Buecher bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteBuecher: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    /**
     * AusgabenBudget hinzufügen - Überprüft auch ob BudgetKategorie nicht schon
     * existiert.
     *
     * @param a
     * @return
     */
    public boolean insertAusgabenBudget(AusgabenBudget a) {

        Session s = HibernateUtil.getSessionFactory().openSession();
        List<AusgabenBudget> abCheck;
        Transaction tx = null;
        boolean ret = false;
        try {
            Query qu = s.createQuery("FROM AusgabenBudget where kategorie=:katBez");
            qu.setString("katBez", a.getKategorie());
            abCheck = qu.list();
            //Wenn AusgabenKategorie  in AusgabenBudget nicht existiert, dann wird neue Kategorie gespeichert.
            if (abCheck.isEmpty()) {
                tx = s.beginTransaction();
                s.save(a);
                tx.commit();
            }
            ret = true;
            //FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertAusgabenBudget: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    /**
     * Methode für updateAusgabenBudgetData() - Nicht einzeln ohne die davor
     * genannte Methode verwenden. AusgabenBudget hinzufügen (Kategorie) oder
     * aktualisieren (wenn existent) - Überprüft auch ob BudgetKategorie
     * existiert.
     *
     * @param a
     * @return
     */
    public boolean insertOrUpdateAusgabenBudget(AusgabenBudget a) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        List<AusgabenBudget> abCheck;
        List<Ausgabenkategorie> ausgabeKat;

        Transaction tx = null;
        boolean ret = false;
        try {
            Query qu = s.createQuery("FROM Ausgabenkategorie order by kategoriebezeichnung asc");
            ausgabeKat = qu.list();
            tx = s.beginTransaction();

            s.saveOrUpdate(a);
            tx.commit();

            /*
            for (Ausgabenkategorie kat : ausgabeKat) {

                qu = s.createQuery("FROM AusgabenBudget where kategorie=:katBez order by kategorie asc");
                qu.setString("katBez", kat.getKategoriebezeichnung());
                abCheck = qu.list();
                //Wenn AusgabenKategorie  in AusgabenBudget nicht existiert, dann wird neue Kategorie gespeichert.

                if (abCheck.isEmpty()) {
                    tx = s.beginTransaction();
                    s.save(a);
                    tx.commit();
                } else {
                    tx = s.beginTransaction();
                    s.update(a);
                    tx.commit();
                }

            }
             */
            ret = true;

            //FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));
        } catch (HibernateException ex) {
            System.out.println("Fehler in insertOrUpdateAusgabenBudget: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertSparen(Sparen a) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(a);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Ein neues Sparkonto wurde eröffnet.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertSparen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;

    }

    public boolean insertAusgaben(Ausgaben a) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(a);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertAusgaben: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertEinnahmenMonatEntwicklung(EinnahmenMonatEntwicklung a) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(a);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertEinnahmenMonatEntwicklung: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;

    }

    public boolean insertEinnahmenJahrEntwicklung(EinnahmenJahrEntwicklung a) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(a);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Neuer Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertEinnahmenJahrEntwicklung: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;

    }

    public boolean deleteAusgaben(Ausgaben b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            Ausgaben bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteAusgaben: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteSparen(Sparen b) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {
            tx = s.beginTransaction();
            Sparen bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteAusgaben: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }
        return ret;
    }

    public boolean deleteAusgabenBudgetByKategorie(Ausgabenkategorie a) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            Query qu = s.createQuery("FROM AusgabenBudget where kategorie=:katbez order by ausgabenbudget_id asc");
            qu.setString("katbez", a.getKategoriebezeichnung());
            AusgabenBudget ab = (AusgabenBudget) qu.list().get(0);
            s.delete(ab);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteAusgabenBudget: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteAusgabenBudget(AusgabenBudget a) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(a);

            tx.commit();
            ret = true;
            //FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteAusgabenBudget: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertOrdnung(Ordnung o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(o);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertOrdnung: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteOrdnung(Ordnung b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            Ordnung bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteOrdnung: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertAusgabenkategorie(Ausgabenkategorie o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(o);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Neuer Eintrag für die Kategorie Ausgabenkategorie wurde erfolgreich gespeichert", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertAusgabenkategorie: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteAusgabenkategorie(Ausgabenkategorie b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Kategorie wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteAusgabenkategorie: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertOrdnungkategorie(Ordnungkategorie o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(o);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Neuer Eintrag für die Kategorie Ordnungkategorie wurde erfolgreich gespeichert", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertOrdnungkategorie: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertVermoegen(Vermoegen o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(o);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertVermoegen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertVermoegenJaehrlich(VermoegenJaehrlich o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(o);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Neues jährliches Vermögen wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertVermoegenJaehrlich: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteOrdnungkategorie(Ordnungkategorie b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Kategorie wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteOrdnungkategorie: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteBuecherkategorie(Buecherkategorie b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Kategorie wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteBuecherkategorie: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteBuecherzustand(Buecherzustand b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Kategorie wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteBuecherzustand: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteVermoegen(Vermoegen b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            Vermoegen bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteVermoegen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteVermoegenJaehrlich(VermoegenJaehrlich b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            VermoegenJaehrlich bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteVermoegenJaehrlich: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertAusgabenausgabezeitraum(Ausgabenausgabezeitraum o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(o);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Neuer Eintrag für die Kategorie Ausgabezeitraum wurde erfolgreich gespeichert", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertAusgabenausgabezeitraum: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteAusgabenausgabezeitraum(Ausgabenausgabezeitraum b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Kategorie wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteAusgabenausgabezeitraum: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertBuecherkategorie(Buecherkategorie o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(o);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Neuer Eintrag für die Kategorie Buecherkategorie wurde erfolgreich gespeichert", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertBuecherkategorie: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertBuecherzustand(Buecherzustand o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(o);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Neuer Eintrag für die Kategorie Buecherzustand wurde erfolgreich gespeichert", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertBuecherzustand: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateReserven(Reserven b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(b);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateReserven: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateReservenKategorie(ReservenKategorie b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(b);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateReservenKategorie: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateReservenWaehrung(ReservenWaehrung b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(b);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateReservenWaehrung: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateBuecher(Buecher b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(b);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateBuecher: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateEinnahmen(Einnahmen a) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(a);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateEinnahmen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateEinnahmenKategorie(EinnahmenKategorie a) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(a);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateEinnahmenKategorie: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateAusgaben(Ausgaben a) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(a);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateAusgaben: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateAusgabenBudget(AusgabenBudget a) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(a);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateAusgabenBudget: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    /**
     * Liest alle Ausgaben (in monatlicher Anrechnung) neu ein und
     * fügt/aktualisiert diese in der AusgabenBudget-Tabelle. Damit werden
     * neue/aktualisierte Beträgte und //Kategorien hinzugefügt/aktualisiert und
     * einmalige Ausgaben (die das laufende Jahr) betreffen werden auch
     * hinzugefügt.
     */
    public void updateAusgabenBudgetDataMonatAlle() {
        DAO dao = new DAO();
        Session s = HibernateUtil.getSessionFactory().openSession();
        try {

            //Ausgaben-Kategorien laden
            List<Ausgabenkategorie> kategorieList;
            //AusgabenBudget: Neue AusgabenBudget Kategorien erstellen - Kategorien von Ausgabenkategorie holen:
            Query qu = s.createQuery("FROM Ausgabenkategorie order by kategoriebezeichnung asc");
            kategorieList = qu.list();

            //Ausgaben-Kategorien (alle verfügbaren von Ausgabenkategorie) durchgehen 
            //AusgabenBudget-Datensätze aktualisieren und falls es neue Kategorien gibt neue AusgabenBudget-Datensätze hinzufügen
            for (int i = 0; i < kategorieList.size(); i++) {
                //Variable für AusgabenBudget Datensatz (wird neuhinzugefügt oder aktualisiert

                //AusgabenBudget Budget-Betrag für diese Kategorie laden (falls verfügbar)
                qu = s.createQuery("FROM AusgabenBudget where kategorie=:katBez");
                qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                List<AusgabenBudget> ausgabenBudgetList = qu.list();
                if (ausgabenBudgetList != null && !ausgabenBudgetList.isEmpty()) {
                    AusgabenBudget ab = ausgabenBudgetList.get(0);

                    //Heutiges Monat laden
                    Date d = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("MM");

                    //Alle Datensätze von AusgabenBudget laden, wird verwendet um BudgetBetrag abzurufen für die dazugehörige Kategorie
                    List<Double> ausgabenListe;
                    //Temp. Variable um Summe von Ausgaben-Betrag für AusgabenBudget-Kategorie zu bilden
                    Double betragSumme;
                    //Alle Ausgaben (Beträge jeder AusganenKategorie) von AusgabenTabelle in AusgabenBudget-Tabelle speichern
                    //AusgabenKategorien auslesen und AusgabenKateogrien und Ausgaben-Beträge in AusgabenBudget-Tabelle aktualisieren oder neu erstellen (wenn nicht existent)

                    betragSumme = 0.0;

                    //Tägliche Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='taeglich' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();

                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    //Auf 2 Nachkommastellen runden.
                    try {
                        betragSumme += Math.round(((ausgabenListe.get(0) * 365) / 12) * 100.0) / 100.0;

                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //Monatliche Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='monatlich' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();

                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    try {
                        betragSumme += ausgabenListe.get(0);
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //wöchentliche Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='woechentlich' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();

                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    try {
                        betragSumme += Math.round((ausgabenListe.get(0) * 4) * 100.0) / 100.0;
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //14-tägige Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='14-taegig' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();
                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    try {
                        betragSumme += Math.round((ausgabenListe.get(0) * 2) * 100.0) / 100.0;
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //Alle 2 Monate: Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='alle 2 Monate' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();
                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    try {
                        betragSumme += Math.round((ausgabenListe.get(0) / 2) * 100.0) / 100.0;
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //Alle 3 Monate, vierteljährlich: Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='vierteljaehrlich' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();
                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    try {
                        betragSumme += Math.round((ausgabenListe.get(0) / 3) * 100.0) / 100.0;
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //Alle 6 Monate, halbjährlich: Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='alle 6 Monate' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();
                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    try {
                        betragSumme += Math.round((ausgabenListe.get(0) / 6) * 100.0) / 100.0;
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //Jährlich: Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='jaehrlich' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();
                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    try {
                        betragSumme += Math.round((ausgabenListe.get(0) / 12) * 100.0) / 100.0;
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //Alle 2 Jahre: Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='alle 2 Jahre' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();
                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    try {
                        betragSumme += Math.round((ausgabenListe.get(0) / 24) * 100.0) / 100.0;
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //Alle 5 Jahre: Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='alle 5 Jahre' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();
                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    try {
                        betragSumme += Math.round((ausgabenListe.get(0) / 60) * 100.0) / 100.0;
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //alle einmaligen dieses Monats:
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where (deleted=false and kategorie=:katBez) and ((EXTRACT(year FROM zahlungsdatum) = :jahrWert and EXTRACT(month FROM zahlungsdatum) = :monatWert) and ausgabezeitraum='einmalig')");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    qu.setInteger("monatWert", Integer.parseInt(sdf.format(d)));
                    sdf = new SimpleDateFormat("yyyy");
                    qu.setInteger("jahrWert", Integer.parseInt(sdf.format(d)));
                    ausgabenListe = qu.list();
                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    try {
                        betragSumme += ausgabenListe.get(0);
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //Differenz von Budget-Betrag und Betrag der Ausgaben(von Ausgaben-Tabelle) einer Ausgaben-Kategorie berechnen
                    try {
                        ab.setTatsaechlicheAusgaben(Double.parseDouble(String.format(Locale.US, "%.2f", betragSumme)));
                        ab.setDifferenz(Double.parseDouble(String.format(Locale.US, "%.2f", (ab.getBetrag() - ab.getTatsaechlicheAusgaben()))));
                        if (ab.getDifferenz() > 0) {
                            ab.setS("+");
                        } else if (ab.getDifferenz() == 0) {
                            ab.setS(" ");
                        } else {
                            ab.setS("-");
                        }
                    } catch (NullPointerException e) {
                    }
                    //AusgabenBudget-Kategorie (entspricht einem Datensatz) speichern
                    dao.insertOrUpdateAusgabenBudget(ab);

                } else {
                    AusgabenBudget ab = new AusgabenBudget();
                    ab.setKategorie(kategorieList.get(i).getKategoriebezeichnung());
                    dao.insertOrUpdateAusgabenBudget(ab);

                }
            }

        } catch (Exception e) {
            System.out.println("Fehler in updateAusgabenBudgetDataMonatAlle: " + e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! updateAusgabenBudgetData ", e.toString()));

        } finally {
            s.close();
        }
    }

    /**
     * AusgabenBudget: Jaehrliche Ansicht und Anrechnung - Alle Ausgaben für das
     * laufende Jahr
     */
    public List<AusgabenBudget> getAusgabenBudgetDataJahrAlle(int jahr) {
        List<AusgabenBudget> ausgabenBudgetJaehrlichList = new ArrayList<>();

        if (jahr >= 2000 && jahr <= 2999) {
            DAO dao = new DAO();
            Session s = HibernateUtil.getSessionFactory().openSession();

            try {

                //Ausgaben-Kategorien laden
                List<Ausgabenkategorie> kategorieList;
                //AusgabenBudget: Neue AusgabenBudget Kategorien erstellen - Kategorien von Ausgabenkategorie holen:
                Query qu = s.createQuery("FROM Ausgabenkategorie order by kategoriebezeichnung asc");
                kategorieList = qu.list();

                //Ausgaben-Kategorien (alle verfügbaren von Ausgabenkategorie) durchgehen 
                //AusgabenBudget-Datensätze aktualisieren und falls es neue Kategorien gibt neue AusgabenBudget-Datensätze hinzufügen
                for (int i = 0; i < kategorieList.size(); i++) {
                    //Variable für AusgabenBudget Datensatz (wird neuhinzugefügt oder aktualisiert

                    //AusgabenBudget Budget-Betrag für diese Kategorie laden (falls verfügbar)
                    qu = s.createQuery("FROM AusgabenBudget where kategorie=:katBez");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    List<AusgabenBudget> ausgabenBudgetList = qu.list();
                    if (ausgabenBudgetList != null && !ausgabenBudgetList.isEmpty()) {
                        AusgabenBudget ab = ausgabenBudgetList.get(0);

                        //Gewähltes Jahr laden
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

                        //Alle Datensätze von AusgabenBudget laden, wird verwendet um BudgetBetrag abzurufen für die dazugehörige Kategorie
                        List<Double> ausgabenListe;
                        //Temp. Variable um Summe von Ausgaben-Betrag für AusgabenBudget-Kategorie zu bilden
                        Double betragSumme;
                        //Alle Ausgaben (Beträge jeder AusganenKategorie) von AusgabenTabelle in AusgabenBudget-Tabelle speichern
                        //AusgabenKategorien auslesen und AusgabenKateogrien und Ausgaben-Beträge in AusgabenBudget-Tabelle aktualisieren oder neu erstellen (wenn nicht existent)

                        betragSumme = 0.0;

                        //Tägliche Ausgaben holen und in AusgabenBudget speichern
                        qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='taeglich' and kategorie=:katBez)");
                        qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                        //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                        ausgabenListe = qu.list();

                        //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                        //Auf 2 Nachkommastellen runden.
                        try {
                            betragSumme += Math.round(((ausgabenListe.get(0) * 365)) * 100.0) / 100.0;

                        } catch (NullPointerException e) {
                            betragSumme += 0.0;
                        }

                        //Monatliche Ausgaben holen und in AusgabenBudget speichern
                        qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='monatlich' and kategorie=:katBez)");
                        qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                        //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                        ausgabenListe = qu.list();

                        //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                        try {
                            betragSumme += Math.round(((ausgabenListe.get(0) * 12)) * 100.0) / 100.0;
                        } catch (NullPointerException e) {
                            betragSumme += 0.0;
                        }

                        //wöchentliche Ausgaben holen und in AusgabenBudget speichern
                        qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='woechentlich' and kategorie=:katBez)");
                        qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                        //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                        ausgabenListe = qu.list();

                        //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                        try {
                            betragSumme += Math.round((ausgabenListe.get(0) * 52) * 100.0) / 100.0;
                        } catch (NullPointerException e) {
                            betragSumme += 0.0;
                        }

                        //14-tägige Ausgaben holen und in AusgabenBudget speichern
                        qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='14-taegig' and kategorie=:katBez)");
                        qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                        //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                        ausgabenListe = qu.list();
                        //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                        try {
                            betragSumme += Math.round((ausgabenListe.get(0) * 24) * 100.0) / 100.0;
                        } catch (NullPointerException e) {
                            betragSumme += 0.0;
                        }

                        //Alle 2 Monate: Ausgaben holen und in AusgabenBudget speichern
                        qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='alle 2 Monate' and kategorie=:katBez)");
                        qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                        //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                        ausgabenListe = qu.list();
                        //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                        try {
                            betragSumme += Math.round((ausgabenListe.get(0) * 6) * 100.0) / 100.0;
                        } catch (NullPointerException e) {
                            betragSumme += 0.0;
                        }

                        //Alle 3 Monate, vierteljährlich: Ausgaben holen und in AusgabenBudget speichern
                        qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='vierteljaehrlich' and kategorie=:katBez)");
                        qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                        //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                        ausgabenListe = qu.list();
                        //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                        try {
                            betragSumme += Math.round((ausgabenListe.get(0) * 3) * 100.0) / 100.0;
                        } catch (NullPointerException e) {
                            betragSumme += 0.0;
                        }

                        //Alle 6 Monate, halbjährlich: Ausgaben holen und in AusgabenBudget speichern
                        qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='alle 6 Monate' and kategorie=:katBez)");
                        qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                        //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                        ausgabenListe = qu.list();
                        //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                        try {
                            betragSumme += Math.round((ausgabenListe.get(0) * 2) * 100.0) / 100.0;
                        } catch (NullPointerException e) {
                            betragSumme += 0.0;
                        }

                        //Jährlich: Ausgaben holen und in AusgabenBudget speichern
                        qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='jaehrlich' and kategorie=:katBez)");
                        qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                        //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                        ausgabenListe = qu.list();
                        //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                        try {
                            betragSumme += Math.round((ausgabenListe.get(0)) * 100.0) / 100.0;
                        } catch (NullPointerException e) {
                            betragSumme += 0.0;
                        }

                        //Alle 2 Jahre: Ausgaben holen und in AusgabenBudget speichern
                        qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='alle 2 Jahre' and kategorie=:katBez)");
                        qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                        //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                        ausgabenListe = qu.list();
                        //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                        try {
                            betragSumme += Math.round((ausgabenListe.get(0) / 2) * 100.0) / 100.0;
                        } catch (NullPointerException e) {
                            betragSumme += 0.0;
                        }

                        //Alle 5 Jahre: Ausgaben holen und in AusgabenBudget speichern
                        qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='alle 5 Jahre' and kategorie=:katBez)");
                        qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                        //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                        ausgabenListe = qu.list();
                        //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                        try {
                            betragSumme += Math.round((ausgabenListe.get(0) / 5) * 100.0) / 100.0;
                        } catch (NullPointerException e) {
                            betragSumme += 0.0;
                        }

                        //alle einmaligen dieses Monats:
                        qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where (deleted=false and kategorie=:katBez) and (EXTRACT(year FROM zahlungsdatum) = :jahrWert and ausgabezeitraum='einmalig')");
                        qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                        qu.setInteger("jahrWert", jahr);
                        ausgabenListe = qu.list();
                        //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                        try {
                            betragSumme += ausgabenListe.get(0);
                        } catch (NullPointerException e) {
                            betragSumme += 0.0;
                        }

                        //Differenz von Budget-Betrag und Betrag der Ausgaben(von Ausgaben-Tabelle) einer Ausgaben-Kategorie berechnen
                        try {
                            ab.setBetrag(ab.getBetrag() * 12);
                            ab.setTatsaechlicheAusgaben(Double.parseDouble(String.format(Locale.US, "%.2f", betragSumme)));
                            ab.setDifferenz(Double.parseDouble(String.format(Locale.US, "%.2f", (ab.getBetrag() - ab.getTatsaechlicheAusgaben()))));
                            if (ab.getDifferenz() > 0) {
                                ab.setS("+");
                            } else if (ab.getDifferenz() == 0) {
                                ab.setS(" ");
                            } else {
                                ab.setS("-");
                            }

                        } catch (NullPointerException e) {
                        }
                        //AusgabenBudget-Kategorie (entspricht einem Datensatz) speichern
                        ausgabenBudgetJaehrlichList.add(ab);

                    } else {
                        AusgabenBudget ab = new AusgabenBudget();
                        ab.setKategorie(kategorieList.get(i).getKategoriebezeichnung());
                        ausgabenBudgetJaehrlichList.add(ab);
                    }
                }

            } catch (Exception e) {
                System.out.println("Fehler in getAusgabenBudgetDataJahrAlle: " + e);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! getAusgabenBudgetDataJahrAlle: ", e.toString()));

            } finally {
                s.close();

            }
        }
        return ausgabenBudgetJaehrlichList;
    }

    /**
     * AusgabenBudget (alle Ausgaben) Datensätze für bestimmtes Monat und Jahr
     * anzeigen
     *
     * @param monat
     * @param jahr
     */
    public void updateAusgabenBudgetDataMonatAlleCustom(int monat, int jahr) {

        if (monat >= 1 && monat <= 12) {
            if (jahr >= 2000 && jahr <= 2999) {

                Date datum;
                SimpleDateFormat datumSelected = new SimpleDateFormat("MM.yyyy");
                try {
                    datum = datumSelected.parse(monat + "." + jahr);
                } catch (ParseException ex) {
                    FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Falsches Datum eingegeben: ", "" + ex));
                    datum = new Date();
                    Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
                }
                DAO dao = new DAO();
                Session s = HibernateUtil.getSessionFactory().openSession();
                try {

                    //Ausgaben-Kategorien laden
                    List<Ausgabenkategorie> kategorieList;
                    //AusgabenBudget: Neue AusgabenBudget Kategorien erstellen - Kategorien von Ausgabenkategorie holen:
                    Query qu = s.createQuery("FROM Ausgabenkategorie order by kategoriebezeichnung asc");
                    kategorieList = qu.list();

                    //Ausgaben-Kategorien (alle verfügbaren von Ausgabenkategorie) durchgehen 
                    //AusgabenBudget-Datensätze aktualisieren und falls es neue Kategorien gibt neue AusgabenBudget-Datensätze hinzufügen
                    for (int i = 0; i < kategorieList.size(); i++) {
                        //Variable für AusgabenBudget Datensatz (wird neuhinzugefügt oder aktualisiert

                        //AusgabenBudget Budget-Betrag für diese Kategorie laden (falls verfügbar)
                        qu = s.createQuery("FROM AusgabenBudget where kategorie=:katBez");
                        qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                        List<AusgabenBudget> ausgabenBudgetList = qu.list();
                        if (ausgabenBudgetList != null && !ausgabenBudgetList.isEmpty()) {
                            AusgabenBudget ab = ausgabenBudgetList.get(0);

                            SimpleDateFormat sdf = new SimpleDateFormat("MM");

                            //Alle Datensätze von AusgabenBudget laden, wird verwendet um BudgetBetrag abzurufen für die dazugehörige Kategorie
                            List<Double> ausgabenListe;
                            //Temp. Variable um Summe von Ausgaben-Betrag für AusgabenBudget-Kategorie zu bilden
                            Double betragSumme;
                            //Alle Ausgaben (Beträge jeder AusganenKategorie) von AusgabenTabelle in AusgabenBudget-Tabelle speichern
                            //AusgabenKategorien auslesen und AusgabenKateogrien und Ausgaben-Beträge in AusgabenBudget-Tabelle aktualisieren oder neu erstellen (wenn nicht existent)

                            betragSumme = 0.0;

                            //Tägliche Ausgaben holen und in AusgabenBudget speichern
                            qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='taeglich' and kategorie=:katBez)");
                            qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                            //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                            ausgabenListe = qu.list();

                            //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                            //Auf 2 Nachkommastellen runden.
                            try {
                                betragSumme += Math.round(((ausgabenListe.get(0) * 365) / 12) * 100.0) / 100.0;
                            } catch (NullPointerException e) {
                                betragSumme += 0.0;
                            }

                            //Monatliche Ausgaben holen und in AusgabenBudget speichern
                            qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='monatlich' and kategorie=:katBez)");
                            qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                            //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                            ausgabenListe = qu.list();

                            //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                            try {
                                betragSumme += ausgabenListe.get(0);
                            } catch (NullPointerException e) {
                                betragSumme += 0.0;
                            }

                            //wöchentliche Ausgaben holen und in AusgabenBudget speichern
                            qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='woechentlich' and kategorie=:katBez)");
                            qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                            //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                            ausgabenListe = qu.list();

                            //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                            try {
                                betragSumme += Math.round((ausgabenListe.get(0) * 4) * 100.0) / 100.0;
                            } catch (NullPointerException e) {
                                betragSumme += 0.0;
                            }

                            //14-tägige Ausgaben holen und in AusgabenBudget speichern
                            qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='14-taegig' and kategorie=:katBez)");
                            qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                            //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                            ausgabenListe = qu.list();
                            //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                            try {
                                betragSumme += Math.round((ausgabenListe.get(0) * 2) * 100.0) / 100.0;
                            } catch (NullPointerException e) {
                                betragSumme += 0.0;
                            }

                            //Alle 2 Monate: Ausgaben holen und in AusgabenBudget speichern
                            qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='alle 2 Monate' and kategorie=:katBez)");
                            qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                            //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                            ausgabenListe = qu.list();
                            //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                            try {
                                betragSumme += Math.round((ausgabenListe.get(0) / 2) * 100.0) / 100.0;
                            } catch (NullPointerException e) {
                                betragSumme += 0.0;
                            }

                            //Alle 3 Monate, vierteljährlich: Ausgaben holen und in AusgabenBudget speichern
                            qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='vierteljaehrlich' and kategorie=:katBez)");
                            qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                            //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                            ausgabenListe = qu.list();
                            //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                            try {
                                betragSumme += Math.round((ausgabenListe.get(0) / 3) * 100.0) / 100.0;
                            } catch (NullPointerException e) {
                                betragSumme += 0.0;
                            }

                            //Alle 6 Monate, halbjährlich: Ausgaben holen und in AusgabenBudget speichern
                            qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='alle 6 Monate' and kategorie=:katBez)");
                            qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                            //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                            ausgabenListe = qu.list();
                            //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                            try {
                                betragSumme += Math.round((ausgabenListe.get(0) / 6) * 100.0) / 100.0;
                            } catch (NullPointerException e) {
                                betragSumme += 0.0;
                            }

                            //Jährlich: Ausgaben holen und in AusgabenBudget speichern
                            qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='jaehrlich' and kategorie=:katBez)");
                            qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                            //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                            ausgabenListe = qu.list();
                            //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                            try {
                                betragSumme += Math.round((ausgabenListe.get(0) / 12) * 100.0) / 100.0;
                            } catch (NullPointerException e) {
                                betragSumme += 0.0;
                            }

                            //Alle 2 Jahre: Ausgaben holen und in AusgabenBudget speichern
                            qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='alle 2 Jahre' and kategorie=:katBez)");
                            qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                            //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                            ausgabenListe = qu.list();
                            //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                            try {
                                betragSumme += Math.round((ausgabenListe.get(0) / 24) * 100.0) / 100.0;
                            } catch (NullPointerException e) {
                                betragSumme += 0.0;
                            }

                            //Alle 5 Jahre: Ausgaben holen und in AusgabenBudget speichern
                            qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='alle 5 Jahre' and kategorie=:katBez)");
                            qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                            //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                            ausgabenListe = qu.list();
                            //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                            try {
                                betragSumme += Math.round((ausgabenListe.get(0) / 60) * 100.0) / 100.0;
                            } catch (NullPointerException e) {
                                betragSumme += 0.0;
                            }

                            //alle einmaligen dieses Monats:
                            qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where (deleted=false and kategorie=:katBez) and ((EXTRACT(year FROM zahlungsdatum) = :jahrWert and EXTRACT(month FROM zahlungsdatum) = :monatWert) and ausgabezeitraum='einmalig')");
                            qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                            qu.setInteger("monatWert", Integer.parseInt(sdf.format(datum)));
                            sdf = new SimpleDateFormat("yyyy");
                            qu.setInteger("jahrWert", Integer.parseInt(sdf.format(datum)));
                            ausgabenListe = qu.list();
                            //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                            try {
                                betragSumme += ausgabenListe.get(0);
                            } catch (NullPointerException e) {
                                betragSumme += 0.0;
                            }

                            //Differenz von Budget-Betrag und Betrag der Ausgaben(von Ausgaben-Tabelle) einer Ausgaben-Kategorie berechnen
                            try {
                                ab.setTatsaechlicheAusgaben(Double.parseDouble(String.format(Locale.US, "%.2f", betragSumme)));
                                ab.setDifferenz(Double.parseDouble(String.format(Locale.US, "%.2f", (ab.getBetrag() - ab.getTatsaechlicheAusgaben()))));
                                if (ab.getDifferenz() > 0) {
                                    ab.setS("+");
                                } else if (ab.getDifferenz() == 0) {
                                    ab.setS(" ");
                                } else {
                                    ab.setS("-");
                                }
                            } catch (NullPointerException e) {
                            }
                            //AusgabenBudget-Kategorie (entspricht einem Datensatz) speichern
                            dao.insertOrUpdateAusgabenBudget(ab);

                        } else {
                            AusgabenBudget ab = new AusgabenBudget();
                            ab.setKategorie(kategorieList.get(i).getKategoriebezeichnung());
                            dao.insertOrUpdateAusgabenBudget(ab);

                        }
                    }

                } catch (Exception e) {
                    System.out.println("Fehler in updateAusgabenBudgetDataMonatAlle: " + e);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! updateAusgabenBudgetData ", e.toString()));

                } finally {
                    s.close();
                }
            }
        }
    }

    /**
     * Liest alle Ausgaben (in monatlicher Anrechnung) - (nur regelmaessig) neu
     * ein und fügt/aktualisiert diese in der AusgabenBudget-Tabelle. Damit
     * werden neue/aktualisierte Beträgte und //Kategorien
     * hinzugefügt/aktualisiert und einmalige Ausgaben (die das laufende Jahr)
     * betreffen werden auch hinzugefügt.
     */
    public void updateAusgabenBudgetDataMonatRegelmaessig() {
        DAO dao = new DAO();
        Session s = HibernateUtil.getSessionFactory().openSession();
        try {

            //Ausgaben-Kategorien laden
            List<Ausgabenkategorie> kategorieList;
            //AusgabenBudget: Neue AusgabenBudget Kategorien erstellen - Kategorien von Ausgabenkategorie holen:
            Query qu = s.createQuery("FROM Ausgabenkategorie order by kategoriebezeichnung asc");
            kategorieList = qu.list();

            //Ausgaben-Kategorien (alle verfügbaren von Ausgabenkategorie) durchgehen 
            //AusgabenBudget-Datensätze aktualisieren und falls es neue Kategorien gibt neue AusgabenBudget-Datensätze hinzufügen
            for (int i = 0; i < kategorieList.size(); i++) {
                //Variable für AusgabenBudget Datensatz (wird neuhinzugefügt oder aktualisiert

                //AusgabenBudget Budget-Betrag für diese Kategorie laden (falls verfügbar)
                qu = s.createQuery("FROM AusgabenBudget where kategorie=:katBez");
                qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                List<AusgabenBudget> ausgabenBudgetList = qu.list();
                if (ausgabenBudgetList != null && !ausgabenBudgetList.isEmpty()) {
                    AusgabenBudget ab = ausgabenBudgetList.get(0);

                    //Heutiges Monat laden
                    Date d = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("MM");

                    //Alle Datensätze von AusgabenBudget laden, wird verwendet um BudgetBetrag abzurufen für die dazugehörige Kategorie
                    List<Double> ausgabenListe;
                    //Temp. Variable um Summe von Ausgaben-Betrag für AusgabenBudget-Kategorie zu bilden
                    Double betragSumme;
                    //Alle Ausgaben (Beträge jeder AusganenKategorie) von AusgabenTabelle in AusgabenBudget-Tabelle speichern
                    //AusgabenKategorien auslesen und AusgabenKateogrien und Ausgaben-Beträge in AusgabenBudget-Tabelle aktualisieren oder neu erstellen (wenn nicht existent)

                    betragSumme = 0.0;

                    //Tägliche Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='taeglich' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();

                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    //Auf 2 Nachkommastellen runden.
                    try {
                        betragSumme += Math.round(((ausgabenListe.get(0) * 365) / 12) * 100.0) / 100.0;
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //Monatliche Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='monatlich' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();

                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    try {
                        betragSumme += ausgabenListe.get(0);
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //wöchentliche Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='woechentlich' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();

                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    try {
                        betragSumme += Math.round((ausgabenListe.get(0) * 4) * 100.0) / 100.0;
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //14-tägige Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='14-taegig' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();
                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    try {
                        betragSumme += Math.round((ausgabenListe.get(0) * 2) * 100.0) / 100.0;
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //Alle 2 Monate: Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='alle 2 Monate' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();
                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    try {
                        betragSumme += Math.round((ausgabenListe.get(0) / 2) * 100.0) / 100.0;
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //Alle 3 Monate, vierteljährlich: Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='vierteljaehrlich' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();
                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    try {
                        betragSumme += Math.round((ausgabenListe.get(0) / 3) * 100.0) / 100.0;
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //Alle 6 Monate, halbjährlich: Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='alle 6 Monate' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();
                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    try {
                        betragSumme += Math.round((ausgabenListe.get(0) / 6) * 100.0) / 100.0;
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //Jährlich: Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='jaehrlich' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();
                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    try {
                        betragSumme += Math.round((ausgabenListe.get(0) / 12) * 100.0) / 100.0;
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //Alle 2 Jahre: Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='alle 2 Jahre' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();
                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    try {
                        betragSumme += Math.round((ausgabenListe.get(0) / 24) * 100.0) / 100.0;
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //Alle 5 Jahre: Ausgaben holen und in AusgabenBudget speichern
                    qu = s.createQuery("SELECT sum(betrag) FROM Ausgaben where deleted=false and (ausgabezeitraum='alle 5 Jahre' and kategorie=:katBez)");
                    qu.setString("katBez", kategorieList.get(i).getKategoriebezeichnung());
                    //Ausgabensumme für ein Ausgabenzeitraum und die betroffene Kategorie berechnen und für Tabelle AusgabenBudget verwenden.
                    ausgabenListe = qu.list();
                    //Bestehenden Betrag von Ausgaben holen und zum Betrag(Ausgabenbetrag) der Kategorie (von AusgabenBudget) dazu addieren.
                    try {
                        betragSumme += Math.round((ausgabenListe.get(0) / 60) * 100.0) / 100.0;
                    } catch (NullPointerException e) {
                        betragSumme += 0.0;
                    }

                    //Differenz von Budget-Betrag und Betrag der Ausgaben(von Ausgaben-Tabelle) einer Ausgaben-Kategorie berechnen
                    try {
                        ab.setTatsaechlicheAusgaben(Double.parseDouble(String.format(Locale.US, "%.2f", betragSumme)));
                        ab.setDifferenz(Double.parseDouble(String.format(Locale.US, "%.2f", (ab.getBetrag() - ab.getTatsaechlicheAusgaben()))));
                        if (ab.getDifferenz() > 0) {
                            ab.setS("+");
                        } else if (ab.getDifferenz() == 0) {
                            ab.setS(" ");
                        } else {
                            ab.setS("-");
                        }
                    } catch (NullPointerException e) {
                    }
                    //AusgabenBudget-Kategorie (entspricht einem Datensatz) speichern
                    dao.insertOrUpdateAusgabenBudget(ab);

                } else {
                    AusgabenBudget ab = new AusgabenBudget();
                    ab.setKategorie(kategorieList.get(i).getKategoriebezeichnung());
                    dao.insertOrUpdateAusgabenBudget(ab);

                }
            }

        } catch (Exception e) {
            System.out.println("Fehler in updateAusgabenBudgetDataMonatRegelmaessig: " + e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! updateAusgabenBudgetData ", e.toString()));

        } finally {
            s.close();
        }
    }

    public boolean updateEinnahmenMonatEntwicklung(EinnahmenMonatEntwicklung a) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(a);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateEinnahmenMonatEntwicklung: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateEinnahmenJahrEntwicklung(EinnahmenJahrEntwicklung a) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(a);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateEinnahmenJahrEntwicklung: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateOrdnung(Ordnung o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(o);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateOrdnung: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateKryptowaehrungenExchange(KryptowaehrungenExchange o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(o);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateKryptowaehrungenExchange: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateKryptowaehrungenKaufVerkauf(KryptowaehrungenKaufVerkauf o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(o);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateKryptowaehrungenKaufVerkauf: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateKryptowaehrungenUeberweisungen(KryptowaehrungenUeberweisungen o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(o);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateKryptowaehrungenUeberweisungen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateKryptowaehrungenVermoegen(KryptowaehrungenVermoegen o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(o);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateKryptowaehrungenVermoegen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateKryptowaehrungenWaehrungen(KryptowaehrungenWaehrungen o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(o);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateKryptowaehrungenWaehrungen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateKryptowaehrungenWerteVerz(KryptowaehrungenWerteVerz o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(o);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateKryptowaehrungenWerteVerz: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateDatenbankNotizen(DatenbankNotizen o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(o);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateDatenbankNotizen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateVermoegen(Vermoegen o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(o);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateVermoegen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateVermoegenJaehrlich(VermoegenJaehrlich o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(o);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateVermoegenJaehrlich: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public List<Reserven> getAllReserven() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Reserven where deleted=false order by reserven_id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllReserven: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<ReservenKategorie> getAllReservenKategorie() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM ReservenKategorie order by kategoriebezid asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllReservenKategorie: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<ReservenWaehrung> getAllReservenWaehrung() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM ReservenWaehrung order by id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllReservenWaehrung: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<Reserven> getSingleReserven(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Reserven where deleted=false and reserven_id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleReserven: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<Vermoegen> getAllVermoegen() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Vermoegen where deleted=false order by vermoegen_id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllVermoegen: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<Vermoegen> getSingleVermoegen(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Vermoegen where deleted=false and vermoegen_id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleVermoegen: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<VermoegenJaehrlich> getAllVermoegenJaehrlich() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM VermoegenJaehrlich where deleted=false order by vermoegenjaehrlich_id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllVermoegenJaehrlich: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<VermoegenJaehrlich> getSingleVermoegenJaehrlich(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM VermoegenJaehrlich where deleted=false and vermoegenjaehrlich_id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleVermoegenJaehrlich: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<Ordnungkategorie> getAllOrdnungskategorie() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Ordnungkategorie order by kategoriebezid asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllOrdnungskategorie: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<Ausgabenkategorie> getAllAusgabenkategorie() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Ausgabenkategorie order by ausgabenkategorieid asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllAusgabenkategorie: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<Ausgabenausgabezeitraum> getAllAusgabenausgabezeitraum() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Ausgabenausgabezeitraum order by zeitraumid asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllAusgabenausgabezeitraum: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<AusgabenBudget> getAllAusgabenBudget() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM AusgabenBudget order by ausgabenbudget_id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllAusgabenBudget: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<AusgabenBudget> getAllAusgabenBudgetOrderByKategorie() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM AusgabenBudget order by kategorie asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllAusgabenBudgetOrderByKategorie: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Anfang: Abfragen von fixierten Listen - "Domains" z.B.: Kategorien
     */
    public List<Buecherkategorie> getAllBuecherkategorie() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Buecherkategorie order by buecherkategorieid asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllBuecherkategorie: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<Buecherzustand> getAllBuecherzustand() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Buecherzustand order by zustandid asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllBuecherzustand: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    //ENDE
    /**
     * Liefert eine Liste aller Bücher als List
     *
     *
     */
    public List<Buecher> getAllBuecher() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Buecher where deleted=false order by buecher_id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllBuecher: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<Buecher> getSingleBuecher(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Buecher where deleted=false and buecher_id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleBuecher: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<Appsettings> getAllAppsettings() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Appsettings");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllAppsettings: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<KryptowaehrungenWaehrungen> getAllKryptowaehrungenWaehrungen() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM KryptowaehrungenWaehrungen order by waehrungsname asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllKryptowaehrungenWaehrungen: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<KryptowaehrungenExchange> getAllKryptowaehrungenExchange() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM KryptowaehrungenExchange order by exchangename asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllKryptowaehrungenExchange: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<DatenbankNotizen> getDatenbankNotiz(String tabelle) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM DatenbankNotizen where tabelle = :tabellenname");
            qu.setString("tabellenname", tabelle);
            return qu.list();

        } catch (Exception e) {
            //System.out.println("Fehler in getAllKryptowaehrungenNotiz: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<KryptowaehrungenUeberweisungen> getAllKryptowaehrungenUeberweisungen() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM KryptowaehrungenUeberweisungen where deleted=false order by id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllKryptowaehrungenUeberweisungen: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<KryptowaehrungenUeberweisungen> getSingleKryptowaehrungenUeberweisungen(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM KryptowaehrungenUeberweisungen where deleted=false and id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleKryptowaehrungenUeberweisungen: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<KryptowaehrungenVermoegen> getAllKryptowaehrungenVermoegen() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM KryptowaehrungenVermoegen where deleted=false order by id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllKryptowaehrungenVermoegen: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<KryptowaehrungenVermoegen> getSingleKryptowaehrungenVermoegen(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM KryptowaehrungenVermoegen where deleted=false and id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleKryptowaehrungenVermoegen: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<KryptowaehrungenVorgang> getAllKryptowaehrungenVorgang() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM KryptowaehrungenVorgang order by vorgangbeschreibung asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllKryptowaehrungenVorgang: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<KryptowaehrungenKaufVerkauf> getAllKryptowaehrungenKaufVerkauf() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM KryptowaehrungenKaufVerkauf where deleted=false order by id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllKryptowaehrungenExchange: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<KryptowaehrungenKaufVerkauf> getSingleKryptowaehrungenKaufVerkauf(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM KryptowaehrungenKaufVerkauf where deleted=false and id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleKryptowaehrungenKaufVerkauf: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<KryptowaehrungenWerteVerz> getAllKryptowaehrungenWerteVerz() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM KryptowaehrungenWerteVerz");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllKryptowaehrungenWerteVerz: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<KryptowaehrungenWerteVerz> getWaehrungAllKryptowaehrungenWerteVerz(String waehrung) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {
            Query qu = s.createQuery("FROM KryptowaehrungenWerteVerz where waehrung = :waehrungvariable");
            qu.setString("waehrungvariable", waehrung);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllKryptowaehrungenWerteVerz: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<KryptowaehrungenWerteVerz> getLagerortAllKryptowaehrungenWerteVerz(String lagerort) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {
            Query qu = s.createQuery("FROM KryptowaehrungenWerteVerz where lagerort = :lagerortvariable");
            qu.setString("lagerortvariable", lagerort);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllKryptowaehrungenWerteVerz: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Regelmäßige Einnahmen und einmalige Einnahmen aus diesem Jahr
     *
     * @return
     */
    public List<Einnahmen> getAllEinnahmen() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        List<Einnahmen> einnahmenListe;

        try {
            //regelmäßige Einnahmen und Einnahmen dieses Jahres:
            Query qu = s.createQuery("FROM Einnahmen where (deleted=false and (EXTRACT(year FROM eingangsdatum) = :jahrWert and haeufigkeit='einmalig')) or (deleted=false and haeufigkeit!='einmalig') order by einnahmen_id asc");
            qu.setInteger("jahrWert", Integer.parseInt(sdf.format(d)));
            einnahmenListe = qu.list();

            return einnahmenListe;

        } catch (Exception e) {
            System.out.println("Fehler in getAllEinnahmen: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Alle in der DB Einnahmen gespeicherten Datensätz
     *
     * @return
     */
    public List<Einnahmen> getAllEinnahmenAlle() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Einnahmen where deleted=false order by einnahmen_id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllEinnahmen: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<Einnahmen> getSingleEinnahmen(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Einnahmen where deleted=false and einnahmen_id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleEinnahmen: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Nur regelmäßige Einnahmen abrufen
     *
     * @return
     */
    public List<Einnahmen> getAllEinnahmenRegelmaessig() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Einnahmen where deleted=false and haeufigkeit!='einmalig' order by einnahmen_id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllEinnahmenRegelmaessig: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Wenn Monat 0 oder null ist dann werden die Einnahmen für das ganze Jahr
     * geliefert. MONAT IST IN STRING, WEGEN DER FÜHRENDEN NULL
     *
     * @param monat
     * @param jahr
     * @return
     */
    public List<Einnahmen> getAllEinnahmenCustom(String monat, Integer jahr) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<Einnahmen> einnahmenListe;

        try {

            if (jahr != null && jahr != 0) {
                if (monat != null && (!monat.isEmpty())) {
                    //regelmäßige Ausgaben:
                    Query qu = s.createQuery("FROM Einnahmen where deleted=false and haeufigkeit!='einmalig' order by einnahmen_id asc");
                    einnahmenListe = qu.list();

                    //Monat und Jahr übergeben - Ausgaben für einen bestimmten Monat
                    Date d = new Date();
                    qu = s.createQuery("FROM Einnahmen where deleted=false and (EXTRACT(year FROM eingangsdatum)  = :jahrWert and EXTRACT(month FROM eingangsdatum)  = :monatWert and haeufigkeit='einmalig') order by einnahmen_id asc");
                    qu.setInteger("jahrWert", jahr);

                    if (Integer.parseInt(monat) < 1) {
                        qu.setInteger("monatWert", 12);
                    } else if (Integer.parseInt(monat) > 12) {
                        qu.setInteger("monatWert", 1);
                    } else {
                        qu.setInteger("monatWert", Integer.parseInt(monat));
                    }
                    qu.setInteger("monatWert", Integer.parseInt(monat));
                    einnahmenListe.addAll(qu.list());
                    return einnahmenListe;
                } else {
                    //regelmäßige Ausgaben:
                    Query qu = s.createQuery("FROM Einnahmen where deleted=false and haeufigkeit!='einmalig' order by einnahmen_id asc");
                    einnahmenListe = qu.list();

                    //Jahr übergeben - Ausgaben für ein bestimmtes Jahr
                    Date d = new Date();
                    qu = s.createQuery("FROM Einnahmen where deleted=false and (EXTRACT(year FROM eingangsdatum)  = :jahrWert and haeufigkeit='einmalig') order by einnahmen_id asc");
                    qu.setInteger("jahrWert", jahr);

                    einnahmenListe.addAll(qu.list());
                    return einnahmenListe;
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: Bitte ein Jahr auswählen!!", "Um Einnahmen für ein Jahr anzuzeigen bitte nur Jahr auswählen."));
                return null;
            }
        } catch (Exception e) {
            System.out.println("Fehler in getAllEinnahmenCustom: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<EinnahmenKategorie> getAllEinnahmenKategorie() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM EinnahmenKategorie order by id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllEinnahmenKategorie: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<MedienMusik> getAllMedienMusik() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM MedienMusik where deleted=false order by id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllMedienMusik: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<MedienMusik> getSingleMedienMusik(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM MedienMusik where deleted=false and id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleMedienMusik: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<MedienMusikGenre> getAllMedienMusikGenre() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM MedienMusikGenre order by id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllMedienMusikGenre: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<MedienSoftware> getAllMedienSoftware() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM MedienSoftware where deleted=false order by id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllMedienSoftware: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<MedienSoftware> getSingleMedienSoftware(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM MedienSoftware where deleted=false and id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleMedienSoftware: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<MedienSoftwareBetriebssystem> getAllMedienSoftwareBetriebssystem() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM MedienSoftwareBetriebssystem order by id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllMedienSoftwareBetriebssystem: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<MedienSoftwareHersteller> getAllMedienSoftwareHersteller() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM MedienSoftwareHersteller order by id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllMedienSoftwareHersteller: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<MedienVideoclips> getAllMedienVideoclips() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM MedienVideoclips where deleted=false order by id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllMedienVideoclips: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<MedienVideoclips> getSingleMedienVideoclips(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM MedienVideoclips where deleted=false and id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleMedienVideoclips: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<MedienVideoclipsSprache> getAllMedienVideoclipsSprache() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM MedienVideoclipsSprache order by id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllMedienVideoclipsSprache: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<MedienVideos> getAllMedienVideos() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM MedienVideos where deleted=false order by videos_id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllMedienVideos: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<MedienVideos> getSingleMedienVideos(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM MedienVideos where deleted=false and videos_id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleMedienVideos: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<MedienVideosGenre> getAllMedienVideosGenre() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM MedienVideosGenre order by id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllMedienVideosGenre: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<MedienVideosSprache> getAllMedienVideosSprache() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM MedienVideosSprache order by id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllMedienVideosSprache: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<AusgabenBudget> getAusgabenBudgetByKategorie(String kategorie) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM AusgabenBudget where kategorie=:kategoriebez order by ausgabenbudget_id asc");
            qu.setString("kategorie", kategorie);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAusgabenBudgetByKategorie: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<EinnahmenMonatEntwicklung> getEinnahmenMonatEntwicklungByMonatJahr(String monatjahr) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM EinnahmenMonatEntwicklung where monatjahr=:monatjahrbez");
            qu.setString("monatjahrbez", monatjahr);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getEinnahmenMonatEntwicklungByMonatJahr: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<EinnahmenJahrEntwicklung> getEinnahmenJahrEntwicklungByJahr(Integer jahr) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {
            Query qu = s.createQuery("FROM EinnahmenJahrEntwicklung where jahr=:jahrbez");
            qu.setInteger("jahrbez", jahr);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getEinahmenJahrEntwicklungByJahr: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /*
    public List<Sparen> getAllSparen() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Sparen where deleted=false order by id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllSparen: " + e);

            return null;
        } finally {
            s.close();
        }
    }
     */
    /**
     * Alle Sparkonten aktualisieren (auf dem jetzigen Stand) und aufrufen
     *
     * @return Sparkonten auf dem aktuellen Stand
     */
    public List<Sparen> getAllSparen() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat formatDD = new SimpleDateFormat("dd");
        SimpleDateFormat formatMM = new SimpleDateFormat("MM");
        SimpleDateFormat formatYYYY = new SimpleDateFormat("yyyy");

        DAO dao = new DAO();

        List<Sparen> sparenList;
        List<Sparen> sparenListUpdated = new ArrayList<>();

        try {
            Query qu = s.createQuery("FROM Sparen where deleted=false order by id asc");
            sparenList = qu.list();
            //Die angesparten Beträge und die Sparziel-Informationen auf das aktuelle Datum aktualisieren

            for (Sparen sparkonto : sparenList) {
                Sparen sparkontoUpdated = sparkonto;
                //Überprüfem ob dieses Sparkonto (Datensatz) noch angespart wird/werden muss

                if (sparkonto.getLetzterteildatum() != null) {
                    int letzterTeilbetragDay = Integer.parseInt(formatDD.format(sparkonto.getLetzterteildatum()));
                    int letzterTeilbetragMonth = Integer.parseInt(formatMM.format(sparkonto.getLetzterteildatum()));
                    int letzterTeilbetragYear = Integer.parseInt(formatYYYY.format(sparkonto.getLetzterteildatum()));

                    //Überprüfen ob ein Tag mindestens, dem Berechnen der Sparbeiträge vergangen sind. Wenn das auftritt wird die Berechnung durchgeführt. 
                    if (!(Integer.parseInt(formatDD.format(d)) == letzterTeilbetragDay && Integer.parseInt(formatMM.format(d)) == letzterTeilbetragMonth && Integer.parseInt(formatYYYY.format(d)) == letzterTeilbetragYear)) {

                        if (sparkonto.getEinsparhaeufigkeit().equals("individuell")) {
                            try {
                                double tagemonate = 0.0;
                                tagemonate = ((sparkonto.getSparzielbetrag() - (sparkonto.getLetzterteilbetrag())) / sparkonto.getSchrittbetrag());

                                int monate = (int) tagemonate;
                                //Tageanteil ist ein Prozentsatz der für die Berechnung der Anzahl von Tagen eines Monats verwendet wird
                                double tageAnteil = Double.parseDouble(String.format("%.2f", tagemonate));
                                tageAnteil = ((tageAnteil * 100) % 100) / 100;
                                //Ungefähr. Taganteil
                                int tage = 28 * (int) tageAnteil;
                                //Monate und Tage (bis noch zum Sparziel) hinzufügen, Jahre werden auto. hinzugefügt
                                LocalDate sparzielDatum = LocalDate.now().plusMonths(monate);
                                sparzielDatum = sparzielDatum.plusDays(tage);
                                //Sparziel Info erstellen 
                                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                                sparkontoUpdated.setSparzielinfo("Noch " + (sparkonto.getSparzielbetrag() - sparkonto.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df) + " (bei monatl. Sparen von " + sparkonto.getSchrittbetrag() + "€)");
                            } catch (NullPointerException e) {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error bei individuell: " + e, "individuell: " + e));
                            }
                        } else if (sparkonto.getEinsparhaeufigkeit().equals("täglich")) {
                            //Nun bis heute angesparte Teilbeträge hinzufügen
                            try {
                                long vergangeneTage = ChronoUnit.DAYS.between(LocalDate.of(letzterTeilbetragYear, letzterTeilbetragMonth, letzterTeilbetragDay), LocalDate.now());
                                if (vergangeneTage > 0) {
                                    double tage = ((sparkonto.getSparzielbetrag() - (sparkontoUpdated.getLetzterteilbetrag())) / sparkonto.getSchrittbetrag());

                                    //Überprüfen ob Sparziel erreicht wurde
                                    if (tage == 1.0) {
                                        //Sparziel erreicht
                                        sparkontoUpdated.setLetzterteildatum(null);
                                        sparkontoUpdated.setSparzielinfo("OK - Fertig gespart am " + sdf.format(d));
                                    } else {
                                        //Ungefähr. Taganteil
                                        //Tage (bis noch zum Sparziel) hinzufügen, Jahre werden auto. hinzugefügt
                                        LocalDate sparzielDatum = LocalDate.now().plusDays((int) tage);
                                        //Sparziel Info erstellen 
                                        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");

                                        if (vergangeneTage >= tage) {
                                            //Sparziel schon längst erreicht - Verbleibende Sparbeträge dazuaddieren
                                            sparkontoUpdated.setLetzterteilbetrag(sparkonto.getLetzterteilbetrag() + (sparkonto.getSparzielbetrag() - sparkonto.getLetzterteilbetrag()));
                                            sparkontoUpdated.setLetzterteildatum(null);
                                            sparkontoUpdated.setSparzielinfo("OK - Fertig gespart am " + sparzielDatum.format(df));

                                        } else {
                                            sparkontoUpdated.setLetzterteilbetrag(sparkonto.getLetzterteilbetrag() + (vergangeneTage * sparkonto.getSchrittbetrag()));
                                            sparkontoUpdated.setLetzterteildatum(d);
                                            sparkontoUpdated.setSparzielinfo("Noch " + (sparkonto.getSparzielbetrag() - sparkontoUpdated.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df));
                                        }
                                    }
                                }
                            } catch (NullPointerException e) {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error bei taeglich: " + e, ""));
                            }
                        } else if (sparkonto.getEinsparhaeufigkeit().equals("monatlich")) {
                            try {
                                long vergangeneMonate = ChronoUnit.MONTHS.between(LocalDate.of(letzterTeilbetragYear, letzterTeilbetragMonth, letzterTeilbetragDay), LocalDate.now());
                                if (vergangeneMonate > 0) {

                                    double tagemonate = 0.0;
                                    //Wieviel man diesen Teilbetrag sparen muss, um auf das Sparziel zu erreichen. Ergebnis entspricht die Anzahl der Monate (Ganzzahliger Wert) und Anzahl der Tagen (Wert in den Nachkommastellen)
                                    tagemonate = ((sparkonto.getSparzielbetrag() - (sparkontoUpdated.getLetzterteilbetrag())) / sparkonto.getSchrittbetrag());
                                    int monate = (int) tagemonate;

                                    //Tageanteil ist ein Prozentsatz der für die Berechnung der Anzahl von Tagen eines Monats verwendet wird
                                    double tageAnteil = Double.parseDouble(String.format("%.2f", tagemonate));
                                    tageAnteil = ((tageAnteil * 100) % 100) / 100;
                                    //Ungefähr. Taganteil
                                    int tage = 28 * (int) tageAnteil;
                                    //Monate und Tage (bis noch zum Sparziel) hinzufügen, Jahre werden auto. hinzugefügt
                                    LocalDate sparzielDatum = LocalDate.now().plusMonths(monate);
                                    if (tage > 0) {
                                        sparzielDatum = sparzielDatum.plusDays(tage);
                                    }
                                    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");

                                    //Sparziel erreich nach regelmäßigem Sparen (auto.)
                                    if (vergangeneMonate >= monate) {
                                        //Sparziel erreicht - Monate haben schon Sparziel überschritten - Nur die Monate die gefällt haben zählen
                                        sparkontoUpdated.setLetzterteilbetrag(sparkonto.getLetzterteilbetrag() + (sparkonto.getSparzielbetrag() - sparkonto.getLetzterteilbetrag()));
                                        sparkontoUpdated.setLetzterteildatum(null);
                                        sparkontoUpdated.setSparzielinfo("OK - Fertig gespart am " + sparzielDatum.format(df));

                                    } else {
                                        if (tagemonate == 1.0) {
                                            //Sparziel erreicht
                                            sparkontoUpdated.setLetzterteildatum(null);
                                            sparkontoUpdated.setSparzielinfo("OK - Fertig gespart am " + sdf.format(d));
                                        } else {
                                            sparkontoUpdated.setLetzterteilbetrag(sparkonto.getLetzterteilbetrag() + (vergangeneMonate * sparkonto.getSchrittbetrag()));

                                            //Sparziel Info erstellen 
                                            sparkontoUpdated.setLetzterteildatum(d);
                                            sparkontoUpdated.setSparzielinfo("Noch " + (sparkonto.getSparzielbetrag() - sparkontoUpdated.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df));
                                        }
                                    }
                                }
                            } catch (NullPointerException e) {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error bei monatlich: " + e, ""));
                            }
                        } else if (sparkonto.getEinsparhaeufigkeit().equals("jährlich")) {
                            try {
                                long vergangeneJahre = ChronoUnit.YEARS.between(LocalDate.of(letzterTeilbetragYear, letzterTeilbetragMonth, letzterTeilbetragDay), LocalDate.now());
                                if (vergangeneJahre > 0) {

                                    double jahremonate = 0.0;
                                    //Wieviel man diesen Teilbetrag sparen muss, um auf das Sparziel zu erreichen. Ergebnis entspricht die Anzahl der Monate (Ganzzahliger Wert) und Anzahl der Tagen (Wert in den Nachkommastellen)
                                    jahremonate = ((sparkonto.getSparzielbetrag() - (sparkontoUpdated.getLetzterteilbetrag())) / sparkonto.getSchrittbetrag());

                                    if (jahremonate == 1.0) {
                                        //Sparziel erreicht
                                        sparkontoUpdated.setSparzielinfo("OK - Fertig gespart am " + sdf.format(sparkonto.getLetzterteildatum()));
                                        sparkontoUpdated.setLetzterteildatum(null);

                                    } else {
                                        //Jahre aufrunden, da man nur jährlich bezahlt
                                        int jahre = (int) Math.ceil(jahremonate);
                                        //Jahre werden . hinzugefügt
                                        LocalDate sparzielDatum = LocalDate.now();
                                        if (jahre > 0) {
                                            sparzielDatum = sparzielDatum.plusYears(jahre);
                                        }
                                        //Sparziel Info erstellen 
                                        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                                        if (vergangeneJahre >= jahremonate) {
                                            sparkontoUpdated.setLetzterteildatum(null);
                                            sparkontoUpdated.setLetzterteilbetrag(sparkonto.getLetzterteilbetrag() + (sparkonto.getSparzielbetrag() - sparkonto.getLetzterteilbetrag()));
                                            sparkontoUpdated.setSparzielinfo("OK - Fertig gespart am " + sparzielDatum.format(df));

                                        } else {
                                            sparkontoUpdated.setLetzterteildatum(d);
                                            sparkontoUpdated.setLetzterteilbetrag(sparkonto.getLetzterteilbetrag() + (vergangeneJahre * sparkonto.getSchrittbetrag()));
                                            sparkontoUpdated.setSparzielinfo("Noch " + (sparkonto.getSparzielbetrag() - sparkontoUpdated.getLetzterteilbetrag()) + "€ bis zum " + sparzielDatum.format(df));

                                        }
                                    }
                                }
                            } catch (NullPointerException e) {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error bei jaehrlich: " + e, ""));
                            }
                        }

                        dao.updateSparen(sparkontoUpdated);
                    }
                }

                sparenListUpdated.add(sparkontoUpdated);
            }
            return sparenListUpdated;
        } catch (Exception e) {
            System.out.println("Fehler in getAllSparen: " + e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + e, ""));
            return null;
        } finally {
            s.close();
        }
    }

    public List<Sparen> getSingleSparen(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {
            Query qu = s.createQuery("FROM Sparen where deleted=false and id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleSparen: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Regelmäßige Ausgaben und einmalige Ausgaben aus diesem Jahr
     *
     * @return
     */
    public List<Ausgaben> getAllAusgaben() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        List<Ausgaben> ausgabenListe;

        try {
            //regelmäßige Ausgaben und Ausgaben dieses Jahres:
            Query qu = s.createQuery("FROM Ausgaben where (deleted=false and (EXTRACT(year FROM zahlungsdatum) = :jahrWert and ausgabezeitraum='einmalig')) or (deleted=false and ausgabezeitraum!='einmalig') order by ausgaben_id asc");
            qu.setInteger("jahrWert", Integer.parseInt(sdf.format(d)));
            ausgabenListe = qu.list();

            return ausgabenListe;

        } catch (Exception e) {
            System.out.println("Fehler in getAllAusgaben: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Nur regelmäßige Einnahmen abrufen
     *
     * @return
     */
    public List<Ausgaben> getAllAusgabenRegelmaessig() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Ausgaben where deleted=false and ausgabezeitraum!='einmalig' order by ausgaben_id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllAusgabenRegelmaessig: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<Ausgaben> getSingleAusgaben(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Ausgaben where deleted=false and ausgaben_id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleAusgaben: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<EinnahmenMonatEntwicklung> getSingleEinnahmenMonatEntwicklung(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM EinnahmenMonatEntwicklung where einnahmen_id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleEinnahmenMonatEntwicklung: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<EinnahmenJahrEntwicklung> getSingleEinnahmenJahrEntwicklung(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM EinnahmenJahrEntwicklung where einnahmen_id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleEinnahmenJahrEntwicklung: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<AusgabenBudget> getSingleAusgabenBudget(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM AusgabenBudget where ausgabenbudget_id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleAusgabenBudget: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Liefert alle ausgaben, auch alte einmalige Ausgaben von vorherigen Jahren
     *
     * @return
     */
    public List<Ausgaben> getAllAusgabenAlle() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Ausgaben where deleted=false order by ausgaben_id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllAusgaben: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Liefer Ausgaben des aktuellen Monats (inkl. allen regelmäßigen Ausgaben)
     *
     * @return
     */
    public List<Ausgaben> getAllAusgabenMonat() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<Ausgaben> ausgabenListe;

        try {
            //regelmäßige Ausgaben:
            Query qu = s.createQuery("FROM Ausgaben where deleted=false and ausgabezeitraum!='einmalig' order by ausgaben_id asc");
            ausgabenListe = qu.list();

            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            SimpleDateFormat sdf2 = new SimpleDateFormat("MM");
            qu = s.createQuery("FROM Ausgaben where deleted=false and (EXTRACT(year FROM zahlungsdatum)  = :jahrWert and EXTRACT(month FROM zahlungsdatum)  = :monatWert and ausgabezeitraum='einmalig') order by ausgaben_id asc");
            qu.setInteger("jahrWert", Integer.parseInt(sdf.format(d)));
            qu.setInteger("monatWert", Integer.parseInt(sdf2.format(d)));

            ausgabenListe.addAll(qu.list());
            return ausgabenListe;

        } catch (Exception e) {
            System.out.println("Fehler in getAllAusgabenMonat: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Wenn Monat 0 oder null ist dann werden die Ausgaben für das ganze Jahr
     * geliefert. MONAT IST IN STRING, WEGEN DER FÜHRENDEN NULL
     *
     * @param monat
     * @param jahr
     * @return
     */
    public List<Ausgaben> getAllAusgabenCustom(String monat, Integer jahr) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<Ausgaben> ausgabenListe;

        try {

            if (jahr != null && jahr != 0) {
                if (monat != null && (!monat.isEmpty())) {
                    //regelmäßige Ausgaben:
                    Query qu = s.createQuery("FROM Ausgaben where deleted=false and ausgabezeitraum!='einmalig' order by ausgaben_id asc");
                    ausgabenListe = qu.list();

                    //Monat und Jahr übergeben - Ausgaben für einen bestimmten Monat
                    Date d = new Date();
                    qu = s.createQuery("FROM Ausgaben where deleted=false and (EXTRACT(year FROM zahlungsdatum)  = :jahrWert and EXTRACT(month FROM zahlungsdatum)  = :monatWert and ausgabezeitraum='einmalig') order by ausgaben_id asc");
                    qu.setInteger("jahrWert", jahr);

                    if (Integer.parseInt(monat) < 1) {
                        qu.setInteger("monatWert", 12);
                    } else if (Integer.parseInt(monat) > 12) {
                        qu.setInteger("monatWert", 1);
                    } else {
                        qu.setInteger("monatWert", Integer.parseInt(monat));
                    }
                    qu.setInteger("monatWert", Integer.parseInt(monat));
                    ausgabenListe.addAll(qu.list());
                    return ausgabenListe;
                } else {
                    //regelmäßige Ausgaben:
                    Query qu = s.createQuery("FROM Ausgaben where deleted=false and ausgabezeitraum!='einmalig' order by ausgaben_id asc");
                    ausgabenListe = qu.list();

                    //Jahr übergeben - Ausgaben für ein bestimmtes Jahr
                    Date d = new Date();
                    qu = s.createQuery("FROM Ausgaben where deleted=false and (EXTRACT(year FROM zahlungsdatum)  = :jahrWert and ausgabezeitraum='einmalig') order by ausgaben_id asc");
                    qu.setInteger("jahrWert", jahr);

                    ausgabenListe.addAll(qu.list());
                    return ausgabenListe;
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: Bitte ein Jahr auswählen!!", "Um Ausgaben für ein Jahr anzuzeigen bitte nur Jahr auswählen."));
                return null;
            }
        } catch (Exception e) {
            System.out.println("Fehler in getAllAusgabenCustom: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<EinnahmenMonatEntwicklung> getAllEinnahmenMonatEntwicklung() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM EinnahmenMonatEntwicklung order by einnahmen_id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllEinnahmenMonatEntwicklung: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public Double getSummeJahrEinnahmen() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {
            Query qu = s.createQuery("SELECT sum(betrag) FROM EinnahmenJahrEntwicklung");
            return (Double) qu.list().get(0);

        } catch (Exception e) {
            System.out.println("Fehler in getSummeJahrEinnahmen: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<EinnahmenJahrEntwicklung> getAllEinnahmenJahrEntwicklung() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM EinnahmenJahrEntwicklung order by jahr asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllEinnahmenMonatEntwicklung: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<Ordnung> getAllOrdnung() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Ordnung where deleted=false order by ordnung_id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllOrdnung: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<Ordnung> getSingleOrdnung(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Ordnung where deleted=false and ordnung_id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleOrdnung: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    //Benutzerdefinierte SQL.Anfragen:
    public List<String> customGetAll(String sqlbefehl) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            SQLQuery qu = s.createSQLQuery(sqlbefehl);

            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler im SQL-Befehl:  " + e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler im SQL-Befehl! ", e.toString()));
            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Benutzerdefinierte (Custom SQL-Query) für Abfragen von Listen/Datenreihen
     *
     * @param sqlbefehl
     * @return
     */
    public List<Ausgaben> customGetAllAusgaben(String sqlbefehl) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createSQLQuery(sqlbefehl);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler im SQL-Befehl:  " + e);
            FacesContext.getCurrentInstance().addMessage("fehlerAnsichtListen", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler im SQL-Befehl! ", e.toString()));
            return null;
        } finally {
            s.close();
        }
    }

    public List<Buecher> customGetAllBuecher(String sqlbefehl) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createSQLQuery(sqlbefehl);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler im SQL-Befehl:  " + e);
            FacesContext.getCurrentInstance().addMessage("fehlerAnsichtListen", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler im SQL-Befehl! ", e.toString()));
            return null;
        } finally {
            s.close();
        }
    }

    public List<Ordnung> customGetAllOrdnung(String sqlbefehl) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createSQLQuery(sqlbefehl);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler im SQL-Befehl:  " + e);
            FacesContext.getCurrentInstance().addMessage("fehlerAnsichtListen", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler im SQL-Befehl! ", e.toString()));
            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Benutzerdefinierte (Custom SQL-Query) für Abfragen, die Werte liefern
     *
     * @param sqlbefehl
     * @return
     */
    public List<String> customGetValue(String sqlbefehl) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createSQLQuery(sqlbefehl);

            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler im SQL-Befehl:  " + e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler im SQL-Befehl! ", e.toString()));
            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Speichern eines Benutzers in die DB
     *
     * @param b zum Speichern fertiges Appsettings-Objekt
     * @return true, wenn erfolgreiche Speicherung
     */
    public boolean insertAppsettings(Appsettings b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(b);

            tx.commit();
            ret = true;
        } catch (HibernateException ex) {
            System.out.println("Fehler in insertAppsettings: " + ex);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    /**
     * Benutzers in der DB aktualisieren Appsettings loeschen und neuanlegen
     *
     * @param b zum Speichern fertiges Appsettings-Objekt
     * @return true, wenn erfolgreiche Speicherung
     */
    public boolean updateAppsettings(Appsettings b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        DAO dao = new DAO();
        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(b);
            tx.commit();

            ret = true;
        } catch (HibernateException ex) {
            System.out.println("Fehler in updateAppsettings: " + ex);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertMedienMusik(MedienMusik b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertMedienMusik: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertMedienMusikGenre(MedienMusikGenre o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(o);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Neuer Eintrag für die Kategorie MedienMusikGenre wurde erfolgreich gespeichert", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertMedienMusikGenre: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertMedienSoftware(MedienSoftware b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertMedienSoftware: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertMedienSoftwareBetriebssystem(MedienSoftwareBetriebssystem o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(o);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Neuer Eintrag für die Kategorie MedienSoftwareBetriebssystem wurde erfolgreich gespeichert", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertMedienSoftwareBetriebssystem: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertMedienSoftwareHersteller(MedienSoftwareHersteller o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(o);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Neuer Eintrag für die Kategorie MedienSoftwareHersteller wurde erfolgreich gespeichert", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertMedienSoftwareHersteller: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertMedienVideoclips(MedienVideoclips b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertMedienVideoclips: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertMedienVideoclipsSprache(MedienVideoclipsSprache o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(o);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Neuer Eintrag für die Kategorie MedienVideoclipsSprache wurde erfolgreich gespeichert", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertMedienVideoclipsSprache: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertMedienVideos(MedienVideos b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertMedienVideos: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertMedienVideosGenre(MedienVideosGenre o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(o);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Neuer Eintrag für die Kategorie MedienVideosGenre wurde erfolgreich gespeichert", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertMedienVideosGenre: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertMedienVideosSprache(MedienVideosSprache o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(o);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Neuer Eintrag für die Kategorie MedienVideosSprache wurde erfolgreich gespeichert", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertMedienVideosSprache: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteMedienMusik(MedienMusik b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            MedienMusik bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteMedienMusik: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteMedienMusikGenre(MedienMusikGenre b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteMedienMusikGenre: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteMedienSoftware(MedienSoftware b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            MedienSoftware bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteMedienSoftware: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteMedienSoftwareBetriebssystem(MedienSoftwareBetriebssystem b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteMedienSoftwareBetriebssystem: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteMedienSoftwareHersteller(MedienSoftwareHersteller b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteMedienSoftwareHersteller: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteMedienVideoclips(MedienVideoclips b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            MedienVideoclips bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteMedienVideoclips: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteMedienVideoclipsSprache(MedienVideoclipsSprache b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteMedienVideoclipsSprache: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteMedienVideos(MedienVideos b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            MedienVideos bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteMedienVideos: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteMedienVideosGenre(MedienVideosGenre b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteMedienVideosGenre: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteMedienVideosSprache(MedienVideosSprache b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteMedienVideosSprache: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateMedienMusik(MedienMusik b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        DAO dao = new DAO();
        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(b);
            tx.commit();

            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateMedienMusik: " + ex);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateMedienMusikGenre(MedienMusikGenre b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        DAO dao = new DAO();
        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(b);
            tx.commit();

            ret = true;
        } catch (HibernateException ex) {
            System.out.println("Fehler in updateMedienMusikGenre: " + ex);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateMedienSoftware(MedienSoftware b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        DAO dao = new DAO();
        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(b);
            tx.commit();

            ret = true;
        } catch (HibernateException ex) {
            System.out.println("Fehler in updateMedienSoftware: " + ex);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateMedienSoftwareBetriebssystem(MedienSoftwareBetriebssystem b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        DAO dao = new DAO();
        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(b);
            tx.commit();

            ret = true;
        } catch (HibernateException ex) {
            System.out.println("Fehler in MedienSoftwareBetriebssystem: " + ex);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateMedienSoftwareHersteller(MedienSoftwareHersteller b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        DAO dao = new DAO();
        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(b);
            tx.commit();

            ret = true;
        } catch (HibernateException ex) {
            System.out.println("Fehler in updateMedienSoftwareHersteller: " + ex);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateMedienVideoclips(MedienVideoclips b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        DAO dao = new DAO();
        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(b);
            tx.commit();

            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateMedienVideoclips: " + ex);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateMedienVideoclipsSprache(MedienVideoclipsSprache b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        DAO dao = new DAO();
        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(b);
            tx.commit();

            ret = true;
        } catch (HibernateException ex) {
            System.out.println("Fehler in updateMedienVideoclipsSprache: " + ex);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateMedienVideos(MedienVideos b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        DAO dao = new DAO();
        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(b);
            tx.commit();

            ret = true;
        } catch (HibernateException ex) {
            System.out.println("Fehler in updateMedienVideos: " + ex);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateMedienVideosGenre(MedienVideosGenre b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        DAO dao = new DAO();
        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(b);
            tx.commit();

            ret = true;
        } catch (HibernateException ex) {
            System.out.println("Fehler in updateMedienVideosGenre: " + ex);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateMedienVideosSprache(MedienVideosSprache b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        DAO dao = new DAO();
        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(b);
            tx.commit();

            ret = true;
        } catch (HibernateException ex) {
            System.out.println("Fehler in updateMedienVideosSprache: " + ex);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteHandel(Handel b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            Handel bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteAusgaben: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public List<Handel> getAllHandel() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Handel where deleted=false order by id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllHandel: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<Handel> getSingleHandel(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Handel where deleted=false and id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleHandel: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public boolean insertHandel(Handel a) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(a);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertHandel: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateHandel(Handel a) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(a);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateHandel: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }
        return ret;
    }

    public boolean updateSparen(Sparen a) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(a);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateSparen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }
        return ret;
    }

    @Override
    public void close() {
        HibernateUtil.close();
    }

}
