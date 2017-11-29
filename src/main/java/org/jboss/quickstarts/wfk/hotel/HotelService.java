package org.jboss.quickstarts.wfk.hotel;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.jboss.quickstarts.wfk.contact.ContactValidator;
import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.customer.CustomerRepository;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;

public class HotelService {
	/**
	 * @author Jordan
	 * <p>This Service assumes the Control responsibility in the ECB pattern.</p>
	 */
    @Inject
    private @Named("logger") Logger log;

    @Inject
    private HotelRepository crud;
    
    @Inject
    private HotelValidator validator;
    
    
    /**
     * Returns a list of all persisted Hotel Objects, along with all data
     * **/
    List<Hotel> findAllHotels(){
    	return crud.findAllHotels();
    }
    
    /**
     * <p>Returns a single Hotel object, specified by a Long id.<p/>
     *
     * @param id The id field of the Hotel to be returned
     * @return The Hotel with the specified id
     */
    Hotel findById(Long id) {
        return crud.findByHotelId(id);
    }
    
    
    Hotel findByPhoneNumber(String phoneNumber) {
    	return crud.findHotelsByPhoneNumber(phoneNumber);
    }

    Hotel create(Hotel hotel) throws ConstraintViolationException, ValidationException, Exception {
    	log.info("HotelService.create() - Creating"  + hotel.getName() + " " + hotel.getPhoneNumber()); 
    	
    	validator.valdateHotel(hotel);
    	
    	return crud.create(hotel);
    }
    
    Hotel update(Hotel hotel) throws ConstraintViolationException, ValidationException, Exception {
    	log.info("HotelService.update() - updating"  + hotel.getName() + " " + hotel.getPhoneNumber()); 
    	
    	validator.valdateHotel(hotel);

    	
    	return crud.update(hotel);
    }  
    
	
    Hotel delete(Hotel hotel) throws Exception {
        log.info("delete() - Deleting " + hotel.toString());

        Hotel deletedHotel = null;

        if (hotel.getId() != null) {
        	deletedHotel = crud.delete(hotel);
        } else {
            log.info("delete() - No ID was found so can't Delete.");
        }

        return deletedHotel;
    }
}
