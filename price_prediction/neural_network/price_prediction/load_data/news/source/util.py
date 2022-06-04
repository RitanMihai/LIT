import json
import urllib.parse
import pandas as pd
from flask import Flask, abort, jsonify, request
from urllib.request import urlopen
import requests
import json

#app = Flask(__name__)
finbert_url = 'http://localhost:8087/list'
#payload = {"text":"Amazon rise 100% from the previous quarter"}
headers = {"Content-type": "application/json"}

#@app.route('/process_news')
def hello():
    news_data = pd.read_csv("analyst_ratings_processed.csv", parse_dates=['date'])
    news_data = news_data.drop(columns="id")
    news_data = news_data.sort_values(by='date').groupby('stock')
    #r = requests.post(finbert_url, json=payload)

    for name, group in news_data:
        if(name == 'NVDA'):
            print("Name ", name, "\n Group", group)
            #records = json.dumps(group.to_json(orient='records'))

            stock = name
            payload = json.loads(group['sentence'].to_json(orient="table", index=False))["data"]
            #payload['sentence'] = title # json.loads(group['sentence'].to_json(orient="table", index=False))["data"]

            #payload = payload.dumps(payload)
            #payload = json.dumps(payload[:3])
            r = requests.post(finbert_url, json=payload)
            r = json.dumps(r.json())

            predictions = pd.read_json(r)
            merged_data = pd.merge(predictions, group, on="sentence")
            merged_data.rename(columns={'sentiment_score': 'Score', 'date': 'Date'}, inplace=True)

            merged_data.to_csv("../"+stock + ".csv", index=False)

    return "Done"


if __name__ == '__main__':
    #app.run(host='0.0.0.0', port=8090, debug=False, threaded=True)
    hello()