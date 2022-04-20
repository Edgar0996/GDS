/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gs.kranon.reportescustomgds;

import com.gs.kranon.reportescustomgds.genesysCloud.GenesysCloud;
import com.gs.kranon.reportescustomgds.genesysCloud.RecuperaConversationID;
import com.gs.kranon.reportescustomgds.utilidades.Utilerias;

import java.util.List;
import java.util.Map;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import com.gs.kranon.reportescustomgds.cuadroMando.ReporteMail;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author VFG
 */
public class app {



	public static void main(String[] args) {
		System.setProperty("user", System.getProperty("user.name"));
		//System.out.println("El usuario es " + System.setProperty("user", System.getProperty("user.name")));
		System.setProperty("diagonal", File.separator);
		System.out.println (new File ("").getAbsolutePath ());
	
		ReporteMail.urlJAr= new File ("").getAbsolutePath ();
		System.setProperty("urlJAr", new File ("").getAbsolutePath ());
		
		if(args.length > 0 ) {
			System.setProperty("pathConfig", args[0]);
			if(args.length == 2) {
				//System.out.println("Ruta recibida"+args[0]);
				/* Asigno la fecha recibida por parametro */
				
				ReporteMail.pathConfig= args[0];
				EjecucionPrincipal ejecutarApp = new EjecucionPrincipal(args[0],args[1]);
				//ejecutarApp.ejecutar();
				System.exit(0);
				//ejecutarApp.execute(EjecucionPrincipal.class);
				//strYesterda = args[0];
			}else {
				
				//System.out.println("Ruta recibida"+args[0]);
				/* Asigno la fecha recibida por parametro */
				ReporteMail.pathConfig= args[0];
				EjecucionPrincipal ejecutarApp = new EjecucionPrincipal(args[0]);
				//ejecutarApp.ejecutar();
				System.exit(0);
				//ejecutarApp.execute(EjecucionPrincipal.class);
				//strYesterda = args[0];
				
				
			}
				
			
		} 
		else {
			//EjecucionPrincipal ejecutarApp = new EjecucionPrincipal();
			System.err.println("Error no se recibio ruta de configuración en cabecera");
	 }
	}
	
	
	public static Map<String, String> RecuperaArhivoConf(String vsUUI) {
		Map<String, String> voMapConf = new HashMap<>();
		Utilerias voUtil = null;
		voUtil = new Utilerias();
		voUtil.getProperties(voMapConf, vsUUI);
		return voMapConf;
	}
	

}