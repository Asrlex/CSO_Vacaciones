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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.tinylog.Logger;

import com.toedter.calendar.JCalendar;

import gui.GUI.Vacaciones;
import varios.acciones.MoveMouseListener;
import varios.dao.DAO;
import varios.misc.OtherDatesSeleccion;

/**
 * Modo selección de vacaciones
 * @author A194855
 *
 */
public class ModoSeleccion implements Runnable{
	
	private Font font;
	private GridBagConstraints c = new GridBagConstraints();
	private Vacaciones parent;
	private DAO dao = DAO.getInstance();
	private int restantes, yeardisp;
	
	public ModoSeleccion (Vacaciones parent, int restantes, int yeardisp){
		this.parent = parent;
		this.restantes = restantes;
		this.yeardisp = yeardisp;
	}
	
	public void run(){
		
		Ms ms = new Ms();
		
		//---PROPIEDADES GUI---
		GridBagLayout gbl = new GridBagLayout();
		c.insets = new Insets(10, 10, 10, 10);
		
		ms.setLayout(gbl);
		ms.getContentPane().setBackground(Color.WHITE);
		ms.setUndecorated(true);
		ms.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.ORANGE));
		
		//---Barra de tareas---
		ms.addMenuBar();
		//---Calendario---
		ms.addCalendar();
		
		ms.pack();
		ms.setVisible(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		ms.setLocation(dim.width/2-ms.getSize().width/2, dim.height/2-ms.getSize().height/2);
	}
	
	class Ms extends JFrame{

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
			cal.setBackground(Color.WHITE);
			cal.getDayChooser().addDateEvaluator(new OtherDatesSeleccion());
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
				JLabel label = new JLabel("Año computable");
					label.setFont(font);
					label.setBackground(Color.white);
				select.add(label);
				JTextField años = new JTextField();
					años.setEditable(false);
					años.setBackground(Color.WHITE);
					años.setText(String.valueOf(yeardisp));
				select.add(años);
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
					if(modelo.getSize() < restantes){
						if(!modelo.contains(df.format(cal.getDate()))){
							modelo.addElement(df.format(cal.getDate()));
							ok.setEnabled(true);
						} else
							JOptionPane.showMessageDialog(null, "YA HA SELECCIONADO ESE DÍA");
					} else
						JOptionPane.showMessageDialog(null, "NO LE QUEDAN MÁS DÍAS");
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
					List<String> fechas = new ArrayList<String>();
					for(int i = 0; i < modelo.getSize(); i++)
						fechas.add(modelo.get(i));
					dao.addDays(fechas, "OkPendiente", yeardisp);
					String mail = dao.getYo().getUsuarioCoord() + "@nube.bankia.com";
					sendMail(mail, modelo.toArray());
					dispose();
					Logger.tag("SELECCION").info("DÍAS PEDIDOS {}", modelo.toArray());
					parent.setVisible(true);
					parent.actualizar();
				}
			});
		}
		
		/**
		 * Manda un mail al superior del usuario 
		 * @param destino Dirección de correo destino
		 */
		public void sendMail(String destino, Object[] objects){
			
			String cuerpo = "El usuario " + dao.getYo().getNombre() + " (" + dao.getYo().getUsuario() +
					") solicita la aprobación de los siguientes días%3A%0A";
			for(int i = 0; i < objects.length; i++){
				cuerpo += "%20%20%20" + objects[i];
				cuerpo += "%0A";
			}
			String asunto = "SOLICITUD%20VACACIONES";
			
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
