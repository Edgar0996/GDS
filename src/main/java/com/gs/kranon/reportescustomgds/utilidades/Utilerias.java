package com.gs.kranon.reportescustomgds.utilidades;

import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.util.SystemOutLogger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.gs.kranon.reportescustomgds.cuadroMando.ReporteMail;

/**
 *
 * @author Victor Paredes
 */
public class Utilerias {
    static {
        System.setProperty("dateLog", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
    }
    private static final Logger voLogger = LogManager.getLogger("Reporte");
	/*
	 * private String vsPathConf =
	 * "/home/samuelmanzano/Appl/Configuraciones/conf.properties"; private String
	 * vsPathConfId =
	 * "/home/samuelmanzano/Appl/Configuraciones/confclientid.properties";
	 */
	
	 // private String vsPathConf = File.separator+"home"+File.separator+System.setProperty("user", System.getProperty("user.name"))+File.separator+"Appl"+File.separator+"Configuraciones"+File.separator+"conf.properties"; 
	  //private String vsPathConfId = File.separator+"home"+File.separator+System.setProperty("user", System.getProperty("user.name"))+File.separator+"Appl"+File.separator+"Configuraciones"+File.separator+"confclientid.properties";
	 
    //Para Wind
	 
	  //private String vsPathConf = "C:"+File.separator+"Appl"+File.separator+"GS"+File.separator+"ReportesCustom"+File.separator+"Configuraciones"+File.separator+"conf.properties"; 
      //private String vsPathConfId ="C:"+File.separator+"Appl"+File.separator+"GS"+File.separator+"ReportesCustom"+File.separator+"Configuraciones"+File.separator+"confclientid.properties";
	  private String vsPathConf = ReporteMail.pathConfig+"/configuraciones/conf.properties";
	  private String vsPathConfId = ReporteMail.pathConfig +"/configuraciones/confclientid.properties" ;
	  
    public String vsUUI = "";

    @SuppressWarnings("rawtypes")
    public boolean getProperties(Map<String, String> voMapConfi, String vsUUI) {
        
        try {
           
            Properties p = new Properties();
            p.load(new FileReader(vsPathConf));
            for (Enumeration voEnum = p.keys(); voEnum.hasMoreElements();) {
                String vsProperty = String.valueOf(voEnum.nextElement());

                if (!vsProperty.contains("##")) {
                    voMapConfi.put(vsProperty, p.getProperty(vsProperty));
                }
            }
           
            return true;
        } catch (Exception e) {
          
            voLogger.error("[Utilerias][" + vsUUI + "] ---> ERROR : " + e.getMessage());
            return false;
        }
    }

    
     @SuppressWarnings("rawtypes")
    public boolean getPropertiesID(Map<String, String> voMapConfi) {
        
        try {
           
            Properties p = new Properties();
            p.load(new FileReader(vsPathConfId));
            for (Enumeration voEnum = p.keys(); voEnum.hasMoreElements();) {
                String vsProperty = String.valueOf(voEnum.nextElement());
                    
                if (!vsProperty.contains("##")) {
                    voMapConfi.put(vsProperty, p.getProperty(vsProperty));
                  
                }
            }
      
            return true;
        } catch (Exception e) {
          
            voLogger.error("[Utilerias][" + vsUUI + "] ---> ERROR : " + e.getMessage());
            return false;
        }
    }
    
    public static String userDateGMT(String vsFecha) {
        if (vsFecha == null || vsFecha.isEmpty()) return "";
        DateTime voDateTime = new DateTime(vsFecha,DateTimeZone.UTC);
        String dateFormat = voDateTime.withZone(DateTimeZone.getDefault()).toString();
        return dateFormat.substring(0,19);
    }
	/**
	 * Permite hacer el calculo del tiempo de ejecucion de la aplicacion 
	 * @param fechaInicio fecha en que inicio el proceso
	 * @param fechaFin fecha en que termino el proceso
	 * @return String tiempo de ejecucion calculado en formato dd hh mm ss
	 */
	public static String tiempoEjecucion(String fechaInicio, String fechaFin) {
		String fechaReturn ="";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date now;
        try {
            now = df.parse(fechaFin);
            java.util.Date date = df.parse(fechaInicio);
            long l = now.getTime() - date.getTime();
            long day = l / (24 * 60 * 60 * 1000);
            long hour = (l / (60 * 60 * 1000) - day * 24);
            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            fechaReturn = hour + "h " + min + "' " + s + "''";
        } catch (ParseException ex) {
        	voLogger.error("[Utilerias] ---> ERROR : " + ex.getMessage());
        }
        return fechaReturn;
	}
	
	public static String secondsToTime(long seconds) {
		return String.format("%02d min. %02d sec", 
			    TimeUnit.MILLISECONDS.toMinutes(seconds),
			    TimeUnit.MILLISECONDS.toSeconds(seconds) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(seconds))
			);

	}

}
