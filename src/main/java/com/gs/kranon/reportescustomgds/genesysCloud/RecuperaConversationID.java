 package com.gs.kranon.reportescustomgds.genesysCloud;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.gs.kranon.reportescustomgds.DataReports;
import com.gs.kranon.reportescustomgds.conexionHttp.ConexionHttp;
import com.gs.kranon.reportescustomgds.conexionHttp.ConexionResponse;
import com.gs.kranon.reportescustomgds.cuadroMando.ReporteMail;
import com.gs.kranon.reportescustomgds.utilidades.Utilerias;

public class RecuperaConversationID {

	 static {
	        System.setProperty("dateLog", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
	    }
	    private static final Logger voLogger = LogManager.getLogger("Reporte");

	    private String vsUUI = "";
	    private String vsFechaInicio = "";
	    private String vsFechaFin = "";
	    private String vsFlowName = "";
	    private String vsFlowName1 = "";

	    private DataReports voDataReport = null;
	    private ConexionHttp voConexionHttp = null;
	    private GenesysCloud voPureCloud = null;
	    private String strUrlFinal;
	    private Utilerias voUti;


	    private Map<String, String> voMapConf = null;
	    private Map<String, Map<String, String>> voConversations;
	    private List<String> vlContactId = null;
	    private Map<String, String> voDetailsConversations;
	    private boolean vbActivo = true;
	    
	    public  RecuperaConversationID (String uui) {
	        voMapConf = new HashMap<>();
	        voPureCloud = new GenesysCloud();
	        voUti = new Utilerias();
	        vsUUI = uui;
	        voUti.getProperties(voMapConf, uui);
	    }
	    
	 public List<String> RecuperaConverStatID(String vsToken,String vsUUI,String originationDirection,String vsFecha,String vsFechaIniciotime,String vsFechaFinTime,String urlArchivoTemp,boolean ReturnError){
		 
		 ConexionResponse voConexionResponse;
         //recuperamos las variables a comparar para ir por nuestros ID's

         voLogger.info("[RecuperaConversationID][" + vsUUI + "] ---> ******************** RECUPERA CONVERSATIONS ID *******************");

         String vsURLPCDetails = "https://api.mypurecloud.com/api/v2/analytics/conversations/details/query";
         voConexionHttp = new ConexionHttp();
         voConversations = new HashMap<>();
         vlContactId = new ArrayList<>();
         HashMap<String, String> voHeader = new HashMap<>();
         voHeader.put("Authorization", "bearer " + vsToken);
         Integer viPag = 0;
         /*
         do {
        	 
             viPag++;
             voLogger.info("[RecuperaConversationID][" + vsUUI + "] ---> LOADING PAGE NUMBER [" + (viPag) + "]");

             voPureCloud.vsHorarioInterval = (voMapConf.get("HorarioVerano").trim().toUpperCase().contentEquals("TRUE")) ? "T05:00:00.000Z" : "T06:00:00.000Z";
            // String vsBody = voPureCloud.getBody(viPag, "2022-04-10T20:00:00", "2022-04-10T21:00:00",originationDirection);
             String vsBody = voPureCloud.getBody(viPag, vsFechaIniciotime, vsFechaFinTime,originationDirection);
            //System.out.println(vsBody);
             voLogger.info("[RecuperaConversationID][" + vsUUI + "] ---> ENDPOINT[" + vsURLPCDetails + "]");
             
             try {
                 voConexionResponse = voConexionHttp.executePost(vsURLPCDetails, 15000, vsBody, voHeader);
             } catch (Exception e) {
                 voLogger.error("[RecuperaConversationID][" + vsUUI + "] ERROR : " + e.getMessage());
                 break;
             }
             //System.out.println("["+new SimpleDateFormat("dd-mm-yyyy HH:mm:ss").format(Calendar.getInstance().getTime())+"]-->   Consiguiendo pagina: "+viPag+". Respuesta: "+voConexionResponse.getCodigoRespuesta());
             if (voConexionResponse.getCodigoRespuesta() == 200) {
                 String vsJsonResponse = voConexionResponse.getMensajeRespuesta();                
                 JSONObject voJsonConversations = new JSONObject(vsJsonResponse);

                 if (vsJsonResponse.equals("{}") || !voJsonConversations.has("conversations")) {
                     voLogger.error("[RecuperaConversationID][" + vsUUI + "] ---> CONVERSATIONS FOUND [0]");
                     break;
                 }
        
                 if (voJsonConversations.has("conversations")) {
                     JSONArray voJsonArrayConversations = voJsonConversations.getJSONArray("conversations");
                     voLogger.info("[RecuperaConversationID][" + vsUUI + "] ---> CONVERSATIONS FOUND[" + voJsonArrayConversations.length() + "]");
                     for (int i = 0; i < voJsonArrayConversations.length(); i++) {

                         voDetailsConversations = new HashMap<>();
                         String vsIdConversation = voJsonArrayConversations.getJSONObject(i).getString("conversationId");
                         vlContactId.add(vsIdConversation); 
                         ReporteMail.arrContactId.add(vsIdConversation);
                        //System.out.println("El ID es este " + vsIdConversation);
                     }
                     
                 } else {
                	 
                     voLogger.error("[RecuperaConversationID][" + vsUUI + "] ---> CODE[" + voConexionResponse.getCodigoRespuesta()
                             + "], MESSAGE ERROR[" + voConexionResponse.getMensajeError() + "]");
                     vbActivo = false;
                     break;
                 }
             }else{
            	 if(ReturnError==false) {
            		
             		PagesNoProcessed(vsFechaIniciotime,voConexionResponse.getCodigoRespuesta(),urlArchivoTemp,vsUUI,vsFecha,vsFechaFinTime,viPag);
             		break;
             	}else {
             		PagesNoProcessedCsv(vsFechaIniciotime,voConexionResponse.getCodigoRespuesta(),urlArchivoTemp,vsUUI,vsFecha,vsFechaFinTime,viPag);
             		break;
             	}
             }
         } while (true);
         */
         //OBTENIENDO TODOS LOS CONVERSATION ID DEL RANGO DE HORAS EN EL DIA
         vlContactId.add("d5cd8350-f440-465f-bf44-9f459a06954d");
       //  vlContactId.add("1cfd066a-c319-4bb9-a5ad-a8a29d3f4bf4");
         
         /*
          *   vlContactId.add("35d5ee86-0b39-429a-8532-0a42b3da0127"); 
          *   vlContactId.add("35d5ee86-0b39-429a-8532-0a42b3da0127");
			 * 
			 */
         
			ReporteMail.paginasRetornadas = ReporteMail.paginasRetornadas + --viPag;
         voLogger.info("[RecuperaConversationID][" + vsUUI + "] \"RESPONSE[{\"totalHits\":\"\" [" + vlContactId.size() + "]");
         return vlContactId;
	 }
	 
	 
	 
	 
	 
	 public  void PagesNoProcessed(String strStartTime,int getCodigoRespuesta, String urlArchivoTemp,String vsUUi,String strFecha,String strFinalTime,int strPage) {
		    
	    
	    	strUrlFinal = urlArchivoTemp+ File.separator + vsUUi + "_page_PC_TEMP.csv";
	    	ReporteMail.paginasRetornadasErr = ReporteMail.paginasRetornadasErr + 1;	
	    		File  fw = new File (strUrlFinal);
	    		//Validamos si el archivo existe
	    		if(fw.exists()){
	    			
	    			try {
						Writer  output = new BufferedWriter(new FileWriter(strUrlFinal, true));
						output.append(String.valueOf(getCodigoRespuesta));
						output.append(',');
						output.append(strFecha);
						output.append(',');
						output.append(strStartTime);
						output.append(',');
						output.append(strFinalTime);
						output.append(',');
						output.append(String.valueOf(strPage));
						output.append('\n');
						output.close();
					} catch (IOException e1) {
						
						e1.printStackTrace();
					}
	            }else{
	                //Archivo NO existe, lo crea.
				try (PrintWriter writer = new PrintWriter(new File(strUrlFinal))) {
					StringBuilder linea = new StringBuilder();
					linea.append(getCodigoRespuesta);
					linea.append(',');
					linea.append(strFecha);
					linea.append(',');
					linea.append(strStartTime);
					linea.append(',');
					linea.append(strFinalTime);
					linea.append(',');
					linea.append(strPage);
					linea.append('\n');
					writer.write(linea.toString());
		            writer.close();
		            
	    	} catch (FileNotFoundException e) {
	            System.out.println(e.getMessage());
	        }
	            }
	    		
	        
	    }

	public boolean PagesNoProcessedCsv(String strStartTime,int getCodigoRespuesta, String urlArchivoTemp,String vsUUi,String strFecha,String strFinalTime,int strPage) {
	 
	    	String strCodigoRespuesta = String.valueOf(getCodigoRespuesta); ;
	    	strUrlFinal = urlArchivoTemp+ File.separator + "page_PE.csv";
	    	   	ReporteMail.lineasPagNoProcesadas = ReporteMail.lineasPagNoProcesadas + 1;
	    		File  fw = new File (strUrlFinal);
	    		//Validamos si el archivo existe
	    		if(fw.exists()){
	    			
	    			try {
						Writer  output = new BufferedWriter(new FileWriter(strUrlFinal, true));
						output.append(String.valueOf(getCodigoRespuesta));
						output.append(',');
						output.append(strFecha);
						output.append(',');
						output.append(strStartTime);
						output.append(',');
						output.append(strFinalTime);
						output.append(',');
						output.append(String.valueOf(strPage));
						output.append('\n');
						output.close();
					} catch (IOException e1) {
						
						e1.printStackTrace();
					}
	            }else{
	                //Archivo NO existe, lo crea.
				try (PrintWriter writer = new PrintWriter(new File(strUrlFinal))) {
					
					StringBuilder linea = new StringBuilder();
					linea.append("Codigo Respuesta");
					linea.append(',');
					linea.append("Fecha ");
					linea.append(',');
					linea.append("StartTime ");
					linea.append(',');
					linea.append("FinalTime ");
					linea.append(',');
					linea.append("Page ");
					linea.append('\n');
					
		            
					linea.append(getCodigoRespuesta);
					linea.append(',');
					linea.append(strFecha);
					linea.append(',');
					linea.append(strStartTime);
					linea.append(',');
					linea.append(strFinalTime);
					linea.append(',');
					linea.append(strPage);
					linea.append('\n');
					writer.write(linea.toString());
		            writer.close();
		            writer.write(linea.toString());
	    	} catch (FileNotFoundException e) {
	            System.out.println(e.getMessage());
	        }
	            }
	    		
	        return true;
	    } 	 
	
}
