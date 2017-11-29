package org.jboss.quickstarts.wfk.bookings;

import javax.validation.ValidationException;

public class InvalidHotelException extends ValidationException {
	
	public InvalidHotelException(String message) {
		super(message);
	}
	
	public InvalidHotelException(String message, Throwable e) {
		super(message, e);
	}
	
	public InvalidHotelException(Throwable e) {
		super(e);
	}
}