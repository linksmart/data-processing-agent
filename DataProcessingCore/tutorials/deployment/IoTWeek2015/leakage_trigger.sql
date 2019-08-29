 select istream 
	 lastNoLeakageMesurment
 from 
	observation(id= '00a467b9290129a71c6b496813cf52b437d878f25148773494967e2b85a2031b').std:lastevent() as meeter, 
	lekage_safe_00a467b9290129a71c6b496813cf52b437d878f25148773494967e2b85a2031b.win:length(1) as lastNoLeakageMesurment 
 where 
	meeter.phenomenonTime.after(lastNoLeakageMesurment.phenomenonTime, 30 seconds)
	
	