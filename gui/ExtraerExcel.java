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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;

import varios.acciones.MoveMouseListener;
import varios.dao.DAO;
import varios.dao.DAOexcel;
import varios.misc.OtherDatesMain;

public class ExtraerExcel implements Runnable {
	
	private DAO dao = DAO.getInstance();
	private GridBagConstraints c = null;
	private Font font = null;
	private Map<String, List<Set<String>>> listaCalendarios;
	
	public ExtraerExcel(Map<String, List<Set<String>>> listaCals){
		this.listaCalendarios = listaCals;
	}
	
	public void run(){
		
		Extraer extraer = new Extraer();
		
		GridBagLayout gbl = new GridBagLayout();
		extraer.setLayout(gbl);
		c = new GridBagConstraints();
		c.insets = new Insets(10, 5, 10, 5);
		extraer.getContentPane().setBackground(Color.WHITE);
		extraer.setUndecorated(true);
		extraer.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.ORANGE));
		
		extraer.addMenuBar();
		extraer.addForm();
		
		extraer.pack();
		extraer.setVisible(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		extraer.setLocation(dim.width/2-extraer.getSize().width/2, dim.height/2-extraer.getSize().height/2);

	}
	
	class Extraer extends JFrame{
		
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
				label1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 200));

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
				}
			});
		}
		
		public void addForm(){
			
			font = new Font("Bankia", Font.BOLD, 16);
			
			JLabel titulo = new JLabel("EXTRAER EXCEL");
			titulo.setFont(new Font("Bankia", Font.BOLD, 36));
			titulo.setForeground(Color.ORANGE);
			c.gridx = 0;
			c.gridy = 0;
			c.gridheight = 5;
			c.gridwidth = 20;
			add(titulo, c);
			
			JLabel li = new JLabel("Fecha inicio:");
			li.setBackground(Color.WHITE);
			li.setFont(font);
			c.gridx = 0;
			c.gridy = 5;
			c.gridheight = 2;
			c.gridwidth = 10;
			add(li, c);
			JDateChooser inicio = new JDateChooser();
			JCalendar ca = inicio.getJCalendar();
				ca.setPreferredSize(new Dimension(220, 220));
				ca.setTodayButtonVisible(true);
				ca.setTodayButtonText("Hoy");
				ca.setNullDateButtonVisible(true);
				ca.setNullDateButtonText("Borrar");
				ca.setBackground(Color.WHITE);
			inicio.setDate(Date.valueOf(LocalDate.now()));
				ca.getDayChooser().addDateEvaluator(new OtherDatesMain());
				ca.setWeekdayForeground(Color.BLACK);
				ca.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				ca.setDecorationBordersVisible(true);
			c.gridx = 10;
			c.gridy = 5;
			c.gridheight = 2;
			c.gridwidth = 10;
			add(inicio, c);
			
			JLabel lf = new JLabel("Fecha final:");
			lf.setBackground(Color.WHITE);
			lf.setFont(font);
			c.gridx = 0;
			c.gridy = 8;
			c.gridheight = 2;
			c.gridwidth = 10;
			add(lf, c);
			JDateChooser fin = new JDateChooser();
			JCalendar ca1 = fin.getJCalendar();
				ca1.setPreferredSize(new Dimension(220, 220));
				ca1.setTodayButtonVisible(true);
				ca1.setTodayButtonText("Hoy");
				ca1.setNullDateButtonVisible(true);
				ca1.setNullDateButtonText("Borrar");
				ca1.setBackground(Color.WHITE);
			fin.setDate(Date.valueOf(LocalDate.now()));
				ca1.getDayChooser().addDateEvaluator(new OtherDatesMain());
				ca1.setWeekdayForeground(Color.BLACK);
				ca1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				ca1.setDecorationBordersVisible(true);
			c.gridx = 10;
			c.gridy = 8;
			c.gridheight = 2;
			c.gridwidth = 10;
			add(fin, c);
			
			JButton confirmar = new JButton("Confirmar");
			confirmar.setBackground(Color.WHITE);
			confirmar.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK), 
					BorderFactory.createEmptyBorder(10, 20, 10, 20)));
			confirmar.setFont(font);
			c.gridx = 0;
			c.gridy = 20;
			c.gridheight = 2;
			c.gridwidth = 20;
			add(confirmar, c);
			confirmar.addMouseListener(new MouseListener(){
				public void mouseClicked(MouseEvent e){
					if(fin.getDate().after(inicio.getDate()) && !fin.getDate().equals(inicio.getDate())){
						DAOexcel.extraerExcel(
								inicio.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), 
								fin.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), 
								dao.getListaEquipo(), listaCalendarios);
					}
					confirmar.setMultiClickThreshhold(2000);
				}
				public void mouseEntered(MouseEvent e){
					if(fin.getDate().after(inicio.getDate()) && !fin.getDate().equals(inicio.getDate())){
						confirmar.setBackground(new Color(111, 255, 86));
						confirmar.setBorder(BorderFactory.createCompoundBorder(
								BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK), 
								BorderFactory.createEmptyBorder(10, 20, 10, 20)));
					} else{
						confirmar.setText("Incorrecto");
						confirmar.setBackground(new Color(255, 68, 68));
						confirmar.setBorder(BorderFactory.createCompoundBorder(
								BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK), 
								BorderFactory.createEmptyBorder(10, 20, 10, 20)));
					}
				}
				public void mouseExited(MouseEvent evt) {
					if(fin.getDate().before(inicio.getDate()) || fin.getDate().equals(inicio.getDate())){
						confirmar.setText("Confirmar");
					}
					confirmar.setBackground(Color.WHITE);
					confirmar.setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK), 
							BorderFactory.createEmptyBorder(10, 20, 10, 20)));
			    }
				public void mousePressed(MouseEvent arg0) {}
				public void mouseReleased(MouseEvent arg0) {}
			});
		}
	}
}
