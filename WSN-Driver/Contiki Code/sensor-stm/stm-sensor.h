/*
 * stm-sensor.h
 *
 *  Created on: Feb 28, 2014
 *      Author: "Prabhakaran Kasinathan"
 */

#ifndef STM_SENSOR_H_
#define STM_SENSOR_H_


//Timers
#include "sys/ctimer.h"

// uIP implementation
#include "net/uip.h"
#include "net/uip-ds6.h"
#include "net/uip-udp-packet.h"

// Includes for Sensors
#include "dev/leds.h"
#include "dev/button-sensor.h"
#include "dev/acc-sensor.c"
#include "dev/temperature-sensor.h"
#include "board.h"

//Includes for DEBUG PRINT
#define DEBUG DEBUG_PRINT
#include "net/uip-debug.h"

// Needed?
#include <stdio.h>
#include <string.h>


// Define
#define MAX_PAYLOAD_LENGTH 100 // Check what could be the max to avoid fragmentation

//Request Commands
#define REQ_DISC		0xB0
#define REQ_SENSORS 	0xB1
#define REQ_LEDS		0xB2
#define REQ_DATA		0xDD

#define ACK_PAYLOAD 	0xA1
#define ACK_DISC		0xA0

// TYPE Definition
#define LED_ALL			0xD0

#define LED_RED			0xD1
#define LED_GREEN		0xD2

#ifdef SKY_NODE
#define LED_BLUE		0xD3
#endif

// Sensor definitions
#define SENSOR_ALL		0xC0

#define SENSOR_TEMP		0xC1
#define SENSOR_ACCEL	0xC2

#ifdef SKY_NODE
#define SENSOR_LIGHT	0xC3
#define SENSOR_HUMIDITY	0xC4
#endif


/*---------------------------------------------------------------------------*/
// Process Received payload from PC
typedef struct _received_payload_t {
	uint8_t type;
	uint8_t seq_num;
	uint8_t sensor;
} received_payload_t;

/*
 * Structure to divide bytes
 */
typedef union _b16_t{
	int value;
	uint8_t byte[1]; // short
}b16_t;

typedef union _b32_t{
	int value;
	uint8_t byte[3]; //int,float,long,
} b32_t;

typedef union _b64_t{
	int value;
	uint8_t byte[7]; //double
} b64_t;

/*---------------------------------------------------------------------------*/
/**
 * @struct _data_payload_t
 * @brief The data payload structure containing any kind of data replied.
 */
typedef struct _payload_data_t {
	uint8_t *type;
	uint8_t *pay_len;
	uint8_t *data[MAX_PAYLOAD_LENGTH];
} payload_data_t;

/*
 * Ack Payload Struct
 */
typedef struct _ack_payload_t {
	uint8_t  type;
	uint8_t seq_num;
} ack_payload_t;


#endif /* STM_SENSOR_H_ */
