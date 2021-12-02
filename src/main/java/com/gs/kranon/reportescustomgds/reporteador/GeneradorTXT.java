/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gs.kranon.reportescustomgds.reporteador;

import com.gs.kranon.reportescustomgds.DataReports;
import com.gs.kranon.reportescustomgds.genesysCloud.GenesysCloud;
import com.gs.kranon.reportescustomgds.utilidades.Utilerias;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.io.File;

/**
 *
 * @author kranoncloud
 */
public class GeneradorTXT {
    Thread voThreadReporte;
    
     public void GeneraTXT(String vlContactId) {
      voThreadReporte = new Thread() {   
          @Override
            public void run() {
                
                String nombreArchivo = vlContactId + "txt";
                
               try { 
                   
               }catch(Exception e)  { 
                   
               }
            };
      }; 
      voThreadReporte.start();
  }
        // Map<String, String> voDetails = voConversations.get(vsContactId);
         
        }
                 
          
   

