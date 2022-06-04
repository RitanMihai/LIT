from flask import Flask, abort, jsonify
from flask_cors import CORS
import sys
import optparse
import time
from flask_mongoengine import MongoEngine
from flask import request, render_template

from prediction import prediction_real_fake as prd
from load_data import load_data as ld
from train.train import Train

app = Flask(__name__)

app.config['MONGODB_SETTINGS'] = {
    'db': 'prediction',
    'host': 'localhost',
    'username': 'admin',
    'password': 'admin',
    'port': 27017
}

db = MongoEngine()
db.init_app(app)


# ARGS: ticker
@app.route('/prediction/<ticker>', methods=['GET'])
def get_prediction(ticker):
    args = request.args
    start_date = args.get('start_date')
    end_date = args.get('end_date')

    prediction = None
    try:
        load_data = ld.LoadData(ticker)
        generate_train_data = False
        if (start_date is not None) or (end_date is not None):
            print("HERE?")
            load_data.load(generate_train_data, start_date, end_date)
        else:
            load_data.load(generate_train_data)

        prediction = prd.Prediction(ticker)
    except FileNotFoundError:
        return jsonify(message="Prediction for " + ticker + " not found. Try to POST " + ticker), 404
    return prediction.get_data()


@app.route('/prediction/<ticker>', methods=['POST'])
def set_prediction(ticker):
    loadData = ld.LoadData(ticker)
    loadData.load()
    #
    train = Train(ticker, 'gan')
    train.train()
    return jsonify(message="Done")


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8090, debug=False, threaded=True)
