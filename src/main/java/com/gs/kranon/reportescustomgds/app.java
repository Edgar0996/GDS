/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gs.kranon.reportescustomgds;

import com.gs.kranon.reportescustomgds.genesysCloud.GenesysCloud;
import com.gs.kranon.reportescustomgds.utilidades.Utilerias;
import java.util.HashMap;
import java.util.Map;
import com.gs.kranon.reportescustomgds.conexionHttp.ConexionResponse;
import com.gs.kranon.reportescustomgds.conexionHttp.ConexionHttp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JOptionPane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.gs.kranon.reportescustomgds.reporteador.Reporteador;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
/**
 *
 * @author VFG
 */
public class app {
    /**
     * @param args the command line arguments
     */
        static {    
        System.setProperty("dateLog", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
    }
    private static final Logger voLogger = LogManager.getLogger("Reporte");
    private GenesysCloud voPureCloud = null;
    private Utilerias voUtil = null;
    private Map<String,String> voMapConf = null;
    private String vsUUI= "1234567890";
    /*Variables de prueba */
    private DataReports voData = null;
     private Reporteador voReporte;
    
    public static void main(String[] args) {
        
        
        
        System.out.println("Ejecutando el proyecto desde consola sin parametros");
        for(int i=0; i<args.length; i++){
            System.out.println("Valor del argumento recibido: "+args[i]);
        }
        GenesysCloud genesysCloud = new GenesysCloud();
        
        new app();
        
    }
     
    public  app() {
        
       voUtil = new Utilerias();
       voMapConf = new HashMap<>();
       
        voUtil.getProperties(voMapConf);
           String vsApps = voMapConf.get("ClientID");
       voPureCloud = new GenesysCloud();
       vsUUI = java.util.UUID.randomUUID().toString();
       voPureCloud.setUUI(vsUUI);
       voLogger.info("[Main  ][" + vsUUI + "] ---> Inicia ejecución de la app.");
       ConexionResponse voConexionResponse;
        String vsToken = voPureCloud.getToken(voMapConf.get("ClientID"), voMapConf.get("ClientSecret"));
        if (vsToken.equals("ERROR")) {
            
           voLogger.error("[Reporteador][" + vsUUI + "] ---> [ClientID] AND [ClientSecret] ARE INCORRECT.");
           System.out.println("Se genero un error");
                }
        
        System.out.println("Esto me regreso el toke línea 63 app"  + vsToken+" vsUUI"+vsUUI);
              /* Prueba de llamado de clases */
                
           
                voData = new DataReports();
                voData.setFlowName("bbva_bbvamxap_ivr");
                voData.setFechaInicio(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
                voData.setFechaFin(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
                JProgressBar voProgreso = null;
                JTextArea voTextArea = null;
                voReporte = new Reporteador(voData,voTextArea,voProgreso);
                voReporte.getDataReport();
                
                
        
       
}
    
}
