create window meeter1Safe.std:lastevent() as observation(id = "1").win:length(2) where prev(resultValue) = resultValue or prev(resultValue) is null
	
	