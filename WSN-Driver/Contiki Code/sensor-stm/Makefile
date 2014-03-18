#MAKE FILE FOR STM
CONTIKI_PROJECT = stm-sensor

PORT=/dev/ttyACM1
#MOTES=/dev/ttyACM1

#Target and CPUREV mentioned in Makefile (prabha)
TARGET=mbxxx
#STM32W_CPUREV=CC
STM32W_CPUREV=xB

all: $(CONTIKI_PROJECT)

WITH_UIP6=1
UIP_CONF_IPV6=1
CFLAGS+= -DUIP_CONF_IPV6_RPL


CONTIKI = ../../..
include $(CONTIKI)/Makefile.include


CFLAGS += -DPROJECT_CONF_H=\"project-conf.h\"

myflash: all
	arm-none-eabi-objcopy -O binary $(CONTIKI_PROJECT).mbxxx $(CONTIKI_PROJECT).bin
	sudo $(CONTIKI)/tools/stm32w/stm32w_flasher/py_files/stm32w_flasher.py -f -r -p $(PORT) $(CONTIKI_PROJECT).bin

mydump:
	$(CONTIKI)/tools/stm32w/serialdump-linux -b115200 $(PORT)
