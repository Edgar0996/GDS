package com.gs.kranon.reportescustomgds.utilidades;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class Excel {

    static {
        System.setProperty("dateLog", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
    }
    private static final Logger voLogger = LogManager.getLogger("Reporte");

    private HSSFWorkbook workbook;
    private HSSFSheet sheet;
    private HSSFRow row;
    private HSSFCell cell;
    private int rowCount = -1;
    private String vsUUI;

    public Excel(String vsUUI) {
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet("Java");
        this.vsUUI = vsUUI;
    }

    public void addInfo(Map<String, Object> voHeaders, Map<String, Map<String, String>> voContents, List content) {
        row = sheet.createRow(++rowCount);
        voLogger.info("[Excel      ][" + vsUUI + "] ---> ADD HEADERS");
        for (Entry<String, Object> voEntry : voHeaders.entrySet()) {
            String vsValue = voEntry.getKey();
            Object voIndex = voEntry.getValue();
            HSSFCell cell = row.createCell((Integer) voIndex);
            if (vsValue instanceof String) {
                cell.setCellValue((String) vsValue);
            }
        }
        voLogger.info("[Excel      ][" + vsUUI + "] ---> ADDING INFORMATION FROM CONVERSATIONS");
        for (int i = 0; i < content.size(); i++) {
            row = sheet.createRow(++rowCount);
            String[] lineElements = (String[]) content.get(i);
            int k=0;
            for (String lineElement : lineElements) {           
                    HSSFCell cell = row.createCell(k);
                    cell.setCellValue(lineElement);
                    k++;

            }
        }
    }

    @SuppressWarnings("deprecation")
    public boolean createCSV(String vsPath) {
        StringBuffer voSBData = new StringBuffer();
        Row row;
        Cell cell;
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                cell = cellIterator.next();
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_BOOLEAN:
                        voSBData.append(cell.getBooleanCellValue() + ",");
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        voSBData.append(cell.getNumericCellValue() + ",");
                        break;
                    case Cell.CELL_TYPE_STRING:
                        if (!cell.getStringCellValue().equals("N/A")) {
                            voSBData.append(cell.getStringCellValue() + ",");
                        } else {
                            voSBData.append("" + ",");
                        }
                        break;
                    case Cell.CELL_TYPE_BLANK:
                        voSBData.append("0" + ",");
                        break;
                    default:
                        voSBData.append(cell + ",");
                }
            }

            voSBData.append("\n");
        }
        File voFile = new File(vsPath);
        if (voFile.exists()) {
            voFile.delete();
        }
        if (!voFile.getParentFile().exists()) {
            voFile.mkdirs();
        }
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(vsPath);
            fos.write(voSBData.toString().getBytes());
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            voLogger.error("[Excel      ][" + vsUUI + "] ---> ERROR : " + e.getMessage());
        } catch (IOException ex) {
            voLogger.error("[Excel      ][" + vsUUI + "] ---> ERROR : " + ex.getMessage());
        }
        return false;
    }

}
