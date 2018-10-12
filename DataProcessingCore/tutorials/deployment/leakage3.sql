select 
	istream meeter.id 
from 
	federation1.trn.v2.observation.T1.win:time(11 seconds) as meeter 
where
	Tools.cmpBinaryInWindow(prev(meeter),meeter,  first(meeter)) and
	meeter.phenomenonTime.getTime() - first(meeter).phenomenonTime.getTime() > 9000 