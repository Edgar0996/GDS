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
	public static String fechaEjecucion = ""; //OK
	//Intervalos de Tiempo
	public static String intervaloTiempo=""; // OK
	//Duración de Intervalo
	public static String duracionIntervalo=""; //OK
	//Tipo de Interacciones Consultadas
	public static String tipoInteracciones=""; //OK
	//Fecha-Hora Inicio del Proceso
	public static String inicioProceso=""; //OK
	//Fecha-Hora Fin del Proceso
	public static String finProceso=""; //OK
	//Tiempo de Ejecución
	public static String tiempoEjecucion=""; //Falta calcular
	//Número de Hits
	public static int numeroHits=0; //OK
	//Número de Páginas Retornadas por el Analytics, este varia por hora
	public static int paginasRetornadas=0;//OK
	//Número de Páginas Retornadas con Error
	public static int paginasRetornadasErr=0; //Pendiente
	//Número de ConversationIDs Obtenidos Exitosamente
	public static int conversationsIdOK=0; //OK, sera el mismo valor de lineas del csv sin duplicados
	//Número de Excepciones (300, 400, 500)
	public static int excepcionesHttp=0;//OK
	//Número de Excepciones TimeOut
	public static int excepcionesTimeout=0;//Pendiente
	//Número de Excepciones Generales
	public static int excepcionesGrales=0; //Pendiente
	//Path del archivo final CSV
	public static String pathCsvFinal=""; //OK
	//Path del archivo de paginas no procesadas
	public static String pathPagNoProcesadas="";//Pendiente
	//Path del archivo de interacciones no procesadas
	public static String pathInteraccionesNoProcesadas="";//OK
	//No de lineas de archivo final CSV
	public static int lineasCsvFinal=0; //Pendiente Va a ser el content.size
	//No de lineas de paginas no procesadas
	public static int lineasPagNoProcesadas=0;//Pendiente
	//No de lineas de Interacciones NO Procesadas
	public static int lineasInteraccionesNoProcesadas=0; //OK
	//No de lineas de Interacciones que no cumplen con el numero de columnas requeridas
	public static int lineasConColumnasDif=0; //OK

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
