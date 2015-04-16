package eu.linksmart.gc.network.connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import eu.linksmart.gc.api.network.ErrorMessage;
import eu.linksmart.gc.api.network.Message;
import eu.linksmart.gc.api.network.NMResponse;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.identity.IdentityManager;
import eu.linksmart.gc.network.networkmanager.core.impl.NetworkManagerCoreImpl;
import eu.linksmart.gc.api.security.communication.SecurityProperty;

public class ConnectionManagerTest {

	private VirtualAddress senderVirtualAddress;
	private VirtualAddress receiverVirtualAddress;
	private ConnectionManager connectionMgr;
	private Connection con;
	private BroadcastConnection bCon;
	private NetworkManagerCoreImpl nmCore;
	private IdentityManager idM;
	private NMResponse handshakeResp;
	private NMResponse handshakeRespDecline;

	@Before
	public void setUp(){
		senderVirtualAddress = new VirtualAddress("354.453.455.323");
		receiverVirtualAddress = new VirtualAddress("354.453.993.323");
		nmCore = mock(NetworkManagerCoreImpl.class);
		connectionMgr = new ConnectionManager(nmCore);
		this.idM = mock(IdentityManager.class);
		connectionMgr.setIdentityManager(idM);
		con = new Connection(senderVirtualAddress, receiverVirtualAddress);
		bCon = new BroadcastConnection(senderVirtualAddress);
		connectionMgr.connections.add(con);
		connectionMgr.connections.add(bCon);
		List<SecurityProperty> policy = new ArrayList<SecurityProperty>();
		policy.add(SecurityProperty.NoSecurity);
		connectionMgr.servicePolicies.put(receiverVirtualAddress, policy);

		//Create handshake response message
		String usedSecurity = ConnectionManager.HANDSHAKE_ACCEPT + " ";
		usedSecurity = usedSecurity.concat(SecurityProperty.NoSecurity.name());
		Message msg = new Message(
				Message.TOPIC_CONNECTION_HANDSHAKE,
				receiverVirtualAddress,
				senderVirtualAddress,
				usedSecurity.getBytes());
		handshakeResp = new NMResponse(NMResponse.STATUS_SUCCESS);
		handshakeResp.setMessageObject(msg);
		
		//Create handshake response message for declined
		handshakeRespDecline = new NMResponse(NMResponse.STATUS_SUCCESS);
		handshakeRespDecline.setMessage(ConnectionManager.HANDSHAKE_DECLINE + " ");		

		when(idM.getServiceInfo(any(VirtualAddress.class))).thenReturn(null);
		when(nmCore.getService()).thenReturn(senderVirtualAddress);
		when(nmCore.getVirtualAddress()).thenReturn(senderVirtualAddress);
	}

	/**
	 * Tests if a stored connection is returned on request.
	 */
	@Test
	public void testConnectionRetrieval() {
		Connection con = connectionMgr.getConnection(senderVirtualAddress, receiverVirtualAddress);
		assertEquals(this.con, con);
	}

	/**
	 * Tests if a stored broadcast connection is returned on request.
	 */
	@Test
	public void testBroadcastConnectionRetrieval() {
		Connection con = null;
		try {
			con = connectionMgr.getBroadcastConnection(senderVirtualAddress);
		} catch (Exception e) {
			fail("Unexpected exception");
		}
		assertEquals(this.bCon, con);
	}

	/**
	 * Tests whether when creating a new connection an appropriate handshake is sent out
	 */
	@Test
	public void testStartHandshake() {
		when(nmCore.sendMessage(any(Message.class), any(boolean.class))).thenReturn(handshakeResp);
		//convert properties to xml and put it into stream
		Properties props = new Properties();
		props.put(ConnectionManager.HANDSHAKE_COMSECMGRS_KEY, "");
		props.put(ConnectionManager.HANDSHAKE_SECPROPS_KEY, SecurityProperty.NoSecurity.name() + ";");
		
		try {
			connectionMgr.createConnection(receiverVirtualAddress, senderVirtualAddress, new byte[0]);
			ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
			verify(nmCore).sendMessage(argument.capture(), any(boolean.class));
			
			//check if communication entities are equal
			assertEquals(receiverVirtualAddress, argument.getValue().getReceiverVirtualAddress());
			assertEquals(senderVirtualAddress, argument.getValue().getSenderVirtualAddress());
			
			//check if contents are equal
			byte[] sentData = argument.getValue().getData();
			
			Properties properties = new Properties();
			try {
				properties.loadFromXML(new ByteArrayInputStream(sentData));
			} catch (InvalidPropertiesFormatException e) {
				fail(e.getMessage());
			} catch (IOException e) {
				fail(e.getMessage());
			}
			
			if(properties.containsKey(ConnectionManager.HANDSHAKE_COMSECMGRS_KEY)
					&& properties.containsKey(ConnectionManager.HANDSHAKE_SECPROPS_KEY)
					&& properties.containsKey(Message.SESSION_ID_KEY)) {
			assertEquals("", properties.get(ConnectionManager.HANDSHAKE_COMSECMGRS_KEY));
			assertEquals(SecurityProperty.NoSecurity.name() + ";", properties.get(ConnectionManager.HANDSHAKE_SECPROPS_KEY));
			} else {
				fail("Sent handshake message misses fields!");
			}

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Check whether after successful handshake connection is returned
	 */
	@Test
	public void testRetrieveAgreedConnection() {
		when(nmCore.sendMessage(any(Message.class), any(boolean.class))).thenReturn(handshakeResp);
		Connection con = null;
		try {
			con = connectionMgr.createConnection(receiverVirtualAddress, senderVirtualAddress, new byte[0]);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		if(con != null) {
			assertEquals(Connection.class, con.getClass());
			assertEquals(null, con.comSecMgr);
		} else {
			fail("Create connection returned null!");
		}
	}

	/**
	 * Check whether after unsuccessful handshake null is returned
	 */
	@Test
	public void testFailedHandshake() {
		when(nmCore.sendMessage(any(Message.class), any(boolean.class))).thenReturn(handshakeRespDecline);
		Connection con = null;
		try {
			con = connectionMgr.createConnection(receiverVirtualAddress, senderVirtualAddress, new byte[0]);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertEquals(null, con);
	}
}
