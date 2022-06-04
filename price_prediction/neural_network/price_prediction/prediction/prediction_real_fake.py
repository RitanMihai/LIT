import pandas as pd
import matplotlib.pyplot as plt
import tensorflow as tf
import numpy as np
from pickle import load
import json
from sklearn.metrics import mean_squared_error

class Prediction(object):
    def __init__(self, ticker=None):
        self.ticker = ticker
        # Load index
        self.test_predict_index = np.load('load_data/final_data/' + ticker + '/index_test.npy', allow_pickle=True)

        self.X_scaler = load(open('models/train_files/' + ticker + '/X_scaler.pkl', 'rb'))
        self.y_scaler = load(open('models/train_files/' + ticker + '/y_scaler.pkl', 'rb'))

        # Load test dataset/ model
        self.G_model = tf.keras.models.load_model('models/gan/' + ticker + '/GAN_3to1.h5')
        self.X_test = np.load('load_data/final_data/' + ticker + '/X_test.npy', allow_pickle=True)
        self.y_test = np.load('load_data/final_data/' + ticker + '/y_test.npy', allow_pickle=True)

    def set_ticker(self, ticker):
        self.ticker = ticker

    def get_test_plot(self):
        # Set output steps
        output_dim = self.y_test.shape[1]

        # Get predicted data
        y_predicted = self.G_model(self.X_test)
        rescaled_predicted_y = self.y_scaler.inverse_transform(y_predicted)

        ## Predicted price
        predict_result = pd.DataFrame()
        for i in range(rescaled_predicted_y.shape[0]):
            y_predict = pd.DataFrame(rescaled_predicted_y[i], columns=["predicted_price"],
                                     index=self.test_predict_index[i:i + output_dim])

            predict_result = pd.concat([predict_result, y_predict], axis=1, sort=False)

        ## Real price
        rescaled_real_y = self.y_scaler.inverse_transform(self.y_test)
        real_price = pd.DataFrame()
        for i in range(rescaled_real_y.shape[0]):
            y_train = pd.DataFrame(rescaled_real_y[i], columns=["real_price"],
                                   index=self.test_predict_index[i:i + output_dim])
            real_price = pd.concat([real_price, y_train], axis=1, sort=False)

        real_price['real_mean'] = real_price.mean(axis=1)
        predict_result['predicted_mean'] = predict_result.mean(axis=1)

        # Plot the predicted result
        plt.figure(figsize=(16, 8))
        plt.plot(real_price["real_mean"])
        plt.plot(predict_result["predicted_mean"], color='r')
        plt.xlabel("Date")
        plt.ylabel("Stock price")
        plt.legend(("Real price", "Predicted price"), loc="upper left", fontsize=16)
        plt.title("The result of test", fontsize=20)
        plt.show()
        plt.savefig('test_plot.png')
        # Calculate RMSE
        predicted = predict_result["predicted_mean"]
        real = real_price["real_mean"]
        For_MSE = pd.concat([predicted, real], axis=1)
        RMSE = np.sqrt(mean_squared_error(predicted, real))
        print('-- RMSE -- ', RMSE)

        return predicted

    def get_data(self):
        test_predicted = self.get_test_plot()
        prediction_stock_json = json.loads(test_predicted.to_json())

        # Static change predicated_mean in ticker
        # prediction_stock_json = {'predicted_mean': prediction_stock_json["predicted_mean"]}
        # prediction_stock_json[self.ticker] = prediction_stock_json.pop('predicted_mean')
        return prediction_stock_json
