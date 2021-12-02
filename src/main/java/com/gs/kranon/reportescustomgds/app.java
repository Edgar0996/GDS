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
import com.gs.kranon.reportescustomgds.mail.SendingMailTLS;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JOptionPane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.gs.kranon.reportescustomgds.reporteador.Reporteador;
import java.util.Properties;
import java.util.logging.Level;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
    private Map<String,String> voMapConfId = null;
    private String vsUUI= "1234567890";
    private String vsToken =  null;
    /*Variables de prueba */
    private DataReports voData = null;
     /*Variables para mandar a llamadar mi reporte */
    private Reporteador voReporte;
    
    public static void main(String[] args) {
        
               
             
        new app();
        
    }
    
     
    public  app()  {
       //Invocamos nuestro primer archivo de configuración 
       voUtil = new Utilerias();
       voMapConf = new HashMap<>();
       voPureCloud = new GenesysCloud();

       vsUUI = java.util.UUID.randomUUID().toString();
       voPureCloud.setUUI(vsUUI);
       voLogger.info("[Main  ][" + vsUUI + "] ---> Inicia ejecución de la app.");
       ConexionResponse voConexionResponse;
        //String vsToken = voPureCloud.getToken(voMapConf.get("ClientID"), voMapConf.get("ClientSecret"));

       voUtil.getProperties(voMapConf);
       String clientsNum = voMapConf.get("NoClienteID");
       String pathArchivo = voMapConf.get("PathReporteFinal");
      
       
       //Invocamos nuestro segundo archivo de configuración para traer los ID's 
       voMapConfId = new HashMap<>();
       voUtil.getPropertiesID(voMapConfId);
        
        
        int total= Integer.parseInt(clientsNum);
            //Generamos el for para recuperar solo los ClientesId que se colocaron en la configuración
        for (int i = 0; i < total; i++) {
            String clientNum =   "ClientId" + i;
            String clientSecr =   "ClientSecret" + i;
            //Seteamos los valores recuperados
                 String idClient =  voMapConfId.get(clientNum);
                 String clientSecret =  voMapConfId.get(clientSecr);
                 
            //Generamos los Tokents  
            
            vsUUI = java.util.UUID.randomUUID().toString();
            voPureCloud.setUUI(vsUUI);
            vsToken = voPureCloud.getToken(idClient, clientSecret);
            
            // Invocamos el metodo para recuperar los id's
           voData = new DataReports();
 
                //voData.setFechaInicio(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
                voData.setFechaInicio("2021-01-01");
                voData.setFechaFin(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
               
                voLogger.info("[Main  ][" + vsUUI + "] ---> Fecha del reporte final."+"[ "+voData.getFechaInicio()+"]");
                voReporte = new Reporteador(voData);
                voReporte.getDataReport(vsToken);
                  
        if (vsToken.equals("ERROR")) {
            
           voLogger.error("[Reporteador][" + vsUUI + "] ---> [ClientID] AND [ClientSecret] ARE INCORRECT." + idClient);
           System.out.println("Se genero un error");
                }
             
        
     }   
        
             
        
                      
           /*
                voData = new DataReports();
                voData.setFlowName("bbva_bbvamxap_ivr");
                //voData.setFechaInicio(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
                voData.setFechaInicio("2021-01-01");
                voData.setFechaFin(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
                voLogger.info("[Main  ][" + vsUUI + "] ---> Fecha del reporte final."+"[ "+voData.getFechaInicio()+"]");
                JProgressBar voProgreso = null;
                JTextArea voTextArea = null;
                voReporte = new Reporteador(voData);
                voReporte.getDataReport(vsToken);
                
               
        
                SendingMailTLS enviarcorreo = new SendingMailTLS();
                 boolean result = enviarcorreo.sendMailKranon("vfrancisco@kranon.com","Reporte de la ejecución del servicio de GDS", vsUUI);
                System.out.print("El resultado del envio del correo fue: "+result);
*/
        }
  
}
