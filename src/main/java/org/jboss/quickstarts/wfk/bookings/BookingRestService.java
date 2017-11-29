package org.jboss.quickstarts.wfk.bookings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.util.RestServiceException;
import org.jboss.resteasy.annotations.cache.Cache;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/booking")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/booking", description = "Operations about bookings")
@Stateless
public class BookingRestService {
	
    @Inject
    private @Named("logger") Logger log;
	
	@Inject
	private BookingService service;
	
	 /**
     * <p>Return all the Booking. </p>
    *
     * @return A Response containing a list of bookings
     */
    @GET
    @ApiOperation(value = "Fetch all Bookings", notes = "Returns a JSON array of all stored Booking objects.")
    public Response retrieveAllBookings() {
    	//List contains Booking objects
    	List<Booking> bookings = service.findAllBookings();
        
        
        return Response.ok(bookings).build();
    }

    /**
     * <p>Search for and return a Booking identified by id.<p/>
     *
     *
     * @param id parameter value
     * @return A Response containing a single Booking
     */
    @GET
    @Cache
    @Path("/booking/{id:[0-9]+}")
    @ApiOperation(
            value = "Fetch a Booking by id",
            notes = "Returns a JSON representation of the Booking object ."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message ="Booking found"),
            @ApiResponse(code = 404, message = "Booking not found")
    })
    public Response retrieveBookingById(
            @ApiParam(value = "id of booking to be fetched", required = true)
            @PathParam("id")
            Long id) {

        Booking booking;
        try {
        	booking = service.findBookingById(id);
        } catch (NoResultException e) {
            // Verify that the booking exists. Return 404, if not present.
            throw new RestServiceException("no Booking with this id exists", Response.Status.NOT_FOUND);
        }
        
        if(booking == null) {
            throw new RestServiceException("no Booking with this id exists", Response.Status.NOT_FOUND);

        }
        return Response.ok(booking).build();
    }

    /**
     * <p>Search for and return customers identified by a customer id.</p>
     *
     * @param id The long parameter value provided as a customer id
     * @return A Response containing a list of customers which have bookings
     */
    @GET
    @Cache
    @Path("/{customer:[0-9]+}")
    @ApiOperation(
            value = "Fetch a Customer by id",
            notes = "Returns a JSON representation of the Booking object with the provided id."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message ="Customer found"),
            @ApiResponse(code = 404, message = "customer with id not found")
    })
    public Response retrieveCustomersById(
            @ApiParam(value = "Id of customer to be fetched", allowableValues = "range[0, infinity]", required = true)
            @PathParam("customer")
            long id) {

        List<Customer> booking = service.findAllCustomerBookingsById(id);
        if (booking.isEmpty()) {
            throw new RestServiceException("No Customers with the id " + id + " was found within the bookings!", Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found booking for customer = " + booking.toString());

        return Response.ok(booking).build();
    }
    

    /**
     * <p>Creates a new Booking from the values provided. Performs validation and will return a JAX-RS response with
     * either 201 (Resource created) or with a map of fields, and related errors.</p>
     *
     * @param The Booking object, constructed automatically from JSON input
     * @return A Response indicating the outcome of the create operation
     */
    @SuppressWarnings("unused")
    @POST
    @ApiOperation(value = "Add a new Booking to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Booking created successfully."),
            @ApiResponse(code = 400, message = "Invalid Booking supplied in request body"),
            @ApiResponse(code = 409, message = "Booking supplied in request body conflicts with an existing Booking"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response createBooking(
            @ApiParam(value = "JSON representation of Booking object to be added to the database", required = true)
            Booking booking) {


        if (booking == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;

        try {
            // Go add the new booking.
            service.create(booking);

            // Create a "Resource Created" 201 Response and pass the booking back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(booking);


        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);

        }  catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }

        log.info("createBooking completed. booking = " + booking.toString());
        return builder.build();
    }

    /**
     * <p>Updates the Booking with the ID provided in the database. Performs validation, and will return a JAX-RS response
     * with either 200 (ok), or with a map of fields, and related errors.</p>
     *
     * @param  The Booking object, constructed automatically from JSON input
     * @param id The long parameter value provided as the id of the Booking to be updated
     * @return A Response indicating the outcome of the create operation
     */
    @PUT
    @Path("/{id:[0-9]+}")
    @ApiOperation(value = "Update a Booking in the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Booking updated successfully"),
            @ApiResponse(code = 400, message = "Invalid Booking supplied in request body"),
            @ApiResponse(code = 404, message = "Booking with id not found"),
            @ApiResponse(code = 409, message = "Booking details supplied in request body conflict with another existing Booking"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response updateBooking(
            @ApiParam(value = "Id of Booking to be updated", allowableValues = "range[0, infinity]", required = true)
            @PathParam("id")
            long id,
            @ApiParam(value = "JSON representation of Booking object to be updated in the database", required = true)
            Booking booking) {

        if (booking == null || booking.getId() == null) {
            throw new RestServiceException("Invalid Booking supplied in request body", Response.Status.BAD_REQUEST);
        }

        if (booking.getId() != null && booking.getId() != id) {
            // The client attempted to update the read-only Id. This is not permitted.
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("id", "The Booking ID in the request body must match that of the booking being updated");
            throw new RestServiceException("booking details supplied in request body conflict with another booking",
                    responseObj, Response.Status.CONFLICT);
        }

        if (service.findBookingById(id) == null) {
            // Verify that the booking exists. Return 404, if not present.
            throw new RestServiceException("No  with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        Response.ResponseBuilder builder;

        try {
             service.update(booking);

            builder = Response.ok(booking);


        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }

        log.info("updateBooking completed. booking = " + booking.toString());
        return builder.build();
    }

    /**
     * <p>Deletes a Booking using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 204 NO CONTENT or with a map of fields, and related errors.</p>
     *
     * @param id The Long parameter value provided as the id of the Booking to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{id:[0-9]+}")
    @ApiOperation(value = "Delete a Booking from the database")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "The Booking has been successfully deleted"),
            @ApiResponse(code = 400, message = "Invalid Booking id supplied"),
            @ApiResponse(code = 404, message = "Booking with id not found"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response deleteBooking(
            @ApiParam(value = "Id of Booking to be deleted", allowableValues = "range[0, infinity]", required = true)
            @PathParam("id")
            long id) {

        Response.ResponseBuilder builder;

        Booking booking = service.findBookingById(id);
        if (booking == null) {
            // Verify that the booking exists. Return 404, if not present.
            throw new RestServiceException("No booking with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        try {
            service.delete(booking);

            builder = Response.noContent();

        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }
        log.info("deletebooking completed. booking = " + booking.toString());
        return builder.build();
    }
}
