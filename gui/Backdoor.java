package gui.admin;

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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JTextField;

import gui.GUI;
import varios.acciones.MoveMouseListener;
import varios.dao.DAO;


/**
 * Da de baja a un empleado
 * @author A194855 - ALEJANDRO SÁNCHEZ RODRIGUEZ
 */
public class Backdoor implements Runnable{
	
	private Font font;
	private GridBagConstraints c;
	private JFrame parent;
	private JFrame current;
	private DAO dao = DAO.getInstance();
	
	public Backdoor(JFrame current) {
		this.parent = current;
	}

	public void run() {
		Be be = new Be();
		
		//---Barra de tareas---
		be.addMenuBar();
		
		//---PROPIEDADES GUI---
		GridBagLayout gbl = new GridBagLayout();
		be.setLayout(gbl);
		c = new GridBagConstraints();
		c.insets = new Insets(20, 5, 20, 5);
		be.getContentPane().setBackground(Color.WHITE);
		be.setUndecorated(true);
		be.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.ORANGE));
		
		//---Paneles---
		be.addForm();
		
		be.pack();
		be.setVisible(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		be.setLocation(dim.width/2-be.getSize().width/2, dim.height/2-be.getSize().height/2);
		current = be;
		parent.setVisible(false);
	}
	
	public class Be extends JFrame{
		
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
				label1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 250));

			
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
		
		public void addForm(){
			
			JLabel label = new JLabel ("Código del usuario: ");
			JTextField tf = new JTextField();
			tf.setColumns(7);
			add(label);
			JButton button = new JButton("OK");
			button.setBackground(new Color(119, 255, 128));
			this.getRootPane().setDefaultButton(button);
			button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					dao.setUsuario(tf.getText());
					dao.cargarDatos();
					new Thread(new GUI()).start();
					parent.dispose();
					current.dispose();
				}
			});
			
			c.gridx = 1;
			c.gridy = 1;
			c.gridheight = 2;
			c.gridwidth = 4;
			add(label, c);
			c.gridx = 6;
			c.gridy = 1;
			c.gridheight = 2;
			c.gridwidth = 6;
			add(tf, c);
			c.gridx = 4;
			c.gridy = 4;
			c.gridheight = 1;
			c.gridwidth = 1;
			add(button, c);
		}
	}
}
