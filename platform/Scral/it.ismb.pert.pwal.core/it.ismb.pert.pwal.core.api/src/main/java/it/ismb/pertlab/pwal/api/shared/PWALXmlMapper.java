package it.ismb.pertlab.pwal.api.shared;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class PWALXmlMapper
{
    public PWALXmlMapper()
    {  }
    
    public <T> T unmarshal(Class<T> docClass, InputStream inputStream)
            throws JAXBException
    {
        JAXBContext jc = JAXBContext.newInstance(docClass);
        Unmarshaller u = jc.createUnmarshaller();
        @SuppressWarnings("unchecked")
        T doc = (T) u.unmarshal(inputStream);
        return doc;
    }

    public <T> ByteArrayOutputStream marshal(Class<T> docClass, T obj)
    {
        Marshaller marshaller = null;
        ByteArrayOutputStream baos = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(docClass);
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    new Boolean(true));
            marshaller.marshal(obj, baos);
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
        return baos;
    }

    public <T> void toXml(Class<T> jaxbClass, T jaxbObj)
            throws JAXBException
    {
        JAXBContext ctx = JAXBContext.newInstance(jaxbClass);
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(jaxbObj, System.out);
    }

    // private static byte[] getBytesFromInputStream(InputStream is)
    // {
    // try (ByteArrayOutputStream os = new ByteArrayOutputStream();)
    // {
    // byte[] buffer = new byte[0xFFFF];
    //
    // for (int len; (len = is.read(buffer)) != -1;)
    // os.write(buffer, 0, len);
    //
    // os.flush();
    //
    // return os.toByteArray();
    // }
    // catch (IOException e)
    // {
    // return null;
    // }
    // }
}
