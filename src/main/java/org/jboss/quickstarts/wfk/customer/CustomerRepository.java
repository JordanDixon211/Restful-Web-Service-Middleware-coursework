package org.jboss.quickstarts.wfk.customer;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.jboss.quickstarts.wfk.contact.Contact;

import java.util.List;
import java.util.logging.Logger;

/**
 * repository class, connects service/control layers
 * @author Jordan
 *
 */
public class CustomerRepository {

    @Inject
    private @Named("logger") Logger log;
	
	@Inject 
	private EntityManager em;
	
    /**
     *returns link of all persisted objects within the hibernate db, finds all cutomer records,
     *and order's them by name
     * @return List of Customer objects
     */
	List<Customer> findAllCustomersOrderedByName(){
		TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_ALL_WITH_ALL_INFORMATION, Customer.class);
		return query.getResultList();
	}

	public Customer findByEmail(String email) {
		// TODO Auto-generated method stub
	    TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_CUSTOMER_BY_EMAIL, Customer.class).setParameter("email", email);
        return query.getSingleResult();	
        }
	
	   /**
	    * <p>Returns a single Customer object, specified by a Long id.<p/>
	    *
	    * @param id The id field of the Contact to be returned
	    * @return The Customer with the specified id
	    */
	Customer findById(Long id) {
     return em.find(Customer.class, id);
	}
	
	Customer create(Customer customer) throws ConstraintViolationException, ValidationException, Exception{
		log.info("customerReprository.create() - Creating " + customer.getName());
		em.persist(customer);
		
		return customer;
	}
	
	
	Customer delete(Customer customer) throws ConstraintViolationException, ValidationException, Exception{
		log.info("customerReprository.create() - Creating " + customer.getName());
		if(customer.getId() != null){	
            em.remove(em.merge(customer));
		}else{
			log.info("Customer does not exist ");
		}
		
		
		return customer;
	}

    
    /**
     * <p>Updates an existing Customer object in the application database with the provided Contact object.</p>
     *
     * <p>{@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     *
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     *
     * @param Customer The Customer object to be merged with an existing Customer
     * @return The Customer that has been merged
     */
    Customer update(Customer customer)throws ConstraintViolationException, ValidationException, Exception{
        log.info("customerReprository.update() - Updating " + customer.getName());
        // Either update the contact or add it if it can't be found.
        	
        
        em.merge(customer);

        return customer;
    }
}
