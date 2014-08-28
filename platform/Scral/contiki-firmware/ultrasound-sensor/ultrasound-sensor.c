/*
 * Copyright (c) 2010, ISMB, Pervasive Technologies
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * This file is part of the Contiki operating system.
 *
 */

/**
 * \file
 *         A very simple Contiki application showing how to use the Ultrasound
 *			sensor.
 * \author
 *         Prabhakaran Kasinathan <kasinathan@ismb.it>
 *
 */

#include "contiki.h"

#include "board.h"

#include <stdio.h> /* For printf() */

//----------------------------------------------------------------------------
#define DEBUG DEBUG_PRINT //DEBUG_NONE DEBUG_PRINT
#include "net/uip-debug.h"
//----------------------------------------------------------------------------
//Include ADC to read GPIO values
#include "hal/error.h"
#include "hal/hal.h"
#include "hal/micro/cortexm3/stm32w108/regs.h"
#include "hal/micro/adc.h"

/*
 * Function: Get inch value from Ultrasound sensor and return Temp in *c
 * ADC channel: ADC1
 * GPIO PIN: PortB(21)
 */

unsigned int get_ultrasound_ext(void){
	 static uint16_t ADCvalue;
	 static int16_t raw_volt_value; // ADC's Raw Volt Value in 16 unsigned bits
	 static int16_t VI=5859; //http://www.maxbotix.com/articles/016.htm [it is read as 3 volt(Vcc/512)=VI] 0.005859
	 static int16_t volt_calibrated;
	 static int16_t RI;
	 static int16_t RI_decimal;

	 /*
	  * The following function is responsible to intialize proper registers in STM32
	  */

	 halStartAdcConversion(ADC_USER_APP2,ADC_REF_INT,ADC_SOURCE_ADC1_VREF2,ADC_CONVERSION_TIME_US_4096);
	 halReadAdcBlocking(ADC_USER_APP2, &ADCvalue);

	 // Add offset if needed: to calibrate the temperature: halConvertValueToVolts(ADCvalue)+SUPPLYOFFSET
	 raw_volt_value = halConvertValueToVolts(ADCvalue);
	 volt_calibrated = ((raw_volt_value+100)); //100 is offset
	 RI = (volt_calibrated*1000)/ VI ; // [(Vm/VI)=RI]
	 //printf("%d.%d",RI/10,RI-(RI/10)*10);
	 return RI;

}
/*---------------------------------------------------------------------------*/
PROCESS(ultra_process, "ultrasound process");
AUTOSTART_PROCESSES(&ultra_process);
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(ultra_process, ev, data)
{
  static struct etimer etimer;
  
  PROCESS_BEGIN();
  //Init HAL
  halInternalInitAdc();
  halAdcSetRange(TRUE);
  //PB6 to Analog mode
  halGpioConfig(PORTB_PIN(6), GPIOCFG_ANALOG);
  //Calibrate
  halAdcCalibrate(ADC_USER_APP2);
	
  boardPrintStringDescription();
  printf("Starting measuring ultrasound sensor\r\n");

  while(1) {
    etimer_set(&etimer, CLOCK_SECOND);
    
    PROCESS_WAIT_UNTIL(etimer_expired(&etimer));
    
    unsigned int RI = get_ultrasound_ext();
    printf("%d.%d",RI/10,RI-(RI/10)*10);
  }
  
  
  PROCESS_END();
}
/*---------------------------------------------------------------------------*/

