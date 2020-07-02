package com.hibernatewithSpringBoot.EntityClasses;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Schedule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int scheduleId;
	private int number;
	private String status;
	private int amount;
	
	@ManyToOne
	private CustomerLedger customerLedger;
	
	public Schedule() {

	}

	public Schedule(int number, String status, int amount) {
		super();
		this.number = number;
		this.status = status;
		this.amount = amount;
	}

	public int getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public CustomerLedger getCustomerLedger() {
		return customerLedger;
	}

	public void setCustomerLedger(CustomerLedger customerLedger) {
		this.customerLedger = customerLedger;
	}

}
