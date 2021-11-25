/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gs.kranon.reportescustomgds;

import com.gs.kranon.reportescustomgds.genesysCloud.GenesysCloud;


/**
 *
 * @author VFG
 */
public class app {
    /**
     * @param args the command line arguments
     */
    private GenesysCloud voPureCloud = null;
    
    public static void main(String[] args) {
        System.out.println("Ejecutando el proyecto desde consola sin parametros");
        for(int i=0; i<args.length; i++){
            System.out.println("Valor del argumento recibido: "+args[i]);
        }
        GenesysCloud genesysCloud = new GenesysCloud();
        
        
        
    }

}
