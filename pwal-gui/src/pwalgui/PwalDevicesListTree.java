package pwalgui;

import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.swt.widgets.Composite;

public class PwalDevicesListTree implements IExamplePage {

	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
	    parent.setLayout( ExampleUtil.createMainLayout( 1 ) );
	    parent.setLayout( ExampleUtil.createGridLayout( 1, true, true, true ) );
	    parent.setLayoutData( ExampleUtil.createFillData() );
		
	}
	
	
	

}
