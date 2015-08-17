{
    "Properties": [
        {
            "IoTStateObservation": [
                {
                    "Value": "level-alert"
                }
            ],
            "About": "Name"
        },
        {
            "IoTStateObservation": [
                {
                    "Value": "select Tools.CreateIoTEntities(*, 'LevelAlert', true), true as SetEventPerEntity from topics{[almanac.observation.iotentity.hash](about = '_5fb87cdf_24d2_3f96_8620_f108de542c2f_fake' or about = '_239d094b_7d77_3f32_8f43_7079a5634af3_fake' or about = '_21cec1c6_3b8e_3d84_901e_3aaae1655c4b_fake' or about = '_2ee611f1_a0f5_3f59_8aa7_a7ccaf7409a4_fake' ).std:groupwin(properties[0].about).win:length(5).win:time_batch(1 sec)} where properties[0].about like '%fillLevel%' group by properties[0].abouthaving avg(cast(properties[0].ioTStateObservation[0].value,double)) >80.0"
                }
            ],
            "About": "Statement"
        }
    ],
    "About": "DataFusionManager"
}