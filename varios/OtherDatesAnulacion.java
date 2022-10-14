package varios.misc;

import java.awt.Color;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

import com.toedter.calendar.IDateEvaluator;

import varios.dao.DAO;

public class OtherDatesAnulacion implements IDateEvaluator {
 
	private DAO dao = DAO.getInstance();
	private String usuario = dao.getUsuario();

	public boolean isInvalid(Date date) {
		LocalDate aux = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		Set<LocalDate> check = dao.getPending(usuario, "KoPendiente", true);
		check.addAll(dao.getPending(usuario, "KoPendiente", false));
		
		Set<LocalDate> check2 = dao.getPending(usuario, "OkPendiente", false);
		check2.addAll(dao.getPending(usuario, "OkPendiente", true));
		check2.addAll(dao.getApproved(usuario, 0));
		
		if(check2.contains(aux))
			return false;
		if(check.contains(aux))
			return true;
		return true;
	}
	public Color getInvalidForegroundColor() {return Color.RED;}
	public Color getInvalidBackroundColor() {return Color.GRAY;}
	public String getInvalidTooltip() {return "Festivo";}
	
	public boolean isSpecial(Date date) {return false;}
	public Color getSpecialForegroundColor() {return Color.BLACK;}
	public Color getSpecialBackroundColor() {return Color.GREEN;}
	public String getSpecialTooltip() {return "Aprobado anulable";}
	 
	
	public boolean isPending(Date date) {return false;}
	public Color getPendingForegroundColor() {return null;}
	public Color getPendingBackroundColor() {return null;}
	public String getPendingTooltip() {return null;}
	
	
	public boolean isAnullable(Date date){return false;}
	public Color getAnullableForegroundColor() {return Color.BLACK;}
	public Color getAnullableBackroundColor() {return new Color (249, 131, 62);}
	public String getAnullableTooltip() {return "Anulable";}

	
	public boolean isPendingR(Date date) {return false;}
	public Color getPendingRForegroundColor() {return null;}
	public Color getPendingRBackroundColor() {return null;}
	public String getPendingRTooltip() {return null;}

	
	public boolean isGiven(Date date) {return false;}
	public Color getGivenForegroundColor() {return null;}
	public Color getGivenBackroundColor() {return null;}
	public String getGivenTooltip() {return null;}
}