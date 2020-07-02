package com.hibernatewithSpringBoot.EntityClasses;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;


@Entity
public class Customer {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private int customerId;
		private String firstName;
		private String lastName;
		private String dob;
		
		@OneToMany(mappedBy="customer")
		private Collection<CustomerLedger> customerLedgers = new ArrayList<CustomerLedger>();
		public Customer(){
			
		}
		public Customer(String firstName, String lastName, String dob) {
			super();
			this.firstName = firstName;
			this.lastName = lastName;
			this.dob = dob;
		}
		
		public Collection<CustomerLedger> getCustomerLedgers() {
			return customerLedgers;
		}

		public void setCustomerLedgers(Collection<CustomerLedger> customerLedgers) {
			this.customerLedgers = customerLedgers;
		}



		public int getcustomerId() {
			return customerId;
		}

		public void setId(int customerId) {
			this.customerId = customerId;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getDob() {
			return dob;
		}

		public void setDob(String dob) {
			this.dob = dob;
		}

}
