import datetime
import random


class Bin:
	
	name="default";
	feed_id="default";
	api_key="default";
	fillLevel=0;
	NOISERATIO=0.01
	UPDATETIME_secs=60
	
	MAXFILLUPTIME_secs=0
	BAG_WEIGTH_PERC=0
	BAG_DROP_PROBABILITY=0.1
	
	def __init__(self,name,feed_id,api_key):
		self.name = name;
		self.feed_id = feed_id;
		self.api_key = api_key;
		self.fillLevel = random.uniform(0,100);
		self.MAXFILLUPTIME_secs = random.uniform(2.5*3600, 3*3600);
		self.BAG_DROP_PROBABILITY=random.uniform(0.05,0.2)
		self.BAG_WEIGTH_PERC = 2*100 * (float(self.UPDATETIME_secs)/float(self.MAXFILLUPTIME_secs)) / float(self.BAG_DROP_PROBABILITY); # I multiply by 2 because distribution is uniform
		
	def update(self):
		now = datetime.datetime.now()
		
		#time-based rule: I keep empty bins at night, after 22 PM - I fill them up quickly in the morning
		if (now.hour >= 23) or (now.hour <= 6) :
		#if(True):
			#empty behaviour
			self.fillLevel = random.uniform(0,self.NOISERATIO);	
			print("\t\tempty behaviour for bin " + self.name);
		else:
			#filling behaviour
			
			if(self.fillLevel < 98):
				dice = random.uniform(0,1);
				if (dice < self.BAG_DROP_PROBABILITY):
					toadd=random.uniform(0,self.BAG_WEIGTH_PERC);
					print("\t\tadding " + ("%0.3f" % toadd) + " to bin " + self.name);
					self.fillLevel += toadd;
				self.fillLevel += random.uniform(-self.NOISERATIO,self.NOISERATIO)
			else:
				self.fillLevel = 100-self.NOISERATIO + random.uniform(-self.NOISERATIO,self.NOISERATIO);
		
	def __str__(self):
		return self.name + ', fillLevel=' + ("%0.3f" % self.fillLevel) + ', BAG_DROP_PROBABILITY=' + ("%0.3f" % self.BAG_DROP_PROBABILITY) + ', BAG_WEIGTH_PERC=' + ("%0.3f" % self.BAG_WEIGTH_PERC) + ', UPDATETIME_secs=' + str(self.UPDATETIME_secs)
