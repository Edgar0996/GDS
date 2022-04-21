package com.gs.kranon.reportescustomgds;

import static java.lang.System.exit;
import static java.lang.Thread.sleep;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.gs.kranon.reportescustomgds.cuadroMando.ReporteMail;
import com.gs.kranon.reportescustomgds.genesysCloud.GenesysCloud;
import com.gs.kranon.reportescustomgds.genesysCloud.RecuperaConversationID;
import com.gs.kranon.reportescustomgds.mail.SendingMailTLS;
import com.gs.kranon.reportescustomgds.mail.SendingMailTLSFiles;
import com.gs.kranon.reportescustomgds.reporteador.GeneradorCSV;
import com.gs.kranon.reportescustomgds.reporteador.Reporteador;
import com.gs.kranon.reportescustomgds.utilidades.FileUtils;
import com.gs.kranon.reportescustomgds.utilidades.Utilerias;

public class EjecucionPrincipal implements Job {
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
	private static String strTokenAct;
	String strYesterda = "";

	public EjecucionPrincipal() {
		ejecutar();
		System.exit(0);
	}

	public EjecucionPrincipal(String string,String data) {
		
		System.out.println("[" + new SimpleDateFormat("dd-mm-yyyy HH:mm:ss").format(Calendar.getInstance().getTime())
				+ "]--> Buscando configuraciones en " + string);
		strYesterda = data;
		ejecutar();
		System.exit(0);
		
	}
	public EjecucionPrincipal(String string) {
		
		System.out.println("[" + new SimpleDateFormat("dd-mm-yyyy HH:mm:ss").format(Calendar.getInstance().getTime())
				+ "]--> Buscando configuraciones en " + string);
		ejecutar();
		System.exit(0);
		
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("[" + new SimpleDateFormat("dd-mm-yyyy HH:mm:ss").format(Calendar.getInstance().getTime())
				+ "]--> INICIANDO TAREA PROGRAMADA: " + new Date());
		ejecutar();
		/*
		 * if(args.length > 0) { Asigno la fecha recibida por paramtreo strYesterda =
		 * args[0]; } else { Recupero la fecha de ayer ("yyyy-MM-dd")
		 * strYesterda="2021-12-30"; //strYesterda = yesterdaydate(); }
		 */


	}// Termina execute
	public void ejecutar() {
/* Recupero la fecha de ayer ("yyyy-MM-dd") */
//strYesterda = "2021-12-30";
if(strYesterda=="") {
	strYesterda = yesterdaydate();
	//strYesterda="2022-04-07";
}
// Inicio de la ejecucion del proceso
ReporteMail.inicioProceso = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
		.format(Calendar.getInstance().getTime());

/* Genero mi cadena UUI */
String vsUUI = GeneraCadenaUUI("1234567890");

/* Invoco mi archivos de configuración */
Map<String, String> voMapConf = RecuperaArhivoConf(vsUUI);



if (voMapConf.size() <= 0) {
	voLogger.error("[app][" + vsUUI + "] ---> NO SE ENCONTRO EL ARCHIVO DE CONFIGURACIÓN O ESTA VACIO");
	//exit(0);
} else {

	int intTimeFrame = Integer.parseInt(voMapConf.get("TimeFrame"));

	String originationDirection = voMapConf.get("OriginationDirection");
	String pathArchivo = ReporteMail.pathConfig+ File.separator;
	ReporteMail.noHilosUsados= Integer.parseInt(voMapConf.get("NoClienteID"));
	Map<String, String> voMapConfId = RecuperaArhivoConfID();

	if (voMapConf.size() <= 0) {
		voLogger.error("[app][" + vsUUI + "] ---> NO SE ENCONTRO EL ARCHIVO DE CONFIGURACIÓN O ESTA VACIO");
	} else {

		/* Genero la carpeta temporal */
		String timeStamp = new SimpleDateFormat("yyyy_MM_dd HH.mm.ss").format(Calendar.getInstance().getTime());
		// Asigno la fecha de ejecucion al datamail
		ReporteMail.fechaEjecucion = new SimpleDateFormat("yyyy-MM-dd")
				.format(Calendar.getInstance().getTime());

		String Archivo = pathArchivo + "temp"+File.separator+"Reporte_" + timeStamp;
		boolean Ruta = createTempDirectory(Archivo);
		if (Ruta == true) {
			voLogger.info("[App  ][" + vsUUI + "] ---> *************INICIO DE LA APLICACIÓN*************** ");
			voLogger.info("[App  ][" + vsUUI + "] ---> FECHA DE LA QUE SE GENERARÁ EL REPORTE: " + strYesterda);
			/* Genero los token's */
			List<String> tokenList = GeneraToken(voMapConf, voMapConfId, vsUUI);
		//System.out.println(tokenList);
			int sumTotalHits = 0;
			DataReports voData = new DataReports();
			// voData.setFechaInicio("2022-04-02");
			// voData.setFechaFin(new
			// SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
			voData.setVsOrigination(originationDirection);

			try {

				// Segmento las 24 horas del día dependiendo el archivo de configuración
				String strFinalTime =  "00:00:00";
				ReporteMail.intervaloTiempo = String.valueOf(1440 / intTimeFrame);
				ReporteMail.duracionIntervalo = String.valueOf(intTimeFrame);
				ReporteMail.tipoInteracciones = originationDirection;
				
				//Pruebas para cambiar la zona horaria 
				String strFinalTimePrueba= strYesterda+"T00:00:00";
				SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				f.setTimeZone(TimeZone.getTimeZone("UTC"));
				SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				Date dataFormateada = formato.parse(strFinalTimePrueba); 
			
				for (int a = 0; a < 1440; a = a + intTimeFrame) {
					//Horario formateado de Genesys
					String strStartTimeprueba = strFinalTimePrueba;
					Date datexx = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(strFinalTimePrueba);
					Calendar calendarw = Calendar.getInstance();
					calendarw.setTime(datexx);
					calendarw.add(calendarw.MINUTE, intTimeFrame);
					strFinalTimePrueba = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(calendarw.getTime());
					Date  dataInicial = formato.parse(strStartTimeprueba);
					String dateFormatoInicial =	f.format(dataInicial);
					Date  dataFinal = formato.parse(strFinalTimePrueba);
					String dateFormatoFinal =f.format(dataFinal);
				   
					List<String> listConversationID = new ArrayList<>();
					RecuperaConversationID recuperaId = new RecuperaConversationID(vsUUI);
					
						System.out.println("["+new SimpleDateFormat("dd-mm-yyyy HH:mm:ss").format(Calendar.getInstance().getTime())+"]--> BLOQUE DE HORA: " + dateFormatoInicial + " A " + dateFormatoFinal);
						voLogger.info("[Horario  ][" + vsUUI + "] ---> BLOQUE DE HORA[ " + dateFormatoInicial + " A "	+ dateFormatoFinal + "]"); 
						listConversationID.addAll(recuperaId.RecuperaConverStatID(tokenList.get(0), vsUUI,originationDirection, strYesterda,dateFormatoInicial,dateFormatoFinal,Archivo,false));
					//Valida si se genero el archivo(paginas no recorrdidas) de error para realizar una segunda vuelta
					sumTotalHits =sumTotalHits + listConversationID.size();
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
					int inttotalNoClienteID;
					int totalNoClienteID = Integer.parseInt(voMapConf.get("NoClienteID"));
					
					for (int h = 0; h < totalNoClienteID; h++) {
						listConversationThrea.add(new ArrayList<String>());
					}
					int totalThread = 0;
					totalNoClienteID--;
					// Valido que el periodo de tiempo tenga datos datos
					
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
						
						System.out.println("["+new SimpleDateFormat("dd-mm-yyyy HH:mm:ss").format(Calendar.getInstance().getTime())+"]-->   Número de hits recuperados: "+listConversationID.size());
						if(listConversationID.size()<=totalNoClienteID) {
							inttotalNoClienteID = listConversationID.size();
						}else {
							inttotalNoClienteID=totalNoClienteID;
						}
						for (int h = 0; h <= inttotalNoClienteID; h++) {
							
							if(tokenList.get(h)=="ERROR" || tokenList.get(h)==null) {
								int s= h-1;
								   strTokenAct=tokenList.get(s);
								
								}else {
									 strTokenAct=tokenList.get(h);
							}
							ExecutorService executor = Executors.newFixedThreadPool(20);
							Future<?> task1= executor.submit(new Reporteador(vsUUI, strTokenAct, vsUUI,listConversationThrea.get(h), Archivo, false,"Default"));
							try { 
								  sleep(200); 
								  } catch (InterruptedException e) { // TODO Auto-generated
							  e.printStackTrace(); 
							  }
						
							if(h>=inttotalNoClienteID) {
								while(!task1.isDone()) {	
								}
								
							}
							List<Runnable> runnableList = executor.shutdownNow();
						}
												
					} else {
						System.out.println("["+new SimpleDateFormat("dd-mm-yyyy HH:mm:ss").format(Calendar.getInstance().getTime())+"]-->   Número de hits recuperados: "+listConversationID.size());
					}
				
				}

			} catch (ParseException e1) {
				voLogger.error("[app][" + vsUUI + "] ---> ERROR AL GENERAR LOS ARCHIVOS TXT");
			}
			/*
			 * valido si existen Id's de error trabajo
			 */
			GenerateCsvErroIE(tokenList.get(0), vsUUI, Archivo);
			GeneraCSVDeError(Archivo,vsUUI);
			/*
			 * sleep de prueba
			 */
			try { 
				  sleep(10000); 
				  } catch (InterruptedException e) { // TODO Auto-generated
			  e.printStackTrace(); }
			// Creamos el csv antes de recorrer los archivos
			// Obteniendo los encabezados
			Map<String, Object> voMapHeadersCSV = new HashMap<String, Object>();
			DataReportGDSmx voDataBBVAmx = new DataReportGDSmx();
			GDSmx voAppBBVAMx = new GDSmx(voMapConf, voDataBBVAmx);
			voMapHeadersCSV = voAppBBVAMx.getHeaderCSV();
			/* Se buscan los archivos que terminen con extension .txt */
			File[] files = FileUtils.getFilesTxt(Archivo);
			List<String[]> content = new ArrayList<String[]>();
			content = FileUtils.getContentForCsv(files, voMapHeadersCSV.size(),strTokenAct,Archivo,vsUUI);
			
			GeneradorCSV generaExcel = new GeneradorCSV();
			boolean resultadoCsv = generaExcel.GeneraReportCSV(Archivo + File.separator +"ReporteFinal_" + strYesterda,
					content, vsUUI, voMapHeadersCSV);
			// Valores para el reporte de correo
			ReporteMail.conversationsIdOK = content.size(); // Va ser sin duplicados
			ReporteMail.pathCsvFinal = Archivo + File.separator+"ReporteFinal_" + strYesterda + ".csv";
			ReporteMail.nameCsvFinal = "ReporteFinal_" + strYesterda + ".csv";
			boolean bIE = FileUtils.searchFile(vsUUI + "_conversations_IE.csv", Archivo);
			if(bIE) {
				ReporteMail.pathInteraccionesNoProcesadas = Archivo + File.separator + vsUUI + "_conversations_IE.csv";
			} else {
				ReporteMail.pathInteraccionesNoProcesadas = "No existen interacciones sin procesar";
			}
			boolean bPE = FileUtils.searchFile(vsUUI + "_page_PE.csv", Archivo);
			if(bPE) {
				ReporteMail.pathPagNoProcesadas = Archivo + File.separator + vsUUI + "_page_PE.csv";
				
			} else {
				ReporteMail.pathPagNoProcesadas = "No existen p&aacute;ginas sin procesar";
				ReporteMail.paginasRetornadasErr = 0;
			}
			if(ReporteMail.lineasConColumnasDif > 0) {
				ReporteMail.pathLogColumnasDif = "C:/Appl/GS/ReportesCustom/Logs/"+strYesterda+"_Reporte.log";
			}else {
				ReporteMail.pathLogColumnasDif = "No existen registros con errores de columnas";
			}
			ReporteMail.numeroHits = sumTotalHits;
			ReporteMail.lineasCsvFinal = content.size();
			//System.out.println("["+new SimpleDateFormat("dd-mm-yyyy HH:mm:ss").format(Calendar.getInstance().getTime())+"]--> Resultado de la generacion del archivo: " + resultadoCsv
				//	+ " Con un tamaño de: " + content.size());
			  try { 
				  sleep(3000); 
				  } catch (InterruptedException e) { // TODO Auto-generated
			  e.printStackTrace(); }
			 
			FileUtils.deleteTemporals(Archivo + File.separator, vsUUI);
			ReporteMail.finProceso = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(Calendar.getInstance().getTime());
			ReporteMail.tiempoEjecucion= Utilerias.tiempoEjecucion(ReporteMail.inicioProceso, ReporteMail.finProceso);
			/* Enviando el correo de reporte a Kranon*/
			SendingMailTLS sendMail = new SendingMailTLS();
			boolean result =sendMail.sendMailKranon("Reporte de ejecución de GDS del "+strYesterda, vsUUI);
			/* Enviando el correo de reporte con archivo adjunto*/
			String strFechaAct=  new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
			ReporteMail.strYesterda=strFechaAct;
			SendingMailTLSFiles sendMailFiles = new SendingMailTLSFiles();
			boolean resultSendFile =sendMailFiles.sendMailKranonFiles("Reporte  "+strFechaAct, vsUUI, ReporteMail.pathCsvFinal, strYesterda);
	
			System.out.println("["+new SimpleDateFormat("dd-mm-yyyy HH:mm:ss").format(Calendar.getInstance().getTime())+"]--> El directorio de trabajo es: " + Archivo+File.separator);
			System.out.println("["+new SimpleDateFormat("dd-mm-yyyy HH:mm:ss").format(Calendar.getInstance().getTime())+"]--> Archivo de Interacciones NO Procesadas: " +Archivo + File.separator + vsUUI + "_conversations_IE.csv");
			System.out.println("["+new SimpleDateFormat("dd-mm-yyyy HH:mm:ss").format(Calendar.getInstance().getTime())+"]--> Archivo de Páginas NO Procesadas: " +Archivo + File.separator + vsUUI + "_page_PE.csv");
			System.out.println("["+new SimpleDateFormat("dd-mm-yyyy HH:mm:ss").format(Calendar.getInstance().getTime())+"]--> Archivo Final CSV: " +ReporteMail.pathCsvFinal);
			try { 
				  sleep(3000); 
				  } catch (InterruptedException e) { // TODO Auto-generated
			  e.printStackTrace(); }
			
		} else {
			voLogger.error("[EjecuciónPrincipal][" + vsUUI + "] ---> ERROR : NO SE  CREO LA CARPETA TEMPORAL");
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

		String strRuta = urlArchivoTem + File.separator + vsUUI + "_conversations_IE_TEMP.csv";
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
							booErrores,"Default");
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
		//System.out.println("Entro aquí mi archivo lo genera de nueva cuenta ");
		List<String> listPage= new ArrayList<>();
		String vsFecha =  null;
		String strStartTime=  null;
		String strFinalTime=  null;
		boolean ReturnError=true;
		
		String strRuta = urlArchivoTem + File.separator + vsUUI + "_page_PC_TEMP.csv";
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
					listPage.addAll(recuperaId.RecuperaConverStatID(vsTokens, vsUUI, originationDirection, vsFecha,strStartTime,strFinalTime,urlArchivoTem,ReturnError));
					
				} catch (IOException e) {

					e.printStackTrace();
				}
			} catch (FileNotFoundException ex) {
				java.util.logging.Logger.getLogger(Reporteador.class.getName()).log(Level.SEVERE, null, ex);
			}

		}
		return listPage;
	}
	
	
	public boolean GeneraCSVDeError(String urlArchivoTemp,String vsUUi) {
    		String strUrlFinal = urlArchivoTemp+ File.separator + "page_PE.csv";
    		File  fw = new File (strUrlFinal);
    		//Validamos si el archivo existe
    		if(fw.exists()){
    			
            }else{
                //Archivo NO existe, lo crea.
			try (PrintWriter writer = new PrintWriter(new File(strUrlFinal))) {
				
				StringBuilder linea = new StringBuilder();
				linea.append("Codigo Respuesta");
				linea.append(',');
				linea.append("Fecha ");
				linea.append(',');
				linea.append("StartTime ");
				linea.append(',');
				linea.append("FinalTime ");
				linea.append(',');
				linea.append("Page ");
				linea.append('\n');
				writer.write(linea.toString());
	            writer.close();
	            writer.write(linea.toString());
    	} catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
            }
    		
    		String strUrlFinalID = urlArchivoTemp+ File.separator + "conversations_IE.csv";
    		File  fwID = new File (strUrlFinalID);
    		if(fwID.exists()){
    			
            }else{

                //Archivo NO existe, lo crea.
			try (PrintWriter writer = new PrintWriter(new File(strUrlFinalID))) {
				
				StringBuilder linea = new StringBuilder();
				linea.append("ConversationID");
				linea.append(',');
				linea.append("Respuesta de error");
				linea.append('\n');
				writer.write(linea.toString());
	            writer.close();
	            writer.write(linea.toString());
				
    	} catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
            	
            }
    		
        return true;
    } 
}
