package com.gs.kranon.reportescustomgds.utilidades;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author Victor Paredes
 */
public class Utilerias {
    static {
        System.setProperty("dateLog", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
    }
    private static final Logger voLogger = LogManager.getLogger("Reporte");

    private String vsPathConf = "C:/Appl/GS/ReportesCustom/Configuraciones/conf.properties";
    public String vsUUI = "";

    @SuppressWarnings("rawtypes")
    public boolean getProperties(Map<String, String> voMapConfi) {
        
        try {
           
            Properties p = new Properties();
            p.load(new FileReader(vsPathConf));
            for (Enumeration voEnum = p.keys(); voEnum.hasMoreElements();) {
                String vsProperty = String.valueOf(voEnum.nextElement());
                
                if (!vsProperty.contains("//")) {
                    voMapConfi.put(vsProperty, p.getProperty(vsProperty));
                }
            }
           
            return true;
        } catch (Exception e) {
          
            voLogger.error("[Utilerias][" + vsUUI + "] ---> ERROR : " + e.getMessage());
            return false;
        }
    }

    public String userDateGMT(String vsFecha) {
        if (vsFecha == null || vsFecha.isEmpty()) return "";
        DateTime voDateTime = new DateTime(vsFecha,DateTimeZone.UTC);
        return voDateTime.withZone(DateTimeZone.getDefault()).toString();
    }

}
