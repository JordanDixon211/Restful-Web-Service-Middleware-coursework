package org.jboss.quickstarts.wfk.bookings;

import javax.validation.ValidationException;

public class BookingExistsException extends ValidationException {
	public BookingExistsException(String message){
		super(message);
	}
	

    public BookingExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingExistsException(Throwable cause) {
        super(cause);
    }
}
