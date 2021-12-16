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
    //private DatosProgreso voDatos;
    private Utilerias voUti;
   
    //private JProgressBar voProgreso;
    private Map<String, String> voMapConf = null;
    private Map<String, Map<String, String>> voConversations;
    private List<String> vlContact = null;
    private List<String> nameTxt;
    private List<String> vlContactId;
    private Map<String, String> voDetailsConversations;
    private String urlArchivoTemp =null;
    private Map<String, Object> voMapHeaderCSV = new HashMap<String, Object>();
    private boolean vbActivo = true;
    private boolean ReturnErro;
    

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

    	 
     
                ConexionResponse voConexionResponse;
                voConexionHttp = new ConexionHttp();
                HashMap<String, String> voHeader = new HashMap<>();
                vlContact = new ArrayList<>();
                voConversations = new HashMap<>();
                voHeader.put("Authorization", "bearer " + vsToken);
                voConversations = new HashMap<>();
                //COMENZAREMOS A ANALIZAR CADA ID DE CONVERSACION PARA EXTRAER SUS BREADCRUMBS
                int viContadorEncontrados = 0;
                String vsURLPCCall = "https://api.mypurecloud.com/api/v2/conversations/calls/";
                ConexionResponse voConexionResponseCall = null;
                
                for (String vsContactId : vlContactId) {
                    String vsURLConversation = vsURLPCCall + vsContactId;
                    viContadorEncontrados++;
                    voLogger.info("[Reporteador][" + vsUUi + "] ---> [" + (viContadorEncontrados) + "] ENDPOINT[" + vsURLConversation + "]");
                    try {
                        voConexionResponseCall = voConexionHttp.executeGet(vsURLConversation, 15000, voHeader, null);
                    } catch (Exception e) {
                        voLogger.error("[Reporteador][" + vsUUi + "] ---> CONTACT_ID [" + vsContactId + "] : " + e.getMessage());
                    }
                    
                    String vsJsonResponse = voConexionResponseCall.getMensajeRespuesta();
                    
                    if(voConexionResponseCall.getCodigoRespuesta() == 200) {
                    		
                   
                    JSONObject voJsonResponseCall = new JSONObject(voConexionResponseCall.getMensajeRespuesta());
                    voLogger.info("[Reporteador][" + vsUUi + "] ---> [" + (viContadorEncontrados) + "] "
                            + "RESPONSE: STATUS[" + voConexionResponseCall.getCodigoRespuesta() + "]");
                   
                    
                    
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
                   
                }else {
                	if(ReturnErro==false) {
                		PagesNoProcessed(vsContactId,voConexionResponseCall.getCodigoRespuesta(),urlArchivoTemp,vsUUi);
                	}else {
                		PagesNoProcessedCsv(vsContactId,voConexionResponseCall.getCodigoRespuesta(),urlArchivoTemp,vsUUi);
                	}
                }
                    
                }
               
                //voDatos.setTotalProgreso(vlContactId.size());
                voLogger.info("[Reporteador][" + vsUUi + "] ---> TOTAL CONVERSATION WITH BREADCRUMBS[" + voConversations.size() + "]");
                voLogger.info("[Reporteador][" + vsUUi + "] ---> ANALIZANDO E INTERPRETANDO BREADCRUMBS PARA LA APP [" + vsFlowName + "  ]  [" + vsFlowName1 + "  ]");
                vbActivo = false;
            
                /*
				 * Genero los archivos TXT GenraTXT = new GeneradorTXT();
                 */
                GenraTXT = new GeneradorTXT();
                nameTxt = new ArrayList<>();
              System.out.println(voConversations.size()+" del hilo: "+this.getName() + "con un total de ids: "+vlContactId.size());
                	
               
                nameTxt.addAll(GenraTXT.GeneraTXT(vlContact, voConversations,vsUUi,urlArchivoTemp));
                
                
        
        
     }   
    



public  void PagesNoProcessed(String vsContactId,int getCodigoRespuesta, String urlArchivoTemp,String vsUUi) {
    
    	String strCodigoRespuesta = String.valueOf(getCodigoRespuesta); ;
    	strUrlFinal = urlArchivoTemp+ "\\" + vsUUi + "_conversations_IE";
    	   		
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
    	strUrlFinal = "C:\\Appl\\GS\\ReportesCustom\\CSVFinales\\" + vsUUi + "_conversations_IE.CSV";
    	   		
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
