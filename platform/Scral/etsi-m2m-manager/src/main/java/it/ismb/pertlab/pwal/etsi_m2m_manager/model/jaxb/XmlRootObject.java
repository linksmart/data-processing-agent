package it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="xml")
public class XmlRootObject {

	private Applications applications;
	private Application application;
	
	public Applications getApplications() {
		return applications;
	}

	public void setApplications(Applications applications) {
		this.applications = applications;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}
}
