/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gs.kranon.reportescustomgds.mail;

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
      message.setContent("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
"<head>\n" +
"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
"  <title>Reporte de GDS</title>\n" +
"  <link href=\"bitnami.css\" media=\"all\" rel=\"Stylesheet\" type=\"text/css\" /> \n" +
"  <link href=\"/dashboard/stylesheets/all.css\" rel=\"stylesheet\" type=\"text/css\" />\n" +
"</head>\n" +
"<body>\n" +
"  <div id=\"wrapper\">\n" +
"    <div class=\"hero\">\n" +
"       <div class=\"row\">\n" +
"	   <div style=\"height:100%!important;width:100%!important;background-color:#f4f5f9;margin:0;padding:0\">\n" +
"<table style=\"font-size:13px;color:#39394d;font-family:Arial;background-color:#f4f5f9;width:100%;height:100%;padding-bottom:10px\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\">\n" +
"    <tbody><tr>\n" +
"        <td style=\"vertical-align:top\">\n" +
"            <table id=\"m_-5819779775643716667table-new-registration\" style=\"width:592px\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\">\n" +
"                <tbody><tr>\n" +
"                    <td style=\"vertical-align:top;padding:0px;background-color:#f4f5f9\">\n" +
"                        <table style=\"margin-left:0px;margin-right:0px;width:100%;background-color:#ffffff;padding-bottom:37px\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\">\n" +
"                            <tbody>\n" +
"                            <tr>\n" +
"                                <td id=\"m_-5819779775643716667td1\" style=\"padding-left:28px;padding-right:28px\">\n" +
"                                    <table style=\"width:100%;font-size:13px;color:#39394d;font-family:Arial\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\">\n" +
"\n" +
"                                        <tbody><tr>\n" +
"                                            <td style=\"height:40px\"></td>\n" +
"                                        </tr>\n" +
"\n" +
"                                        <tr style=\"height:12px\"></tr>\n" +
"\n" +
"                                                <tr>\n" +
"                                                    <td colspan=\"2\" style=\"font-size:13px;color:#39394d;font-family:Arial;vertical-align:top;line-height:20px\">\n" +
"                                                        Estimad@ colega de Promotora Kranon, Te enviamos las estadísticas de la ejecución del reporte customizado de General de Seguros!\n" +
"                                                    </td>\n" +
"                                                </tr>\n" +
"\n" +
"                                        <tr style=\"height:16px\"></tr>\n" +
"\n" +
"                                        <tr>\n" +
"                                            <td colspan=\"2\">\n" +
"                                                <table style=\"width:100%;border-radius:10px;background-color:#f7f7fc\">\n" +
"                                                        <tbody><tr>\n" +
"                                                            <td colspan=\"3\" style=\"padding-left:16px;vertical-align:top;padding-top:18px\"><strong>Ejecución del Reporte de General de Seguros</strong></td>\n" +
"                                                        </tr>\n" +
"\n" +
"                                                            <tr>\n" +
"                                                                <td style=\"padding-left:16px;padding-top:16px;height:18px;width:22%;vertical-align:top;color:#747487\">Fecha y hora de inicio:</td>\n" +
"                                                                <td style=\"vertical-align:top;padding-top:16px\">2 dic. 2021 06:00 p.&nbsp;m. </td>\n" +
"\n" +
"                                                            </tr>\n" +
"\n" +
"                                                        <tr>\n" +
"                                                            <td style=\"padding-left:16px;padding-top:16px;height:18px;width:22%;vertical-align:top;color:#747487\">Número de registros:</td>\n" +
"                                                            <td style=\"vertical-align:top;padding-top:16px\">852 7677 8087</td>\n" +
"                                                        </tr>\n" +
"                                                        <tr>\n" +
"                                                            <td style=\"padding-left:16px;padding-top:16px;height:18px;width:22%;vertical-align:top;color:#747487\">Registros procesados:</td>\n" +
"                                                            <td style=\"vertical-align:top;padding-top:16px\">852 7677 8087</td>\n" +
"                                                        </tr>\n" +
"														<tr>\n" +
"                                                            <td style=\"padding-left:16px;padding-top:16px;height:18px;width:22%;vertical-align:top;color:#747487\">Path del reporte generado:</td>\n" +
"                                                            <td style=\"vertical-align:top;padding-top:16px\">C:\\\\Appl\\\\GS\\\\ReportesCustom\\\\</td>\n" +
"                                                        </tr>\n" +
"														<tr>\n" +
"                                                                <td style=\"padding-left:16px;padding-top:16px;height:18px;width:22%;vertical-align:top;color:#747487\">Fecha y hora de termino:</td>\n" +
"                                                                <td style=\"vertical-align:top;padding-top:16px\">2 dic. 2021 07:00 p.&nbsp;m. </td>\n" +
"\n" +
"                                                        </tr>\n" +
"                                                    <tr>\n" +
"                                                        <td style=\"height:16px;width:22%\"></td>\n" +
"                                                        <td></td>\n" +
"                                                    </tr>\n" +
"\n" +
"                                                </tbody></table>\n" +
"                                            </td>\n" +
"\n" +
"                                        </tr>\n" +
"\n" +
"\n" +
"                                    </tbody></table>\n" +
" \n" +
"                                </td>\n" +
"                            </tr>\n" +
"\n" +
"                        </tbody></table>\n" +
"                    </td>\n" +
"                </tr>\n" +
"            </tbody></table>\n" +
"        </td>\n" +
"    </tr>\n" +
"</tbody></table>\n" +
"</div>\n" +
"	   \n" +
"	   \n" +
"\n" +
"       </div>\n" +
"    </div>\n" +
"    <div id=\"lowerContainer\" class=\"row\">\n" +
"      <div id=\"content\" class=\"large-12 columns\">\n" +
"          <!-- @@BITNAMI_MODULE_PLACEHOLDER@@ -->\n" +
"      </div>\n" +
"	  \n" +
"    </div>\n" +
"  </div>\n" +
"  <footer>\n" +
"    <div class=\"row\">\n" +
"      <div class=\"large-12 columns\">\n" +
"        <div class=\"row\">\n" +
"\n" +
"          <div class=\"large-4 columns\">\n" +
"            <p class=\"text-right\">Copyright (c) 2021, Promotora Kranon</p>\n" +
"          </div>\n" +
"        </div>\n" +
"      </div>\n" +
"    </div>\n" +
"  </footer>\n" +
"\n" +
"</body>\n" +
"</html>","text/html");
      Transport.send(message);
      System.out.println("Correcto!");
    } catch (MessagingException e) {      
      throw new RuntimeException(e);
    }
    voLogger.info("[SendingMailTLS  ][" + vsUUI + "] ---> Correo enviado exitosamente a: "+"[ "+destinatario+"]");
        return true;
    }
    
}
