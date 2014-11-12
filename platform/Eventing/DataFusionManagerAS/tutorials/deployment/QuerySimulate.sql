select 
	Tools.CreateIoTEntity('_a2ae2f57_1362_3339_a7e4_d29799029cc5', 'FillLevelSensor:getLevel', Tools.Random.nextInt(30)),
	Tools.CreateIoTEntity('_239d094b_7d77_3f32_8f43_7079a5634af3', 'fillLevel:SimpleFillLevelSensor:getLevel', '85.00'),
	Tools.CreateIoTEntity('_21cec1c6_3b8e_3d84_901e_3aaae1655c4b', 'fillLevel:SimpleFillLevelSensor:getLevel', '85.00'),
	Tools.CreateIoTEntity('_2ee611f1_a0f5_3f59_8aa7_a7ccaf7409a4', 'fillLevel:SimpleFillLevelSensor:getLevel', '85.00'),
	Tools.CreateIoTEntity('_53ce16a8_6f26_38c4_8c29_ffa551ee42a9', 'fillLevel:SimpleFillLevelSensor:getLevel', '31.00'),
	true as SetEventPerEntity
from pattern[
	every timer:interval(1 seconds)
]
