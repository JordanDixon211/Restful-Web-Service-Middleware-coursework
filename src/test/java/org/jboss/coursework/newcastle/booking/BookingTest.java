package org.jboss.coursework.newcastle.booking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.quickstarts.wfk.bookings.Booking;
import org.jboss.quickstarts.wfk.bookings.BookingRestService;
import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.customer.CustomerRestService;
import org.jboss.quickstarts.wfk.hotel.Hotel;
import org.jboss.quickstarts.wfk.hotel.HotelRestService;
import org.jboss.quickstarts.wfk.util.RestServiceException;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BookingTest {
	@Inject
	BookingRestService bookingRestService;
	@Inject
    CustomerRestService customerRestService;
	@Inject
    HotelRestService hotelRestService;
    @Inject
    @Named("logger") Logger log;
    @Deployment
    public static Archive<?> createTestArchive() {
        // This is currently not well tested. If you run into issues, comment line 67 (the contents of 'resolve') and
        // uncomment 65. This will build our war with all dependencies instead.
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
//                .importRuntimeAndTestDependencies()
                .resolve(
                        "io.swagger:swagger-jaxrs:1.5.15"
        ).withTransitivity().asFile();

        return ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addPackages(true, "org.jboss.quickstarts.wfk")
                .addAsLibraries(libs)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("arquillian-ds.xml").addClasses(Customer.class, CustomerRestService.class, Hotel.class, HotelRestService.class, Booking.class, BookingRestService.class)
            
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }
    
    /**
     * <p>A utility method to construct Customer object for use in
     * testing. This object is not persisted.</p>
     *
     * @param name The first name of the Customer being created
     * @param email     The email address of the Customer being created
     * @param phone     The phone number of the Customer being created
     * @return The Customer object create
     */
    private Customer createCustomerInstance(String name, String email, String phone) {
    Customer customer = new Customer();
    customer.setName(name);
    customer.setEmail(email);
    customer.setPhoneNumber(phone);
    return customer;
    }
    
    
    /**
     * <p>A utility method to construct Hotel object for use in
     * testing. This object is not persisted.</p>
     *
     * @param name The first name of the Hotel being created
     * @param postcode     The postcode address of the Hotel being created
     * @param phone     The phone number of the Hotel being created
     * @return The Hotel object create
     */
    private Hotel createHotelInstance(String name, String postCode, String phone) {
    Hotel hotel = new Hotel();
    hotel.setName(name);
    hotel.setPostCode(postCode);
    hotel.setPhoneNumber(phone);
    return hotel;
    }
    
    
    /**
     * <p>A utility method to construct Booking object for use in
     * testing. This object is not persisted.</p>
     *
     * @param Date The Booking being created
     * @param Hotel     The Hotel being created
     * @param Customer     The Customer being created
     * @return The Booking object create
     */
    private Booking createBookingInstance(int day, int month, int year,  Hotel hotel, Customer customer) {
    		Booking booking = new Booking();
    		booking.setBookingDate(day, month, year);
    		booking.setCustomer(customer);
    		booking.setHotel(hotel);
    return booking;
    }
    
    @Test
    @InSequence(1)
    public void testCreate() throws Exception {
    	Customer customer = createCustomerInstance("Jordan" , "jordan.dixon@hotmail.co.uk", "03948594039");
    	Hotel hotel =  createHotelInstance("Jordan" , "O2", "03948594039");    	
        Booking booking = createBookingInstance(25, 11, 2019, hotel, customer);
        
        Response responseHotel =  hotelRestService.createHotel(hotel);
        Response responseCustomer =  customerRestService.createCustomer(customer);
        Response responseBooking =  bookingRestService.createBooking(booking);

        
        assertEquals("Unexpected response status", 201, responseBooking.getStatus());
        log.info(" New Booking was persisted and returned status " + responseBooking.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(2)
    public void testInvalidBookingNoCustomer() {
    	Hotel hotel =  createHotelInstance("Jordan" , "O2", "0348594039");    	
        Booking booking = createBookingInstance(24, 11, 2019, hotel, null);
        
        Response responseHotel =  hotelRestService.createHotel(hotel);
        Response responseBooking =  bookingRestService.createBooking(booking);


        try {
        	hotelRestService.createHotel(hotel);
        	fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.CONFLICT, e.getStatus());
            assertEquals("Unexpected response body", 4, e.getReasons().size());
            log.info("Invalid Booking register attempt failed with return code " + e.getStatus());
        }

    }
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(3)
    public void testInvalidBookingNoHotel() {
       	Customer customer = createCustomerInstance("Jordan" , "jordan.dixon@hotmail.co.uk", "03948594039");
        Booking booking = createBookingInstance(24, 11, 2019, null, customer);
        
        Response responseCustomer =  customerRestService.createCustomer(customer);

        try {
            Response responseBooking =  bookingRestService.createBooking(booking);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.BAD_REQUEST, e.getStatus());
            assertEquals("Unexpected response body", 4, e.getReasons().size());
            log.info("Invalid Hotel Creat attempt failed with return code " + e.getStatus());
        }

    }
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(4)
    public void testBookingInPast() {
    	Customer customer = createCustomerInstance("Jordan" , "jordan.dixon@hotmail.co.uk", "03948594039");
    	Hotel hotel =  createHotelInstance("Jordan" , "O2", "03948594039");    	
        Booking booking = createBookingInstance(22, 11, 2019, hotel, customer);
        
        Response responseHotel =  hotelRestService.createHotel(hotel);
        Response responseCustomer =  customerRestService.createCustomer(customer);

        try {
            Response responseBooking =  bookingRestService.createBooking(booking);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.BAD_REQUEST, e.getStatus());
            assertEquals("Unexpected response body", 4, e.getReasons().size());
            log.info("Invalid Booking Creat attempt failed with return code " + e.getStatus());
        }

    }
    
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(5)
    public void testDuplicateBookingOnSameDay() {
    	Customer customer = createCustomerInstance("Jordan" , "jordan.dixon@hotmail.co.uk", "03948594039");
    	Hotel hotel =  createHotelInstance("Jordan" , "O2", "03948594039");    	
        Booking booking = createBookingInstance(22, 11, 2019, hotel, customer);
        
        Response responseHotel =  hotelRestService.createHotel(hotel);
        Response responseCustomer =  customerRestService.createCustomer(customer);

        try {
            Response responseBooking =  bookingRestService.createBooking(booking);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.BAD_REQUEST, e.getStatus());
            assertEquals("Unexpected response body", 4, e.getReasons().size());
            log.info("Invalid Booking Creat attempt failed with return code " + e.getStatus());
        }
    }
}
