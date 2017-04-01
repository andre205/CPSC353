import twitter
import json
from urllib import unquote
import sys

CONSUMER_KEY = 'Rej7x4wDaowUruWveVDErRRLI'
CONSUMER_SECRET = 'v2rOmHb2IxcJ4RlZFO2FGHjbjlarVwrceiy5dqI8v97ksmdDzF'
OAUTH_TOKEN = '829385334551040001-l21ut58yQSBm5f4BnXIzrTghwxzTS0s'
OAUTH_TOKEN_SECRET = 'sITLs1CbkgrFyBpuY0QTCVosq95ug2Ewx4fxczllAzXjp'

auth = twitter.oauth.OAuth(OAUTH_TOKEN, OAUTH_TOKEN_SECRET, CONSUMER_KEY, CONSUMER_SECRET)

twitter_api = twitter.Twitter(auth=auth)

#q = raw_input("Enter search term 1: ")
#q2 = raw_input("Enter search term 2: ")

if len(sys.argv) != 3:
    sys.exit('Please enter 2 search terms as command line arguments')

q = sys.argv[1]
q2 = sys.argv[2]

count = 1000

search_results = twitter_api.search.tweets(q=q, count=count)
search_results2 = twitter_api.search.tweets(q=q2, count=count)

statuses = search_results['statuses']
statuses2 = search_results2['statuses']

# Iterate through 5 more batches of results by following the cursor

for _ in range(5):
    #print "Length of statuses", len(statuses)
    #print "Length of statuses2", len(statuses2)
    try:
        next_results = search_results['search_metadata']['next_results']
        next_results2 = search_results2['search_metadata']['next_results']
    except KeyError, e: # No more results when next_results doesn't exist
        break

    # Create a dictionary from next_results, which has the following form:
    # ?max_id=313519052523986943&q=NCAA&include_entities=1
    kwargs = dict([ kv.split('=') for kv in next_results[1:].split("&") ])
    kwargs2 = dict([ kv.split('=') for kv in next_results2[1:].split("&") ])

    search_results = twitter_api.search.tweets(**kwargs)
    search_results2 = twitter_api.search.tweets(**kwargs2)

    statuses += search_results['statuses']
    statuses2 += search_results2['statuses']


status_texts = [ status['text']
                 for status in statuses ]
status_texts2 = [ status['text']
                 for status in statuses2 ]

# Compute a collection of all words from all tweets
words = [ w
          for t in status_texts
              for w in t.split() ]
words2 = [ w
          for t in status_texts2
              for w in t.split() ]

print 'Sentiment Analysis'
sent_file = open('AFINN-111.txt')

scores = {} # initialize an empty dictionary
for line in sent_file:
    term, score  = line.split("\t")  # The file is tab-delimited. "\t" means "tab character"
    scores[term] = int(score)  # Convert the score to an integer.
score = 0
score2 = 0
for word in words:
    uword = word.encode('utf-8')
    if uword in scores.keys():
        score = score + scores[word]
for word in words2:
    uword = word.encode('utf-8')
    if uword in scores.keys():
        score2 = score2 + scores[word]

print
print 'Sentiment score for ' + q + ': ' + str(float(score))
print 'Sentiment score for ' + q2 + ': ' + str(float(score2))
print
if float(score2) > float(score):
    print q2 + ' had a higher sentiment'
elif float(score2) == float(score):
    print 'equal sentiment'
else:
    print q + ' had a higher sentiment'
