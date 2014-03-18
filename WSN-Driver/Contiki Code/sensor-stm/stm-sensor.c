/*
 * stm-sensor.c
 *
 *  Created on: Feb 28, 2014
 *      Author: "Prabhakaran Kasinathan"
 */
#include "contiki.h"
//Custom Headers Includes / Defines / Functions
#include "stm-sensor.h"

/*
 * Definitions for UDP Connections
 */
#define UDP_REMOTE_PORT 7730
#define UDP_LOCAL_PORT  7731

static struct uip_udp_conn *udp_client_conn;
static uip_ipaddr_t node_ipaddr, pc_ipaddr, mcast_ipaddr;
/*
 * Payload Defines
 */
static payload_data_t data_payload;


/*---------------------------------------------------------------------------*/
/*
 * Write Custom Functions before Main process Thread
 * Process Declaration and AutoStart
 */
/*---------------------------------------------------------------------------*/
PROCESS(discovery_process, "Discovery Process");
PROCESS(wsn_driver_process, "WSN-Driver Process");
AUTOSTART_PROCESSES(&wsn_driver_process);

/*---------------------------------------------------------------------------*/
/*
 * Send Data packet
*/
static void send_packet (payload_data_t *payload, uint8_t len) {
	//send the udp packet

	uint8_t i,j;
	j=0;
	uint8_t data[len+2];
	PRINTF("\n Payload Data: type: %x, Len: %x \n data:[",payload->type,payload->pay_len);
	data[0]=payload->type;
	data[1]=payload->pay_len;
	for(i=2;i<len+2;i++){
		PRINTF("%x ,",payload->data[j]);
		data[i]=payload->data[j];j++;
	}
	PRINTF("]\n");
	uip_udp_packet_sendto(udp_client_conn, data, sizeof(data), &pc_ipaddr, UIP_HTONS(UDP_REMOTE_PORT));
	PRINTF("\nSent TYPE_DATA_PAYLOAD");
}
/*---------------------------------------------------------------------------*/
/*
 * Send Ack Packet
*/
static void send_ack(uint8_t seq_no) {
	//send the udp packet
	uint8_t ack_dis[1];
	ack_dis[0] = (uint8_t *) ACK_PAYLOAD;
	ack_dis[1] = seq_no;
	uip_udp_packet_sendto(udp_client_conn, ack_dis, 2, &pc_ipaddr, UIP_HTONS(UDP_REMOTE_PORT));
	PRINTF("\nSent Ack Packet :[%x, %x]",ack_dis[0],ack_dis[1]);
}
/*---------------------------------------------------------------------------*/
/*
 * Get Temperature readings
 */
int read_temperature(void)
{
    unsigned int temp = temperature_sensor.value(0);

    //printf("Temp: %d.%d �C  \r",temp/10,temp-(temp/10)*10);
    //printf("(X,Y,Z): (%d,%d,%d) mg      \r",acc_sensor.value(ACC_X_AXIS),acc_sensor.value(ACC_Y_AXIS),acc_sensor.value(ACC_Z_AXIS));
    return (temp/10);
}
/*---------------------------------------------------------------------------*/
/*
 * request alive or not
 */
void request_discovery(){
	uint8_t index=0;

	PRINTF("\n \t Requesting Discovery");

	data_payload.type = (uint8_t *) REQ_DISC;
	//data_payload.data[index] = (uint8_t *) seq_no;	index++;
	data_payload.pay_len = index;

	//uip_udp_packet_sendto(udp_client_conn, &data_payload, index, &pc_ipaddr, UIP_HTONS(UDP_REMOTE_PORT));
	send_packet(&data_payload,index);
}
/*---------------------------------------------------------------------------*/
/*
 * request sensors
 */
void request_sensors(){
	PRINTF("\n Requested Sensors Type");
	uint8_t index=0;
	data_payload.type = (uint8_t *) REQ_SENSORS;
	data_payload.data[index] = (uint8_t *) SENSOR_TEMP;	index++;
	data_payload.data[index] = (uint8_t *) SENSOR_ACCEL;index++;
	data_payload.pay_len = index;

	//uip_udp_packet_sendto(udp_client_conn, &data_payload, index, &pc_ipaddr, UIP_HTONS(UDP_REMOTE_PORT));
	send_packet(&data_payload,index);
}
/*---------------------------------------------------------------------------*/
void get_temperature(){
	PRINTF("\n Requested Temp Data");
	uint8_t index=0;
	b32_t temp;
	temp.value = read_temperature();
	//PRINTF("\n temp: %d",temp.value);

	data_payload.type = SENSOR_TEMP;
	data_payload.data[index] = temp.byte[3]; index++;
	data_payload.data[index] = temp.byte[2]; index++;
	data_payload.data[index] = temp.byte[1]; index++;
	data_payload.data[index] = temp.byte[0]; index++;
	data_payload.pay_len = (uint8_t *) index;

	send_packet(&data_payload,index);
	//uip_udp_packet_sendto(udp_client_conn, &data_payload, index, &pc_ipaddr, UIP_HTONS(UDP_REMOTE_PORT));
}
/*---------------------------------------------------------------------------*/
void get_accel(){
	PRINTF("\n \t Requested Accel Data");
	uint8_t index=0;
	b32_t xaxis,yaxis,zaxis;

	xaxis.value = acc_sensor.value(ACC_X_AXIS);
	yaxis.value = acc_sensor.value(ACC_Y_AXIS);
	zaxis.value = acc_sensor.value(ACC_Z_AXIS);

	data_payload.type = SENSOR_ACCEL;

	data_payload.data[index] = xaxis.byte[3];index++;
	data_payload.data[index] = xaxis.byte[2];index++;
	data_payload.data[index] = xaxis.byte[1];index++;
	data_payload.data[index] = xaxis.byte[0];index++;

	data_payload.data[index] = yaxis.byte[3];index++;
	data_payload.data[index] = yaxis.byte[2];index++;
	data_payload.data[index] = yaxis.byte[1];index++;
	data_payload.data[index] = yaxis.byte[0];index++;

	data_payload.data[index] = zaxis.byte[3];index++;
	data_payload.data[index] = zaxis.byte[2];index++;
	data_payload.data[index] = zaxis.byte[1];index++;
	data_payload.data[index] = zaxis.byte[0];index++;

	data_payload.pay_len = (uint8_t *) index;

	//uip_udp_packet_sendto(udp_client_conn, &data_payload, index, &pc_ipaddr, UIP_HTONS(UDP_REMOTE_PORT));
	send_packet(&data_payload,index);
}

/*---------------------------------------------------------------------------*/
/*
 * Get Temperature();
 *
 */
void get_all_sensors(){
	uint8_t index=0;

	b32_t temp;
	b32_t xaxis,yaxis,zaxis;


	temp.value = read_temperature();
	xaxis.value = acc_sensor.value(ACC_X_AXIS);
	yaxis.value = acc_sensor.value(ACC_Y_AXIS);
	zaxis.value = acc_sensor.value(ACC_Z_AXIS);


	data_payload.type = SENSOR_ALL;
	// Temp
	data_payload.data[index] = SENSOR_TEMP; index++;

	data_payload.data[index] = temp.byte[3]; index++;
	data_payload.data[index] = temp.byte[2]; index++;
	data_payload.data[index] = temp.byte[1]; index++;
	data_payload.data[index] = temp.byte[0]; index++;

	//Accel
	data_payload.data[index] = SENSOR_ACCEL;index++;

	data_payload.data[index] = xaxis.byte[3];index++;
	data_payload.data[index] = xaxis.byte[2];index++;
	data_payload.data[index] = xaxis.byte[1];index++;
	data_payload.data[index] = xaxis.byte[0];index++;

	data_payload.data[index] = yaxis.byte[3];index++;
	data_payload.data[index] = yaxis.byte[2];index++;
	data_payload.data[index] = yaxis.byte[1];index++;
	data_payload.data[index] = yaxis.byte[0];index++;

	data_payload.data[index] = zaxis.byte[3];index++;
	data_payload.data[index] = zaxis.byte[2];index++;
	data_payload.data[index] = zaxis.byte[1];index++;
	data_payload.data[index] = zaxis.byte[0];index++;

	data_payload.pay_len = (uint8_t *) index;

	//uip_udp_packet_sendto(udp_client_conn, &data_payload, index, &pc_ipaddr, UIP_HTONS(UDP_REMOTE_PORT));
	send_packet(&data_payload,index);
}

/*---------------------------------------------------------------------------*/
/*
 * TCP IP Event Handler
 */
static void tcpip_handler(void) {
	static received_payload_t *recv_pkt;


	if(uip_newdata()) {

		recv_pkt=(received_payload_t *)uip_appdata;
		PRINTF("\n Received a pkt: [%x, %x, %x]",recv_pkt->type,recv_pkt->seq_num,recv_pkt->sensor);

		switch (recv_pkt->type) {

		case ACK_DISC:
			process_exit(&discovery_process);
			PRINTF("\n \t Discovery Process Killed");
			send_ack((uint8_t *) recv_pkt->seq_num); // completing the handshake
			break;

		case REQ_SENSORS:
			request_sensors();
			break;

		case REQ_DATA:
			PRINTF("\n Req Data Pkt");
			switch (recv_pkt->sensor) {

			case SENSOR_ALL:
				get_all_sensors();
				break;
			case SENSOR_TEMP:
				get_temperature();
				break;
			case SENSOR_ACCEL:
				get_accel();
				break;
			default:
				break;
			}
			break;
			default:
				break;
		}

	}
}
/*---------------------------------------------------------------------------*/
static void set_global_address(void)
{
	//create a global IP address, based on the MAC address
	uip_ip6addr(&node_ipaddr, 0xebb1, 0, 0, 0, 0, 0, 0, 0);
	uip_ds6_set_addr_iid(&node_ipaddr, &uip_lladdr);
	uip_ds6_addr_add(&node_ipaddr, 0, ADDR_AUTOCONF);

	//create a manual global IP address
	uip_ip6addr(&node_ipaddr, 0xebb1, 0, 0, 0, 0, 0, 0, 0x2);
	uip_ds6_addr_add(&node_ipaddr, 0, ADDR_MANUAL);

	//Define PC destination IP address
	uip_ip6addr(&pc_ipaddr, 0xebb1, 0, 0, 0, 0, 0, 0, 1);

	//set the multicast IP address
	uip_ip6addr(&mcast_ipaddr, 0xff02, 0, 0, 0, 0, 0, 0, 1);
}
/*---------------------------------------------------------------------------*/
static void print_local_addresses(void)
{
  int i;
  uint8_t state;

  PRINTF("Client IPv6 addresses: ");
  for(i = 0; i < UIP_DS6_ADDR_NB; i++) {
    state = uip_ds6_if.addr_list[i].state;
    if(uip_ds6_if.addr_list[i].isused &&
       (state == ADDR_TENTATIVE || state == ADDR_PREFERRED)) {
      PRINT6ADDR(&uip_ds6_if.addr_list[i].ipaddr);
      PRINTF("\n");
      /* hack to make address "final" */
      if (state == ADDR_TENTATIVE) {
	uip_ds6_if.addr_list[i].state = ADDR_PREFERRED;
      }
    }
  }
}



/*---------------------------------------------------------------------------*/
PROCESS_THREAD(discovery_process, ev, data){

	static struct etimer discv_timer;
	PROCESS_BEGIN();

	etimer_set(&discv_timer, 20 * CLOCK_SECOND);
	PROCESS_WAIT_UNTIL(etimer_expired(&discv_timer));
	uint8_t disc_attempts = 10;

	while(disc_attempts >0 ){
		request_discovery();
		etimer_set(&discv_timer, 5 * CLOCK_SECOND);
		PROCESS_WAIT_UNTIL(etimer_expired(&discv_timer));
		disc_attempts--;
	}
	PROCESS_END();
}
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(wsn_driver_process, ev, data)
{
  static struct etimer wait_timer;

  PROCESS_BEGIN();
  // TURN off RDC tp enable packet reception at all-time : (more energy consumption)
  NETSTACK_MAC.off(1);

  PROCESS_PAUSE();

  leds_on(LEDS_RED);
  /*
   * Standard Procedure to Create UDP Connection with Server
   * This is a UDP Client Process:
   */
  set_global_address();

  PRINTF("UDP client process started\n");

  print_local_addresses();

  /* receive connection from any (0) host */
  udp_client_conn = udp_new(NULL, UIP_HTONS(0), NULL);
  udp_bind(udp_client_conn, UIP_HTONS(UDP_LOCAL_PORT));

  /*
   * Activate Sensors: Should Be moved to Activate Sensors Functions:
   */
  SENSORS_ACTIVATE(acc_sensor);
  SENSORS_ACTIVATE(temperature_sensor);
  SENSORS_ACTIVATE(button_sensor);
  boardPrintStringDescription();
  printf(" \n Initializing All Sensors \r\n");
  // Standard Procedure
  leds_off(LEDS_RED);

  process_start(&discovery_process,NULL);

  while(1) {
	  PROCESS_YIELD(); // waits until something happens

	  if(ev == tcpip_event) { //occurs when a packet is received
		  tcpip_handler(); //execute this function
	  }

	  /*
	   * Testing
	  PROCESS_WAIT_EVENT_UNTIL(ev == sensors_event && data == &button_sensor);
	  leds_toggle(LEDS_RED);
	  //get_temperature();
	  get_all_sensors();
	  */
  }
  PROCESS_END();
}

