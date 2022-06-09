from flask import Flask, abort, jsonify, Response

from flask_mongoengine import MongoEngine
import pymongo
import json
from flask import request
from flask_cors import CORS
import pprint
from prediction import prediction_real_fake as prd
from load_data import load_data as ld
from train.train import Train

from bson.objectid import ObjectId

app = Flask(__name__)
CORS(app, resources={r"*": {"origins": "*"}})

try:
    mongo = pymongo.MongoClient(
        host="localhost",
        username="admin",
        password="admin",
        port=27017,
        serverSelectionTimeoutMS=1000
    )

    db = mongo.predictions
    mongo.server_info()
except:
    print("ERROR - Cannot connecto to the database")


# ARGS: ticker
@app.route('/prediction/<ticker>', methods=['GET'])
def get_prediction(ticker):
    args = request.args
    start_date = args.get('start_date')
    end_date = args.get('end_date')

    prediction = None
    try:
        force_regenerating = True
        load_data = ld.LoadData(ticker, force_regenerating)
        generate_train_data = False
        if (start_date is not None) or (end_date is not None):
            print("HERE?")
            load_data.load(generate_train_data, start_date, end_date)
        else:
            load_data.load(generate_train_data)

        prediction = prd.Prediction(ticker)
    except FileNotFoundError:
        return jsonify(message="Prediction for " + ticker + " not found. Try to POST " + ticker), 404

    predictions = prediction.get_data()

    # I should replace with insert_many()
    for cur_prd in predictions:
        # Insert into database trough reflection, non-reflective would look like db.ticker.insert_one,
        # but this obviously do not work
        # Might work with db[ticker].insert_one(cur_prd), but I do not want to risk now
        dbResponse = getattr(db, ticker).insert_one(cur_prd)
        pass
    return jsonify("Done")


@app.route('/prediction/<ticker>', methods=['POST'])
def set_prediction(ticker):
    loadData = ld.LoadData(ticker)
    loadData.load()
    #
    train = Train(ticker, 'gan')
    train.train()
    return jsonify(message="Done")


@app.route('/mongo/predictions/<symbol>', methods=['GET'])
def get_stock_info(symbol):
    predictions = list(db[symbol].find())

    for prediction in predictions:
        prediction["_id"] = str(prediction["_id"])

    return Response(response=json.dumps(predictions), status=200, mimetype="application/json")

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8090, debug=False, threaded=True)
