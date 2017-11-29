package org.jboss.quickstarts.wfk.bookings;

import javax.validation.ValidationException;

public class InvalidCustomerException extends ValidationException {
	
	public InvalidCustomerException(String message) {
		super(message);
	}
	
	public InvalidCustomerException(String message, Throwable e) {
		super(message, e);
	}
	
	public InvalidCustomerException(Throwable e) {
		super(e);
	}
	
}