package com.gs.kranon.reportescustomgds.mail;

import com.gs.kranon.reportescustomgds.cuadroMando.ReporteMail;
import com.gs.kranon.reportescustomgds.utilidades.Utilerias;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.LogManager;

public class SendingMailTLSFiles {
    static {    
        System.setProperty("dateLog", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
    }
    private Utilerias voUtil = new Utilerias();
    private Map<String,String> voMapConf = new HashMap<>();
    private ReporteMail datosMail = new ReporteMail();

    private static final org.apache.log4j.Logger voLogger = LogManager.getLogger("Reporte");
    
    public boolean sendMailKranonFiles(String asunto, String vsUUI, String file, String name){
                /***********************************************************/
    /* Leemos la configuracion del archivo de configuracion */
    voUtil.getProperties(voMapConf, "");
    final String username = voMapConf.get("MailUsername");
    final String password = voMapConf.get("MailPassword");
    final String mailAuth= voMapConf.get("MailAuth");
    final String mailEnable= voMapConf.get("MailEnable");
    final String mailHost= voMapConf.get("MailHost");
    final String mailPort= voMapConf.get("MailPort");
    final String recipients = voMapConf.get("MailDestinatarioGDS");
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
    	//Se compone la parte de la plantilla del correo
    BodyPart texto = new MimeBodyPart();
    texto.setContent("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n"
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
      		+ "<p><center><FONT SIZE=4><span style=\"color: #ff0000;\">Fecha Reporte: "+ReporteMail.strYesterda+" </span></font></center></p>\r\n"
      		+ "<div style=\"height:80%!important;width:70%!important; margin:0;padding:0\">\r\n"
      		+ "<hr color=\"\" />\r\n"
      		+ "<table class=\"demoTable\" style=\"height: 165px; width: 659px;\">\r\n"
      		+ "<thead>\r\n"
      		+ "<tr style=\"height: 18px;\">\r\n"
      		+ "<td style=\"width: 236.719px; height: 18px;\" colspan=\"4\"><span style=\"color: #c82828;\"></span><span style=\"color: #c82828;\"></span><strong>Notificación automática del envío del reporte inbound</strong></td>\r\n"
      		+ "</tr>\r\n"
      		+ "</thead>\r\n"
      		+ "<tbody>\r\n"
      		+ "<tr style=\"height: 36px;\">\r\n"
      		+ "<td style=\"width: 310.938px; height: 46px;\" colspan=\"2\">\r\n"
      		+ "<p>Fecha de Inicio del proceso:  <span style=\"color: #001aff;\">"+ReporteMail.inicioProceso+"</span> </p>\r\n"
      		+ "</td>\r\n"
      		+ "</tr>\r\n"
      		+ "<tr style=\"height: 46px;\">\r\n"
      		+ "<td style=\"width: 236.719px; height: 46px;\">\r\n"
      		+ "<p>Fecha de Fin de proceso: <span style=\"color: #001aff;\">"+ReporteMail.finProceso+"</span></p>\r\n"
      		+ "</td>\r\n"
      		+ "</tr>\r\n"
      		+ "<tr style=\"height: 55px;\">\r\n"
      		+ "<td style=\"width: 310.938px; height: 55px;\" colspan=\"2\">\r\n"
      		+ "<p>Archivo Adjunto: <span style=\"color: #001aff;\">"+ReporteMail.nameCsvFinal+"</span></p>\r\n"
      		+ "</td>\r\n"
      		+ "</tr>\r\n"
      		+ "</tbody>\r\n"
      		+ "</table>\r\n"
      		+ "<hr color=\"\" />\r\n"
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
      		+ "            <p class=\"text-center\">Copyright (c) 2021, Promotora Kranon SA de CV</p>\r\n"
      		+ "          </div>\r\n"
      		+ "        </div>\r\n"
      		+ "      </div>\r\n"
      		+ "    </div>\r\n"
      		+ "  </footer>\r\n"
      		+ "\r\n"
      		+ "</body>\r\n"
      		+ "</html>\r\n"
      		+ "","text/html");
    //Se compone la parte del archivo
    BodyPart adjunto = new MimeBodyPart();
    adjunto.setDataHandler(new DataHandler(new FileDataSource(file)));
    adjunto.setFileName("ReporteFinal_"+name+".csv");
    //Generamos un miltipart para adjuntar todo en el mensaje
    MimeMultipart multipart = new MimeMultipart();
    multipart.addBodyPart(texto);
    multipart.addBodyPart(adjunto);
      Message message = new MimeMessage(session);
      String[] parts = recipients.split(",");
      ArrayList<String> email = new ArrayList<>(Arrays.asList(parts));
      //String[] parts = recipients.split(",");
      InternetAddress[] address = new InternetAddress[email.size()];
      for (int i = 0; i < email.size(); i++) {
          address[i] = new InternetAddress(email.get(i));
      }
       message.setRecipients(Message.RecipientType.TO, address);
      //message.setRecipients(Message.RecipientType.TO,
        //InternetAddress.parse(destinatario)); //vfrancisco@kranon.com
      message.setSubject(asunto); //Bitacora de ejecución del reporte customizado de GDS
      message.setContent(multipart);
      Transport.send(message);
      //System.out.println("Correcto!");
    } catch (MessagingException e) {      
      throw new RuntimeException(e);
    }
    voLogger.info("[SendingMailTLSFiles  ][" + vsUUI + "] ---> CORREO ENVIADO A GDS EXITOSAMENTE");
        return true;
    }
    
    public static String userDateGMT(String vsFecha) {
    	String dateToStr="";
    	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");      
		dateToStr = dateFormat.format(vsFecha);  
       	
    	return dateToStr;
    }

}
