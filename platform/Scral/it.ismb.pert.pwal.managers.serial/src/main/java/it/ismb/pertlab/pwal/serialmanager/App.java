package it.ismb.pertlab.pwal.serialmanager;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Hello world!
 *
 */
public class App implements SerialPortEventListener
{
	SerialPort sp=null;
	public App(SerialPort sp){
		this.sp=sp;
	}
    public static void main( String[] args )
    {
    	SerialPort port=null;
		try {
			port= new SerialPort("/dev/ttyACM0");
			port.openPort();
	    	port.setParams(9600,
	    		    SerialPort.DATABITS_8,
	    		    SerialPort.STOPBITS_1,
	    		    SerialPort.PARITY_NONE);
	    	port.addEventListener(new App(port));
	    	
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	
	    try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			port.closePort();
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	public void serialEvent(SerialPortEvent arg0) {
		if(arg0.isRXCHAR()){//If data is available
			try {
				System.out.println("Bytes ready: "+arg0.getEventValue());
				String buffer = sp.readString(7);
				System.out.println(buffer);
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
