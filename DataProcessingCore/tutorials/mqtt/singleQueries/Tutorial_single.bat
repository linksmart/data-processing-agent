java -jar Client\target\.\DataFusionClient-1.0.0-SNAPSHOT-jar-with-dependencies.jar -n "binA" -e "_5fb87cdf_24d2_3f96_8620_f108de542c2f_fake" -f DataFusionManagerAS\tutorials\mqtt\groupQuery\BinA.json
java -jar Client\target\.\DataFusionClient-1.0.0-SNAPSHOT-jar-with-dependencies.jar -n "binB" -e "_239d094b_7d77_3f32_8f43_7079a5634af3_fake" -f DataFusionManagerAS\tutorials\mqtt\groupQuery\BinB.json
java -jar Client\target\.\DataFusionClient-1.0.0-SNAPSHOT-jar-with-dependencies.jar -n "binC" -e "_21cec1c6_3b8e_3d84_901e_3aaae1655c4b_fake" -f DataFusionManagerAS\tutorials\mqtt\groupQuery\BinC.json
java -jar Client\target\.\DataFusionClient-1.0.0-SNAPSHOT-jar-with-dependencies.jar -n "binD" -e "_2ee611f1_a0f5_3f59_8aa7_a7ccaf7409a4_fake" -f DataFusionManagerAS\tutorials\mqtt\groupQuerys\BinD.json
timeout 5 > nul
java -jar Client\target\.\DataFusionClient-1.0.0-SNAPSHOT-jar-with-dependencies.jar -n "binALevelAlert" -e "_5fb87cdf_24d2_3f96_8620_f108de542c2f_fake" -f DataFusionManagerAS\tutorials\mqtt\groupQuery\BinALevelAlert.json
java -jar Client\target\.\DataFusionClient-1.0.0-SNAPSHOT-jar-with-dependencies.jar -n "binBLevelAlert" -e "_239d094b_7d77_3f32_8f43_7079a5634af3_fake" -f DataFusionManagerAS\tutorials\mqtt\groupQuery\BinBLevelAlert.json
java -jar Client\target\.\DataFusionClient-1.0.0-SNAPSHOT-jar-with-dependencies.jar -n "binCLevelAlert" -e "_21cec1c6_3b8e_3d84_901e_3aaae1655c4b_fake" -f DataFusionManagerAS\tutorials\mqtt\groupQuery\BinCLevelAlert.json
java -jar Client\target\.\DataFusionClient-1.0.0-SNAPSHOT-jar-with-dependencies.jar -n "binDLevelAlert" -e "_2ee611f1_a0f5_3f59_8aa7_a7ccaf7409a4_fake" -f DataFusionManagerAS\tutorials\mqtt\groupQuery\BinDLevelAlert.json
