package com.gs.kranon.reportescustomgds;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Victor Paredes
 */
public class GDSmx {

    private Map<String, String> voMapConf = null;
    private DataReportGDSmx voDataReport = null;

    public GDSmx(Map<String, String> voMapConf, DataReportGDSmx voDataReport) {
        this.voMapConf = voMapConf;
        this.voDataReport = voDataReport;
    }

    public void analizar(Map<String, String> voDetails) {
        String vsBreadCrumbs = voDetails.get("breadCrumbs");
        voDetails.put("incomingCall", getIncomingCall(vsBreadCrumbs));
        voDetails.put("generalSchedule", getGeneralSchedule(vsBreadCrumbs));
        voDetails.put("languageSelection", LanguageSelection(voDetails.get("generalSchedule"), vsBreadCrumbs));
        voDetails.put("endType", getEngType(voDetails.get("languageSelection"), vsBreadCrumbs));
        voDetails.put("vdnTransfer", getVDNTranfer(vsBreadCrumbs));

        if (voDetails.get("incomingCall").equals("1")) {
            voDataReport.setTotalLlamadas(voDataReport.getTotalLlamadas() + 1);
        }
        if (voDetails.get("generalSchedule").equals("OPEN")) {
            voDataReport.setTotalHorarioAbierto(voDataReport.getTotalHorarioAbierto() + 1);
        }
        if (voDetails.get("generalSchedule").equals("CLOSED")) {
            voDataReport.setTotalHorarioCerrado(voDataReport.getTotalHorarioCerrado() + 1);
        }
        if (voDetails.get("languageSelection").equals("ENGLISH")) {
            voDataReport.setAtencionIngles(voDataReport.getAtencionIngles() + 1);
        }
        if (voDetails.get("languageSelection").equals("SPANISH")) {
            voDataReport.setAtencionEspaniol(voDataReport.getAtencionEspaniol() + 1);
        }
        if (voDetails.get("languageSelection").equals("ABANDONED")) {
            voDataReport.setAbandonadaIdioma(voDataReport.getAbandonadaIdioma() + 1);
        }

        //INGLES
        if (voDetails.get("languageSelection").equals("ENGLISH") && voDetails.get("vdnTransfer").equals("ENGLISH")) {
            voDataReport.setTransferIng(voDataReport.getTransferIng() + 1);
        }

        if (voDetails.get("languageSelection").equals("ENGLISH") && voDetails.get("endType").equals("CLOSED_TRANSFER")) {
            voDataReport.setTerminadasHorarioCerrado(voDataReport.getTerminadasHorarioCerrado() + 1);
        }

        if (voDetails.get("languageSelection").equals("ENGLISH") && voDetails.get("endType").equals("ABANDONED")) {
            voDataReport.setAbandonadaAtencionIngles(voDataReport.getAbandonadaAtencionIngles() + 1);
        }

        //ESPAÑOL
        if (voDetails.get("languageSelection").equals("SPANISH")) {
            voDataReport.setSolicitudID(voDataReport.getSolicitudID() + 1);
        }

        if (voDetails.get("languageSelection").equals("SPANISH")
                && voDetails.get("endType").equals("OPEN_TRANSFER")
                && voDetails.get("vdnTransfer").equals("UNIVERSAL")) {
            voDataReport.setTranferUniversal(voDataReport.getTranferUniversal() + 1);
        }

        if (voDetails.get("languageSelection").equals("SPANISH")
                && voDetails.get("endType").equals("OPEN_TRANSFER")
                && voDetails.get("vdnTransfer").equals("MORAL")) {
            voDataReport.setTransferMoral(voDataReport.getTransferMoral() + 1);
        }

        if (voDetails.get("languageSelection").equals("SPANISH")
                && voDetails.get("endType").equals("OPEN_TRANSFER")
                && voDetails.get("vdnTransfer").equals("FISICA")) {
            voDataReport.setTransferFisica(voDataReport.getTransferFisica() + 1);
        }

        if (voDetails.get("languageSelection").equals("SPANISH")
                && voDetails.get("endType").equals("OPEN_TRANSFER_ERROR")
                && voDetails.get("vdnTransfer").equals("UNIVERSAL")) {
            voDataReport.setTransferError(voDataReport.getTransferError() + 1);
        }

        if (voDetails.get("languageSelection").equals("SPANISH")
                && voDetails.get("endType").equals("CLOSED_TRANSFER")) {
            voDataReport.setSinTransferHorarioCerrado(voDataReport.getSinTransferHorarioCerrado() + 1);
        }

        if (voDetails.get("endType").equals("EXCEDE_INTENTOS")) {
            voDataReport.setExcedeIntentos(voDataReport.getExcedeIntentos() + 1);
        }

        if (voDetails.get("languageSelection").equals("SPANISH")
                && voDetails.get("endType").equals("ABANDONED")) {
            voDataReport.setAbandonoAtencionEspaniol(voDataReport.getAbandonoAtencionEspaniol() + 1);
        }
    }

    private String getIncomingCall(String vsBreadCrumbs) {
        if (vsBreadCrumbs.contains("bbvamxap_horarioC") || vsBreadCrumbs.contains("bbvamxap_horarioA")) {
            return "1";
        } else {
            return "0";
        }
    }

    private String getGeneralSchedule(String vsBreadCrumbs) {
        if (vsBreadCrumbs.contains("bbvamxap_horarioC")) {
            return "CLOSED";
        }
        if (vsBreadCrumbs.contains("bbvamxap_horarioA")) {
            return "OPEN";
        }
        return "ABANDONED";
    }

    private String LanguageSelection(String vsGeneralSchedule, String vsBreadCrumbs) {
        if (vsGeneralSchedule.equals("CLOSED")) {
            return "N/A";
        }
        if (vsBreadCrumbs.contains("bbvamxap_opc2Engl")) {
            return "ENGLISH";
        }
        if (vsBreadCrumbs.contains("bbvamxap_opcDSpan")) {
            return "SPANISH";
        }
        return "ABANDONED";
    }

    private String getEngType(String vsLanguageSelection, String vsBreadCrumbs) {
        if (vsLanguageSelection.contains("ENGLISH") && vsBreadCrumbs.contains("transfeVDN")) {
            return "OPEN_TRANSFER";
        }
        if (vsLanguageSelection.contains("ENGLISH") && vsBreadCrumbs.contains("transfer_hcerrado")) {
            return "CLOSED_TRANSFER";
        }
        if (vsLanguageSelection.contains("ENGLISH")) {
            return "ABANDONED";
        }
        if (vsLanguageSelection.contains("SPANISH") && vsBreadCrumbs.contains("XferERRO") && vsBreadCrumbs.contains("transfeVDN")) {
            return "OPEN_TRANSFER_ERROR";
        }
        if (vsLanguageSelection.contains("SPANISH") && vsBreadCrumbs.contains("transfeVDN")) {
            return "OPEN_TRANSFER";
        }
        if (vsLanguageSelection.contains("SPANISH") && vsBreadCrumbs.contains("transfer_hcerrado")) {
            return "CLOSED_TRANSFER";
        }
        if (vsLanguageSelection.contains("SPANISH") && vsBreadCrumbs.contains("identifi_excedint")) {
            return "EXCEDE_INTENTOS";
        }
        if (vsLanguageSelection.contains("SPANISH")) {
            return "ABANDONED";
        }
        return "N/A";
    }

    private String getVDNTranfer(String vsBreadCrumbs) {
        String[] vaVDNs = voMapConf.get("VDNTransfersBBVAmx").split(",");
        for (String vsVDN : vaVDNs) {
            String[] vsDataTransfer = vsVDN.split("-");
            if (vsBreadCrumbs.contains(vsDataTransfer[1])) {
                return vsDataTransfer[0];
            }
        }
        return "";
    }
    
    public String getMetricas(String vsFechaInicio, String vsApplication) {
        int viTotalIngles = 
                voDataReport.getTransferIng() + 
                voDataReport.getTerminadasHorarioCerrado() +
                voDataReport.getAbandonadaAtencionIngles();
        int viTotalIdioma =
                voDataReport.getAtencionIngles() +
                voDataReport.getAtencionEspaniol() +
                voDataReport.getAbandonadaIdioma();
        
        StringBuilder voBufferMetrics = new StringBuilder();
        voBufferMetrics.append(
                  " ----------------------------------------------------------------\r\n"
                + "             Reporte: INBOUND VOZ\r\n"
                + "          Aplicación: " + vsApplication + "\r\n"
                + "               Fecha: " + vsFechaInicio + " 00:00 al " + vsFechaInicio + " 23:59\r\n"
                + " ----------------------------------------------------------------\n\n");

        voBufferMetrics.append(" 1. Número Total de Llamadas:\r\n")
                       .append(String.format(
                               "                    Número de Llamadas Recibidas:        %8d\r\n", voDataReport.getTotalLlamadas()))
                       .append("           -------------------------------------------------\r\n")
                       .append(String.format(
                               "          Llamadas Dentro de Horario de Atención:        %8d\r\n", voDataReport.getTotalHorarioAbierto()))
                       .append(String.format(
                               "           Llamadas Fuera de Horario de Atención:        %8d\r\n\n\n", voDataReport.getTotalHorarioCerrado()))
                
                       .append(" 2. Selección del Idioma de Atención:\r\n")
                       .append(String.format(
                               "                              Atención en Inglés:        %8d\r\n", voDataReport.getAtencionIngles()))
                       .append(String.format(
                               "                             Atención en Español:        %8d\r\n", voDataReport.getAtencionEspaniol()))
                       .append(String.format(
                               "             Llamadas Abandonadas por el Cliente:        %8d\r\n", voDataReport.getAbandonadaIdioma()))
                       .append("           -------------------------------------------------\r\n")
                       .append(String.format(
                               "                                         Totales:        %8d\r\n\n\n", viTotalIdioma))
                       .append(" 2.1. Atención en Inglés:\r\n")
                       .append(String.format(
                               "                           Llamadas Transferidas:        %8d\r\n", voDataReport.getTransferIng()))
                       .append(String.format(
                               "          Llamadas Terminadas (Fuera de Horario):        %8d\r\n", voDataReport.getTerminadasHorarioCerrado()))
                       .append(String.format(
                               "             Llamadas Abandonadas por el Cliente:        %8d\r\n", voDataReport.getAbandonadaAtencionIngles()))
                       .append("           -------------------------------------------------\r\n")
                       .append(String.format(
                               "                                         Totales:        %8d\r\n\n\n", viTotalIngles))
                       .append(" 2.2. Atención en Español:\r\n")
                       .append(String.format(
                               "                   Solicitudes de Identificación:        %8d\r\n", voDataReport.getSolicitudID()))
                       .append("           -------------------------------------------------\r\n")
                       .append(String.format(
                               "             Llamadas Transferidas - UNIVERSALES:        %8d\r\n", voDataReport.getTranferUniversal()))
                       .append(String.format(
                               "                 Llamadas Transferidas - MORALES:        %8d\r\n", voDataReport.getTransferMoral()))
                       .append(String.format(
                               "                 Llamadas Transferidas - FISICAS:        %8d\r\n", voDataReport.getTransferFisica()))
                       .append(String.format(
                               "           Llamadas Transferidas - ERROR Backend:        %8d\r\n", voDataReport.getTransferError()))
                       .append(String.format(
                               "          Llamadas Terminadas (Fuera de Horario):        %8d\r\n", voDataReport.getSinTransferHorarioCerrado()))
                       .append(String.format(
                               "   Llamadas Terminadas - Cliente Excede Intentos:        %8d\r\n", voDataReport.getExcedeIntentos()))
                       .append(String.format(
                               "             Llamadas Abandonadas por el Cliente:        %8d\r\n", voDataReport.getAbandonoAtencionEspaniol()));
                       
        return voBufferMetrics.toString();
    }
    
    public Map<String, Object> getHeaderCSV(){
        Integer i = 0;
        Map<String, Object> voHeaders = new HashMap<>();
        voHeaders.put("conversationId", i++);
        voHeaders.put("conversationStart", i++);
        voHeaders.put("conversationEnd", i++);
        voHeaders.put("ani", i++);
        voHeaders.put("dnis", i++);
        voHeaders.put("flowName", i++);
        voHeaders.put("flowType", i++);
        voHeaders.put("incomingCall", i++);
        voHeaders.put("generalSchedule", i++);
        voHeaders.put("languageSelection", i++);
        voHeaders.put("endType", i++);
        voHeaders.put("vdnTransfer", i++);
        voHeaders.put("UUI", i++);
        voHeaders.put("comentarioCTI", i++);
        voHeaders.put("breadCrumbs", i++);
        return voHeaders;
    }

}
