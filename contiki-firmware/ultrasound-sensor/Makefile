CONTIKI_PROJECT = ultrasound-sensor

PORT=/dev/ttyACM0

#Target and CPUREV mentioned in Makefile (prabha)
TARGET=mbxxx
#STM32W_CPUREV=CC
STM32W_CPUREV=xB

all: $(CONTIKI_PROJECT)

CONTIKI = ../../..
include $(CONTIKI)/Makefile.include

myflash: all
	@echo "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ FLASHING"
	@echo "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ FLASHING"	
	@echo "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ FLASHING"	
	arm-none-eabi-objcopy -O binary $(CONTIKI_PROJECT).mbxxx $(CONTIKI_PROJECT).bin

	sudo -s $(FLASHER) -f -r -p $(PORT) $(CONTIKI_PROJECT).bin
mydump:
	$(CONTIKI)/tools/stm32w/serialdump-linux -b115200 $(PORT)
