select 
	Tools.CreateIoTEntity('_5fb87cdf_24d2_3f96_8620_f108de542c2f_fake', 'FillLevelSensor:getLevel', Tools.Random.nextInt(100)),
	Tools.CreateIoTEntity('_239d094b_7d77_3f32_8f43_7079a5634af3_fake', 'fillLevel:SimpleFillLevelSensor:getLevel', '85.00'),
	Tools.CreateIoTEntity('_21cec1c6_3b8e_3d84_901e_3aaae1655c4b_fake', 'fillLevel:SimpleFillLevelSensor:getLevel', '85.00'),
	Tools.CreateIoTEntity('_2ee611f1_a0f5_3f59_8aa7_a7ccaf7409a4_fake', 'fillLevel:SimpleFillLevelSensor:getLevel', '85.00'),
	true as SetEventPerEntity
from pattern[
	every timer:interval(1 seconds)
]