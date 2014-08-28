/**
 *@file stm_sensors.c
 *@brief This file contains the main application code
 * that transmits real-time accelerometer sensor readings
 * of the STM32W platform to a Java application.
 * The application also receives commands from the
 * Java application to control the application's behavior.
 *
 *@author Hussein KHALEEL <khaleel@ismb.it>
 */



#include "contiki.h"
#include "contiki-lib.h"
#include <stdio.h>
#include "contiki-net.h"
#include "net/uip.h"
#include "net/rpl/rpl.h"
#include "net/netstack.h"
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#define DEBUG DEBUG_PRINT
#include "net/uip-debug.h"

#include "pt.h"
#include "dev/leds.h"

#include "dev/button-sensor.h"
#include "dev/light-sensor.c"
#include "dev/temperature-sensor.h"
#include "payload.h"

#define UIP_IP_BUF	((struct uip_ip_hdr *)&uip_buf[UIP_LLH_LEN])
#define UDP_IP_HDR	((struct uip_udpip_hdr *)&uip_buf[UIP_LLH_LEN])

#define UDP_REMOTE_PORT 7730
#define UDP_LOCAL_PORT  7731
//------------------------------------------------------------------------------------
//PROCESS(tx_process,"tx_process");
//PROCESS(tcpip_handler_process,"tcpip_handler_process");
PROCESS(stm_sensors_process, "stm_sensors_process");
AUTOSTART_PROCESSES(&stm_sensors_process);
//------------------------------------------------------------------------------------
static struct etimer wait_timer;
static uip_ipaddr_t bcast_ipaddr, pc_ipaddr;
static struct uip_udp_conn *udp_conn;
static data_payload_t data_payload;
static uint16_t data_seq_num = 0;
static control_payload_t* control_payload_ptr;

static float sampling_rate = 10; // sampling rate in seconds, this is the initial value
static uint16_t sampling_rate_ms; //sampling rate in milliseconds

//variables for buffering samples
static uint8_t buffer_samples = 0;
static uint8_t buf_idx = 0;
//static uint8_t buf_len = 3;
static data_payload_buffer_t data_payload_buffer;

//---------------------------------------------------------------------------
/**
 * @name tcpip_handler()
 * 
 * @brief This function is called whenever the node receives a packet.
 * The packet is distinguished according to the first byte of its payload,
 * which contains the payload typem, as follows:
 *
 * <B>TYPE_CONTROL_PAYLOAD</B> indicates that this packet contains a command
 * to control operational parameters of the application.
 * \n The commands are as follows:
 * @param COMMAND_SET_RATE indicates a command to changed the sampling rate of the
 * accelerometer readings. The new rate is determined by the variable <CODE>data_1</CODE>.
 * @param COMMAND_STOP indicates a command to stop the accelerometer readings.
 */

static void tcpip_handler() {
//PROCESS_THREAD(tcpip_handler_process,ev,data) {
	//PROCESS_BEGIN();
	
	static uint8_t *type_ptr;
	static ack_payload_t ack_payload;
	static int c=0;
	if(uip_newdata()) {
		printf("\nReceived...");
		type_ptr=(uint8_t *)uip_appdata;
		if(type_ptr[0]==TYPE_CONTROL_PAYLOAD) {
			control_payload_ptr = (control_payload_t *)uip_appdata;
			//for debugging, print the received data
			printf("TYPE_CONTROL_PAYLOAD");
			printf("\ndata length = %d\nrx_bytes: ", uip_datalen());
			for(c=0;c<uip_datalen();c++) {
				printf("%d   ", type_ptr[c]);
			}
			printf("\ntype: %x, seq_no: %u\n", control_payload_ptr->type, control_payload_ptr->seq_num);
			printf("command %x, data_1: %u, data_2: %u, data_3: %u\n", control_payload_ptr->command, control_payload_ptr->data_1,
					control_payload_ptr->data_2, control_payload_ptr->data_3);
			
			//check the command
			if(control_payload_ptr->command==COMMAND_SET_RATE) {
				printf("\nCOMMAND_SET_RATE");
				//stop sampling, change the sampling rate, start sampling
				//process_exit(&tx_process);
				//check if fast sampling is required, i.e. the sampling rate is < 1000ms
				//if so, we buffer samples internally then send accel. readings every 1000ms
				sampling_rate_ms = control_payload_ptr->data_1;
				if(sampling_rate_ms<1000) {
					sampling_rate_ms = 1000;
					buffer_samples = 0;//1; //override not to do buffered sampling
				} else buffer_samples = 0;
				sampling_rate = (float)(sampling_rate_ms)/(float)1000;
				//process_start(&tx_process,NULL);
				printf("\nsampling_rate changed to %d ms", sampling_rate_ms);
				//assemble and send the ack packet
				ack_payload.type = ACK_PAYLOAD;
				ack_payload.command = COMMAND_SET_RATE;
				ack_payload.data_1 = sampling_rate_ms;
				uip_udp_packet_sendto(udp_conn, &ack_payload, sizeof(ack_payload_t), &pc_ipaddr, UIP_HTONS(UDP_REMOTE_PORT));
				printf("\nsent ack packet");
			}
			if(control_payload_ptr->command==COMMAND_STOP) {
				printf("\nCOMMAND_STOP");
					//assemble and send the ack packet
				ack_payload.type = ACK_PAYLOAD;
				ack_payload.command = COMMAND_STOP;
				uip_udp_packet_sendto(udp_conn, &ack_payload, sizeof(ack_payload_t), &pc_ipaddr, UIP_HTONS(UDP_REMOTE_PORT));
				printf("\nsent ack packet");
				leds_on(LEDS_RED);
				leds_off(LEDS_GREEN);
			}
			if(control_payload_ptr->command==COMMAND_START) {
				printf("\nCOMMAND_START");
				//process_start(&tx_process,NULL);
				//assemble and send the ack packet
				ack_payload.type = ACK_PAYLOAD;
				ack_payload.command = COMMAND_START;
				uip_udp_packet_sendto(udp_conn, &ack_payload, sizeof(ack_payload_t), &pc_ipaddr, UIP_HTONS(UDP_REMOTE_PORT));
				printf("\nsent ack packet");
				leds_on(LEDS_GREEN);
			}
		}
		else {
			printf("unknown type");
		}
	}
	
	//PROCESS_EXIT();
	//PROCESS_END();
}
//----------------------------------------------------------------------------
/**
 * @name tx_process()
 * 
 * @brief This process gets the accelerometer readings, forms a data packet,
 * and sends it to the Java application.
 * \n The sending is done every <CODE>sampling_rate</CODE> seconds.
 * @note Each accelerometer reading is composed of 3 values:
 * \n <CODE>acc_x_axis</CODE>, <CODE>acc_y_axis</CODE>, and <CODE>acc_z_axis</CODE>

PROCESS_THREAD(tx_process,ev,data) {
	PROCESS_BEGIN();

	while(1) {
		if(buffer_samples==1) {
			//FIXME

			data_payload_buffer.type = TYPE_DATA_PAYLOAD_BUFFER;
			data_payload_buffer.seq_num = ++data_seq_num;
			data_payload_buffer.temperature = (temperature_sensor.value(0))/10;
			printf("temperature %d",data_payload_buffer.temperature);
			data_payload_buffer.buf_len = 3;
			data_payload_buffer.sampling_rate = sampling_rate_ms;
			uip_udp_packet_sendto(udp_conn, &data_payload_buffer, sizeof(data_payload_buffer_t), &pc_ipaddr, UIP_HTONS(UDP_REMOTE_PORT));
			
			printf("\nTYPE_DATA_PAYLOAD_BUFFER, size %d", sizeof(data_payload_buffer));
		}
		else {
			//normal sampling, 1 sample per packet (no buffering samples)
			data_payload.type = TYPE_DATA_PAYLOAD;
			data_payload.seq_num = ++data_seq_num;
			data_payload.temperature = (temperature_sensor.value(0))/10;

			data_payload.sampling_rate = sampling_rate_ms;
			uip_udp_packet_sendto(udp_conn, &data_payload, sizeof(data_payload_t), &pc_ipaddr, UIP_HTONS(UDP_REMOTE_PORT));
			etimer_set(&wait_timer, sampling_rate * (float)CLOCK_SECOND);
			PROCESS_WAIT_UNTIL(etimer_expired(&wait_timer));
		}
	
		leds_toggle(LEDS_GREEN);
		printf("\nsend %d\n", data_seq_num);
	}
	PROCESS_EXIT();
	PROCESS_END();
}
 */

//----------------------------------------------------------------------------
static void print_temperature_reading(void) {
	int temp;
	temp = temperature_sensor.value(0);
	printf("\nTemp %u.%u degC", temp/10,temp-(temp/10)*10);
}
//----------------------------------------------------------------------------
/**
 * @name set_global_address()
 * 
 * @brief This function is executed when the node is powered on,
 * to set the node's IPv6 global address.
 */
static void set_global_address(void) {
	uip_ipaddr_t ipaddr;
	//struct uip_ds6_addr *root_if;
	
	uip_ip6addr(&ipaddr, 0xebb1, 0, 0, 0, 0, 0, 0, 0);
	uip_ds6_set_addr_iid(&ipaddr, &uip_lladdr);
	uip_ds6_addr_add(&ipaddr, 0, ADDR_AUTOCONF);
	
	uip_ip6addr(&ipaddr, 0xebb1, 0, 0, 0, 0, 0, 0, 3);
	uip_ds6_addr_add(&ipaddr, 0, ADDR_MANUAL);
	
}
//----------------------------------------------------------------------------
/**
 * @name print_local_addresses()
 * 
 * @brief This function is executed when the node is powered on,
 * to print the node's IPv6 addresses.
 */
static void print_local_addresses(void) {
	uint8_t i;
	uint8_t state;

	PRINTF("IPv6 addresses: ");
	for(i = 0; i < UIP_DS6_ADDR_NB; i++) {
		state = uip_ds6_if.addr_list[i].state;
		if (state == ADDR_TENTATIVE || state == ADDR_PREFERRED) {
			PRINT6ADDR(&uip_ds6_if.addr_list[i].ipaddr);
			PRINTF("\n");
			if (state == ADDR_TENTATIVE) {
				uip_ds6_if.addr_list[i].state = ADDR_PREFERRED;
			}
		}
	}
}
//------------------------------------------------------------------------------
/**
 * @name stm_sensors_process()
 * 
 * @brief This is the main application's process that manages the node's operations.
 */
PROCESS_THREAD(stm_sensors_process, ev, data)
{
	PROCESS_BEGIN();
	//stm32w_radio_set_channel(26);
	
	//int16_t aaa;
	//uint16_t bbb;
	//int ccc;
	
	NETSTACK_MAC.off(1);
	
	

	set_global_address();
	
	print_local_addresses();
	
	udp_conn = udp_new(NULL, UIP_HTONS(0), NULL);
	udp_bind(udp_conn, UIP_HTONS(UDP_LOCAL_PORT));

	//uip_ip6addr(&bcast_ipaddr, 0xff02, 0, 0, 0, 0, 0, 0, 1);
	uip_ip6addr(&pc_ipaddr, 0xebb1, 0, 0, 0, 0, 0, 0, 1);
	
	leds_on(LEDS_RED);
	
	//printf("\nsize of int16_t = %d",sizeof(aaa));
	//printf("\nsize of uint16_t = %d",sizeof(bbb));
	//printf("\nsize of int = %d",sizeof(ccc));
	
	sampling_rate_ms = (uint16_t)sampling_rate*1000;
	
	//wait...
	etimer_set(&wait_timer, 10 * CLOCK_SECOND);
	PROCESS_WAIT_UNTIL(etimer_expired(&wait_timer));
	leds_off(LEDS_RED);
	
	//etimer_set(&my_timer, 2*CLOCK_SECOND);
	//process_start(&tx_process,NULL);

	while(1) {
		PROCESS_YIELD();
		if(ev == tcpip_event) {
		 // tcpip_handler();
		  //process_start(&tcpip_handler_process,NULL);
		printf("hi");
		}
	}

	PROCESS_END();
}



