/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gs.kranon.reportescustomgds.reporteador;

import com.gs.kranon.reportescustomgds.utilidades.Utilerias;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.*;
import java.text.SimpleDateFormat;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author kranoncloud
 */
public class GeneradorTXT {
	
    private Map<String,String> voMapConf = null;
    private Utilerias voUtil = null;
    private String  conversationId;
    private String pathArchivo;
    private FileWriter write;
    private String timeStamp;
    private String Archivo;
    private String Temporal;
    private List<String> nameTxt= new ArrayList<>();
    
	static {
	        System.setProperty("dateLog", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
	    }
	    private static final Logger voLogger = LogManager.getLogger("Reporte");
	    
	       
    Thread voThreadReporte;
    
     public List<String> GeneraTXT(List<String> vlContactId,Map<String, Map<String, String>> voConversations) {
    	 timeStamp = new SimpleDateFormat("yyyy_MM_dd HH.mm.ss").format(Calendar.getInstance().getTime());
         nameTxt.add(timeStamp);
      voThreadReporte = new Thread() {
    	  
          @Override
          
            public void run() {
        	  
            voUtil = new Utilerias();
            voMapConf = new HashMap<>();
            voUtil.getProperties(voMapConf);
            pathArchivo = voMapConf.get("PathReporteFinal");
              
        	  
        	//Creamos nuestro archivo TXT
              try { 
           
            	//timeStamp = new SimpleDateFormat("yyyy_MM_dd HH.mm.ss").format(Calendar.getInstance().getTime());
            	System.out.println("Nombre del archivo que se usara: "+timeStamp);
            	Archivo = pathArchivo + "temp\\Reporte_" + timeStamp;
            	boolean Ruta = createTempDirectory(Archivo);
            	if (Ruta == true) {
            		Temporal = Archivo;
                //Genero mi archivo temporal
            	Archivo =	Temporal + "\\" + timeStamp;
  				File files = new File(timeStamp+".txt"); 
  				write = new FileWriter(Temporal + "\\" + timeStamp+".txt");
                System.out.println("La ruta se creo estamos en true: "+timeStamp);
                
  				//files.deleteOnExit();
                //Recorro mi voConversations Map para saber que argumentos tiene cada Id de Llamada
        	  voLogger.info("[GeneradorTXT][" + conversationId + "] ---> ******************** Iniciamos la Generación de los TXT *******************");
        	  int i = 1;
        	  for(String vsContactId : vlContactId) {
        		  
    		  for (Map.Entry entry : voConversations.entrySet()) {
        		  //Comparo mis Id's para crear el TXt y pintar el contenido de cada llamada
    			 
    			  if( entry.getKey().equals(vsContactId)) {
    		        	
    		          
    	        		Map<String, String> voDetails = voConversations.get(vsContactId);
    	        		
    	        		
    	        		String  conversationStart = String.valueOf(voDetails.get("conversationStart"));
    	        		String  conversationEnd = String.valueOf(voDetails.get("conversationEnd"));
    	        		String  ani = String.valueOf(voDetails.get("ani"));
    	        		String  dnis = String.valueOf(voDetails.get("dnis"));
    	        		
    	        		//Escribir sobre el archivo
    	        		try {
    	        			//Leemos nuestro archivo creado
    	        			Writer  output = new BufferedWriter(new FileWriter(Archivo+".txt", true));
    	        			output.append("conversationId= "+vsContactId+", conversationStart= " + conversationStart + ", conversationEnd=  " + conversationEnd + ", ani= " + ani + ", dnis= " + dnis + "\n");
    	        			output.close(); 	
  	  				
    	        		}catch(Exception e)  { 
    	          			voLogger.error("[Generador][" + vlContactId + "] ---> ERROR : NO SE LOGRÓ ESCRIBIR EN EL ARCHIVO DE NOMBRE [" + timeStamp + "]" );              
    	          		}		
    	                   
    	        		  }
        		}
    		  i ++;
        	  }
        	 nameTxt = new ArrayList<>();
             nameTxt.add(timeStamp);
        	 voLogger.info("[GeneradorTXT][" + conversationId + "] ---> Se Generaron [\\" +   voConversations.size() + "\\]" );
                                
            	}else {
            		voLogger.error("[Generador][" + vlContactId + "] ---> ERROR : NO SE  CREO LA CARPETA TEMPORAL" ); 
                        //Se tendria que terminar el programa aquí con algun return o break
            	}
            	
  				
  				
  				
  				
          		}catch(Exception e)  { 
          			voLogger.error("[Generador][" + vlContactId + "] ---> ERROR : NO SE CREO EL ARCHIVO TXT");              
          		}
        	  
        	 

            }; //Termina run
      }; 
      voThreadReporte.start();
      
          System.out.println("Imprimiendo timeStamp desde GeneradorTXT es: "+timeStamp);
          System.out.println("El nombre a regresar desde GeneradorTXT es: "+nameTxt.get(0));
      return nameTxt;
  }
     
     public static boolean createTempDirectory(String ruta){
    	
    		 
    		    File Directory = new File(ruta);
    		    if (Directory.mkdirs()) {
    		    	return true;	
    		    }else {
    		    	return false;	
    		    }
    		    	
    		   
    		   
    		}
     
       
         
        }
                 
          
   

