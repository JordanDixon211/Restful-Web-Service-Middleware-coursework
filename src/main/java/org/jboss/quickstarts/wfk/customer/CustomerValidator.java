package org.jboss.quickstarts.wfk.customer;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.jboss.quickstarts.wfk.contact.UniqueEmailException;

public class CustomerValidator {

	@Inject
	private Validator validator;
	
	@Inject
	private CustomerRepository crud;
	
	public void valdateCustomer(Customer customer)throws ConstraintViolationException,ValidationException{
		//bean Validator, checks for issues.
		Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
		
		if(!violations.isEmpty()) {
			throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
		}
		
		if(emailAlreadyExists(customer.getEmail(), customer.getId())) {
			throw new UniqueEmailException("Unique Email Violation");
		}
		
		
		if(customerAlreadyExists(customer.getId())) {
			throw new CustomerExistsException("customer Already Exists");
		}
	}
	
	private boolean emailAlreadyExists(String email, Long id) {
		Customer customer = null;
		Customer customerWithId = null;

		try {
			customer = crud.findByEmail(email);
		}catch(NoResultException e) {
			
		}
		
		if(customer != null && id != null) {
			try {
				customerWithId = crud.findById(id);
				if(customerWithId != null && customerWithId.getEmail().equals(email)) {
					customer = null;
				}
			}catch(NoResultException e) {
				
			}
		}
		
		return customer != null;
		
	}
	
	private boolean customerAlreadyExists(Long id) {
		Customer customer = null;
		
		if(id != null) {
			try {
				customer = crud.findById(id);	
			}catch(NoResultException e) {
				
			}
		}
		
		return customer != null ? true : false;
		
	}
}
