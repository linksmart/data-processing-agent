package it.ismb.pertlab.pwal.etsi_m2m_manager.model;

import static org.junit.Assert.assertEquals;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.Application;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.Applications;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.NamedReferenceCollection;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.ReferenceToNamedResource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.junit.Test;

public class EtsiM2MMessageParserTest {

	@Test
	public void testParseApplications() throws FileNotFoundException, DatatypeConfigurationException
	{
		InputStream is=new FileInputStream(this.getClass().getClassLoader().getResource("applications.xml").getFile());
		EtsiM2MMessageParser parser=new EtsiM2MMessageParser();
		Applications appl=parser.parseApplications(is);
		assertEquals("http://m2mtilab.dtdns.net/etsi/almanac/accessRights/AR",appl.getAccessRightID());
		//appl.getApplicationAnncCollection();
		NamedReferenceCollection appColl=appl.getApplicationCollection();
		List<ReferenceToNamedResource> refs=appColl.getNamedReference();
		
		assertEquals("APP", refs.get(0).getId());
		assertEquals("http://m2mtilab.dtdns.net/etsi/almanac/applications/APP", refs.get(0).getValue());

		assertEquals("consumer", refs.get(1).getId());
		assertEquals("http://m2mtilab.dtdns.net/etsi/almanac/applications/consumer", refs.get(1).getValue());

		assertEquals(DatatypeFactory.newInstance()
				.newXMLGregorianCalendar("2014-05-08T09:36:41"),
				appl.getCreationTime());
		assertEquals(DatatypeFactory.newInstance()
				.newXMLGregorianCalendar("2014-05-15T14:12:49"),
				appl.getLastModifiedTime());
		assertEquals("http://m2mtilab.dtdns.net/etsi/almanac/applications/mgmtObjs",appl.getMgmtObjsReference());
		assertEquals("http://m2mtilab.dtdns.net/etsi/almanac/applications/subscriptions",appl.getSubscriptionsReference());
	}
	
	@Test
	public void testGetApplication() throws FileNotFoundException, DatatypeConfigurationException
	{
		InputStream is=new FileInputStream(this.getClass().getClassLoader().getResource("application.xml").getFile());
		EtsiM2MMessageParser parser=new EtsiM2MMessageParser();
		Application app=parser.parseApplication(is);

		assertEquals("http://m2mtilab.dtdns.net/etsi/almanac/applications/water/accessRights/AR",app.getAccessRightID());
		assertEquals("http://m2mtilab.dtdns.net/etsi/almanac/applications/water/accessRights",app.getAccessRightsReference());
		//TODO expected FALSE but was null
		//assertEquals("FALSE",app.getAnnounceTo().isActivated());
		assertEquals(0,app.getAnnounceTo().getSclList().getReference().size());
		//assertEquals("",app.getAPoC());
		//assertEquals("",app.getAPoCPaths().getAPoCPath());
		//TODO was null
		//assertEquals("http://m2mtilab.dtdns.net/etsi/almanac/applications/water",app.getAppId());
		assertEquals("http://m2mtilab.dtdns.net/etsi/almanac/applications/water/containers",app.getContainersReference());
		assertEquals(DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-05-08T09:37:25"),app.getCreationTime());
		//assertEquals(DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-05-08T09:37:25"),app.getExpirationTime());
		assertEquals("http://m2mtilab.dtdns.net/etsi/almanac/applications/water/groups",app.getGroupsReference());
		assertEquals(DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-05-08T09:37:25"),app.getLastModifiedTime());
		//assertEquals("",app.getLocRequestor());
		
		//assertEquals("",app.getNotificationChannelsReference());
		assertEquals("DIA_REFERENCE_POINT",app.getReferencePoint().value());
		assertEquals("water",app.getSearchStrings().getSearchString().get(0));
		assertEquals("http://m2mtilab.dtdns.net/etsi/almanac/applications/water/subscriptions",app.getSubscriptionsReference());
		
	}
}
