package org.jboss.quickstarts.wfk.guestbooking;

import javax.inject.Inject;

import org.jboss.quickstarts.wfk.bookings.Booking;
import org.jboss.quickstarts.wfk.customer.Customer;

public class GuestBooking {
	
	@Inject
	private Customer customer;
	
	@Inject
	private Booking booking;

	public void setCustomer(Customer customer){
		this.customer = customer;
	}
	
	public void setBooking(Booking booking){
		this.booking = booking;
	}
	
	public Customer getCustomer(){
		return customer;
	}
	
	
	public Booking getBooking(){
		return booking;
	}
}
