select window(*) from topics {/almanac/observation/iotentity/#}.win:time(10 sec).std:unique(about,properties[0].ioTStateObservation[0].value)

