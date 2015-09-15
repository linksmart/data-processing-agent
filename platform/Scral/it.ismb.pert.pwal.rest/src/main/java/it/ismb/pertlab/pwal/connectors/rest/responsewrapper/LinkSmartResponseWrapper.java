package it.ismb.pertlab.pwal.connectors.rest.responsewrapper;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class LinkSmartResponseWrapper extends HttpServletResponseWrapper {

//	private String contentType; 
	
	public LinkSmartResponseWrapper(HttpServletResponse response) {
		super(response);
//		this.forceBufferSize(32000);
	}
	
//	public void setContentType(String contentType)
//	{ 	}
	
	public String getContentType()
	{
		return super.getContentType();
	}
	
	public void forceBufferSize(int bytes)
	{
		super.setBufferSize(bytes);
	}
	
	public void forceContentType(String type) {
        super.setContentType(type); 
    }
	
    public void setHeader(String name, String value) 
    {
        if (!name.equals("Content-Type") && !name.equals("Transfer-Encoding")) {
            super.setHeader(name, value);
        }
    }

    public void addHeader(String name, String value) 
    {
        if (!name.equals("Content-Type") && !name.equals("Transfer-Encoding")) {
            super.addHeader(name, value);
        }
    }
}
