package com.gs.kranon.reportescustomgds.genesysCloud;


import com.gs.kranon.reportescustomgds.conexionHttp.ConexionResponse;
import com.gs.kranon.reportescustomgds.conexionHttp.ConexionHttp;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.log4j.LogManager;

public class GenesysCloud {
    
    static {
        System.setProperty("dateLog", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
    }
    private static final org.apache.log4j.Logger voLogger = LogManager.getLogger("Reporte");

    private ConexionResponse conexionResponse = null;
    private String vsUUI = "";
    public String vsHorarioInterval = "T05:00:00.000Z";

    public void setUUI(String vsUUI) {
        this.vsUUI = vsUUI;
    }

    public String getToken(String vsClID, String vsClSec) {
        String vsAccessToken = "ERROR";
        String encodeData;
        String URLServicio = "https://login.mypurecloud.com/oauth/token?grant_type=client_credentials";
        String inputJson = "";
        int timeOut = 15000;
        HashMap<String, String> header = new HashMap<>();
        try {
            encodeData = new String(Base64.encodeBase64((vsClID + ":" + vsClSec).getBytes("ISO-8859-1")));
            header.put("Authorization", " Basic " + encodeData);
        } catch (UnsupportedEncodingException e1) {
            voLogger.info("[PureCloud  ][" + vsUUI + "] ---> " + e1.getMessage());
        }
        try {
            ConexionHttp conexionHttp = new ConexionHttp();
            conexionResponse = conexionHttp.executePost(URLServicio, timeOut, inputJson, header);
            if (conexionResponse.getCodigoRespuesta() == 200) {
                JSONObject json = new JSONObject(conexionResponse.getMensajeRespuesta());
                if (json.has("access_token")) {
                    vsAccessToken = json.getString("access_token");
                    voLogger.error("[PureCloud  ][" + vsUUI + "] ---> TOKEN[SUCCESS].");
                } else {
                    voLogger.info("[PureCloud  ][" + vsUUI + "] ---> TOKEN[ERROR].");
                }
            } else {
                voLogger.error("[PureCloud  ][" + vsUUI + "] ---> TOKEN[" + vsAccessToken + "], "
                        + " CODIGO RESPUESTA[" + conexionResponse.getCodigoRespuesta() + "], MENSAJE RESPUESTA[" + conexionResponse.getMensajeRespuesta() + "]");
            }
        } catch (IOException e) {
            voLogger.error("[PureCloud  ][" + vsUUI + "] --->" + e.getMessage());
        } catch (Exception e) {
            voLogger.error("[PureCloud  ][" + vsUUI + "] --->" + e.getMessage());
        }
        return vsAccessToken;
    }

    public String getBody(Integer viPag, String vsFecha, String vsFechaInt) {
        return "{\r\n"
                + "	\"order\": \"desc\",\r\n"
                + "	\"orderBy\": \"conversationStart\",\r\n"
                + "	\"paging\": {\r\n"
                + "	\"pageSize\": 100,\r\n"
                + "	\"pageNumber\": " + viPag + "\r\n"
                + "	}, \"segmentFilters\": [{\r\n"
                + "	\"type\": \"and\",\r\n"
                + "	\"predicates\": [{\r\n"
                + "	\"dimension\": \"mediaType\",\r\n"
                + "	\"value\": \"voice\"\r\n"
                + "	}]},\r\n"
                + "	{ \"type\": \"or\",\r\n"
                + "	\"predicates\": [{\r\n"
                + "	\"dimension\": \"direction\",\r\n"
                + "	\"value\": \"inbound\"\r\n"
                + "	}]}],\r\n"
                + "	\"conversationFilters\":[],\r\n"
                + "	\"evaluationFilters\":[],\r\n"
                + "	\"surveyFilters\":[],\r\n"
                + "	\"metrics\": [\"tAcd\",\"tAcw\",\"tTalkComplete\",\"tIvr\",\"tHeldComplete\",\"tAlert\",\"tAbandon\",\r\n"
                + "	\"nTransferred\",\"tTalk\",\"tHeld\",\"nOutboundAttempted\",\"tContacting\",\"tDialing\",\"tHandle\",\r\n"
                + "	\"nBlindTransferred\",\"nConsult\",\"nConsultTransferred\",\"oMediaCount\",\"oExternalMediaCount\",\r\n"
                + "	\"tVoicemail\",\"tMonitoring\",\"tFlowOut\" ],\r\n"
                + "	\"interval\": \"" + vsFecha + vsHorarioInterval + "/" + vsFechaInt + vsHorarioInterval + "\"\r\n"
                + "}";
    }
}
