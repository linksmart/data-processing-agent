#include <math.h>

// Command to send to change pump speed is: *p1=<num>$x#

// Pins for Arduino motor shield
#define PWM_A 9 //used to send PWM command to the water pump
#define PUMP_FB 0 //used to receive speed feedback from the water pump. corresponds to the pin 2
#define FLOW_METER_FB 1 //used to receive the flow value from the flow meter. corresponds to the pin3

// State definition for the serial protocol parser (state machine)
#define STATE_UNDEF 0
#define STATE_BEGIN 1
#define STATE_KEY 2
#define STATE_ID 3
#define STATE_SEP 4
#define STATE_VALUE 5
#define STATE_CHECKSUM 6

int state = STATE_UNDEF;
char key;  
int id;
int value;
boolean checksumOk;
int incomingByte = 0;   // for incoming serial data

volatile int wp_pulses = 0; //water pump pulses counter
volatile int flowMeter_pulses = 0; //flow meter pulses counter

// This function is called on interrupt from the water pump sensor
void waterPumpPulse() 
{ 
  wp_pulses++;         
} 


// This function is called on interrupt from the flow meter sensor
void flowMeterPulse() 
{ 
  flowMeter_pulses++;         
} 

// Calculates the checksum for a given String
char checksum(String data) {
  char checksum = '$';
  for (int i=0;i<data.length();i++) {
    checksum = checksum ^ data[i];
  }
  return checksum;
}

// Sends a key-value pair to the Serial port according to the protocol
void sendValue(String key, String value) {
  String data = key + "=" + value;
  Serial.println("*" + data + '$' + checksum(data) + "#"); 
}

// parse a byte incoming from the serial port
// implements a state machine to reflect the protocol and calls action()
// once a complete message has been received
void parseInput(char in) {
  if(in == '*') {
    state = STATE_BEGIN;
    value = 0;
    checksumOk = false;
    return;
  }
  if (state == STATE_BEGIN) {
    key = (char) in;
    state = STATE_KEY;
    return;
  }
  if (state == STATE_KEY) {
    id = (int) (in - '0');
    state = STATE_ID; 
    return;
  }
  if (state == STATE_ID && in == '=') {
    state = STATE_SEP;
    return;
  }
  if (state == STATE_SEP && in == '$') {
    state = STATE_VALUE;
    return;
  }
  if (state == STATE_SEP) {
    value = 10 * value + (int)(in - '0'); 
    return;
  }
  if (state == STATE_VALUE) {
    // here in must be checked for checksum
    checksumOk = true;
    state = STATE_CHECKSUM;
    return;
  }
  if (state == STATE_CHECKSUM && checksumOk && in == '#') {
    action();
    state = STATE_UNDEF;
  }
}

// initialize pins and state
void setup()
{ 
  //set pwm_frequency
  setPwmFrequency(9,1);
  
  //starts at 100%
  analogWrite(PWM_A, 255);
  
  //delay
  delay(1000);
  
  analogWrite(PWM_A, 0);
  
  pinMode(PUMP_FB, INPUT_PULLUP);
  attachInterrupt(PUMP_FB, waterPumpPulse, FALLING); //listening interrupts
  
  pinMode(FLOW_METER_FB, INPUT_PULLUP);
  attachInterrupt(FLOW_METER_FB, flowMeterPulse, FALLING); //listening interrupts  
  Serial.begin(9600);     // opens serial port, sets data rate to 9600 bps
}

// The main function. First reads incoming data from the serial port, 
// then measures the water flow and temperature and sends all sensor values  
void loop()
{
  // Read incoming data
  while (Serial.available() > 0) {
    // read the incoming byte:
    incomingByte = Serial.read();
    parseInput(incomingByte);
  }
  
  wp_pulses = 0;
  flowMeter_pulses = 0;
  sei();
  delay(1000);
  cli();
  double flow = (flowMeter_pulses * 60 / 7.5);    //(Pulse frequency x 60) / 7.5Q = flow rate in L/hou
  String flow_string = "";
  flow_string += (int)flow;
  
  String wp_speed_string = "";
  wp_speed_string += wp_pulses;
  Serial.print("idFlowSensor {\"type\":\"ac:FlowSensor\","); Serial.print("\"flow\":\""); Serial.print(flow_string); Serial.println("\"}");
  //Serial.print(wp_speed_string);
}

// Actuator function. Implements the action conditionally on the key and value received.
// Currently only supporting one pump
void action() {
  if (key == 'p' && id == 1) {
    int pwm_value = value;
    if(pwm_value > 255)
      pwm_value = 255;
    else if(pwm_value <= 10)
      pwm_value = 0;
    analogWrite(PWM_A, pwm_value);

  }
}

void setPwmFrequency(int pin, int divisor) {
  byte mode;
  if(pin == 5 || pin == 6 || pin == 9 || pin == 10) {
    switch(divisor) {
    case 1: 
      mode = 0x01; 
      break;
    case 8: 
      mode = 0x02; 
      break;
    case 64: 
      mode = 0x03; 
      break;
    case 256: 
      mode = 0x04; 
      break;
    case 1024: 
      mode = 0x05; 
      break;
    default: 
      return;
    }
    if(pin == 5 || pin == 6) {
      TCCR0B = TCCR0B & 0b11111000 | mode;
    } 
    else {
      TCCR1B = TCCR1B & 0b11111000 | mode;
    }
  } 
  else if(pin == 3 || pin == 11) {
    switch(divisor) {
    case 1: 
      mode = 0x01; 
      break;
    case 8: 
      mode = 0x02; 
      break;
    case 32: 
      mode = 0x03; 
      break;
    case 64: 
      mode = 0x04; 
      break;
    case 128: 
      mode = 0x05; 
      break;
    case 256: 
      mode = 0x06; 
      break;
    case 1024: 
      mode = 0x7; 
      break;
    default: 
      return;
    }
    TCCR2B = TCCR2B & 0b11111000 | mode;
  }
}

