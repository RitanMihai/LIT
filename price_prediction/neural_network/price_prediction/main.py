from flask import Flask
from flask_cors import CORS
import sys
import optparse
import time
from flask import request, render_template

from prediction import prediction as prd

app = Flask(__name__)

@app.route("/prediction",methods=['GET'])
def get_prediction():
     prediction = prd.Prediction()
     return prediction.get_data()

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8090, debug=False, threaded=True)