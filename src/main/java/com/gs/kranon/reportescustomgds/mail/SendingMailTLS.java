/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gs.kranon.reportescustomgds.mail;

import com.gs.kranon.reportescustomgds.cuadroMando.ReporteMail;
import com.gs.kranon.reportescustomgds.utilidades.Utilerias;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.LogManager;

/**
 *
 * @author xme7845_1
 */
public class SendingMailTLS {
    static {    
        System.setProperty("dateLog", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
    }
    private Utilerias voUtil = new Utilerias();
    private Map<String,String> voMapConf = new HashMap<>();
    private ReporteMail datosMail = new ReporteMail();

    private static final org.apache.log4j.Logger voLogger = LogManager.getLogger("Reporte");
    
    public boolean sendMailKranon(String destinatario, String asunto, String vsUUI){
                /***********************************************************/
    /* Leemos la configuracion del archivo de configuracion */
    voUtil.getProperties(voMapConf, "");
    final String username = voMapConf.get("MailUsername");
    final String password = voMapConf.get("MailPassword");
    final String mailAuth= voMapConf.get("MailAuth");
    final String mailEnable= voMapConf.get("MailEnable");
    final String mailHost= voMapConf.get("MailHost");
    final String mailPort= voMapConf.get("MailPort");
    Properties props = new Properties();
    props.put("mail.smtp.auth", mailAuth);
    props.put("mail.smtp.starttls.enable", mailEnable);
    props.put("mail.smtp.host", mailHost);
    props.put("mail.smtp.port", mailPort);
    Session session = Session.getInstance(props,
      new javax.mail.Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
      });
    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(username));
      message.setRecipients(Message.RecipientType.TO,
        InternetAddress.parse(destinatario)); //vfrancisco@kranon.com
      message.setSubject(asunto); //Bitacora de ejecución del reporte customizado de GDS
      message.setContent("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n"
      		+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n"
      		+ "<head>\r\n"
      		+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\r\n"
      		+ "  <title>Reporte de GDS</title>\r\n"
      		+ "\r\n"
      		+ "</head>\r\n"
      		+ "<body>\r\n"
      		+ "  <div id=\"wrapper\">\r\n"
      		+ "    <div class=\"hero\">\r\n"
      		+ "       <div class=\"row\">\r\n"
      		+ "	   <div style=\"height:80%!important;width:70%!important; margin:0;padding:0\">\r\n"
      		+ "<table class=\"demoTable\" style=\"height: 165px; width: 659px;\">\r\n"
      		+ "<thead>\r\n"
      		+ "<tr style=\"height: 18px;\">\r\n"
      		+ "<td style=\"width: 236.719px; height: 18px;\" colspan=\"4\"><span style=\"color: #c82828;\">&nbsp;</span><span style=\"color: #c82828;\">&nbsp;</span><strong>Configuración de ejecución</strong></td>\r\n"
      		+ "</tr>\r\n"
      		+ "</thead>\r\n"
      		+ "<tbody>\r\n"
      		+ "<tr style=\"height: 36px;\">\r\n"
      		+ "<td style=\"width: 310.938px; height: 46px;\" colspan=\"2\">\r\n"
      		+ "<p>Fecha de Ejecuci&oacute;n del Reporte</p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"height: 46px; width: 341.062px;\" colspan=\"2\"><span style=\"color: #c82828;\">"+ReporteMail.fechaEjecucion+"</span></td>\r\n"
      		+ "</tr>\r\n"
      		+ "<tr style=\"height: 46px;\">\r\n"
      		+ "<td style=\"width: 236.719px; height: 46px;\">\r\n"
      		+ "<p>Intervalos de Tiempo</p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"width: 71.2188px; height: 46px;\"><span style=\"color: #c82828;\">"+ReporteMail.intervaloTiempo+"</span></td>\r\n"
      		+ "<td style=\"width: 182.547px; height: 46px;\">\r\n"
      		+ "<p>Duraci&oacute;n de Intervalo</p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"width: 155.516px; height: 46px;\"><span style=\"color: #c82828;\">"+ReporteMail.duracionIntervalo+"</span></td>\r\n"
      		+ "</tr>\r\n"
      		+ "<tr style=\"height: 55px;\">\r\n"
      		+ "<td style=\"width: 310.938px; height: 55px;\" colspan=\"2\">\r\n"
      		+ "<p>Tipo de Interacciones Consultadas</p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"width: 341.062px; height: 55px;\" colspan=\"2\"><span style=\"color: #c82828;\">"+ReporteMail.tipoInteracciones+"</span></td>\r\n"
      		+ "</tr>\r\n"
      		+ "</tbody>\r\n"
      		+ "</table>\r\n"
      		+ "<hr color=\"\" />\r\n"
      		+ "<table class=\"demoTable\" style=\"height: 165px; width: 659px;\">\r\n"
      		+ "<thead>\r\n"
      		+ "<tr style=\"height: 18px;\">\r\n"
      		+ "<td style=\"width: 236.719px; height: 18px;\" colspan=\"4\"><span style=\"color: #c82828;\">&nbsp;</span><strong>Tiempos de ejecución</strong></td>\r\n"
      		+ "</tr>\r\n"
      		+ "</thead>\r\n"
      		+ "<tbody>\r\n"
      		+ "<tr style=\"height: 36px;\">\r\n"
      		+ "<td style=\"width: 310.938px; height: 46px;\" colspan=\"2\">\r\n"
      		+ "<p>Fecha-Hora Inicio del Proceso</p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"height: 46px; width: 341.062px;\" colspan=\"2\"><span style=\"color: #c82828;\">"+ReporteMail.inicioProceso+"</span></td>\r\n"
      		+ "</tr>\r\n"
      		+ "<tr style=\"height: 46px;\">\r\n"
      		+ "<td style=\"width: 236.719px; height: 46px;\" colspan=\"2\">\r\n"
      		+ "<p>Fecha-Hora Fin del Proceso</p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"width: 182.547px; height: 46px;\" colspan=\"2\">\r\n"
      		+ "<p><span style=\"color: #c82828;\">"+ReporteMail.finProceso+"</span></p>\r\n"
      		+ "</td>\r\n"
      		+ "</tr>\r\n"
      		+ "<tr style=\"height: 55px;\">\r\n"
      		+ "<td style=\"height: 55px;\" colspan=\"2\">\r\n"
      		+ "<p>Tiempo de Ejecuci&oacute;n</p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"height: 55px;\" colspan=\"2\"><span style=\"color: #c82828;\">00h 10' 03''</span></td>\r\n"
      		+ "</tr>\r\n"
      		+ "<tr>\r\n"
      		+ "<td style=\"width: 310.938px;\" colspan=\"2\">\r\n"
      		+ "<p>N&uacute;mero de Hits</p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"width: 341.062px;\" colspan=\"2\"><span style=\"color: #c82828;\">"+ReporteMail.numeroHits+"</span></td>\r\n"
      		+ "</tr>\r\n"
      		+ "</tbody>\r\n"
      		+ "</table>\r\n"
      		+ "<hr color=\"\" />\r\n"
      		+ "<table class=\"demoTable\" style=\"height: 98px; width: 659px;\">\r\n"
      		+ "<thead>\r\n"
      		+ "<tr style=\"height: 18px;\">\r\n"
      		+ "<td style=\"width: 236.719px; height: 18px;\" colspan=\"4\"><span style=\"color: #c82828;\">&nbsp;</span><strong>Analytics</strong></td>\r\n"
      		+ "</tr>\r\n"
      		+ "</thead>\r\n"
      		+ "<tbody>\r\n"
      		+ "<tr style=\"height: 36px;\">\r\n"
      		+ "<td style=\"width: 439.312px; height: 37px;\" colspan=\"2\">\r\n"
      		+ "<p>N&uacute;mero de P&aacute;ginas Retornadas por el Analytics</p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"height: 37px; width: 212.688px;\" colspan=\"2\"><span style=\"color: #c82828;\">"+ReporteMail.paginasRetornadas+"</span></td>\r\n"
      		+ "</tr>\r\n"
      		+ "<tr style=\"height: 46px;\">\r\n"
      		+ "<td style=\"width: 439.312px; height: 43px;\" colspan=\"2\">\r\n"
      		+ "<p>N&uacute;mero de P&aacute;ginas Retornadas con Error</p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"width: 212.688px; height: 43px;\" colspan=\"2\">\r\n"
      		+ "<p><span style=\"color: #c82828;\">"+ReporteMail.paginasRetornadasErr+"</span></p>\r\n"
      		+ "</td>\r\n"
      		+ "</tr>\r\n"
      		+ "</tbody>\r\n"
      		+ "</table>\r\n"
      		+ "<hr color=\"\" />\r\n"
      		+ "<table class=\"demoTable\" style=\"height: 165px; width: 659px;\">\r\n"
      		+ "<thead>\r\n"
      		+ "<tr style=\"height: 18px;\">\r\n"
      		+ "<td style=\"width: 236.719px; height: 18px;\" colspan=\"4\"><span style=\"color: #c82828;\">&nbsp;</span><strong>Excepciones generadas</strong></td>\r\n"
      		+ "</tr>\r\n"
      		+ "</thead>\r\n"
      		+ "<tbody>\r\n"
      		+ "<tr style=\"height: 36px;\">\r\n"
      		+ "<td style=\"width: 440.328px; height: 46px;\" colspan=\"2\">\r\n"
      		+ "<p>N&uacute;mero de ConversationIDs Obtenidos Exitosamente</p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"height: 46px; width: 211.672px;\" colspan=\"2\"><span style=\"color: #c82828;\">"+ReporteMail.conversationsIdOK+"</span></td>\r\n"
      		+ "</tr>\r\n"
      		+ "<tr style=\"height: 46px;\">\r\n"
      		+ "<td style=\"width: 440.328px; height: 46px;\" colspan=\"2\">\r\n"
      		+ "<p>N&uacute;mero de Excepciones (300, 400, 500)</p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"width: 211.672px; height: 46px;\" colspan=\"2\">\r\n"
      		+ "<p><span style=\"color: #c82828;\">"+ReporteMail.excepcionesHttp+"</span></p>\r\n"
      		+ "</td>\r\n"
      		+ "</tr>\r\n"
      		+ "<tr style=\"height: 55px;\">\r\n"
      		+ "<td style=\"height: 55px; width: 440.328px;\" colspan=\"2\">\r\n"
      		+ "<p>N&uacute;mero de Excepciones TimeOut</p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"height: 55px; width: 211.672px;\" colspan=\"2\"><span style=\"color: #c82828;\">"+ReporteMail.excepcionesTimeout+"</span></td>\r\n"
      		+ "</tr>\r\n"
      		+ "<tr>\r\n"
      		+ "<td style=\"width: 440.328px;\" colspan=\"2\">\r\n"
      		+ "<p>N&uacute;mero de Excepciones Generales</p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"width: 211.672px;\" colspan=\"2\"><span style=\"color: #c82828;\">"+ReporteMail.excepcionesGrales+"</span></td>\r\n"
      		+ "</tr>\r\n"
      		+ "</tbody>\r\n"
      		+ "</table>\r\n"
      		+ "<hr color=\"\" />\r\n"
      		+ "<table class=\"demoTable\" style=\"height: 165px; width: 1065px;\">\r\n"
      		+ "<thead>\r\n"
      		+ "<tr style=\"height: 18px;\">\r\n"
      		+ "<td style=\"width: 215.656px; height: 18px;\" colspan=\"7\"><span style=\"color: #c82828;\">&nbsp;</span><strong>Archivos generados</strong></td>\r\n"
      		+ "</tr>\r\n"
      		+ "</thead>\r\n"
      		+ "<tbody>\r\n"
      		+ "<tr style=\"height: 36px;\">\r\n"
      		+ "<td style=\"width: 223.469px; height: 46px;\" colspan=\"2\">\r\n"
      		+ "<p>Archivo Final CSV</p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"height: 46px; width: 351.047px;\" colspan=\"2\"><b>"+ReporteMail.pathCsvFinal+"</b></td>\r\n"
      		+ "<td style=\"width: 151.453px;\"><span style=\"color: #c82828;\">&nbsp;</span></td>\r\n"
      		+ "<td style=\"width: 225.688px;\">\r\n"
      		+ "<p>N&uacute;mero de L&iacute;neas</p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"width: 97.3438px;\">"+ReporteMail.lineasCsvFinal+"</td>\r\n"
      		+ "</tr>\r\n"
      		+ "<tr style=\"height: 46px;\">\r\n"
      		+ "<td style=\"width: 223.469px; height: 46px;\" colspan=\"2\">\r\n"
      		+ "<p>P&aacute;ginas NO Procesadas</p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"height: 46px; width: 351.047px;\" colspan=\"2\">\r\n"
      		+ "<p><b>"+ReporteMail.pathPagNoProcesadas+"</b></p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"width: 151.453px;\">\r\n"
      		+ "<p><span style=\"color: #c82828;\">&nbsp;</span></p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"width: 225.688px;\">\r\n"
      		+ "<p>N&uacute;mero de L&iacute;neas<span style=\"color: #c82828;\"></span></p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"width: 97.3438px;\">\r\n"
      		+ "<p>"+ReporteMail.lineasPagNoProcesadas+"</p>\r\n"
      		+ "</td>\r\n"
      		+ "</tr>\r\n"
      		+ "<tr style=\"height: 55px;\">\r\n"
      		+ "<td style=\"height: 55px; width: 223.469px;\" colspan=\"2\">\r\n"
      		+ "<p>Interacciones NO Procesadas</p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"height: 55px; width: 351.047px;\" colspan=\"2\">\r\n"
      		+ "<p><b>"+ReporteMail.pathInteraccionesNoProcesadas+"</b></p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"width: 151.453px;\"><span style=\"color: #c82828;\">&nbsp;</span></td>\r\n"
      		+ "<td style=\"width: 225.688px;\">\r\n"
      		+ "<p>N&uacute;mero de L&iacute;neas</p>\r\n"
      		+ "</td>\r\n"
      		+ "<td style=\"width: 97.3438px;\">\r\n"
      		+ "<p>"+ReporteMail.lineasInteraccionesNoProcesadas+"</p>\r\n"
      		+ "</td>\r\n"
      		+ "</tr>\r\n"
      		+ "</tbody>\r\n"
      		+ "</table>\r\n"
      		+ "</div>\r\n"
      		+ "	   \r\n"
      		+ "	   \r\n"
      		+ "\r\n"
      		+ "       </div>\r\n"
      		+ "    </div>\r\n"
      		+ "    <div id=\"lowerContainer\" class=\"row\">\r\n"
      		+ "	  \r\n"
      		+ "    </div>\r\n"
      		+ "  </div>\r\n"
      		+ "  <footer>\r\n"
      		+ "    <div class=\"row\">\r\n"
      		+ "      <div class=\"large-12 columns\">\r\n"
      		+ "        <div class=\"row\">\r\n"
      		+ "\r\n"
      		+ "          <div class=\"text-center\">\r\n"
      		+ "            <p class=\"text-center\">Copyright (c) 2021, Promotora Kranon</p>\r\n"
      		+ "          </div>\r\n"
      		+ "        </div>\r\n"
      		+ "      </div>\r\n"
      		+ "    </div>\r\n"
      		+ "  </footer>\r\n"
      		+ "\r\n"
      		+ "</body>\r\n"
      		+ "</html>\r\n"
      		+ "","text/html");
      Transport.send(message);
      System.out.println("Correcto!");
    } catch (MessagingException e) {      
      throw new RuntimeException(e);
    }
    voLogger.info("[SendingMailTLS  ][" + vsUUI + "] ---> CORREO ENVIADO EXITOSAMENTE A: "+"[ "+destinatario+"]");
        return true;
    }
    
}
