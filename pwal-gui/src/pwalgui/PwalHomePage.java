package pwalgui;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.model.Accelerometer;
import it.ismb.pertlab.pwal.api.devices.model.Thermometer;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.internal.Pwal;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.rap.rwt.template.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class PwalHomePage extends AbstractEntryPoint {
	
	static Pwal p=null;
	
    @Override
    protected void createContents(Composite parent) {
    	
    	
    	/*
    	 * Create Layout for PWAL 
    	 */
    	RowLayout parentLayout = new RowLayout();
    	parentLayout.marginLeft = 5;
    	parentLayout.marginTop = 5;
    	parentLayout.marginRight = 5;
    	parentLayout.marginBottom = 5;
        parent.setLayout( parentLayout );
        
        /*
         * Create PWAL instance only once, to avoid Serial Port Manager error
         */
    	if(PwalHomePage.p == null)
    	{
    		ApplicationContext c=new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
    		PwalHomePage.p=(Pwal) c.getBean("PWAL");
    	}
    	
    	/*
    	 * Access the PWAL devices and display them somehow
    	 */
    	for(Device d:p.listDevices())
    	{
    		d.getId();
        	if(d.getType().equals(DeviceType.THERMOMETER))
        	{
        		Thermometer term=(Thermometer) d;
            	//Text t = new Text(parent, SWT.BOLD);
            	//t.setText("\n"+d.getId()+" "+d.getType()+" "+term.getTemperature());        		
        	}else if(d.getType().equals(DeviceType.ACCELEROMETER)){
        		Accelerometer a=(Accelerometer) d;
        	}
    	}
    	
    	  /*	
    	Table table = new Table( parent, SWT.FULL_SELECTION );
    	new TableColumn( table, SWT.NONE ); // important
    	Template template = new Template();
    	TextCell textCell = new TextCell( template );
    	textCell.setLeft( 0 ).setRight( 0 ).setTop( 0 ).setBottom( 0 );
    	textCell.setBindingIndex( 0 );
    	table.setData( RWT.ROW_TEMPLATE, template );
    	TableItem item = new TableItem( table, SWT.NONE );
    	item.setText( 0, "Data in the first column" );
    	*/
    }

}
