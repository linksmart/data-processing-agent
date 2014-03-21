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
        String inputString=input.nextLine();
        for(Device d:p.listDevices())
        {
        	System.out.println(d.getId()+" "+d.getType());
        	if(d.getType().equals(DeviceType.THERMOMETER))
        	{
        		Thermometer t=(Thermometer) d;
        		System.out.println("This is a thermometer: temp="+t.getTemperature());
        	}else if(d.getType().equals("pwal:Oxymeter")){
        		OxyMeter om=(OxyMeter)d;
        		System.out.println("This is an oxymeter: saturation="+om.getSaturation());
        	}
        }
    }
}
