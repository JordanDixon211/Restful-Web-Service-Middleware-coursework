package org.jboss.quickstarts.wfk.bookings;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.customer.CustomerExistsException;
import org.jboss.quickstarts.wfk.customer.CustomerRepository;

public class BookingValidator {
	@Inject 
	private Validator validator;
	
	@Inject 
	private BookingRepository crud;
	
	
	public void validateBooking(Booking booking)throws ConstraintViolationException, ValidationException{
		Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
		
		if(!violations.isEmpty()) {
			throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
		}

	}
	
	private boolean bookingAlreadyExists(Long id) {
		Booking booking = null;
		
		if(id != null) {
			try {
				booking = crud.findBookingById(id);	
			}catch(NoResultException e) {
				
			}
		}
		
		return booking != null ? true : false;
		
	}	
}
