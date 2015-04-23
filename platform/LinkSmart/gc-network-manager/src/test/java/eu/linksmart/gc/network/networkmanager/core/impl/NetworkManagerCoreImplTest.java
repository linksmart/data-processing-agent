package eu.linksmart.gc.network.networkmanager.core.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import eu.linksmart.gc.api.network.ErrorMessage;
import eu.linksmart.gc.api.network.Message;
import eu.linksmart.gc.api.network.NMResponse;
import eu.linksmart.gc.api.network.Registration;
import eu.linksmart.gc.api.network.ServiceAttribute;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.network.connection.BroadcastConnection;
import eu.linksmart.gc.network.connection.Connection;
import eu.linksmart.gc.network.connection.ConnectionManager;
import eu.linksmart.gc.network.connection.MessageSerializerUtiliy;
import eu.linksmart.gc.network.connection.NOPConnection;
import eu.linksmart.gc.api.network.identity.IdentityManager;
import eu.linksmart.gc.api.network.routing.BackboneRouter;
import eu.linksmart.gc.api.utils.Part;

/**
 * Test class for NetworkManagerCoreImpl 
 */
public class NetworkManagerCoreImplTest {

	private NetworkManagerCoreImpl nmCoreImpl;
	private VirtualAddress senderVirtualAddress;
	private VirtualAddress receiverVirtualAddress;
	private String topic;
	byte [] data;

	/**
	 * Setup method which is called before the unit tests are executed
	 * It sets some variable values and mocks.
	 */
	@Before
	public void setUp(){
		senderVirtualAddress = new VirtualAddress("354.453.455.323");
		receiverVirtualAddress = new VirtualAddress("354.453.993.323");
		topic = "test";
		data = "LinkSmart rocks".getBytes();
		nmCoreImpl = new NetworkManagerCoreImpl();
		nmCoreImpl.myVirtualAddress = new VirtualAddress("124.3235.346234.3456");

		// Mocked classes
		nmCoreImpl.backboneRouter = mock(BackboneRouter.class);
		nmCoreImpl.identityManager = mock(IdentityManager.class);
		nmCoreImpl.connectionManager = mock(ConnectionManager.class);

		// Mocked methods
		try {
			when(nmCoreImpl.connectionManager.getBroadcastConnection(eq(senderVirtualAddress)))
			.thenReturn(new BroadcastConnection(senderVirtualAddress));
		} catch (Exception e) { /*NOP*/}
		when(nmCoreImpl.connectionManager.getConnection(eq(receiverVirtualAddress), eq(nmCoreImpl.myVirtualAddress)))
		.thenReturn(new NOPConnection(senderVirtualAddress, receiverVirtualAddress));
		when(nmCoreImpl.backboneRouter.broadcastData(any(VirtualAddress.class), any(byte[].class))).
		thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		when(nmCoreImpl.backboneRouter.sendDataSynch(eq(nmCoreImpl.myVirtualAddress), eq(receiverVirtualAddress), any(byte[].class))).
		thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		when(nmCoreImpl.identityManager.getServiceInfo(receiverVirtualAddress)).
		thenReturn(new Registration(receiverVirtualAddress, new Part[]{}));
		when(nmCoreImpl.backboneRouter.sendDataSynch(eq(senderVirtualAddress), eq(receiverVirtualAddress), any(byte[].class))).
		thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		when(nmCoreImpl.backboneRouter.sendDataAsynch(any(VirtualAddress.class), eq(receiverVirtualAddress), any(byte[].class))).
		thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		when(nmCoreImpl.backboneRouter.sendDataAsynch(any(VirtualAddress.class), eq(senderVirtualAddress), any(byte[].class))).
		thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		when(nmCoreImpl.backboneRouter.addRouteToBackbone(any(VirtualAddress.class), any(String.class), any(String.class))).
		thenReturn(true);
	}

	/**
	 * Tests broadcastMessage of NetworkManagerCoreImpl. 
	 */
	@Test
	public void testBroadcastMessage() {
		byte [] data = "LinkSmart rocks".getBytes();
		Message message = new Message(topic, senderVirtualAddress, receiverVirtualAddress, data);

		NMResponse response = nmCoreImpl.broadcastMessage(message);

		assertNotNull("Response should not be null", response);
		assertEquals(NMResponse.STATUS_SUCCESS, response.getStatus());
	}

	/**
	 * Tests the synchronous sendMessage call
	 */
	@Test
	public void testSendMessageSync() {
		byte [] data = "LinkSmart rocks".getBytes();
		Message message = new Message(topic, senderVirtualAddress, receiverVirtualAddress, data);

		NMResponse response = nmCoreImpl.sendMessage(message, true);

		verify(nmCoreImpl.backboneRouter).
		sendDataSynch(eq(senderVirtualAddress), eq(receiverVirtualAddress), any(byte[].class));
		assertNotNull("Response should not be null", response);
	}

	/**
	 * Tests the asynchronous sendMessage call
	 */
	@Test
	public void testSendMessageAsync() {
		byte [] data = "LinkSmart rocks".getBytes();
		Message message = new Message(topic, senderVirtualAddress, receiverVirtualAddress, data);

		NMResponse response = nmCoreImpl.sendMessage(message, false);

		verify(nmCoreImpl.backboneRouter).
		sendDataAsynch(eq(senderVirtualAddress), eq(receiverVirtualAddress), any(byte[].class));
		assertNotNull("Response should not be null", response);
	}

	/**
	 * Tests receiveDataSync else-case, which occurs if neither the receiverVirtualAddress 
	 * is null, nor does the identity manager contain the receiver registration object.
	 */
	@Test
	public void  testReceiveDataSync() {
		byte rawData[] = null;
		try {
			rawData = getData();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occurred: " + e);
		}


		// Call the method in test
		NMResponse response = nmCoreImpl.receiveDataSynch(senderVirtualAddress, receiverVirtualAddress, rawData);

		// Check if the response is as expected
		assertEquals("The response was not successful.",  
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}

	/**
	 * Tests receiveDataSync where processing is not successful.
	 */
	@Test
	public void  testReceiveDataSyncUnsuccessful() {
		Registration receiverRegistration = new Registration(receiverVirtualAddress, new Part[]{});
		Set<Registration> infos = new HashSet<Registration>();
		infos.add(receiverRegistration);

		when(nmCoreImpl.identityManager.getLocalServices()).
		thenReturn(infos);

		byte rawData[] = null;
		try {
			rawData = getData();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occurred: " + e);
		}

		// Call the method in test
		NMResponse response = nmCoreImpl.receiveDataSynch(senderVirtualAddress, receiverVirtualAddress, rawData);

		// Check if the response is as expected
		assertEquals("The request should not be successful.",  
				NMResponse.STATUS_ERROR, response.getStatus());
	}

	/**
	 * Tests receiveDataSync with broadcasting message, but without processing it
	 * Because it is not processed, an exception should occur.
	 */
	@Test
	public void  testReceiveDataSyncBroadcastUnsucessful() {

		nmCoreImpl.connectionManager = mock(ConnectionManager.class);
		Connection connection = mock(Connection.class);
		ErrorMessage errorMsg = new ErrorMessage(
				ErrorMessage.RECEPTION_ERROR,
				nmCoreImpl.myVirtualAddress, senderVirtualAddress,
				NetworkManagerCoreImpl.UNPROCESSED_MSG.getBytes());
		try {
			when(nmCoreImpl.connectionManager.getBroadcastConnection(eq(senderVirtualAddress))).
			thenReturn(connection);
			when(connection.processData(eq(senderVirtualAddress), any(VirtualAddress.class), any(byte[].class))).
			thenReturn(new Message(topic, senderVirtualAddress, null, data));
			when(connection.processMessage(eq(errorMsg))).
			thenReturn("testdata".getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured " +e);
		}

		byte rawData[] = null;
		try {
			rawData = getData();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occurred: " + e);
		}

		// Call the method in test
		NMResponse response = nmCoreImpl.receiveDataSynch(senderVirtualAddress, null, rawData);

		// Check if the response is as expected
		assertEquals("The request was not successful.",  
				NMResponse.STATUS_ERROR, response.getStatus());
		//the mock returns testdata as answer when invoked
		assertEquals("testdata", response.getMessage());
	}

	/**
	 * Tests receiveDataSync with broadcasting message as there is no receiver VirtualAddress
	 */
	@Test
	public void  testReceiveDataSyncBroadcast() {
		mockForSucessfulReceiveData();

		byte rawData[] = null;
		try {
			rawData = getData();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occurred: " + e);
		}

		// Call the method in test
		NMResponse response = nmCoreImpl.receiveDataSynch(senderVirtualAddress, null, rawData);

		// Check if the response is as expected
		assertEquals("The request was not successful.",  
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}

	/**
	 * Tests receiveData with broadcasting message as there is no receiver VirtualAddress
	 */
	@Test
	public void  testReceiveDataAsyncBroadcast() {
		mockForSucessfulReceiveData();

		byte rawData[] = null;
		try {
			rawData = getData();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occurred: " + e);
		}

		// Call the method in test
		NMResponse response = nmCoreImpl.receiveDataAsynch(senderVirtualAddress, null, rawData);

		// Check if the response is as expected
		assertEquals("The request was not successful.",  
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}

	/**
	 * Tests receiveData with broadcasting message as there is no receiver VirtualAddress
	 * and with an empty return message
	 */
	@Test
	public void  testReceiveDataAsyncBroadcastEmtpyMessage() {
		mockForSucessfulReceiveData();

		byte rawData[] = null;
		try {
			rawData = getData();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occurred: " + e);
		}

		// Call the method in test
		NMResponse response = nmCoreImpl.receiveDataAsynch(senderVirtualAddress, null, rawData);

		// Check if the response is as expected
		assertEquals("The request was not successful.",  
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}

	/**
	 * Tests receiveDataSync else-case, which occurs if neither the receiverVirtualAddress 
	 * is null, nor does the identity manager contain the receiverRegistration object.
	 */
	@Test
	public void  testReceiveDataAsync() {
		byte rawData[] = null;
		try {
			rawData = getData();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occurred: " + e);
		}
		// Call the method in test
		NMResponse response = nmCoreImpl.receiveDataAsynch(senderVirtualAddress, receiverVirtualAddress, rawData);

		// Check if the response is as expected
		assertEquals("The response was not successful.",  
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}

	/**
	 * Tests receiveDataAsync with broadcasting message, but without processing it
	 * Because it is not processed, an exception should occur.
	 */
	@Test
	public void  testReceiveDataAsyncBroadcastUnsucessful() {

		nmCoreImpl.connectionManager = mock(ConnectionManager.class);
		Connection connection = mock(Connection.class);
		ErrorMessage errorMsg = new ErrorMessage(
				ErrorMessage.RECEPTION_ERROR,
				nmCoreImpl.myVirtualAddress, senderVirtualAddress,
				NetworkManagerCoreImpl.UNPROCESSED_MSG.getBytes());
		try {
			when(nmCoreImpl.connectionManager.getBroadcastConnection(eq(senderVirtualAddress))).
			thenReturn(connection);
			when(connection.processData(eq(senderVirtualAddress), any(VirtualAddress.class), any(byte[].class))).
			thenReturn(new Message(topic, senderVirtualAddress, null, data));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured " +e);
		}

		byte rawData[] = null;
		try {
			rawData = getData();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occurred: " + e);
		}

		// Call the method in test
		NMResponse response = nmCoreImpl.receiveDataSynch(senderVirtualAddress, null, rawData);

		// Check if the response is as expected
		assertEquals("The request was not successful.",  
				NMResponse.STATUS_ERROR, response.getStatus());
		assertEquals(new String(MessageSerializerUtiliy.serializeMessage(errorMsg, true, false)), response.getMessage());
	}

	/**
	 * Tests that if addRemoteVirtualAddress. the right backboneRouter-method is called
	 */
	@Test
	public void  testAddRemoteVirtualAddress() {
		VirtualAddress remoteVirtualAddress = new VirtualAddress("354.453.111.323");

		nmCoreImpl.addRemoteVirtualAddress(senderVirtualAddress, remoteVirtualAddress);

		verify(nmCoreImpl.backboneRouter).addRouteForRemoteService(senderVirtualAddress, remoteVirtualAddress);
	}

	/**
	 * Tests getServiceByDescription
	 */
	@Test
	public void testGetServiceByDescription() {
		String description = "description";
		String query = ("(DESCRIPTION==description)");

		Registration[] foundServiceInfos = nmCoreImpl.getServiceByDescription(description);
		verify(nmCoreImpl.identityManager).getServiceByAttributes(new Part[]{
				new Part(ServiceAttribute.DESCRIPTION.name(), description)},
				IdentityManager.SERVICE_RESOLVE_TIMEOUT,
				false,
				false);
	}

	/**
	 * Tests getServiceByPID
	 */
	@Test
	public void testGetServiceByPID() {
		String PID = "Unique PID";
		String query = ("(PID==Unique PID)");
		Registration[] infos = new Registration[1];
		infos[0] = new Registration(new VirtualAddress(), new Part[0]);
		when(nmCoreImpl.identityManager.getServiceByAttributes(
				new Part[]{new Part(ServiceAttribute.PID.name(), PID)},
				IdentityManager.SERVICE_RESOLVE_TIMEOUT,
				false,
				false)).thenReturn(infos);

		Registration foundServiceInfo = nmCoreImpl.getServiceByPID(PID);
		assertEquals(infos[0].getVirtualAddress(), foundServiceInfo.getVirtualAddress());
	}

	/**
	 * Tests if there are more than one Registration returned for getServiceByPID
	 */
	@Test
	public void testGetServiceByPIDWithExceptionSeveralRegistrations() {
		String PID = "Unique PID";
		String query = ("(PID==Unique PID)");

		// Return more than one VirtualAddress so that an exception is thrown
		Registration[] infos = new Registration[2];
		infos[0] = new Registration(new VirtualAddress(), new Part[0]);
		infos[1] = new Registration(new VirtualAddress(), new Part[0]);
		when(nmCoreImpl.identityManager.getServiceByAttributes(
				new Part[]{new Part(ServiceAttribute.PID.name(), PID)},
				IdentityManager.SERVICE_RESOLVE_TIMEOUT,
				false,
				false)).thenReturn(infos);

		try {
			nmCoreImpl.getServiceByPID(PID);
			// if we get here, there was no exception
			fail("There should be an exception because more than one VirtualAddress was " +
					"returned which is not allowed for getServiceByPID.");
		} catch(RuntimeException e){
			// Check if an exception with the right message was thrown
			assertEquals("More than one service registration found to passed PID", e.getMessage());
		}
	}

	/**
	 * Tests if there was no PID given for getServiceByPID
	 */
	@Test
	public void testGetServiceByPIDWithExceptionNoPID() {
		String PID = "";

		try {
			nmCoreImpl.getServiceByPID(PID);
			// if we get here, there was no exception
			fail("There should be an exception because no PID was given.");
		} catch(RuntimeException e){
			// Check if an exception with the right message was thrown
			assertEquals("PID not specificed", e.getMessage());
		}
		// Checks that method was not called
		verify(nmCoreImpl.identityManager, times(0)).getServicesByAttributes(any(String.class));
	}

	/**
	 * Tests registerService and checks if a Registration object is returned.
	 */
	@Test
	public void testRegisterService() {
		String endpoint = "for test not important";
		Part[] attributes = new Part[] { 
				new Part("DESCRIPTION", "description"), 
				new Part("PID", "Unique PID")};
		String backboneName = "backbone1";
		when(nmCoreImpl.identityManager.getServiceByAttributes(
				new Part[]{attributes[1]},
				IdentityManager.SERVICE_RESOLVE_TIMEOUT,
				false, false)).thenReturn(new Registration[0]);
		when(nmCoreImpl.identityManager.createServiceByAttributes(attributes)).
		thenReturn(new Registration(new VirtualAddress(), attributes));

		try {
			// call the method in test
			Registration registration = nmCoreImpl.registerService(attributes, endpoint, backboneName);
			// check that something was returned
			assertNotNull(registration);
		} catch (RemoteException e) {
			e.printStackTrace();
			fail("Exception occured " + e);
		}
	}

	/**
	 * Tests registerService, throws exception because PID already exists for another VirtualAddress
	 */
	@Test
	public void testRegisterServiceWithException() {
		String endpoint = "for test not important";
		String backboneName = "backbone1";
		Part[] attributes = new Part[] { 
				new Part("DESCRIPTION", "description"), 
				new Part("PID", "Unique PID")};

		Registration registration = new Registration(new VirtualAddress(), attributes);
		Registration[] returnedRegistrations = new Registration[1];
		returnedRegistrations[0] = registration;

		// "returning" that PID already exists
		when(nmCoreImpl.identityManager.getServiceByAttributes(
				new Part[]{attributes[1]},
				IdentityManager.SERVICE_RESOLVE_TIMEOUT,
				false, false)).
				thenReturn(returnedRegistrations);

		try {
			// call the method under test
			nmCoreImpl.registerService(attributes, endpoint, backboneName);
			// Should not reach this point
			fail("An exception should have occured saying that the PID is already in use.");
		} catch (RemoteException e) {
			// This exception should not be thrown
			e.printStackTrace();
			fail("Wrong exception occured " + e);
		} catch (IllegalArgumentException e) {
			assertEquals("PID already in use. Please choose a different one.", 
					e.getMessage());
		}
	}


	/**
	 * Gets test data needed in tests
	 * @return byte[] from processed message
	 * @throws Exception
	 */
	private byte[] getData() throws Exception {
		Message message = new Message(topic, senderVirtualAddress, receiverVirtualAddress, data);
		return new NOPConnection(senderVirtualAddress, receiverVirtualAddress).processMessage(message);
	}

	private void mockForSucessfulReceiveData() {
		nmCoreImpl.connectionManager = mock(ConnectionManager.class);
		Connection connection = mock(Connection.class);
		try {
			when(nmCoreImpl.connectionManager.getConnection(any(VirtualAddress.class), any(VirtualAddress.class))).
			thenReturn(connection);
			when(nmCoreImpl.connectionManager.getBroadcastConnection(eq(senderVirtualAddress))).
			thenReturn(connection);
			when(connection.processData(eq(senderVirtualAddress), any(VirtualAddress.class), any(byte[].class))).
			thenReturn(new Message(topic, senderVirtualAddress, senderVirtualAddress, data));
			when(connection.processMessage(any(Message.class))).
			thenReturn(data);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured " +e);
		}
	}
}
