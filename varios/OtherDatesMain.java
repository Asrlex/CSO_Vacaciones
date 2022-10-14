package varios.misc;

import java.awt.Color;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

import com.toedter.calendar.IDateEvaluator;

import varios.dao.DAO;

public class OtherDatesMain implements IDateEvaluator {
 
	private DAO dao = DAO.getInstance();
	private String usuario = dao.getUsuario();
	private Set<LocalDate> vacaciones = dao.getVacaciones(), vacacionesL = dao.getVacacionesL();
	
	public boolean isSpecial(Date date) {return false;}
	public Color getSpecialForegroundColor() {return Color.BLACK;}
	public Color getSpecialBackroundColor() {return Color.GREEN;}
	public String getSpecialTooltip() {return "Aprobado";}
	 
	public boolean isInvalid(Date date) {
		LocalDate aux = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if(aux.getDayOfWeek().getValue() > 5 || vacaciones.contains(aux))
			return true;
		return false;
	}
	public Color getInvalidForegroundColor() {return Color.BLACK;}
	public Color getInvalidBackroundColor() {return Color.GRAY;}
	public String getInvalidTooltip() {return "No disponible";}
	
	public boolean isPending(Date date) {
		Set<LocalDate> check = dao.getPending(usuario, "OkPendiente", false);
		check.addAll(dao.getPending(usuario, "KoPendiente", false));
		LocalDate aux = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if(check.contains(aux))
			return true;
		return false;
	}
	public Color getPendingForegroundColor() {return Color.BLACK;}
	public Color getPendingBackroundColor() {return new Color (249, 131, 62);}
	public String getPendingTooltip() {return "Pendiente";}
	
	
	public boolean isAnullable(Date date){
		LocalDate aux = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if(vacacionesL.contains(aux))
			return true;
		return false;
	}
	public Color getAnullableForegroundColor() {return Color.BLACK;}
	public Color getAnullableBackroundColor() {return Color.blue;}
	public String getAnullableTooltip() {return "Festivo laborable";}
	
	
	public boolean isPendingR(Date date) {
		Set<LocalDate> check = dao.getPending(usuario, "OkPendiente", true);
		check.addAll(dao.getPending(usuario, "KoPendiente", true));
		LocalDate aux = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if(check.contains(aux))
			return true;
		return false;
	}
	public Color getPendingRForegroundColor() {return Color.BLACK;}
	public Color getPendingRBackroundColor() {return Color.ORANGE;}
	public String getPendingRTooltip() {return "PendienteRes";}


	public boolean isGiven(Date date) {
		Set<LocalDate> check = dao.getApproved(usuario, 0);
		LocalDate aux = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if(check.contains(aux))
			return true;
		return false;
	}
	public Color getGivenForegroundColor() {return Color.black;}
	public Color getGivenBackroundColor() {return Color.green;}
	public String getGivenTooltip() {return "Aprobado";}
}
