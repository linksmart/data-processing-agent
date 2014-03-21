package it.ismb.pertlab.pwal;

import java.util.Scanner;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.model.OxyMeter;
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
                		System.out.println("This is a thermometer id="+de.getId()+"  temp="+t.getTemperature());
        			}else if(de.getType().equals(DeviceType.OXYGEN_METER)){
                		OxyMeter om=(OxyMeter)de;
                		System.out.println("This is an oxymeter: saturation="+om.getSaturation());
        			}
        			break;
        	}

        }while(!command.equals("exit"));
    }
}
