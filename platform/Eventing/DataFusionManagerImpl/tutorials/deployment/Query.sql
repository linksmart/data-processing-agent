select 
	Tools.CreateIoTEntity(binA.about, 'levelAlertA' ,true),
	Tools.CreateIoTEntity(binB.about, 'levelAlertB' ,true),
	Tools.CreateIoTEntity(binC.about, 'levelAlertC' ,true),
	Tools.CreateIoTEntity(binD.about, 'levelAlertD' ,true),
	true as SetEventPerEntity
from topics
		{/almanac/observation/iotentity/#}(about = '_5fb87cdf_24d2_3f96_8620_f108de542c2f_fake' and properties[0].about like '%fillLevel%').std:lastevent() as binA,
		{/almanac/observation/iotentity/#}(about = '_239d094b_7d77_3f32_8f43_7079a5634af3_fake' and properties[0].about like '%fillLevel%').std:lastevent() as binB,
		{/almanac/observation/iotentity/#}(about = '_21cec1c6_3b8e_3d84_901e_3aaae1655c4b_fake' and properties[0].about like '%fillLevel%').std:lastevent() as binC,
		{/almanac/observation/iotentity/#}(about = '_2ee611f1_a0f5_3f59_8aa7_a7ccaf7409a4_fake' and properties[0].about like '%fillLevel%').std:lastevent() as binD
		
where 
		cast(binA.properties[0].ioTStateObservation[0].value,double) >80.0 and
		cast(binB.properties[0].ioTStateObservation[0].value,double) >80.0 and
		cast(binC.properties[0].ioTStateObservation[0].value,double) >80.0 and
		cast(binD.properties[0].ioTStateObservation[0].value,double) >80.0
		