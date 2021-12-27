package com.gs.kranon.reportescustomgds.utilidades;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.apache.log4j.LogManager;

import com.gs.kranon.reportescustomgds.cuadroMando.ReporteMail;
import com.gs.kranon.reportescustomgds.reporteador.Reporteador;

public class FileUtils {
	private static final org.apache.log4j.Logger voLogger = LogManager.getLogger("Reporte");

	/**
	 * Elimina los archivos temporales de una carpeta, eliminando la carpeta al
	 * finalizar el proceso
	 * 
	 * @param directory Directorio que se eliminará
	 * @param vsUUI     vsUUI
	 */
	public static void deleteTemporals(String directory, String vsUUI) {
		// System.out.println("Entrando a eliminar los archivos txt: " + directory);
		File path = new File(directory);
		File[] ficheros = path.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".txt");
			}
		});
		File f = null;
		if (path.exists()) {
			for (File fichero : ficheros) {

				try {

					f = new File(fichero.toString());
					FileReader dir = new FileReader(f);
					dir.close();
					f.delete();
					// System.out.println("El archivo " + result);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}

			}
			// path.delete();
		} else {
			voLogger.error("[FileUtils][" + vsUUI + "] ---> No existe el directorio");
		}
		System.out.println("Archivos eliminados (txt)");
		voLogger.error("[FileUtils][" + vsUUI + "] ---> Archivos temporales borrados.");

	}

	/**
	 * Obtiene los archivos temporales de una carpeta que tienen como extension
	 * .txt. *
	 * 
	 * @param directoty Directorio desde el cual se recuperan los txt
	 */
	public static File[] getFilesTxt(String directory) {
		File dir = new File(directory);
		/* Se buscan los archivos que terminen con extension .txt */
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".txt");
			}
		});
		System.out.println("Total de archivos recuperados: " + files.length);
		return files;

	}

	/**
	 * Obtiene el contenido de todos los archivos temporales que sera usado para
	 * rellenar el csv
	 * 
	 * @param files Listado de archivos a procesar
	 */
	public static List<String[]> getContentForCsv(File[] files, int numColumnas) {
		//Variable para almacenar las lineas obtenidas
		Set<String> arrSD = new HashSet<String>();
		
		/* Recorremos el listado de los archivos recuperados */
		if (files.length != 0) {
			List<String[]> content = new ArrayList<String[]>();
			for (int x = 0; x < files.length; x++) {
				File file = files[x];
				// System.out.println("Archivo recuperado: " + file);
				// Leemos el txt recibido por parametro
				FileReader fileReaderConversations = null;
				String lineContent = "";
				try {
					// sleep(3000);
					fileReaderConversations = new FileReader(file);
					BufferedReader buffer = new BufferedReader(fileReaderConversations);
					while ((lineContent = buffer.readLine()) != null) {
						arrSD.add(lineContent);
						//String[] lineElements = lineContent.split(",");
						//content.add(lineElements);
					}
					buffer.close();
					// file.delete();
				} catch (IOException ex) {
					java.util.logging.Logger.getLogger(Reporteador.class.getName()).log(Level.SEVERE, null, ex);
				} finally {
					try {
						if (null != fileReaderConversations) {
							fileReaderConversations.close();
						}
					} catch (IOException ex) {
						java.util.logging.Logger.getLogger(Reporteador.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
			/* Recorremos el arrSD para separar por comas y regresar los content */
			for(String s : arrSD){
				String[] lineElements = s.split(",");
				System.out.println("Numero de columnas obtenidas: "+lineElements.length);
				if(lineElements.length == numColumnas) {
					content.add(lineElements);
				} else {
					ReporteMail.lineasConColumnasDif = ReporteMail.lineasConColumnasDif + 1; 
					voLogger.error("[FileUtils]---> Línea que no cumple con el número de columnas: "+ s);
				}
				
				
	        }
			return content;
		} else {
			System.out.println("El directorio no contiene extensiones de tipo '.txt'");
			return null;
		}

	}
	/**
	 * Permite buscar si existe un archivo en un directorio, si el archivo existe retorna true 
	 * @param archivoABuscar archivo a buscar
	 * @param directorio donde se buscara el archivo
	 */
	public static boolean searchFile(String archivoABuscar, String directorio) {
		File dir = new File(directorio);
	    File[] archivos = dir.listFiles();
	    for (File archivo : archivos) {
	        if (archivo.getName().equals(archivoABuscar)) {
	            return true;
	        }

	    }
	    return false;
	}

	public FileUtils() {
		// TODO Auto-generated constructor stub
	}

}