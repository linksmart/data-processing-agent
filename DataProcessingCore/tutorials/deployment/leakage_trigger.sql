 select istream 
	 lastNoLeakageMesurment
 from 
	observation(id= '1').std:lastevent() as meeter, 
	meeter1Safe.win:length(1) as lastNoLeakageMesurment 
 where 
	meeter.phenomenonTime.after(lastNoLeakageMesurment.phenomenonTime, 5 seconds)
	
	