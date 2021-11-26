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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
/**
 *
 * @author VFG
 */
public class app {
    /**
     * @param args the command line arguments
     */
    private static final Logger voLogger = LogManager.getLogger("Reporte");
    private GenesysCloud voPureCloud = null;
    private Utilerias voUtil = null;
    private Map<String,String> voMapConf = null;
    private Map<String,String> voMapConfId = null;
    private String vsUUI= "1234567890";
    
    public static void main(String[] args) {
        
               
             
        new app();
        
    }
     
    public  app() {
       //Invocamos nuestro primer archivo de configuración 
       voUtil = new Utilerias();
       voMapConf = new HashMap<>();
       voPureCloud = new GenesysCloud();
       voUtil.getProperties(voMapConf);
       String clientsNum = voMapConf.get("NoClienteID");
      
       
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
            String vsToken = voPureCloud.getToken(idClient, clientSecret);
            
        if (vsToken.equals("ERROR")) {
            
           voLogger.error("[Reporteador][" + vsUUI + "] ---> [ClientID] AND [ClientSecret] ARE INCORRECT." + idClient);
           System.out.println("Se genero un error");
                }
        
        System.out.println("Esto me regreso el toke línea 81 app con este ID"  + idClient );
       
}
        }
			 
      
       
       
       
       
     
       
        
    
}
