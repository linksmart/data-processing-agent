package it.ismb.pertlab.pwal.etsi_m2m_manager.model;

import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.Application;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.Applications;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.Container;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.Containers;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.ContentInstance;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.ContentInstances;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.SclBase;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/***
 * This class is an helper to parse Etsi M2M messages
 * 
 * @author GDa + GBo
 * 
 */
public class EtsiM2MMessageParser {

	public SclBase parseSclBase(InputStream is) throws JAXBException {
		return this.unmarshal(SclBase.class, is);
	}

	public Applications parseApplications(InputStream is) throws JAXBException {
		return this.unmarshal(Applications.class, is);
	}

	public Application parseApplication(InputStream is) throws JAXBException {
		return this.unmarshal(Application.class, is);
	}

	public Containers parseContainers(InputStream is) throws JAXBException {
		return this.unmarshal(Containers.class, is);
	}

	public Container parseContainer(InputStream is) throws JAXBException {
		return this.unmarshal(Container.class, is);
	}

	public ContentInstances parseContentInstances(InputStream is)
			throws JAXBException {
		return this.unmarshal(ContentInstances.class, is);
	}

	public ContentInstance parseContentInstance(InputStream is)
			throws JAXBException {
		return this.unmarshal(ContentInstance.class, is);
	}

	private <T> T unmarshal(Class<T> docClass, InputStream inputStream)
			throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(docClass);
		Unmarshaller u = jc.createUnmarshaller();
		@SuppressWarnings("unchecked")
		T doc = (T) u.unmarshal(inputStream);
		return doc;
	}

	public <T> void toXml(Class<T> jaxbClass, T jaxbObj) throws JAXBException {
		JAXBContext ctx = JAXBContext.newInstance(jaxbClass);
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(jaxbObj, System.out);
	}
}
