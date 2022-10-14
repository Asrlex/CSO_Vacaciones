package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Multimap;

import varios.acciones.MoveMouseListener;
import varios.components.CustomScrollBar;
import varios.components.JTableFactory;
import varios.components.JTableFactory.CustomModel;
import varios.dao.DAO;
import varios.dao.DAOexcel;
import varios.objetos.Empleado;

public class HR implements Runnable{
	
	private Font font = null;
	private GridBagConstraints c = new GridBagConstraints();
	private JFrame parent = null;
	private DAO dao = DAO.getInstance();
	private int i1 = 0, i2 = 0, i3 = 0, i4 = 0;
	private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private DateTimeFormatter fmtread = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private JTable tablaval = null, tablapen = null;
	private String u = "";
	
	public HR(JFrame current) {
		this.parent = current;
	}

	public void run() {
		
		Rh rh = new Rh();
		
		//---Barra de tareas---
		rh.addMenuBar();
		
		//---PROPIEDADES GUI---
		GridBagLayout gbl = new GridBagLayout();
		rh.setLayout(gbl);
		c.insets = new Insets(5, 5, 5, 5);
		rh.getContentPane().setBackground(Color.WHITE);
		rh.setUndecorated(true);
		rh.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.ORANGE));
		
		//---Paneles---
		rh.addForm();
		
		rh.pack();
		rh.setVisible(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		rh.setLocation(dim.width/2-rh.getSize().width/2, dim.height/2-rh.getSize().height/2);
	}
	
	public class Rh extends JFrame{
		
		private static final long serialVersionUID = 1L;

		public void addMenuBar(){
			
			JMenuBar bar = new JMenuBar();
				bar.setLayout(new GridBagLayout());
				bar.setBackground(Color.WHITE);
			font = new Font("Bankia", Font.BOLD, 12);
			
			JButton exit = new JButton("", dao.getSalir());
				exit.setContentAreaFilled(false);
				exit.setFont(font);
				exit.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
			
			JButton min = new JButton("", dao.getMinimizar());
				min.setContentAreaFilled(false);
				min.setFont(font);
				min.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));
			
			JLabel label1 = new JLabel ();
				label1.setForeground(Color.ORANGE);
				label1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 550));
			
			MoveMouseListener.generar(bar);
			bar.add(Box.createHorizontalGlue());
			bar.add(label1);
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
		
		public void addForm(){
			font = new Font("Bankia", Font.BOLD, 17);
			JPanel form = new JPanel();
				form.setBackground(Color.WHITE);
				form.setLayout(new GridBagLayout());
				JLabel label = new JLabel("Selección de usuario");
					label.setBackground(Color.white);
					label.setFont(font);
					c.gridx = 0;
					c.gridy = 0;
					c.gridwidth = 15;
					c.gridheight = 2;
					add(form, c);
					form.add(label, c);
				DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
				model.addElement("---");
				dao.getListaEquipo().stream()
//					.filter(e -> !e.getUsuario().equals("A148657") && !e.getUsuario().equals("A115809") && 
//							!e.getUsuario().equals("A119737") && !e.getUsuario().equals("A124050"))
						.sorted(Comparator.comparing(Empleado::getNombre))
							.forEach(e -> {
								model.addElement(e.getNombre() + " (" + e.getUsuario() + ")");
							});
				JComboBox<String> usuarios = new JComboBox<>(model);
					usuarios.setBackground(Color.WHITE);
					usuarios.setFont(font);
					c.gridx = 0;
					c.gridy = 3;
					c.gridwidth = 15;
					c.gridheight = 2;
					add(form, c);
					form.add(usuarios, c);
			
			JPanel tabla = new JPanel();
				tabla.setBackground(Color.white);
			JPanel boton = new JPanel();
				boton.setBackground(Color.white);
				JButton b = new JButton("Extraer");
					b.setBackground(Color.gray);
					b.setFont(font);
					boton.add(b);
			b.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(tablaval != null && tablapen != null){
						DAOexcel.extraerRRHH(u, tablaval, tablapen);
					}
				}
			});
			usuarios.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(!usuarios.getSelectedItem().equals("---")){
				    	u = StringUtils.substringBetween((String) usuarios.getSelectedItem(), "(", ")");
				    	if(tabla.getComponentCount() == 0){
					    	JTabbedPane pestañas = generarPestañas(u);
					    	tabla.add(pestañas);
							pestañas.repaint(); pestañas.revalidate();
					    	tabla.repaint(); tabla.revalidate();
					    	repaint(); revalidate(); pack();
				    	} else{
				    		Multimap<Integer, LocalDate> mapa = dao.getUsuarioRRHHValidado(u);
				    		if(mapa.isEmpty()){
				    			JOptionPane.showMessageDialog(null, "NO HAY REGISTROS DE ESTE USUARIO");
				    		} else{
					    		((CustomModel)tablaval.getModel()).updateAllData(
					    				generarDatosVal(mapa));
					    		((CustomModel)tablapen.getModel()).updateAllData(
					    				generarDatosPen(dao.getUsuarioRRHHPendiente(u)));
				    		}
				    	}
					} else{
						tabla.removeAll();
				    	tabla.repaint(); tabla.revalidate();
				    	repaint(); revalidate(); pack();
					}
				}
			});
			
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 15;
			c.gridheight = 2;
			add(form, c);
			c.gridy = 3;
			c.gridwidth = 15;
			c.gridheight = 15;
			add(tabla, c);
			c.gridy = 19;
			c.gridwidth = 15;
			c.gridheight = 2;
			add(boton, c);
		}
		
		private JTabbedPane generarPestañas(String u){
			JTabbedPane pestañas = new JTabbedPane();
				pestañas.setBackground(Color.WHITE);
				pestañas.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
				pestañas.setPreferredSize(new Dimension());
				pestañas.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
				pestañas.setPreferredSize(new Dimension(430, 360));
				JPanel panel1 = new JPanel();
					panel1.setBackground(Color.WHITE);
					Multimap<Integer, LocalDate> mapa = dao.getUsuarioRRHHValidado(u);
		    		if(mapa.isEmpty()){
		    			JOptionPane.showMessageDialog(null, "NO HAY REGISTROS DE ESTE USUARIO");
		    			return null;
		    		}
					panel1.add(generarTablaValidada(mapa));
					pestañas.addTab("Validadas", null, panel1, "Validadas");
					pestañas.setMnemonicAt(0, KeyEvent.VK_1);
				JPanel panel2 = new JPanel();
					panel2.setBackground(Color.WHITE);
					panel2.add(generarTablaPendientes(dao.getUsuarioRRHHPendiente(u)));
					pestañas.addTab("Pendientes", null, panel2, "Pendientes");
					pestañas.setMnemonicAt(1, KeyEvent.VK_2);
			return pestañas;
		}
		
		private JScrollPane generarTablaValidada(Multimap<Integer, LocalDate> multimap){
			int a = LocalDate.now().getYear();
			final String[] columnas = new String[]{String.valueOf(a-2), String.valueOf(a-1), String.valueOf(a), String.valueOf(a+1)};
			tablaval = JTableFactory.generar(generarDatosVal(multimap), columnas, font, new Color(51, 189, 88));
				tablaval.getColumnModel().getColumn(0).setPreferredWidth(100);
				tablaval.getColumnModel().getColumn(1).setPreferredWidth(100);
				tablaval.getColumnModel().getColumn(2).setPreferredWidth(100);
				tablaval.getColumnModel().getColumn(3).setPreferredWidth(100);
				List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
				TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tablaval.getModel());
				sorter = new TableRowSorter<>(tablaval.getModel());
				tablaval.setRowSorter(sorter);
				sortKeys.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
				sorter.setSortKeys(sortKeys);
				sorter.sort();
			
			return CustomScrollBar.generar(tablaval, new Dimension(420, 300), Color.ORANGE);
		}
		
		private String[][] generarDatosVal(Multimap<Integer, LocalDate> multimap){
			int a = LocalDate.now().getYear();
			i1 = 0; i2 = 0; i3 = 0; i4 = 0;
			multimap.entries().stream().forEach(e -> {
				if(e.getKey() == (a - 2))
					i1++;
				if(e.getKey() == (a - 1))
					i2++;
				if(e.getKey() == (a))
					i3++;
				if(e.getKey() == (a + 1))
					i4++;
			});
			String[][] data = new String[Collections.max(Arrays.asList(i1, i2, i3, i4))][4];
			i1 = 0; i2 = 0; i3 = 0; i4 = 0;
			multimap.entries().stream().forEach(e -> {
				if(e.getKey() == (a - 2)){
					data[i1][0] = fmt.format(e.getValue());
					i1++;
				}
				if(e.getKey() == (a - 1)){
					data[i2][1] = fmt.format(e.getValue());
					i2++;
				}
				if(e.getKey() == (a)){
					data[i3][2] = fmt.format(e.getValue());
					i3++;
				}
				if(e.getKey() == (a + 1)){
					data[i4][3] = fmt.format(e.getValue());
					i4++;
				}
			});
			return data;
		}
		
		private JScrollPane generarTablaPendientes(List<List<String>> list){
			final String[] columnas = new String[]{"Fecha", "Año Computable", "Tipo"};
			tablapen = JTableFactory.generar(generarDatosPen(list), columnas, font, new Color(51, 189, 88));
				tablapen.getColumnModel().getColumn(0).setPreferredWidth(100);
				tablapen.getColumnModel().getColumn(1).setPreferredWidth(140);
				tablapen.getColumnModel().getColumn(2).setPreferredWidth(130);
			
			return CustomScrollBar.generar(tablapen, new Dimension(420, 300), Color.ORANGE);
		}
		
		private String[][] generarDatosPen(List<List<String>> list){
			String[][] data;
			int i = 0;
			if(!list.isEmpty()){
				data = new String[list.size()][3];
				for(List<String> l : list){
					data[i][0] = fmt.format(fmtread.parse(l.get(0)));
					data[i][1] = l.get(1);
					data[i][2] = l.get(2);
					i++;
				}
			} else{
				data = new String[1][3];
				data[0][0] = "";
				data[0][1] = "";
				data[0][2] = "";
			}
			return data;
		}
	}
}
