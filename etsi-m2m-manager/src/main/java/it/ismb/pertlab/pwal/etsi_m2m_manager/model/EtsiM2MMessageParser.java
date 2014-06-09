package it.ismb.pertlab.pwal.etsi_m2m_manager.model;

import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.Application;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.Applications;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.XmlRootObject;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class EtsiM2MMessageParser {

	public Applications parseApplications(InputStream is)
	{
		try {
			XmlRootObject obj = this.unmarshal(XmlRootObject.class, is);
			Applications apps=obj.getApplications();
			return apps;
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Application parseApplication(InputStream is)
	{
		try {
			XmlRootObject obj = this.unmarshal(XmlRootObject.class, is);
			Application app=obj.getApplication();
			return app;
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	private <T> T unmarshal( Class<T> docClass, InputStream inputStream ) throws JAXBException {
			
		    JAXBContext jc = JAXBContext.newInstance( docClass );
		    Unmarshaller u = jc.createUnmarshaller();
		    T doc = (T)u.unmarshal( inputStream );
		    return doc;
	}
}
