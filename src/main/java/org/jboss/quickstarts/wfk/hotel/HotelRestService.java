package org.jboss.quickstarts.wfk.hotel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
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
import org.jboss.quickstarts.wfk.contact.UniqueEmailException;
import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.util.RestServiceException;
import org.jboss.resteasy.annotations.cache.Cache;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/hotel")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/hotel")
@Stateless
public class HotelRestService {
    @Inject
    private @Named("logger") Logger log;
	
	@Inject
	private HotelService service;
	
	
	@GET
	@Path("/{id:[0-9]+}")
	@Cache
	@ApiOperation(value = "fetch all Hotels by id", notes = "Return JSON array of all stored Hotels")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Hotel found"),
			@ApiResponse(code = 404, message = "hotel with id not found")
	})
	public Response retrieveHotelById(@ApiParam(value = "Id of hotel to be fetched", allowableValues = "range[0, infinity]", required = true)
								 @PathParam("id") Long id) {
		
		Hotel hotel = service.findById(id);
		if(hotel == null) {
            throw new RestServiceException("No Hotel with the id " + id + " was found!", Response.Status.NOT_FOUND);
		}
        log.info("findById " + id + ": found Hotel = " + hotel.toString());

		return Response.ok(hotel).build();
	}

	@GET
	@ApiOperation(value = "fetch all hotels", notes = "Return JSON array of all stored")
	public Response retriveAllHotels() {
		List<Hotel> hotelList;
		
		hotelList = service.findAllHotels();
		
		return Response.ok(hotelList).build();
	}
	
	@GET
	@Cache
	@Path("/{phoneNumber:[0-9]+}")
	@ApiOperation(
			value = "fetch all Hotels by phoneNumber" , 
			notes = "Return JSON array of all stored Hotels"
			)
	@ApiResponses(value = {
					@ApiResponse(code = 200, message = "Hotel found"),
					@ApiResponse(code = 404, message = "Hotel with this phone number has not been found")
			})
	public Response getHotelByPhoneNumber(@ApiParam(value = "Phone number of Hotel to be fetched", allowableValues = "range[0,infinity]")
	@PathParam("phoneNumber") 
	String phoneNumber) {
		Hotel hotel = null;
		
		if(phoneNumber == null) {
            throw new RestServiceException("No Hotel with the phoneNumber " + phoneNumber + " was found!", Response.Status.NOT_FOUND);
		}
		
		hotel = service.findByPhoneNumber(phoneNumber);
		
		if(hotel == null) {
            throw new RestServiceException("No Hotel with the phoneNumber " + phoneNumber + " was found!", Response.Status.NOT_FOUND);
		}
		
		
        log.info("findByphoneNumber " + phoneNumber + ": found hotel = " + hotel.toString());

		
		return Response.ok(hotel).build();	
	}
	
	@POST
	@ApiOperation(value = "add new Hotel to the database")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Hotel created successfully"),
			@ApiResponse(code = 400, message = "invalid hotel supplied in request body"),
			@ApiResponse(code = 409, message = "Hotel supplied in request body conflicts with existing hotel"),
			@ApiResponse(code = 500, message = "an unexpected error occured while processing the request")
	})
	public Response createHotel(@ApiParam(value = "JSON reporesentation of a hotel object to be added to the database", required = true)
	Hotel hotel) {
		
		if(hotel == null) {
			throw new RestServiceException("bad Request", Response.Status.BAD_REQUEST);
		}
		
		Response.ResponseBuilder builder;
		
		   try {
	            //add new hotel
	            service.create(hotel);

	            // Create an OK Response and pass the contact back in case it is needed.
	            builder = Response.status(Response.Status.CREATED).entity(hotel);


	        } catch (ConstraintViolationException ce) {
	            //Handle bean validation issues
	            Map<String, String> responseObj = new HashMap<>();

	            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
	                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
	            }
	            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
	        } catch (UniquePhoneNumberException e) {
	            // Handle the unique constraint violation
	            Map<String, String> responseObj = new HashMap<>();
	            responseObj.put("phoneNumber", "That phoneNumber is already used, please use a unique PhoneNumber");
	            throw new RestServiceException("Hotel details supplied in request body conflict with another Hotel",
	                    responseObj, Response.Status.CONFLICT, e);
	        } catch (Exception e) {
	            // Handle generic exceptions
	            throw new RestServiceException(e);
	        }
		
		
		log.info("createHotel completed. hotel = " + hotel.toString());
		return builder.build();	
	}
	
	@PUT
	@Path("/{id:[0-9]+}")
	@ApiOperation(value = "Update a hotel in the database")
	@ApiResponses( value = {
			@ApiResponse(code = 200, message = "Hotel updated sucessfully"),
			@ApiResponse(code = 400, message = "Invalid Hotel supplied in request body"),
			@ApiResponse(code = 404, message = "Hotel with the id has not been found"),
			@ApiResponse(code = 409, message = "Hotel details supplied in request conflict with another"),
			@ApiResponse(code = 500, message = "An unexpected error has occured processing the request")
	})
	public Response updateHotel(
			@ApiParam(value = "id of Hotel to be updated", allowableValues = "range[0, infinity]", required = true) 
			@PathParam("id")
			long id, @ApiParam(value =  "JSON represenation of Hotel object to be updated in the database", required = true)
			Hotel hotel) {
		
	
		//check if a valid id has been given 
		if(hotel == null || hotel.getId() < 1 ) {
			throw new RestServiceException("No Hotel with that id exists" + Response.Status.NOT_FOUND);
		}
		
		if(hotel.getId() != id) {
			  Map<String, String> responseObj = new HashMap<>();
	            responseObj.put("id", "The Hotel ID in the request body must match that of the Hotel being updated");
			throw new RestServiceException("details supplied in request body conflict with another hotel",responseObj, Response.Status.CONFLICT);
		}
		
		
		if(service.findById(id) == null) {
			throw new RestServiceException("No Hotel with this id " + id + "was found", Response.Status.NOT_FOUND);
		}
		Response.ResponseBuilder builder;


        try {
            // Apply the changes the Contact.
            service.update(hotel);

            // Create an OK Response and pass the contact back in case it is needed.
            builder = Response.ok(hotel);


        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        } catch (UniquePhoneNumberException e) {
            // Handle the unique constraint violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("phoneNumber", "That phoneNumber is already used, please use a unique PhoneNumber");
            throw new RestServiceException("Hotel details supplied in request body conflict with another Hotel",
                    responseObj, Response.Status.CONFLICT, e);
        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }
		
        log.info("updateHotel completed. Hotel = " + hotel.toString());
		return builder.build();
	}
	
	 /**
     * <p>Deletes a Hotel using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 204 NO CONTENT or with a map of fields, and related errors.</p>
     *
     * @param id The Long parameter value provided as the id of the Hotel to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{id:[0-9]+}")
    @ApiOperation(value = "Delete a Hotel from the database")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "The Hotel has been successfully deleted"),
            @ApiResponse(code = 400, message = "Invalid Hotel id supplied"),
            @ApiResponse(code = 404, message = "Hotel with id not found"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response deleteHotel(
            @ApiParam(value = "Id of Hotel to be deleted", allowableValues = "range[0, infinity]", required = true)
            @PathParam("id")
            long id) {

        Response.ResponseBuilder builder;

        Hotel hotel = service.findById(id);
        if (hotel == null) {
            throw new RestServiceException("No hotel with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        try {
            service.delete(hotel);

            builder = Response.noContent();

        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }
        log.info("deletehotel completed. hotel = " + hotel.toString());
        return builder.build();
    }
	
	
	
}
