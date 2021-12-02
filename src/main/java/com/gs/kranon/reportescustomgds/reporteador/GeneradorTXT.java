/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gs.kranon.reportescustomgds.reporteador;

import com.gs.kranon.reportescustomgds.DataReports;
import com.gs.kranon.reportescustomgds.app;
import com.gs.kranon.reportescustomgds.genesysCloud.GenesysCloud;
import com.gs.kranon.reportescustomgds.utilidades.Utilerias;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.util.SystemOutLogger;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author kranoncloud
 */
public class GeneradorTXT {
	
	private Map<String,String> voMapConf = null;
    private Utilerias voUtil = null;
    
	static {
	        System.setProperty("dateLog", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
	    }
	    private static final Logger voLogger = LogManager.getLogger("Reporte");
	    
	       
    Thread voThreadReporte;
    
     public void GeneraTXT(String vlContactId, Map<String, Map<String, String>> voConversations) {
    	 
    	 
      voThreadReporte = new Thread() {
    	  
          @Override
            public void run() {
        	  
        	  voUtil = new Utilerias();
              voMapConf = new HashMap<>();
              voUtil.getProperties(voMapConf);
              String pathArchivo = voMapConf.get("PathReporteFinal");
              
                
        	  //Recorro mi voConversations Map para saber que argumentos tiene cada Id de Llamada
        	  voLogger.info("[GeneradorTXT][" + vlContactId + "] ---> ******************** Iniciamos la Generación de los TXT *******************");
        	  for (Map.Entry entry : voConversations.entrySet()) {
        		  //Comparo mis Id's para crear el TXt y pintar el contenido de cada llamada
        		if( entry.getKey().equals(vlContactId)) {
        	 
	          
        		Map<String, String> voDetails = voConversations.get(vlContactId);
        		
        		
        		String  conversationStart = String.valueOf(voDetails.get("conversationStart"));
        		String  conversationEnd = String.valueOf(voDetails.get("conversationEnd"));
        		String  ani = String.valueOf(voDetails.get("ani"));
        		String  dnis = String.valueOf(voDetails.get("dnis"));
        		
        		//Creo mi archivo temporal
        		try { 
				File files = new File(vlContactId); 
				 
				//files.deleteOnExit();
				
				FileWriter write = new FileWriter(pathArchivo + vlContactId);
				
				//Escribir sobre el archivo
				write.write("conversationId=" + vlContactId); 
				write.append("\nconversationStart = " + conversationStart);
				write.append("\nconversationEnd = " + conversationEnd);
				write.append("\nani = " + ani);
				write.append("\ndnis = " + dnis);
				write.close();
								 
        		}catch(Exception e)  { 
        			voLogger.error("[Generador][" + vlContactId + "] ---> ERROR : NO SE CREO EL ARCHIVO TXT");              
        		}
        					//System.out.println(Identry.getKey() + ", " + Identry.getValue());
        					 
        				
                   
        		  }
        		   
        		}
        	  voLogger.info("[GeneradorTXT][" + vlContactId + "] ---> Se Generaron [\\" +   voConversations.size() + "\\]" );
            };
      }; 
      voThreadReporte.start();
  }
        // Map<String, String> voDetails = voConversations.get(vsContactId);
         
        }
                 
          
   

