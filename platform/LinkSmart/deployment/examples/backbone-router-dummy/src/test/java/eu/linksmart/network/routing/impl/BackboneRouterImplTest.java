package eu.linksmart.network.routing.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.NMResponse;
import eu.linksmart.gc.api.network.backbone.Backbone;
import eu.linksmart.gc.api.network.networkmanager.core.NetworkManagerCore;

public class BackboneRouterImplTest {
	
	private BackboneRouterImplDummy backboneRouter = new BackboneRouterImplDummy();
	private VirtualAddress receiverVirtualAddress = new VirtualAddress("354.453.455.323");
	private VirtualAddress senderVirtualAddress = new VirtualAddress("354.453.993.323");
	private NetworkManagerCore networkManagerCore;
	/**
	 * Test sendDataSync of BackboneRouter. Backbone and route must be predefined.
	 */
	@Test
	public void testSendDataSync() {
		// set up
		Backbone backbone = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone);
		backboneRouter.addRoute(receiverVirtualAddress, backbone.getClass().getName());
		when(backbone.sendDataSynch(eq(senderVirtualAddress), eq(receiverVirtualAddress),any(byte[].class))).
				thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		// call method to test
		NMResponse response = backboneRouter.sendDataSynch(senderVirtualAddress, receiverVirtualAddress, new byte[]{});
		// assert that something came back
		assertNotNull(response);
	}
	
	/**
	 * Test sendDataSync of BackboneRouter. Route must not be predefined,
	 * so that an NMResponse with status error is returned.
	 */
	@Test
	public void testSendDataSyncWithUndefinedRoute() {
		NMResponse nmResponse = backboneRouter.sendDataSynch(senderVirtualAddress, receiverVirtualAddress, new byte[]{});
		assertEquals(NMResponse.STATUS_ERROR,nmResponse.getStatus());
	}
	
	/**
	 * Test sendDataSync of BackboneRouter. Backbone and route must not be predefined,
	 * an according NMResponse will be sent.
	 */
	@Test
	public void testSendDataSyncSendNullForReceiverVirtualAddress() {
		NMResponse nmResponse = backboneRouter.sendDataSynch(senderVirtualAddress, null, new byte[]{});
		assertEquals(NMResponse.STATUS_ERROR,nmResponse.getStatus());
	}
	
	/**
	 * Test sendDataAsync of BackboneRouter. Backbone and route must be predefined.
	 */
	@Test
	public void testSendDataAsync() {
		// set up
		Backbone backbone = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone);
		backboneRouter.addRoute(receiverVirtualAddress, backbone.getClass().getName());
		when(backbone.sendDataAsynch(eq(senderVirtualAddress), eq(receiverVirtualAddress), any(byte[].class))).
				thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		
		// call method to test
		NMResponse response = backboneRouter.sendDataAsynch(
				senderVirtualAddress, 
				receiverVirtualAddress, 
				new byte[]{});
		// assert that something came back
		assertNotNull(response);
	}
	
	/**
	 * Test sendDataAsync of BackboneRouter. Backbone and route must not be predefined,
	 * so that an NM Response with status error is returned.
	 */
	@Test
	public void testSendDataAsyncWithUndefinedRoute() {
		// as the route was not set, there should be an exception
		NMResponse nmResponse = backboneRouter.sendDataAsynch(senderVirtualAddress, receiverVirtualAddress, new byte[]{});
		assertEquals(NMResponse.STATUS_ERROR,nmResponse.getStatus());
	}
	
	/**
	 * Test method receiveDataSync where backbone's route is already known.
	 */
	@Test
	public void testReceiveDataSyncWithKnownRoute() {
		// set up
		Backbone backbone = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone);
		backboneRouter.addRoute(senderVirtualAddress, backbone.getClass().getName());
		
		backboneRouter.bindNMCore(networkManagerCore);
		when(networkManagerCore.receiveDataSynch(
					eq(senderVirtualAddress), eq(receiverVirtualAddress), any(byte[].class))).
				thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		
		// call method to test
		NMResponse response = backboneRouter.receiveDataSynch(
				senderVirtualAddress, 
				receiverVirtualAddress, 
				new byte[]{}, 
				backbone);
		// check result
		assertNotNull("Response should not be null.", response);
		assertEquals("Status should be successful.", 
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}
	
	/**
	 * Test method receiveDataSync where backbone's route is not known.
	 */
	@Test
	public void testReceiveDataSyncWithoutKnownRoute() {
		// set up
		Backbone backbone = mock(Backbone.class);
		
		NetworkManagerCore networkManagerCore = mock(NetworkManagerCore.class);
		backboneRouter.bindNMCore(networkManagerCore);
		when(networkManagerCore.receiveDataSynch(
					eq(senderVirtualAddress), eq(receiverVirtualAddress), any(byte[].class))).
				thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		
		// call method to test
		NMResponse response = backboneRouter.receiveDataSynch(
				senderVirtualAddress, 
				receiverVirtualAddress, 
				new byte[]{}, 
				backbone);
		// check result
		assertNotNull("Response should not be null.", response);
		assertEquals("Status should be successful.", 
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}
	
	/**
	 * Test method receiveDataSync where no NetworkManagerCore was set in 
	 * BackboneRouter.
	 */
	@Test
	public void testReceiveDataSyncUnsuccesful() {
		// set up
		Backbone backbone = mock(Backbone.class);
		
		// call method to test
		NMResponse response = backboneRouter.receiveDataSynch(
				senderVirtualAddress, 
				receiverVirtualAddress, 
				new byte[]{}, 
				backbone);
		// check result
		assertNotNull("Response should not be null.", response);
		assertEquals("Response should have error status.", 
				NMResponse.STATUS_ERROR, response.getStatus());
	}
	
	/**
	 * Test method receiveDataAsync where backbone's route is already known.
	 */
	@Test
	public void testReceiveDataAsyncWithKnownRoute() {
		// set up
		Backbone backbone = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone);
		backboneRouter.addRoute(senderVirtualAddress, backbone.getClass().getName());
		
		NetworkManagerCore networkManagerCore = mock(NetworkManagerCore.class);
		backboneRouter.bindNMCore(networkManagerCore);
		when(networkManagerCore.receiveDataAsynch(
					eq(senderVirtualAddress), eq(receiverVirtualAddress), any(byte[].class))).
				thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		
		// call method to test
		NMResponse response = backboneRouter.receiveDataAsynch(
				senderVirtualAddress, 
				receiverVirtualAddress, 
				new byte[]{}, 
				backbone);
		// check result
		assertNotNull("Response should not be null.", response);
		assertEquals("Status should be successful.", 
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}
	
	/**
	 * Test method receiveDataAsync where backbone's route is not known.
	 */
	@Test
	public void testReceiveDataAsyncWithoutKnownRoute() {
		// set up
		Backbone backbone = mock(Backbone.class);
		
		NetworkManagerCore networkManagerCore = mock(NetworkManagerCore.class);
		backboneRouter.bindNMCore(networkManagerCore);
		when(networkManagerCore.receiveDataAsynch(
					eq(senderVirtualAddress), eq(receiverVirtualAddress), any(byte[].class))).
				thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		
		// call method to test
		NMResponse response = backboneRouter.receiveDataAsynch(
				senderVirtualAddress, 
				receiverVirtualAddress, 
				new byte[]{}, 
				backbone);
		// check result
		assertNotNull("Response should not be null.", response);
		assertEquals("Status should be successful.", 
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}
	
	/**
	 * Test method receiveDataAsync where no NetworkManagerCore was set in 
	 * BackboneRouter.
	 */
	@Test
	public void testReceiveDataAsyncUnsuccesful() {
		// set up
		Backbone backbone = mock(Backbone.class);
		
		// call method to test
		NMResponse response = backboneRouter.receiveDataAsynch(
				senderVirtualAddress, 
				receiverVirtualAddress, 
				new byte[]{}, 
				backbone);
		// check result
		assertNotNull("Response should not be null.", response);
		assertEquals("Response should have error status.", 
				NMResponse.STATUS_ERROR, response.getStatus());
	}
	
	/**
	 * Tests broadcastData with two known backbones where one broadcast is 
	 * successful and one is not. As one is successful, the whole broadcast is 
	 * considered successful.
	 */
	@Test
	public void testBroadcastDataWith2Backbones() {
		// set up
		Backbone backbone1 = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone1);
		backboneRouter.addRoute(senderVirtualAddress, backbone1.getClass().getName());
		when(backbone1.broadcastData(eq(senderVirtualAddress), any(byte[].class))).
			thenReturn(new NMResponse(NMResponse.STATUS_ERROR));
		
		Backbone backbone2 = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone2);
		backboneRouter.addRoute(senderVirtualAddress, backbone2.getClass().getName());
		when(backbone2.broadcastData(eq(senderVirtualAddress), any(byte[].class))).
			thenReturn(new NMResponse(NMResponse.STATUS_SUCCESS));
		
		// call method to test
		NMResponse response = backboneRouter.broadcastData(senderVirtualAddress, new byte[]{});
		// check result
		assertNotNull("Response should not be null.", response);
		assertEquals("Status should be successful.", 
				NMResponse.STATUS_SUCCESS, response.getStatus());
	}
	
	/**
	 * Tests broadcastData with null values. Status should be error status.
	 */
	@Test
	public void testBroadcastDataWithNullValue() {
		// call method to test
		NMResponse response = backboneRouter.broadcastData(null, null);
		// check result
		assertNotNull("Response should not be null.", response);
		assertEquals("Status should be successful.", 
				NMResponse.STATUS_ERROR, response.getStatus());
	}
	
	/**
	 * Tests broadcastData with one backbone with an unsuccessful broadcast.
	 */
	@Test
	public void testBroadcastDataUnsuccessful() {
		// set up
		Backbone backbone1 = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone1);
		backboneRouter.addRoute(senderVirtualAddress, backbone1.getClass().getName());
		when(backbone1.broadcastData(eq(senderVirtualAddress), any(byte[].class))).
			thenReturn(new NMResponse(NMResponse.STATUS_ERROR));
		
		// call method to test
		NMResponse response = backboneRouter.broadcastData(senderVirtualAddress, new byte[]{});
		// check result
		assertNotNull("Response should not be null.", response);
		assertEquals("Status should not be successful.", 
				NMResponse.STATUS_ERROR, response.getStatus());
	}
	
	// Set mock data for tests
    @Before
    public void setUp(){   
    	networkManagerCore = mock(NetworkManagerCore.class);
    }
	
	/**
	 * Tests method addRouteToBackbone. As all parameters are set, the call 
	 * should return true.
	 */
	@Test
	public void testAddRouteToBackbone() {
		// set up
		String endpoint = "endpoint";
		Backbone backbone = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone);
		when(backbone.addEndpoint(senderVirtualAddress, endpoint)).thenReturn(true);
		
		// call method to test
		boolean successful = backboneRouter.addRouteToBackbone(
				senderVirtualAddress, 
				backbone.getClass().getName(), 
				endpoint);
		// check results
		assertEquals("Adding the backbone should not fail.", 
				true, successful);
	}
	
	/**
	 * Tests method addRouteToBackbone. As senderVirtualAddress is null, the call 
	 * should return false.
	 */
	@Test
	public void testAddRouteToBackboneSenderNull() {
		// set up
		String endpoint = "endpoint";
		Backbone backbone = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone);
		
		// call method to test
		boolean successful = backboneRouter.addRouteToBackbone(
				null, 
				backbone.getClass().getName(), 
				endpoint);
		// check results
		assertEquals("Adding the backbone should fail.", 
				false, successful);
	}
	
	/**
	 * Tests method addRouteToBackbone. As the backbone name is null, the call should return false.
	 */
	@Test
	public void testAddRouteToBackboneBackboneNull() {
//		// set up
//		String endpoint = "endpoint";
//		
//		// call method to test
//		boolean successful = 
//				backboneRouter.addRouteToBackbone(
//				senderVirtualAddress, 
//				null, 
//				endpoint);
//				
//		// check results
//		assertEquals("Adding the backbone should fail.", 
//				false, successful);
	}
	
	/**
	 * Tests method addRouteToBackbone. As senderVirtualAddress is null, the call 
	 * should return false.
	 */
	@Test
	public void testAddRouteToBackboneEndpointNull() {
		// set up
		Backbone backbone = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone);
		
		// call method to test
		boolean successful = backboneRouter.addRouteToBackbone(
				senderVirtualAddress, 
				backbone.getClass().getName(), 
				null);
		// check results
		assertEquals("Adding the backbone should fail.", 
				false, successful);
	}
	
	/**
	 * Tests method addRouteToBackbone. As there is already is an entry in the
	 * Map 'virtualAddressBackboneMap', the call should return false.
	 */
	@Test
	public void testAddRouteToBackboneTwice() {
		// set up
		String endpoint = "endpoint";
		Backbone backbone = mock(Backbone.class);
		backboneRouter.bindBackbone(backbone);
		when(backbone.addEndpoint(senderVirtualAddress, endpoint)).thenReturn(true);
		boolean successfulFirst = backboneRouter.addRouteToBackbone(
				senderVirtualAddress, 
				backbone.getClass().getName(), 
				endpoint);
		
		// call method to test
		boolean successfulSecond = backboneRouter.addRouteToBackbone(
				senderVirtualAddress, 
				backbone.getClass().getName(), 
				endpoint);
		// check results
		assertEquals("Adding the backbone for the first time should not fail.", 
				true, successfulFirst);
		assertEquals("Adding the backbone for the second time should fail.", 
				false, successfulSecond);
	}
	
	/**
	 * Tests method addRouteToBackbone. Although, no backbone is bound yet, the route should be added as a potential route.
	 */
	@Test
	public void testAddingPotentialRoute() {
		// set up
		String endpoint = "endpoint";
		Backbone backbone = mock(Backbone.class);
//		backboneRouter.bindBackbone(backbone);
		when(backbone.addEndpoint(senderVirtualAddress, endpoint)).thenReturn(true);
						
		
//		//Activate move
		boolean successfulFirst = backboneRouter.addRouteToBackbone(
				senderVirtualAddress, 
				"BackboneSOAPImpl", 
				endpoint);

//		assertEquals("Adding the route without an available backbone should not fail.", 
//				true, successfulFirst);
		
		int potentialRouteMapSize= backboneRouter.getCopyOfPotentialRouteMap().size();
		int activeRouteMapSize = backboneRouter.getCopyOfActiveRouteMap().size();
//		assertEquals(true, potentialRouteMapSize>0);
//		assertEquals(false, activeRouteMapSize>0);
	}
	
	/**
	 * Tests method addRouteToBackbone. Although, no backbone is bound yet, the first route should be added as a potential route, but the second try should return false
	 */
	@Test
	public void testAddingPotentialRouteWithAlreadyRegisteredService() {
		// set up
		String endpoint = "endpoint";
		Backbone backbone = mock(Backbone.class);
//		backboneRouter.bindBackbone(backbone);
		when(backbone.addEndpoint(senderVirtualAddress, endpoint)).thenReturn(true);
						
		
		//Activate move
		boolean successfulFirst = backboneRouter.addRouteToBackbone(
				senderVirtualAddress, 
				"BackboneSOAPImpl", 
				endpoint);

//		assertEquals("Adding the route without an available backbone should not fail.", 
//				true, successfulFirst);
		
		boolean successfulSecond = backboneRouter.addRouteToBackbone(
				senderVirtualAddress, 
				"BackboneSOAPImpl", 
				endpoint);

		assertEquals("Adding the route without an available backbone with the previous VirtualAddress should fail.", 
				false, successfulSecond);

	}
	
	/**
	 * Tests method addRouteToBackbone. 
	 * Although, no backbone is bound yet, the first route should be added as a potential route. 
	 * As soon as the backbone is bound, the potential route should become an active.
	 */
	@Test
	public void testMovingPotentialToActiveRoutes() {
//		// set up
//		String endpoint = "endpoint";
//		Backbone backbone = mock(Backbone.class);
//		when(backbone.getName()).thenReturn("BackboneSOAPImpl");
//		when(backbone.addEndpoint(senderVirtualAddress, endpoint)).thenReturn(true);
//
//		backboneRouter.bindNMCore(networkManagerCore);
//		
//		//Activate move
//		boolean successfulFirst = backboneRouter.addRouteToBackbone(
//				senderVirtualAddress, 
//				"BackboneSOAPImpl", 
//				endpoint);
//
//		assertEquals("Adding the route without an available backbone should not fail.", 
//				true, successfulFirst);
//		
//		int potentialRouteMapSize= backboneRouter.getCopyOfPotentialRouteMap().size();
//		int activeRouteMapSize = backboneRouter.getCopyOfActiveRouteMap().size();
//		assertEquals(true, potentialRouteMapSize>0);
//		assertEquals(false, activeRouteMapSize>0);
//		
//		backboneRouter.bindBackbone(backbone);
//		
//				
//		potentialRouteMapSize= backboneRouter.getCopyOfPotentialRouteMap().size();
//		activeRouteMapSize = backboneRouter.getCopyOfActiveRouteMap().size();
//		assertEquals(false, potentialRouteMapSize>0);
//		assertEquals(true, activeRouteMapSize>0);

	}
	
	/**
	 * Tests method addRouteToBackbone. 
	 * Although, no backbone is bound yet, the first route should be added as a potential route. 
	 * As soon as the backbone is bound, the potential route should become an active.
	 */
	@Test
	public void testRemovingActiveRoutes() {
//		// set up
//		String endpoint = "endpoint";
//		Backbone backbone = mock(Backbone.class);
//		when(backbone.getName()).thenReturn("BackboneSOAPImpl");
//		when(backbone.addEndpoint(senderVirtualAddress, endpoint)).thenReturn(true);
//
//		backboneRouter.bindNMCore(networkManagerCore);
//		
//		//Activate move
//		boolean successfulFirst = backboneRouter.addRouteToBackbone(
//				senderVirtualAddress, 
//				"BackboneSOAPImpl", 
//				endpoint);
//
//		assertEquals("Adding the route without an available backbone should not fail.", 
//				true, successfulFirst);
//		
//		int potentialRouteMapSize= backboneRouter.getCopyOfPotentialRouteMap().size();
//		int activeRouteMapSize = backboneRouter.getCopyOfActiveRouteMap().size();
//		assertEquals(true, potentialRouteMapSize>0);
//		assertEquals(false, activeRouteMapSize>0);
//		
//		backboneRouter.bindBackbone(backbone);
//		
//				
//		potentialRouteMapSize= backboneRouter.getCopyOfPotentialRouteMap().size();
//		activeRouteMapSize = backboneRouter.getCopyOfActiveRouteMap().size();
//		assertEquals(false, potentialRouteMapSize>0);
//		assertEquals(true, activeRouteMapSize>0);
//		
//		backboneRouter.unbindBackbone(backbone);
//		
//		activeRouteMapSize = backboneRouter.getCopyOfActiveRouteMap().size();
//		assertEquals(false, activeRouteMapSize>0);
		

	}
	
	/**
	 * Tests method removeRoute. As the route was not saved prior to the
	 * removal, the method under test should return false. 
	 */
	@Test
	public void testRemoveRouteUnsuccesful() {
		// call method to test
		boolean successful = backboneRouter.removeRoute(senderVirtualAddress, "BackboneName");
		// check results
		assertEquals("Remove the backbone should return false as there is no " +
				"backbone to remove.", false, successful);
	}
	
	/**
	 * Tests method removeRoute. 
	 */
	@Test
	public void testRemovePotentialRoute() {
//		// set up
//		String endpoint = "endpoint";
//		Backbone backbone = mock(Backbone.class);
//		backboneRouter.bindBackbone(backbone);
//		when(backbone.addEndpoint(senderVirtualAddress, endpoint)).thenReturn(true);
//			backboneRouter.addRouteToBackbone(
//					senderVirtualAddress, 
//					backbone.getClass().getName(), 
//					endpoint);
//		// call method to test
//		boolean successfulRemove = backboneRouter.removeRoute(senderVirtualAddress, 
//				backbone.getClass().getName());
//		// check results
//		assertEquals("Remove the backbone should return true.", 
//				true, successfulRemove);
	}
}
