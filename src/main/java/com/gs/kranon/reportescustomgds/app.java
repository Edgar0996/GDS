/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gs.kranon.reportescustomgds;

import com.gs.kranon.reportescustomgds.genesysCloud.GenesysCloud;
import com.gs.kranon.reportescustomgds.genesysCloud.RecuperaConversationID;
import com.gs.kranon.reportescustomgds.utilidades.Utilerias;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.gs.kranon.reportescustomgds.conexionHttp.ConexionResponse;
import com.gs.kranon.reportescustomgds.conexionHttp.ConexionHttp;
import com.gs.kranon.reportescustomgds.mail.SendingMailTLS;
import com.gs.kranon.reportescustomgds.reporteador.GeneradorCSV;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.JOptionPane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.gs.kranon.reportescustomgds.reporteador.Reporteador;
import java.io.BufferedReader;
import static java.lang.System.exit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.Properties;
import java.util.logging.Level;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

/**
 *
 * @author VFG
 */
public class app {

    /**
     * @param args the command line arguments
     */
    static {
        System.setProperty("dateLog", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
    }
    private static final Logger voLogger = LogManager.getLogger("Reporte");
    static String pathArchivo;
    private GenesysCloud voPureCloud = null;
    private Utilerias voUtil = null;
    private Map<String, String> voMapConf = null;
    private Map<String, String> voMapConfId = null;
    private String vsUUI = "1234567890";
    private String vsToken = null;

    /*Variables de prueba */
    private DataReports voData = null;
    /*Variables para mandar a llamar mi reporte */
    private Reporteador voReporte;
    private RecuperaConversationID RecuperaId;

    public static void main(String[] args) {

        /* Genero mi cadena UUI */
        String vsUUI = GeneraCadenaUUI("1234567890");

        /* Invoco mi archivos de configuración */
        Map<String, String> voMapConf = RecuperaArhivoConf(vsUUI);

        if (voMapConf.size() <= 0) {
            voLogger.error("[app][" + vsUUI + "] ---> NO SE ENCONTRO EL ARCHIVO DE CONFIGURACIÓN O ESTA VACIO");
            exit(0);
        } else {

            int totalThreads = Integer.parseInt(voMapConf.get("Threads"));
            int totalNoClienteID = Integer.parseInt(voMapConf.get("NoClienteID"));
            String originationDirection = voMapConf.get("OriginationDirection");
            String pathArchivo = voMapConf.get("PathReporteFinal");
            //SAM - Hay que revisar esta validacion ya que dice Efra que se puede usar un token en dos hilos al mismo tiempo
            if (totalThreads > totalNoClienteID) {
                voLogger.error("[app][" + vsUUI + "] ---> LA CONFIGURACIÓN DE CLIENTES Y HILOS NO ES CORRECTA");
            } else {

                Map<String, String> voMapConfId = RecuperaArhivoConfID();

                if (voMapConf.size() <= 0) {
                    voLogger.error("[app][" + vsUUI + "] ---> NO SE ENCONTRO EL ARCHIVO DE CONFIGURACIÓN O ESTA VACIO");
                } else {

                    /* Genero la carpeta temporal */
                    String timeStamp = new SimpleDateFormat("yyyy_MM_dd HH.mm.ss").format(Calendar.getInstance().getTime());
                    String Archivo = pathArchivo + "temp\\Reporte_" + timeStamp;
                    boolean Ruta = createTempDirectory(Archivo);
                    if (Ruta == true) {
                        /* Genero los token's */
                        List<String> tokenList = GeneraToken(voMapConf, voMapConfId, vsUUI);

                        int i = 0;
                        DataReports voData = new DataReports();
                        voData.setFechaInicio("2021-01-01");
                        voData.setFechaFin(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
                        voData.setVsOrigination(originationDirection);

                        /*
					 * Recupero los ConversationID'S
                         */
                        List<String> listConversationID = new ArrayList<>();
                        RecuperaConversationID recuperaId = new RecuperaConversationID(voData, vsUUI);
                        listConversationID.addAll(recuperaId.RecuperaConverStatID(tokenList.get(0), vsUUI, originationDirection));

      	
                    	
                    	//Genero mis indices dependiendo de mi variables totalThreads
                	    List<List<String>> listConversationThrea = new ArrayList<List<String>>();
                    	for(int h = 0 ; h < totalThreads; h++){
                    		listConversationThrea.add(new ArrayList<String>());
                    	}
                    	
                    	int totalThread=0;
                    	totalThreads--;
                    	for(int b = 0 ; b < listConversationID.size(); b++){
                    		
                    		if(totalThread < totalThreads) {
                    			
                    			
                    			listConversationThrea.get(totalThread).add(listConversationID.get(b));
                    			totalThread ++;
                    			
                    		}else {
                    			listConversationThrea.get(totalThread).add(listConversationID.get(b));
                    			totalThread=0;
                    		}   		
                    	}
                    	
                    	for(int h = 0 ; h <= totalThreads; h++){
                    		
                    			
                    			Reporteador voReporte = new Reporteador(voData,vsUUI,tokenList.get(h),vsUUI,listConversationThrea.get(h),Archivo ); 	
                    			voReporte.start();
                    			
                    			try {
        							voReporte.join(10);
        						} catch (InterruptedException e) {
        							// TODO Auto-generated catch block
        							e.printStackTrace();
        						}	
                    			
                    			
                    	}
                    	
                    	
						
                        /* Se empieza a generar el CSV con los archivos que existen en el directorio de trabajo */
                        File dir = new File(Archivo);
                        /* Se buscan los archivos que terminen con extension .txt*/
                        File[] files = dir.listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.toLowerCase().endsWith(".txt");
                            }
                        });
                        /* Recorremos el listado de los archivos recuperados */
                        if (files.length != 0) {
                            //Creamos el csv antes de recorrer los archivos
                            //Obteniendo los encabezados
                            Map<String, Object> voMapHeadersCSV = new HashMap<String, Object>();
                            DataReportGDSmx voDataBBVAmx = new DataReportGDSmx();
                            GDSmx voAppBBVAMx = new GDSmx(voMapConf, voDataBBVAmx);
                            voMapHeadersCSV = voAppBBVAMx.getHeaderCSV();

                            //for (String vsContactId : vlContactId) {
                            //Map<String, String> voDetails = voConversations.get(vsContactId);
                            //voAppBBVAMx.analizar(voDetails);
                            //voConversations.replace(vsContactId, voDetails);
                            //voMapHeaderCSV = voAppBBVAMx.getHeaderCSV();
                            //}
                            List content = new ArrayList();
                            GeneradorCSV generaExcel = new GeneradorCSV();
                            for (int x = 0; x < files.length; x++) {
                                File file = files[x];
                                System.out.println("Archivo recuperado: " + file);
                                //Leemos el txt recibido por parametro
                                FileReader fileReaderConversations = null;
                                String lineContent = "";
                                
                                try {
                                    //sleep(3000);
                                    fileReaderConversations = new FileReader(file);
                                    BufferedReader buffer = new BufferedReader(fileReaderConversations);
                                    while ((lineContent = buffer.readLine()) != null) {
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
                                } finally {
                                    try {
                                        if (null != fileReaderConversations) {
                                            fileReaderConversations.close();
                                        }
                                    } catch (IOException ex) {
                                        java.util.logging.Logger.getLogger(Reporteador.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                //Enviamos el contenido del archivo a la funcion 
                                String path = Archivo+ "\\ReporteFinal";
                                System.out.println("El directorio a enviar es: "+path);
                                
                                
                            }
                            boolean resultadoCsv = generaExcel.GeneraReportCSV(Archivo+ "\\ReporteFinal", content, vsUUI,voMapHeadersCSV);
                           System.out.println("resultado de la generacion del archivo: "+resultadoCsv+" Con un tamanio de content: "+content);
                        } else {
                            System.out.println("El directorio no contiene extensiones de tipo '.txt'");
                        }
                    }
                    voLogger.error("[Generador][" + vsUUI + "] ---> ERROR : NO SE  CREO LA CARPETA TEMPORAL");
                    //Se tendria que terminar el programa aquí con algun return o break
                }

            }
        }
        //new app();
    }

    public static Map<String, String> RecuperaArhivoConf(String vsUUI) {

        Map<String, String> voMapConf = new HashMap<>();
        Utilerias voUtil = null;
        voUtil = new Utilerias();
        voUtil.getProperties(voMapConf, vsUUI);
        return voMapConf;
    }

    public static Map<String, String> RecuperaArhivoConfID() {

        Map<String, String> voMapConfId = new HashMap<>();
        Utilerias voUtil = null;
        voUtil = new Utilerias();
        voUtil.getPropertiesID(voMapConfId);
        return voMapConfId;
    }

    public static String GeneraCadenaUUI(String vsUUI) {

        GenesysCloud voPureCloud = new GenesysCloud();
        vsUUI = java.util.UUID.randomUUID().toString();
        voPureCloud.setUUI(vsUUI);

        return vsUUI;
    }

    public static List<String> GeneraToken(Map<String, String> voMapConf, Map<String, String> voMapConfId, String vsUUI) {

        List<String> Token = new ArrayList<>();
        String clientsNum = voMapConf.get("NoClienteID");
        int total = Integer.parseInt(clientsNum);

        for (int i = 0; i < total; i++) {

            String clientNum = "ClientId" + i;
            String clientSecr = "ClientSecret" + i;
            //Seteamos los valores recuperados
            String idClient = voMapConfId.get(clientNum);
            String clientSecret = voMapConfId.get(clientSecr);

            //Generamos los Tokents  
            GenesysCloud voPureCloud = new GenesysCloud();
            String vsToken = voPureCloud.getToken(idClient, clientSecret, vsUUI);

            Token.add(vsToken);

            if (vsToken.equals("ERROR")) {

                voLogger.error("[Reporteador][" + vsUUI + "] ---> [ClientID] AND [ClientSecret] ARE INCORRECT." + idClient);

            }

        }

        return Token;
    }

    public static boolean createTempDirectory(String ruta) {

        File Directory = new File(ruta);
        if (Directory.mkdirs()) {
            return true;
        } else {
            return false;
        }

    }
    /* public static Map<String, Object> obtenerEncabezados() {
        Map<String, Object> voMapHeaderCSV = new HashMap<String, Object>();
        DataReportGDSmx voDataBBVAmx = new DataReportGDSmx();
        GDSmx voAppBBVAMx = new GDSmx(voMapConf, voDataBBVAmx);
        voMapHeaderCSV = voAppBBVAMx.getHeaderCSV();
        return voMapHeaderCSV;

    } */

}
