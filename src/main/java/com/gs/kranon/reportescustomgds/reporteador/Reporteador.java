package com.gs.kranon.reportescustomgds.reporteador;

import com.gs.kranon.reportescustomgds.DataReportGDSmx;
import com.gs.kranon.reportescustomgds.GDSmx;
import com.gs.kranon.reportescustomgds.DataReports;
import com.gs.kranon.reportescustomgds.utilidades.Excel;
import com.gs.kranon.reportescustomgds.utilidades.Utilerias;
import com.gs.kranon.reportescustomgds.conexionHttp.ConexionHttp;
import com.gs.kranon.reportescustomgds.conexionHttp.ConexionResponse;
import com.gs.kranon.reportescustomgds.genesysCloud.GenesysCloud;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Reporteador {

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
    private GeneradorTXT GenraTXT=null;
    //private DatosProgreso voDatos;
    private Utilerias voUti;

    //private JTextArea voTA;
    //private JProgressBar voProgreso;
    private Map<String, String> voMapConf = null;
    private Map<String, Map<String, String>> voConversations;
    private List<String> vlContactId = null;
    private Map<String, String> voDetailsConversations;

    private Map<String, Object> voMapHeaderCSV = new HashMap<String, Object>();
    private boolean vbActivo = true;

    Thread voThreadProgreso;
    Thread voThreadReporte;

    public Reporteador(DataReports voDataReport) {
        this.voDataReport = voDataReport;
        voMapConf = new HashMap<>();
        voPureCloud = new GenesysCloud();
        voUti = new Utilerias();
        voUti.getProperties(voMapConf);
    }

    public void getDataReport(String vsToken) {

        voThreadReporte = new Thread() {

            @Override
            public void run() {

                vsUUI = voPureCloud.getVsUUI();
                ConexionResponse voConexionResponse;
                //recuperamos las variables a comparar para ir por nuestros ID's

                vsFlowName = voDataReport.getFlowName();
                vsFlowName1 = voDataReport.getFlowName1();
                vsFechaInicio = "2021-11-17";
                vsFechaFin = "2021-11-17";

                voLogger.info("[Reporteador][" + vsUUI + "] ---> ******************** STARTING REPORT *******************");

                /*if (voDateInicio.equals(voDateFin) || voDateInicio.after(voDateFin)) {
                    voLogger.error("[Reporteador][" + vsUUI + "] ---> THE DATES ARE INCORRECT.");
                    vbActivo = false;
                    return;
                }*/
                String vsURLPCDetails = "https://api.mypurecloud.com/api/v2/analytics/conversations/details/query";
                voConexionHttp = new ConexionHttp();
                voConversations = new HashMap<>();
                vlContactId = new ArrayList<>();
                HashMap<String, String> voHeader = new HashMap<>();
                voHeader.put("Authorization", "bearer " + vsToken);
                Integer viPag = 0;

                //voTA.setText("Espere...");
                //OBTENIENDO TODOS LOS CONVERSATION ID DEL RANGO DE HORAS EN EL DIA
                do {
                    viPag++;
                    voLogger.info("[Reporteador][" + vsUUI + "] ---> LOADING PAGE NUMBER [" + (viPag) + "]");

                    voPureCloud.vsHorarioInterval = (voMapConf.get("HorarioVerano").trim().toUpperCase().contentEquals("TRUE")) ? "T05:00:00.000Z" : "T06:00:00.000Z";
                    String vsBody = voPureCloud.getBody(viPag, vsFechaInicio, vsFechaFin);
                    voLogger.info("[Reporteador][" + vsUUI + "] ---> ENDPOINT[" + vsURLPCDetails + "], REQUEST[" + vsBody.replace("\r\n", "") + "]");

                    try {
                        voConexionResponse = voConexionHttp.executePost(vsURLPCDetails, 15000, vsBody, voHeader);
                    } catch (Exception e) {
                        voLogger.error("[Reporteador][" + vsUUI + "] ERROR : " + e.getMessage());
                        break;
                    }

                    if (voConexionResponse.getCodigoRespuesta() == 200) {
                        
                        String vsJsonResponse = voConexionResponse.getMensajeRespuesta();
                        // voLogger.info("[Reporteador][" + vsUUI + "] ---> STATUS[" + voConexionResponse.getCodigoRespuesta() + "], "
                        //       + "RESPONSE[{\"totalHits\":\"" + new JSONObject(vsJsonResponse).getInt("totalHits") + "\"}]");

                        JSONObject voJsonConversations = new JSONObject(vsJsonResponse);

                        if (vsJsonResponse.equals("{}") || !voJsonConversations.has("conversations")) {
                            voLogger.info("[Reporteador][" + vsUUI + "] ---> CONVERSATIONS FOUND [0]");
                            break;
                        }
                        voLogger.info("[Reporteador][" + vsUUI + "Cadena JSON" + voJsonConversations);
                        if (voJsonConversations.has("conversations")) {
                            JSONArray voJsonArrayConversations = voJsonConversations.getJSONArray("conversations");
                            voLogger.info("[Reporteador][" + vsUUI + "] ---> CONVERSATIONS FOUND[" + voJsonArrayConversations.length() + "]");
                            for (int i = 0; i < voJsonArrayConversations.length(); i++) {

                                voDetailsConversations = new HashMap<>();
                                String vsIdConversation = voJsonArrayConversations.getJSONObject(i).getString("conversationId");
                                String vsConversationStart = voUti.userDateGMT(voJsonArrayConversations.getJSONObject(i).getString("conversationStart"));
                                String vsConversationEnd = (voJsonArrayConversations.getJSONObject(i).has("conversationEnd"))
                                        ? voUti.userDateGMT(voJsonArrayConversations.getJSONObject(i).getString("conversationEnd")) : "";
                                voDetailsConversations.put("conversationStart", vsConversationStart.substring(0, 23).replace("T", " "));
                                voDetailsConversations.put("conversationEnd", (vsConversationEnd == null || "".equals(vsConversationEnd))
                                        ? "" : vsConversationEnd.substring(0, 23).replace("T", " "));

                                JSONArray voJSONParticipants = voJsonArrayConversations.getJSONObject(i).getJSONArray("participants");

                                JSONArray voJSONSesions = voJSONParticipants.getJSONObject(0).getJSONArray("sessions");

                                String vsDnis = voJSONSesions.getJSONObject(0).getString("dnis");
                                String vsAni = voJSONSesions.getJSONObject(0).getString("ani");

                                vlContactId.add(vsIdConversation);
                                voDetailsConversations.put("ani", vsAni);
                                voDetailsConversations.put("dnis", vsDnis);
                                voDetailsConversations.put("breadCrumbs", "");
                                voDetailsConversations.put("comentarioCTI", "");
                                voDetailsConversations.put("UUI", "");
                                voConversations.put(vsIdConversation, voDetailsConversations);
                              
                            }

                        } else {
                            voLogger.error("[Reporteador][" + vsUUI + "] ---> CODE[" + voConexionResponse.getCodigoRespuesta()
                                    + "], MESSAGE ERROR[" + voConexionResponse.getMensajeError() + "]");
                            vbActivo = false;
                            break;
                        }
                    }
                } while (true);
                
                
                voLogger.info("[Reporteador][" + vsUUI + "] TOTAL DE ID'S [" + vlContactId.size() + "]");
                //voDatos.setTotalProgreso(vlContactId.size());
                //voProgreso.setMinimum(0);
                //voProgreso.setMaximum(vlContactId.size());

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    voLogger.error("[Reporteador][" + vsUUI + "] ---> " + ex.getMessage());
                }

                //voThreadProgreso.start();
                //COMENZAREMOS A ANALIZAR CADA ID DE CONVERSACION PARA EXTRAER SUS BREADCRUMBS
                Integer viContadorEncontrados = 0;
                String vsURLPCCall = "https://api.mypurecloud.com/api/v2/conversations/calls/";
                ConexionResponse voConexionResponseCall = null;
                //System.out.println("Esto esta en la línea 186" + voConversations);
                for (String vsContactId : vlContactId) {
                   //Genero los archivos TXT
                GenraTXT = new GeneradorTXT();
                GenraTXT.GeneraTXT(vsContactId, voConversations);
                    
                    String vsURLConversation = vsURLPCCall + vsContactId;
                    viContadorEncontrados++;
                    voLogger.info("[Reporteador][" + vsUUI + "] ---> [" + (viContadorEncontrados) + "] ENDPOINT[" + vsURLConversation + "]");
                    try {
                        voConexionResponseCall = voConexionHttp.executeGet(vsURLConversation, 15000, voHeader, null);
                    } catch (Exception e) {
                        voLogger.error("[Reporteador][" + vsUUI + "] ---> CONTACT_ID [" + vsContactId + "] : " + e.getMessage());
                    }
                    JSONObject voJsonResponseCall = new JSONObject(voConexionResponseCall.getMensajeRespuesta());
                    voLogger.info("[Reporteador][" + vsUUI + "] ---> [" + (viContadorEncontrados) + "] "
                            + "RESPONSE: STATUS[" + voConexionResponseCall.getCodigoRespuesta() + "]");

                    if (voJsonResponseCall.has("participants")) {
                        JSONArray voJsonArrayResponseCall = voJsonResponseCall.getJSONArray("participants");
                        for (int j = 0; j < voJsonArrayResponseCall.length(); j++) {

                            if (voJsonArrayResponseCall.getJSONObject(j).getJSONObject("attributes").length() > 0) {

                                Map<String, String> voDetails = voConversations.get(vsContactId);
                                JSONObject voJSONAttributes = voJsonArrayResponseCall.getJSONObject(j).getJSONObject("attributes");

                                if (voJSONAttributes.has("vsBreadCrumbs")) {
                                    String vsBreadCrumbs = voJSONAttributes.getString("vsBreadCrumbs");
                                    voDetails.replace("breadCrumbs", vsBreadCrumbs.replace(",", ">"));
                                    voLogger.info("[Reporteador][" + vsUUI + "] ---> [" + viContadorEncontrados + "] BREADCRUMBS[" + vsBreadCrumbs + "]");

                                    if (voJSONAttributes.has("vsComentarioCTI")) {
                                        String vsComentarioCTI = voJSONAttributes.getString("vsComentarioCTI");
                                        voDetails.replace("comentarioCTI", vsComentarioCTI.replace(",", ">"));
                                        voLogger.info("[Reporteador][" + vsUUI + "] ---> [" + viContadorEncontrados + "] COMENTARIO_CTI[" + vsComentarioCTI + "]");
                                    }
                                    if (voJSONAttributes.has("vsUUI")) {
                                        String vsUUILlamada = voJSONAttributes.getString("vsUUI");
                                        voDetails.replace("UUI", vsUUILlamada);
                                        voLogger.info("[Reporteador][" + vsUUI + "] ---> [" + viContadorEncontrados + "] UUI[" + vsUUILlamada + "]");
                                    }

                                } else {
                                    voLogger.info("[Reporteador][" + vsUUI + "] ---> [" + (viContadorEncontrados)
                                            + "] BREADCRUMBS[NOT FOUND] IN CONTACT ATTRIBUTES[" + voJSONAttributes.toString() + "]");
                                }

                                voConversations.replace(vsContactId, voDetails);

                                break;
                            }
                        }
                    }
                    //voDatos.setProgreso(viContadorEncontrados);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        voLogger.error("[Reporteador][" + vsUUI + "] ---> " + ex.getMessage());
                    }
                }
                //voDatos.setTotalProgreso(vlContactId.size());
                voLogger.info("[Reporteador][" + vsUUI + "] ---> TOTAL CONVERSATION WITH BREADCRUMBS[" + voConversations.size() + "]");
                voLogger.info("[Reporteador][" + vsUUI + "] ---> ANALIZANDO E INTERPRETANDO BREADCRUMBS PARA LA APP [" + vsFlowName + "  ]  [" + vsFlowName1 + "  ]");
                /*
                
                
                switch(vsFlowName){
                    case "bbva2_bbvamxap_ivr":
                        DataReportGDSmx voDataBBVAmx = new DataReportGDSmx();
                        GDSmx voAppBBVAMx = new GDSmx(voMapConf, voDataBBVAmx);
                        
                        for(String vsContactId : vlContactId) {
                            Map<String, String> voDetails = voConversations.get(vsContactId);
                            voAppBBVAMx.analizar(voDetails);
                            voConversations.replace(vsContactId, voDetails);
                            voMapHeaderCSV = voAppBBVAMx.getHeaderCSV();
                        }
                        //voTA.setText(voAppBBVAMx.getMetricas(vsFechaInicio,vsFlowName));
                    break;
                    default:
                        DataReportGDSmx voDataDefault = new DataReportGDSmx();
                        GDSmx voBBVADefault = new GDSmx(voMapConf, voDataDefault);
                        for(String vsContactId : vlContactId) {
                            Map<String, String> voDetails = voConversations.get(vsContactId);
                            voBBVADefault.analizar(voDetails);
                            voConversations.replace(vsContactId, voDetails);
                            voMapHeaderCSV = voBBVADefault.getHeaderCSV();
                        }
                        //voTA.setText("NO EXISTEN METRICAS PARA MOSTRAR PARA ESTA APP");
                        //voTA.setText(voBBVADefault.getMetricas(vsFechaInicio,vsFlowName));
                        voLogger.info("[Reporteador][" + vsUUI + "] ---> APP SIN METRICAS [" + vsFlowName + "]");
                    break;
                }
                 */
                vbActivo = false;
            };
        };
        
        voThreadReporte.start();
    }

    public boolean GeneraReportCSV(String vsPathExcel) {
        if (voMapHeaderCSV.size() <= 0) {
            voLogger.warn("[Reporteador][" + vsUUI + "] ---> NO HAY DATOS PARA REALIZAR LA EXPORTACION.");
            return false;
        }
        if (!vbActivo) {
            voLogger.info("[Reporteador][" + vsUUI + "] ---> GENERANDO CSV[" + vsPathExcel + ".csv]");
            Excel voExcel = new Excel(vsUUI);
            voExcel.addInfo(voMapHeaderCSV, voConversations);
            if (voExcel.createCSV(vsPathExcel + ".csv")) {
                voLogger.info("[Reporteador][" + vsUUI + "] ---> ARCHIVO CSV CREADO EXITOSAMENTE");
            } else {
                voLogger.error("[Reporteador][" + vsUUI + "] ---> ERROR : NO SE CREO EL ARCHIVO CSV");
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean GenerarReportTXT(String vsPath) {
        PrintWriter pw = null;
        voLogger.info("[Reporteador][" + vsUUI + "] ---> GENERANDO TXT[" + vsPath + ".txt]");
        try {
            pw = new PrintWriter(new FileWriter(vsPath + ".txt", true));
            //pw.println(voTA.getText());
            pw.close();
            voLogger.info("[Reporteador][" + vsUUI + "] ---> ARCHIVO TXT CREADO EXITOSAMENTE");
            return true;
        } catch (Exception e) {
            voLogger.error("[Utilerias][" + vsUUI + "] ---> ERROR : " + e.getMessage());
            return false;
        }
    }

    public boolean getGenerado() {
        return !vbActivo;
    }
}
