package eu.linksmart.api.event.datafusion;

/**
 * This is the part of the API offered by Data Fusion. The Statement is the Interface that any statement object must fulfill. This interface is a generalization of any statement of a CEP engine.<p>
 *
 * This is the API offered to the CEP engine wrapper/s.
 *
 * @author Jose Angel Carvajal Soto
 * @version     0.03
 * @since       0.03
 * @see  DataFusionWrapper
 *
 * */
public interface Statement {
    /***
     * Name of the statement
     *
     * @return  returns the name of the statement as string
     * */
    public String getName();
    /***
     * The statement on the CEP engine (Typically EPL)
     *
     * @return  returns the statement in the native CEP language as string
     * */
    public String getStatement();
    /***
     * The source broker where the events are coming
     *
     * @return  The broker URL or alias as string
     * */
    public String getSource();
    /***
     * The input topics of the statement (DEPRECATED)
     *
     * @return  return the list of topics that are needed in the statement
     * */
    @Deprecated
     public String[] getInput();
    /***
     * The output brokers where the events will be published
     *
     * @return  The broker URLs or aliases as string
     * */
    public String[] getScope();
    /***
     * The input topic number i of the statement (DEPRECATED)
     *
     * @param index of the topic selected to return
     *
     * @return  return the selected topic
     * */
    @Deprecated
    public String getInput(int index);
    /***
     * The output broker number i of the statement
     *
     * @param index of the broker selected to return
     *
     * @return  The selected broker URL or alias as string
     * */
    public String getScope(int index);
    /***
     * The output topics where the events will be published
     *
     * @return  The output topics as string
     * */
    public String[] getOutput();
    /***
     * The Input value is a optional value. This return if the value has been defined or not (DEPRECATED)
     *
     * @return  <code>true</code> if there is Input has being defined, <code>false</code> otherwise
     * */

    @Deprecated
    public boolean haveInput();
    /***
     * The output value is a optional value. This return if the value has been defined or not
     *
     * @return  <code>true</code> if there is output has being defined, <code>false</code> otherwise
     * */
    public boolean haveOutput();
    /***
     * The Scope value is a optional value. This return if the value has been defined or not
     *
     * @return  <code>true</code> if there is Scope has being defined, <code>false</code> otherwise
     * */
    public boolean haveScope();
    /***
     * Returns the hash ID of the statement. By default this is the SHA256 of the name.
     *
     * @return  The ID as string.
     * */
    public String getHash();

}
