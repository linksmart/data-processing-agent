package eu.linksmart.event.datafusion.esper.impl;

import eu.linksmart.api.event.datafusion.ComplexEventHandler;

import java.util.Map;

class StatementSubscriber{
	
		private ComplexEventHandler CEPHandler;
		public StatementSubscriber(ComplexEventHandler CEPHandler){
			this.CEPHandler= CEPHandler;
			
		}
		public void update(Map<String,Object> row){
			
			 String st ="e";
			 st +="g";
			System.out.println("aqui");
		}
		 public void update(Object[] args) {
			 String st ="e";
			 st +="g";
			 System.out.println("aqui");
	        }
		 public void update(Object args) {
			 String st ="e";
			 st +="g";
			 System.out.println("aqui");
	        }
		 public void update(String args) {
			 String st ="e";
			 st +="g";
			 System.out.println("aqui");
	        }
	}