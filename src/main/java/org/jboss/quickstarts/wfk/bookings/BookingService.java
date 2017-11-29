package org.jboss.quickstarts.wfk.bookings;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.hotel.Hotel;

public class BookingService {
	
	@Inject
	private @Named("logger") Logger log;
	
	@Inject
	private BookingRepository crud;
	
	@Inject
	private BookingValidator validator;
	
	List<Booking> findAllBookings(){
		
		return crud.findAllBookings();
	}
	
	List<Customer> findAllCustomerBookingsById(Long id){
		return crud.findAllCustomerBookingsById(id);
	}
	
	Booking findBookingById(Long id){
		return crud.findBookingById(id);
	}
	
	public Booking create(Booking booking) throws ConstraintViolationException, ValidationException, Exception {
		validator.validateBooking(booking);
	
		return crud.create(booking);
	}
	
	Booking update(Booking booking) throws ConstraintViolationException, ValidationException, Exception {
		validator.validateBooking(booking);
	
		return crud.update(booking);
	}
	
	Booking delete(Booking booking) throws ConstraintViolationException, ValidationException, Exception {
		validator.validateBooking(booking);
	
		return crud.delete(booking);
	}
	
	

}
