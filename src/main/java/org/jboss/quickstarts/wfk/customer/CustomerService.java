package org.jboss.quickstarts.wfk.customer;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

public class CustomerService {
	/**
	 * @author Jordan
	 * <p>This Service assumes the Control responsibility in the ECB pattern.</p>
	 */
    @Inject
    private @Named("logger") Logger log;

    @Inject
    private CustomerRepository crud;
	
	@Inject
	private CustomerValidator validator;
    
    public CustomerService() {
    }
    
    /**
     * Returns a list of all persisted Customer Objects, sorted by Name
     * **/
    List<Customer> findAllOrderedByName(){
    	return crud.findAllCustomersOrderedByName();
    }
    
    /**
     * <p>Returns a single Customer object, specified by a Long id.<p/>
     *
     * @param id The id field of the Contact to be returned
     * @return The Customer with the specified id
     */
    Customer findById(Long id) {
        return crud.findById(id);
    }

   public Customer create(Customer customer) throws ConstraintViolationException, ValidationException, Exception {
    	log.info("CustomerService.create() - Creating"  + customer.getName() ); 
    	
    	//INSERT VALIDADOTOR HERE
    	validator.valdateCustomer(customer);

    	return crud.create(customer);
    }
    
    Customer update(Customer customer) throws ConstraintViolationException, ValidationException, Exception {
    	log.info("CustomerService.update() - updating"  + customer.getName()); 
    	
    	//INSERT VALIDADOTOR HERE
    	validator.valdateCustomer(customer);
    	return crud.update(customer);
    }
    
    Customer delete(Customer customer) throws Exception {
        log.info("delete() - Deleting " + customer.toString());

        Customer deletedCustomer = null;

        if (customer.getId() != null) {
        	deletedCustomer = crud.delete(customer);
        } else {
            log.info("delete() - No ID was found so can't Delete.");
        }

        return deletedCustomer;
    }
}
