/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gs.kranon.reportescustomgds;

import com.gs.kranon.reportescustomgds.genesysCloud.GenesysCloud;
import com.gs.kranon.reportescustomgds.genesysCloud.RecuperaConversationID;
import com.gs.kranon.reportescustomgds.utilidades.FileUtils;
import com.gs.kranon.reportescustomgds.utilidades.Utilerias;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.gs.kranon.reportescustomgds.conexionHttp.ConexionResponse;
import com.gs.kranon.reportescustomgds.cuadroMando.ReporteMail;
import com.gs.kranon.reportescustomgds.conexionHttp.ConexionHttp;
import com.gs.kranon.reportescustomgds.mail.SendingMailTLS;
import com.gs.kranon.reportescustomgds.reporteador.GeneradorCSV;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.gs.kranon.reportescustomgds.reporteador.Reporteador;
import java.io.BufferedReader;
import static java.lang.System.exit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import static java.lang.Thread.sleep;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.crypto.Data;

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
	private List<String> listConversationID = new ArrayList<>();
	/* Variables de prueba */
	private DataReports voData = null;
	/* Variables para mandar a llamar mi reporte */
	private Reporteador voReporte;
	private RecuperaConversationID RecuperaId;
	List<String> Threa;


	public static void main(String[] args) {
		String strYesterda = "";
		if(args.length > 0) {
			/* Asigno la fecha recibida por paramtreo */
			strYesterda = args[0];
		} else {
			/* Recupero la fecha de ayer */
			strYesterda = yesterdaydate();
		}
		for(int c = 0; c < args.length; c++) {
			System.out.println("Argumento "+c+" recibido desde consola: "+args[c]);
		}
		//Inicio de la ejecucion del proceso
		ReporteMail.inicioProceso = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

		/* Genero mi cadena UUI */
		String vsUUI = GeneraCadenaUUI("1234567890");

		/* Invoco mi archivos de configuración */
		Map<String, String> voMapConf = RecuperaArhivoConf(vsUUI);

		

		if (voMapConf.size() <= 0) {
			voLogger.error("[app][" + vsUUI + "] ---> NO SE ENCONTRO EL ARCHIVO DE CONFIGURACIÓN O ESTA VACIO");
			exit(0);
		} else {

			int intTimeFrame = Integer.parseInt(voMapConf.get("TimeFrame"));

			String originationDirection = voMapConf.get("OriginationDirection");
			String pathArchivo = voMapConf.get("PathReporteFinal");

			Map<String, String> voMapConfId = RecuperaArhivoConfID();

			if (voMapConf.size() <= 0) {
				voLogger.error("[app][" + vsUUI + "] ---> NO SE ENCONTRO EL ARCHIVO DE CONFIGURACIÓN O ESTA VACIO");
			} else {

				/* Genero la carpeta temporal */
				String timeStamp = new SimpleDateFormat("yyyy_MM_dd HH.mm.ss").format(Calendar.getInstance().getTime());
				// Asigno la fecha de ejecucion al datamail
				ReporteMail.fechaEjecucion = new SimpleDateFormat("yyyy-MM-dd")
						.format(Calendar.getInstance().getTime());

				String Archivo = pathArchivo + "temp\\Reporte_" + timeStamp;
				boolean Ruta = createTempDirectory(Archivo);
				if (Ruta == true) {
					voLogger.info("[App  ][" + vsUUI + "] ---> *************INICIO DE LA APLICACIÓN*************** ");
					voLogger.info("[App  ][" + vsUUI + "] ---> FECHA DE LA QUE SE GENERARÁ EL REPORTE: " + strYesterda);
					/* Genero los token's */
					List<String> tokenList = GeneraToken(voMapConf, voMapConfId, vsUUI);
					System.out.println(tokenList);
					int sumTotalHits = 0;
					DataReports voData = new DataReports();
					// voData.setFechaInicio("2021-01-01");
					// voData.setFechaFin(new
					// SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
					voData.setVsOrigination(originationDirection);

					try {

						// Segmento las 24 horas del día dependiendo el archivo de configuración
						String strFinalTime = "00:00:00";
						ReporteMail.intervaloTiempo = String.valueOf(1440 / intTimeFrame);
						ReporteMail.duracionIntervalo = String.valueOf(intTimeFrame);
						ReporteMail.tipoInteracciones = originationDirection;
						
						SimpleDateFormat isoFormat = new SimpleDateFormat("HH:mm:ss");
						isoFormat.setTimeZone(TimeZone.getTimeZone("UTC -7"));
						Date date = isoFormat.parse("13:00:00");
						System.out.println("El valor de mi las 00 horas en el formato de Genesys es "+ date );
						for (int a = 0; a < 1440; a = a + intTimeFrame) {
							// System.out.println("Se repite: " + bb);
							String strStartTime = strFinalTime;
							Date datex = new SimpleDateFormat("HH:mm:ss").parse(strFinalTime);
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(datex);
							calendar.add(calendar.MINUTE, intTimeFrame);
							strFinalTime = new SimpleDateFormat("HH:mm:ss").format(calendar.getTime());
							/*
							 * Recupero los ConversationID'S
							 */
							List<String> listConversationID = new ArrayList<>();
							RecuperaConversationID recuperaId = new RecuperaConversationID(vsUUI);
							if (strFinalTime.equals("00:00:00")) {
								strFinalTime = "23:59:59";
								System.out.println("BLOQUE DE HORA: " + strStartTime + " A " + strFinalTime);
								voLogger.info("[Horario  ][" + vsUUI + "] --->  BLOQUE DE HORA[ " + strStartTime + " A "
										+ strFinalTime + "]");
								listConversationID.addAll(recuperaId.RecuperaConverStatID(tokenList.get(0), vsUUI,originationDirection, strYesterda, strStartTime, strFinalTime,Archivo, false));
								String generaerror = " ";
								listConversationID.addAll(recuperaId.RecuperaConverStatID(tokenList.get(0), vsUUI,originationDirection, strYesterda, generaerror, strFinalTime,Archivo, false));
								sumTotalHits = sumTotalHits + listConversationID.size();
							} else {
								System.out.println("BLOQUE DE HORA: " + strStartTime + " A " + strFinalTime);
								voLogger.info("[Horario  ][" + vsUUI + "] ---> BLOQUE DE HORA[ " + strStartTime + " A "
										+ strFinalTime + "]"); 
								listConversationID.addAll(recuperaId.RecuperaConverStatID(tokenList.get(0), vsUUI,originationDirection, strYesterda, strStartTime, strFinalTime,Archivo, false));
								String generaerror = " ";
								listConversationID.addAll(recuperaId.RecuperaConverStatID(tokenList.get(0), vsUUI,originationDirection, strYesterda, generaerror, strFinalTime,Archivo, false));
								sumTotalHits = sumTotalHits + listConversationID.size();
							}
							//Valida si se genero el archivo(paginas no recorrdidas) de error para realizar una segunda vuelta
							
							List<String> listPageRecuperado = new ArrayList<>();
							listPageRecuperado.addAll(GenerateCsvErroPC(tokenList.get(0), vsUUI, Archivo, originationDirection));
							
							/*
							 * for (int r = 0; r < listConversationID.size(); r++) {
							 * System.out.println("Con este horario inicial " + strStartTime +
							 * " y horario terminal " + strFinalTime + " En mi For " + a + " Viene este ID "
							 * + listConversationID.get(r)); }
							 */

							// Genero mis indices dependiendo de mi variables totalThreads
							List<List<String>> listConversationThrea = new ArrayList<List<String>>();
							List<String> Threa = new ArrayList<String>();
							int totalNoClienteID = Integer.parseInt(voMapConf.get("NoClienteID"));
							for (int h = 0; h < totalNoClienteID; h++) {
								listConversationThrea.add(new ArrayList<String>());
							}
							int totalThread = 0;
							totalNoClienteID--;
							// Valido que en se periodo de tiempo tenga datos datos
							if (listConversationID.size() != 0) {
								for (int b = 0; b < listConversationID.size(); b++) {

									if (totalThread < totalNoClienteID) {

										listConversationThrea.get(totalThread).add(listConversationID.get(b));
										totalThread++;

									} else {

										listConversationThrea.get(totalThread).add(listConversationID.get(b));
										totalThread = 0;
									}
								}
								String strTokenAct;
								for (int h = 0; h <= totalNoClienteID; h++) {
									if(tokenList.get(h)=="ERROR" || tokenList.get(h)==null) {
										int s= h-1;
										   strTokenAct=tokenList.get(s);
										
										}else {
											 strTokenAct=tokenList.get(h);
											
										}
									Reporteador voReporte = new Reporteador(vsUUI, strTokenAct, vsUUI,
									listConversationThrea.get(h), Archivo, false);
									voReporte.start();
									voReporte.setName("Hilo" + h);
									Threa.add("Hilo" + h);
									
									try {
										//System.out.println("Se creo el hilo " + h);
										sleep(500);
										//System.out.println("y me espere " );
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}

							}

						}

					} catch (ParseException e1) {
						voLogger.error("[app][" + vsUUI + "] ---> ERROR AL GENERAR LOS ARCHIVOS TXT");
					}
					/*
					 * valido si existen Id's de error trabajo
					 */

					GenerateCsvErroIE(tokenList.get(0), vsUUI, Archivo);

					/*
					 * sleep de prueba
					 */
					try {
						sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// Creamos el csv antes de recorrer los archivos
					// Obteniendo los encabezados
					Map<String, Object> voMapHeadersCSV = new HashMap<String, Object>();
					DataReportGDSmx voDataBBVAmx = new DataReportGDSmx();
					GDSmx voAppBBVAMx = new GDSmx(voMapConf, voDataBBVAmx);
					voMapHeadersCSV = voAppBBVAMx.getHeaderCSV();
					/* Se buscan los archivos que terminen con extension .txt */
					File[] files = FileUtils.getFilesTxt(Archivo);
					List<String[]> content = new ArrayList<String[]>();
					content = FileUtils.getContentForCsv(files, voMapHeadersCSV.size());

					GeneradorCSV generaExcel = new GeneradorCSV();
					boolean resultadoCsv = generaExcel.GeneraReportCSV(Archivo + "\\ReporteFinal_" + strYesterda,
							content, vsUUI, voMapHeadersCSV);
					// Valores para el reporte de correo
					ReporteMail.conversationsIdOK = content.size(); // Va ser sin duplicados
					ReporteMail.pathCsvFinal = Archivo + "\\ReporteFinal_" + strYesterda + ".csv";
					boolean bIE = FileUtils.searchFile(vsUUI + "_conversations_IE.csv", Archivo);
					if(bIE) {
						ReporteMail.pathInteraccionesNoProcesadas = Archivo + "\\" + vsUUI + "_conversations_IE.csv";
					} else {
						ReporteMail.pathInteraccionesNoProcesadas = "No existen interacciones sin procesar";
					}
					boolean bPE = FileUtils.searchFile(vsUUI + "_page_PE.csv", Archivo);
					if(bPE) {
						ReporteMail.pathPagNoProcesadas = Archivo + "\\" + vsUUI + "_page_PE.csv";
						
					} else {
						ReporteMail.pathPagNoProcesadas = "No existen páginas sin procesar";
						ReporteMail.paginasRetornadasErr = 0;
					}
					ReporteMail.numeroHits = sumTotalHits;
					ReporteMail.lineasCsvFinal = content.size();
					System.out.println("resultado de la generacion del archivo: " + resultadoCsv
							+ " Con un tamanio de content: " + content.size());
					
					/* Calculando la duracion del programa */
					

					/*
					 * System.out.println("Valor de ReporteMail ejecucion: " +
					 * ReporteMail.fechaEjecucion + " inicioProceso: " + ReporteMail.inicioProceso +
					 * " finProceso: " + ReporteMail.finProceso + " numeroHits: " +
					 * ReporteMail.numeroHits + " paginasRetornadas: " +
					 * ReporteMail.paginasRetornadas + " conversationsIdOK: " +
					 * ReporteMail.conversationsIdOK + " excepcionesHttp: " +
					 * ReporteMail.excepcionesHttp + " pathCsvFinal: " + ReporteMail.pathCsvFinal +
					 * " pathInteraccionesNoProcesadas: " +
					 * ReporteMail.pathInteraccionesNoProcesadas +
					 * " lineasInteraccionesNoProcesadas: " +
					 * ReporteMail.lineasInteraccionesNoProcesadas + " tipoInteracciones: " +
					 * ReporteMail.tipoInteracciones);
					 */

					  try { 
						  sleep(3000); 
						  } catch (InterruptedException e) { // TODO Auto-generated
					  e.printStackTrace(); }
					 
					FileUtils.deleteTemporals(Archivo + "\\", vsUUI);
					ReporteMail.finProceso = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.format(Calendar.getInstance().getTime());
					ReporteMail.tiempoEjecucion= Utilerias.tiempoEjecucion(ReporteMail.inicioProceso, ReporteMail.finProceso);
					/* Enviando el correo */
					SendingMailTLS sendMail = new SendingMailTLS();
					boolean result =sendMail.sendMailKranon("Reporte de ejecución de GDS del "+strYesterda, vsUUI);
					System.out.println("El directorio de trabajo es: " + Archivo+"\\");
				} else {
					voLogger.error("[Generador][" + vsUUI + "] ---> ERROR : NO SE  CREO LA CARPETA TEMPORAL");
					// Se tendria que terminar el programa aquí con algun return o break
				}
			}

		} // Termina el Else
			// new app();
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

	public static List<String> GeneraToken(Map<String, String> voMapConf, Map<String, String> voMapConfId,
			String vsUUI) {

		List<String> Token = new ArrayList<>();
		String clientsNum = voMapConf.get("NoClienteID");
		int total = Integer.parseInt(clientsNum);
		voLogger.info("[App  ][" + vsUUI + "] ---> GENERANDO " + total + " TOKENS");
		for (int i = 0; i < total; i++) {

			String clientNum = "ClientId" + i;
			String clientSecr = "ClientSecret" + i;
			// Seteamos los valores recuperados
			String idClient = voMapConfId.get(clientNum);
			String clientSecret = voMapConfId.get(clientSecr);

			// Generamos los Tokents
			GenesysCloud voPureCloud = new GenesysCloud();
			String vsToken = voPureCloud.getToken(idClient, clientSecret, vsUUI);
			//System.out.print("Valor de token: "+vsToken);
			Token.add(vsToken);

			if (vsToken.equals("ERROR")) {

				voLogger.error("[App ][" + vsUUI + "] ---> [ClientID] AND [ClientSecret] ARE INCORRECT." + idClient);

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

	public static String yesterdaydate() {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date someDate = new Date();
		Date newDate = new Date(someDate.getTime() + TimeUnit.DAYS.toMillis(-1));
		String dateToStr = dateFormat.format(newDate);
		return dateToStr;

	}

	public static void GenerateCsvErroIE(String vsTokens, String vsUUI, String urlArchivoTem) {

		List<String> listConversationID = new ArrayList<>();

		String strRuta = urlArchivoTem + "\\" + vsUUI + "_conversations_IE_TEMP.csv";
		File dir = new File(strRuta);
		boolean booErrores = true;
		if (dir.exists()) {

			try {
				String cadena;
				FileReader f = new FileReader(dir);
				BufferedReader b = new BufferedReader(f);
				try {

					while ((cadena = b.readLine()) != null) {
						String[] parts = cadena.split(",");
						listConversationID.add(parts[0]);
					}
					f.close();
					dir.delete();
					Reporteador voReporte = new Reporteador(vsUUI, vsTokens, vsUUI, listConversationID, urlArchivoTem,
							booErrores);
					voReporte.start();
					voReporte.setName("Hilonuevo");
					try {
						voReporte.join(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} catch (IOException e) {

					e.printStackTrace();
				}
			} catch (FileNotFoundException ex) {
				java.util.logging.Logger.getLogger(Reporteador.class.getName()).log(Level.SEVERE, null, ex);
			}

		}

	}

	
	public static List<String> GenerateCsvErroPC(String vsTokens, String vsUUI, String urlArchivoTem,String originationDirection) {

		List<String> listPage= new ArrayList<>();
		String vsFecha =  null;
		String strStartTime=  null;
		String strFinalTime=  null;
		boolean ReturnError=true;
		
		String strRuta = urlArchivoTem + "\\" + vsUUI + "_page_PC_TEMP.csv";
		File dir = new File(strRuta);
		boolean booErrores = true;
		if (dir.exists()) {

			try {
				String cadena;
				FileReader f = new FileReader(dir);
				BufferedReader b = new BufferedReader(f);
				try {

					while ((cadena = b.readLine()) != null) {
						String[] parts = cadena.split(",");
						vsFecha= parts[1];
						strStartTime= parts[2];
						strFinalTime= parts[3];
					}
					f.close();
					dir.delete();
					RecuperaConversationID recuperaId = new RecuperaConversationID(vsUUI);
					
					listPage.addAll(recuperaId.RecuperaConverStatID(vsTokens, vsUUI, originationDirection, vsFecha, strStartTime, strFinalTime, urlArchivoTem, ReturnError));
					
				} catch (IOException e) {

					e.printStackTrace();
				}
			} catch (FileNotFoundException ex) {
				java.util.logging.Logger.getLogger(Reporteador.class.getName()).log(Level.SEVERE, null, ex);
			}

		}
		return listPage;
	}
	/*
	 * public static Map<String, Object> obtenerEncabezados() { Map<String, Object>
	 * voMapHeaderCSV = new HashMap<String, Object>(); DataReportGDSmx voDataBBVAmx
	 * = new DataReportGDSmx(); GDSmx voAppBBVAMx = new GDSmx(voMapConf,
	 * voDataBBVAmx); voMapHeaderCSV = voAppBBVAMx.getHeaderCSV(); return
	 * voMapHeaderCSV;
	 * 
	 * }
	 */

}