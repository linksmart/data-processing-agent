package eu.ebbits.pwal.impl.driver.llrpreader.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Class with the utils methods used by the LLRP driver
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since   PWAL 0.2.0
 */
public final class Utils {
    
    private Utils() {
    }
    
    static final String DATE_FORMAT = "MMM dd - HH:mm";
    static final String DATE_FILE_FORMAT = "yyyyMMddHHmm";
    
    /**
     * Recupera la data corrente (formato: yyy-MM-dd)
     * 
     * @return: data corrente
     */
    public static String currentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
    
    
    /**
     * Recupera l'ora corrente (formato: HH-mm-ss)
     * 
     * @return data corrente
     */
    public static String currentTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);        
    }
    
    /**
     * Metodo accessorio per recuperare data e ora corrente da aggiungere nel file di configurazione
     * che sarà esportato, per caricarlo in seguito
     * 
     * @return data e ora attuali
     */
    public static String nowFile() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FILE_FORMAT);
        return sdf.format(cal.getTime());
    }
    
    /**
     * Metodo accessorio per recuperare data e ora corrente
     * 
     * @return data e ora attuali
     */
    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(cal.getTime());
    }
    
    /**
     * Metodo che restituisce un timestamp
     * 
     * @return long: timestamp
     */
    public static String getTimestamp() {
        Date d = new Date();
        return ""+d.getTime();
    }
    

    /**
     * Metodo che restituisce una data nel formato voluto a partire da un valore in millisecondi
     * 
     * @param timestamp: valore in millisecondi
     * 
     * @return data formattata
     */
    public static String timestampToDate(String timestamp) {
        DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        return formatter.format(calendar.getTime());
    }

    
    /**
     * Serve per recuperare lo stack trace come una stringa
     * 
     * @param throwable: eccezione di cui stampare lo stack trace
     * 
     * @return: stack trace come stringa
     * 
     */
    public static String getStackTrace(Throwable throwable) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        return writer.toString();
    }
}