CONTIKI_PROJECT=border-router
all: $(CONTIKI_PROJECT) 

#PORT=/dev/ttyUSB0
PORT=/dev/ttyACM0

# STM32 = mbxxx / Telosb = sky
TARGET = mbxxx

# if STM32 check CPUREV : CC / xB
STM32W_CPUREV=xB

CONTIKI=../../..
#CONTIKI=/home/hussein/contiki-2.6/
#CONTIKI=/home/hussein/contiki-2.6-ST/Contiki2_6

WITH_UIP6=1
UIP_CONF_IPV6=1
CFLAGS+= -DUIP_CONF_IPV6_RPL

#linker optimizations
SMALL=1

CFLAGS += -DPROJECT_CONF_H=\"project-conf.h\"
PROJECT_SOURCEFILES += slip-bridge.c

#Simple built-in webserver is the default.
#Override with make WITH_WEBSERVER=0 for no webserver.
#WITH_WEBSERVER=webserver-name will use /apps/webserver-name if it can be
#found in the /apps, /platform/$(TARGET)/apps/, or current directory (in that order).
# WITH_WEBSERVER=webserver for /apps/webserver
# WITH_WEBSERVER=raven-webserver for /platform/avr-raven/apps/raven-webserver/
#make clean before changing webservers!

#Note /apps/webserver contains a 2500 byte style sheet which is a severe test
#of the slip connection. Large MSS together with low baud rates without flow
#control will overrun the transmit buffer when the style sheet is requested.

WITH_WEBSERVER=1
ifeq ($(WITH_WEBSERVER),1)
CFLAGS += -DWEBSERVER=1
PROJECT_SOURCEFILES += httpd-simple.c
else ifneq ($(WITH_WEBSERVER), 0)
APPS += $(WITH_WEBSERVER)
CFLAGS += -DWEBSERVER=2
endif

ifeq ($(PREFIX),)
 PREFIX = ebb1::1/64
endif

include $(CONTIKI)/Makefile.include

$(CONTIKI)/tools/tunslip6: $(CONTIKI)/tools/tunslip6.c
	(cd $(CONTIKI)/tools && $(MAKE) tunslip6)

connect-router:	$(CONTIKI)/tools/tunslip6
	sudo $(CONTIKI)/tools/tunslip6 -s $(PORT) $(PREFIX)

connect-router-cooja:	$(CONTIKI)/tools/tunslip6
	sudo $(CONTIKI)/tools/tunslip6 -a 127.0.0.1 $(PREFIX)


myflash: all
	arm-none-eabi-objcopy -O binary $(CONTIKI_PROJECT).mbxxx $(CONTIKI_PROJECT).bin
	sudo $(CONTIKI)/tools/stm32w/stm32w_flasher/py_files/stm32w_flasher.py -f -r -p $(PORT) $(CONTIKI_PROJECT).bin
	#sudo $(CONTIKI)/tools/stm32w/stm32w_flasher/stm32w_flasher -f -r -p $(PORT) $(CONTIKI_PROJECT).bin
	#sudo $(CONTIKI)/tools/stm32w_original/stm32w_flasher/stm32w_flasher -f -r -p $(PORT) $(CONTIKI_PROJECT).bin


mydump:
	$(CONTIKI)/tools/stm32w/serialdump-linux -b115200 $(PORT)

