/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gs.kranon.reportescustomgds;

import com.gs.kranon.reportescustomgds.genesysCloud.GenesysCloud;
import com.gs.kranon.reportescustomgds.genesysCloud.RecuperaConversationID;
import com.gs.kranon.reportescustomgds.utilidades.Utilerias;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.gs.kranon.reportescustomgds.conexionHttp.ConexionResponse;
import com.gs.kranon.reportescustomgds.cuadroMando.ReporteMail;
import com.gs.kranon.reportescustomgds.conexionHttp.ConexionHttp;
import com.gs.kranon.reportescustomgds.mail.SendingMailTLS;
import com.gs.kranon.reportescustomgds.reporteador.GeneradorCSV;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JOptionPane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.gs.kranon.reportescustomgds.reporteador.Reporteador;
import java.io.BufferedReader;
import static java.lang.System.exit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import static java.lang.Thread.sleep;
import java.util.Properties;
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
	private List<String> listConversationID = new ArrayList<>();
	/* Variables de prueba */
	private DataReports voData = null;
	/* Variables para mandar a llamar mi reporte */
	private Reporteador voReporte;

	private RecuperaConversationID RecuperaId;

	public static void main(String[] args) {
		
		/* Genero mi cadena UUI */
		String vsUUI = GeneraCadenaUUI("1234567890");

		/* Invoco mi archivos de configuración */
		Map<String, String> voMapConf = RecuperaArhivoConf(vsUUI);

		/* Recupero la fecha de ayer */
		String strYesterda = yesterdaydate();
		
		

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
				ReporteMail.inicioProceso = timeStamp;
				ReporteMail.fechaEjecucion = new SimpleDateFormat("yyyy_MM_dd")
						.format(Calendar.getInstance().getTime());

				String Archivo = pathArchivo + "temp\\Reporte_" + timeStamp;
				boolean Ruta = createTempDirectory(Archivo);
				if (Ruta == true) {
					/* Genero los token's */
					List<String> tokenList = GeneraToken(voMapConf, voMapConfId, vsUUI);
					
					int sumTotalHits = 0;
					DataReports voData = new DataReports();
					voData.setFechaInicio("2021-01-01");
					voData.setFechaFin(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
					voData.setVsOrigination(originationDirection);

					try {

						// Segmento las 24 horas del día dependiendo el archivo de configuración
						String strFinalTime = "00:00:00";
						int bb = 1;
						for (int a = 0; a < 1440; a = a + intTimeFrame) {
							//System.out.println("Se repite: " + bb);
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
							RecuperaConversationID recuperaId = new RecuperaConversationID(voData, vsUUI);
							if (strFinalTime.equals("00:00:00")) {
								strFinalTime = "23:59:59";
								voLogger.info("[Horario  ][" + vsUUI + "] --->  BLOQUE DE HORA[ "+strStartTime+ " A "+ strFinalTime+"]");
								listConversationID.addAll(recuperaId.RecuperaConverStatID(tokenList.get(0), vsUUI,originationDirection, strYesterda, strStartTime, strFinalTime));
								sumTotalHits =sumTotalHits + listConversationID.size();
							} else {
								voLogger.info("[Horario  ][" + vsUUI + "] ---> BLOQUE DE HORA["+strStartTime+ "A"+ strFinalTime+"]");
								listConversationID.addAll(recuperaId.RecuperaConverStatID(tokenList.get(0), vsUUI,originationDirection, strYesterda, strStartTime, strFinalTime));
								sumTotalHits =sumTotalHits + listConversationID.size();
							}
							
							  for (int r = 0; r < listConversationID.size(); r++) {
							  System.out.println("Con este horario inicial " + strStartTime +
							  " y horario terminal " + strFinalTime + " En mi For " + a + " Viene este ID "
							  + listConversationID.get(r)); }
							 

							// Genero mis indices dependiendo de mi variables totalThreads
							List<List<String>> listConversationThrea = new ArrayList<List<String>>();
							int totalNoClienteID = Integer.parseInt(voMapConf.get("NoClienteID"));
							for (int h = 0; h < totalNoClienteID; h++) {
								listConversationThrea.add(new ArrayList<String>());
							}
							int totalThread = 0;
							totalNoClienteID--;
							// Valido que en se periodo de tiempo tenga datos datos
							if(listConversationID.size() != 0) {
							for (int b = 0; b < listConversationID.size(); b++) {

								if (totalThread < totalNoClienteID) {

									listConversationThrea.get(totalThread).add(listConversationID.get(b));
									totalThread++;

								} else {

									listConversationThrea.get(totalThread).add(listConversationID.get(b));
									totalThread = 0;
								}
							}
							for (int h = 0; h <= totalNoClienteID; h++) {

								Reporteador voReporte = new Reporteador(vsUUI, tokenList.get(h), vsUUI,
										listConversationThrea.get(h), Archivo, false);
								voReporte.start();
								voReporte.setName("Hilo" + h);

								if (h == totalNoClienteID) {
									voReporte.setPriority(1);
									try {
										voReporte.join();

									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}

							}

							bb++;
							}
							try {
								sleep(5000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					} catch (ParseException e1) {
						voLogger.error("[app][" + vsUUI + "] ---> ERROR AL GENERAR LOS ARCHIVOS TXT");
					}
					double hits= sumTotalHits;
					double division= (hits / 100.0);
					ReporteMail.numeroHits = sumTotalHits;
					ReporteMail.paginasRetornadas = (int) Math.ceil(division);
					System.out.println("Valor calculado de pagina: "+ReporteMail.paginasRetornadas);
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

					/*
					 * Se empieza a generar el CSV con los archivos que existen en el directorio de
					 * trabajo
					 */

					File dir = new File(Archivo);
					/* Se buscan los archivos que terminen con extension .txt */
					File[] files = dir.listFiles(new FilenameFilter() {
						public boolean accept(File dir, String name) {
							return name.toLowerCase().endsWith(".txt");
						}
					});
					/* Recorremos el listado de los archivos recuperados */
					if (files.length != 0) {
						// Creamos el csv antes de recorrer los archivos
						// Obteniendo los encabezados
						Map<String, Object> voMapHeadersCSV = new HashMap<String, Object>();
						DataReportGDSmx voDataBBVAmx = new DataReportGDSmx();
						GDSmx voAppBBVAMx = new GDSmx(voMapConf, voDataBBVAmx);
						voMapHeadersCSV = voAppBBVAMx.getHeaderCSV();

						List content = new ArrayList();
						GeneradorCSV generaExcel = new GeneradorCSV();
						for (int x = 0; x < files.length; x++) {
							File file = files[x];
							System.out.println("Archivo recuperado: " + file);
							// Leemos el txt recibido por parametro
							FileReader fileReaderConversations = null;
							String lineContent = "";

							try {
								// sleep(3000);
								fileReaderConversations = new FileReader(file);
								BufferedReader buffer = new BufferedReader(fileReaderConversations);
								while ((lineContent = buffer.readLine()) != null) {
									String[] lineElements = lineContent.split(",");
									content.add(lineElements);
									System.out.println("content recuperado hasta el archivo: " + content.size()+" con el file"+file.getName());
									
								}
								buffer.close();
							} catch (FileNotFoundException ex) {
								java.util.logging.Logger.getLogger(Reporteador.class.getName()).log(Level.SEVERE, null,
										ex);
							} catch (IOException ex) {
								java.util.logging.Logger.getLogger(Reporteador.class.getName()).log(Level.SEVERE, null,
										ex);
								/*
								
								 */
							} finally {
								try {
									if (null != fileReaderConversations) {
										fileReaderConversations.close();
									}
								} catch (IOException ex) {
									java.util.logging.Logger.getLogger(Reporteador.class.getName()).log(Level.SEVERE,
											null, ex);
								}
							}
							// Enviamos el contenido del archivo a la funcion
							String path = Archivo + "\\ReporteFinal";
							//System.out.println("El directorio a enviar es: " + path);

						}
						boolean resultadoCsv = generaExcel.GeneraReportCSV(Archivo + "\\ReporteFinal", content, vsUUI,
								voMapHeadersCSV);
						System.out.println("resultado de la generacion del archivo: " + resultadoCsv
								+ " Con un tamanio de content: " + content.size());
						ReporteMail.finProceso = new SimpleDateFormat("yyyy_MM_dd HH.mm.ss")
								.format(Calendar.getInstance().getTime());
						System.out.println("Valor de ReporteMail ejecucion: " + ReporteMail.fechaEjecucion
								+ " inicioProceso: " + ReporteMail.inicioProceso + " finProceso: "
								+ ReporteMail.finProceso + " numeroHits: " + ReporteMail.numeroHits+" paginasRetornadas: "+ReporteMail.paginasRetornadas);
					} else {
						System.out.println("El directorio no contiene extensiones de tipo '.txt'");
					}
				}
				voLogger.error("[Generador][" + vsUUI + "] ---> ERROR : NO SE  CREO LA CARPETA TEMPORAL");
				// Se tendria que terminar el programa aquí con algun return o break
			}

		}
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

		for (int i = 0; i < total; i++) {

			String clientNum = "ClientId" + i;
			String clientSecr = "ClientSecret" + i;
			// Seteamos los valores recuperados
			String idClient = voMapConfId.get(clientNum);
			String clientSecret = voMapConfId.get(clientSecr);

			// Generamos los Tokents
			GenesysCloud voPureCloud = new GenesysCloud();
			String vsToken = voPureCloud.getToken(idClient, clientSecret, vsUUI);

			Token.add(vsToken);

			if (vsToken.equals("ERROR")) {

				voLogger.error(
						"[Reporteador][" + vsUUI + "] ---> [ClientID] AND [ClientSecret] ARE INCORRECT." + idClient);

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
