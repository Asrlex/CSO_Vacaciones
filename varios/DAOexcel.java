package varios.dao;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.tinylog.Logger;

import varios.objetos.Empleado;

public class DAOexcel {
	
	/**
	 * Extrae un Excel con todas las vacaciones
	 * @param inicio
	 * @param fin
	 * @param list
	 * @param listaCalendarios
	 */
	public static void extraerExcel(LocalDate inicio, LocalDate fin, List<Empleado> list, 
								Map<String, List<Set<String>>> listaCalendarios){
		System.out.println(inicio);
		System.out.println(fin);
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate aux = inicio;
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet hoja = wb.createSheet("Calendario");
		
		XSSFFont hFont = wb.createFont();
			hFont.setFontName("Bankia");
			hFont.setBold(true);
			hFont.setFontHeightInPoints((short) 16);
			hFont.setColor(IndexedColors.RED.getIndex());
	    
	    XSSFCellStyle hCellStyle = wb.createCellStyle();
	        hCellStyle.setFont(hFont);
	        hCellStyle.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
	        hCellStyle.setBorderBottom(BorderStyle.DOUBLE);
	        hCellStyle.setBorderTop(BorderStyle.DOUBLE);
	        hCellStyle.setBorderRight(BorderStyle.DOUBLE);
	        hCellStyle.setBorderLeft(BorderStyle.DOUBLE);
	        hCellStyle.setAlignment(HorizontalAlignment.CENTER);
	        hCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		
		XSSFFont cFont = wb.createFont();
	        cFont.setFontName("Bankia");
	        cFont.setBold(false);
	        cFont.setFontHeightInPoints((short) 14);
	        cFont.setColor(IndexedColors.BLACK.getIndex());
	    
	    XSSFCellStyle cCellStyle = wb.createCellStyle();
	    	cCellStyle.setFont(cFont);
	    XSSFCellStyle aprCellStyle = wb.createCellStyle();
	    	aprCellStyle.setFont(cFont);
	    	aprCellStyle.setAlignment(HorizontalAlignment.CENTER);
	        aprCellStyle.setWrapText(true);
	        aprCellStyle.setBorderRight(BorderStyle.THICK);
	        aprCellStyle.setRightBorderColor(IndexedColors.BRIGHT_GREEN.getIndex());
	        aprCellStyle.setBorderLeft(BorderStyle.THICK);
	        aprCellStyle.setLeftBorderColor(IndexedColors.BRIGHT_GREEN.getIndex());
	        aprCellStyle.setBorderTop(BorderStyle.THICK);
	        aprCellStyle.setTopBorderColor(IndexedColors.BRIGHT_GREEN.getIndex());
	        aprCellStyle.setBorderBottom(BorderStyle.THICK);
	        aprCellStyle.setBottomBorderColor(IndexedColors.BRIGHT_GREEN.getIndex());
	        aprCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	    XSSFCellStyle pcCellStyle = wb.createCellStyle();
	    	pcCellStyle.cloneStyleFrom(aprCellStyle);
		    pcCellStyle.setRightBorderColor(IndexedColors.LIGHT_YELLOW.getIndex());
		    pcCellStyle.setLeftBorderColor(IndexedColors.LIGHT_YELLOW.getIndex());
		    pcCellStyle.setTopBorderColor(IndexedColors.LIGHT_YELLOW.getIndex());
		    pcCellStyle.setBottomBorderColor(IndexedColors.LIGHT_YELLOW.getIndex());
	    XSSFCellStyle prCellStyle = wb.createCellStyle();
	    	prCellStyle.cloneStyleFrom(aprCellStyle);
		    prCellStyle.setRightBorderColor(IndexedColors.LIGHT_ORANGE.getIndex());
		    prCellStyle.setLeftBorderColor(IndexedColors.LIGHT_ORANGE.getIndex());
		    prCellStyle.setTopBorderColor(IndexedColors.LIGHT_ORANGE.getIndex());
		    prCellStyle.setBottomBorderColor(IndexedColors.LIGHT_ORANGE.getIndex());
	    XSSFCellStyle kocCellStyle = wb.createCellStyle();
	    	kocCellStyle.cloneStyleFrom(aprCellStyle);
		    kocCellStyle.setRightBorderColor(IndexedColors.RED.getIndex());
		    kocCellStyle.setLeftBorderColor(IndexedColors.RED.getIndex());
		    kocCellStyle.setTopBorderColor(IndexedColors.RED.getIndex());
		    kocCellStyle.setBottomBorderColor(IndexedColors.RED.getIndex());
	    XSSFCellStyle korCellStyle = wb.createCellStyle();
	    	korCellStyle.cloneStyleFrom(aprCellStyle);
		    korCellStyle.setRightBorderColor(IndexedColors.DARK_RED.getIndex());
		    korCellStyle.setLeftBorderColor(IndexedColors.DARK_RED.getIndex());
		    korCellStyle.setTopBorderColor(IndexedColors.DARK_RED.getIndex());
		    korCellStyle.setBottomBorderColor(IndexedColors.DARK_RED.getIndex());
		XSSFCellStyle empCellStyle = wb.createCellStyle();
			empCellStyle.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			empCellStyle.setFillPattern(FillPatternType.THIN_FORWARD_DIAG);
		
			
		XSSFRow hRow = hoja.createRow(0);
	    long duracion = ChronoUnit.DAYS.between(inicio, fin) + 2;
	    System.out.println(duracion);
	    String[] cabeceras = new String[(int) duracion];
        for(int i = 0; i < duracion; i++){
        	if(i == 0)
        		cabeceras[0] = "Empleado";
        	else{
        		System.out.println(aux);
        		if(aux.getDayOfWeek().getValue() > 5 || 
        				DAO.getInstance().getVacaciones().contains(aux))
        			cabeceras[i] = "--";
        		else cabeceras[i] = fmt.format(aux);
        		aux = aux.plusDays(1);
        	}
        }
		for(int i = 0; i < duracion; i++){
			XSSFCell celda = hRow.createCell(i);
			if(cabeceras[i].equals("--"))
				celda.setCellStyle(empCellStyle);
			else{
				celda.setCellValue(cabeceras[i]);
				celda.setCellStyle(hCellStyle);
			}
		}	
		
		int nfila = 1;
		List<Set<String>> auxL;
		double[] resumen = new double[(int) duracion];
		for(Empleado p : list){
			
			aux = inicio;
			auxL = listaCalendarios.get(p.getUsuario());
			
			XSSFRow fila = hoja.createRow(nfila);
			XSSFCell celda0 = fila.createCell(0);
			celda0.setCellValue(p.getNombre() + "     (" + p.getUsuario() + ")");
			celda0.setCellStyle(cCellStyle);
			
			for(int i = 1; i < duracion; i++){
				if(aux.getDayOfWeek() == DayOfWeek.SATURDAY ||
						aux.getDayOfWeek() == DayOfWeek.SUNDAY || 
						(DAO.getInstance().getVacaciones().contains(aux))){
					XSSFCell celda = fila.createCell(i);
						celda.setCellStyle(empCellStyle);
						resumen[i-1] = -1;
					
        		} else{
    				XSSFCell celda = fila.createCell(i);
    				if(auxL.get(0).contains(fmt.format(aux))){
    					
    					resumen[i-1]++;
    					celda.setCellValue("APROBADO");
    					celda.setCellStyle(aprCellStyle);
    					
    				} else if(auxL.get(1).contains(fmt.format(aux))){
    					
    					celda.setCellValue("OK COORD");
    					celda.setCellStyle(pcCellStyle);
    					
    				} else if(auxL.get(2).contains(fmt.format(aux))){
    					
    					celda.setCellValue("OK RESP");
    					celda.setCellStyle(prCellStyle);
    					
    				} else if(auxL.get(3).contains(fmt.format(aux))){
    					
    					celda.setCellValue("KO COORD");
    					celda.setCellStyle(kocCellStyle);
    					
    				} else if(auxL.get(4).contains(fmt.format(aux))){
    					
    					celda.setCellValue("KO RESP");
    					celda.setCellStyle(korCellStyle);
    					
    				} else{
    					fila.createCell(i).setCellValue("--");
    				}
        		}
				aux = aux.plusDays(1);
	        }
			nfila++;
		}
		
		XSSFRow fila = hoja.createRow(nfila+1);
		XSSFRow fila1 = hoja.createRow(nfila+2);
		XSSFCell celda0 = fila.createCell(0);
			celda0.setCellValue("Total empleados: " + (nfila-1));
			celda0.setCellStyle(hCellStyle);
		XSSFCell celda01 = fila1.createCell(0);
			celda01.setCellStyle(hCellStyle);
			
		for(int i = 1; i < duracion; i++){
			XSSFCell celda = fila.createCell(i);
			XSSFCell celda1 = fila1.createCell(i);
			double porcentaje = ((resumen[i-1]/nfila)*100);
			celda.setCellValue(new DecimalFormat("#.##").format(porcentaje) + "%");
			celda1.setCellValue(resumen[i-1]);
			if(porcentaje < 0){
				celda.setCellValue("--");
				celda.setCellStyle(empCellStyle);
				celda1.setCellStyle(empCellStyle);
			} else if(porcentaje < 10){
				celda.setCellStyle(aprCellStyle);
				celda1.setCellStyle(aprCellStyle);
			} else if(porcentaje > 10 && porcentaje < 20){
				celda.setCellStyle(pcCellStyle);
				celda1.setCellStyle(pcCellStyle);
			} else if(porcentaje > 20 && porcentaje < 30){
				celda.setCellStyle(prCellStyle);
				celda1.setCellStyle(prCellStyle);
			} else if(porcentaje > 30 && porcentaje < 40){
				celda.setCellStyle(kocCellStyle);
				celda1.setCellStyle(kocCellStyle);
			} else if(porcentaje > 40 && porcentaje < 50){
				celda.setCellStyle(korCellStyle);
				celda1.setCellStyle(korCellStyle);
			}
		}
		
		hoja.addMergedRegion(new CellRangeAddress(nfila+1, nfila+2, 0, 0));
		
		for(int i = 0; i < cabeceras.length; i++){
			hoja.autoSizeColumn(i);
		}
		
		String name = "VACACIONES " + fmt.format(inicio) + "-" + fmt.format(fin) + ".xlsx";
		try {
			String path = System.getProperty("user.home") + "/Desktop/" + name;
			FileOutputStream fos = new FileOutputStream(path);
			wb.write(fos);
			fos.close();
			wb.close();
			
			JOptionPane.showMessageDialog(null, "Documento extraído a su escritorio con nombre " + name);
			Logger.tag("REGISTROS").info("Vacaciones extraídas");
			Desktop.getDesktop().open(new File(path));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public static void extraerRRHH(String u, JTable val, JTable pen){
		XSSFWorkbook wb = new XSSFWorkbook();
		CreationHelper ch = wb.getCreationHelper();
		
		XSSFFont hFont = wb.createFont();
			hFont.setFontName("Bankia");
			hFont.setBold(true);
			hFont.setFontHeightInPoints((short) 16);
			hFont.setColor(IndexedColors.RED.getIndex());
		XSSFFont cFont = wb.createFont();
	        cFont.setFontName("Bankia");
	        cFont.setBold(false);
	        cFont.setFontHeightInPoints((short) 14);
	        cFont.setColor(IndexedColors.BLACK.getIndex());
        XSSFCellStyle hCellStyle = wb.createCellStyle();
		    hCellStyle.setFont(hFont);
		    hCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		    hCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    hCellStyle.setBorderBottom(BorderStyle.DOUBLE);
		    hCellStyle.setBorderTop(BorderStyle.DOUBLE);
		    hCellStyle.setBorderRight(BorderStyle.DOUBLE);
		    hCellStyle.setBorderLeft(BorderStyle.DOUBLE);
	    XSSFCellStyle cCellStyle = wb.createCellStyle();
	    	cCellStyle.setFont(cFont);
    	XSSFCellStyle dateCellStyle = wb.createCellStyle();
    		dateCellStyle.setFont(cFont);
			dateCellStyle.setDataFormat(ch.createDataFormat().getFormat("dd-MM-yyyy"));
			
		XSSFSheet hoja = wb.createSheet("Validadas");
		XSSFRow hRow = hoja.createRow(0);
		String[] cabeceras = new String[]{val.getColumnName(0), 
				val.getColumnName(1), val.getColumnName(2), val.getColumnName(3)};
		for(int i = 0; i < cabeceras.length; i++){
			XSSFCell celda = hRow.createCell(i);
			celda.setCellValue(cabeceras[i]);
			celda.setCellStyle(hCellStyle);
		}
		
		for(int i = 0; i < val.getModel().getRowCount(); i++){
			XSSFRow fila = hoja.createRow(i + 1);
			for(int j = 0; j < 4; j++){
				XSSFCell celda = fila.createCell(j);
					celda.setCellValue((String) val.getModel().getValueAt(i, j));
					celda.setCellStyle(cCellStyle);
			}
		}
		for(int i = 0; i < cabeceras.length; i++){
			hoja.autoSizeColumn(i);
		}
		
		XSSFSheet hoja2 = wb.createSheet("Pendientes");
		XSSFRow hRow2 = hoja2.createRow(0);
		String[] cabeceras2 = new String[]{"Fecha", "Año Computable", "Tipo"};
		for(int i = 0; i < cabeceras2.length; i++){
			XSSFCell celda = hRow2.createCell(i);
			celda.setCellValue(cabeceras2[i]);
			celda.setCellStyle(hCellStyle);
		}
		
		for(int i = 0; i < pen.getModel().getRowCount(); i++){
			XSSFRow fila = hoja2.createRow(i + 1);
			XSSFCell celda = fila.createCell(0);
				celda.setCellValue((String) pen.getModel().getValueAt(i, 0));
				celda.setCellStyle(cCellStyle);
			XSSFCell an = fila.createCell(1);
				an.setCellValue((String) pen.getModel().getValueAt(i, 1));
				an.setCellStyle(cCellStyle);
			XSSFCell tipo = fila.createCell(2);
				tipo.setCellValue((String) pen.getModel().getValueAt(i, 2));
				tipo.setCellStyle(cCellStyle);
		}
		for(int i = 0; i < cabeceras2.length; i++){
			hoja2.autoSizeColumn(i);
		}
		
		String filename = "Vacaciones " + u + " (" + (LocalDate.now().getYear()-2) + "-" + (LocalDate.now().getYear()+1) + ").xlsx";
		try(FileOutputStream fos = new FileOutputStream(
				System.getProperty("user.home") + "/Desktop/" + filename);){
			wb.write(fos);
			wb.close();
			Desktop.getDesktop().open(
					new File(System.getProperty("user.home") + "/Desktop/" + filename));
		} catch (IOException e) {e.printStackTrace();}
		JOptionPane.showMessageDialog(null, "Excel extraído a su escritorio");
	}
}