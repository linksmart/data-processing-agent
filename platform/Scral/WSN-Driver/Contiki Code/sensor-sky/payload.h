/**
 *@file payload.h
 *@brief This file contains payload structures used in the application.
 *
 *@author Hussein KHALEEL <khaleel@ismb.it>
 */

/**
 * @define TYPE_DATA_PAYLOAD
 * @brief The data payload identifier. Using the value of this macro,
 * a recipient determines that the received payload contains accelerometer readings.
 */
#define TYPE_DATA_PAYLOAD 0xD0

/**
 * @struct _data_payload_t
 * @brief The payload structure containing the accelerometer readings.
 */
typedef struct _data_payload_t {
	uint8_t type; 
	uint16_t seq_num;
	int   temperature;
	int   acc_x_axis;
	int   acc_y_axis;
	int   acc_z_axis;
	uint16_t sampling_rate;
} data_payload_t;


#define TYPE_DATA_PAYLOAD_BUFFER 0xD1
typedef struct _data_payload_buffer_t {
	uint8_t type; 
	uint16_t seq_num;
	int temperature;
	int buf_len;
	int acc_x_axis[3];
	int acc_y_axis[3];
	int acc_z_axis[3];
	uint16_t sampling_rate;
} data_payload_buffer_t;




/**
 * @define TYPE_CONTROL_PAYLOAD
 * @brief The payload identifier. Using the value of this macro,
 * a recipient determines that the received payload contains control commands.
 */
#define TYPE_CONTROL_PAYLOAD 0xC0

/**
 * @define COMMAND_SET_RATE
 * @brief The command identifier macro. Indicates a commands to control the sampling rate.
 */
#define COMMAND_SET_RATE 0xC1

/**
 * @define COMMAND_STOP
 * @brief The command identifier macro. Indicates a commands to stop the accelerometer readings.
 */
#define COMMAND_STOP 0xC2

/**
 * @define COMMAND_START
 * @brief The command identifier macro. Indicates a commands to start the accelerometer readings.
 */
#define COMMAND_START 0xC4

//#define COMMAND_GET_RATE 0xC3

/**
 * @struct _control_payload_t
 * @brief The payload structure of the control commands.
 */
typedef struct _control_payload_t {
	uint16_t type;
	uint16_t seq_num;
	uint16_t command;
	uint16_t data_1;
	uint16_t data_2;
	uint16_t data_3;
} control_payload_t;


/**
 * @define ACK_PAYLOAD
 * @brief Identifier of an acknowledgement packet from the sensor node to the server.
 */
#define ACK_PAYLOAD 0xA0

/**
 * @struct _ack_payload_t
 * @brief The payload structure of the acknowledgemnt packet.
 * This packet is sent as a response to a server's control command to confirm the successful
 * execution of the control command.
 */
typedef struct _ack_payload_t {
	uint8_t type;
	uint16_t command;
	uint16_t data_1;
} ack_payload_t;









