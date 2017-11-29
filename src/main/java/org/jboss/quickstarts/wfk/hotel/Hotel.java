package org.jboss.quickstarts.wfk.hotel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.quickstarts.wfk.bookings.Booking;
import org.jboss.quickstarts.wfk.customer.Customer;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@NamedQueries({
	@NamedQuery(name = Hotel.FIND_ALL_HOTELS_WITH_INFORMATION, query = "SELECT h FROM Hotel h"),
	@NamedQuery(name = Hotel.FIND_BY_PHONE_NUMBER, query = "SELECT h FROM Hotel h where h.phoneNumber = :phoneNumber")
})
@XmlRootElement
@Table(name = "hotel" , uniqueConstraints = @UniqueConstraint(columnNames = "phoneNumber"))
public class Hotel implements Serializable{
	public static final String FIND_ALL_HOTELS_WITH_INFORMATION = "Hotel.findAllHotelsWithInformation";
	public static final String FIND_BY_PHONE_NUMBER = "Hotel.findByPhoneNumber";

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    
    @NotNull
    @Size(min = 1 , max = 50)
    @Pattern(regexp = "[A-Za-z-']+", message = "Please use a name without numbers or specials")
    @Column(name = "name")
	private String name;
    
    @NotNull
    @Pattern(regexp = "^(0)\\d[0-9]{9}")
    @Size(min = 11, max = 11)
	private String phoneNumber;
    
    @NotNull
    @Size(min = 1 , max = 50)
    @Pattern(regexp = "[A-Z][0-9]")
    @Column(name = "postcode")
	private String postCode;
    
    
    @JsonIgnore
    @OneToMany(mappedBy="hotel", orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<Booking>();
    
    public void addBookings(Booking booking){
    	this.bookings.add(booking);
    }
    
    @JsonIgnore
    public List<Booking> getBookings(){
    	return bookings;
    }
    
	public Long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
		result = prime * result + ((postCode == null) ? 0 : postCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Hotel other = (Hotel) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (phoneNumber == null) {
			if (other.phoneNumber != null)
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		if (postCode == null) {
			if (other.postCode != null)
				return false;
		} else if (!postCode.equals(other.postCode))
			return false;
		return true;
	}
	
	
    
    
}
