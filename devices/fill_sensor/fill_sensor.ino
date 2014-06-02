// ---------------------------------------------------------------------------
// Example NewPing library sketch that does a ping about 20 times per second.
// ---------------------------------------------------------------------------


#include <NewPing.h>

#define TRIGGER_PIN  9 // Arduino pin tied to trigger pin on the ultrasonic sensor.
#define ECHO_PIN     10  // Arduino pin tied to echo pin on the ultrasonic sensor.
#define MAX_DISTANCE 400 // Maximum distance we want to ping for (in centimeters). Maximum sensor distance is rated at 400-500cm.

#define CALIBRATE_PIN 12
#define BUTTON_PRESSED LOW
const int debounceDelay = 50;

const int fullnessReportPeriod = 200; //every how many milliseconds should we check fullness
byte calibrationButtonState = HIGH;
byte lastCalibrationButtonState = HIGH;
long lastCalibrationButtonChange = millis();
boolean calibrationDone = false;
int containerDepth = 0;
boolean firstCalibrationAlreadyRequested = false; //this is so we do calibration only after user has requested it for first time
long lastFullnessMeasurement = millis();
int lastFullness = -1;

NewPing sonar(TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE); // NewPing setup of pins and maximum distance.





// Arduino 7 segment display example software
// http://www.hacktronics.com/Tutorials/arduino-and-7-segment-led.html
// License: http://www.opensource.org/licenses/mit-license.php (Go crazy)
 
// Define the LED digit patters, from 0 - 9
// Note that these patterns are for common cathode displays
// For common anode displays, change the 1's to 0's and 0's to 1's
// 1 = LED on, 0 = LED off, in this order:
//THE LAST SEGMENT IS THE DOT
#define NUM_SEGMENTS 8
#define NUM_DIGITS 10
#define SEGMENT_ON HIGH
#define SEGMENT_OFF LOW
byte seven_seg_digits[NUM_DIGITS][NUM_SEGMENTS] = { 
                               { 1,1,1,1,1,1,0,0 },  // = 0
                               { 0,1,1,0,0,0,0,0 },  // = 1
                               { 1,1,0,1,1,0,1,0 },  // = 2
                               { 1,1,1,1,0,0,1,0 },  // = 3
                               { 0,1,1,0,0,1,1,0 },  // = 4
                               { 1,0,1,1,0,1,1,0 },  // = 5
                               { 1,0,1,1,1,1,1,0 },  // = 6
                               { 1,1,1,0,0,0,0,0 },  // = 7
                               { 1,1,1,1,1,1,1,0 },  // = 8
                               { 1,1,1,0,0,1,1,0 }   // = 9
                               };

byte pinForSegment[NUM_SEGMENTS] = {A1, A0, 5, 3, 2, A3, A4, 6};


void setup() {
  Serial.begin(9600); // Open serial monitor at 115200 baud to see ping results.

//turn on display for testing
  for (byte i = 0; i < NUM_SEGMENTS; i++) { 
    pinMode(pinForSegment[i], OUTPUT);
    digitalWrite(pinForSegment[i], SEGMENT_ON);
  }
  delay(1000);

  for (byte i = 0; i < NUM_DIGITS; i++) {
   writeDigit(i); 
   delay(300);
  }

  //turn it off now
  for (byte i = 0; i < NUM_SEGMENTS; i++) { 
    digitalWrite(pinForSegment[i], SEGMENT_OFF);
  }

  pinMode(CALIBRATE_PIN, INPUT);
  digitalWrite(CALIBRATE_PIN, HIGH);
  lastCalibrationButtonChange = millis();

  //turn the dot on to mark need for calibration
  writeDot(SEGMENT_ON);  

 // Serial.println("ready");

}

void loop() {
    byte calibrationButtonReading = digitalRead(CALIBRATE_PIN);

    // check to see if you just pressed the button 
    // (i.e. the input went from LOW to HIGH),  and you've waited 
    // long enough since the last press to ignore any noise:  
  
    // If the switch changed, due to noise or pressing:
    if (calibrationButtonReading != lastCalibrationButtonState) {
      // reset the debouncing timer
      lastCalibrationButtonChange = millis();
      lastCalibrationButtonState = calibrationButtonReading;
    } 
    
    if ((millis() - lastCalibrationButtonChange) > debounceDelay) {
      // whatever the reading is at, it's been there for longer
      // than the debounce delay, so take it as the actual current state:
      // if the button state has changed:
      if (calibrationButtonReading != calibrationButtonState) {
        calibrationButtonState = calibrationButtonReading;
        if (calibrationButtonState == BUTTON_PRESSED) {
          firstCalibrationAlreadyRequested = true;
          calibrationDone = false;
        }
      }
    }

    if (!calibrationDone) {
      if (firstCalibrationAlreadyRequested) { //otherwise we don't do it until user presses button
        writeDot(SEGMENT_ON);
        containerDepth = sonar.convert_cm(sonar.ping_median(11));
        if (containerDepth > 0) {
          calibrationDone = true;
//          Serial.println(containerDepth);
          writeDot(SEGMENT_OFF);
        }
      }
    }

    if (calibrationDone && (millis() - lastFullnessMeasurement) > fullnessReportPeriod) { //measurement cycle
      int distanceToTop = sonar.convert_cm(sonar.ping_median(5));
      distanceToTop = constrain(distanceToTop, 0, containerDepth);
      int fullness = containerDepth - distanceToTop;
      if (fullness != lastFullness) {
        lastFullness = fullness; 
        Serial.print("idFillLevelSensor {\"type\":\"ac:FillLevelSensor\","); Serial.print("\"depth\":\""); Serial.print(containerDepth); Serial.print("\",\"level\":\""); Serial.print(fullness); Serial.println("\"}");
      }
      fullness = map(fullness, 0, containerDepth * 0.9, 0, 9);//strictly speaking this x0.9 is wrong, but do this because sensor has a minimum range

      writeDigit(fullness);
      lastFullnessMeasurement = millis();
    }

}


void writeDot(byte dotState) {
  digitalWrite(pinForSegment[NUM_SEGMENTS - 1], dotState);
}
    
void writeDigit(byte digit) {
  for (byte segment = 0; segment < NUM_SEGMENTS; ++segment) {
    digitalWrite(pinForSegment[segment], seven_seg_digits[digit][segment]);
  }
}
