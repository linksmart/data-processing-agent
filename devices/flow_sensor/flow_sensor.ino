#include <math.h>

// Pins for Arduino motor shield
#define DIR_A 12
#define PWM_A 3
#define BRK_A 9
#define DIR_B 13
#define PWM_B 11
#define BRK_B 8
// Pin for thermistor
#define THERMISTOR_PIN_ANALOG 5

// Pin for water flow sensor in uno or duemilanove
#define HALL_SENSOR_PIN_DIGITAL 2
// Pin for water flow sensor in leonardo, with interrupt 1
//#define HALL_SENSOR_PIN_DIGITAL 1



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

// variable for counting the pulses from the hall sensor (water flow)
volatile int pulses;

// This function is called on interrupt from the hall sensor (water flow) 
void rpm () 
{ 
  pulses++;         
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

// calculates the temperature in degrees Celsius from the analog input value
double temperature(int rawADC) {
  double temp;
  temp = log(((10240000/rawADC) - 10000));
  temp = 1 / (0.001129148 + (0.000234125 + (0.0000000876741 * temp * temp)) * temp);
  temp = temp - 273.15; // Convert Kelvin to Celcius
  return temp;
}

// parse a byte incoming from the serial port
// implements a state machine to reflect the protocol and calls action()
// once a complete message has been received
void parseInput(int in) {
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
    id = in - '0';
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
    value = 10 * value + (in - '0'); 
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
double calc;
// initialize pins and state
void setup()
{
  // set up water flow sensor
  pinMode(HALL_SENSOR_PIN_DIGITAL, INPUT); // initializes digital pin 2 as an input
  attachInterrupt(0, rpm, RISING);         // and the interrupt is attached. N.B. Leonardo interrupt 1 is in pin 2, duemilanove is interrupt zero

  // set up motor shield
  pinMode(DIR_A, OUTPUT);   // sets the pin as output
  pinMode(PWM_A, OUTPUT);   // sets the pin as output
  pinMode(BRK_A, OUTPUT);   // sets the pin as output
  digitalWrite(DIR_A, HIGH);
  /*
  pinMode(DIR_B, OUTPUT);   // sets the pin as output
  pinMode(PWM_B, OUTPUT);   // sets the pin as output
  pinMode(BRK_B, OUTPUT);   // sets the pin as output
  digitalWrite(DIR_B, HIGH);*/
  calc =0;
  Serial.begin(9600);     // opens serial port, sets data rate to 9600 bps
  analogWrite(PWM_A, 255);
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
  
  // Sense water flow
  pulses = 0;   // Set pulses to 0 ready for new measurement
  sei();        // Enable interrupts
  delay (1000); // Wait 1 second
  cli();        // Disable interrupts
   calc += (pulses / (8.2)*10); // (Pulse frequency x 60) / 8.2 = flow rate in L/min 
  pulses=0;
  String data = "";
  if(calc >=32748 or calc<0)
    calc=0.0;
  data = String((int)calc);
  //sendValue("f1",data);
  Serial.print("idFlowSensor {\"type\":\"ac:FlowSensor\","); Serial.print("\"flow\":\""); Serial.print(data); Serial.println("\"}");
/*
  // Sense temperature
  int therm;   
  therm=analogRead(THERMISTOR_PIN_ANALOG);
  double celsius = temperature(therm);
  data = "";
  data += (int) celsius;
  data += ".";
  celsius = (celsius - (int) celsius) * 10;
  data += (int) celsius;
  //sendValue("t1",data);
*/
}

// Actuator function. Implements the action conditionally on the key and value received.
// Currently only supporting one pump
void action() {
  if (key == 'p' && id == 1) {
    analogWrite(PWM_A, value*2);
  }
}
