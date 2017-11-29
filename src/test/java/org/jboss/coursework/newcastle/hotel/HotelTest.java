package org.jboss.coursework.newcastle.hotel;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Date;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.contact.ContactRestService;
import org.jboss.quickstarts.wfk.contact.UniqueEmailException;
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
public class HotelTest {
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
                .addAsWebInfResource("arquillian-ds.xml").addClasses(Hotel.class, HotelRestService.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
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
    
    @Test
    @InSequence(1)
    public void testCreate() throws Exception {
        Hotel hotel = createHotelInstance("Jordan","O2", "029384938493");
        Response response =  hotelRestService.createHotel(hotel);
        
        
        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info(" New Hotel was persisted and returned status " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(2)
    public void testInvalidCreatePhoneFormat() {
        Hotel hotel = createHotelInstance("Jordan","O2", "02938400938493");

        try {
        	hotelRestService.createHotel(hotel);
        	fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.BAD_REQUEST, e.getStatus());
            log.info("Invalid Hotel register attempt failed with return code " + e.getStatus());
        }

    }
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(3)
    public void testDuplicatePhoneNumber() {
        Hotel hotel = createHotelInstance("Jordan","O2", "02938400938493");
        
        Hotel hotelCpy = createHotelInstance("Jordan","O2", "02938400938493");
    	hotelRestService.createHotel(hotel);

        try {
        	hotelRestService.createHotel(hotelCpy);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.CONFLICT, e.getStatus());
            log.info("Invalid Hotel Creat attempt failed with return code " + e.getStatus());
        }

    }
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(4)
    public void testUpdate() throws Exception {
        // Register an initial user
    	Hotel hotel = createHotelInstance("Jordan","O3", "029384938493");
        Response response =  hotelRestService.createHotel(hotel);
        Hotel HotelUpdateobj = createHotelInstance("Jordan","O3", "029384918493");

        
        Response HotelUpdate =  hotelRestService.updateHotel(1, HotelUpdateobj);
        
        assertEquals("Unexpected response status", 200, response.getStatus());
        log.info(" New Hotel was persisted and returned status " + response.getStatus());

    }

   





}
