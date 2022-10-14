package gui;

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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.border.Border;

import com.toedter.calendar.JCalendar;

import gui.admin.Backdoor;
import gui.admin.DiasPasados;
import gui.admin.ModoConsulta;
import gui.admin.VistaAdmin;
import varios.acciones.MoveMouseListener;
import varios.components.CustomScrollBar;
import varios.dao.DAO;
import varios.misc.OtherDatesMain;

public class GUI implements Runnable{
	
	private int restantes = 0, yeardisp = 0, diasdisp = 0;
	private Font font;
	private DAO dao;
	private Border border;
	private GridBagConstraints c = new GridBagConstraints();
	private static ScheduledExecutorService SExS = Executors.newScheduledThreadPool(1);
	private int permiso;
	private JPanel centro, tablas;
	private Vacaciones v;
	private Map<Integer, Integer> disponibles;
	private JTextField tfdias;
	private Runnable failsafeCheck;
	
	public GUI(){
		failsafeCheck = () -> {
			if(!DAO.checkFailsafe())
				System.exit(0);
		};
	}
	
	public void run (){
		if(!DAO.checkFailsafe()){
			JOptionPane.showMessageDialog(null, "Acceso denegado, aplicación en mantenimiento");
			System.exit(0);
		}
		dao = DAO.getInstance();
		permiso = dao.getPermiso();
		disponibles = dao.getDays();
		if(permiso < 0){
			JOptionPane.showMessageDialog(null, "Acceso denegado, contacte con Control y Reporting");
			System.exit(0);
		}
		DAO.control();
		
		v = new Vacaciones();
		v.addMenuBar();
		
		v.setBackground(Color.WHITE);
		GridBagLayout gbl = new GridBagLayout();
		v.setLayout(gbl);
		c.insets = new Insets(5, 5, 5, 5);
		v.getContentPane().setBackground(Color.WHITE);
		v.setUndecorated(true);
		v.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.ORANGE));
		
		v.addTop();
		v.addCalendar();
		v.addButtons();
		v.addTables();
		
		v.pack();
		v.setVisible(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		v.setLocation(dim.width/2 - v.getSize().width/2, dim.height/2 - v.getSize().height/2);
		
		SExS.scheduleAtFixedRate(failsafeCheck, 10, 120, TimeUnit.SECONDS);
		if(permiso > 1){
			List<String> t = dao.getValidar(true);
			t.addAll(dao.getValidar(false));
			if(!t.isEmpty())
				JOptionPane.showMessageDialog(null, "DÍAS PENDIENTES DE VALIDAR", "Validar", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	class Vacaciones extends JFrame{
		
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
				
			JMenuItem actualizar = new JMenuItem("Actualizar", dao.getActualizar());
				actualizar.setBackground(Color.white);
				actualizar.setFont(font);
				menu.add(actualizar);
				
			JMenuItem info = new JMenuItem("Ayuda", dao.getInfo());
				info.setBackground(Color.white);
				info.setFont(font);
				menu.add(info);
			
			JMenuItem pasado = new JMenuItem("Gestión días pasados", dao.getBorrar());
				pasado.setBackground(Color.white);
				pasado.setFont(font);
				if(permiso != 2)
					menu.add(pasado);
				
			JButton exit = new JButton(dao.getSalir());
				exit.setContentAreaFilled(false);
				exit.setFont(font);
				exit.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
			
			JButton min = new JButton(dao.getMinimizar());
				min.setContentAreaFilled(false);
				min.setFont(font);
				min.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));
			
			JLabel label1 = new JLabel(dao.getUsuario());
				label1.setForeground(Color.ORANGE);
				label1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 600));
			
			MoveMouseListener.generar(bar);
			bar.add(menu);
			bar.add(label1);
			bar.add(Box.createHorizontalGlue());
			bar.add(min);
			bar.add(exit);
			setJMenuBar(bar);
			
			actualizar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					actualizar();
				}
			});
			info.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					if(permiso == 2)mostrarInfoTecnico();
					else mostrarInfoAdmin();
				}
			});
			pasado.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					new Thread(new DiasPasados()).start();
				}
			});
			min.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					setState(Frame.ICONIFIED);
				}
			});
			exit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					SExS.shutdownNow();
					dispose();
					System.exit(0);
				}
			});
		}
		
		public void addTop(){
			
			JPanel top = new JPanel();
			top.setBackground(Color.WHITE);
			JLabel titulo = new JLabel("<html><div style='text-align: center;'>VACACIONES</div></html>");
			titulo.setFont(new Font("Bankia", Font.BOLD, 50));
			titulo.setForeground(Color.ORANGE);
			titulo.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 150));
			top.add(titulo);
			
			JLabel panel = new JLabel(dao.getLogo());
			panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 15));
			top.add(panel);
			
			c.gridx = 0;
			c.gridy = 0;
			c.gridheight = 2;
			c.gridwidth = 30;
			add(top, c);
		}
		
		public void addCalendar(){
		
			centro = new JPanel();
				centro.setBackground(Color.WHITE);
				centro.setLayout(new GridLayout(1, 0, 2, 2));
			
			JCalendar cal = new JCalendar();
				cal.setPreferredSize(new Dimension(300, 300));
				cal.setTodayButtonVisible(true);
				cal.setTodayButtonText("Hoy");
				cal.setBackground(Color.WHITE);
				
				cal.getDayChooser().addDateEvaluator(new OtherDatesMain());
				cal.setWeekdayForeground(Color.BLACK);
				cal.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				cal.setDecorationBordersVisible(true);
				cal.setWeekOfYearVisible(false);
				cal.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.ORANGE));
			
			c.gridx = 8;
			c.gridy = 3;
			c.gridheight = 14;
			c.gridwidth = 14;
			add(centro, c);
			centro.add(cal);
		}
		
		public void addButtons(){
		
			JPanel buttons = new JPanel();
				buttons.setBackground(Color.WHITE);
				buttons.setLayout(new GridLayout(0, 1, 15, 10));
				buttons.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 40));
				font = new Font("Bankia", Font.BOLD, 17);
			
			border = BorderFactory.createEmptyBorder(5, 5, 5, 5);
				
			JButton vistaAdmin = getJButton("Vista Admin", dao.getAdmin(), 1);
			buttons.add(vistaAdmin);
			
			JButton consulta = getJButton("Consulta", dao.getConsulta(), 2);
			buttons.add(consulta);
			
			JButton seleccion = getJButton("Selección", dao.getSeleccion(), 3);
			buttons.add(seleccion);
			
			JButton anular = getJButton("Anulación", dao.getAnulacion(), 4);
			buttons.add(anular);
			
			JButton rrhh = getJButton("RRHH", dao.getRrhh(), 5);
			buttons.add(rrhh);
			
			JButton acceso = getJButton("Acceso externo", dao.getBackdoor(), 6);
			buttons.add(acceso);
			
			switch(permiso){
				case 2:{
					acceso.setVisible(false);
					rrhh.setVisible(false);
					consulta.setVisible(false);
					vistaAdmin.setVisible(false);
				}
				case 1:{
					acceso.setVisible(false);
					rrhh.setVisible(false);
				}
				case 3:{
					acceso.setVisible(false);
				}
			}
			
			c.gridx = 0;
			c.gridy = 3;
			c.gridheight = 14;
			c.gridwidth = 8;
			add(buttons, c);
		}
		
		private JButton getJButton(String texto, Icon im, int selec){
			JButton boton = new JButton(texto, im);
				boton.setFont(font);
				boton.setBackground(Color.WHITE);
				boton.setBorder(border);
				boton.addMouseListener(new MouseListener(){
					public void mouseClicked(MouseEvent e) {
						switch(selec){
							case 1:
								new Thread(new VistaAdmin(v)).start();
								break;
							case 2:
								new Thread(new ModoConsulta(v)).start();
								break;
							case 3:
								new Thread(new ModoSeleccion(v, restantes, yeardisp)).start();
								break;
							case 4:
								new Thread(new ModoAnulacion(v)).start();
								break;
							case 5:
								new Thread(new HR(v)).start();
								break;
							case 6:
								new Thread(new Backdoor(v)).start();
								break;
						}
					}
					public void mousePressed(MouseEvent e) {}
					public void mouseReleased(MouseEvent e) {}
					public void mouseEntered(MouseEvent e) {
						boton.setBackground(new Color(130, 255, 105));
					}
					public void mouseExited(MouseEvent e) {
						boton.setBackground(Color.WHITE);
					}
				});
			
			return boton;
		}
		
		public void addTables(){
			Font font = new Font("Bankia", Font.BOLD, 15);
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			Set<LocalDate> fechas = new HashSet<>();
			Set<LocalDate> fechas1 = new HashSet<>();
			
			tablas = new JPanel();
				tablas.setBackground(Color.WHITE);
				tablas.setLayout(new GridBagLayout());
				tablas.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
				c.gridx = 22;
				c.gridy = 3;
				c.gridheight = 14;
				c.gridwidth = 8;
				add(tablas, c);
				
				JPanel dias = new JPanel();
					dias.setBackground(Color.WHITE);
					dias.setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createMatteBorder(2, 2, 2, 2, Color.green), 
							BorderFactory.createEmptyBorder(5, 5, 5, 5)));
					c.gridx = 0;
					c.gridy = 0;
					c.gridheight = 1;
					c.gridwidth = 1;
					tablas.add(dias, c);
					JLabel ldias = new JLabel("Días disponibles");
						ldias.setBackground(Color.white);
						ldias.setFont(new Font("Bankia", Font.BOLD, 20));
						dias.add(ldias);
					tfdias = new JTextField();
						tfdias.setBackground(Color.white);
						tfdias.setColumns(5);
						tfdias.setEditable(false);
						tfdias.setHorizontalAlignment(JTextField.CENTER);
						tfdias.setFont(new Font("Bankia", Font.BOLD, 20));
						dias.add(tfdias);
				
				JLabel solT = new JLabel("Pendientes aprobación");
					solT.setFont(font);
					c.gridx = 0;
					c.gridy = 1;
					c.gridheight = 1;
					c.gridwidth = 1;
					tablas.add(solT, c);
					DefaultListModel<String> modelo = new DefaultListModel<String>();
					JList<String> aprobacion = new JList<String>(modelo);
					DefaultListCellRenderer renderer = (DefaultListCellRenderer) aprobacion.getCellRenderer();
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
						aprobacion.setName("Pendientes aprobación");
						aprobacion.setFont(font);
						aprobacion.setBorder(border);
						fechas = dao.getPending(dao.getUsuario(), "OkPendiente", false);
						fechas.forEach(d -> modelo.addElement(fmt.format(d)));
						fechas.clear();
						fechas = dao.getPending(dao.getUsuario(), "OkPendiente", true);
						fechas.forEach(d -> modelo.addElement(fmt.format(d) + " (R)"));
						fechas.clear();
					c.gridx = 0;
					c.gridy = 2;
					c.gridheight = 2;
					c.gridwidth = 1;
					tablas.add(CustomScrollBar.generar(aprobacion, new Dimension(150, 80), Color.ORANGE), c);
				
				JLabel penT = new JLabel("Pendientes anulación");
					penT.setFont(font);
					c.gridx = 0;
					c.gridy = 4;
					c.gridheight = 1;
					c.gridwidth = 1;
					tablas.add(penT, c);
					DefaultListModel<String> modelo1 = new DefaultListModel<String>();
					JList<String> anulacion = new JList<String>(modelo1);
					DefaultListCellRenderer renderer1 = (DefaultListCellRenderer) anulacion.getCellRenderer();
					renderer1.setHorizontalAlignment(SwingConstants.CENTER);
						anulacion.setName("Pendientes anulacion");
						anulacion.setFont(font);
						anulacion.setBorder(border);
						fechas1 = dao.getPending(dao.getUsuario(), "KoPendiente", false);
						fechas1.forEach(d -> modelo1.addElement(fmt.format(d)));
						fechas1.clear();
						fechas1 = dao.getPending(dao.getUsuario(), "KoPendiente", true);
						fechas1.forEach(d -> modelo1.addElement(fmt.format(d) + " (R)"));
						fechas1.clear();
					c.gridx = 0;
					c.gridy = 5;
					c.gridheight = 2;
					c.gridwidth = 1;
					tablas.add(CustomScrollBar.generar(anulacion, new Dimension(150, 80), Color.ORANGE), c);
					
					
				if(disponibles.get(LocalDate.now().getYear() - 1) != null)
					yeardisp = LocalDate.now().getYear() - 1;
				else if(disponibles.get(LocalDate.now().getYear()) != null)
					yeardisp = LocalDate.now().getYear();
				else if(disponibles.get(LocalDate.now().getYear() + 1) != null)
					yeardisp = LocalDate.now().getYear() + 1;
				else
					yeardisp = 0;
				
				if(yeardisp == 0){
					tfdias.setText("0 (" + LocalDate.now().getYear() + ")");
				} else{
					diasdisp = disponibles.get(yeardisp);
					restantes = diasdisp;
					tfdias.setText(String.valueOf(restantes) + " (" + yeardisp + ")");
				}
		}
		
		public void actualizar(){
			disponibles = dao.getDays();
			v.remove(centro); v.remove(tablas);
			v.addCalendar(); v.addTables();
			v.repaint(); v.revalidate();
		}
		
		public void mostrarInfoTecnico(){
			final JLabel l = new JLabel(""
					+ "<html>La ventana principal muestra un calendario interactivo con los "
					+ "		diferentes tipos de días: <ul>"
					+ "     <li>Verde --> Día validado</li>"
					+ "     <li>Naranja --> Pendiente del coordinador</li>"
					+ "     <li>Amarillo --> Pendiente del responsable</li>"
					+ "     <li>Azul --> Festivo laborable</li></ul>"
					+ "<br/>A la derecha aparece el número de días disponibles de los que se disponga. "
					+ "		Entre paréntesis el año en el que computan."
					+ "<br/>Debajo hay un desglose de los días pendientes."
					+ "<br/>"
					+ "<br/>Accesos: <ul>"
					+ "		<li>Los botones de la izquierda permiten solicitar la validación o anulación "
					+ "		de días de vacaciones.</li>"
					+ "		<li>Consulta --> Desglose listado de las vacaciones del equipo</li>"
					+ "		<li>Selección --> Seleccionar días de vacaciones. Se deniega acceso si "
					+ "			el número de días disponibles es 0.<br/>Selecciona por defecto el año "
					+ "			computable del que se dispongan días.</li>"
					+ "		<li>Anulación --> Solicitar la anulación de días de vacaciones.</li></ul>"
					+ "<br/>En la esquina superior izquierda hay un menú que permite actualizar la "
					+ "		ventana si no se han actualizado<br/>correctamente los datos tras gestionar "
					+ "		días de vacaciones."
					+ "</html>");
			l.setFont(new Font("Bankia", Font.BOLD, 18));
			JOptionPane.showMessageDialog(null, l,"Info", JOptionPane.INFORMATION_MESSAGE);
		}
		
		public void mostrarInfoAdmin(){
			final JLabel l = new JLabel(""
					+ "<html>La ventana principal muestra un calendario interactivo con los "
					+ "		diferentes tipos de días: <ul>"
					+ "     <li>Verde --> Día validado</li>"
					+ "     <li>Naranja --> Pendiente del coordinador</li>"
					+ "     <li>Amarillo --> Pendiente del responsable</li>"
					+ "     <li>Azul --> Festivo laborable</li></ul>"
					+ "<br/>A la derecha aparece el número de días disponibles de los que se disponga. "
					+ "		Entre paréntesis el año en el que computan."
					+ "<br/>Debajo hay un desglose de los días pendientes."
					+ "<br/>"
					+ "<br/>Accesos: <ul>"
					+ "		<li>Los botones de la izquierda permiten solicitar la validación o anulación"
					+ "		de días de vacaciones.</li>"
					+ "		<li>Vista Admin --> Funciones de administrador.</li>"
					+ "		<li>Consulta --> Desglose listado de las vacaciones del equipo.</li>"
					+ "		<li>Selección --> Seleccionar días de vacaciones. Se deniega acceso si "
					+ "			el número de días disponibles es 0.<br/>Selecciona por defecto el año "
					+ "			computable del que se dispongan días.</li>"
					+ "		<li>Anulación --> Solicitar la anulación de días de vacaciones.</li></ul>"
					+ "<br/>En la esquina superior izquierda hay un menú que permite actualizar la "
					+ "		ventana si no se han actualizado<br/>correctamente los datos tras gestionar "
					+ "		días de vacaciones."
					+ "<br/>Los coordinadores tendrán también la opción de anular días pasados si se lo "
					+ "		solicita un técnico y provisto se informe a RRHH."
					+ "<br/>"
					+ "<br/>Vista Admin:<ul>"
					+ "		<li>Vista mensual de las vacaciones, tanto aprobadas como pendientes, "
					+ "		del equipo, y un desglose con porcentajes. Los filtros en la parte superior "
					+ "		permiten limitar el número de usuarios visualizados.</li>"
					+ "		<li>Extraer un Excel con el rango de fechas deseado.</li>"
					+ "		<li>Gestionar la peticiones pendientes de los técnicos/coordinadores del "
					+ "		equipo. Permite anular una petición directamente, sin tener que seguir "
					+ "		todos los pasos del proceso.</li>"
					+ "</html>");
			l.setFont(new Font("Bankia", Font.BOLD, 18));
			JOptionPane.showMessageDialog(null, l,"Info", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
