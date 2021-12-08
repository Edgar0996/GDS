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
public class GeneradorTXT  {
	
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
	    
	       
    
    
     public List<String> GeneraTXT(List<String> vlContactId,Map<String, Map<String, String>> voConversations,String UUI) {
    	 timeStamp = new SimpleDateFormat("yyyy_MM_dd HH.mm.ss").format(Calendar.getInstance().getTime());
         nameTxt.add(timeStamp);
         
          
        
        	  
            voUtil = new Utilerias();
            voMapConf = new HashMap<>();
            voUtil.getProperties(voMapConf,"");
            pathArchivo = voMapConf.get("PathReporteFinal");
              
        	  
        	//Creamos nuestro archivo TXT
              try { 
           
            	
            	Archivo = pathArchivo + "temp\\Reporte_" + timeStamp;
            	boolean Ruta = createTempDirectory(Archivo);
            	if (Ruta == true) {
            		Temporal = Archivo;
                //Genero mi archivo temporal
            	Archivo =	Temporal + "\\" + timeStamp;
  				File files = new File(timeStamp+".txt"); 
  				write = new FileWriter(Temporal + "\\" + timeStamp+".txt");
  				//files.deleteOnExit();
                
  				
                //Recorro mi voConversations Map para saber que argumentos tiene cada Id de Llamada
        	  voLogger.info("[GeneradorTXT][" + UUI + "] ---> ******************** Iniciamos la Generación de los TXT *******************");
        	  int i = 1;
        	  for(String vsContactId : vlContactId) {
        		  
    		  for (Map.Entry entry : voConversations.entrySet()) {
        		  //Comparo mis Id's para crear el TXt y pintar el contenido de cada llamada
    			 
    			  if( entry.getKey().equals(vsContactId)) {
    		        	
    		          
    	        		Map<String, String> voDetails = voConversations.get(vsContactId);
    	        		String  conversationStart = String.valueOf(voDetails.get("ConversationStart"));
    	        		String  conversationEnd = String.valueOf(voDetails.get("vsConversationEnd"));
    	        		String  ani = String.valueOf(voDetails.get("ani"));
    	        		String  dnis = String.valueOf(voDetails.get("dnis"));
    	        		List<String> dataComplet = new ArrayList<>();
    	        		dataComplet.add(vsContactId);
    	        		dataComplet.add("Campania");
    	        		dataComplet.add("Agente");
    	        		dataComplet.add(ani);
    	        		dataComplet.add(dnis);
    	        		dataComplet.add(conversationStart);
    	        		dataComplet.add("FechaContestacion");
    	        		dataComplet.add("TiempoEspera");
    	        		dataComplet.add(conversationEnd);
    	        		dataComplet.add("DuracionLlamada");
    	        		dataComplet.add("FechaDefinicion");
    	        		dataComplet.add("TiempoDefinicion");
    	        		dataComplet.add("Calificacion");
    	        		dataComplet.add("ENDOSO_Calificacion");
    	        		dataComplet.add("ENDOSO_NombreSolicitante");
    	        		dataComplet.add("ENDOSO_Poliza");
    	        		dataComplet.add("ENDOSO_ProcedeEndoso");
    	        		dataComplet.add("ENDOSO_MotivoNoEndoso");
    	        		dataComplet.add("INCIDENCIA_Calificacion");
    	        		dataComplet.add("INCIDENCIA_NombreQuienHabla");
    	        		dataComplet.add("INCIDENCIA_OrigenIncidencia");
    	        		dataComplet.add("INCIDENCIA_ProcesoAfectado");
    	        		dataComplet.add("LLAMADACORTADA_Calificacion");
    	        		dataComplet.add("LLAMADACORTADA_Comentarios");
    	        		dataComplet.add("LLAMADAOTRAAREA_Calificacion");
    	        		dataComplet.add("LLAMADAOTRAAREA_NombreQuienHabla");
    	        		dataComplet.add("LLAMADAOTRAAREA_AreaSolicitada");
    	        		dataComplet.add("ENVIODOCUMENTOS_Calificacion");
    	        		dataComplet.add("ENVIODOCUMENTOS_NombreQuienHabla");
    	        		dataComplet.add("ENVIODOCUMENTOS_CorreoElectronico");
    	        		dataComplet.add("ENVIODOCUMENTOS_DocumentoSolicitado");
    	        		dataComplet.add("CONSULTAPROCESODEYEL_Calificacion");
    	        		dataComplet.add("CONSULTAPROCESODEYEL_NombreQuienHabla");
    	        		dataComplet.add("CONSULTAPROCESODEYEL_MotivoConsulta");
    	        		dataComplet.add("CONSULTA_Calificacion");
    	        		dataComplet.add("CONSULTA_NombreQuienHabla");
    	        		dataComplet.add("CONSULTA_MotivoConsulta");
    	        		dataComplet.add("ACTUALIZACIONCORREODEYEL_Calificacion");
    	        		dataComplet.add("ACTUALIZACIONCORREODEYEL_NombreQuienHabla");
    	        		dataComplet.add("ACTUALIZACIONCORREODEYEL_MotivoConsulta");
    	        		dataComplet.add("SOLICITUDREFERENCIADEYEL_Calificacion");
    	        		dataComplet.add("SOLICITUDREFERENCIADEYEL_NombreQuienHabla");
    	        		dataComplet.add("SOLICITUDREFERENCIADEYEL_MotivoConsulta");
    	        		dataComplet.add("SOLICITUDUSUARIODEYEL_Calificacion");
    	        		dataComplet.add("SOLICITUDUSUARIODEYEL_NombreQuienHabla");
    	        		dataComplet.add("SOLICITUDUSUARIODEYEL_MotivoConsulta");
    	        		dataComplet.add("DOMICILIACION_Calificacion");
    	        		dataComplet.add("DOMICILIACION_NombreQuienHabla");
    	        		dataComplet.add("DOMICILIACION_ClaveAgente");
    	        		dataComplet.add("DOMICILIACION_NumSucursal");
    	        		dataComplet.add("DOMICILIACION_NumRamo");
    	        		dataComplet.add("DOMICILIACION_NumPoliza");
    	        		dataComplet.add("DOMICILIACION_Movimiento");
    	        		dataComplet.add("CARGORECURRENTE_Calificacion");
    	        		dataComplet.add("CARGORECURRENTE_NombreQuienHabla");
    	        		dataComplet.add("CARGORECURRENTE_ClaveAgente");
    	        		dataComplet.add("CARGORECURRENTE_NumSucursal");
    	        		dataComplet.add("CARGORECURRENTE_NumRamo");
    	        		dataComplet.add("CARGORECURRENTE_NumPoliza");
    	        		dataComplet.add("CARGORECURRENTE_Rehabilitacion");
    	        		dataComplet.add("CARGORECURRENTE_Cancelacion");
    	        		dataComplet.add("CARGORECURRENTE_CobroExitoso");
    	        		dataComplet.add("CARGORECURRENTE_Motivo");
    	        		dataComplet.add("EMISION_Calificacion");
    	        		dataComplet.add("EMISION_NombreQuienHabla");
    	        		dataComplet.add("EMISION_ClaveAgente");
    	        		dataComplet.add("EMISION_NumCotizacion");
    	        		dataComplet.add("EMISION_NumSucursal");
    	        		dataComplet.add("EMISION_NumRamo");
    	        		dataComplet.add("EMISION_NumPoliza");
    	        		dataComplet.add("EMISION_PagoAceptado");
    	        		dataComplet.add("EMISION_Importe");
    	        		dataComplet.add("EMISION_Motivo");
    	        		dataComplet.add("COTIZACION_Calificacion");
    	        		dataComplet.add("COTIZACION_NombreQuienHabla");
    	        		dataComplet.add("COTIZACION_ClaveAgente");
    	        		dataComplet.add("COTIZACION_CorreoElectronico");
    	        		dataComplet.add("COTIZACION_NumeroCotizacion");
    	        		dataComplet.add("ENVIOLINK_Calificacion");
    	        		dataComplet.add("ENVIOLINK_NombreQuienHabla");
    	        		dataComplet.add("ENVIOLINK_CorreoElectronico");
    	        		dataComplet.add("TRANSFERENCIAIVR_Calificacion");
    	        		dataComplet.add("TRANSFERENCIAIVR_NombreQuienHabla");
    	        		dataComplet.add("PAGO_Calificacion");
    	        		dataComplet.add("PAGO_NombreQuienHabla");
    	        		dataComplet.add("PAGO_ClaveAgente");
    	        		dataComplet.add("PAGO_NumSucursal");
    	        		dataComplet.add("PAGO_NumRamo");
    	        		dataComplet.add("PAGO_NumPoliza");
    	        		dataComplet.add("PAGO_NumImporte");
    	        		dataComplet.add("PAGO_PagoAceptado");
    	        		dataComplet.add("PAGO_Motivo");
    	        		
    	        		int a=0;
    	        		
    	        		for (String data : dataComplet ) {
    	        			//Escribir sobre el archivo
        	        		try {
        	        			int f = a -1;
        	        			//Leemos nuestro archivo creado
        	        			Writer  output = new BufferedWriter(new FileWriter(Archivo+".txt", true));
        	        			
        	        			if (f == 90) {
        	        				output.append(dataComplet.get(a));
            	        			output.close(); 
        	        			}else {
        	        				output.append(dataComplet.get(a) + ",");
            	        			output.close(); 
        	        			}
        	        				
        	        				
      	  				
        	        		}catch(Exception e)  { 
        	          			voLogger.error("[Generador][" + UUI + "] ---> ERROR : NO SE LOGRÓ ESCRIBIR EN EL ARCHIVO DE NOMBRE [" + timeStamp + "]" );              
        	          		}

    	        			a++;
    	        		}
    	        		//mandamos un salto de línea
	        			Writer  output = new BufferedWriter(new FileWriter(Archivo+".txt", true));
	        			output.append("\n");
	        			output.close(); 
    	        		
    	        		dataComplet.clear();
    	        		
    	        		
    	        				
    	                   
    	        		  }
    			  
    			  
        		}
    		  i ++;
        	  }
        	  
        	  try {
                  Thread.sleep(5000);
                  } catch (InterruptedException ex) {
                  voLogger.error("[Generador][" + UUI + "] ---> ERROR : en la interrupción [" + ex.getMessage() + "]" );
                  }
			 
        	 
        	 nameTxt = new ArrayList<>();
             nameTxt.add(timeStamp);
             
        	 voLogger.info("[GeneradorTXT][" + UUI + "] ---> Se Generaron [\\" +   voConversations.size() + "\\] Archivos TXT" );
                                
            	}else {
            		voLogger.error("[Generador][" + UUI + "] ---> ERROR : NO SE  CREO LA CARPETA TEMPORAL" ); 
                        //Se tendria que terminar el programa aquí con algun return o break
            	}
            	
  				
  				
  				
  				
          		}catch(Exception e)  { 
          			voLogger.error("[Generador][" + UUI + "] ---> ERROR : NO SE CREO EL ARCHIVO TXT");              
          		}
        	  
        	 

            
     
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
                 
          
   

