package org.jboss.quickstarts.wfk.bookings;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.hotel.Hotel;

public class BookingRepository {

    @Inject
    private @Named("logger") Logger log;
	
	@Inject
	private EntityManager em;
	
	
	Booking findBookingById(long id) {
		TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_BOOKINGS_BY_ID, Booking.class).setParameter("bookingid", id);
		return query.getSingleResult();
	}
	
	List<Booking> findAllBookings(){
		TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_ALL_BOOKING_WITH_ALL_INFORMATION, Booking.class);
		return query.getResultList();
	}
	
	List<Customer> findAllCustomerBookingsById(Long id){
		TypedQuery<Customer> query = em.createNamedQuery(Booking.FIND_ALL_CUSTOMER_BOOKING_BY_ID, Customer.class).setParameter("customerid", id);
		return query.getResultList();
	}

	
	
	Booking create(Booking booking) {
		log.info("BookingRepository.create() - Creating " + booking.getHotel() + " " + booking.getCustomer());
		em.persist(booking);
		
		return booking;
	}
	
	
	Booking update(Booking booking) {
		log.info("BookingRepository.update() - updating " + booking.getHotel() + " " + booking.getCustomer());

		em.merge(booking);
		                                    
		return booking;
	}
	
	Booking delete(Booking booking) {
		if(booking.getId() != null) {
			em.remove(em.merge(booking));
		}else {
			log.info("BookingRepository.delete() - No ID found, record not removed.");
		}
		return booking;
	}
}
