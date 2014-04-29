package pwalgui;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.model.Accelerometer;
import it.ismb.pertlab.pwal.api.devices.model.Thermometer;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.internal.Pwal;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.rap.rwt.template.Template;
import org.eclipse.rap.rwt.template.TextCell;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class PwalDevicesListTree extends AbstractEntryPoint {

	private Composite header;
	private Composite exampleParent;
	private Color backgroundColor_blue;
	private Color backgroundColor_green;
	private static Pwal p=null;

	public int createUI() {
		/* Display display = new Display();
	      Shell shell = new Shell( display );
	      shell.setLayout( new GridLayout( 5, false ) );

	      Button button = new Button( shell, SWT.PUSH );
	      button.setText( "Hello world!" );

	     // shell.pack();
		 * 
		 */

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(2, false));

		Label nameLabel = new Label(shell,SWT.BOLD);
		Font boldFont = new Font( nameLabel.getDisplay(), new FontData( "Arial",30, SWT.BOLD ) );
		nameLabel.setFont( boldFont );
		nameLabel.setText("PWAL:Physical World Adaptation Layer");

		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		
	/*	Text nameText = new Text(shell, SWT.BORDER);
		
		nameText.setLayoutData(gridData);
		nameText.setText("Text grows horizontally");*/

	/*	Label addressLabel = new Label(shell, SWT.NONE);
		addressLabel.setText("Address:");
		gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		addressLabel.setLayoutData(gridData);*/

	/* Text addressText = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;
		addressText.setLayoutData(gridData);
		addressText.setText("This text field and the List\nbelow share any excess space.");
		*/

		Label DeviceListLabel = new Label(shell, SWT.NONE);
		DeviceListLabel.setText("List of PWAL Devices available currently");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		DeviceListLabel.setLayoutData(gridData);

		List DeviceList = new List(shell, SWT.BORDER | SWT.MULTI);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;
		DeviceList.setLayoutData(gridData);
		
		
		//createPwalInstance();
		if(PwalDevicesListTree.p == null)
		{
			ApplicationContext c=new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
			//((ClassPathXmlApplicationContext) c).close();
			PwalDevicesListTree.p=(Pwal) c.getBean("PWAL");

		}
		for(Device d:p.listDevices())
		{
			d.getId();
			if(d.getType().equals(DeviceType.THERMOMETER))
			{
				Thermometer temp=(Thermometer) d;
				DeviceList.add("\n Sensor: "+d.getId()+" "+d.getType()+" :"+temp.getTemperature()+" C");  

			} else if(d.getType().equals(DeviceType.ACCELEROMETER)){
				Accelerometer a=(Accelerometer) d;
				DeviceList.add("\n Sensor:"+d.getId()+" "+d.getType()+" x,y,z"+a.getXAcceleration()+a.getYAcceleration()+a.getZAcceleration());   
			}

		}

		shell.open();
		shell.setMaximized(true);
		return 0;
	}

	@Override
	protected void createContents(Composite parent) {

		/*backgroundColor_blue = new Color( parent.getDisplay(), 0x31, 0x61, 0x9C );
		backgroundColor_green = new Color( parent.getDisplay(), 0x20, 0x50, 0x50 );

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.makeColumnsEqualWidth=true;
		parent.setLayoutData(gridLayout);
		new Button(parent, SWT.NONE).setText("B1");
		new Button(parent, SWT.NONE).setText("Wide Button 2");
		new Button(parent, SWT.PUSH).setText("Button 3");

		header = new Composite( parent, SWT.NONE );
		header.setBackground( backgroundColor_blue );
		header.setBackgroundMode( SWT.INHERIT_DEFAULT );
		FillLayout headerlayout = new FillLayout();
		//header.setLayoutData( createLayoutDataForHeader() );
		//header.setLayoutData(headerlayout);

		Label label = new Label( header, SWT.NONE );
		label.setText( "Physical World Adaptation Layer:PWAL \n 1\n 2\n3" );
		label.setForeground( parent.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
		label.setBounds( 40, 30, 500, 30 );

		exampleParent = new Composite( parent, SWT.NONE );
		exampleParent.setBackground( backgroundColor_green );
		FillLayout exparentlayout = new FillLayout();
		//exampleParent.setLayoutData( createLayoutDataForExampleParent() );
		//exampleParent.setLayoutData(exparentlayout);
		Label label1 = new Label( exampleParent, SWT.NONE );
		label1.setText( "Physical World Adaptation Layer:PWAL \n 1\n 2\n3" );

		label1.setBounds( 100, 500, 500, 30 );
		new Button(exampleParent, SWT.PUSH).setText("B1");
		 */



	}


}
