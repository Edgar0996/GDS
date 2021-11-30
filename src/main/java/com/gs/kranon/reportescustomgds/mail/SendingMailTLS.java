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

    voUtil.getProperties(voMapConf);
    final String username = voMapConf.get("MailUsername");;
    final String password = voMapConf.get("MailPassword");
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");
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
      message.setText("Estimado colega de Promotora Kranon,"
        + "\n\n Te enviamos las estadisticas de la ejecución del reporte customizado de General de Seguros!");
      Transport.send(message);
      System.out.println("Correcto!");
    } catch (MessagingException e) {      
      throw new RuntimeException(e);
    }
    voLogger.info("[SendingMailTLS  ][" + vsUUI + "] ---> Correo enviado exitosamente a: "+"[ "+destinatario+"]");
        return true;
    }
    
}
