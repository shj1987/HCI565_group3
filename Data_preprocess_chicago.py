'''
Step1:
Huajie Shao@ 4/20/2018
add: remove the repeated ancestors and User_assertion
Fun: from the raw data of input to clean the data
add the media and friends counts
'''

import os, csv
import pprint as pp
import json
import string
import operator
# import re
import time

###--read business files----
def read_file():
	count = 0
	cityName = "Urbana"
	loc_score_map = dict()
	max_star = 0
	max_count = 0
	with open("yelp_restaurants_2800.json") as yelp:
		for line in yelp:
			line_object = json.loads(line)  #load json format
			try:
				count += 1
				business = line_object['businesses']
				N = len(business)
				for i in xrange(N):
					each_item = business[i]
					coordinate = each_item["location"]["coordinate"]
					latitude = coordinate["latitude"]
					longitude = coordinate["longitude"]
					loc = (latitude,longitude)
					bus_id = each_item['id']
					stars = each_item['rating']
					review_count = each_item["review_count"]
					loc_score_map[loc] = [review_count, stars]
				###----max-----
					if stars > max_star:
						max_star = stars
					if review_count > max_count:
						max_count = review_count
			except:
				pass
		print "Number of locations: ", count

	return loc_score_map, max_count, max_star

def compute_score(loc_score_map,max_count, max_star):
	fwrite = open("chicago_score.txt","w")
	loc_score = dict()
	w = 0.5
	num = 1
	for loc in loc_score_map:
		val = loc_score_map[loc]
		stars = val[1]
		review_count = val[0]
		norm_stars = stars/max_star
		norm_count = 1.0*review_count/max_count
		loc_score[loc] = w * norm_count + (1 - w) * norm_stars
	###ranking the result--
	score_res = sorted(loc_score.items(), key = operator.itemgetter(1), reverse = True)
	for loc, score in score_res:
		latitude,longitude = loc[0],loc[1]
		fwrite.write(str(num) + "\t" + str(latitude) + "\t" + str(longitude) + "\t" + str(score) + "\n")
		num += 1
	fwrite.close()
####--------------------
def main():
	loc_score_map,max_count, max_star = read_file()
	compute_score(loc_score_map,max_count, max_star)
	# read_review()

###-----main------
if __name__ == '__main__':
	time_start = time.time()
	main()
	time_end = time.time()
	print "Total time: ", time_end - time_start


