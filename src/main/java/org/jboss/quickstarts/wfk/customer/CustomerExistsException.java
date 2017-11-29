package org.jboss.quickstarts.wfk.customer;

import javax.validation.ValidationException;

public class CustomerExistsException extends ValidationException {
	public CustomerExistsException(String message){
		super(message);
	}
	

    public CustomerExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomerExistsException(Throwable cause) {
        super(cause);
    }
}
