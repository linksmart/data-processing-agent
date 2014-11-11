select 
	binB.about as binBID,
	binC.about as binCID,
	binD.about as binDID,
	thermometer.about as thermometerID,
	toyBin.about as toyBinID
from topics
		{/almanac/observation/iotentity/_a2ae2f57_1362_3339_a7e4_d29799029cc5}(cast(properties[0].ioTStateObservation[0].value,double) >10.0).std:lastevent() as toyBin,
		{/almanac/observation/iotentity/_239d094b_7d77_3f32_8f43_7079a5634af3}(cast(properties[0].ioTStateObservation[0].value,double) >80.0).std:lastevent() as binB,
		{/almanac/observation/iotentity/_21cec1c6_3b8e_3d84_901e_3aaae1655c4b}(cast(properties[0].ioTStateObservation[0].value,double) >80.0).std:lastevent() as binC,
		{/almanac/observation/iotentity/_2ee611f1_a0f5_3f59_8aa7_a7ccaf7409a4}(cast(properties[0].ioTStateObservation[0].value,double) >80.0).std:lastevent() as binD,
		{/almanac/observation/iotentity/_53ce16a8_6f26_38c4_8c29_ffa551ee42a9}(cast(properties[0].ioTStateObservation[0].value,double) >= 30.0).std:lastevent() as thermometer 