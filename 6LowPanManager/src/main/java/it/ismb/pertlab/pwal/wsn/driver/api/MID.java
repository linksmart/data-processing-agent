package it.ismb.pertlab.pwal.wsn.driver.api;

public class MID {
		private static MID mid = new MID();
		private int cnt = 0;
		
		private MID(){}
		
		public MID getMID(){
			return mid;
		}
		
		public int nextMID(){
			return cnt++;
		}
}
