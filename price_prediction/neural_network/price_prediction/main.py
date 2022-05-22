from flask import Flask, abort, jsonify
from flask_cors import CORS
import sys
import optparse
import time
from flask import request, render_template

from prediction import prediction as prd
from load_data import load_data as ld
from train.train import Train

app = Flask(__name__)


# ARGS: ticker
@app.route('/prediction', methods=['GET'])
def get_prediction():
    args = request.args
    ticker = args.get('ticker')
    prediction = None

    try:
        prediction = prd.Prediction(ticker)
    except FileNotFoundError:
        return jsonify(message="Prediction for " + ticker + " not found. Try to POST " + ticker), 404
    return prediction.get_data()

@app.route('/prediction', methods=['POST'])
def set_prediction():
    # Search for the specified ticker:
    args = request.args
    ticker = args.get('ticker')

    loadData = ld.LoadData(ticker)
    loadData.load()

    train = Train(ticker, 'gan')
    train.train()
    return jsonify(message="Done")

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8090, debug=False, threaded=True)
