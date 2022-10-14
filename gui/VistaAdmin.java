package gui.admin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.lang.StringUtils;

import varios.acciones.MoveMouseListener;
import varios.components.CustomScrollBar;
import varios.components.SortedComboBoxModel;
import varios.dao.DAO;
import varios.objetos.Empleado;

/**
 * Vista detallada de las vacaciones de los empleados 
 * @author A194855
 *
 */
public class VistaAdmin implements Runnable{

	private Font font;
	private Dimension dim;
	private GridBagConstraints c;
	private JFrame parent, current;
	private Color naranja = new Color (249, 131, 62),amarillo = new Color(249, 217, 62);
	private String direccion, equipo, subequipo, horario;
	private Month mes;
	private int año, days, auxn = 0, size = 0, iTabla = 0;
	private JTable table = new JTable(), res = new JTable();
	private DAO dao = DAO.getInstance();
	private boolean active = true;
	private String[] porcentajes;
	private Set<LocalDate> festivos = dao.getVacaciones(), festivosLab = dao.getVacacionesL();
	private Map<String, List<Set<String>>> listaCals = new HashMap<String, List<Set<String>>>();
	private String[] estructura = new String[days];
	private List<Empleado> conjunto;
	private List<String[]> lista = new ArrayList<String[]>();
	private JPanel inferior, central;
	
	public VistaAdmin (JFrame parent){
		this.parent = parent;
		this.direccion = "TODOS";
		this.equipo = "TODOS";
		this.subequipo = "TODOS";
		this.horario = "TODOS";
		this.año = LocalDate.now().getYear();
		this.mes = Month.of(LocalDate.now().getMonthValue());
	}
	
	public void run(){
		DetailedView dv = new DetailedView();
		
		//---AJUSTES GUI---
		GridBagLayout gbl = new GridBagLayout();
		dv.setLayout(gbl);
		c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 10 , 10);
		dv.getContentPane().setBackground(Color.WHITE);
		dv.setUndecorated(true);
		dv.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.ORANGE));
		
		//---BARRA DE MENÚ---
		dv.addMenuBar();
		
		//---BARRA SUPERIOR CON FILTROS---
		dv.addFilters();
		
		dv.pack();
		dv.setVisible(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		dv.setLocation(dim.width/2-dv.getSize().width/2, dim.height/2-dv.getSize().height/2);
		
		current = dv;
		parent.setVisible(false);
	}
	
	class DetailedView extends JFrame{

		private static final long serialVersionUID = 1L;
		
		public void addMenuBar(){
			
			JMenuBar bar = new JMenuBar();
				bar.setLayout(new GridBagLayout());
				bar.setBackground(Color.WHITE);
			font = new Font("Bankia", Font.BOLD, 12);
			
			JMenu menu = new JMenu("Menú");
				menu.setIcon(dao.getMenu());
				menu.setBackground(Color.white);
				menu.setFont(font);
				menu.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
				
			JButton exit = new JButton("", dao.getSalir());
				exit.setContentAreaFilled(false);
				exit.setFont(font);
				exit.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
			
			JButton min = new JButton("", dao.getMinimizar());
				min.setContentAreaFilled(false);
				min.setFont(font);
				min.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));
				
			JLabel label1 = new JLabel ();
				label1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1400));

			
			MoveMouseListener.generar(bar);
			bar.add(menu);
			bar.add(label1);
			bar.add(Box.createHorizontalGlue());
			bar.add(min);
			bar.add(exit);
			setJMenuBar(bar);
			
			min.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					setState(Frame.ICONIFIED);
				}
			});
			exit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					dispose();
					parent.setVisible(true);
				}
			});
		}
		
		public void addFilters(){
			
			font = new Font("Bankia", Font.BOLD, 15);
			dim = new Dimension(200, 60);
			GridLayout grid = new GridLayout(0, 1, 3, 0);
			
			//---DIRECCION---
			JPanel dir = new JPanel();
			dir.setBackground(Color.WHITE);
			dir.setLayout(grid);
			dir.setPreferredSize(dim);
				JLabel dirL = new JLabel("Dirección");
					dirL.setFont(font);
					dir.add(dirL);
				SortedComboBoxModel<String> modelo_dir = new SortedComboBoxModel<String>();
				dao.getListaDirecciones().forEach(modelo_dir::addElement);
				JComboBox <String> dirCB = new JComboBox<String>(modelo_dir);
					dirCB.setSelectedItem(direccion);
					dirCB.setBackground(Color.WHITE);
					dirCB.setFont(font);
			dir.add(dirCB);
			c.gridx = 1;
			c.gridy = 1;
			c.gridheight = 2;
			c.gridwidth = 4;
			add(dir, c);
			
			//---EQUIPO---
			JPanel eq = new JPanel();
			eq.setBackground(Color.WHITE);
			eq.setLayout(grid);
			eq.setPreferredSize(dim);
				JLabel eqL = new JLabel("Equipo");
					eqL.setFont(font);
					eq.add(eqL);
				SortedComboBoxModel<String> modelo_eq = new SortedComboBoxModel<String>();
				dao.getListaEquipos().forEach(modelo_eq::addElement);
				JComboBox <String> eqCB = new JComboBox<String>(modelo_eq);
					eqCB.setSelectedItem(equipo);
					eqCB.setBackground(Color.WHITE);
					eqCB.setFont(font);
			eq.add(eqCB);
			c.gridx = 5;
			add(eq, c);
			
			//---SUBEQUIPO---
			JPanel sub = new JPanel();
			sub.setBackground(Color.WHITE);
			sub.setLayout(grid);
			sub.setPreferredSize(dim);
				JLabel subL = new JLabel("SubEquipo");
					subL.setFont(font);
					sub.add(subL);
				SortedComboBoxModel<String> modelo_sub = new SortedComboBoxModel<String>();
				dao.getListaSubEquipos().forEach(modelo_sub::addElement);
				JComboBox <String> subCB = new JComboBox<String>(modelo_sub);
					subCB.setSelectedItem(subequipo);
					subCB.setBackground(Color.WHITE);
					subCB.setFont(font);
			sub.add(subCB);
			c.gridx = 9;
			add(sub, c);
			
			//---HORARIO---
			JPanel hor = new JPanel();
			hor.setBackground(Color.WHITE);
			hor.setLayout(grid);
			hor.setPreferredSize(dim);
				JLabel horL = new JLabel("Horario");
					horL.setFont(font);
					hor.add(horL);
				SortedComboBoxModel<String> modelo_hor = new SortedComboBoxModel<String>();
				dao.getListaHorarios().forEach(modelo_hor::addElement);
				JComboBox <String> horCB = new JComboBox<String>(modelo_hor);
					horCB.setSelectedItem(horario);
					horCB.setBackground(Color.WHITE);
					horCB.setFont(font);
			hor.add(horCB);
			c.gridx = 13;
			add(hor, c);
			
			//---FECHAS---
			JPanel time = new JPanel();
			time.setLayout(new GridLayout(0, 1, 3, 0));
			//---MES---
			JPanel selector = new JPanel();
				selector.setBackground(Color.WHITE);
			JLabel label = new JLabel("Seleccione mes y año: ");
				label.setFont(font);
			selector.add(label);
			String[] meses = new String[]{"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", 
			                            "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE",
			                            "NOVIEMBRE", "DICIEMBRE"};
			JComboBox<String> month = new JComboBox<String>(meses);
				month.setSelectedIndex(mes.getValue()-1);
				month.setBackground(Color.WHITE);
				month.setFont(font);
				selector.add(month);
				time.add(selector);
			//---AÑO---
			Integer [] years = new Integer[]{LocalDate.now().getYear()-1, LocalDate.now().getYear(), LocalDate.now().getYear()+1};
			JComboBox<Integer> year = new JComboBox<Integer>(years);
				year.setSelectedItem(año);
				year.setBackground(Color.WHITE);
				year.setFont(font);
				selector.add(year);
				time.add(selector);
			c.gridx = 0;
			c.gridy = 4;
			c.gridheight = 1;
			c.gridwidth = 17;
			add(time, c);
			
			//---BOTÓN---
			JPanel butt = new JPanel();
			JButton ok = new JButton("Aplicar filtros");
				ok.setBackground(Color.WHITE);
				butt.setBackground(Color.WHITE);
				ok.setFont(font);
				butt.add(ok);
			c.gridy = 7;
			add(butt, c);

			JLabel labelT = new JLabel(StringUtils.capitalize(mes.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es"))) + " - " + año + 
					"  |  Dirección: " + direccion + "    Equipo: " + equipo + "    SubEquipo: " + subequipo + "    Horario: " + horario);
				labelT.setBackground(Color.white);
				labelT.setFont(new Font("Bankia", Font.BOLD, 18));
				c.gridx = 2;
				c.gridy = 9;
				c.gridheight = 1;
				c.gridwidth = 40;
				add(labelT, c);
			
			
			
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					direccion = (String) dirCB.getSelectedItem();
					equipo = (String) eqCB.getSelectedItem();
					subequipo = (String) subCB.getSelectedItem();
					horario = (String) horCB.getSelectedItem();
					mes = Month.of(month.getSelectedIndex()+1);
					año = (Integer) year.getSelectedItem();
					days = YearMonth.of(año, mes).lengthOfMonth();
					
					generarTabla(false);
		    		generarResumen(false);
		    		
		    		labelT.setText(StringUtils.capitalize(mes.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es"))) + " - " + año + 
					"  |  Dirección: " + direccion + "    Equipo: " + equipo + "    SubEquipo: " + subequipo + "    Horario: " + horario);
				}
			});
						
			//---CONTROL PENDIENTES---
			JPanel ctrlP = new JPanel();
			ctrlP.setBackground(Color.WHITE);
			ctrlP.setLayout(new GridLayout(2, 1));
			
			JButton control = new JButton("<html><div style='text-align: center;'>CONTROLAR<br />"
											+ "PENDIENTES</div></html>");
			control.setFont(new Font("Bankia", Font.BOLD, 21));
			control.setPreferredSize(new Dimension(200, 75));
			control.setBackground(Color.WHITE);
			
			control.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK), 
					BorderFactory.createEmptyBorder(10, 20, 10, 20)));
			ctrlP.add(control);
			control.addMouseListener(new MouseListener(){
				public void mouseClicked(MouseEvent e){
					new Thread(new ControlarPendientes(current)).start();
					current.setVisible(false);
					control.setMultiClickThreshhold(2000);
				}
				public void mouseEntered(MouseEvent e){
					control.setBackground(new Color(111, 255, 86));
					control.setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK), 
							BorderFactory.createEmptyBorder(10, 20, 10, 20)));
				}
				public void mouseExited(MouseEvent evt) {
					control.setBackground(Color.WHITE);
					control.setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK), 
							BorderFactory.createEmptyBorder(10, 20, 10, 20)));
			    }
				public void mousePressed(MouseEvent arg0) {}
				public void mouseReleased(MouseEvent arg0) {}
			});
			c.gridx = 20;
			c.gridy = 0;
			c.gridheight = 8;
			c.gridwidth = 7;
			c.anchor = GridBagConstraints.EAST;
			add(control, c);
			
			JButton extraer = new JButton("<html><div style='text-align: center;'>EXTRAER<br />"
											+ "EXCEL</div></html>", dao.getExcel());
			extraer.setFont(new Font("Bankia", Font.BOLD, 21));
			extraer.setPreferredSize(new Dimension(200, 75));
			extraer.setBackground(Color.WHITE);
			extraer.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(2, 2, 2, 2, Color.GREEN), 
			BorderFactory.createEmptyBorder(10, 20, 10, 20)));
			extraer.addMouseListener(new MouseListener(){
				public void mouseClicked(MouseEvent e){
					new Thread(new ExtraerExcel(listaCals)).start();
					extraer.setMultiClickThreshhold(2000);
				}
				public void mouseEntered(MouseEvent e){
					extraer.setBackground(new Color(111, 255, 86));
					extraer.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK), 
						BorderFactory.createEmptyBorder(10, 20, 10, 20)));
				}
				public void mouseExited(MouseEvent evt) {
					extraer.setBackground(Color.WHITE);
					extraer.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(2, 2, 2, 2, Color.GREEN), 
						BorderFactory.createEmptyBorder(10, 20, 10, 20)));
				}
				public void mousePressed(MouseEvent arg0) {}
				public void mouseReleased(MouseEvent arg0) {}
			});
			c.gridx = 34;
			c.gridwidth = 6;
			c.anchor = GridBagConstraints.CENTER;
			add(extraer, c);
			
			days = YearMonth.of(año, mes).lengthOfMonth();
			addTabla();
			addResumen();
		}
		
		public void addTabla(){
			central = new JPanel();
				central.setBackground(Color.WHITE);
				central.add(CustomScrollBar.generar(generarTabla(true), new Dimension(1580, 500), Color.ORANGE));
			c.gridx = 0;
			c.gridy = 11;
			c.gridheight = 15;
			c.gridwidth = 40;
			add(central, c);
		}
		
		public void addResumen(){
			inferior = new JPanel();
				inferior.setBackground(Color.WHITE);
			c.gridx = 0;
			c.gridy = 30;
			c.gridheight = 2;
			c.gridwidth = 40;
			add(inferior, c);
			inferior.add(CustomScrollBar.generar(generarResumen(true), new Dimension(1580, 90), Color.ORANGE));
		}

		private JTable generarTabla(boolean init){
			if(init)
				table = new JTable(new CustomModel(generarDatosTabla(true), generarColumnasTabla()){
					private static final long serialVersionUID = 1L;
					public Class<? extends Object> getColumnClass(int column){
		                return getValueAt(0, column).getClass();
		            }
				});
			else
				((CustomModel)table.getModel()).updateCompleteTable(
						generarDatosTabla(false), generarColumnasTabla());
			
			ColorRenderer renderer = new ColorRenderer();
			renderer.setHorizontalAlignment( JLabel.CENTER );
			table.setDefaultRenderer(String.class, renderer);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.getColumnModel().getColumn(0).setPreferredWidth(250);
			for(int t = 1; t < days + 1; t++){
				table.getColumnModel().getColumn(t).setPreferredWidth(42);
				table.setRowHeight(30);
			}
			table.getTableHeader().setResizingAllowed(false);
			table.getTableHeader().setReorderingAllowed(false);
			table.setBackground(Color.WHITE);
			table.setFont(new Font("Bankia", Font.PLAIN, 14));
			
	        return table;
		}
		
		private JTable generarResumen(boolean init){
			if(init)
				res = new JTable(new CustomModel(generarDatosResumen(), generarColumnasResumen()){
					private static final long serialVersionUID = 1L;
					public Class<? extends Object> getColumnClass(int column){
		                return getValueAt(0, column).getClass();
		            }
				});
			else
				((CustomModel)res.getModel()).updateCompleteTable(
	    				generarDatosResumen(), generarColumnasResumen());
			
			ColorRendererResumen r = new ColorRendererResumen();
			res.setDefaultRenderer(String.class, r);
			r.setHorizontalAlignment( JLabel.CENTER );
			res.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			res.getColumnModel().getColumn(0).setPreferredWidth(250);
			for(int t = 1; t < days + 1; t++){
				res.getColumnModel().getColumn(t).setPreferredWidth(42);
				res.setRowHeight(30);
			}
			res.setEnabled(false);
			res.setBackground(Color.WHITE);
			res.setFont(new Font("Bankia", Font.BOLD, 15));

	        return res;
		}
		
		public Object[][] generarDatosTabla(boolean first){
			if(first){
				conjunto = dao.getListaEquipo();
				if(conjunto.isEmpty()){
					JOptionPane.showMessageDialog(null, "NO EXISTEN USUARIOS QUE CUMPLAN LOS"
													+ "CRITERIOS SOLICITADOS");
					active = false;
				}
			} else{
				conjunto = dao.filter(dao.getListaEquipo(), direccion, equipo, subequipo, horario);
				if(conjunto.isEmpty()){
					JOptionPane.showMessageDialog(null, "NO EXISTEN USUARIOS QUE CUMPLAN LOS"
							+ "CRITERIOS SOLICITADOS");
					active = false;
				}
			}
			
		    List<Empleado> list = new ArrayList<>(conjunto);
		    list.sort((o1, o2) -> o1.getNombre().compareTo(o2.getNombre()));
		    
			Object[][] data = new Object [conjunto.size()][days + 1];
			size = conjunto.size();
			
			String[] resumen = new String[days+1];
			resumen[0] = "NÚMERO DE EMPLEADOS";
			for(int i = 1; i < days+1; i++){
				resumen[i] = "0";
			}
			if(active){
				iTabla = 0;
				list.forEach(e -> {
					lista = getCalendar(e.getUsuario(), resumen, conjunto.size());
					estructura = lista.get(0);
					for(int j = 0; j < days + 1; j++){
						data[iTabla][j] = j == 0 ? e.getNombre() : estructura[j-1];
					}
					iTabla++;
				});
				porcentajes = lista.get(1);
			} else{
				for(int i = 0; i < conjunto.size(); i++){
					for(int j = 0; j < days + 1; j++){
						data[i][j] = "";
					}
				}
				
			}
			return data;
		}
		
		public List<String[]> getCalendar (String code, String[] resumen, int size){
			List <String[]> lista = new ArrayList<String[]>();
			Set<LocalDate> aprobadas = dao.getApproved(code, 0);
			Set<LocalDate> pendientesC = dao.getPending(code, "OkPendiente", false);
			Set<LocalDate> pendientesR = dao.getPending(code, "OkPendiente", true);
			Set<LocalDate> pendientesCKO = dao.getPending(code, "KoPendiente", false);
			Set<LocalDate> pendientesRKO = dao.getPending(code, "KoPendiente", true);
			
			String [] fechas = new String[days];
			for(int i = 0; i < days; i++){
				fechas[i] = "";
			}
			
			cotejarFechas(aprobadas, "APR", fechas, resumen);
			cotejarFechas(pendientesC, "P CO", fechas, null);
			cotejarFechas(pendientesR, "P RES", fechas, null);
			cotejarFechas(pendientesCKO, "KO CO", fechas, null);
			cotejarFechas(pendientesRKO, "KO RES", fechas, null);
			cotejarFechas(pendientesRKO, "KO RES", fechas, null);
			cotejarFechas(festivosLab, "F LAB", fechas, null);
			buscarNoLaborables(fechas, resumen);
			
			lista.add(fechas);
			lista.add(resumen);
			
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			List<Set<String>> calendarios = new ArrayList<Set<String>>();
			Set<String> aprS = new HashSet<String>();
			Set<String> pcS = new HashSet<String>();
			Set<String> prS = new HashSet<String>();
			Set<String> kocS = new HashSet<String>();
			Set<String> korS = new HashSet<String>();
			for(LocalDate d : aprobadas){aprS.add(fmt.format(d));}
			for(LocalDate d : pendientesC){pcS.add(fmt.format(d));}
			for(LocalDate d : pendientesR){prS.add(fmt.format(d));}
			for(LocalDate d : pendientesCKO){kocS.add(fmt.format(d));}
			for(LocalDate d : pendientesRKO){korS.add(fmt.format(d));}
			calendarios.add(aprS);
			calendarios.add(pcS);
			calendarios.add(prS);
			calendarios.add(kocS);
			calendarios.add(korS);
			listaCals.put(code, calendarios);
			
			return lista;
		}
		
		private void buscarNoLaborables(String [] fechas, String[] resumen){
			LocalDate inicio = LocalDate.of(año, mes.getValue(), 1);
			for(int i = 1; i < (mes.maxLength() + 1); i++){
				if(inicio.getDayOfWeek().getValue() > 5 || 
						festivos.contains(inicio)){
					fechas[i-1] = "NO L";
				}
				inicio = inicio.plusDays(1);
			}
		}
		
		private void cotejarFechas(Set<LocalDate> lista, String tipo, String [] fechas, String[] resumen){
			for(LocalDate aux : lista){
				if(aux.getMonthValue() == mes.getValue()  &&  aux.getYear() == año){
					fechas[aux.getDayOfMonth() - 1] = tipo;
					if(tipo.equals("APR")){
						auxn = Integer.parseInt(resumen[aux.getDayOfMonth()]);
						auxn++;
						resumen[aux.getDayOfMonth()] = String.valueOf(auxn);
					}
				}
			}
		}
		
		public String [] generarColumnasTabla (){
			String [] columns = new String[days + 1];
			for(int i = 0; i < days + 1; i++){
				if(i == 0)
					columns[i] = "Empleado";
				else
					columns[i] = Integer.toString(i);
			}
			return columns;
		}
		
		public String [] generarColumnasResumen (){
			
			String [] columns = new String[days + 1];
			for(int i = 0; i < days + 1; i++){
				if(i == 0)
					columns[i] = "RESUMEN (" + size + " empleados)";
				else
					columns[i] = Integer.toString(i);
			}
			return columns;
		}
		
		public Object[][] generarDatosResumen(){
			Object[][] datos = new Object[2][days+1];
			String[] secundario = new String[days+1];
			secundario[0] = "PORCENTAJES ASISTENCIA";
			String[] blank = new String[days+1];
			if(active){
				for(int j = 1; j < days+1; j++){
					secundario[j] = porcentajes[j];
					if(porcentajes[j].equals("0")){
						porcentajes[j] = "";
					}
				}
				double aux = 0.00000;
				for(int j = 1; j < days+1; j++){
					if(!porcentajes[j].equals("0") && !porcentajes[j].equals("")){
						aux = Integer.parseInt(porcentajes[j])*100/size;
						secundario[j] = (int)aux + "%";
					}else{
						secundario[j] = "";
					}
				}
				datos[0] = secundario;
				datos[1] = porcentajes;
			}else{
				for(int j = 1; j < days+1; j++){
					secundario[j] = "";
					blank[j] = "";
				}
				datos[0] = secundario;
				datos[1] = blank;
			}
			active = true;
			return datos;
		}
		
		class CustomModel extends AbstractTableModel {
			private static final long serialVersionUID = 1L;
			
			String[] colNames;
		    Object[][] data;
		    
		    public CustomModel(Object[][] data, String[] colNames){
		    	this.colNames = colNames;
		    	this.data = data;
		    }
		  
		    public String getColumnName(int column) { return colNames[column]; }
		  
		    public int getColumnCount() { return colNames.length; }
		  
		    public int getRowCount() { return data.length; }
		  
		    public Object getValueAt(int row, int col) { return data[row][col]; }
		    
		    public void setValueAt(Object value, int row, int col){
		    	data[row][col] = value;
		    	fireTableCellUpdated(row, col);
		    }
		    
		    public void updateAllData(Object[][] dat){
		    	data = dat;
		    	fireTableDataChanged();
		    }
		    
		    public void updateCompleteTable(Object[][] dat, String[] cols){
		    	colNames = cols;
		    	data = dat;
		    	fireTableStructureChanged();
		    }
		}
		
		class ColorRenderer extends DefaultTableCellRenderer {
			private static final long serialVersionUID = 1L;
		    public Component getTableCellRendererComponent(JTable table, Object value,
		    		boolean isSelected, boolean hasFocus, int row, int column) {
		        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		        CustomModel model = (CustomModel)table.getModel();
		        String o = (String) model.getValueAt(row, column);
		        
	        	if(isSelected){
		        	setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.ORANGE));
		        } else{
		        	setForeground(Color.black);
		        }
	        	switch(o){
	        		case "APR":
	        			setBackground(Color.GREEN); break;
	        		case "NO L":
	        			setBackground(Color.DARK_GRAY); break;
	        		case "P RES":
	        			setBackground(amarillo); break;
	        		case "P CO":
	        			setBackground(naranja); break;
	        		case "KO CO":
	        			setBackground(new Color(255, 73, 73)); break;
	        		case "KO RES":
	        			setBackground(new Color(255, 145, 145)); break;
	        		case "F LAB":
	        			setBackground(new Color(153, 191, 255)); break;
        			default:
        				setBackground(Color.WHITE); break;
	        	}
		        return this;
		    }
		}
		
		class ColorRendererResumen extends DefaultTableCellRenderer {
			private static final long serialVersionUID = 1L;
		    public Component getTableCellRendererComponent(JTable table, Object value,
		    		boolean isSelected, boolean hasFocus, int row, int column) {
		        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		        CustomModel model = (CustomModel)table.getModel();
		        String aux = (String) model.getValueAt(row, column);
        		setForeground(Color.black);
        		
	        	if((aux.length() == 2) && (aux.contains("%"))){
		            setBackground(new Color(169, 244, 137));
	        	} else if((aux.length() > 2) && (aux.contains("%"))){
	        		if((Integer.valueOf(aux.substring(0, 2)) >= 10) 
	        				&& (Integer.valueOf(aux.substring(0, 2)) < 20)){
	        			setBackground(new Color(249, 248, 147));
	        		} else if((Integer.valueOf(aux.substring(0, 2)) >= 20) 
	        				&& (Integer.valueOf(aux.substring(0, 2)) < 30)){
	        			setBackground(new Color(249, 220, 147));
	        		} else if((Integer.valueOf(aux.substring(0, 2)) >= 30) 
	        				&& (Integer.valueOf(aux.substring(0, 2)) < 40)){
	        			setBackground(new Color(249, 170, 147));
	        		} else if((Integer.valueOf(aux.substring(0, 2)) >= 40) 
	        				&& (Integer.valueOf(aux.substring(0, 2)) < 60)){
	        			setBackground(new Color(255, 0, 0));
	        			setForeground(Color.white);
	        		}
	        	} else if(aux.equals("")){
	        		setBackground(Color.DARK_GRAY);
	        	} else{
	        		setBackground(Color.WHITE);
	        	}
		        return this;
		    }
		}
	}
}
