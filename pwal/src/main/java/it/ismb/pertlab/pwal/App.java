package it.ismb.pertlab.pwal;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.model.FillLevel;
import it.ismb.pertlab.pwal.api.devices.model.OxyMeter;
import it.ismb.pertlab.pwal.api.devices.model.Resistance;
import it.ismb.pertlab.pwal.api.devices.model.Semaphore;
import it.ismb.pertlab.pwal.api.devices.model.Semaphore.State;
import it.ismb.pertlab.pwal.api.devices.model.Thermometer;
import it.ismb.pertlab.pwal.api.devices.model.VehicleCounter;
import it.ismb.pertlab.pwal.api.devices.model.VehicleSpeed;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.internal.Pwal;

import java.util.Scanner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Pwal start" );
        ApplicationContext ctx=new ClassPathXmlApplicationContext(new String[]{"applicationContext-pwal.xml"});
        Pwal p=(Pwal) ctx.getBean("PWAL");
//        CnetDataPusher cnet = new CnetDataPusher(5);
//        p.addPwalDeviceListener(cnet);
        Scanner input = new Scanner(System.in);
        String command;
        do
        {
        	command=input.nextLine();
        	switch(command){
        		case "L":
        			System.out.println("Devices list:");
        			int i=0;
        			for (Device k : p.getDevicesList()) {
						System.out.println((i++)+") pwalId: "+ k.getPwalId() +" id: "+k.getId()+" type: "+k.getType());
					}
        			break;
        		case "Q":
        			System.out.println("Inserisci l'indice da interrogare:");
        			command=input.nextLine();
        			Device de=(Device)p.getDevicesList().toArray()[Integer.parseInt(command)];
        			switch (de.getType()) {
					case DeviceType.THERMOMETER:
						Thermometer t=(Thermometer) de;
                		System.out.println("This is a thermometer id="+de.getId()+"  temp="+t.getTemperature());						
						break;
					case DeviceType.OXYGEN_METER:
                		OxyMeter om=(OxyMeter)de;
                		System.out.println("This is an oxymeter: saturation="+om.getSaturation());
						break;
					case DeviceType.SEMAPHORE:
		 				System.out.println("Digita state per interrogare il semaforo o GREEN, YELLOW, RED per forzare il valore");
        				command=input.nextLine();
        				Semaphore s=(Semaphore) de;
        				if(command.equals("state"))
        				{
        					System.out.println("Stato del semaforo: "+s.getState());
        				}else{
        					s.setState(State.valueOf(command));
        				}
						break;
					case DeviceType.VEHICLE_COUNTER:
						VehicleCounter vc = (VehicleCounter)de;
						System.out.println("This is a Vehicle counter sensor. "
								+ "Id: "+ vc.getId()
								+ ", Latitude: " + vc.getLocation().getLat()
								+ ", Longitude: " + vc.getLocation().getLon()
								+ ", NetworkType: " + vc.getNetworkType()
								+ ", Count: " + vc.getCount()
								+ ", Occupancy: " + vc.getOccupancy() + "%");
						break;
					case DeviceType.VEHICLE_SPEED:
						VehicleSpeed vs = (VehicleSpeed)de;
						System.out.println("This is a Vehicle speed sensor. "
								+ "Id: "+ vs.getId()
								+ ", Latitude: " + vs.getLocation().getLat()
								+ ", Longitude: " + vs.getLocation().getLon()								+ ", AverageSpeed: " + vs.getAverageSpeed() + "Km/h"
								+ ", NetworkType: " + vs.getNetworkType()
								+ ", Count: " + vs.getCount()
								+ ", MedianSpeed: " + vs.getMedianSpeed() + "Km/h"
								+ ", Occupancy: " + vs.getOccupancy() + "%");
						break;
					case DeviceType.FILL_LEVEL_SENSOR:
						FillLevel fl = (FillLevel)de;
						System.out.println("This is a fill level sensor"
								+ "Id: " + fl.getId()
								+ ", NetworkType " + fl.getNetworkType()
								+ ", Level:" +fl.getLevel() 
								+ ", Depth: " + fl.getDepth());
						break;
					case DeviceType.RESISTANCE:
						Resistance r=(Resistance) de;
						System.out.println("This is a resistance"
								+" ohm: "+r.getOhm());
					default:
						break;
					}
        	}

        }while(!command.equals("exit"));
        input.close();
        ((ConfigurableApplicationContext)ctx).close();
    }
}
