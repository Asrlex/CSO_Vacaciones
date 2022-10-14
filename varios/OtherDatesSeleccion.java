package varios.misc;

import java.awt.Color;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

import com.toedter.calendar.IDateEvaluator;

import varios.dao.DAO;

public class OtherDatesSeleccion implements IDateEvaluator {
 
	private DAO dao = DAO.getInstance();
	private String usuario = dao.getUsuario();
	private Set<LocalDate> vacaciones = dao.getVacaciones(), vacacionesL = dao.getVacacionesL();
 
	public boolean isSpecial(Date date) {return false;}
	public Color getSpecialForegroundColor() {return Color.BLACK;}
	public Color getSpecialBackroundColor() {return Color.GREEN;}
	public String getSpecialTooltip() {return "";}
	 

	public boolean isInvalid(Date date) {
		Set<LocalDate> check = vacaciones;
		check.addAll(vacacionesL);
		check.addAll(dao.getApproved(usuario, 0));
		LocalDate aux = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if(aux.getDayOfWeek().getValue() > 5 || check.contains(aux))
			return true;
		return false;
	}
	public Color getInvalidForegroundColor() {return Color.RED;}
	public Color getInvalidBackroundColor() {return Color.GRAY;}
	public String getInvalidTooltip() {return "Aprobado anulable";}

	
	public boolean isPending(Date date) {
		Set<LocalDate> check = dao.getPending(usuario, "OkPendiente", false);
		check.addAll(dao.getPending(usuario, "OkPendiente", true));
		LocalDate aux = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if(aux.getDayOfWeek().getValue() > 5 || check.contains(aux))
			return true;
		return false;
	}
	public Color getPendingForegroundColor() {return new Color (249, 131, 62);}
	public Color getPendingBackroundColor() {return new Color (249, 131, 62);}
	public String getPendingTooltip() {return "Pendiente";}
	
	public boolean isAnullable(Date date){return false;}
	public Color getAnullableForegroundColor() {return new Color (249, 131, 62);}
	public Color getAnullableBackroundColor() {return new Color (249, 131, 62);}
	public String getAnullableTooltip() {return "Pendiente anulable";}
	
	public boolean isPendingR(Date date) {return false;}
	public Color getPendingRForegroundColor() {return null;}
	public Color getPendingRBackroundColor() {return null;}
	public String getPendingRTooltip() {return null;}

	public boolean isGiven(Date date) {return false;}
	public Color getGivenForegroundColor() {return null;}
	public Color getGivenBackroundColor() {return null;}
	public String getGivenTooltip() {return null;}
}