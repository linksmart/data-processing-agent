package datafusion.sdk.client.epl; /**
 * Created by Caravajal on 21.05.2015.
 */
import com.espertech.esper.client.EPStatementSyntaxException;
import com.espertech.esper.epl.generated.EsperEPL2GrammarParser;
import com.espertech.esper.epl.parse.ParseHelper;
import com.espertech.esper.epl.parse.ParseRuleSelector;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.almanac.event.datafusion.utils.epl.EPLStatement;
import eu.linksmart.api.event.datafusion.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.gc.api.types.MqttTunnelledMessage;
import eu.linksmart.gc.network.backbone.protocol.mqtt.ForwardingListener;
import eu.linksmart.gc.network.backbone.protocol.mqtt.Utils;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.Tree;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Observable;
import java.util.Observer;


public class StatementSender implements Observer{
    private String feedback;
    private MqttClient mqtt;
    private String queryHash =null;
    private ForwardingListener forwardingListener;
    static private Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    static private ParseRuleSelector eplParseRule = new ParseRuleSelector(){
        public Tree invokeParseRule(EsperEPL2GrammarParser parser) throws RecognitionException
        {
            return parser.startEPLExpressionRule();
        }
    };
    public StatementSender(String brokerName,String brokerPort ) throws MqttException {
         forwardingListener = new ForwardingListener(brokerName,brokerPort,this);
        mqtt = new MqttClient(Utils.getBrokerURL( brokerName, brokerPort), "senderClient", new MemoryPersistence());


    }
    public StatementSender() throws MqttException, NoSuchAlgorithmException {
        this("localhost","1883");

    }
    public synchronized String send(Statement statement ) throws MqttException, InterruptedException {

        if(!mqtt.isConnected()){
            mqtt.connect();
        }
        queryHash = statement.getHash();

        forwardingListener.setListening("queries/"+queryHash);


        mqtt.publish("queries/add",gson.toJson((EPLStatement)statement).getBytes(),0,false);

        feedback = "Timeout error: the connection between the Data-Fusion Manager was not possible";




            this.wait(10000);


        forwardingListener.close();


        return feedback;

    }
    public String send(String name, String Statement ) throws MqttException, InterruptedException {

       return send(factory(name,Statement));



    }
    public  String send(String name, String Statement, String[] Scope) throws MqttException, InterruptedException {

        return send(factory(name, Statement, Scope));

    }
    public static Statement factory(String name, String Statement) throws EPStatementSyntaxException {


        return factory(name, Statement, new String[]{"local"}, null, null);


    }
    public static Statement factory(String name, String Statement, String[] Scope) throws EPStatementSyntaxException {
        return factory(name,Statement,Scope,null,null);


    }
    public static Statement factory(String name, String Statement, String[] Scope, String source) throws EPStatementSyntaxException {
        return factory(name,Statement,Scope,source,null,null);


    }
    public static Statement factory(String name, String Statement, String[] Scope, String source, String[] Input) throws EPStatementSyntaxException {
        return factory(name,Statement,Scope,source,Input,null);


    }
    public static Statement factory(String name, String Statement, String[] Scope, String source, String[] Input, String[] Output) throws EPStatementSyntaxException {

        EPLStatement st = new EPLStatement();
        if(
            !(Statement.toLowerCase().equals("pause") ||
            Statement.toLowerCase().equals("start") ||
            Statement.toLowerCase().equals("remove") ||
            Statement.toLowerCase().contains("add instance ") ||
            Statement.toLowerCase().equals("shutdown"))
        ) {
            ParseHelper.parse(Statement, Statement, true, eplParseRule, true);
        }

        st.setName(name);
        st.setScope(Scope);
        st.setStatement(Statement);
        st.setInput(Input);
        st.setOutput(Output);
        st.setSource(source);

        return st;


    }


    @Override
    public void update(Observable o, Object arg) {

        feedback = new String(((MqttTunnelledMessage)arg).getPayload());
        synchronized (StatementSender.this) {
            StatementSender.this.notifyAll();
        }




    }
}
