package org.jboss.quickstarts.wfk.hotel;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.customer.Customer;

public class HotelRepository {

    @Inject
    private @Named("logger") Logger log;

	@Inject
	private EntityManager em;

	/**
	 * returns link of all persisted objects within the hibernate db, finds all
	 * Hotel records, and order's them by name
	 * 
	 * @return List of Hotel objects
	 */
	List<Hotel> findAllHotels() {
		TypedQuery<Hotel> query = em.createNamedQuery(Hotel.FIND_ALL_HOTELS_WITH_INFORMATION, Hotel.class);
		return query.getResultList();
	}

	/**
	 * returns link of all persisted objects within the hibernate db, finds all
	 * Hotel records using the phoneNumber, as the Phone Number is unique
	 * 
	 * @return List of Hotel objects
	 */
	Hotel findHotelsByPhoneNumber(String phoneNumber) {
		TypedQuery<Hotel> query = em.createNamedQuery(Hotel.FIND_BY_PHONE_NUMBER, Hotel.class).setParameter("phoneNumber", phoneNumber);
		return query.getSingleResult();
	}
	/**
	 * <p>
	 * Returns a single Hotel object, specified by a Long id.
	 * <p/>
	 *
	 * @param id The id field of the Hotel to be returned
	 * @return The Hotel with the specified id
	 */

    /**
     * <p>Returns a single Hotel object, specified by a Long id.<p/>
     *
     * @param id The id field of the Contact to be returned
     * @return The Hotel with the specified id
     */
	Hotel findByHotelId(Long id) {
        return em.find(Hotel.class, id);
    }

	Hotel create(Hotel hotel)throws ConstraintViolationException, ValidationException, Exception {
	log.info("HotelRepository.create() - Creating " + hotel.getName() + " " + hotel.getPhoneNumber());
	em.persist(hotel);
	
	return hotel;
	}
	
    /**
     * <p>Updates an existing Hotel object in the application database with the provided Contact object.</p>
     *
     * <p>{@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     *
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     *
     * @param Hotel The Customer Hotel to be merged with an existing Hotel
     * @return The Hotel that has been merged
     */
    Hotel update(Hotel hotel) throws ConstraintViolationException, ValidationException, Exception{
    	log.info("HotelRepository.update() - updating " + hotel.getName() + " " + hotel.getPhoneNumber());

        // Either update the contact or add it if it can't be found.
        em.merge(hotel);

        return hotel;
    }
    
    Hotel delete(Hotel hotel) throws ConstraintViolationException, ValidationException, Exception{
		log.info("hotelReprository.delete() - deleting " + hotel.getName());
		if(hotel.getId() != null){	
            em.remove(em.merge(hotel));
		}else{
			log.info("hotel does not exist ");
		}
		
		
		return hotel;
	}
}
