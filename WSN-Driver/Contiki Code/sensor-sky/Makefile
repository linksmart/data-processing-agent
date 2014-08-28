CONTIKI_PROJECT=telosb_sensors
all: $(CONTIKI_PROJECT)

MOTES=/dev/ttyUSB0


# STM32 = mbxxx / Telosb = sky
TARGET = sky


WITH_UIP6=1
UIP_CONF_IPV6=1
CFLAGS+= -DUIP_CONF_IPV6_RPL

CONTIKI=../../..

include $(CONTIKI)/Makefile.include

CFLAGS += -DPROJECT_CONF_H=\"project-conf.h\"
