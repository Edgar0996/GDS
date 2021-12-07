/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gs.kranon.reportescustomgds;

import com.gs.kranon.reportescustomgds.genesysCloud.GenesysCloud;
import com.gs.kranon.reportescustomgds.utilidades.Utilerias;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.gs.kranon.reportescustomgds.conexionHttp.ConexionResponse;
import com.gs.kranon.reportescustomgds.conexionHttp.ConexionHttp;
import com.gs.kranon.reportescustomgds.mail.SendingMailTLS;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.JOptionPane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.gs.kranon.reportescustomgds.reporteador.Reporteador;
import static java.lang.System.exit;
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

    public static void main(String[] args) {

        /* Genero mi cadena UUI */
        String vsUUI = GeneraCadenaUUI("1234567890");

        /* Invoco mi archivos de configuración */
        Map<String, String> voMapConf = RecuperaArhivoConf(vsUUI);

        if (voMapConf.size() <= 0) {
            voLogger.error("[app][" + vsUUI + "] ---> NO SE ENCONTRO EL ARCHIVO DE CONFIGURACIÓN O ESTA VACIO");
            exit(0);
        } else {
            //String Threads = voMapConf.get("Threads");
            //String NoClienteID = voMapConf.get("NoClienteID");
            int totalThreads = Integer.parseInt(voMapConf.get("Threads"));
            int totalNoClienteID = Integer.parseInt(voMapConf.get("NoClienteID"));
            String originationDirection = voMapConf.get("OriginationDirection");
            //SAM - Hay que revisar esta validacion ya que dice Efra que se puede usar un token en dos hilos al mismo tiempo
            if (totalThreads > totalNoClienteID) {
                voLogger.error("[app][" + vsUUI + "] ---> LA CONFIGURACIÓN DE CLIENTES Y HILOS NO ES CORRECTA");
            } else {

                Map<String, String> voMapConfId = RecuperaArhivoConfID();

                if (voMapConf.size() <= 0) {
                    voLogger.error("[app][" + vsUUI + "] ---> NO SE ENCONTRO EL ARCHIVO DE CONFIGURACIÓN O ESTA VACIO");
                } else {
                    /* Genero los token's */
                    List<String> GeneraCadenaUUI = GeneraCadenaUUI(voMapConf, voMapConfId, vsUUI);
                    System.out.println("Tokents 142 " + GeneraCadenaUUI.size());
                    int i = 0;
                    DataReports voData = new DataReports();
                    voData.setFechaInicio("2021-01-01");
                    voData.setFechaFin(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));

                    for (String vsToKen : GeneraCadenaUUI) {

                        Reporteador voReporte = new Reporteador(voData);
                        voReporte.getDataReport(vsToKen);

                        System.out.println("Si entro aquí ");

                    }
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

    public static List<String> GeneraCadenaUUI(Map<String, String> voMapConf, Map<String, String> voMapConfId, String vsUUI) {

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
            String vsToken = voPureCloud.getToken(idClient, clientSecret);

            Token.add(vsToken);

            if (vsToken.equals("ERROR")) {

                voLogger.error("[Reporteador][" + vsUUI + "] ---> [ClientID] AND [ClientSecret] ARE INCORRECT." + idClient);

            }

        }

        return Token;
    }

    public app() {
        //Invocamos nuestro primer archivo de configuración 

        voMapConf = new HashMap<>();
        voPureCloud = new GenesysCloud();

        vsUUI = java.util.UUID.randomUUID().toString();
        voPureCloud.setUUI(vsUUI);
        voLogger.info("[Main  ][" + vsUUI + "] ---> Inicia ejecución de la app.");

        voUtil.getProperties(voMapConf, vsUUI);
        String clientsNum = voMapConf.get("NoClienteID");
        pathArchivo = voMapConf.get("PathReporteFinal");

        //Invocamos nuestro segundo archivo de configuración para traer los ID's 
        voMapConfId = new HashMap<>();
        voUtil.getPropertiesID(voMapConfId);

        int total = Integer.parseInt(clientsNum);
        //Generamos el for para recuperar solo los ClientesId que se colocaron en la configuración
        for (int i = 0; i < total; i++) {
            String clientNum = "ClientId" + i;
            String clientSecr = "ClientSecret" + i;
            //Seteamos los valores recuperados
            String idClient = voMapConfId.get(clientNum);
            String clientSecret = voMapConfId.get(clientSecr);

            //Generamos los Tokents  
            vsUUI = java.util.UUID.randomUUID().toString();
            voPureCloud.setUUI(vsUUI);
            vsToken = voPureCloud.getToken(idClient, clientSecret);
            //System.out.println("Tokents 104 " + vsToken);
            // Invocamos el metodo para recuperar los id's
            voData = new DataReports();
            voData.setFechaInicio("2021-01-01");
            voData.setFechaFin(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));

            voLogger.info("[Main  ][" + vsUUI + "] ---> Fecha del reporte final." + "[ " + voData.getFechaInicio() + "]");
            voReporte = new Reporteador(voData);
            voReporte.getDataReport(vsToken);

            if (vsToken.equals("ERROR")) {

                voLogger.error("[Reporteador][" + vsUUI + "] ---> [ClientID] AND [ClientSecret] ARE INCORRECT." + idClient);
                System.out.println("Se genero un error");
            }

        }

        /*
                voData = new DataReports();
                voData.setFlowName("bbva_bbvamxap_ivr");
                //voData.setFechaInicio(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
                voData.setFechaInicio("2021-01-01");
                voData.setFechaFin(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
                voLogger.info("[Main  ][" + vsUUI + "] ---> Fecha del reporte final."+"[ "+voData.getFechaInicio()+"]");
                JProgressBar voProgreso = null;
                JTextArea voTextArea = null;
                voReporte = new Reporteador(voData);
                voReporte.getDataReport(vsToken);
                
               
             String destinatario= voMapConf.get("MailDestinatario");
                SendingMailTLS enviarcorreo = new SendingMailTLS();
                 boolean result = enviarcorreo.sendMailKranon(destinatario,"Reporte de la ejecución del servicio de GDS", vsUUI);
                System.out.print("El resultado del envio del correo fue: "+result);*/
    }

}
