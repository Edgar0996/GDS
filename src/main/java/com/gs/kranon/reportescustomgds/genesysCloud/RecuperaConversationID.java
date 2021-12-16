package com.gs.kranon.reportescustomgds.genesysCloud;

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
import com.gs.kranon.reportescustomgds.reporteador.GeneradorCSV;
import com.gs.kranon.reportescustomgds.reporteador.GeneradorTXT;
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
	    
	    private Utilerias voUti;


	    private Map<String, String> voMapConf = null;
	    private Map<String, Map<String, String>> voConversations;
	    private List<String> vlContactId = null;
	    private Map<String, String> voDetailsConversations;
	    private boolean vbActivo = true;
	    
	    public  RecuperaConversationID (DataReports voDataReport, String uui) {
	    	this.voDataReport = voDataReport;
	        voMapConf = new HashMap<>();
	        voPureCloud = new GenesysCloud();
	        voUti = new Utilerias();
	        vsUUI = uui;
	        voUti.getProperties(voMapConf, uui);
	    }
	    
	 public List<String> RecuperaConverStatID(String vsToken,String vsUUI,String originationDirection,String vsFecha,String strStartTime,String strFinalTime){
		 
		 ConexionResponse voConexionResponse;
         //recuperamos las variables a comparar para ir por nuestros ID's

         

         voLogger.info("[Reporteador][" + vsUUI + "] ---> ******************** STARTING REPORT *******************");

         String vsURLPCDetails = "https://api.mypurecloud.com/api/v2/analytics/conversations/details/query";
         voConexionHttp = new ConexionHttp();
         voConversations = new HashMap<>();
         vlContactId = new ArrayList<>();
         HashMap<String, String> voHeader = new HashMap<>();
         voHeader.put("Authorization", "bearer " + vsToken);
         Integer viPag = 0;

         //OBTENIENDO TODOS LOS CONVERSATION ID DEL RANGO DE HORAS EN EL DIA
         do {
        	 
             viPag++;
             voLogger.info("[ReuperaConvetID][" + vsUUI + "] ---> LOADING PAGE NUMBER [" + (viPag) + "]");

             voPureCloud.vsHorarioInterval = (voMapConf.get("HorarioVerano").trim().toUpperCase().contentEquals("TRUE")) ? "T05:00:00.000Z" : "T06:00:00.000Z";
             String vsBody = voPureCloud.getBody(viPag, vsFecha, vsFecha,originationDirection,strStartTime,strFinalTime);
             //System.out.println(vsBody);
             voLogger.info("[ReuperaConvetID][" + vsUUI + "] ---> ENDPOINT[" + vsURLPCDetails + "], REQUEST[" + vsBody.replace("\r\n", "") + "]");
             
             try {
                 voConexionResponse = voConexionHttp.executePost(vsURLPCDetails, 15000, vsBody, voHeader);
             } catch (Exception e) {
                 voLogger.error("[ReuperaConvetID][" + vsUUI + "] ERROR : " + e.getMessage());
                 break;
             }

             if (voConexionResponse.getCodigoRespuesta() == 200) {
                 String vsJsonResponse = voConexionResponse.getMensajeRespuesta();
                 voLogger.info("[ReuperaConvetID][" + vsUUI + "] ---> STATUS[" + voConexionResponse.getCodigoRespuesta() + "], "
                         + "RESPONSE[{\"totalHits\":\"" + new JSONObject(vsJsonResponse).getInt("totalHits") + "\"}]");
                
                 JSONObject voJsonConversations = new JSONObject(vsJsonResponse);

                 if (vsJsonResponse.equals("{}") || !voJsonConversations.has("conversations")) {
                     voLogger.info("[ReuperaConvetID][" + vsUUI + "] ---> CONVERSATIONS FOUND [0]");
                     break;
                 }
                 voLogger.info("[ReuperaConvetID][" + vsUUI + "Cadena JSON" + voJsonConversations);
                 if (voJsonConversations.has("conversations")) {
                     JSONArray voJsonArrayConversations = voJsonConversations.getJSONArray("conversations");
                     voLogger.info("[ReuperaConvetID][" + vsUUI + "] ---> CONVERSATIONS FOUND[" + voJsonArrayConversations.length() + "]");
                     for (int i = 0; i < voJsonArrayConversations.length(); i++) {

                         voDetailsConversations = new HashMap<>();
                         String vsIdConversation = voJsonArrayConversations.getJSONObject(i).getString("conversationId");
                         vlContactId.add(vsIdConversation);
                         
                     }
                     
                 } else {
                	 
                     voLogger.error("[ReuperaConvetID][" + vsUUI + "] ---> CODE[" + voConexionResponse.getCodigoRespuesta()
                             + "], MESSAGE ERROR[" + voConexionResponse.getMensajeError() + "]");
                     vbActivo = false;
                     break;
                 }
             }
         } while (true);
			
			  vlContactId.add("35d5ee86-0b39-429a-8532-0a42b3da012");
			  vlContactId.add("35d5ee86-0b39-429a-8532-0a42b3da013");
			 
         
         voLogger.info("[ReuperaConvetID][" + vsUUI + "] TOTAL DE ID'S POR BLOQUE DE HORA [" + vlContactId.size() + "]");
         return vlContactId;
	 }
	
}
