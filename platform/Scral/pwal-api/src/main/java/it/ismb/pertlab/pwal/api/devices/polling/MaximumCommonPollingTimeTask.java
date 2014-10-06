/*
 * PWAL -Network-level Data Publisher
 * 
 * Copyright (c) 2014 Dario Bonino
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package it.ismb.pertlab.pwal.api.devices.polling;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * A class implementing a {@link Callable} task that computes the maximum common
 * divisor of two integers using the Euler's method.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class MaximumCommonPollingTimeTask implements Callable<Integer>
{
	// the first integer
	private int pollingTimesMillis[];
	
	public MaximumCommonPollingTimeTask(int pollingTimesMillis[])
	{
		this.pollingTimesMillis = pollingTimesMillis;
	}
	
	@Override
	public Integer call() throws Exception
	{
		if(pollingTimesMillis.length ==1)
		{
			return pollingTimesMillis[0];
		}
		else if (pollingTimesMillis.length == 2)
		{
			// run the callable in this thread
			return gcd(this.pollingTimesMillis[0], this.pollingTimesMillis[1]);
			
		}
		else
		{
			//split computation in parallel tasks
			FutureTask<Integer> futureGCDRight = new FutureTask<Integer>(new MaximumCommonPollingTimeTask(Arrays.copyOfRange(pollingTimesMillis, 0, pollingTimesMillis.length / 2)));
			FutureTask<Integer> futureGCDLeft = new FutureTask<Integer>(new MaximumCommonPollingTimeTask(Arrays.copyOfRange(pollingTimesMillis, pollingTimesMillis.length / 2, pollingTimesMillis.length)));
		
			
			//execute the tasks
			futureGCDLeft.run();
			futureGCDRight.run();
			
			//return the result
			return gcd(futureGCDLeft.get(),futureGCDRight.get());
		}
	}
	
	/**
	 * Compute the maximum common divisor of two integers using the euclidean algorithm (division-based)
	 * @param a The first number
	 * @param b The second number
	 * @return The greates common divisor of the two numbers
	 */
	private Integer gcd(int a, int b)
	{
		//temporary variable
		int t;
		
		//cycle until the remainder of the division is 0
		while (b!=0)
		{
			//store b to use it as a in the next iteration
			t = b;
			
			//compute the remainder of the division between a and b
			b = a % b;
			
			// set a at the previous b value
			a = t;
		}
		// return the computed gcd
		return a;
	}
	
}
