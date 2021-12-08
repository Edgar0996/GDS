package com.gs.kranon.reportescustomgds.reporteador;

import com.gs.kranon.reportescustomgds.DataReportGDSmx;
import com.gs.kranon.reportescustomgds.GDSmx;
import com.gs.kranon.reportescustomgds.DataReports;
import com.gs.kranon.reportescustomgds.utilidades.Excel;
import com.gs.kranon.reportescustomgds.utilidades.Utilerias;
import com.gs.kranon.reportescustomgds.conexionHttp.ConexionHttp;
import com.gs.kranon.reportescustomgds.conexionHttp.ConexionResponse;
import com.gs.kranon.reportescustomgds.genesysCloud.GenesysCloud;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.util.Scanner;
import java.util.logging.Level;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Reporteador extends  Thread  {

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
    private GeneradorTXT GenraTXT = null;
    private GeneradorCSV GenraCSV = null;
    //private DatosProgreso voDatos;
    private Utilerias voUti;

    //private JProgressBar voProgreso;
    private Map<String, String> voMapConf = null;
    private Map<String, Map<String, String>> voConversations;
    private List<String> vlContact = null;
    private List<String> nameTxt;
    private Map<String, String> voDetailsConversations;

    private Map<String, Object> voMapHeaderCSV = new HashMap<String, Object>();
    private boolean vbActivo = true;

    

    public Reporteador(DataReports voDataReport, String uui) {
        this.voDataReport = voDataReport;
        voMapConf = new HashMap<>();
        voPureCloud = new GenesysCloud();
        voUti = new Utilerias();
        vsUUI = uui;
        voUti.getProperties(voMapConf, uui);
    }
   
 
    public synchronized void run(String vsToken,String vsUUI,List<String> vlContactId) {

    	 
     
                ConexionResponse voConexionResponse;
                voConexionHttp = new ConexionHttp();
                HashMap<String, String> voHeader = new HashMap<>();
                voConversations = new HashMap<>();
                vlContact = new ArrayList<>();
                voConversations = new HashMap<>();
                voHeader.put("Authorization", "bearer " + vsToken);
                //voThreadProgreso.start();
                //COMENZAREMOS A ANALIZAR CADA ID DE CONVERSACION PARA EXTRAER SUS BREADCRUMBS
                int viContadorEncontrados = 0;
                String vsURLPCCall = "https://api.mypurecloud.com/api/v2/conversations/calls/";
                ConexionResponse voConexionResponseCall = null;
                
                for (String vsContactId : vlContactId) {

                    String vsURLConversation = vsURLPCCall + vsContactId;
                    viContadorEncontrados++;
                    voLogger.info("[Reporteador][" + vsUUI + "] ---> [" + (viContadorEncontrados) + "] ENDPOINT[" + vsURLConversation + "]");
                    try {
                        voConexionResponseCall = voConexionHttp.executeGet(vsURLConversation, 15000, voHeader, null);
                    } catch (Exception e) {
                        voLogger.error("[Reporteador][" + vsUUI + "] ---> CONTACT_ID [" + vsContactId + "] : " + e.getMessage());
                    }
                    
                    String vsJsonResponse = voConexionResponseCall.getMensajeRespuesta();
                    JSONObject voJsonResponseCall = new JSONObject(vsJsonResponse);
                    voLogger.info("[Reporteador][" + vsUUI + "] ---> [" + (viContadorEncontrados) + "] "
                            + "RESPONSE: STATUS[" + voConexionResponseCall.getCodigoRespuesta() + "]");
                    voLogger.info("[Reporteador][" + vsUUI + "] Mi Segundo JSON es [" + viContadorEncontrados + "consecutivo " + voConexionResponseCall.getMensajeRespuesta() + "]");
                    if (voJsonResponseCall.has("participants")) {
                    	JSONArray voJsonArrayResponseCall = voJsonResponseCall.getJSONArray("participants");
                    	
                    		
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
                            voConversations.put(vsContactId, voDetailsConversations);
                          
                           
                    		
                    	
                    	
                    }
                    
                    
                }
                
               
                //voDatos.setTotalProgreso(vlContactId.size());
                voLogger.info("[Reporteador][" + vsUUI + "] ---> TOTAL CONVERSATION WITH BREADCRUMBS[" + voConversations.size() + "]");
                voLogger.info("[Reporteador][" + vsUUI + "] ---> ANALIZANDO E INTERPRETANDO BREADCRUMBS PARA LA APP [" + vsFlowName + "  ]  [" + vsFlowName1 + "  ]");
                vbActivo = false;
            
                /*
				 * Genero los archivos TXT GenraTXT = new GeneradorTXT();
                 */
                
          
                GenraTXT = new GeneradorTXT();
                nameTxt = new ArrayList<>();
                nameTxt.addAll(GenraTXT.GeneraTXT(vlContact, voConversations,vsUUI));
                
                
        
        
     }   
    



    public boolean getGenerado() {
        return !vbActivo;
    }
}
