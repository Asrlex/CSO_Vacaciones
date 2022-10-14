package varios.dao;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;
import org.tinylog.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import varios.acciones.SwingUtils;
import varios.objetos.Empleado;

public class DAO{
	
	private static final String JDBC_URL = "jdbc:ucanaccess://RECURSOS_VACACIONES/VacacionesTest.accdb";
	private static final String JDBC_URL_USER = "jdbc:ucanaccess://../RECURSOS_GLOBALES/GLOBAL-BDD.accdb";
	private static final String JDBC_DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";
	private String usuario = System.getProperty("user.name").toUpperCase();
//	private String usuario = "A186545";
	private Set<LocalDate> vacaciones = new HashSet<>(), vacacionesL = new HashSet<>();
	private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private final DateTimeFormatter fmt2 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private Icon salir, minimizar, logo, admin, consulta, info, menu, check, add, seleccion, 
					anulacion, rrhh, backdoor, actualizar, borrar, excel, lista;
	private static DAO dao;
	private List<Empleado> usuarios = new ArrayList<>(), equipo = new ArrayList<>();
	private int permiso;
	private Empleado yo;
	private List<String> listaDirecciones = new ArrayList<>(), listaEquipos = new ArrayList<>(), listaSubEquipos = new ArrayList<>(), listaHorarios = new ArrayList<>();

	public static boolean checkFailsafe(){
		try {
			String result = Files.lines(Paths.get("./RECURSOS_VACACIONES/failsafe.txt")).findFirst().get();
			return result.equals("ON");
		} catch (IOException e) {e.printStackTrace(); return false;}
	}
		
	public static void control(){
		Logger.tag("ENTRADA").info("ENTRADA V3");
	}
	
	public static DAO getInstance(){
		if(dao == null)
			dao = new DAO();
		return dao;
	}
	
	public void cargarDatos(){
		getPeople();
		checkUser();
		getEquipo();
		getHolidays();
		CargarImagenes();
		cargarFiltros();
	}
	
	private DAO(){
		cargarDatos();
	}
	
	private void CargarImagenes(){
		setSalir(FontIcon.of(MaterialDesign.MDI_CLOSE_OCTAGON_OUTLINE, 25, Color.RED));
		setMinimizar(FontIcon.of(MaterialDesign.MDI_WINDOW_MINIMIZE, 25, Color.BLACK));
		setLogo(SwingUtils.getCSOLogo());
		setInfo(FontIcon.of(MaterialDesign.MDI_HELP_CIRCLE_OUTLINE, 25, Color.BLUE));
		setMenu(FontIcon.of(MaterialDesign.MDI_MENU, 25, Color.BLUE));
		setExcel(FontIcon.of(MaterialDesign.MDI_FILE_EXCEL_BOX, 25, new Color(29, 111, 66)));
		setBorrar(FontIcon.of(MaterialDesign.MDI_DELETE_VARIANT, 20, Color.BLACK));
		setAdmin(FontIcon.of(MaterialDesign.MDI_CALENDAR_MULTIPLE_CHECK, 20, new Color(230, 156, 30)));
		setConsulta(FontIcon.of(MaterialDesign.MDI_MAGNIFY, 20, Color.BLUE));
		setSeleccion(FontIcon.of(MaterialDesign.MDI_CURSOR_POINTER, 20, Color.BLACK));
		setAnulacion(FontIcon.of(MaterialDesign.MDI_WINDOW_CLOSE, 20, Color.BLACK));
		setRrhh(FontIcon.of(MaterialDesign.MDI_HUMAN_MALE_FEMALE, 20, Color.BLACK));
		setBackdoor(FontIcon.of(MaterialDesign.MDI_KEY_VARIANT, 20, new Color(252, 186, 3)));
		setActualizar(FontIcon.of(MaterialDesign.MDI_REFRESH, 25, new Color(30, 168, 67)));
		setAdd(FontIcon.of(MaterialDesign.MDI_ARROW_RIGHT_BOLD, 25, new Color(30, 168, 67)));
		setCheck(FontIcon.of(MaterialDesign.MDI_ACCOUNT_CHECK, 25, new Color(30, 168, 67)));
		setLista(FontIcon.of(MaterialDesign.MDI_CHECKBOX_MULTIPLE_MARKED_OUTLINE, 25, Color.black));
	}

	private void getPeople(){
		final String query = "SELECT DISTINCT Usuario, Nombre, Direccion, Equipo, SubEquipo, "
				+ "Horario, Puesto, UsuarioCoordinador, Coordinador, UsuarioResponsable, "
				+ "Responsable FROM Usuarios";
		Empleado p;
		usuarios.clear();
		try{
			Class.forName(JDBC_DRIVER);
			try(Connection con = DriverManager.getConnection(JDBC_URL_USER);
					PreparedStatement s = con.prepareStatement(query);
					ResultSet check = s.executeQuery();){
				ResultSetMetaData rsmd = check.getMetaData();
				int columnsNumber = rsmd.getColumnCount();
				while(check.next()){
					p = new Empleado();
					for (int j = 1; j <= columnsNumber; j++) {
						String cv = check.getString(j);
						switch(j){
							case 1:
								p.setUsuario(cv); break;
							case 2:
								p.setNombre(cv); break;
							case 3:
								p.setDireccion(cv); break;
							case 4:
								p.setEquipo(cv); break;
							case 5:
								p.setSubEquipo(cv); break;
							case 6:
								p.setHorario(cv); break;
							case 7:
								p.setPuesto(cv); break;
							case 8:
								p.setUsuarioCoord(cv); break;
							case 9:
								p.setCoordinador(cv); break;
							case 10:
								p.setUsuarioResp(cv); break;
							case 11:
								p.setResp(cv); break;
						}
					}
					
					if(p.getUsuario().equals(usuario))
						yo = p;
					usuarios.add(p);
				}
			}
		} catch (SQLException | ClassNotFoundException e){
			JOptionPane.showMessageDialog(null, "Conexión no completada, contacte con CyR");
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("USUARIO: " + yo.getUsuario());
		System.out.println("NOMBRE: " + yo.getNombre());
		System.out.println("USUARIOS A CARGO: " + usuarios.size() + " usuarios");
	}
	
	public void checkUser(){
		System.out.println("YO -----> " + usuario);
		int resultado = -1;
		if(yo.getDireccion().equals("DIRECCION DE MEDIOS")){
			resultado = 3;
		} else if(yo.getDireccion().equals("DIRECCION GENERAL")){
			resultado = 4;
		} else{
			switch(yo.getPuesto()){
				case "Tecnico":
					resultado = 2; break;
				case "Coordinador":
				case "Responsable":
					resultado = 1; break;
				case "Director":
					resultado = 0; break;
				default:
					JOptionPane.showMessageDialog(null, "No se puede comprobar el usuario.");
					System.exit(1);
			}
		}
		setPermiso(resultado);
	}
	
	private void getEquipo(){
		System.out.println("PERMISO: " + getPermiso());
		equipo.clear();
		if(getPermiso() != 2){
			switch(usuario){
				case "A119737":
					equipo.addAll(usuarios.stream()
						.filter(u -> u != null && (u.getDireccion().equals("DIRECCION DE TRAMITACION")))
							.collect(Collectors.toList()));
					break;
				case "A124050":
					equipo.addAll(usuarios.stream()
							.filter(u -> u != null && (u.getDireccion().equals("DIRECCIÓN DE FORMALIZACION")))
								.collect(Collectors.toList()));
					break;
				case "A148657":
				case "A194855":
				case "A195885":
				case "A185618":
				case "A178028":
				case "A196805":
				case "A195148":
					equipo.addAll(usuarios);break;
				default:
					equipo.addAll(usuarios.stream()
							.filter(u -> u != null && (u.getUsuarioCoord().equals(usuario) || u.getUsuarioResp().equals(usuario)))
								.collect(Collectors.toList()));
					break;
			}
		}
		System.out.println(equipo.size());
	}
	
	public List<Empleado> getAdmins(){
		return usuarios.stream().filter(u -> u.getPuesto().equals("Tecnico")).collect(Collectors.toList());
	}
	
	public List<String> getValidar(boolean choice){
		List<String> usuarios = new ArrayList<>();
		String res = "";
		String query;
		if(choice){
			query = "SELECT DISTINCT u.Nombre, u.Usuario "
					+ "FROM Usuarios AS u INNER JOIN VacacionesPendientes AS vp "
					+ "ON u.Usuario = vp.Usuario WHERE (((vp.Responsable) = ?) AND ((u.UsuarioResponsable) = ?))";
		}else{
			query = "SELECT DISTINCT u.Nombre, u.Usuario "
					+ "FROM Usuarios AS u INNER JOIN VacacionesPendientes AS vp "
					+ "ON u.Usuario = vp.Usuario WHERE (((vp.Responsable) = ?) AND ((u.UsuarioCoordinador) = ?))";
		}
		try{
			Class.forName(JDBC_DRIVER);
			try(Connection con = DriverManager.getConnection(JDBC_URL);
					PreparedStatement s = con.prepareStatement(query);){
				s.setBoolean(1, choice);
				s.setString(2, usuario);
				
				try(ResultSet check = s.executeQuery();){
					ResultSetMetaData rsmd = check.getMetaData();
					int columnsNumber = rsmd.getColumnCount();
					
					while(check.next()){
						for (int j = 1; j <= columnsNumber; j++) {
							String cv = check.getString(j);
							if(j == 1)
								res = cv;
							if(j == 2)
								res += " (" + cv + ")";
						}
						usuarios.add(res);
					}
				}
			}
		} catch (SQLException | ClassNotFoundException e){
			
			e.printStackTrace();
		}
		return usuarios;
	}
	
	private void getHolidays(){
		LocalDate aux = null;
		final String query = "SELECT Fecha FROM Festivos WHERE Laborables = ?";
		try{
			Class.forName(JDBC_DRIVER);
			try(Connection con = DriverManager.getConnection(JDBC_URL);){
				con.setAutoCommit(false);
				try(PreparedStatement s = con.prepareStatement(query);){
					s.setBoolean(1,  true);
					try(ResultSet check = s.executeQuery();){
						ResultSetMetaData rsmd = check.getMetaData();
						int columnsNumber = rsmd.getColumnCount();
						while(check.next()){
							for (int i = 1; i <= columnsNumber; i++) {
								String cv = check.getString(i).substring(0, 10);
								aux = LocalDate.parse(cv, fmt);
								vacaciones.add(aux);
							}
						}
					}
				}
				try(PreparedStatement s2 = con.prepareStatement(query);){
					s2.setBoolean(1,  false);
					try(ResultSet check2 = s2.executeQuery();){
						ResultSetMetaData rsmd2 = check2.getMetaData();
						int columnsNumber2 = rsmd2.getColumnCount();
						while(check2.next()){
							for (int i = 1; i <= columnsNumber2; i++) {
								String cv = check2.getString(i).substring(0, 10);
								aux = LocalDate.parse(cv, fmt);
								vacacionesL.add(aux);
							}
						}
					}
				}
				
				con.commit();
			}
		} catch (SQLException | ClassNotFoundException e){
			
			e.printStackTrace();
		}
	}
	
	public Map<Integer, Integer> getDays(){
		Map<Integer, Integer> mapa = new HashMap<Integer, Integer>();
		final String query = "SELECT Dias, AnoComputable FROM DiasVacaciones WHERE Usuario = ? AND Dias > 0";
		try{
			Class.forName(JDBC_DRIVER);
			try(Connection con = DriverManager.getConnection(JDBC_URL);
					PreparedStatement s = con.prepareStatement(query);){
				s.setString(1, usuario);
				try(ResultSet check = s.executeQuery();){
					ResultSetMetaData rsmd = check.getMetaData();
					int columnsNumber = rsmd.getColumnCount();
					int dias = 0, año = 0;
					while(check.next()){
						dias = 0; año = 0;
						for (int i = 1; i <= columnsNumber; i++){
							if(i == 1)
								dias = Integer.parseInt(check.getString(i));
							else
								año = Integer.parseInt(check.getString(i));
						}
						mapa.put(año, dias);
					}
				}
			}
		} catch (SQLException | ClassNotFoundException e){
			
			e.printStackTrace();
		}
		return mapa;
	}
	
	public void cargarFiltros(){
		listaDirecciones.add("TODOS");
		listaEquipos.add("TODOS");
		listaSubEquipos.add("TODOS");
		listaHorarios.add("TODOS");
		final String query = "SELECT DISTINCT Direccion FROM Usuarios";
		final String query2 = "SELECT DISTINCT Equipo FROM Usuarios";
		final String query3 = "SELECT DISTINCT SubEquipo FROM Usuarios";
		final String query4 = "SELECT DISTINCT Horario FROM Usuarios";
		try{
			Class.forName(JDBC_DRIVER);
			try(Connection con = DriverManager.getConnection(JDBC_URL_USER);){
				con.setAutoCommit(false);	
				try(PreparedStatement s = con.prepareStatement(query);
						ResultSet check = s.executeQuery();){
					ResultSetMetaData rsmd = check.getMetaData();
					int columnsNumber = rsmd.getColumnCount();
					while(check.next()){
						for (int i = 1; i <= columnsNumber; i++) {
							String cv = check.getString(i);
							listaDirecciones.add(cv);
						}
					}
				}
				try(PreparedStatement s2 = con.prepareStatement(query2);
						ResultSet check2 = s2.executeQuery();){
					ResultSetMetaData rsmd2 = check2.getMetaData();
					int columnsNumber2 = rsmd2.getColumnCount();
					while(check2.next()){
						for (int i = 1; i <= columnsNumber2; i++) {
							String cv = check2.getString(i);
							listaEquipos.add(cv);
						}
					}
				}
				try(PreparedStatement s3 = con.prepareStatement(query3);
						ResultSet check3 = s3.executeQuery();){
					ResultSetMetaData rsmd3 = check3.getMetaData();
					int columnsNumber3 = rsmd3.getColumnCount();
					while(check3.next()){
						for (int i = 1; i <= columnsNumber3; i++) {
							String cv = check3.getString(i);
							listaSubEquipos.add(cv);
						}
					}
				}
				try(PreparedStatement s4 = con.prepareStatement(query4);
						ResultSet check4 = s4.executeQuery();){
					ResultSetMetaData rsmd4 = check4.getMetaData();
					int columnsNumber4 = rsmd4.getColumnCount();
					while(check4.next()){
						for (int i = 1; i <= columnsNumber4; i++) {
							String cv = check4.getString(i);
							listaHorarios.add(cv);
						}
					}
				}
				con.commit();
			}
		} catch (SQLException | ClassNotFoundException e){
			
			e.printStackTrace();
		}
	}
	
	public Set<LocalDate> getPending(String user, String choice, boolean choice1){
		LocalDate aux = null;
		Set<LocalDate> fechas = new HashSet<>();
		final String query = "SELECT vp.Fecha " + 
						"FROM VacacionesPendientes as vp " + 
						"WHERE vp.Usuario = ? " + 
						"AND vp.Color = ? AND vp.Responsable = ?";
		try{
			Class.forName(JDBC_DRIVER);
			try(Connection con = DriverManager.getConnection(JDBC_URL);
					PreparedStatement s = con.prepareStatement(query);){
				s.setString(1, user);
				s.setString(2, choice);
				s.setBoolean(3, choice1);
				try(ResultSet check = s.executeQuery();){
					ResultSetMetaData rsmd = check.getMetaData();
					int columnsNumber = rsmd.getColumnCount();
					while(check.next()){
						for (int i = 1; i <= columnsNumber; i++) {
							String cv = check.getString(i).substring(0, 10);
							aux = LocalDate.parse(cv, fmt);
							fechas.add(aux);
						}
					}
				}
			}
		} catch (SQLException | ClassNotFoundException e){
			
			e.printStackTrace();
		}
		return fechas;
	}
	
	public Set<LocalDate> getApproved(String user, int año){
		Set<LocalDate> fechas = new HashSet<>();
		LocalDate aux = null;
		String query = "";
		if(año > 0)
			query = "SELECT Fecha FROM Vacaciones WHERE Usuario = ? AND Anocomputable = ?";
		else
			query = "SELECT Fecha FROM Vacaciones WHERE Usuario = ?";
		try{
			Class.forName(JDBC_DRIVER);
			try(Connection con = DriverManager.getConnection(JDBC_URL);
					PreparedStatement s = con.prepareStatement(query);){
				s.setString(1, user);
				if(año > 0){
					s.setInt(2, año);
				}
				try(ResultSet check = s.executeQuery();){
					ResultSetMetaData rsmd = check.getMetaData();
					int columnsNumber = rsmd.getColumnCount();
					
					while(check.next()){
						for (int i = 1; i <= columnsNumber; i++) {
							String cv = check.getString(i).substring(0, 10);
							aux = LocalDate.parse(cv, fmt);
							fechas.add(aux);
						}
					}
				}
			}
		} catch (SQLException | ClassNotFoundException e){
			
			e.printStackTrace();
		}
		return fechas;
	}
	
	public void addDays(List<String> dates, String tipo, int año){
		final String query = "INSERT INTO VacacionesPendientes (Usuario, Color, Fecha, "
				+ "AnoComputable, Responsable) VALUES (?, ?, ?, ?, ?)";
		try{
			Class.forName(JDBC_DRIVER);
			try(Connection con = DriverManager.getConnection(JDBC_URL);
					PreparedStatement s = con.prepareStatement(query);){
				con.setAutoCommit(false);
				for(String date : dates){
					s.setString(1, usuario);
					s.setString(2, tipo);
					s.setDate(3, Date.valueOf(LocalDate.parse(date, fmt2)));
					s.setInt(4, año);
					s.setBoolean(5, false);
					s.addBatch();
				}
				s.executeBatch();
				if(tipo.equals("OkPendiente"))
					modificarNumDias(false, año, usuario, dates.size(), con);
				con.commit();
			}
		} catch (SQLException | ClassNotFoundException e){
			
			e.printStackTrace();
		}
	}
	
	public List<Empleado> filter (List<Empleado> list, String direccion, 
											String equipo, String subequipo, String horario){
	  	boolean dir = !direccion.equals("TODOS");
		boolean eq = !equipo.equals("TODOS");
		boolean sub = !subequipo.equals("TODOS");
		boolean hor = !horario.equals("TODOS");
		boolean enter = false;
		List<Empleado> filtrado = new ArrayList<>();
		
		if(!dir && !eq && !sub && !hor)
			return list;
		for(Empleado aux : list){
			enter = false;
			if((aux.getDireccion().equals(direccion)) || 
					(aux.getEquipo().equals(equipo)) ||
					(aux.getSubEquipo().contains(subequipo)) ||
					(aux.getHorario().contains(horario))){
				if((dir && !aux.getDireccion().equals(direccion)) || 
						(eq && !aux.getEquipo().equals(equipo)) ||
						(sub && !aux.getSubEquipo().contains(subequipo)) ||
						(hor && !aux.getHorario().contains(horario))){
					enter = false;
				} else{
					enter = true;
				}
				if(enter){
					filtrado.add(aux);
				}
			}
			if(!dir && !eq && !sub && !hor){
				filtrado.add(aux);
			}
		}
		return filtrado;
	}
	
	public void confirmCoord(List<String> lista, String user){
		final String query = "UPDATE VacacionesPendientes SET Responsable = ? "
						+ "WHERE Fecha = ? AND Usuario = ?";
		try{
			Class.forName(JDBC_DRIVER);
			try(Connection con = DriverManager.getConnection(JDBC_URL);
					PreparedStatement s = con.prepareStatement(query);){
				con.setAutoCommit(false);
				for(String date : lista){
					s.setBoolean(1, true);
					s.setDate(2, Date.valueOf(LocalDate.parse(date, fmt2)));
					s.setString(3, user);
					s.addBatch();
				}
				s.executeBatch();
				
				con.commit();
			}
			Logger.tag("VALIDACION").info("DÍAS VALIDADOS COORDINADOR A {}: {}", user, lista);
		} catch (SQLException | ClassNotFoundException  e){
			
			e.printStackTrace();
		}
	}
	
	public void confirmResp(List<String> lista, String user){
		Date sqlDate = null;
		int año = 0;
		final String query = "INSERT INTO Vacaciones (Usuario, Fecha, AnoComputable) "
				+ "VALUES (?, ?, ?)";
		try{
			Class.forName(JDBC_DRIVER);
			try(Connection con = DriverManager.getConnection(JDBC_URL);){
				con.setAutoCommit(false);
				for(String date : lista){
					sqlDate = Date.valueOf(LocalDate.parse(date, fmt2));
					año = getAñoComputable(true, Date.valueOf(LocalDate.parse(date, fmt2)), user, con);
					borrarPendiente(sqlDate, user, con);
					try(PreparedStatement s = con.prepareStatement(query);){
						s.setString(1,  user);
						s.setDate(2, sqlDate);
						s.setInt(3, año);
						s.executeUpdate();
					}
					Logger.tag("VALIDACION").info("DÍA VALIDADO RESPONSABLE A {}: {}", user, date);
				}
				con.commit();
			}
				
		} catch (SQLException | ClassNotFoundException  e){
			
			e.printStackTrace();
		}
	}
	
	public void anulResp(List<String> lista, String user){
		Date sqlDate = null;
		int año = 0;
		final String query = "DELETE FROM Vacaciones WHERE Usuario = ? AND Fecha = ?";
		try{
			Class.forName(JDBC_DRIVER);
			try(Connection con = DriverManager.getConnection(JDBC_URL);){
				con.setAutoCommit(false);
				for(String date : lista){
					año = 0;
					sqlDate = Date.valueOf(LocalDate.parse(date, fmt2));
					try(PreparedStatement s = con.prepareStatement(query);){
						s.setString(1,  user);
						s.setDate(2, sqlDate);
						s.executeUpdate();
					}
					año = getAñoComputable(true, sqlDate, user, con);
					borrarPendiente(sqlDate, user, con);
					modificarNumDias(true, año, user, 1, con);
					
					Logger.tag("VALIDACION").info("DÍA ANULADO RESPONSABLE A {}: {}", user, date);
				}
				con.commit();
			}
		} catch (SQLException | ClassNotFoundException e){
			
			e.printStackTrace();
		}
	}
	
	public void eliminarDias(List<String> lista, String user){
		Date sqlDate = null;
		int año = 0;
		try{
			Class.forName(JDBC_DRIVER);
			try(Connection con = DriverManager.getConnection(JDBC_URL);){
				for(String date : lista){
					sqlDate = Date.valueOf(LocalDate.parse(date));
					año = 0;
					año = getAñoComputable(true, sqlDate, user, con);
					borrarPendiente(sqlDate, user, con);
					modificarNumDias(true, año, user, 1, con);
					
					Logger.tag("VALIDACION").info("DÍA ELIMINADO A {}: {}", user, date);
				}
			}
		} catch (SQLException | ClassNotFoundException e){
			
			e.printStackTrace();
		}
	}
	
	public void borrarDiaPasado(String dia, String user){
		final String query = "DELETE FROM Vacaciones WHERE Fecha = ? AND Usuario = ?";
		try {
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			Date sqlDate = Date.valueOf(LocalDate.parse(dia, fmt));
			Class.forName(JDBC_DRIVER);
			try(Connection con = DriverManager.getConnection(JDBC_URL);){
				con.setAutoCommit(false);
				int año = getAñoComputable(false, sqlDate, user, con);
				try(PreparedStatement s = con.prepareStatement(query);){
					s.setDate(1, sqlDate);
					s.setString(2, user);
					s.executeUpdate();
				}
				modificarNumDias(true, año, user, 1, con);
			
				con.commit();
			}
			Logger.tag("VALIDACION").info("DÍA ELIMINADO A {}: {}", user, dia);
			JOptionPane.showMessageDialog(null, "Día borrado.");
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	private void modificarNumDias(boolean sumar, int año, String user, int dias, Connection con){
		String query = "";
		if(sumar)
			query = "UPDATE DiasVacaciones SET Dias = Dias+? "
					+ "WHERE AnoComputable = ? "
					+ "AND Usuario = ?";
		else
			query = "UPDATE DiasVacaciones SET Dias = Dias-? "
					+ "WHERE AnoComputable = ? "
					+ "AND Usuario = ?";
		try(PreparedStatement s = con.prepareStatement(query);){
			s.setInt(1, dias);
			s.setInt(2, año);
			s.setString(3, user);
			s.executeUpdate();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	private void borrarPendiente(java.sql.Date sqlDate, String user, Connection con){
		final String query = "DELETE FROM VacacionesPendientes WHERE Fecha = ? AND Usuario = ?";
		try(PreparedStatement s = con.prepareStatement(query);){
			s.setDate(1,  sqlDate);
			s.setString(2,  user);
			s.executeUpdate();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	public int getAñoComputable(boolean pendientes, java.sql.Date sqlDate, String user, Connection con){
		int año = 0;
		String query;
		if(pendientes)
			query = "SELECT TOP 1 AnoComputable "
					+ "FROM VacacionesPendientes "
					+ "WHERE Fecha = ? AND Usuario = ?";
		else
			query = "SELECT AnoComputable "
					+ "FROM Vacaciones "
					+ "WHERE Fecha = ? AND Usuario = ?";
		try(PreparedStatement s = con.prepareStatement(query);){
			s.setDate(1, sqlDate);
			s.setString(2, user);
			try(ResultSet check = s.executeQuery();){
				while(check.next()){
					String columnValue = check.getString(1);
					año = Integer.parseInt(columnValue);
				}
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return año;
	}

	public Multimap<Integer, LocalDate> getUsuarioRRHHValidado(String u){
		String query = "select AnoComputable, Fecha from Vacaciones where Usuario = ? and AnoComputable between ? and ?";
		Multimap<Integer, LocalDate> mapa = ArrayListMultimap.create();
		int año = 0;
		try{
			Class.forName(JDBC_DRIVER);
			try(Connection con = DriverManager.getConnection(JDBC_URL);
					PreparedStatement s = con.prepareStatement(query);){
				int a = LocalDate.now().getYear();
				s.setString(1, u);
				s.setInt(2, a - 2);
				s.setInt(3, a + 1);
				try(ResultSet check = s.executeQuery();){
					ResultSetMetaData rsmd = check.getMetaData();
					int columnsNumber = rsmd.getColumnCount();
					
					while(check.next()){
						año = 0;
						for (int i = 1; i <= columnsNumber; i++) {
							String cv = check.getString(i);
							if(i == 1)
								año = Integer.valueOf(cv);
							if(i == 2){
								mapa.put(año, LocalDate.parse(cv.substring(0, 10)));
							}
						}
					}
				}
			}
		} catch (SQLException | ClassNotFoundException  e){
			
			e.printStackTrace();
		}
		return mapa;
	}
	
	public List<List<String>> getUsuarioRRHHPendiente(String u){
		String query = "SELECT Fecha, AnoComputable, Color FROM VacacionesPendientes WHERE Usuario = ?";
		List<List<String>> res = new ArrayList<>();
		List<String> aux;
		try{
			Class.forName(JDBC_DRIVER);
			try(Connection con = DriverManager.getConnection(JDBC_URL);
					PreparedStatement s = con.prepareStatement(query);){
				s.setString(1, u);
				try(ResultSet check = s.executeQuery();){
					ResultSetMetaData rsmd = check.getMetaData();
					int columnsNumber = rsmd.getColumnCount();
					
					while(check.next()){
						aux = new ArrayList<>();
						for (int i = 1; i <= columnsNumber; i++) {
							String cv = check.getString(i);
							if(i == 1)
								aux.add(cv.substring(0, 10));
							if(i == 2)
								aux.add(cv);
							if(i == 3)
								aux.add(cv);
						}
						res.add(aux);
					}
				}
			}
		} catch (SQLException | ClassNotFoundException  e){
			
			e.printStackTrace();
		}
		return res;
	}
	
	
	public Set<LocalDate> getVacaciones() {return vacaciones;}
	public void setVacaciones(Set<LocalDate> vacaciones) {this.vacaciones = vacaciones;}
	public Set<LocalDate> getVacacionesL() {return vacacionesL;}
	public void setVacacionesL(Set<LocalDate> vacacionesL) {this.vacacionesL = vacacionesL;}
	public Icon getSalir() {return salir;}
	public void setSalir(Icon salir) {this.salir = salir;}
	public Icon getMinimizar() {return minimizar;}
	public void setMinimizar(Icon minimizar) {this.minimizar = minimizar;}
	public Icon getLogo() {return logo;}
	public void setLogo(Icon logo) {this.logo = logo;}
	public String getUsuario() {return usuario;}
	public void setUsuario(String usuario){this.usuario = usuario;}
	public List<Empleado> getUsuarios() {return usuarios;}
	public void setUsuarios(List<Empleado> usuarios) {this.usuarios = usuarios;}
	public List<Empleado> getListaEquipo() {return equipo;}
	public void setListaEquipo(List<Empleado> equipo) {this.equipo = equipo;}
	public int getPermiso() {return permiso;}
	public void setPermiso(int permiso) {this.permiso = permiso;}
	public Empleado getYo() {return yo;}
	public void setYo(Empleado yo) {this.yo = yo;}
	public Icon getAdmin() {return admin;}
	public void setAdmin(Icon admin) {this.admin = admin;}
	public Icon getConsulta() {return consulta;}
	public void setConsulta(Icon consulta) {this.consulta = consulta;}
	public Icon getSeleccion() {return seleccion;}
	public void setSeleccion(Icon seleccion) {this.seleccion = seleccion;}
	public Icon getAnulacion() {return anulacion;}
	public void setAnulacion(Icon anulacion) {this.anulacion = anulacion;}
	public Icon getRrhh() {return rrhh;}
	public void setRrhh(Icon rrhh) {this.rrhh = rrhh;}
	public Icon getBackdoor() {return backdoor;}
	public void setBackdoor(Icon backdoor) {this.backdoor = backdoor;}
	public Icon getActualizar() {return actualizar;}
	public void setActualizar(Icon actualizar) {this.actualizar = actualizar;}
	public Icon getInfo() {return info;}
	public void setInfo(Icon info) {this.info = info;}
	public Icon getMenu() {return menu;}
	public void setMenu(Icon menu) {this.menu = menu;}
	public List<String> getListaDirecciones() {return listaDirecciones;}
	public void setListaDirecciones(List<String> listaDirecciones) {this.listaDirecciones = listaDirecciones;}
	public List<String> getListaEquipos() {return listaEquipos;}
	public void setListaEquipos(List<String> listaEquipos) {this.listaEquipos = listaEquipos;}
	public List<String> getListaSubEquipos() {return listaSubEquipos;}
	public void setListaSubEquipos(List<String> listaSubEquipos) {this.listaSubEquipos = listaSubEquipos;}
	public List<String> getListaHorarios() {return listaHorarios;}
	public void setListaHorarios(List<String> listaHorarios) {this.listaHorarios = listaHorarios;}
	public Icon getBorrar() {return borrar;}
	public void setBorrar(Icon borrar) {this.borrar = borrar;}
	public Icon getExcel() {return excel;}
	public void setExcel(Icon excel) {this.excel = excel;}
	public Icon getAdd() {return add;}
	public void setAdd(Icon add) {this.add = add;}
	public Icon getCheck() {return check;}
	public void setCheck(Icon check) {this.check = check;}
	public Icon getLista() {return lista;}
	public void setLista(Icon lista) {this.lista = lista;}
}
