package com.hibernatewithSpringBoot.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hibernatewithSpringBoot.EntityClasses.Customer;
import com.hibernatewithSpringBoot.Sevices.CreditcardService;

@RestController
public class CreditcardController {
	
	@Autowired
	private CreditcardService creditCardService;
	
	@RequestMapping(method=RequestMethod.POST,value="/customer")
	public void addCustomer(@RequestBody Customer customer) {
		creditCardService.addCustomer(customer);
	}
	
	@RequestMapping(method=RequestMethod.POST,value="/purchase")
	public void addPurchase(@RequestParam(value ="customerid") int customerid,@RequestParam(value="amount") int amount){
		creditCardService.addLedgerWithPurchase(amount, customerid);
	}
	
	@RequestMapping(method=RequestMethod.POST,value="/payment")
	public String addPayment(@RequestParam(value ="customerid") int customerid){
		return creditCardService.updateSchedulePayment(customerid);
	}
	
	@RequestMapping("/customers/{customerid}")
	public String fetchDetails(@PathVariable int customerid) {
		return creditCardService.fetchDetails(customerid);
	}
	
	@RequestMapping("/customersOpenBalance/{customerid}")
	public String checkCustomerOpenBalance(@PathVariable int customerid) {
		return creditCardService.checkOpenBalance(customerid);
	}
}
 	