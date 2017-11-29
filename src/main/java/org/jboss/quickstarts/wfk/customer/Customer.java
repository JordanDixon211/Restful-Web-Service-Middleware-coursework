package org.jboss.quickstarts.wfk.customer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.quickstarts.wfk.bookings.Booking;
import org.jboss.quickstarts.wfk.contact.Contact;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author Jordan
 * class which provides implementation of a customer to be stored in database. shows how customers
 * are represented in a customer database
 *
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Customer.FIND_ALL_WITH_ALL_INFORMATION, query = "SELECT c  FROM Customer c ORDER BY c.name"),
    @NamedQuery(name = Customer.FIND_CUSTOMER_BY_EMAIL, query = "SELECT c FROM Customer c WHERE c.email = :email")
})
@XmlRootElement
@Table(name = "customer" , uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Customer implements Serializable{
	
	public static final String FIND_ALL_WITH_ALL_INFORMATION = "Customer.findAllWithAllInformation";
	public static final String FIND_CUSTOMER_BY_EMAIL = "Customer.findCustomerByEmail";

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    
    @NotNull
    @Size(min = 1, max = 50)
    @Pattern(regexp = "[A-Za-z-']+", message = "Please use a name without numbers or specials")
    @Column(name = "name")
    private String name;
    
    @NotNull
    @NotEmpty
    @Email(message = "The email address must be in the format of name@domain.com")
    private String email;
    
    @NotNull
    @Pattern(regexp = "^(0)\\d[0-9]{9}")
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @JsonIgnore
    @OneToMany(mappedBy="customer", orphanRemoval = true)
    private List<Booking> booking = new ArrayList<Booking>();
    
    
    public void addBookings(Booking booking) {
    	this.booking.add(booking);
    }
    
    @JsonIgnore
    public List<Booking> getBookings(){
    	return booking;
    }
    
    public Long getId() {
    	return id;
    }
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof Customer)) {
			 return false;
		}
		
		Customer cust = (Customer) obj;
		return this.email == null ? cust.getEmail() == null : this.email.equals(cust.getEmail()); 
	}
    
	public int hashCode() {
		int hc = 57; 
		
		return  57 * (this.email == null ? 0 : email.hashCode());
	}
	
	

}
