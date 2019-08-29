select 
	Tools.CreateIoTEntity(binA.about, 'levelAlert' ,true),
	Tools.CreateIoTEntity(binB.about, 'levelAlert' ,true),
	Tools.CreateIoTEntity(binC.about, 'levelAlert' ,true),
	Tools.CreateIoTEntity(binD.about, 'levelAlert' ,true),
	true as SetEventPerEntity
from topics
		{/almanac/observation/iotentity/#}(about = '_a2ae2f57_1362_3339_a7e4_d29799029cc5' and properties[1].about like '%FillLevelSensor:getLevel%').std:lastevent() as binA,
		{/almanac/observation/iotentity/#}(about = '_239d094b_7d77_3f32_8f43_7079a5634af3' and properties[0].about like '%fillLevel:SimpleFillLevelSensor:getLevel%').std:lastevent() as binB,
		{/almanac/observation/iotentity/#}(about = '_21cec1c6_3b8e_3d84_901e_3aaae1655c4b' and properties[0].about like '%fillLevel:SimpleFillLevelSensor:getLevel%').std:lastevent() as binC,
		{/almanac/observation/iotentity/#}(about = '_2ee611f1_a0f5_3f59_8aa7_a7ccaf7409a4' and properties[0].about like '%fillLevel:SimpleFillLevelSensor:getLevel%').std:lastevent() as binD
		
where 
		cast(binA.properties[1].ioTStateObservation[0].value,double) >10.0 and
		cast(binB.properties[0].ioTStateObservation[0].value,double) >80.0 and
		cast(binC.properties[0].ioTStateObservation[0].value,double) >80.0 and
		cast(binD.properties[0].ioTStateObservation[0].value,double) >80.0