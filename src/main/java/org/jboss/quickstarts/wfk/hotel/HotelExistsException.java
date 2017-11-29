package org.jboss.quickstarts.wfk.hotel;

import javax.validation.ValidationException;

public class HotelExistsException extends ValidationException{
	public HotelExistsException(String message){
		super(message);
	}
	

    public HotelExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public HotelExistsException(Throwable cause) {
        super(cause);
    }
}
