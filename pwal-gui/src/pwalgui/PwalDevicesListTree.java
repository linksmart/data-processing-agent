package pwalgui;

import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public class PwalDevicesListTree extends AbstractEntryPoint {

	private Composite header;
	private Composite exampleParent;
	private Color backgroundColor;
	private Color backgroundColor1;
	@Override
	protected void createContents(Composite parent) {
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
