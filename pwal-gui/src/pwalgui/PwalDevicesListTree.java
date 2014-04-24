package pwalgui;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.rap.rwt.template.Template;
import org.eclipse.rap.rwt.template.TextCell;


public class PwalDevicesListTree extends AbstractEntryPoint {
	
	private Composite header;
	private Composite exampleParent;
	private Color backgroundColor;
	private Color backgroundColor1;
	
	
	@Override
	protected void createContents(Composite parent) {
		
		backgroundColor = new Color( parent.getDisplay(), 0x31, 0x61, 0x9C );
		backgroundColor1 = new Color( parent.getDisplay(), 0x20, 0x50, 0x50 );
	/*	parent.setLayout( new FormLayout() );
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
		
		*/
		
		
		Table table = new Table( parent, SWT.FULL_SELECTION); 
		new TableColumn( table, SWT.NONE ); // important
		Template template = new Template();
		TextCell textCell = new TextCell( template );
		//textCell.setLeft( 0 ).setRight( 0 ).setTop( 0 ).setBottom( 0 );
		textCell.setBindingIndex( 0 );
		textCell.setWidth(500);
		textCell.setHeight(100);
		//textCell.setBackground(backgroundColor);
		table.setData( RWT.ROW_TEMPLATE, template );
		
		TableItem item = new TableItem( table, SWT.INHERIT_DEFAULT);
		item.setText( 0, "Data in sdasdsadasthe first column \n and this is the matter" );
		item.setText( 1, "Data in the second column" );
		item.setText( 2, "Data in the first column" );
		

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
		layoutData.height = 100;
		return layoutData;
	}
	
}
