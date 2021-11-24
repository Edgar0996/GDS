/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gs.kranon.reportescustomgds;

/**
 *
 * @author VFG
 */
public class app {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Ejecutando el proyecto desde consola sin parametros");
        for(int i=0; i<args.length; i++){
            System.out.println("Valor del argumento recibido: "+args[i]);
        }
    }

}
