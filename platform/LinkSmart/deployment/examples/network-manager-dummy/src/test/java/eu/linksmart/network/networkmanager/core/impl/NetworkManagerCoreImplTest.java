package eu.linksmart.network.networkmanager.core.impl;

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

import eu.linksmart.gc.api.network.Message;
import eu.linksmart.gc.api.network.NMResponse;
import eu.linksmart.gc.api.network.Registration;
import eu.linksmart.gc.api.network.ServiceAttribute;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.identity.IdentityManager;
import eu.linksmart.gc.api.network.routing.BackboneRouter;
import eu.linksmart.gc.api.utils.Part;

/**
 * Test class for NetworkManagerCoreImpl 
 */
public class NetworkManagerCoreImplTest {

	private NetworkManagerCoreImplDummy nmCoreImpl;
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
		
	}

	/**
	 * Tests broadcastMessage of NetworkManagerCoreImpl. 
	 */
	@Test
	public void testBroadcastMessage() {
		
	}

	/**
	 * Tests the synchronous sendMessage call
	 */
	@Test
	public void testSendMessageSync() {
		
	}

	/**
	 * Tests the asynchronous sendMessage call
	 */
	@Test
	public void testSendMessageAsync() {
		
	}

	/**
	 * Tests receiveDataSync else-case, which occurs if neither the receiverVirtualAddress 
	 * is null, nor does the identity manager contain the receiver registration object.
	 */
	@Test
	public void  testReceiveDataSync() {
		
	}

	/**
	 * Tests receiveDataSync where processing is not successful.
	 */
	@Test
	public void  testReceiveDataSyncUnsuccessful() {
		
	}

	/**
	 * Tests receiveDataSync with broadcasting message, but without processing it
	 * Because it is not processed, an exception should occur.
	 */
	@Test
	public void  testReceiveDataSyncBroadcastUnsucessful() {

	}

	/**
	 * Tests receiveDataSync with broadcasting message as there is no receiver VirtualAddress
	 */
	@Test
	public void  testReceiveDataSyncBroadcast() {
		
	}

	/**
	 * Tests receiveData with broadcasting message as there is no receiver VirtualAddress
	 */
	@Test
	public void  testReceiveDataAsyncBroadcast() {
		
	}

	/**
	 * Tests receiveData with broadcasting message as there is no receiver VirtualAddress
	 * and with an empty return message
	 */
	@Test
	public void  testReceiveDataAsyncBroadcastEmtpyMessage() {
		
	}

	/**
	 * Tests receiveDataSync else-case, which occurs if neither the receiverVirtualAddress 
	 * is null, nor does the identity manager contain the receiverRegistration object.
	 */
	@Test
	public void  testReceiveDataAsync() {
		
	}

	/**
	 * Tests receiveDataAsync with broadcasting message, but without processing it
	 * Because it is not processed, an exception should occur.
	 */
	@Test
	public void  testReceiveDataAsyncBroadcastUnsucessful() {

	}

	/**
	 * Tests that if addRemoteVirtualAddress. the right backboneRouter-method is called
	 */
	@Test
	public void  testAddRemoteVirtualAddress() {
		
	}

	/**
	 * Tests getServiceByDescription
	 */
	@Test
	public void testGetServiceByDescription() {
		
	}

	/**
	 * Tests getServiceByPID
	 */
	@Test
	public void testGetServiceByPID() {
		
	}

	/**
	 * Tests if there are more than one Registration returned for getServiceByPID
	 */
	@Test
	public void testGetServiceByPIDWithExceptionSeveralRegistrations() {
		
	}

	/**
	 * Tests if there was no PID given for getServiceByPID
	 */
	@Test
	public void testGetServiceByPIDWithExceptionNoPID() {
		
	}

	/**
	 * Tests registerService and checks if a Registration object is returned.
	 */
	@Test
	public void testRegisterService() {
		
	}

	/**
	 * Tests registerService, throws exception because PID already exists for another VirtualAddress
	 */
	@Test
	public void testRegisterServiceWithException() {
		
	}

}
