/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gs.kranon.reportescustomgds;

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
		try { // CREANDO EL JOB A EJECUTAR
			System.out.println("Entrando a crear el JOB");
			JobDetail voJob = JobBuilder.newJob(EjecucionPrincipal.class)
					.withIdentity("JobReportesCustom", "ReportesGSD").build();


			// CREADO EL HILO QUE EJECUTARA EL JOB 
			Trigger voTrigger = TriggerBuilder.newTrigger().withIdentity("TriggerReportesCustom", "ReportesGSD").startNow()
					.withSchedule(CronScheduleBuilder.cronSchedule("0 15 10 * * ? *")).build();
			System.out.println("Programando el JOB");
			Scheduler voScheduler = StdSchedulerFactory.getDefaultScheduler();
			voScheduler.start();
			voScheduler.scheduleJob(voJob, voTrigger);
		} catch (SchedulerException ex) {
			System.err.println(ex.getMessage());
		}

	}

	

}