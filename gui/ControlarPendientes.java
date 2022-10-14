package gui.admin;

import java.awt.Color;
import java.awt.Desktop;
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

import varios.acciones.MoveMouseListener;
import varios.components.CustomScrollBar;
import varios.components.SortedComboBoxModel;
import varios.dao.DAO;

/**
 * Ventana encargada de aceptar o rechazar vacaciones pendientes
 * @author A194855
 */
public class ControlarPendientes implements Runnable{
	
	private GridBagConstraints c;
	private Font font;
	private JFrame parent;
	private DAO dao = DAO.getInstance();
	private Dimension dim = new Dimension(150, 100);
	private String solicitante;
	private Border def_border = BorderFactory.createMatteBorder(3, 3, 3, 3, Color.black);
	private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private List<String> pend_coord = new ArrayList<>(), pend_resp = new ArrayList<>();
	private SortedComboBoxModel<String> modelo_coord = new SortedComboBoxModel<String>(),
			 modelo_resp = new SortedComboBoxModel<String>();
	private JComboBox<String> empCB_coord = new JComboBox<String>(modelo_coord),
			empCB_resp = new JComboBox<String>(modelo_resp);

	public ControlarPendientes (JFrame parent){
		this.parent = parent;
	}
	
	public void run(){
		
		Cp cp = new Cp();
		
		//---AJUSTES GUI---
		GridBagLayout gbl = new GridBagLayout();
		cp.setLayout(gbl);
		c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 10 , 10);
		cp.getContentPane().setBackground(Color.WHITE);
		cp.setUndecorated(true);
		cp.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.ORANGE));

		//---BARRA DE MENÚ---
		cp.addMenuBar();
		
		//---FORMULARIO---
		cp.addFormC();
		cp.addFormR();
		
		cp.pack();
		cp.setVisible(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		cp.setLocation(dim.width/2-cp.getSize().width/2, dim.height/2-cp.getSize().height/2);
	}
	
	class Cp extends JFrame{
		
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
				
			JMenuItem actualizar = new JMenuItem("Actualizar (F5)", dao.getActualizar());
				actualizar.setBackground(Color.white);
				actualizar.setFont(font);
				actualizar.setMnemonic(KeyEvent.VK_F5);
				menu.add(actualizar);

			JMenuItem info = new JMenuItem("Ayuda (CTRL + I)", dao.getInfo());
				info.setBackground(Color.white);
				info.setFont(font);
				info.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
				menu.add(info);
			
			JButton exit = new JButton("", dao.getSalir());
				exit.setContentAreaFilled(false);
				exit.setFont(font);
				exit.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
			
			JButton min = new JButton("", dao.getMinimizar());
				min.setContentAreaFilled(false);
				min.setFont(font);
				min.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));
				
			JLabel label1 = new JLabel ();
				label1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 400));
			
			MoveMouseListener.generar(bar);
			bar.add(menu);
			bar.add(label1);
			bar.add(Box.createHorizontalGlue());
			bar.add(min);
			bar.add(exit);
			setJMenuBar(bar);
			
			actualizar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					cargarPendientesCoord();
					cargarPendientesResp();
				}
			});
			info.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){}
			});
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
		
		public void addFormC (){
			
			//**************************************
			//**************************************
			font = new Font("Bankia", Font.BOLD, 18);
			
			DefaultListModel<String> modelo_lista_val_coord = new DefaultListModel<String>();
			JList<String> lista_val_coord = new JList<String>(modelo_lista_val_coord);
			JScrollPane sp_val_coord = CustomScrollBar.generar(lista_val_coord, dim, Color.ORANGE);
				sp_val_coord.setBorder(def_border);
			List<String> lista_val = new ArrayList<String>();
			
			DefaultListModel<String> modelo_lista_anul_coord = new DefaultListModel<String>();
			JList<String> lista_anul_coord = new JList<String>(modelo_lista_anul_coord);
			JScrollPane sp_anul_coord = CustomScrollBar.generar(lista_anul_coord, dim, Color.ORANGE);
				sp_anul_coord.setBorder(def_border);
			List<String> lista_anul = new ArrayList<String>();
			
			JLabel label = new JLabel("Aprobación como validador");
			label.setBackground(Color.WHITE);
			label.setFont(font);
 			empCB_coord.setBackground(Color.WHITE);
			
			//**************************************
			//**************************************
			cargarPendientesCoord();
			//**************************************
			//**************************************
			DefaultListModel<String> modelo_selec_val_coord = new DefaultListModel<String>();
			JList<String> selec_val_coord = new JList<String>(modelo_selec_val_coord);
				selec_val_coord.setBackground(Color.WHITE);
				selec_val_coord.setFont(font);
			JScrollPane sp_selec_val_coord = CustomScrollBar.generar(selec_val_coord, dim, Color.ORANGE);
				sp_selec_val_coord.setBorder(def_border);
			
			JButton add_val = new JButton(dao.getAdd());
				add_val.setBackground(new Color(255, 204, 96));
				add_val.setBorder(BorderFactory.createLineBorder(Color.black, 2));
			
			JButton conf_val_coord = new JButton("Confirmar\nValidación", dao.getCheck());
				conf_val_coord.setBackground(new Color(160, 255, 147));
				conf_val_coord.setEnabled(false);
				add_val.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent arg0){
						if(!lista_val_coord.isSelectionEmpty()){
							lista_val.addAll(lista_val_coord.getSelectedValuesList());
							lista_val_coord.getSelectedValuesList().forEach(sel -> {
								modelo_selec_val_coord.addElement(sel);
								modelo_lista_val_coord.removeElement(sel);
							});
							conf_val_coord.setEnabled(true);
						}
					}
				});
			
			//**************************************
			//**************************************
			DefaultListModel<String> modelo_selec_anul_coord = new DefaultListModel<String>();
			JList<String> selec_anul_coord = new JList<String>(modelo_selec_anul_coord);
				selec_val_coord.setBackground(Color.WHITE);
				selec_val_coord.setFont(font);
			JScrollPane sp_selec_anul_coord = CustomScrollBar.generar(selec_anul_coord, dim, Color.ORANGE);
				sp_selec_anul_coord.setBorder(def_border);

			JButton add_anul = new JButton(dao.getAdd());
				add_anul.setBackground(new Color(255, 204, 96));
				add_anul.setBorder(BorderFactory.createLineBorder(Color.black, 2));
			
			JButton conf_anul_coord = new JButton("Confirmar\nAnulación", dao.getCheck());
				conf_anul_coord.setBackground(new Color(160, 255, 147));
				conf_anul_coord.setEnabled(false);
				add_anul.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						if(!lista_anul_coord.isSelectionEmpty()){
							lista_anul.addAll(lista_anul_coord.getSelectedValuesList());
							lista_anul_coord.getSelectedValuesList().forEach(s -> {
								modelo_selec_anul_coord.addElement(s);
								modelo_lista_anul_coord.removeElement(s);
							});
							conf_anul_coord.setEnabled(true);
						}
					}
				});
			
			JButton clear = new JButton("Eliminar dias", dao.getBorrar());
				clear.setBackground(Color.WHITE);
				clear.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
				    	String sel = (String) empCB_coord.getSelectedItem();
				    	Pattern pat = Pattern.compile("(\\(.+?\\))");
				    	Matcher mat = pat.matcher(sel);
				    	if(mat.find()){
				    		solicitante = mat.group(1).substring(1,  8);
				    	}
				    	List<String> lista = new ArrayList<String>(modelo_selec_val_coord.getSize());
				    	for(int i = 0 ; i < modelo_selec_val_coord.getSize(); i++)
				    		lista.add(modelo_selec_val_coord.get(i));
				    	if(!lista.isEmpty())
					    	if(JOptionPane.showConfirmDialog(null, 
					    			"¿Desea eliminar los días indicados del usuario " + sel + "?", 
					    			"Confirmar", JOptionPane.YES_NO_OPTION) == 0){
					    		dao.eliminarDias(lista, solicitante);
					    		modelo_selec_val_coord.removeAllElements();
					    		cargarPendientesCoord();
					    	}
				    	else
				    		JOptionPane.showMessageDialog(null, "Seleccione algún día");
					    		
					}
				});
				clear.addMouseListener(new MouseListener(){
					public void mouseClicked(MouseEvent arg0) {}
					@Override
					public void mouseEntered(MouseEvent arg0) {
						sp_selec_val_coord.setBorder(
								BorderFactory.createMatteBorder(3, 3, 3, 3, Color.green));
					}
					@Override
					public void mouseExited(MouseEvent arg0) {
						sp_selec_val_coord.setBorder(def_border);
					}
					public void mousePressed(MouseEvent arg0) {}
					public void mouseReleased(MouseEvent arg0){}
				});
			
			//**************************************
			empCB_coord.addActionListener(new ActionListener () {
			    public void actionPerformed(ActionEvent e) {
			    	if(empCB_coord.getSelectedItem() != null && 
			    			!empCB_coord.getSelectedItem().equals("*****")){
			    		modelo_lista_val_coord.removeAllElements();
			    		modelo_lista_anul_coord.removeAllElements();
				    	String sel = (String) empCB_coord.getSelectedItem();
				    	Pattern pat = Pattern.compile("(\\(.+?\\))");
				    	Matcher mat = pat.matcher(sel);
				    	if(mat.find()){
				    		solicitante = mat.group(1).substring(1,  8);
				    	}
				    	Set<LocalDate> fechas = dao.getPending(solicitante, "OkPendiente", false);
				    	if(!fechas.isEmpty())
					    	fechas.forEach(f -> modelo_lista_val_coord.addElement(fmt.format(f)));
				    	Set<LocalDate> fechas1 = dao.getPending(solicitante, "KoPendiente", false);
				    	if(!fechas1.isEmpty())
				    		fechas1.forEach(f -> modelo_lista_anul_coord.addElement(fmt.format(f)));
			    	}
			    }
			});
			
			conf_val_coord.addActionListener(new ActionListener(){
				public void actionPerformed (ActionEvent e){
					modelo_selec_val_coord.clear();
					dao.confirmCoord(lista_val, solicitante);
					String mail = dao.getYo().getCoordinador().replace(" ", "%20");
					sendMail(mail, lista_val, "aprobación");
					lista_val.clear();
					modelo_lista_val_coord.clear();
					modelo_lista_anul_coord.clear();
					cargarPendientesCoord();
					cargarPendientesResp();
				}
			});
			conf_anul_coord.addActionListener(new ActionListener(){
				public void actionPerformed (ActionEvent e){
					modelo_selec_anul_coord.clear();
					dao.confirmCoord(lista_anul, solicitante);
					String mail = dao.getYo().getCoordinador().replace(" ", "%20");
					sendMail(mail, lista_anul, "cancelación");
					lista_anul.clear();
					modelo_lista_anul_coord.clear();
					modelo_lista_val_coord.clear();
					cargarPendientesCoord();
					cargarPendientesResp();

				}
			});
			
			c.gridx = 0;
			c.gridy = 1;
			c.gridheight = 1;
			c.gridwidth = 25;
			add(label, c);
			c.gridx = 0;
			c.gridy = 3;
			c.gridheight = 1;
			c.gridwidth = 25;
			add(empCB_coord, c);
			
			c.gridx = 1;
			c.gridy = 7;
			c.gridheight = 7;
			c.gridwidth = 7;
			add(sp_val_coord, c);
			c.gridx = 8;
			c.gridy = 9;
			c.gridheight = 1;
			c.gridwidth = 1;
			add(add_val, c);
			c.gridx = 8;
			c.gridy = 11;
			c.gridheight = 1;
			c.gridwidth = 1;
			add(conf_val_coord, c);
			c.gridx = 9;
			c.gridy = 7;
			c.gridheight = 7;
			c.gridwidth = 7;
			add(sp_selec_val_coord, c);
			
			c.gridx = 1;
			c.gridy = 15;
			c.gridheight = 7;
			c.gridwidth = 7;
			add(sp_anul_coord, c);
			c.gridx = 8;
			c.gridy = 17;
			c.gridheight = 1;
			c.gridwidth = 1;
			add(add_anul, c);
			c.gridx = 8;
			c.gridy = 19;
			c.gridheight = 1;
			c.gridwidth = 1;
			add(conf_anul_coord, c);
			c.gridx = 9;
			c.gridy = 15;
			c.gridheight = 7;
			c.gridwidth = 7;
			add(sp_selec_anul_coord, c);
			
			c.gridx = 8;
			c.gridy = 22;
			c.gridheight = 1;
			c.gridwidth = 1;
			add(clear, c);
		}
		
		void cargarPendientesCoord(){
			pend_coord.clear();
			pend_coord.addAll(dao.getValidar(false));
			modelo_coord.removeAllElements();
			modelo_coord.addElement("*****");
			for(String s : pend_coord){
				modelo_coord.addElement(s);
 			}
			empCB_coord.setSelectedItem("*****");
		}
		
		public void sendMail(String destino, List<String> fechas, String tipo){
			
			String cuerpo = "El%20usuario%20" + dao.getUsuario() + "%20solicita%20la%20" + tipo + "%20de%20"
					+ "los%20siguientes%20días%20de%20vacaciones%3A%0A";
			for(String s : fechas){
				cuerpo += "%20%20%20" + s;
				cuerpo += "%0A";
			}
			String asunto = "SOLICITUD%20VACACIONES";
			
			try {
				Desktop desktop = Desktop.getDesktop(); 
			    String message = String.format("mailto:%s?subject=%s&body=%s",
			    		destino, asunto, cuerpo); 
			    URI uri = URI.create(message);
			    desktop.mail(uri);
			    JOptionPane.showMessageDialog(null, "ENVÍE EL CORREO");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void addFormR (){
			
			//**************************************
			//**************************************
			font = new Font("Bankia", Font.BOLD, 18);
			
			DefaultListModel<String> modelo_lista_val_resp = new DefaultListModel<String>();
			JList<String> lista_val_resp = new JList<String>(modelo_lista_val_resp);
			JScrollPane sp_val_resp = CustomScrollBar.generar(lista_val_resp, dim, Color.ORANGE);
				sp_val_resp.setBorder(def_border);
			List<String> lista_val = new ArrayList<String>();
			
			DefaultListModel<String> modelo_lista_anul_resp = new DefaultListModel<String>();
			JList<String> lista_anul_resp = new JList<String>(modelo_lista_anul_resp);
			JScrollPane sp_anul_resp = CustomScrollBar.generar(lista_anul_resp, dim, Color.ORANGE);
				sp_anul_resp.setBorder(def_border);
			List<String> lista_anul = new ArrayList<String>();
			
			JLabel labelr = new JLabel("Aprobación como responsable");
			labelr.setBackground(Color.WHITE);
			labelr.setFont(font);
			empCB_resp.setBackground(Color.WHITE);
			
			//**************************************
			//**************************************
			cargarPendientesResp();
			//**************************************
			//**************************************
			DefaultListModel<String> modelo_selec_val_resp = new DefaultListModel<String>();
			JList<String> selec_val_resp = new JList<String>(modelo_selec_val_resp);
				selec_val_resp.setBackground(Color.WHITE);
				selec_val_resp.setFont(font);
			JScrollPane sp_selec_val_resp = CustomScrollBar.generar(selec_val_resp, dim, Color.ORANGE);
				sp_selec_val_resp.setBorder(def_border);
				
			JButton add_val = new JButton(dao.getAdd());
				add_val.setBackground(new Color(255, 204, 96));
				add_val.setBorder(BorderFactory.createLineBorder(Color.black, 2));
			
			JButton conf_val_resp = new JButton("Confirmar\nValidación", dao.getCheck());
				conf_val_resp.setBackground(new Color(160, 255, 147));
				conf_val_resp.setEnabled(false);
				add_val.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						if(!lista_val_resp.isSelectionEmpty()){
							lista_val.addAll(lista_val_resp.getSelectedValuesList());
							for(String s : lista_val_resp.getSelectedValuesList()){
								modelo_selec_val_resp.addElement(s);
								modelo_lista_val_resp.removeElement(s);
							}
							conf_val_resp.setEnabled(true);
						}
					}
				});
			
			//**************************************
			//**************************************
			DefaultListModel<String> mod_anul_selec_resp = new DefaultListModel<String>();
			JList<String> selec_anul_resp = new JList<String>(mod_anul_selec_resp);
				selec_val_resp.setBackground(Color.WHITE);
				selec_val_resp.setFont(font);
			JScrollPane sp_selec_anul_resp = CustomScrollBar.generar(selec_anul_resp, dim, Color.ORANGE);
				sp_selec_anul_resp.setBorder(def_border);
					
			JButton add_anul = new JButton(dao.getAdd());
				add_anul.setBackground(new Color(255, 204, 96));
				add_anul.setBorder(BorderFactory.createLineBorder(Color.black, 2));
			
			JButton conf_anul_resp = new JButton("Confirmar\nAnulación", dao.getCheck());
				conf_anul_resp.setBackground(new Color(160, 255, 147));
				conf_anul_resp.setEnabled(false);
				add_anul.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						if(!lista_anul_resp.isSelectionEmpty()){
							lista_anul.addAll(lista_anul_resp.getSelectedValuesList());
							for(String s : lista_anul_resp.getSelectedValuesList()){
								mod_anul_selec_resp.addElement(s);
								modelo_lista_anul_resp.removeElement(s);
							}
							conf_anul_resp.setEnabled(true);
						}
					}
				});
				
			//**************************************
				
			JButton clear = new JButton("Eliminar dias", dao.getBorrar());
				clear.setBackground(Color.WHITE);
				clear.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
				    	String sel = (String) empCB_resp.getSelectedItem();
				    	Pattern pat = Pattern.compile("(\\(.+?\\))");
				    	Matcher mat = pat.matcher(sel);
				    	if(mat.find()){
				    		solicitante = mat.group(1).substring(1,  8);
				    	}
				    	List<String> lista = new ArrayList<String>(modelo_selec_val_resp.getSize());
				    	for(int i = 0 ; i < modelo_selec_val_resp.getSize(); i++)
				    		lista.add(modelo_selec_val_resp.get(i));
				    	if(JOptionPane.showConfirmDialog(null, 
				    			"¿Desea eliminar los días indicados del usuario " + sel + "?", 
				    			"Confirmar", JOptionPane.YES_NO_OPTION) == 0){
				    		dao.eliminarDias(lista, solicitante);
				    		modelo_selec_val_resp.removeAllElements();
				    		cargarPendientesResp();
				    	}
					}
				});
				clear.addMouseListener(new MouseListener(){
					public void mouseClicked(MouseEvent arg0) {}
					@Override
					public void mouseEntered(MouseEvent arg0) {
						sp_selec_val_resp.setBorder(
								BorderFactory.createMatteBorder(3, 3, 3, 3, Color.green));
					}
					@Override
					public void mouseExited(MouseEvent arg0) {
						sp_selec_val_resp.setBorder(def_border);
					}
					public void mousePressed(MouseEvent arg0) {}
					public void mouseReleased(MouseEvent arg0){}
				});

			//**************************************
			empCB_resp.addActionListener(new ActionListener () {
			    public void actionPerformed(ActionEvent e) {
			    	if(empCB_resp.getSelectedItem() != null &&
			    			!empCB_resp.getSelectedItem().equals("*****")){
				    	modelo_lista_val_resp.removeAllElements();
				    	modelo_lista_anul_resp.removeAllElements();
				    	String sel = (String) empCB_resp.getSelectedItem();
				    	Pattern pat = Pattern.compile("(\\(.+?\\))");
				    	Matcher mat = pat.matcher(sel);
				    	if(mat.find()){
				    		solicitante = mat.group(1).substring(1,  8);
				    	}
				    	
				    	Set<LocalDate> fechas_val = dao.getPending(solicitante, "OkPendiente", true);
				    	if(!fechas_val.isEmpty()){
				    		fechas_val.forEach(f -> modelo_lista_val_resp.addElement(fmt.format(f)));
				    	}
				    	
				    	Set<LocalDate> fechas_anul = dao.getPending(solicitante, "KoPendiente", true);
				    	if(!fechas_anul.isEmpty()){
				    		fechas_anul.forEach(f -> modelo_lista_anul_resp.addElement(fmt.format(f)));
				    	}
			    	} else{
			    		modelo_lista_val_resp.clear();
			    		modelo_lista_anul_resp.clear();
			    	}
			    }
			});
			
			conf_val_resp.addActionListener(new ActionListener(){
				public void actionPerformed (ActionEvent e){
					modelo_selec_val_resp.clear();
					dao.confirmResp(lista_val, solicitante);
					lista_val.clear();
					cargarPendientesCoord();
					cargarPendientesResp();
					modelo_lista_val_resp.clear();
					modelo_lista_anul_resp.clear();
				}
			});
			
			conf_anul_resp.addActionListener(new ActionListener(){
				public void actionPerformed (ActionEvent e){
					mod_anul_selec_resp.clear();
					dao.anulResp(lista_anul, solicitante);
					lista_anul.clear();
					modelo_lista_anul_resp.clear();
					modelo_lista_val_resp.clear();
					cargarPendientesCoord();
					cargarPendientesResp();
				}
			});

			c.gridx = 0;
			c.gridy = 24;
			c.gridheight = 1;
			c.gridwidth = 25;
			add(labelr, c);
			c.gridx = 0;
			c.gridy = 26;
			c.gridheight = 1;
			c.gridwidth = 25;
			add(empCB_resp, c);
			
			c.gridx = 1;
			c.gridy = 30;
			c.gridheight = 7;
			c.gridwidth = 7;
			add(sp_val_resp, c);
			c.gridx = 8;
			c.gridy = 32;
			c.gridheight = 1;
			c.gridwidth = 1;
			add(add_val, c);
			c.gridx = 8;
			c.gridy = 34;
			c.gridheight = 1;
			c.gridwidth = 1;
			add(conf_val_resp, c);
			c.gridx = 9;
			c.gridy = 30;
			c.gridheight = 7;
			c.gridwidth = 7;
			add(sp_selec_val_resp, c);
			
			c.gridx = 1;
			c.gridy = 38;
			c.gridheight = 7;
			c.gridwidth = 7;
			add(sp_anul_resp, c);
			c.gridx = 8;
			c.gridy = 40;
			c.gridheight = 1;
			c.gridwidth = 1;
			add(add_anul, c);
			c.gridx = 8;
			c.gridy = 42;
			c.gridheight = 1;
			c.gridwidth = 1;
			add(conf_anul_resp, c);
			c.gridx = 9;
			c.gridy = 38;
			c.gridheight = 7;
			c.gridwidth = 7;
			add(sp_selec_anul_resp, c);
			
			c.gridx = 8;
			c.gridy = 45;
			c.gridheight = 1;
			c.gridwidth = 1;
			add(clear, c);
		}
		
		void cargarPendientesResp(){
			pend_resp.clear();
			pend_resp.addAll(dao.getValidar(true));
			modelo_resp.removeAllElements();
			modelo_resp.addElement("*****");
 			for(String s : pend_resp){
 				modelo_resp.addElement(s);
 			}
			empCB_resp.setSelectedItem("*****");
		}
	}
}
