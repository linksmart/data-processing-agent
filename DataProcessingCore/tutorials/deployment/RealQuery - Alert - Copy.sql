select 
	binB.about as binBID,
	binC.about as binCID,
	binD.about as binDID,
	thermometer.about as thermometerID,
	toyBin.about as toyBinID
from topics
		{/almanac/observation/iotentity/_a2ae2f57_1362_3339_a7e4_d29799029cc5}.std:lastevent() as toyBin,
		{/almanac/observation/iotentity/_89319db8_9d3f_315c_a4c6_9e2132ae6a44}.std:lastevent() as binB,
		{/almanac/observation/iotentity/_a1be5ba7_bb8a_3b69_b376_06d89fbecee6}.std:lastevent() as binC,
		{/almanac/observation/iotentity/_597438b1_a750_3832_9205_57ede636ba67}.std:lastevent() as binD,
		{/almanac/observation/iotentity/_8e298178_bccf_390b_b612_8d5d5d990e40}.std:lastevent() as thermometer 
where 
		cast(toyBin.properties[0].ioTStateObservation[0].value,double) > 2.0 and 
		cast(thermometer.properties[0].ioTStateObservation[0].value,double) > 30.0

