/**
 * Objeto para guardar las estadisticas de ejecucion que se enviaran por correo electronico
 */
package com.gs.kranon.reportescustomgds.cuadroMando;

/**
 * @author Victor Francisco Garcia
 *
 */
public class ReporteMail {
	//Fecha de Ejecución del Reporte
	public static String fechaEjecucion = "";
	//Intervalos de Tiempo
	public static String intervaloTiempo="";
	//Duración de Intervalo
	public static String duracionIntervalo="";
	//Tipo de Interacciones Consultadas
	public static String tipoInteracciones="";
	//Fecha-Hora Inicio del Proceso
	public static String inicioProceso="";
	//Fecha-Hora Fin del Proceso
	public static String finProceso="";
	//Tiempo de Ejecución
	public static String tiempoEjecucion="";
	//Número de Hits
	public static int numeroHits=0;
	//Número de Páginas Retornadas por el Analytics
	public static int paginasRetornadas=0;
	//Número de Páginas Retornadas con Error
	public static int paginasRetornadasErr=0;
	//Número de ConversationIDs Obtenidos Exitosamente
	public static int conversationsIdOK=0;
	//Número de Excepciones (300, 400, 500)
	public static int excepcionesHttp=0;
	//Número de Excepciones TimeOut
	public static int excepcionesTimeout=0;
	//Número de Excepciones Generales
	public static int excepcionesGrales=0;
	//Path del archivo final CSV
	public static String pathCsvFinal="";
	//Path del archivo de paginas no procesadas
	public static String pathPagNoProcesadas="";
	//Path del archivo de interacciones no procesadas
	public static String pathInteraccionesNoProcesadas="";
	//No de lineas de archivo final CSV
	public static int lineasCsvFinal=0;
	//No de lineas de paginas no procesadas
	public static int lineasPagNoProcesadas=0;
	//No de lineas de Interacciones NO Procesadas
	public static int lineasInteraccionesNoProcesadas=0; 

	public ReporteMail() {

	}

	@Override
	public String toString() {
		return "ReporteMail [fechaEjecucion=" + fechaEjecucion + ", intervaloTiempo=" + intervaloTiempo
				+ ", duracionIntervalo=" + duracionIntervalo + ", tipoInteracciones=" + tipoInteracciones
				+ ", inicioProceso=" + inicioProceso + ", finProceso=" + finProceso + ", tiempoEjecucion="
				+ tiempoEjecucion + ", numeroHits=" + numeroHits + ", paginasRetornadas=" + paginasRetornadas
				+ ", paginasRetornadasErr=" + paginasRetornadasErr + ", conversationsIdOK=" + conversationsIdOK
				+ ", excepcionesHttp=" + excepcionesHttp + ", excepcionesTimeout=" + excepcionesTimeout
				+ ", excepcionesGrales=" + excepcionesGrales + ", pathCsvFinal=" + pathCsvFinal
				+ ", pathPagNoProcesadas=" + pathPagNoProcesadas + ", pathInteraccionesNoProcesadas="
				+ pathInteraccionesNoProcesadas + ", lineasCsvFinal=" + lineasCsvFinal + ", lineasPagNoProcesadas="
				+ lineasPagNoProcesadas + ", lineasInteraccionesNoProcesadas=" + lineasInteraccionesNoProcesadas + "]";
	}
	
}
