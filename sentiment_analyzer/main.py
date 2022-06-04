from flask import Flask
from flask_cors import CORS
import sys
import optparse
import time
from flask import request, render_template
from finbert.finbert import predict
#from pytorch_pretrained_bert.modeling import BertForSequenceClassification
import json
import numpy as np

from transformers import AutoModelForSequenceClassification

import nltk
import os

nltk.download('punkt')
app = Flask(__name__)
CORS(app)
start = int(round(time.time()))
# The one below works without docker
#model = BertForSequenceClassification.from_pretrained('src/models/classifier_model/finber-sentiment', num_labels=3, cache_dir=None)


#model = AutoModelForSequenceClassification.from_pretrained('models/classifier_model/finber-sentiment', num_labels=3, cache_dir=None)

#In docker image the below one is used
model = AutoModelForSequenceClassification.from_pretrained('models/classifier_model/finber-sentiment', num_labels=3, cache_dir=None)

label_dict = {0: 'positive', 1: 'negative', 2: 'neutral'}

@app.route("/",methods=['POST'])
def score():
    text=request.get_json()['sentence']
    print("The text that i receive is " + text)
    return(predict(text, model).to_json(orient='records'))

@app.route("/list",methods=['POST'])
def score2():
    sentences = request.get_json()
    predictions = []
    for sentence in sentences:
        text = sentence['sentence']
        prediction = json.loads(predict(text, model).to_json(orient='records'))
        
        sentiment_score = 0
        
        logit = [0,0,0]
        for itr in prediction:
            logit = np.add(itr["logit"], logit).tolist()
            sentiment_score = sentiment_score + itr["sentiment_score"]
        
        prediction_as_text = label_dict[np.argmax(np.array(logit))]
        
        #prediction = label_dict[sentiment_score]

        prd = {"sentence": text, "prediction":prediction_as_text, "sentiment_score":sentiment_score}
        predictions.append(prd)

    return json.dumps(predictions)

@app.route("/",methods=['GET'])
def index():
     return render_template('index.html')

@app.route("/first")
def hello_world():
    return "<p>Hello, World!</p>"

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8087, debug=False, threaded=True)
