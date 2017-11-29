package org.jboss.coursework.newcastle.customer;

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
import org.jboss.quickstarts.wfk.util.RestServiceException;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CustomerTest {
    @Inject
    CustomerRestService customerRestService;
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
                .addAsWebInfResource("arquillian-ds.xml").addClasses(Customer.class, CustomerRestService.class)
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
    
    @Test
    @InSequence(1)
    public void testCreate() throws Exception {
        Customer customer = createCustomerInstance("Jordan","Jordan@hotmail.com", "04958493849");
        Response response =  customerRestService.createCustomer(customer);
        
        
        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info(" New Customer was persisted and returned status " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(2)
    public void testInvalidCreatePhoneFormat() {
        Customer customer = createCustomerInstance("Jordan","Jordan@hotmail.com", "029384565656938493");

        try {
        	customerRestService.createCustomer(customer);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.BAD_REQUEST, e.getStatus());
            log.info("Invalid Customer register attempt failed with return code " + e.getStatus());
        }

    }
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(3)
    public void testInvalidCreateEmail() {
        Customer customer = createCustomerInstance("Jordan","Jordanhotmail.com", "029384938493");

        try {
        	customerRestService.createCustomer(customer);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.BAD_REQUEST, e.getStatus());
            log.info("Invalid Customer register attempt failed with return code " + e.getStatus());
        }

    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(4)
    public void testDuplicateEmail() throws Exception {
        // Register an initial user
        Customer customer = createCustomerInstance("Jordan","Jordan@hotmail.com", "029384538493");
    	customerRestService.createCustomer(customer);

        // Register a different user with the same email
        Customer sameCustomer = createCustomerInstance("Jordan","Jordan@hotmail.com", "029384538493");

        try {
        	customerRestService.createCustomer(sameCustomer);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.CONFLICT, e.getStatus());
            assertTrue("Unexecpted error. Should be Unique email violation", e.getCause() instanceof UniqueEmailException);
            assertEquals("Unexpected response body", 1, e.getReasons().size());
            log.info("Duplicate Customer register attempt failed with return code " + e.getStatus());
        }

    }
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(5)
    public void testUpdate() throws Exception {
        // Register an initial user
        Customer customer = createCustomerInstance("Jordan","Jordan@hotmail.com", "029384938493");
        Response response =  customerRestService.createCustomer(customer);
        Customer customerUpdateobj = createCustomerInstance("Jordan","Jordan@gmail.com", "029384938493");

        
        Response customerUpdate =  customerRestService.updateCustomer(customerUpdateobj, 1);
        
        assertEquals("Unexpected response status", 200, response.getStatus());
        log.info(" New Customer was persisted and returned status " + response.getStatus());

    }



}
