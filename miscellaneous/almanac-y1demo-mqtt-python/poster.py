import httplib

in_file = open("test.txt","r")

i=0;



while(1):
	line = in_file.readline()
	if not line: break;
	
	if not(line.startswith('###')):
		line = line.replace('_','',1);
		line = line.replace('_','-');
		#print(str(i) + ':' + line)
		conn = httplib.HTTPConnection('192.168.50.54:8080')		
		resource = '/connectors.rest/devices/' + line
		
		conn.request("GET", resource);
		try:
			r1 = conn.getresponse()
			if(r1.status != 200):
				print("ERROR " + line);
		except httplib.BadStatusLine:
			print(str(i) + ': eccezzione: ' + line)
			pass		
		i+=1;
		conn.close()

in_file.close()
