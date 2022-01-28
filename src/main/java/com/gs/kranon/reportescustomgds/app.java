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
		System.out.println("El usuario es " + System.setProperty("user", System.getProperty("user.name")));
		System.setProperty("diagonal", File.separator);
		
		if(args.length > 0) {
			System.out.println("Valor recibido desde consola: "+args[0]);
			/* Asigno la fecha recibida por parametro */
			EjecucionPrincipal ejecutarApp = new EjecucionPrincipal(args[0]);
			//ejecutarApp.ejecutar();
			System.exit(0);
			//ejecutarApp.execute(EjecucionPrincipal.class);
			//strYesterda = args[0];
		} else {
			/* Ejecuntando la aplicacion mediante el CRON */

		try { // CREANDO EL JOB A EJECUTAR

			System.out.println("Entrando a crear el JOB");

			Map<String, String> voMapConf = RecuperaArhivoConf("1234567890");
			String EjecucionCron = voMapConf.get("EjecucionCron");
			System.out.println("La fecha de mi croon es " + EjecucionCron);
			System.err.println("Entrando a crear el JOB");

			JobDetail voJob = JobBuilder.newJob(EjecucionPrincipal.class)
					.withIdentity("JobReportesCustom", "ReportesGSD").build();


			// CREADO EL HILO QUE EJECUTARA EL JOB 
			Trigger voTrigger = TriggerBuilder.newTrigger().withIdentity("TriggerReportesCustom", "ReportesGSD").startNow()
					.withSchedule(CronScheduleBuilder.cronSchedule(EjecucionCron)).build();
			System.err.println("Entrando a crear el JOB");

			Scheduler voScheduler = StdSchedulerFactory.getDefaultScheduler();
			voScheduler.start();
			voScheduler.scheduleJob(voJob, voTrigger);
		} catch (SchedulerException ex) {
			System.err.println(ex.getMessage());
		}
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