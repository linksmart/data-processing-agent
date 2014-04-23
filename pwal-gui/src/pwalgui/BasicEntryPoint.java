package pwalgui;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.model.Accelerometer;
import it.ismb.pertlab.pwal.api.devices.model.Thermometer;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.internal.Pwal;

import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class BasicEntryPoint extends AbstractEntryPoint {
	
	static Pwal p=null;
	
    @Override
    protected void createContents(Composite parent) {
    	if(BasicEntryPoint.p == null)
    	{
    		ApplicationContext c=new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
    		BasicEntryPoint.p=(Pwal) c.getBean("PWAL");
    	}
    	for(Device d:p.listDevices())
    	{
    		d.getId();
        	if(d.getType().equals(DeviceType.THERMOMETER))
        	{
        		Thermometer term=(Thermometer) d;
            	Text t = new Text(parent, SWT.BOLD);
            	t.setText("\n"+d.getId()+" "+d.getType()+" "+term.getTemperature());        		
        	}else if(d.getType().equals(DeviceType.ACCELEROMETER)){
        		Accelerometer a=(Accelerometer) d;
        	}
    	}
    	/*
        parent.setLayout(new GridLayout(2, false));
        Button checkbox = new Button(parent, SWT.CHECK);
        checkbox.setText("Hello");
        Button button = new Button(parent, SWT.PUSH);
        button.setText("World");
        */
    }

}
