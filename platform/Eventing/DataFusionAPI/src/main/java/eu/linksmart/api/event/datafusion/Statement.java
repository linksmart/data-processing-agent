package eu.linksmart.api.event.datafusion;

/**
 * Created by Caravajal on 06.10.2014.
 */
public interface Statement {

    public String getName();

    public String getStatement();

    public String[] getInput();

    public String[] getScope();

    public String getInput(int index);

    public String getScope(int index);

    public String[] getOutput();
    public boolean haveInput();
    public boolean haveOutput();
    public boolean haveScope();
}
