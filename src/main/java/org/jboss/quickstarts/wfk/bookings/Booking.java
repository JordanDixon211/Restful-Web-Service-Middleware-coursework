package org.jboss.quickstarts.wfk.bookings;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.hotel.Hotel;

@Entity 
@NamedQueries({
	@NamedQuery(name = Booking.FIND_ALL_BOOKING_WITH_ALL_INFORMATION, query = "SELECT b  FROM Booking b"),
	@NamedQuery(name = Booking.FIND_BOOKINGS_BY_ID, query = "SELECT b FROM Booking b WHERE b.id = :bookingid"),
	@NamedQuery(name = Booking.FIND_ALL_CUSTOMER_BOOKING_BY_ID, query = "SELECT  b.customer FROM Booking b WHERE b.customer.id = :customerid")
})
@XmlRootElement
@Table (name = "booking")
public class Booking implements Serializable{
	public static final String FIND_ALL_BOOKING_WITH_ALL_INFORMATION = "Booking.findAllBookingWithAllInformation";
	public static final String FIND_ALL_CUSTOMER_BOOKING_BY_ID = "Booking.findAllCustomerBookingById";
	public static final String FIND_BOOKINGS_BY_ID = "Booking.findBookingsById";

    private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	
    @NotNull
    @Future(message = "bookings cannot be made in the past")
    @Column(name = "booking_date")
    @Temporal(TemporalType.DATE)
    private Date bookingDate;	
    

	@ManyToOne
	@JoinColumn(name = "customer")
	private Customer customer;
	

	@ManyToOne
	@JoinColumn(name = "hotel")
	private Hotel hotel;
    
    
	public Date getBookingDate() {
		return bookingDate;
	}


	public void setBookingDate(int day, int month, int year) {
		Calendar cal = new GregorianCalendar(day, month, year);
		this.bookingDate = cal.getTime();
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Customer getCustomer() {
		return customer;
	}


	public void setCustomer(Customer customer) {
		this.customer = customer;
	}


	public Hotel getHotel() {
		return hotel;
	}


	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}
}
