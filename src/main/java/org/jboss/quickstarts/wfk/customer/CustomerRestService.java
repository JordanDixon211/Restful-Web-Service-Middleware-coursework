package org.jboss.quickstarts.wfk.customer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.quickstarts.wfk.area.InvalidAreaCodeException;
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.contact.UniqueEmailException;
import org.jboss.quickstarts.wfk.util.RestServiceException;
import org.jboss.resteasy.annotations.cache.Cache;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/customers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/customers" , description = "Operations on Customers")
@Stateless
public class CustomerRestService {
    @Inject
    private @Named("logger") Logger log;
    
	@Inject
	private CustomerService service;

	/**   
	 * return all the Customers sorted by firstName
	 * @return Response contains list of Customers ordered by firstName
	 * */
	@GET
	@ApiOperation(value = "fetch all Customers", notes="Returns a JSON array of all stored Customers")
	public Response retrieveAllContacts() {
		List<Customer> customers = service.findAllOrderedByName();
        return Response.ok(customers).build();
	}
	
	
    /**
     * <p>Search for and return a customer identified by id.<p/>
     *
     *
     * @param id of the Customer
     * @return A Response containing a customer with that id
     */
	@GET
	@Cache
	@Path("/{id:[0-9]+}")
	@ApiOperation(
			value = "Fetch a customer by id",
			notes = "Returns a JSON representation fo a customer Object."
			)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Customer found"),
			@ApiResponse(code = 404, message = "Customer has not been found")
	})
	public Response getCustomerById(@ApiParam(value = "Id contact to be fetched" , allowableValues = "range[0, infinity]", required = true)
	@PathParam("id") long id) {
		
		Customer customer = service.findById(id);
		if(customer == null) {
			throw new RestServiceException("No Customer with id: " + id + " was found ", Response.Status.NOT_FOUND);
		}
        log.info("findById " + id + ": found customer = " + customer.toString());

		return Response.ok(customer).build();
	}
	
	@POST
	@ApiOperation(value = "Add a new customer to the database")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Customer created successfully"),
			@ApiResponse(code = 400, message = "Invalid Customer supplied in request body"),
			@ApiResponse(code = 409, message = "Customer supplied in rquest body conflicts with an existing Customer"),
			@ApiResponse(code = 500, message = "An unexpected error occured")
			
	})
	public Response createCustomer(@ApiParam(value = "JSON representation of Customer object to be added to database", required = true) 
	Customer customer) {
		
		if(customer == null) {
			throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
		}
		
		Response.ResponseBuilder builder; 
		
		try {
			service.create(customer);
			
			builder = Response.status(Response.Status.CREATED).entity(customer);

		} catch (ConstraintViolationException e) {
			// TODO Auto-generated catch block
			 Map<String, String> responseObj = new HashMap<>();

	            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
	                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
	            }
	            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);

		} catch(UniqueEmailException e) {
			Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "That email is already used, please use a unique email");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.CONFLICT, e);
		}catch(CustomerExistsException e) {
			Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "That email is already used, please use a unique email");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.CONFLICT, e);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new RestServiceException(e);
		}

		
        log.info("createCustomer completed. customer = " + customer.toString());

		return builder.build();
	}
	
	@PUT
	@Path("/{id:[0-9]+}")
	@ApiOperation(value = "Update a Customer in the database")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Customer updated sucessfully"),
			@ApiResponse(code = 400, message = "Invalid Customer supplied in request body"),
			@ApiResponse(code = 404, message = "Customer with id has not been found"),
			@ApiResponse(code = 409, message = "Customer with these details conflict with another existing customer"),
			@ApiResponse(code = 500, message = "An unexpected error occured whilst procesing the request")
	})
	public Response updateCustomer(@ApiParam(value = "JSON representation of a Customer object to be added to the database", required = true)
	Customer customer, @ApiParam(value = "id of the customer to be updated", allowableValues = "range[0,infinity]", required = true) @PathParam("id") long id){
		if(customer == null || customer.getId() == null) {
			throw new RestServiceException();
		}
		
		if(customer.getId() != null && customer.getId() != id) {
			throw new RestServiceException("Invalid Customer supplied in request body", Response.Status.BAD_REQUEST);
		}
		
		if(service.findById(customer.getId()) == null) {
			throw new RestServiceException("no Customer with this id", Response.Status.NOT_FOUND);
		}
		
		Response.ResponseBuilder builder;
		
		try {
			service.update(customer);
			builder = Response.ok(customer);

		} catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        } catch (UniqueEmailException e) {
            // Handle the unique constraint violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "That email is already used, please use a unique email");
            throw new RestServiceException("Contact details supplied in request body conflict with another Contact",
                    responseObj, Response.Status.CONFLICT, e);
        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }
		
		return builder.build();
	}
	
	 /**
     * <p>Deletes a customer using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 204 NO CONTENT or with a map of fields, and related errors.</p>
     *
     * @param id The Long parameter value provided as the id of the customer to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{id:[0-9]+}")
    @ApiOperation(value = "Delete a Customer from the database")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "The Customer has been successfully deleted"),
            @ApiResponse(code = 400, message = "Invalid Customer id supplied"),
            @ApiResponse(code = 404, message = "Customer with id not found"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response deleteCustomer(
            @ApiParam(value = "Id of Customer to be deleted", allowableValues = "range[0, infinity]", required = true)
            @PathParam("id")
            long id) {

        Response.ResponseBuilder builder;

        Customer customer = service.findById(id);
        if (customer == null) {
            // Verify that the contact exists. Return 404, if not present.
            throw new RestServiceException("No customer with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        try {
            service.delete(customer);

            builder = Response.noContent();

        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }
        log.info("deletecustomer completed. customer = " + customer.toString());
        return builder.build();
    }
	
}