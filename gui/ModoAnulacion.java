package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
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
import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.tinylog.Logger;

import com.toedter.calendar.JCalendar;

import gui.GUI.Vacaciones;
import varios.acciones.MoveMouseListener;
import varios.dao.DAO;
import varios.misc.OtherDatesAnulacion;

/**
 * Modo selección de vacaciones
 * @author A194855
 *
 */
public class ModoAnulacion implements Runnable{
	
	private Font font;
	private GridBagConstraints c;
	private Vacaciones parent;
	private DAO dao = DAO.getInstance();

	public ModoAnulacion (Vacaciones parent){
		this.parent = parent;
	}
	
	/**
	 * ****************************************************
	 * Método activo
	 */
	public void run(){
		
		Ma ma = new Ma();
		
		//---PROPIEDADES GUI---
		GridBagLayout gbl = new GridBagLayout();
		ma.setLayout(gbl);
		c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 10, 10);
		ma.getContentPane().setBackground(Color.WHITE);
		ma.setUndecorated(true);
		ma.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.ORANGE));
		
		//---Barra de tareas---
		ma.addMenuBar();
		
		//---Calendario---
		ma.addCalendar();
		
		ma.pack();
		ma.setVisible(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		ma.setLocation(dim.width/2-ma.getSize().width/2, dim.height/2-ma.getSize().height/2);
	}
	
	class Ma extends JFrame{

		private static final long serialVersionUID = 1L;
		
		/**
		 * ****************************************************
		 * Añade un menú con botones de ayuda en la parte superior de la ventana
		 */
		private void addMenuBar(){
			
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
				label1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 500));

				
			//Add items to menu bar
			MoveMouseListener.generar(bar);
			bar.add(Box.createHorizontalGlue());
			bar.add(label1, c);
			c.gridx = 26;
			c.gridy = 0;
			c.gridheight = 1;
			c.gridwidth = 2;
			bar.add(min, c);
			c.gridx = 28;
			c.gridy = 0;
			c.gridheight = 1;
			c.gridwidth = 2;
			bar.add(exit, c);
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
		 * Añade un calendario para elegir fechas
		 */
		public void addCalendar (){
			
			font = new Font("Bankia", Font.BOLD, 15);
			DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
			
			JPanel panel = new JPanel();
			panel.setBackground(Color.white);
			panel.setLayout(new BorderLayout());
			panel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(10, 30, 10, 25), 
					BorderFactory.createMatteBorder(2,  2,  2,  2,  Color.orange)));
			JCalendar cal = new JCalendar();
			cal.setTodayButtonVisible(true);
			cal.setTodayButtonText("Hoy");
			cal.setNullDateButtonVisible(true);
			cal.setNullDateButtonText("Borrar");
			cal.setBackground(Color.WHITE);
			cal.getDayChooser().addDateEvaluator(new OtherDatesAnulacion());
			cal.setWeekdayForeground(Color.BLACK);
			cal.setDecorationBordersVisible(true);
			cal.setPreferredSize(new Dimension(350, 350));
			panel.add(cal, BorderLayout.NORTH);
			c.gridx = 0;
			c.gridy = 0;
			c.gridheight = 10;
			c.gridwidth = 10;
			add(panel, c);
			
			JPanel select = new JPanel();
			select.setBackground(Color.white);
			select.setLayout(new GridLayout(0, 1, 3, 3));
			select.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 30));
			JLabel label = new JLabel("Año computable:");
			label.setFont(font);
			label.setBackground(Color.white);
			select.add(label);
			
			String[] años = new String[]{"----", "2019", "2020", "2021"};
			JComboBox<String> cb = new JComboBox<String>(años);
			cb.setBackground(Color.WHITE);
			select.add(cb);
			c.gridx = 11;
			c.gridy = 0;
			c.gridheight = 3;
			c.gridwidth = 10;
			add(select, c);
			
			JButton add = new JButton("Añadir");
			add.setBackground(Color.WHITE);
			add.setFont(font);
			add.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder(0, 25, 0, 30), 
						BorderFactory.createCompoundBorder(
								BorderFactory.createMatteBorder(2,  2,  2,  2,  Color.BLACK), 
								BorderFactory.createEmptyBorder(5, 15, 5, 15))));
			c.gridx = 11;
			c.gridy = 3;
			c.gridheight = 1;
			c.gridwidth = 10;
			add(add, c);
			
			DefaultListModel<String> modelo = new DefaultListModel<String>();
			JList<String> text = new JList<String>(modelo);
				text.setBackground(Color.WHITE);
				text.setFont(font);
				
			JScrollPane sp = new JScrollPane(text);
			sp.setPreferredSize(new Dimension(200, 100));
			text.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 30));
			c.gridx = 11;
			c.gridy = 4;
			c.gridheight = 4;
			c.gridwidth = 10;
			add(sp, c);
			
			JPanel buttons = new JPanel();
			buttons.setBackground(Color.white);
			buttons.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 30));
			JButton empty = new JButton("Vaciar");
			empty.setBackground(Color.GRAY);
			empty.setFont(font);
			buttons.add(empty);
			
			JButton ok = new JButton("Confirmar");
			ok.setEnabled(false);
			ok.setBackground(new Color(119, 255, 128));
			ok.setFont(font);
			buttons.add(ok);
			
			c.gridx = 15;
			c.gridy = 8;
			c.gridheight = 1;
			c.gridwidth = 4;
			add(buttons, c);
			
			add.addActionListener(new ActionListener(){
				public void actionPerformed (ActionEvent e){
					if(!modelo.contains(df.format(cal.getDate()))){
						modelo.addElement(df.format(cal.getDate()));
						ok.setEnabled(true);
					} else{
						JOptionPane.showMessageDialog(null, "YA HA SELECCIONADO ESE DÍA");
					}
				}
			});
			
			empty.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					modelo.removeAllElements();
					ok.setEnabled(false);
				}
			});
			
			ok.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(!cb.getSelectedItem().equals("----")){
						List<String> fechas = new ArrayList<String>();
						for(int i = 0; i < modelo.getSize(); i++)
							fechas.add(modelo.get(i));
						dao.addDays(
								fechas, "KoPendiente", Integer.valueOf((String) cb.getSelectedItem()));
						String mail = dao.getYo().getUsuarioCoord() + "@nube.bankia.com";
						sendMail(mail, modelo.toArray());
						dispose();
						Logger.tag("ANULACION").info("SOLICITA DÍAS ANULADOS {}", modelo.toArray().toString());
						parent.setVisible(true);
						parent.actualizar();
					}
				}
			});
		}
		
		/**
		 * Manda un mail al superior del usuario 
		 * @param destino Dirección de correo destino
		 */
		public void sendMail(String destino, Object[] objects){
			
			String cuerpo = "El usuario " + dao.getYo().getNombre() + " (" + dao.getYo().getUsuario() +
					") solicita la anulación de los siguientes días%3A%0A";
			for(int i = 0; i < objects.length; i++){
				cuerpo += "%20%20%20" + objects[i];
				cuerpo += "%0A";
			}
			String asunto = "SOLICITUD%20ANULACIÓN%20VACACIONES";
			
			try {
				Desktop desktop = Desktop.getDesktop();
			    String message = String.format("mailto:%s?subject=%s&body=%s",
			    		destino, asunto, cuerpo.replace(" ", "%20")); 
			    URI uri = URI.create(message); 
			    desktop.mail(uri);
			    JOptionPane.showMessageDialog(null, "ENVÍE EL CORREO");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
