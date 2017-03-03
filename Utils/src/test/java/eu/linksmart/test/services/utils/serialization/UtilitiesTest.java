package eu.linksmart.test.services.utils.serialization;

import eu.linksmart.services.utils.function.Utils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by José Ángel Carvajal on 03.03.2017 a researcher of Fraunhofer FIT.
 */
public class UtilitiesTest {

    @Test
    public void functionsTest(){
        Pattern pattern = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+(-[A-Za-z]+)?");

        assertEquals(true,pattern.matcher(Utils.getVersion()).find());
        assertEquals(true,Utils.getDateFormat()!= null);
        assertEquals(true,Utils.getTimeZone()!=null);
        assertEquals(true,Utils.getTimestamp(new Date(0)).contains("70"));
        assertEquals(true,Utils.getIsoTimestamp(new Date(0)).contains("70"));
        assertEquals(true, !"".equals(Utils.getDateNowString() ));
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",Utils.hashIt(""));
        assertEquals(true, Utils.isResource("testConf1.cfg"));
        try {
            assertEquals(true, Utils.createPropertyFiles("testConf1.cfg").containsKey("my.test.property"));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(true, null != Utils.initLoggingConf(this.getClass()));
        try {
            File file = File.createTempFile("test","tmp");
            assertEquals(true,Utils.isFile(file.getCanonicalPath()));
            file.delete();
            assertEquals(false,Utils.isFile(file.getCanonicalPath()));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }


    }
}
