package com.gs.kranon.reportescustomgds.utilidades;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.log4j.LogManager;

public class FileUtils {
    private static final org.apache.log4j.Logger voLogger = LogManager.getLogger("Reporte");
	/**
	 * Elimina los archivos temporales de una carpeta, eliminando la carpeta al finalizar el proceso
	 * @param directoty Directorio que se eliminará
	 * @param LOGGER Logger
	 */
	public static void deleteTemporals(String directory, String vsUUI) {
		System.out.println("Entrando a eliminar los archivos txt: "+directory);
		File path = new File(directory);
    	File[] ficheros = path.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".txt");
			}
		});
        File f = null;
        if (path.exists()) {
            for (File fichero : ficheros) {
                f = new File(fichero.toString());
                System.out.println("Archivo a eliminar: "+fichero.toString());
                boolean result = f.delete();
                System.out.println("Valor de la eliminacion del archivo: "+result);

            }
            //path.delete();
        } else {
        	 voLogger.error("[FileUtils][" + vsUUI + "] ---> No existe el directorio");
        }
        System.out.println("Archivos eliminados (txt)");
        voLogger.error("[FileUtils][" + vsUUI + "] ---> Archivos temporales borrados.");

	}

	public FileUtils() {
		// TODO Auto-generated constructor stub
	}

}
