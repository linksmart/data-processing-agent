package eu.almanac.event.datafusion.utils.epl.intern;

/**
 * Created by Jose Angel Carvajal on 31.08.2015 a researcher of Fraunhofer FIT.
 */
public class EPLStatement extends eu.almanac.event.datafusion.utils.epl.EPLStatement {
    public void setScope(String[] scope) {
        this.scope = scope;
    }

    public void setOutput(String[] output) {
        this.output = output;
    }

    public void setInput(String[] input) {
        this.input = input;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public void setName(String name) {
        this.name = name;
    }

}
