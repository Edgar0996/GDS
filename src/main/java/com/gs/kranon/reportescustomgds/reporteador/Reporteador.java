package com.gs.kranon.reportescustomgds.reporteador;

import com.gs.kranon.reportescustomgds.DataReportGDSmx;
import com.gs.kranon.reportescustomgds.GDSmx;
import com.gs.kranon.reportescustomgds.DataReports;
import com.gs.kranon.reportescustomgds.utilidades.Excel;
import com.gs.kranon.reportescustomgds.utilidades.Utilerias;
import com.gs.kranon.reportescustomgds.conexionHttp.ConexionHttp;
import com.gs.kranon.reportescustomgds.conexionHttp.ConexionResponse;
import com.gs.kranon.reportescustomgds.cuadroMando.ReporteMail;
import com.gs.kranon.reportescustomgds.genesysCloud.GenesysCloud;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Reporteador extends  Thread  {

    static {
        System.setProperty("dateLog", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
    }
    private static final Logger voLogger = LogManager.getLogger("Reporte");

    private String vsUUi = "";
    private String vsToken= "";
    private String vsFechaInicio = "";
    private String vsFechaFin = "";
    private String vsFlowName = "";
    private String vsFlowName1 = "";
    private String strUrlFinal;

    private DataReports voDataReport = null;
    private ConexionHttp voConexionHttp = null;
    private GenesysCloud voPureCloud = null;
    private GeneradorTXT GenraTXT = null;
    private GeneradorCSV GenraCSV = null;
    private Utilerias voUti;
   
    private Map<String, String> voMapConf = null;
    private Map<String, Map<String, String>> voConversations;
    private List<String> vlContact = null;
    private List<String> nameTxt;
    private List<String> vlContactId;
    private Map<String, String> voDetailsConversations;
    private String urlArchivoTemp =null;
    private Map<String, Object> voMapHeaderCSV = new HashMap<String, Object>();
    private boolean ReturnErro;
    private JSONObject voJSONAgent;
    

    public Reporteador(String uui,String vsTokens,String vsUUI,List<String> vlContactIds,String urlArchivoTem,boolean ReturnError) {
        this.voDataReport = voDataReport;
        voMapConf = new HashMap<>();
        voPureCloud = new GenesysCloud();
        voUti = new Utilerias();
        vsUUi = vsUUI;
        vsToken = vsTokens;
        vlContactId=vlContactIds;
        voUti.getProperties(voMapConf, uui);
        urlArchivoTemp=urlArchivoTem;
        ReturnErro=ReturnError;
    }
   
 
    public synchronized void run() {

    	 
    	//voLogger.info("[Reporteador][" + vsUUi + "] ---> *************CONSULTANDO EL DETALLE DE LOS CONVERSATIONSID*************** ");
                ConexionResponse voConexionResponse;
                voConexionHttp = new ConexionHttp();
                HashMap<String, String> voHeader = new HashMap<>();
                vlContact = new ArrayList<>();
                voConversations = new HashMap<>();
                voHeader.put("Authorization", "bearer " + vsToken);
                voConversations = new HashMap<>();
                //COMENZAREMOS A ANALIZAR CADA ID DE CONVERSACION PARA EXTRAER SUS BREADCRUMBS
                int viContadorEncontrados = 0;
                String vsURLPCCall = "https://api.mypurecloud.com/api/v2/conversations/";
                ConexionResponse voConexionResponseCall = null;
                
                for (String vsContactId : vlContactId) {
                    String vsURLConversation = vsURLPCCall + vsContactId;
                    viContadorEncontrados++;
                    //voLogger.info("[Reporteador][" + vsUUi + "] ---> [" + (viContadorEncontrados) + "] ENDPOINT[" + vsURLConversation + "]");
                    try {
                        voConexionResponseCall = voConexionHttp.executeGet(vsURLConversation, 15000, voHeader, null);
                    } catch (Exception e) {
                        voLogger.error("[Reporteador][" + vsUUi + "] ---> CONTACT_ID [" + vsContactId + "] : " + e.getMessage());
                    }
                    
                    //String vsJsonResponse = voConexionResponseCall.getMensajeRespuesta();
                    
                    if(voConexionResponseCall.getCodigoRespuesta() == 200) {
                    		
                   
                    JSONObject voJsonResponseCall = new JSONObject(voConexionResponseCall.getMensajeRespuesta());
                    voLogger.info("[Reporteador][" + vsUUi + "] ---> [" + (viContadorEncontrados) + "] ENDPOINT[\"" + vsURLConversation + "\"]\""
                            + " RESPONSE: STATUS[" + voConexionResponseCall.getCodigoRespuesta() + "]");
                   
                    
                    
                    if (voJsonResponseCall.has("participants")) {
                    	      
                    	JSONArray voJsonArrayResponseCall = voJsonResponseCall.getJSONArray("participants");
                    	//System.out.println("Este ID " + vsContactId + " Tiene este largo de participants " + voJsonArrayResponseCall.length());  
                    	
                    	voDetailsConversations = new HashMap<>();
                		String vsConversationStart = voJsonArrayResponseCall.getJSONObject(0).getString("startTime");
                		String vsConversationEnd = voJsonArrayResponseCall.getJSONObject(0).getString("endTime");
                		String vsAni = voJsonArrayResponseCall.getJSONObject(0).getString("ani");
                		String vsDnis = voJsonArrayResponseCall.getJSONObject(0).getString("dnis");

                		vlContact.add(vsContactId);
                		voDetailsConversations.put("ani", vsAni);
                        voDetailsConversations.put("dnis", vsDnis);
                        voDetailsConversations.put("ConversationStart", vsConversationStart );
                        voDetailsConversations.put("vsConversationEnd", vsConversationEnd);
                        //voDetailsConversations.put("queueName", queueName);
                        //Recupero los datos del Agente
                        for (int r = 0; r < voJsonArrayResponseCall.length(); r++) { 
                        	JSONObject voJSONParticipants = voJsonArrayResponseCall.getJSONObject(r);
                        	String purpose = voJSONParticipants.getString("purpose");
                        	//System.out.println("Mi primer purpose el: "+ r +" es "+ purpose);
                        	if(purpose.equals("agent")) {
								/*
								 * voJSONAgent=voJsonArrayResponseCall.getJSONObject(r); for(int s = 0; s <
								 * voJSONAgent.length(); s++) { System.out.println("El valor de mi jason es " +
								 * voJSONAgent.getString("participantType") ); }
								 */
                        		String Agente = voJsonArrayResponseCall.getJSONObject(r).getString("name");
                    				 voDetailsConversations.put("Agente", Agente);
                    			String queueName = voJsonArrayResponseCall.getJSONObject(r).getString("queueName");
                    				 voDetailsConversations.put("queueName", queueName);
                    				 
                    			
                        	}
                        }
                        
                       
                        
                        //Recupero los atributos de la llamada	
                    	for (int j = 0; j < voJsonArrayResponseCall.length(); j++) { 
                    		 if (voJsonArrayResponseCall.getJSONObject(j).getJSONObject("attributes").length() > 0) {
                    			 JSONObject voJSONAttributes = voJsonArrayResponseCall.getJSONObject(j).getJSONObject("attributes");
                    			 
                    			 if (voJSONAttributes.has("Endoso_Procede")) {
                    				 String Endoso_Procede = voJSONAttributes.getString("Endoso_Procede");
                    				 String Endoso_ProcedeSinComas= Endoso_Procede.replace(" ,", ".");
                    				 voDetailsConversations.put("Endoso_Procede", Endoso_ProcedeSinComas);
                    				 
                    			 }
                    			 if (voJSONAttributes.has("Cotizacion_Hablante")) {
                					 String Cotizacion_Hablante = voJSONAttributes.getString("Cotizacion_Hablante");
                					 String Cotizacion_HablanteSinComas= Cotizacion_Hablante.replace(" ,", ".");
                    				 voDetailsConversations.put("Cotizacion_Hablante", Cotizacion_HablanteSinComas);
                    				
                				 }
                    			 if (voJSONAttributes.has("Emision_No_Cobro")) {
                					 String Emision_No_Cobro = voJSONAttributes.getString("Emision_No_Cobro");
                					 String Emision_No_CobroSinComas= Emision_No_Cobro.replace(" ,", ".");
                    				 voDetailsConversations.put("Emision_No_Cobro", Emision_No_CobroSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Tramites_Escala")) {
                					 String Tramites_Escala = voJSONAttributes.getString("Tramites_Escala");
                					 String Tramites_EscalaSinComas= Tramites_Escala.replace(" ,", ".");
                    				 voDetailsConversations.put("Tramites_Escala", Tramites_EscalaSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Cargo_No_Cobro")) {
                					 String Cargo_No_Cobro = voJSONAttributes.getString("Cargo_No_Cobro");
                					 String Cargo_No_CobroSinComas= Cargo_No_Cobro.replace(" ,", ".");
                    				 voDetailsConversations.put("Cargo_No_Cobro", Cargo_No_CobroSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Cotizacion_NúmCoti")) {
                					 String Cotizacion_NúmCoti = voJSONAttributes.getString("Cotizacion_NúmCoti");
                					 String Cotizacion_NúmCotiSinComas= Cotizacion_NúmCoti.replace(" ,", ".");
                    				 voDetailsConversations.put("Cotizacion_NúmCoti", Cotizacion_NúmCotiSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Pago_No_Cobro")) {
                					 String Pago_No_Cobro = voJSONAttributes.getString("Pago_No_Cobro");
                					 String Pago_No_CobroSinComas= Pago_No_Cobro.replace(" ,", ".");
                    				 voDetailsConversations.put("Pago_No_Cobro", Pago_No_CobroSinComas);
                    				
                				 }
                    			 if (voJSONAttributes.has("Link_Hablante")) {
                					 String Link_Hablante = voJSONAttributes.getString("Link_Hablante");
                					 String Link_HablanteSinComas= Link_Hablante.replace(" ,", ".");
                    				 voDetailsConversations.put("Link_Hablante", Link_HablanteSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Endoso_Solicitante")) {
                					 String Endoso_Solicitante = voJSONAttributes.getString("Endoso_Solicitante");
                					 String Endoso_SolicitanteSinComas= Endoso_Solicitante.replace(" ,", ".");
                    				 voDetailsConversations.put("Endoso_Solicitante", Endoso_SolicitanteSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Domiciliacion_Movimiento")) {
                					 String Domiciliacion_Movimiento = voJSONAttributes.getString("Domiciliacion_Movimiento");
                					 String Domiciliacion_MovimientoSinComas= Domiciliacion_Movimiento.replace(" ,", ".");
                    				 voDetailsConversations.put("Domiciliacion_Movimiento", Domiciliacion_MovimientoSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("LlamadaCortada_Comentarios")) {
                					 String LlamadaCortada_Comentarios = voJSONAttributes.getString("LlamadaCortada_Comentarios");
                					 String LlamadaCortada_ComentariosSinComas= LlamadaCortada_Comentarios.replace(" ,", ".");
                    				 voDetailsConversations.put("LlamadaCortada_Comentarios", LlamadaCortada_ComentariosSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Cargo_Cancelacion")) {
                					 String Cargo_Cancelacion = voJSONAttributes.getString("Cargo_Cancelacion");
                					 String Cargo_CancelacionSinComas= Cargo_Cancelacion.replace(" ,", ".");
                    				 voDetailsConversations.put("Cargo_Cancelacion", Cargo_CancelacionSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Cargo_Cancelacion")) {
                					 String Cotizacion_ClaveAgente = voJSONAttributes.getString("Cotizacion_ClaveAgente");
                					 String Cotizacion_ClaveAgenteSinComas= Cotizacion_ClaveAgente.replace(" ,", ".");
                    				 voDetailsConversations.put("Cotizacion_ClaveAgente", Cotizacion_ClaveAgenteSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Cargo_Cancelacion")) {
                					 String Pago_Aceptado = voJSONAttributes.getString("Pago_Aceptado");
                					 String Pago_AceptadoSinComas= Pago_Aceptado.replace(" ,", ".");
                    				 voDetailsConversations.put("Pago_Aceptado", Pago_AceptadoSinComas);
                    				
                				 }	
                    			 if (voJSONAttributes.has("TransferIVR_Hablante")) {
                					 String TransferIVR_Hablante = voJSONAttributes.getString("TransferIVR_Hablante");
                					 String TransferIVR_HablanteSinComas= TransferIVR_Hablante.replace(" ,", ".");
                    				 voDetailsConversations.put("TransferIVR_Hablante", TransferIVR_HablanteSinComas);
                    			
                				 }
                    			 if (voJSONAttributes.has("Pago_OtroPago")) {
                					 String Pago_OtroPago = voJSONAttributes.getString("Pago_OtroPago");
                					 String Pago_OtroPagoSinComas= Pago_OtroPago.replace(" ,", ".");
                    				 voDetailsConversations.put("Pago_OtroPago", Pago_OtroPagoSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("EnvioDoctos_Hablante")) {
                					 String EnvioDoctos_Hablante = voJSONAttributes.getString("EnvioDoctos_Hablante");
                					 String EnvioDoctos_HablanteSinComas= EnvioDoctos_Hablante.replace(" ,", ".");
                    				 voDetailsConversations.put("EnvioDoctos_Hablante", EnvioDoctos_HablanteSinComas);
                    				
                				 }
                    			 if (voJSONAttributes.has("Emision_Ramo")) {
                					 String Emision_Ramo = voJSONAttributes.getString("Emision_Ramo");
                					 String Emision_RamoSinComas= Emision_Ramo.replace(" ,", ".");
                    				 voDetailsConversations.put("Emision_Ramo", Emision_RamoSinComas);
                    				
                				 }
                    			 if (voJSONAttributes.has("Emision_Cotizacion")) {
                					 String Emision_Cotizacion = voJSONAttributes.getString("Emision_Cotizacion");
                					 String Emision_CotizacionSinComas= Emision_Cotizacion.replace(" ,", ".");
                    				 voDetailsConversations.put("Emision_Cotizacion", Emision_CotizacionSinComas);
                    				 
                				 }	
                    			 if (voJSONAttributes.has("Cargo_Cobro")) {
                					 String Cargo_Cobro = voJSONAttributes.getString("Cargo_Cobro");
                					 String Cargo_CobroSinComas= Cargo_Cobro.replace(" ,", ".");
                    				 voDetailsConversations.put("Cargo_Cobro", Cargo_CobroSinComas);
                    				 
                				 }	
                    			 if (voJSONAttributes.has("Tramites_Consultado")) {
                					 String Tramites_Consultado = voJSONAttributes.getString("Tramites_Consultado");
                					 String Tramites_ConsultadoSinComas= Tramites_Consultado.replace(" ,", ".");
                    				 voDetailsConversations.put("Tramites_Consultado", Tramites_ConsultadoSinComas);
                    				
                				 }	
                    			 if (voJSONAttributes.has("Pago_Hablante")) {
                					 String Pago_Hablante = voJSONAttributes.getString("Pago_Hablante");
                					 String Pago_HablanteSinComas= Pago_Hablante.replace(" ,", ".");
                    				 voDetailsConversations.put("Pago_Hablante", Pago_HablanteSinComas);
                    				 
                				 }	
                    			 if (voJSONAttributes.has("Cotizacion_OtroCoti")) {
                					 String Cotizacion_OtroCoti = voJSONAttributes.getString("Cotizacion_OtroCoti");
                					 String Cotizacion_OtroCotiSinComas= Cotizacion_OtroCoti.replace(" ,", ".");
                    				 voDetailsConversations.put("Cotizacion_OtroCoti", Cotizacion_OtroCotiSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Cargo_Ramo")) {
                					 String Cargo_Ramo = voJSONAttributes.getString("Cargo_Ramo");
                					 String Cargo_RamoSinComas= Cargo_Ramo.replace(" ,", ".");
                    				 voDetailsConversations.put("Cargo_Ramo", Cargo_RamoSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("LlamadaOtra_Hablante")) {
                					 String LlamadaOtra_Hablante = voJSONAttributes.getString("LlamadaOtra_Hablante");
                					 String LlamadaOtra_HablanteSinComas= LlamadaOtra_Hablante.replace(" ,", ".");
                    				 voDetailsConversations.put("LlamadaOtra_Hablante", LlamadaOtra_HablanteSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Emision_Sucursal")) {
                					 String Emision_Sucursal = voJSONAttributes.getString("Emision_Sucursal");
                					 String Emision_SucursalSinComas= Emision_Sucursal.replace(" ,", ".");
                    				 voDetailsConversations.put("Emision_Sucursal", Emision_SucursalSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("EnvioDoctos_Solicitados")) {
                					 String EnvioDoctos_Solicitados = voJSONAttributes.getString("EnvioDoctos_Solicitados");
                					 String EnvioDoctos_SolicitadosSinComas= EnvioDoctos_Solicitados.replace(" ,", ".");
                    				 voDetailsConversations.put("EnvioDoctos_Solicitados", EnvioDoctos_SolicitadosSinComas);
                    				
                				 }
                    			 if (voJSONAttributes.has("Consulta_Motivo")) {
                					 String Consulta_Motivo = voJSONAttributes.getString("Consulta_Motivo");
                					 String Consulta_MotivoSinComas= Consulta_Motivo.replace(" ,", ".");
                    				 voDetailsConversations.put("Consulta_Motivo", Consulta_MotivoSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Pago_Sucursal")) {
                					 String Pago_Sucursal = voJSONAttributes.getString("Pago_Sucursal");
                					 String Pago_SucursalSinComas= Pago_Sucursal.replace(" ,", ".");
                    				 voDetailsConversations.put("Pago_Sucursal", Pago_SucursalSinComas);
                    				
                				 }
                    			 if (voJSONAttributes.has("Incidencia_Hablante")) {
                					 String Incidencia_Hablante = voJSONAttributes.getString("Incidencia_Hablante");
                					 String Incidencia_HablanteSinComas= Incidencia_Hablante.replace(" ,", ".");
                    				 voDetailsConversations.put("Incidencia_Hablante", Incidencia_HablanteSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Endoso_No_Procede")) {
                					 String Endoso_No_Procede = voJSONAttributes.getString("Endoso_No_Procede");
                					 String Endoso_No_ProcedeSinComas= Endoso_No_Procede.replace(" ,", ".");
                    				 voDetailsConversations.put("Endoso_No_Procede", Endoso_No_ProcedeSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Emision_Hablante")) {
                					 String Emision_Hablante = voJSONAttributes.getString("Emision_Hablante");
                					 String Emision_HablanteSinComas= Emision_Hablante.replace(" ,", ".");
                    				 voDetailsConversations.put("Emision_Hablante", Emision_HablanteSinComas);
                    				
                				 }
                    			 if (voJSONAttributes.has("EnvioDoctos_Email")) {
                					 String EnvioDoctos_Email = voJSONAttributes.getString("EnvioDoctos_Email");
                					 String EnvioDoctos_EmailSinComas= EnvioDoctos_Email.replace(" ,", ".");
                    				 voDetailsConversations.put("EnvioDoctos_Email", EnvioDoctos_EmailSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Cargo_Hablante")) {
                					 String Cargo_Hablante = voJSONAttributes.getString("Cargo_Hablante");
                					 String Cargo_HablanteSinComas= Cargo_Hablante.replace(" ,", ".");
                    				 voDetailsConversations.put("Cargo_Hablante", Cargo_HablanteSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Cargo_Sucursal")) {
                					 String Cargo_Sucursal = voJSONAttributes.getString("Cargo_Sucursal");
                					 String Cargo_SucursalSinComas= Cargo_Sucursal.replace(" ,", ".");
                    				 voDetailsConversations.put("Cargo_Sucursal", Cargo_SucursalSinComas);
                    				
                				 }
                    			 if (voJSONAttributes.has("Cargo_OtroMov")) {
                					 String Cargo_OtroMov = voJSONAttributes.getString("Cargo_OtroMov");
                					 String Cargo_OtroMovSinComas= Cargo_OtroMov.replace(" ,", ".");
                    				 voDetailsConversations.put("Cargo_OtroMov", Cargo_OtroMovSinComas);
                    				
                				 }
                    			 if (voJSONAttributes.has("LlamadaOtra_Area")) {
                					 String LlamadaOtra_Area = voJSONAttributes.getString("LlamadaOtra_Area");
                					 String LlamadaOtra_AreaSinComas= LlamadaOtra_Area.replace(" ,", ".");
                    				 voDetailsConversations.put("LlamadaOtra_Area", LlamadaOtra_AreaSinComas);
                    				
                				 }
                    			 if (voJSONAttributes.has("Domiciliacion_Hablante")) {
                					 String Domiciliacion_Hablante = voJSONAttributes.getString("Domiciliacion_Hablante");
                					 String Domiciliacion_HablanteSinComas= Domiciliacion_Hablante.replace(" ,", ".");
                    				 voDetailsConversations.put("Domiciliacion_Hablante", Domiciliacion_HablanteSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Cargo_Rehabilitacion")) {
                					 String Cargo_Rehabilitacion = voJSONAttributes.getString("Cargo_Rehabilitacion");
                					 String Cargo_RehabilitacionSinComas= Cargo_Rehabilitacion.replace(" ,", ".");
                    				 voDetailsConversations.put("Cargo_Rehabilitacion", Cargo_RehabilitacionSinComas);
                    				
                				 }
                    			 if (voJSONAttributes.has("Domiciliacion_Sucursal")) {
                					 String Domiciliacion_Sucursal = voJSONAttributes.getString("Domiciliacion_Sucursal");
                					 String Domiciliacion_SucursalSinComas= Domiciliacion_Sucursal.replace(" ,", ".");
                    				 voDetailsConversations.put("Domiciliacion_Sucursal", Domiciliacion_SucursalSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Pago_Importe")) {
                					 String Pago_Importe = voJSONAttributes.getString("Pago_Importe");
                					 String Pago_ImporteSinComas= Pago_Importe.replace(" ,", ".");
                    				 voDetailsConversations.put("Pago_Importe", Pago_ImporteSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Endoso_Poliza")) {
                					 String Endoso_Poliza = voJSONAttributes.getString("Endoso_Poliza");
                					 String Endoso_PolizaSinComas= Endoso_Poliza.replace(" ,", ".");
                    				 voDetailsConversations.put("Endoso_Poliza", Endoso_PolizaSinComas);
                    				
                				 }
                    			 if (voJSONAttributes.has("Domiciliacion_OtroMov")) {
                					 String Domiciliacion_OtroMov = voJSONAttributes.getString("Domiciliacion_OtroMov");
                					 String Domiciliacion_OtroMovSinComas= Domiciliacion_OtroMov.replace(" ,", ".");
                    				 voDetailsConversations.put("Domiciliacion_OtroMov", Domiciliacion_OtroMovSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Domiciliacion_Ramo")) {
                					 String Domiciliacion_Ramo = voJSONAttributes.getString("Domiciliacion_Ramo");
                					 String Cargo_CancelacionSinComas= Cargo_Cancelacion.replace(" ,", ".");
                    				 voDetailsConversations.put("Domiciliacion_Ramo", Domiciliacion_Ramo);
                    				
                				 }
                    			 if (voJSONAttributes.has("Domiciliacion_Poliza")) {
                					 String Domiciliacion_Poliza = voJSONAttributes.getString("Domiciliacion_Poliza");
                					 String Cargo_CancelacionSinComas= Cargo_Cancelacion.replace(" ,", ".");
                    				 voDetailsConversations.put("Domiciliacion_Poliza", Domiciliacion_Poliza);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Emision_PagoAcep")) {
                					 String Emision_PagoAcep = voJSONAttributes.getString("Emision_PagoAcep");
                					 String Cargo_CancelacionSinComas= Cargo_Cancelacion.replace(" ,", ".");
                    				 voDetailsConversations.put("Emision_PagoAcep", Emision_PagoAcep);
                    		
                				 }
                    			 if (voJSONAttributes.has("Domiciliacion_ClaveAgente")) {
                					 String Domiciliacion_ClaveAgente = voJSONAttributes.getString("Domiciliacion_ClaveAgente");
                					 String Domiciliacion_ClaveAgenteSinComas= Domiciliacion_ClaveAgente.replace(" ,", ".");
                    				 voDetailsConversations.put("Domiciliacion_ClaveAgente", Domiciliacion_ClaveAgenteSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Cargo_Poliza")) {
                					 String Cargo_Poliza = voJSONAttributes.getString("Cargo_Poliza");
                					 String Cargo_PolizaSinComas= Cargo_Poliza.replace(" ,", ".");	
                    				 voDetailsConversations.put("Cargo_Poliza", Cargo_PolizaSinComas);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Pago_Ramo")) {
                					 String Pago_Ramo = voJSONAttributes.getString("Pago_Ramo");
                    				 voDetailsConversations.put("Pago_Ramo", Pago_Ramo);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Emision_ClaveAgente")) {
                					 String Emision_ClaveAgente = voJSONAttributes.getString("Emision_ClaveAgente");
                    				 voDetailsConversations.put("Emision_ClaveAgente", Emision_ClaveAgente);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Incidencia_Afectacion")) {
                					 String Incidencia_Afectacion = voJSONAttributes.getString("Incidencia_Afectacion");
                    				 voDetailsConversations.put("Incidencia_Afectacion", Incidencia_Afectacion);
                    			
                				 }
                    			 if (voJSONAttributes.has("Pago_ClaveAgente")) {
                					 String Pago_ClaveAgente = voJSONAttributes.getString("Pago_ClaveAgente");
                    				 voDetailsConversations.put("Pago_ClaveAgente", Pago_ClaveAgente);
                    				
                				 }
                    			 if (voJSONAttributes.has("Consulta_Hablante")) {
                					 String Consulta_Hablante = voJSONAttributes.getString("Consulta_Hablante");
                    				 voDetailsConversations.put("Consulta_Hablante", Consulta_Hablante);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Link_Email")) {
                					 String Link_Email = voJSONAttributes.getString("Link_Email");
                    				 voDetailsConversations.put("Link_Email", Link_Email);
                    				
                				 }
                    			 if (voJSONAttributes.has("Tramites_PersonaEscalada")) {
                					 String Tramites_PersonaEscalada = voJSONAttributes.getString("Tramites_PersonaEscalada");
                    				 voDetailsConversations.put("Tramites_PersonaEscalada", Tramites_PersonaEscalada);
                    			
                				 }
                    			 if (voJSONAttributes.has("Emision_Importe")) {
                					 String Emision_Importe = voJSONAttributes.getString("Emision_Importe");
                    				 voDetailsConversations.put("Emision_Importe", Emision_Importe);
                    				
                				 }
                    			 if (voJSONAttributes.has("Incidencia_Origen")) {
                					 String Incidencia_Origen = voJSONAttributes.getString("Incidencia_Origen");
                    				 voDetailsConversations.put("Incidencia_Origen", Incidencia_Origen);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Cotizacion_Email")) {
                					 String Cotizacion_Email = voJSONAttributes.getString("Cotizacion_Email");
                    				 voDetailsConversations.put("Cotizacion_Email", Cotizacion_Email);
                    				 
                				 }
                    			 if (voJSONAttributes.has("Tramites_ClaveAgente")) {
                					 String Tramites_ClaveAgente = voJSONAttributes.getString("Tramites_ClaveAgente");
                    				 voDetailsConversations.put("Tramites_ClaveAgente", Tramites_ClaveAgente);
                    				
                				 }
                    			 if (voJSONAttributes.has("Pago_Poliza")) {
                					 String Pago_Poliza = voJSONAttributes.getString("Pago_Poliza");
                    				 voDetailsConversations.put("Pago_Poliza", Pago_Poliza);
                    				
                				 }	
                    			 if (voJSONAttributes.has("Emision_Poliza")) {
                					 String Emision_Poliza = voJSONAttributes.getString("Emision_Poliza");
                    				 voDetailsConversations.put("Emision_Poliza", Emision_Poliza);
                    				
                				 }	
                    			 if (voJSONAttributes.has("Cargo_ClaveAgente")) {
                					 String Cargo_ClaveAgente = voJSONAttributes.getString("Cargo_ClaveAgente");
                    				 voDetailsConversations.put("Cargo_ClaveAgente", Cargo_ClaveAgente);
                    				 
                				 }	
                    			 if (voJSONAttributes.has("Emision_OtroMov")) {
                					 String Emision_OtroMov = voJSONAttributes.getString("Emision_OtroMov");
                    				 voDetailsConversations.put("Emision_OtroMov", Emision_OtroMov);
                    				
                				 }
                    		 }
                    	}
                    	
                        
                    	voConversations.put(vsContactId, voDetailsConversations);
                    		
                    } 
                   
                }else {
                	
                	//Validar errores 500 (timeout), 503, 404, 204, los demas seran excepciones generales
                	String strExceptimeout= String.valueOf(voConexionResponseCall.getCodigoRespuesta());
                	//System.err.println("El error es " + strExceptimeout);
                	
                	
                	if(ReturnErro==false) {
                		
                		if (strExceptimeout.equals("500" )|| strExceptimeout.equals("401") ||  strExceptimeout.equals("400")  ||  strExceptimeout.equals("403")   ||  strExceptimeout.equals("404") ||  strExceptimeout.equals("503") ||  strExceptimeout.equals("429")  ){
                    		//System.err.println("Errores excepcionesHttp " );
                    		ReporteMail.excepcionesHttp= ReporteMail.excepcionesHttp + 1;
                    	}else if (strExceptimeout == "504") {
                    		ReporteMail.excepcionesTimeout = ReporteMail.excepcionesTimeout + 1;
                    	}else {
                    		//System.err.println("Errores excepcionesGrales " );
                    		
                    		ReporteMail.excepcionesGrales = ReporteMail.excepcionesGrales + 1;
                    	}
                		PagesNoProcessed(vsContactId,voConexionResponseCall.getCodigoRespuesta(),urlArchivoTemp,vsUUi);
                	}else {
                		
                		PagesNoProcessedCsv(vsContactId,voConexionResponseCall.getCodigoRespuesta(),urlArchivoTemp,vsUUi);
                	}
                }
                    if(voDetailsConversations != null) {
                    voLogger.info("[Reporteador][" + vsUUi + "] ---> TOTAL CONVERSATION WITH BREADCRUMBS[" + voDetailsConversations.size() + "]");
                    }

                }
                /*
				 * Genero los archivos TXT GenraTXT = new GeneradorTXT();
                 */
                GenraTXT = new GeneradorTXT();
                nameTxt = new ArrayList<>();
              //System.out.println(voConversations.size()+" del hilo: "+this.getName() + "con un total de ids: "+vlContactId.size());
                	
               
                nameTxt.addAll(GenraTXT.GeneraTXT(vlContact, voConversations,vsUUi,urlArchivoTemp));
                
                
        
        
     }   
    



public  void PagesNoProcessed(String vsContactId,int getCodigoRespuesta, String urlArchivoTemp,String vsUUi) {
    
    	String strCodigoRespuesta = String.valueOf(getCodigoRespuesta); ;
    	strUrlFinal = urlArchivoTemp+ "\\" + vsUUi + "_conversations_IE_TEMP.csv";
    	   		
    		File  fw = new File (strUrlFinal);
    		//Validamos si el archivo existe
    		if(fw.exists()){
    			
    			try {
					Writer  output = new BufferedWriter(new FileWriter(strUrlFinal, true));
					output.append(vsContactId);
					output.append(",");
					output.append(strCodigoRespuesta);
					output.append("\n");
					output.close();
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
            }else{
                //Archivo NO existe, lo crea.
			try (PrintWriter writer = new PrintWriter(new File(strUrlFinal))) {
				StringBuilder linea = new StringBuilder();			
				linea.append(vsContactId);
				linea.append(',');
				linea.append(strCodigoRespuesta);
				linea.append('\n');
				writer.write(linea.toString());
	            writer.close();
	            
    	} catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
            }
    		
        
    }

public boolean PagesNoProcessedCsv(String vsContactId,int getCodigoRespuesta, String urlArchivoTemp,String vsUUi) {
    	
    	String strCodigoRespuesta = String.valueOf(getCodigoRespuesta); ;
    	strUrlFinal = urlArchivoTemp+ "\\" + vsUUi + "_conversations_IE.csv";
    	   	ReporteMail.lineasInteraccionesNoProcesadas = ReporteMail.lineasInteraccionesNoProcesadas + 1;
    		File  fw = new File (strUrlFinal);
    		//Validamos si el archivo existe
    		if(fw.exists()){
    			
    			try {
					Writer  output = new BufferedWriter(new FileWriter(strUrlFinal, true));
					output.append(vsContactId);
					output.append(",");
					output.append(strCodigoRespuesta);
					output.append("\n");
					output.close();
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
            }else{
                //Archivo NO existe, lo crea.
			try (PrintWriter writer = new PrintWriter(new File(strUrlFinal))) {
				
				StringBuilder linea = new StringBuilder();
				
				linea.append("ConversationID");
				linea.append(',');
				linea.append("Respuesta de error");
				linea.append('\n');
				
	            
				linea.append(vsContactId);
				linea.append(',');
				linea.append(strCodigoRespuesta);
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
