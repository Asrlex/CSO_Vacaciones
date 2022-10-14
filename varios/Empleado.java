package varios.objetos;

import java.util.Date;

public class Empleado{
	
	private String nombre = "", usuario = "", direccion = "", equipo = "", subEquipo = "", horario = "", 
			puesto = "", coordinador = "", usuarioCoord = "", responsable = "", usuarioResp = "";
	private Date alta = null;
	private boolean admin;

	public String getNombre() {return this.nombre;}
	public String getUsuario() {return this.usuario;}
	public String getDireccion() {return this.direccion;}
	public String getEquipo() {return this.equipo;}
	public String getSubEquipo() {return this.subEquipo;}
	public String getHorario() {return this.horario;}
	public String getPuesto() {return this.puesto;}
	public String getCoordinador() {return this.coordinador;}
	public String getUsuarioCoord() {return this.usuarioCoord;}
	public String getResp() {return this.responsable;}
	public String getUsuarioResp() {return this.usuarioResp;}
	public Date getAlta() {return this.alta;}
	public boolean isAdmin(){return this.admin;}
	
	
	public void setNombre(String nombre) {this.nombre = nombre;}
	public void setUsuario(String usuario) {this.usuario = usuario;}
	public void setDireccion(String direccion) {this.direccion = direccion;}
	public void setEquipo(String equipo) {this.equipo = equipo;}
	public void setSubEquipo(String subEquipo) {this.subEquipo = subEquipo;}
	public void setHorario(String horario) {this.horario = horario;}
	public void setPuesto(String puesto) {this.puesto = puesto;}
	public void setCoordinador(String coordinador) {this.coordinador = coordinador;}
	public void setUsuarioCoord(String usuarioCoord) {this.usuarioCoord = usuarioCoord;}
	public void setResp(String responsable) {this.responsable = responsable;}
	public void setUsuarioResp(String usuarioResp) {this.usuarioResp = usuarioResp;}
	public void setAlta(Date alta) {this.alta = alta;}
	public void setAdmin(boolean admin){this.admin = admin;}
}
