package pwalgui;

import java.util.HashMap;

import javax.swing.text.AbstractDocument.Content;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.model.Accelerometer;
import it.ismb.pertlab.pwal.api.devices.model.Thermometer;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.internal.Pwal;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.rap.rwt.client.service.BrowserNavigation;
import org.eclipse.rap.rwt.client.service.BrowserNavigationEvent;
import org.eclipse.rap.rwt.client.service.BrowserNavigationListener;
import org.eclipse.rap.rwt.template.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.qos.logback.core.Context;


public class PwalHomePage extends AbstractEntryPoint {

	static Pwal p=null;

	/*
	 * Layout defines
	 * (non-Javadoc)
	 * @see org.eclipse.rap.rwt.application.AbstractEntryPoint#createContents(org.eclipse.swt.widgets.Composite)
	 */
	private Composite header;
	private Composite exampleParent;
	private Color backgroundColor;
	private Color backgroundColor1;

	@Override
	protected void createContents(Composite parent) {


		/*
		 * Create Layout for PWAL 
		 */
		parent.setLayout( new FormLayout() );
		backgroundColor = new Color( parent.getDisplay(), 0x31, 0x61, 0x9C );
		backgroundColor1 = new Color( parent.getDisplay(), 0x20, 0x50, 0x50 );

		header = new Composite( parent, SWT.NONE );
		header.setBackground( backgroundColor );
		header.setBackgroundMode( SWT.INHERIT_DEFAULT );
		header.setLayoutData( createLayoutDataForHeader() );

		Label label = new Label( header, SWT.NONE );
		label.setText( "Physical World Adaptation Layer:PWAL" );
		label.setForeground( parent.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
		label.setBounds( 40, 30, 500, 30 );

		exampleParent = new Composite( parent, SWT.NONE );
		exampleParent.setBackground( backgroundColor1 );
		exampleParent.setLayoutData( createLayoutDataForExampleParent() );

		Label t = new Label(exampleParent, SWT.NONE);
		t.setText("List of Connected Devices");
		//t.setForeground( parent.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
		t.setBounds( 40, 30, 500, 30 );


		//createPwalInstance();
		if(PwalHomePage.p == null)
		{
			ApplicationContext c=new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
			//((ClassPathXmlApplicationContext) c).close();
			PwalHomePage.p=(Pwal) c.getBean("PWAL");

		}
		
		Label l1 = new Label(exampleParent, SWT.NONE);
		l1.setBounds( 40, 60, 500, 50 );
		Label l2 = new Label(exampleParent, SWT.NONE);
		l2.setBounds( 40, 90, 500, 50 );
		
		/*
		 * Access the PWAL devices and display them somehow
		 */

		for(Device d:p.listDevices())
		{
			d.getId();
			if(d.getType().equals(DeviceType.THERMOMETER))
			{
				Thermometer temp=(Thermometer) d;
				l1.setText("\n Sensor: "+d.getId()+" "+d.getType()+" :"+temp.getTemperature()+" C");  

			} else if(d.getType().equals(DeviceType.ACCELEROMETER)){
				Accelerometer a=(Accelerometer) d;
				l2.setText("\n Sensor:"+d.getId()+" "+d.getType()+" x,y,z"+a.getXAcceleration()+a.getYAcceleration()+a.getZAcceleration());   
			}

		}

	}



	/*
	 * Header Layout 
	 */
	private FormData createLayoutDataForHeader() {
		FormData layoutData = new FormData();
		layoutData.left = new FormAttachment( 0, 0 );
		layoutData.right = new FormAttachment( 100, 0 );
		layoutData.top = new FormAttachment( 0, 0 );
		layoutData.height = 80;
		return layoutData;
	}

	private FormData createLayoutDataForExampleParent() {
		FormData layoutData = new FormData();
		layoutData.top = new FormAttachment( header, 0 );
		layoutData.left = new FormAttachment( 0, 0 );
		layoutData.right = new FormAttachment( 100, 0 );
		layoutData.bottom = new FormAttachment( 100, 0 );
		//layoutData.height = 0;
		return layoutData;
	}


}
