package org.jboss.quickstarts.wfk.hotel;

import javax.validation.ValidationException;

public class UniquePhoneNumberException extends ValidationException{
	   public UniquePhoneNumberException(String message) {
	        super(message);
	    }

	    public UniquePhoneNumberException(String message, Throwable cause) {
	        super(message, cause);
	    }

	    public UniquePhoneNumberException(Throwable cause) {
	        super(cause);
	    }
}
