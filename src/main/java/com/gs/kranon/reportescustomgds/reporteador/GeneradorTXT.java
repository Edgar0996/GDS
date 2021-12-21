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
import java.util.Random;
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
	    
	       
    
    
     public List<String> GeneraTXT(List<String> vlContactId,Map<String, Map<String, String>> voConversations,String UUI,String urlArchivoTemp) {
    	 timeStamp = new SimpleDateFormat("yyyy_MM_dd HH.mm.ss").format(Calendar.getInstance().getTime());
         nameTxt.add(timeStamp);
	  
            voUtil = new Utilerias();
            voMapConf = new HashMap<>();
            voUtil.getProperties(voMapConf,"");
            pathArchivo = voMapConf.get("PathReporteFinal");
              
        	  
        	
              try { 
           
            	
                int min = 1;
          		int max = 100;
          		Random random = new Random();
          		int value = random.nextInt(max + min) + min;
            	Temporal = urlArchivoTemp;
                //Genero mi archivo temporal
            	Archivo =	Temporal + "\\" + timeStamp +"_" + value;
  				File files = new File(timeStamp+"_" + value +".txt"); 
  				write = new FileWriter(Temporal + "\\" + timeStamp+ "_" + value +".txt");
  				//files.deleteOnExit();
                
  				
                //Recorro mi voConversations Map para saber que argumentos tiene cada Id de Llamada
  				//voLogger.info("[GeneradorTXT][" + UUI + "] ---> ******************** Iniciamos la Generación de los TXT *******************");
        	  int i = 1;
        	//Comparo mis Id's para crear el TXt y pintar el contenido de cada llamada
        	  for(String vsContactId : vlContactId) {
        		  
    		  for (Map.Entry entry : voConversations.entrySet()) {
        		  
    			 
    			  if( entry.getKey().equals(vsContactId)) {
    				  
    	        		Map<String, String> voDetails = voConversations.get(vsContactId);
    	        		String  conversationStart = String.valueOf(voDetails.get("ConversationStart"));
    	        		if(conversationStart==null) {
    	        			conversationStart="";
    	        		}
    	        		
    	        		String  conversationEnd = String.valueOf(voDetails.get("vsConversationEnd"));
    	        		if(conversationEnd==null) {
    	        			conversationEnd="";
    	        		}
    	        		String  ani = String.valueOf(voDetails.get("ani"));
    	        		if(ani==null) {
    	        			ani="";
    	        		}
    	        		String  dnis = String.valueOf(voDetails.get("dnis"));
    	        		if(dnis==null) {
    	        			dnis="";
    	        		}
    	        		String Endoso_Procede = String.valueOf(voDetails.get("Endoso_Procede"));
    	        		
    	        		if(Endoso_Procede == "null") {
    	        			
    	        			Endoso_Procede=" ";
    	        		}
    	        		String Cotizacion_Hablante = String.valueOf(voDetails.get("Cotizacion_Hablante"));
    	        		if(Cotizacion_Hablante=="null") {
    	        			Cotizacion_Hablante="";
    	        		}
    	        		String Emision_No_Cobro = String.valueOf(voDetails.get("Emision_No_Cobro"));
    	        		if(Emision_No_Cobro=="null") {
    	        			Emision_No_Cobro="";
    	        		}
    	        		String Tramites_Escala = String.valueOf(voDetails.get("Tramites_Escala"));
    	        		if(Tramites_Escala=="null") {
    	        			Tramites_Escala="";
    	        		}
    	        		String Cargo_No_Cobro = String.valueOf(voDetails.get("Cargo_No_Cobro"));
    	        		if(Cargo_No_Cobro=="null") {
    	        			Cargo_No_Cobro="";
    	        		}
    	        		String Cotizacion_NúmCoti = String.valueOf(voDetails.get("Cotizacion_NúmCoti"));
    	        		if(Cotizacion_NúmCoti=="null") {
    	        			Cotizacion_NúmCoti="";
    	        		}
    	        		String Pago_No_Cobro = String.valueOf(voDetails.get("Pago_No_Cobro"));
    	        		if(Pago_No_Cobro=="null") {
    	        			Pago_No_Cobro="";
    	        		}
    	        		String Link_Hablante = String.valueOf(voDetails.get("Link_Hablante"));
    	        		if(Link_Hablante=="null") {
    	        			Link_Hablante="";
    	        		}
    	        		String Endoso_Solicitante = String.valueOf(voDetails.get("Endoso_Solicitante"));
    	        		if(Endoso_Solicitante=="null") {
    	        			Endoso_Solicitante="";
    	        		}
    	        		String Domiciliacion_Movimiento = String.valueOf(voDetails.get("Domiciliacion_Movimiento"));
    	        		if(Domiciliacion_Movimiento=="null") {
    	        			Domiciliacion_Movimiento="";
    	        		}
    	        		String LlamadaCortada_Comentarios = String.valueOf(voDetails.get("LlamadaCortada_Comentarios"));
    	        		if(LlamadaCortada_Comentarios=="null") {
    	        			LlamadaCortada_Comentarios="";
    	        		}
    	        		String Cargo_Cancelacion = String.valueOf(voDetails.get("Cargo_Cancelacion"));
    	        		if(Cargo_Cancelacion=="null") {
    	        			Cargo_Cancelacion="";
    	        		}
    	        		String Cotizacion_ClaveAgente = String.valueOf(voDetails.get("Cotizacion_ClaveAgente"));
    	        		if(Cotizacion_ClaveAgente=="null") {
    	        			Cotizacion_ClaveAgente="";
    	        		}
    	        		String Pago_Aceptado = String.valueOf(voDetails.get("Pago_Aceptado"));
    	        		if(Pago_Aceptado=="null") {
    	        			Pago_Aceptado="";
    	        		}
    	        		String TransferIVR_Hablante = String.valueOf(voDetails.get("TransferIVR_Hablante"));
    	        		if(TransferIVR_Hablante=="null") {
    	        			TransferIVR_Hablante="";
    	        		}
    	        		String Pago_OtroPago = String.valueOf(voDetails.get("Pago_OtroPago"));
    	        		if(Pago_OtroPago=="null") {
    	        			Pago_OtroPago="";
    	        		}
    	        		String EnvioDoctos_Hablante = String.valueOf(voDetails.get("EnvioDoctos_Hablante"));
    	        		if(EnvioDoctos_Hablante=="null") {
    	        			EnvioDoctos_Hablante="";
    	        		}
    	        		String Emision_Ramo = String.valueOf(voDetails.get("Emision_Ramo"));
    	        		if(Emision_Ramo=="null") {
    	        			Emision_Ramo="";
    	        		}
    	        		String Emision_Cotizacion = String.valueOf(voDetails.get("Emision_Cotizacion"));
    	        		if(Emision_Cotizacion=="null") {
    	        			Emision_Cotizacion="";
    	        		}
    	        		String Cargo_Cobro = String.valueOf(voDetails.get("Cargo_Cobro"));
    	        		if(Cargo_Cobro=="null") {
    	        			Cargo_Cobro="";
    	        		}
    	        		String Tramites_Consultado = String.valueOf(voDetails.get("Tramites_Consultado"));
    	        		if(Tramites_Consultado=="null") {
    	        			Tramites_Consultado="";
    	        		}
    	        		String Pago_Hablante = String.valueOf(voDetails.get("Pago_Hablante"));
    	        		if(Pago_Hablante=="null") {
    	        			Pago_Hablante="";
    	        		}
    	        		String Cotizacion_OtroCoti = String.valueOf(voDetails.get("Cotizacion_OtroCoti"));
    	        		if(Cotizacion_OtroCoti=="null") {
    	        			Cotizacion_OtroCoti="";
    	        		}
    	        		String Cargo_Ramo = String.valueOf(voDetails.get("Cargo_Ramo"));
    	        		if(Cargo_Ramo=="null") {
    	        			Cargo_Ramo="";
    	        		}
    	        		String LlamadaOtra_Hablante = String.valueOf(voDetails.get("LlamadaOtra_Hablante"));
    	        		if(LlamadaOtra_Hablante=="null") {
    	        			LlamadaOtra_Hablante="";
    	        		}
    	        		String Emision_Sucursal = String.valueOf(voDetails.get("Emision_Sucursal"));
    	        		if(Emision_Sucursal=="null") {
    	        			Emision_Sucursal="";
    	        		}
    	        		String EnvioDoctos_Solicitados = String.valueOf(voDetails.get("EnvioDoctos_Solicitados"));
    	        		if(EnvioDoctos_Solicitados=="null") {
    	        			EnvioDoctos_Solicitados="";
    	        		}
    	        		String Consulta_Motivo = String.valueOf(voDetails.get("Consulta_Motivo"));
    	        		if(Consulta_Motivo=="null") {
    	        			Consulta_Motivo="";
    	        		}
    	        		String Pago_Sucursal = String.valueOf(voDetails.get("Pago_Sucursal"));
    	        		if(Pago_Sucursal=="null") {
    	        			Pago_Sucursal="";
    	        		}
    	        		String Incidencia_Hablante = String.valueOf(voDetails.get("Incidencia_Hablante"));
    	        		if(Incidencia_Hablante=="null") {
    	        			Incidencia_Hablante="";
    	        		}
    	        		String Endoso_No_Procede = String.valueOf(voDetails.get("Endoso_No_Procede"));
    	        		if(Endoso_No_Procede=="null") {
    	        			Endoso_No_Procede="";
    	        		}
    	        		String Emision_Hablante = String.valueOf(voDetails.get("Emision_Hablante"));
    	        		if(Emision_Hablante=="null") {
    	        			Emision_Hablante="";
    	        		}
    	        		String EnvioDoctos_Email = String.valueOf(voDetails.get("EnvioDoctos_Email"));
    	        		if(EnvioDoctos_Email=="null") {
    	        			EnvioDoctos_Email="";
    	        		}
    	        		String Cargo_Hablante = String.valueOf(voDetails.get("Cargo_Hablante"));
    	        		if(Cargo_Hablante=="null") {
    	        			Cargo_Hablante="";
    	        		}
    	        		String Cargo_Sucursal = String.valueOf(voDetails.get("Cargo_Sucursal"));
    	        		if(Cargo_Sucursal=="null") {
    	        			Cargo_Sucursal="";
    	        		}
    	        		String Cargo_OtroMov = String.valueOf(voDetails.get("Cargo_OtroMov"));
    	        		if(Cargo_OtroMov=="null") {
    	        			Cargo_OtroMov="";
    	        		}
    	        		String LlamadaOtra_Area = String.valueOf(voDetails.get("LlamadaOtra_Area"));
    	        		if(LlamadaOtra_Area=="null") {
    	        			LlamadaOtra_Area="";
    	        		}
    	        		String Domiciliacion_Hablante = String.valueOf(voDetails.get("Domiciliacion_Hablante"));
    	        		if(Domiciliacion_Hablante=="null") {
    	        			Domiciliacion_Hablante="";
    	        		}
    	        		String Cargo_Rehabilitacion = String.valueOf(voDetails.get("Cargo_Rehabilitacion"));
    	        		if(Cargo_Rehabilitacion=="null") {
    	        			Cargo_Rehabilitacion="";
    	        		}
    	        		String Domiciliacion_Sucursal = String.valueOf(voDetails.get("Domiciliacion_Sucursal"));
    	        		if(Domiciliacion_Sucursal=="null") {
    	        			Domiciliacion_Sucursal="";
    	        		}
    	        		String Pago_Importe = String.valueOf(voDetails.get("Pago_Importe"));
    	        		if(Pago_Importe=="null") {
    	        			Pago_Importe="";
    	        		}
    	        		String Endoso_Poliza = String.valueOf(voDetails.get("Endoso_Poliza"));
    	        		if(Endoso_Poliza=="null") {
    	        			Endoso_Poliza="";
    	        		}
    	        		String Domiciliacion_OtroMov = String.valueOf(voDetails.get("Domiciliacion_OtroMov"));
    	        		if(Domiciliacion_OtroMov=="null") {
    	        			Domiciliacion_OtroMov="";
    	        		}
    	        		String Domiciliacion_Ramo = String.valueOf(voDetails.get("Domiciliacion_Ramo"));
    	        		if(Domiciliacion_Ramo=="null") {
    	        			Domiciliacion_Ramo="";
    	        		}
    	        		String Domiciliacion_Poliza = String.valueOf(voDetails.get("Domiciliacion_Poliza"));
    	        		if(Domiciliacion_Poliza=="null") {
    	        			Domiciliacion_Poliza="";
    	        		}
    	        		String Emision_PagoAcep = String.valueOf(voDetails.get("Emision_PagoAcep"));
    	        		if(Emision_PagoAcep=="null") {
    	        			Emision_PagoAcep="";
    	        		}
    	        		String Domiciliacion_ClaveAgente = String.valueOf(voDetails.get("Domiciliacion_ClaveAgente"));
    	        		if(Domiciliacion_ClaveAgente=="null") {
    	        			Domiciliacion_ClaveAgente="";
    	        		}
    	        		String Cargo_Poliza = String.valueOf(voDetails.get("Cargo_Poliza"));
    	        		if(Cargo_Poliza=="null") {
    	        			Cargo_Poliza="";
    	        		}
    	        		String Pago_Ramo = String.valueOf(voDetails.get("Pago_Ramo"));
    	        		if(Pago_Ramo=="null") {
    	        			Pago_Ramo="";
    	        		}
    	        		String Emision_ClaveAgente = String.valueOf(voDetails.get("Emision_ClaveAgente"));
    	        		if(Emision_ClaveAgente=="null") {
    	        			Emision_ClaveAgente="";
    	        		}
    	        		String Incidencia_Afectacion = String.valueOf(voDetails.get("Incidencia_Afectacion"));
    	        		if(Incidencia_Afectacion=="null") {
    	        			Incidencia_Afectacion="";
    	        		}
    	        		String Pago_ClaveAgente = String.valueOf(voDetails.get("Pago_ClaveAgente"));
    	        		if(Pago_ClaveAgente=="null") {
    	        			Pago_ClaveAgente="";
    	        		}
    	        		String Consulta_Hablante = String.valueOf(voDetails.get("Consulta_Hablante"));
    	        		if(Consulta_Hablante=="null") {
    	        			Consulta_Hablante="";
    	        		}
    	        		String Link_Email = String.valueOf(voDetails.get("Link_Email"));
    	        		if(Link_Email=="null") {
    	        			Link_Email="";
    	        		}
    	        		String Tramites_PersonaEscalada = String.valueOf(voDetails.get("Tramites_PersonaEscalada"));
    	        		if(Tramites_PersonaEscalada=="null") {
    	        			Tramites_PersonaEscalada="";
    	        		}
    	        		String Emision_Importe = String.valueOf(voDetails.get("Emision_Importe"));
    	        		if(Emision_Importe=="null") {
    	        			Emision_Importe="";
    	        		}
    	        		String Incidencia_Origen = String.valueOf(voDetails.get("Incidencia_Origen"));
    	        		if(Incidencia_Origen=="null") {
    	        			Incidencia_Origen="";
    	        		}
    	        		String Cotizacion_Email = String.valueOf(voDetails.get("Cotizacion_Email"));
    	        		if(Cotizacion_Email=="null") {
    	        			Cotizacion_Email="";
    	        		}
    	        		String Tramites_ClaveAgente = String.valueOf(voDetails.get("Tramites_ClaveAgente"));
    	        		if(Tramites_ClaveAgente=="null") {
    	        			Tramites_ClaveAgente="";
    	        		}
    	        		String Pago_Poliza = String.valueOf(voDetails.get("Pago_Poliza"));
    	        		if(Pago_Poliza=="null") {
    	        			Pago_Poliza="";
    	        		}
    	        		String Emision_Poliza = String.valueOf(voDetails.get("Emision_Poliza"));
    	        		if(Emision_Poliza=="null") {
    	        			Emision_Poliza="";
    	        		}
    	        		String Cargo_ClaveAgente = String.valueOf(voDetails.get("Cargo_ClaveAgente"));
    	        		if(Cargo_ClaveAgente=="null") {
    	        			Cargo_ClaveAgente="";
    	        		}
    	        		String Emision_OtroMov = String.valueOf(voDetails.get("Emision_OtroMov"));
    	        		if(Emision_OtroMov=="null") {
    	        			Emision_OtroMov="";
    	        		}
    	        		
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
    	        		dataComplet.add(Endoso_Procede);
    	        		dataComplet.add(Cotizacion_Hablante);
    	        		dataComplet.add(Emision_No_Cobro);
    	        		dataComplet.add(Tramites_Escala);
    	        		dataComplet.add(Cargo_No_Cobro);
    	        		dataComplet.add(Cotizacion_NúmCoti);
    	        		dataComplet.add(Pago_No_Cobro);
    	        		dataComplet.add(Link_Hablante);
    	        		dataComplet.add(Endoso_Solicitante);
    	        		dataComplet.add(Domiciliacion_Movimiento);
    	        		dataComplet.add(LlamadaCortada_Comentarios);
    	        		dataComplet.add(Cargo_Cancelacion);
    	        		dataComplet.add(Cotizacion_ClaveAgente);
    	        		dataComplet.add(Pago_Aceptado);
    	        		dataComplet.add(TransferIVR_Hablante);
    	        		dataComplet.add(Pago_OtroPago);
    	        		dataComplet.add(EnvioDoctos_Hablante);
    	        		dataComplet.add(Emision_Ramo);
    	        		dataComplet.add(Emision_Cotizacion);
    	        		dataComplet.add(Cargo_Cobro);
    	        		dataComplet.add(Tramites_Consultado);
    	        		dataComplet.add(Pago_Hablante);
    	        		dataComplet.add(Cotizacion_OtroCoti);
    	        		dataComplet.add(Cargo_Ramo);
    	        		dataComplet.add(LlamadaOtra_Hablante);
    	        		dataComplet.add(Emision_Sucursal);
    	        		dataComplet.add(EnvioDoctos_Solicitados);
    	        		dataComplet.add(Consulta_Motivo);
    	        		dataComplet.add(Pago_Sucursal);
    	        		dataComplet.add(Incidencia_Hablante);
    	        		dataComplet.add(Endoso_No_Procede);
    	        		dataComplet.add(Emision_Hablante);
    	        		dataComplet.add(EnvioDoctos_Email);
    	        		dataComplet.add(Cargo_Hablante);
    	        		dataComplet.add(Cargo_Sucursal);
    	        		dataComplet.add(Cargo_OtroMov);
    	        		dataComplet.add(LlamadaOtra_Area);
    	        		dataComplet.add(Domiciliacion_Hablante);
    	        		dataComplet.add(Cargo_Rehabilitacion);
    	        		dataComplet.add(Domiciliacion_Sucursal);
    	        		dataComplet.add(Pago_Importe);
    	        		dataComplet.add(Endoso_Poliza);
    	        		dataComplet.add(Domiciliacion_OtroMov);
    	        		dataComplet.add(Domiciliacion_Ramo);
    	        		dataComplet.add(Domiciliacion_Poliza);
    	        		dataComplet.add(Emision_PagoAcep);
    	        		dataComplet.add(Domiciliacion_ClaveAgente);
    	        		dataComplet.add(Cargo_Poliza);
    	        		dataComplet.add(Pago_Ramo);
    	        		dataComplet.add(Emision_ClaveAgente);
    	        		dataComplet.add(Incidencia_Afectacion);
    	        		dataComplet.add(Pago_ClaveAgente);
    	        		dataComplet.add(Consulta_Hablante);
    	        		dataComplet.add(Link_Email);
    	        		dataComplet.add(Tramites_PersonaEscalada);
    	        		dataComplet.add(Emision_Importe);
    	        		dataComplet.add(Incidencia_Origen);
    	        		dataComplet.add(Cotizacion_Email);
    	        		dataComplet.add(Tramites_ClaveAgente);
    	        		dataComplet.add(Pago_Poliza);
    	        		dataComplet.add(Emision_Poliza);
    	        		dataComplet.add(Cargo_ClaveAgente);
    	        		dataComplet.add(Emision_OtroMov);
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
    	        		//Rercorro mi data para pintar línea por línea en mi archivo txt
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
        	          			voLogger.error("[GeneradorTXT][" + UUI + "] ---> ERROR : NO SE LOGRÓ ESCRIBIR EN EL ARCHIVO DE NOMBRE [" + timeStamp + "]" );              
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
        	  
        	  //try {
                  //Thread.sleep(5000);
                  //} catch (InterruptedException ex) {
                  //voLogger.error("[Generador][" + UUI + "] ---> ERROR : en la interrupción [" + ex.getMessage() + "]" );
                  //}
			 
        	 
        	 nameTxt = new ArrayList<>();
             nameTxt.add(files.getName());
             
        	 voLogger.info("[GeneradorTXT][" + UUI + "] ---> SE AGREGARON  [\\" +   voConversations.size() + "\\] CONVERSATIONS ID EN EL  TXT" );
                                
            	
            	
  				
  				
  				
  				
          		}catch(Exception e)  { 
          			voLogger.error("[GeneradorTXT][" + UUI + "] ---> ERROR : NO SE CREO EL ARCHIVO TXT");              
          		}
        	  
        	 

            
  
      return nameTxt;
      
  }
     
    
     
       
         
        }
                 
          
   

