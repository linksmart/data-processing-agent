
import eu.linksmart.services.event.cep.tooling.Tools;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 10.12.2018 a researcher of Fraunhofer FIT.
 */
public class ToolsTest {
    @Test
    public void addAll(){
        Object[] o={0,1,2}, o2= {3,4},o3= {1,2}, test = ArrayUtils.addAll(ArrayUtils.toArray(0), o3);
        List<Integer> l = Arrays.asList(0,1,2), l2 = Arrays.asList(3,4);

        Assert.assertTrue("Array must be equal!",validateArray(Tools.addAll(o,o2)));
        Assert.assertTrue("Array must be equal!",validateArray(Tools.addAll(o,l2)));
        Assert.assertTrue("Array must be equal!",validateArray(Tools.addAll(l,o2)));
        Assert.assertTrue("Array must be equal!",validateArray(Tools.addAll(l,l2)));
        Assert.assertTrue("Array must be equal!",validateArray(Tools.addAll(o,3)));
        Assert.assertTrue("Array must be equal!",validateArray(Tools.addAll(0,o3))); // <- doesn't work not know why
        Assert.assertTrue("Array must be equal!",validateArray(Tools.addAll(0,1)));

    }
    private boolean validateArray(List union){
        for(int i=0; i<union.size();i++)
            if (!union.get(i).equals(i) )
                return false;

        return true;
    }
}
