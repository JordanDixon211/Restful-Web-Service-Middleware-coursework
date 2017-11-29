package org.jboss.quickstarts.wfk.guestbooking;
 
import java.util.HashMap;
import java.util.Map;
 
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.quickstarts.wfk.bookings.BookingExistsException;
import org.jboss.quickstarts.wfk.bookings.BookingService;
import org.jboss.quickstarts.wfk.customer.CustomerExistsException;
import org.jboss.quickstarts.wfk.customer.CustomerService;
import org.jboss.quickstarts.wfk.hotel.HotelExistsException;
import org.jboss.quickstarts.wfk.util.RestServiceException;
 
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
 
@Path("/guestbookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/guestbookings")
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class GuestBookingRestService {
 
       @Resource
       UserTransaction userTransaction;
      
       @Inject
       BookingService bookingService;
      
       @Inject
       CustomerService customerService;
      
      
    @POST
    @ApiOperation(value = "Add a new booking and customer to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Guest booking created successfully."),
            @ApiResponse(code = 400, message = "Invalid guest booking supplied in request body"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 409, message = "guest booking supplied in request body conflicts with an existing guest booking"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
       public Response createGuestBooking(
                    @ApiParam(value = "JSON representation of Booking object and Customer object to be added", required = true)
                    GuestBooking guestBooking) {
 
       //perform checks on the given guest booking input
       if(guestBooking == null || guestBooking.getBooking() == null) {
             throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
       }
       try {
             try {
                    userTransaction.begin();
                    
                    
                   
                    customerService.create(guestBooking.getCustomer());
                    
                    guestBooking.getBooking().setCustomer(guestBooking.getCustomer());
                    bookingService.create(guestBooking.getBooking());
                   
                    userTransaction.commit();
             } catch (CustomerExistsException  e) {
                    userTransaction.rollback();
                    throw new RestServiceException(e.getMessage(), Response.Status.CONFLICT, e);
             } catch (HotelExistsException  e) {
                 userTransaction.rollback();
                 throw new RestServiceException(e.getMessage(), Response.Status.CONFLICT, e);
          }  catch (BookingExistsException e) {
                 userTransaction.rollback();
                 throw new RestServiceException(e.getMessage(), Response.Status.CONFLICT, e);
          } catch (ConstraintViolationException e) {
                Map<String, String> responseObj = new HashMap<>();
 
                for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                    responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
                }
               
                userTransaction.rollback();
                throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
             } catch (Exception e) {
                userTransaction.rollback();
                    throw new RestServiceException(e);
                    }
       } catch (SystemException e) {
             throw new RestServiceException(e);
       }
      
       Response.ResponseBuilder builder = Response.status(Response.Status.CREATED).entity(guestBooking);
       return builder.build();
       }
}