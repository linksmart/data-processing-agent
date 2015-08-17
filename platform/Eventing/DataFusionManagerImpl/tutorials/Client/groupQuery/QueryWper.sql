select 
	about,
	window(*).allOf(e => cast(e.properties[0].ioTStateObservation[0].value,double) > 80.0)
from topics
		{/almanac/observation/iotentity/#}(
				properties[0].about like '%fillLevel%' and 
				about in (
					'_5fb87cdf_24d2_3f96_8620_f108de542c2f_fake',
					'_239d094b_7d77_3f32_8f43_7079a5634af3_fake',
					'_21cec1c6_3b8e_3d84_901e_3aaae1655c4b_fake', 
					'_2ee611f1_a0f5_3f59_8aa7_a7ccaf7409a4_fake'
					) 
			).std:unique(about)



		


	 

