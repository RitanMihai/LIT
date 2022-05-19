import pandas as pd
import matplotlib.pyplot as plt
import tensorflow as tf
import numpy as np
from pickle import load
import json

from sklearn.metrics import mean_squared_error

########### Test dataset #########
class Prediction(object):
    def __init__(self):
        # Load scaler/ index
        self.X_scaler = load(open('prediction_data_files/X_scaler.pkl', 'rb'))
        self.y_scaler = load(open('prediction_data_files/y_scaler.pkl', 'rb'))
        self.test_predict_index = np.load("prediction_data_files/index_test.npy", allow_pickle=True)

        # Load test dataset/ model
        self.G_model = tf.keras.models.load_model('models/AAPL_GAN_3to1.h5')
        self.X_test = np.load("prediction_data_files/X_test.npy", allow_pickle=True)
        self.y_test = np.load("prediction_data_files/y_test.npy", allow_pickle=True)

    def get_test_plot(self):
        # Set output steps
        output_dim = self.y_test.shape[1]

        # Get predicted data
        y_predicted = self.G_model(self.X_test)
        rescaled_real_y = self.y_scaler.inverse_transform(self.y_test)
        rescaled_predicted_y = self.y_scaler.inverse_transform(y_predicted)

        ## Predicted price
        predict_result = pd.DataFrame()
        for i in range(rescaled_predicted_y.shape[0]):
            y_predict = pd.DataFrame(rescaled_predicted_y[i], columns=["predicted_price"],
                                     index=self.test_predict_index[i:i + output_dim])

            predict_result = pd.concat([predict_result, y_predict], axis=1, sort=False)

        predict_result['predicted_mean'] = predict_result.mean(axis=1)

        #drop 2020
        # Input_Before = '2020-01-01'
        # predict_result = predict_result.loc[predict_result.index < Input_Before]
        # real_price = real_price.loc[real_price.index < Input_Before]

        # Plot the predicted result
        plt.figure(figsize=(16, 8))
        plt.plot(predict_result["predicted_mean"], color='r')
        plt.xlabel("Date")
        plt.ylabel("Stock price")
        plt.legend(("Real price", "Predicted price"), loc="upper left", fontsize=16)
        plt.title("The result of test", fontsize=20)
        plt.show()
        plt.savefig('test_plot.png')
        # Calculate RMSE
        predicted = predict_result["predicted_mean"]
        RMSE = 100
        return predict_result, RMSE

    def get_data(self):
        test_predicted, test_RMSE = self.get_test_plot()
        prediction_stock_json = json.loads(test_predicted.dropna(axis=1).to_json())

        #Static change predicated_mean in APPL
        prediction_stock_json = {'predicted_mean' : prediction_stock_json["predicted_mean"]}
        prediction_stock_json['APPL'] = prediction_stock_json.pop('predicted_mean')
        return prediction_stock_json