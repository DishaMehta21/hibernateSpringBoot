package com.hibernatewithSpringBoot.Sevices;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;


import com.hibernatewithSpringBoot.EntityClasses.Customer;
import com.hibernatewithSpringBoot.EntityClasses.CustomerLedger;
import com.hibernatewithSpringBoot.EntityClasses.Schedule;
import com.hibernatewithSpringBoot.Utilities.Utility;

@Service
public class CreditcardService {
	
	Transaction transaction = null;

	public void addCustomer(Customer customer) {
		try(Session session = Utility.getSessionFactory().openSession()){
			transaction = session.beginTransaction();
			
			session.save(customer);
			
			transaction.commit();
			
		}catch(Exception ex) {
			System.out.println(ex);
		}
	}
	
	public void addLedgerWithPurchase(int amount,int custId) {
		CustomerLedger ld = null;
		int amt = -(amount);
		int balance=0;
		Customer c = getCustomer(custId);
		CustomerLedger le = getLedgerBalance(c.getcustomerId());
		if(le==null) {
			balance = amt;
		}else{
			balance = le.getBalance() + amt;
		}
		try(Session session = Utility.getSessionFactory().openSession()){
			transaction = session.beginTransaction();
						
			ld = new CustomerLedger();
			ld.setType("Purchase");
			ld.setAmount(amt);
			ld.setBalance(balance);
			ld.setStatus("Open");
			ld.setCustomer(c);
			session.save(ld);
			
			transaction.commit();
			
			addSchedule(ld,amount/4);
		}catch(Exception ex) {
			System.out.println(ex);
				if(transaction != null) {
					transaction.rollback();
				}
		}
				
	}
	
	public Customer getCustomer(int id) {
		Customer c =null;
		try(Session session = Utility.getSessionFactory().openSession()){
			transaction = session.beginTransaction();
			Criteria cr = session.createCriteria(Customer.class);
			cr.add(Restrictions.eq("customerId",id));
			Object result = cr.uniqueResult();
			c = (Customer)result;
			transaction.commit();
		}catch(Exception ex) {
				ex.printStackTrace();
		}
		return c;
	}
	
	public CustomerLedger getLedgerBalance(int customerid) {
		CustomerLedger cl =null;
		try(Session session = Utility.getSessionFactory().openSession()){
			transaction = session.beginTransaction();
			String str = "from CustomerLedger where customer_customerid = :customerid order by id desc";
			Query query = session.createQuery(str);
			query.setParameter("customerid", customerid);
			query.setMaxResults(1);
			List rs = query.list();
			if(rs.size()==0)
				return cl;
			cl = (CustomerLedger)rs.get(0);
			transaction.commit();
		}catch(Exception ex) {
				ex.printStackTrace();
		}
		return cl;
	}
	
	public void addSchedule(CustomerLedger ledger,int amount) {
		int cnt=1;
		try(Session session = Utility.getSessionFactory().openSession()){
			transaction = session.beginTransaction();
			
		while(cnt!=5) {
			Schedule schedule = new Schedule(cnt,"unpaid",amount);
			schedule.setCustomerLedger(ledger);
			session.save(schedule);
			cnt++;
		}
			transaction.commit();
			
		}catch(Exception ex) {
			System.out.println(ex);
				if(transaction != null) {
					transaction.rollback();
				}
		}
	}
	
	public String updateSchedulePayment(int customerId) {
		int amount=0;
		Schedule sc = null;
		int ledgerId = getLedgerDetails(customerId);
		if(ledgerId==0)
			return "This customer has not any open ledger";
		else {
			try(Session session = Utility.getSessionFactory().openSession()){
				transaction = session.beginTransaction();
				String str = "from Schedule where customerledger_id = :ledgerId and status = :status order by scheduleid asc";
				Query query = session.createQuery(str);
				query.setParameter("ledgerId", ledgerId);
				query.setParameter("status", "unpaid");
				query.setMaxResults(1);
				List rs = query.list();
				sc = (Schedule)rs.get(0);
				amount =sc.getAmount();
				sc.setStatus("paid");
				
				session.save(sc);
				
				transaction.commit();
				
				if(sc.getNumber()==4) {
					CompletePayment(ledgerId);
				}
				addToLedger(customerId,sc.getAmount());
				
			}catch(Exception ex) {
					ex.printStackTrace();
					System.out.println(ex);
			}
			return "Payment done successfully";
		}
	}
	
	public int getLedgerDetails(int customerId) {
		CustomerLedger cusLed = new CustomerLedger();
		int LedgerId = 0;
		try(Session session = Utility.getSessionFactory().openSession()){
			transaction = session.beginTransaction();
			String str = "from CustomerLedger where customer_customerid = :customerId and status =:status order by id asc";
			Query query = session.createQuery(str);
			query.setParameter("customerId", customerId);
			query.setParameter("status", "Open");
			query.setMaxResults(1);
			List rs = query.list();
			if(rs.size()==0)
				return LedgerId;
			cusLed = (CustomerLedger)rs.get(0);
			LedgerId = cusLed.getId();
			transaction.commit();	
			
		}catch(Exception ex) {
				ex.printStackTrace();
				System.out.println(ex);
		}
		return LedgerId;
	}
	
	public void CompletePayment(int ledgerId) {
		try(Session session = Utility.getSessionFactory().openSession()){
			session.beginTransaction();
			String str = "Update CustomerLedger set status = :status where id = :id";
			Query query = session.createQuery(str);
			query.setParameter("status", "Complete");
			query.setParameter("id", ledgerId);
			int res = query.executeUpdate();
			session.getTransaction().commit();
		}catch(Exception ex) {
				ex.printStackTrace();
				System.out.println(ex);
		}
	}
	
	public void addToLedger(int customerId,int amount) {
		CustomerLedger cd = new CustomerLedger();
		cd.setAmount(amount);
		cd.setType("Payment");
		cd.setStatus("Complete");
		Customer c = getCustomer(customerId);
		cd.setCustomer(c);
		try(Session session = Utility.getSessionFactory().openSession()){
			transaction = session.beginTransaction();
			session.save(cd);
			transaction.commit();
			
			int balance = getCustomerLedgerLatestBalance(customerId);
			UpdateLedgerBalance(cd.getId(),balance,amount);
			
		}catch(Exception ex) {
			System.out.println(ex);
				if(transaction != null) {
					transaction.rollback();
				}
		}
	}
		
	public int getCustomerLedgerLatestBalance(int customerId) {
		CustomerLedger cusLed = new CustomerLedger();
		int balance = 0;
		try(Session session = Utility.getSessionFactory().openSession()){
			transaction = session.beginTransaction();
			String str = "from CustomerLedger where customer_customerid = :customerId order by id desc";
			Query query = session.createQuery(str);
			query.setParameter("customerId", customerId);
			query.setMaxResults(2);
			List rs = query.list();
			cusLed = (CustomerLedger)rs.get(1);
			balance = cusLed.getBalance();
			System.out.println("balance---"+balance);
			transaction.commit();
			
		}catch(Exception ex) {
				ex.printStackTrace();
				System.out.println(ex);
		}
		return balance;
		
	}
		
	public void UpdateLedgerBalance(int ledgerId,int balance,int amount) {
		int res=0;
		int updatedBalance =0;
		try(Session session = Utility.getSessionFactory().openSession()){
			transaction = session.beginTransaction();
			CustomerLedger customerLedger = new CustomerLedger();
			String str = "Update CustomerLedger set balance = :balance where id = :ledgerId";
			Query query = session.createQuery(str);
			query.setParameter("ledgerId", ledgerId);
			updatedBalance = balance + amount;
			query.setParameter("balance", updatedBalance);
			res = query.executeUpdate();
			transaction.commit();
			
		}catch(Exception ex) {
				ex.printStackTrace();
				System.out.println(ex);
		}
	}

	public String fetchDetails(int customerid) {
		CustomerLedger cusLed = new CustomerLedger();
		int balance=0;
		String returnedValue="";
		try(Session session = Utility.getSessionFactory().openSession()){
			transaction = session.beginTransaction();
			String str = "from CustomerLedger where customer_customerid = :customerId order by id desc";
			Query query = session.createQuery(str);
			query.setParameter("customerId", customerid);
			query.setMaxResults(1);
			List rs = query.list();
			if(rs.size()==0) {
				returnedValue = "Customer does not exists";
				return returnedValue;
			}
			cusLed = (CustomerLedger)rs.get(0);
			balance = cusLed.getBalance();
			transaction.commit();
			
		}catch(Exception ex) {
				ex.printStackTrace();
				System.out.println(ex);
		}
		returnedValue = "customer's latest balance is " + balance;
		return returnedValue;
		
	}

	public String checkOpenBalance(int customerid) {
		CustomerLedger cusLed = new CustomerLedger();
		String returnedValue="";
		try(Session session = Utility.getSessionFactory().openSession()){
			transaction = session.beginTransaction();
			String str = "from CustomerLedger where customer_customerid = :customerId and status = :status";
			Query query = session.createQuery(str);
			query.setParameter("customerId", customerid);
			query.setParameter("status", "Open");
			List rs = query.list();
			if(rs.size()==0) {
				returnedValue = "Customer does not have any open ledger";
				return returnedValue;
			}
			transaction.commit();
			
		}catch(Exception ex) {
				ex.printStackTrace();
				System.out.println(ex);
		}
		returnedValue = "customer has open ledger";
		return returnedValue;
	}
}
