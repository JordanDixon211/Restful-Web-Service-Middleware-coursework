package org.jboss.quickstarts.wfk.hotel;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.jboss.quickstarts.wfk.customer.CustomerRepository;
import org.jboss.quickstarts.wfk.hotel.UniquePhoneNumberException;

public class HotelValidator {
	
	@Inject
	private Validator validator;
	
	@Inject
	private HotelRepository crud;
	
	public void valdateHotel(Hotel hotel) throws ConstraintViolationException, ValidationException {
		Set<ConstraintViolation<Hotel>> violations = validator.validate(hotel);
		
		if(!violations.isEmpty()) {
			throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
		}
		
		if(phoneNumberAlreadyExists(hotel.getPhoneNumber(), hotel.getId())) {
            throw new UniquePhoneNumberException("Unique PhoneNumber Violation");
		}
		
		if(hotelAlreadyExists(hotel.getId())) {
            throw new HotelExistsException("This hotel with id" + hotel.getId() + " already exists");
		}
	}
	
	private boolean phoneNumberAlreadyExists(String phoneNumber, Long id) {
			Hotel hotel = null;
			Hotel hotelId = null;
		
			try {
				hotel = crud.findHotelsByPhoneNumber(phoneNumber);
			}catch(NoResultException e) {
				
			}
			
			try {
				if(hotel != null && id != null) {
					hotelId = crud.findByHotelId(id);
					if(hotelId != null && hotelId.getPhoneNumber().equals(phoneNumber)) {
						hotel = null;
					}
				}
			}catch(NoResultException e) {

			}			
		return hotel != null;
	}
	
	private boolean hotelAlreadyExists(Long id) {
		Hotel hotel = null;
		
		if(id != null) {
			try {
				hotel = crud.findByHotelId(id);
			}catch(NoResultException e) {
				
			}
		}
		
		return hotel != null;
		
	}	
	
}
