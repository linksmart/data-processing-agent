package it.ismb.pertlab.pwal.serialmanager;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;


public class SerialManager extends DevicesManager implements SerialPortEventListener{

	private SerialPort port=null;
	private String buffer=new String();
	
	@Override
	public void run() {

    	try {
			port= new SerialPort("/dev/ttyACM0");
			port.openPort();
	    	port.setParams(9600,
	    		    SerialPort.DATABITS_8,
	    		    SerialPort.STOPBITS_1,
	    		    SerialPort.PARITY_NONE);
	    	port.addEventListener(this);
	    	
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	synchronized (this) {
    		try {
				this.wait();
			} catch (Exception e) {
				
			}
		}

		try {
			port.closePort();
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String readValue()
	{
		String ret=null;
		synchronized (this.buffer) {
			ret=this.buffer;
		}
		return ret;
	}
	
	@Override
	public void serialEvent(SerialPortEvent arg0) {
		if(arg0.isRXCHAR()){//If data is available
			try {
				synchronized (this.buffer) {
					this.buffer = port.readString(7);					
				}
            }
            catch (SerialPortException ex) {
            	System.out.println(ex);
            }
        }else if(arg0.isCTS()){//If CTS line has changed state
            if(arg0.getEventValue() == 1){//If line is ON
                System.out.println("CTS - ON");
            }
            else {
                System.out.println("CTS - OFF");
            }
        }
        else if(arg0.isDSR()){///If DSR line has changed state
            if(arg0.getEventValue() == 1){//If line is ON
                System.out.println("DSR - ON");
            }
            else {
                System.out.println("DSR - OFF");
            }
        }
	}	

}
