package com.toedter.calendar;

import java.awt.Color;
import java.util.Date;

public class MinMaxDateEvaluator implements IDateEvaluator {

	private DateUtil dateUtil = new DateUtil();
	
	public boolean isSpecial(Date date) {
		return false;
	}

	public Color getSpecialForegroundColor() {
		return null;
	}

	public Color getSpecialBackroundColor() {
		return null;
	}

	public String getSpecialTooltip() {
		return null;
	}

	public boolean isInvalid(Date date) {
		return !dateUtil.checkDate(date);
	}

	public Color getInvalidForegroundColor() {
		return null;
	}

	public Color getInvalidBackroundColor() {
		return null;
	}

	public String getInvalidTooltip() {
		return null;
	}
	
	public boolean isPending(Date date){
		return false;
	}
	public Color getPendingForegroundColor(){
		return null;
	}
	public Color getPendingBackroundColor(){
		return null;
	}
	public String getPendingTooltip(){
		return null;
	}
	
	public boolean isAnullable(Date date){
		return false;
	}
	public Color getAnullableForegroundColor(){
		return null;
	}
	public Color getAnullableBackroundColor(){
		return null;
	}
	public String getAnullableTooltip(){
		return null;
	}
	
	public boolean isPendingR(Date date){
		return false;
	}
	public Color getPendingRForegroundColor(){
		return null;
	}
	public Color getPendingRBackroundColor(){
		return null;
	}
	public String getPendingRTooltip(){
		return null;
	}
	
	/**
	 * Sets the maximum selectable date. If null, the date 01\01\9999 will be
	 * set instead.
	 * 
	 * @param max
	 *            the maximum selectable date
	 * 
	 * @return the maximum selectable date
	 */
	public Date setMaxSelectableDate(Date max) {
		return dateUtil.setMaxSelectableDate(max);
	}

	/**
	 * Sets the minimum selectable date. If null, the date 01\01\0001 will be
	 * set instead.
	 * 
	 * @param min
	 *            the minimum selectable date
	 * 
	 * @return the minimum selectable date
	 */
	public Date setMinSelectableDate(Date min) {
		return dateUtil.setMinSelectableDate(min);
	}

	/**
	 * Gets the maximum selectable date.
	 * 
	 * @return the maximum selectable date
	 */
	public Date getMaxSelectableDate() {
		return dateUtil.getMaxSelectableDate();
	}

	/**
	 * Gets the minimum selectable date.
	 * 
	 * @return the minimum selectable date
	 */
	public Date getMinSelectableDate() {
		return dateUtil.getMinSelectableDate();
	}

	
	public boolean isGiven(Date date) {
		return false;
	}
	public Color getGivenForegroundColor() {
		return null;
	}
	public Color getGivenBackroundColor() {
		return null;
	}
	public String getGivenTooltip() {
		return null;
	}
}
