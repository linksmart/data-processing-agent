select 
	window(* ) as bins
	
from 
	federation1.trn.v2.observation.hash.std:groupwin(sensor.id).win:expr(current_count = 8).win:length_batch(8) as wasteBin  
where 
	wasteBin.datastream.id = "53164a515c5435e0346bfb90280f3fdfbe6abe0e237e6c76d198339582b1fa99" 
	