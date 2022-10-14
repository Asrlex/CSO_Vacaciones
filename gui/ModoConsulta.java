package gui.admin;

import java.awt.Color;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import varios.acciones.MoveMouseListener;
import varios.components.CustomScrollBar;
import varios.dao.DAO;
import varios.objetos.Empleado;

/**
 * Modo consulta
 * @author A194855
 */
public class ModoConsulta implements Runnable{
	
	private Font font = new Font("Bankia", Font.BOLD, 20);
	private GridBagConstraints c;
	private Dimension dim = new Dimension(500, 400);
	private Font font1 = new Font("Bankia", Font.BOLD, 15);
	private Set<LocalDate> fechas = new HashSet<>(), fechas2 = new HashSet<>();
	private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private JFrame parent;
	private DAO dao = DAO.getInstance();
	private List<Empleado> subs = dao.getListaEquipo();
	private DefaultListModel<String> model = new DefaultListModel<String>(), 
			model1 = new DefaultListModel<String>(), 
			model2 = new DefaultListModel<String>();
	

	public ModoConsulta (JFrame parent){
		this.parent = parent;
	}
	
	public void run(){
		Mc mc = new Mc();
		mc.llenarListas();
		
		GridBagLayout gbl = new GridBagLayout();
		mc.setLayout(gbl);
		c = new GridBagConstraints();
		c.insets = new Insets(5, 10, 10, 10);
		mc.getContentPane().setBackground(Color.WHITE);
		mc.setUndecorated(true);
		mc.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.ORANGE));

		mc.addMenuBar();

		mc.addValid();

		mc.addAnnul();

		mc.addReady();
		
		mc.pack();
		mc.setVisible(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		mc.setLocation(dim.width/2-mc.getSize().width/2, dim.height/2-mc.getSize().height/2);
	}
	
	class Mc extends JFrame{
		
		private static final long serialVersionUID = 1L;

		public void addMenuBar(){
			JMenuBar bar = new JMenuBar();
				bar.setLayout(new GridBagLayout());
				bar.setBackground(Color.WHITE);
			font = new Font("Bankia", Font.BOLD, 12);
			
			JButton exit = new JButton("", dao.getSalir());
				exit.setContentAreaFilled(false);
				exit.setFont(font);
				exit.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			
			JButton min = new JButton("", dao.getMinimizar());
				min.setContentAreaFilled(false);
				min.setFont(font);
				min.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));
				
			JLabel label1 = new JLabel ();
				label1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1400));

			MoveMouseListener.generar(bar);
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
		
		/**
		 * ****************************************************
		 * Añade la caja de texto con las fechas pendientes de validar
		 */
		public void addValid(){
			
			GridLayout grid = new GridLayout(0, 1, 3, 3);
			
			JPanel validation = new JPanel();
			validation.setBackground(Color.WHITE);
			validation.setLayout(grid);
			
			JLabel validLabel = new JLabel("Pendientes de validación");
			validLabel.setBackground(Color.WHITE);
			validLabel.setFont(font);
			c.gridx = 0;
			c.gridy = 0;
			c.gridheight = 1;
			c.gridwidth = 4;
			add(validLabel, c);
			
			JList<String> lista = new JList<String>();
				lista.setModel(model);
				lista.setBackground(Color.WHITE);
				lista.setFont(font1);
				lista.setForeground(new Color(255, 161, 0));
			c.gridx = 0;
			c.gridy = 1;
			c.gridheight = 6;
			c.gridwidth = 4;
			add(CustomScrollBar.generar(lista, dim, Color.ORANGE), c);
		}
		
		/**
		 * ****************************************************
		 * Añade la caja de texto con las fechas pendientes de anular
		 */
		public void addAnnul(){
			
			JPanel annulation = new JPanel();
			annulation.setBackground(Color.WHITE);
			GridLayout grid = new GridLayout(0, 1, 3, 3);
			annulation.setLayout(grid);
			
			JLabel annulLabel = new JLabel("Pendientes de Anulación");
			annulLabel.setBackground(Color.WHITE);
			annulLabel.setFont(font);
			c.gridx = 5;
			c.gridy = 0;
			c.gridheight = 1;
			c.gridwidth = 4;
			add(annulLabel, c);
			
			JList<String> lista = new JList<String>();
				lista.setModel(model1);
				lista.setBackground(Color.WHITE);
				lista.setFont(font1);
				lista.setForeground(new Color(255, 76, 76));
			c.gridx = 5;
			c.gridy = 1;
			c.gridheight = 6;
			c.gridwidth = 4;
			add(CustomScrollBar.generar(lista, dim, Color.ORANGE), c);
		}
		
		/**
		 * ****************************************************
		 * Añade la tabla con las fechas ya aprobadas
		 */
		public void addReady(){
			
			JPanel ready = new JPanel();
			ready.setBackground(Color.WHITE);
			GridLayout grid = new GridLayout(0, 1, 3, 3);
			ready.setLayout(grid);
			
			JLabel readyLabel = new JLabel("Días aprobados");
			readyLabel.setBackground(Color.WHITE);
			readyLabel.setFont(font);
			c.gridx = 10;
			c.gridy = 0;
			c.gridheight = 1;
			c.gridwidth = 4;
			add(readyLabel, c);
			
			JList<String> lista = new JList<String>();
				lista.setModel(model2);
				lista.setBackground(Color.WHITE);
				lista.setFont(font1);
				lista.setForeground(new Color(34, 216, 2));
			c.gridx = 10;
			c.gridy = 1;
			c.gridheight = 6;
			c.gridwidth = 4;
			add(CustomScrollBar.generar(lista, dim, Color.ORANGE), c);
		}
		
		public void llenarListas(){
			
			for(Empleado e : subs){
				
				fechas = dao.getApproved(e.getUsuario(), 0);
				if(!fechas.isEmpty())
					model2.addElement("- " + e.getNombre() + " (" + e.getUsuario() + ")" + ":");
				for(LocalDate d : fechas){
					model2.addElement("\n\t       **" + fmt.format(d) + "**");
				}
				fechas.clear();
				
				fechas = dao.getPending(e.getUsuario(), "KoPendiente", false);
				fechas2 = dao.getPending(e.getUsuario(), "KoPendiente", true);
				if(!fechas.isEmpty() || !fechas2.isEmpty())
					model1.addElement("- " + e.getNombre() + " (" + e.getUsuario() + ")" + ":");
				if(!fechas.isEmpty())
					fechas.forEach(f -> model1.addElement("\n\t       **" + fmt.format(f) + "**"));
				if(!fechas2.isEmpty())
					fechas2.forEach(f -> model1.addElement("\n\t       **" + fmt.format(f) + "**"));				fechas.clear();
				fechas2.clear();
				
				fechas = dao.getPending(e.getUsuario(), "OkPendiente", false);
				fechas2 = dao.getPending(e.getUsuario(), "OkPendiente", true);
				if(!fechas.isEmpty() || !fechas2.isEmpty())
					model.addElement("- " + e.getNombre() + " (" + e.getUsuario() + ")" + ":");
				if(!fechas.isEmpty())
					fechas.forEach(f -> model.addElement("\n\t       **" + fmt.format(f) + "**"));
				if(!fechas2.isEmpty())
					fechas2.forEach(f -> model.addElement("\n\t       **" + fmt.format(f) + "**"));
				fechas.clear();
				fechas2.clear();
			}
		}
	}
}
