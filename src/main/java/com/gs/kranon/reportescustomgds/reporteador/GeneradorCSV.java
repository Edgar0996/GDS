package com.gs.kranon.reportescustomgds.reporteador;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.gs.kranon.reportescustomgds.DataReportGDSmx;
import com.gs.kranon.reportescustomgds.GDSmx;
import com.gs.kranon.reportescustomgds.utilidades.Excel;

public class GeneradorCSV {
	static {
        System.setProperty("dateLog", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
    }
	
    private static final Logger voLogger = LogManager.getLogger("Reporte");
    private Map<String, Object> voMapHeaderCSV = new HashMap<String, Object>();
    private boolean vbActivo = true;
    private Map<String, String> voMapConf = null;
    private List<String> vlContactId = null;

    
    public void GeneraCSV(List<String> nameTxt,String pathCSV,List<String> vlContactId,Map<String, Map<String, String>> voConversations,String vsUUI){
    	//Leemos el txt recibido por parametro
        FileReader fileReaderConversations = null;
        String lineContent = "";
        List content = new ArrayList();
        try {
           //sleep(3000);
            fileReaderConversations = new FileReader(pathCSV + "temp\\Reporte_" + nameTxt.get(0) + "\\" + nameTxt.get(0) + ".txt");
            BufferedReader buffer = new BufferedReader(fileReaderConversations);
            while((lineContent = buffer.readLine()) != null){
            String[] lineElements = lineContent.split(",");
             content.add(lineElements);
            }
            buffer.close();
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(Reporteador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Reporteador.class.getName()).log(Level.SEVERE, null, ex);
			/*
			 * } catch (InterruptedException ex) {
			 * java.util.logging.Logger.getLogger(Reporteador.class.getName()).log(Level.
			 * SEVERE, null, ex);
			 */
        } finally{
            try {
                if(null != fileReaderConversations)
                fileReaderConversations.close();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(Reporteador.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
      //Generando excel
        String Archivo;
        Archivo = pathCSV + "temp\\Reporte_" + nameTxt.get(0) + "\\ReporteFinal";
        vbActivo = false;
        //Obteniendo los encabezados
        DataReportGDSmx voDataBBVAmx = new DataReportGDSmx();
        GDSmx voAppBBVAMx = new GDSmx(voMapConf, voDataBBVAmx);
        for (String vsContactId : vlContactId) {
            Map<String, String> voDetails = voConversations.get(vsContactId);
            voAppBBVAMx.analizar(voDetails);
            voConversations.replace(vsContactId, voDetails);
            voMapHeaderCSV = voAppBBVAMx.getHeaderCSV();
        }
       // boolean resultadoCsv = GeneraReportCSV(Archivo, content,vsUUI);
                                                                      
    }
    
    
    public boolean GeneraReportCSV(String vsPathExcel, List content,String vsUUI, Map<String, Object> voMapHeadersCSVs) {
        voMapHeaderCSV = voMapHeadersCSVs;
        vbActivo = false;
        if (voMapHeaderCSV.size() <= 0) {
            voLogger.warn("[Reporteador][" + vsUUI + "] ---> NO HAY DATOS PARA REALIZAR LA EXPORTACION.");
            return false;
        }
        if (!vbActivo) {
            voLogger.info("[Reporteador][" + vsUUI + "] ---> GENERANDO CSV[" + vsPathExcel + ".csv]");
            Excel voExcel = new Excel(vsUUI);
            voExcel.addInfo(voMapHeaderCSV, content);
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
}
