import csv
import redis
r = redis.StrictRedis(host='localhost', port=6379, db=0)
with open('hawkercentre.csv', 'rb') as csvfile:
	spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
	for row in spamreader:
		r.execute_command("geoadd", "hawkers", row[0], row[1], row[3])