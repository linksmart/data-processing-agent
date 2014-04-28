package it.ismb.pertlab.pwal;

import java.util.Scanner;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.model.HumiditySensor;
import it.ismb.pertlab.pwal.api.devices.model.LightSensor;
import it.ismb.pertlab.pwal.api.devices.model.OxyMeter;
import it.ismb.pertlab.pwal.api.devices.model.PressureSensor;
import it.ismb.pertlab.pwal.api.devices.model.Thermometer;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.internal.Pwal;

import org.springframework.context.ApplicationContext;
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
        ApplicationContext ctx=new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
        Pwal p=(Pwal) ctx.getBean("PWAL");
        Scanner input = new Scanner(System.in);
        String command;
        do
        {
        	command=input.nextLine();
        	switch(command){
        		case "L":
        			System.out.println("Devices list:");
        			int i=0;
        			for(Device d:p.listDevices())
        		    {
			        	System.out.println((i++)+") id: "+d.getId()+" type: "+d.getType());
        		    }
        			break;
        		case "Q":
        			System.out.println("Inserisci l'indice da interrogare:");
        			command=input.nextLine();
        			Device de=(Device)p.listDevices().toArray()[Integer.parseInt(command)];
        			if(de.getType().equals(DeviceType.THERMOMETER))
        			{
        				Thermometer t=(Thermometer) de;
                		System.out.println("This is a thermometer id="+t.getId()+"  temp="+t.getTemperature()
                				+ " " + (t.getUnit()==null ? "" : t.getUnit().getSymbol())
                				+ (t.getUpdatedAt()==null ? "" : " updated at "+ t.getUpdatedAt()));
        			}else if(de.getType().equals(DeviceType.OXYGEN_METER)){
                		OxyMeter om=(OxyMeter)de;
                		System.out.println("This is an oxymeter: saturation="+om.getSaturation()
                				+ " " + (om.getUnit()==null ? "" : om.getUnit().getSymbol())
                				+ (om.getUpdatedAt()==null ? "" : " updated at "+ om.getUpdatedAt()));
        			} else if(de.getType().equals(DeviceType.HUMIDITY_SENSOR)) {
        				HumiditySensor hs = (HumiditySensor) de;
        				System.out.println("This is an humidity sensor: humidity="+hs.getHumidity()
                				+ " " + (hs.getUnit()==null ? "" : hs.getUnit().getSymbol())
                				+ (hs.getUpdatedAt()==null ? "" : " updated at "+ hs.getUpdatedAt()));
        			} else if(de.getType().equals(DeviceType.LIGHT_SENSOR)) {
        				LightSensor ls = (LightSensor) de;
        				System.out.println("This is a light sensor: light="+ls.getLight()
                				+ " " + (ls.getUnit()==null ? "" : ls.getUnit().getSymbol())
                				+ (ls.getUpdatedAt()==null ? "" : " updated at "+ ls.getUpdatedAt()));
        			} else if(de.getType().equals(DeviceType.PRESSURE_SENSOR)) {
        				PressureSensor ps = (PressureSensor) de;
        				System.out.println("This is a pressure sensor: pressure="+ps.getPressure()
                				+ " " + (ps.getUnit()==null ? "" : ps.getUnit().getSymbol())
                				+ (ps.getUpdatedAt()==null ? "" : " updated at "+ ps.getUpdatedAt()));
        			}
        			break;
        	}

        }while(!command.equals("exit"));
    }
}
