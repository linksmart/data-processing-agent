package it.ismb.pertlab.pwal.xivelymanager.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.xively.client.model.Location;
import com.xively.client.model.Unit;

public final class Utils {
	
	/**
	 * Method used to compare two dates of creation of a feed
	 *  
	 * @param firstCreatedAt
	 *                 first date to be compared
	 *                 
	 * @param secondCreatedAt
	 *                 second date to be compared
	 *                 
	 * @return an int < 0 if the first date is earlier than the second, = 0 if are the same, > 0 if the second date is earlier than the first
	 * 
	 * @throws ParseException
	 *                 if a date passed is not in the correct format
	 */
	public static int compareCreatedAt(String firstCreatedAt, String secondCreatedAt) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date1 = format.parse(firstCreatedAt);
		Date date2 = format.parse(secondCreatedAt);
		return date1.compareTo(date2);
	}
	
	/**
	 * Method used to convert a com.xively.client.model.Location object returned
	 * by the xivley library in the it.ismb.pertlab.pwal.api.devices.model.Location used by the PWAL model
	 * 
	 * @param location
	 *             the location parsed by the xively library 
	 *             
	 * @return the location to be used in the PWAL
	 */
	public static it.ismb.pertlab.pwal.api.devices.model.Location convertLocation(Location location) {
		it.ismb.pertlab.pwal.api.devices.model.Location result = new it.ismb.pertlab.pwal.api.devices.model.Location();
		result.setDisposition(location.getDisposition()==null ? "" : location.getDisposition().toString());
		result.setDomain(location.getDomain()==null ? "" : location.getDomain().toString());
		result.setEle(location.getElevation());
		result.setExposure(location.getExposure()==null ? "" : location.getExposure().toString());
		result.setLat(location.getLatitiude());
		result.setLon(location.getLongitute());
		result.setName(location.getName()==null ? "" : location.getName());
		return result;
	}
	
	/**
	 * Method used to convert a com.xively.client.model.Unit object returned
	 * by the xivley library in the it.ismb.pertlab.pwal.api.devices.model.Unit used by the PWAL model
	 * 
	 * @param unit
	 *             the unit parsed by the xively library 
	 *             
	 * @return the unit to be used in the PWAL
	 */
	public static it.ismb.pertlab.pwal.api.devices.model.Unit convertUnit(Unit unit) {
		it.ismb.pertlab.pwal.api.devices.model.Unit result = new it.ismb.pertlab.pwal.api.devices.model.Unit();
		result.setSymbol(unit.getLabel()==null ? "" : unit.getSymbol().toString());
		result.setType(unit.getUnitType()==null ? "" : unit.getUnitType().toString());
		result.setValue(unit.getLabel()==null ? "" : unit.getLabel().toString());
		return result;
	}
	
	
	
	/**
	 * Private constructor
	 */
	private Utils() {	
	}
}
