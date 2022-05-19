import pandas as pd
import matplotlib.pyplot as plt
import tensorflow as tf
import numpy as np
from pickle import load
from sklearn.metrics import mean_squared_error


########### Test dataset #########

# Load scaler/ index
X_scaler = load(open('../data_processing/forecast/X_scaler.pkl', 'rb'))
y_scaler = load(open('../data_processing/forecast/y_scaler.pkl', 'rb'))
test_predict_index = np.load("../data_processing/forecast/index_test.npy", allow_pickle=True)

# Load test dataset/ model
G_model = tf.keras.models.load_model('../../Models/GAN_3to1.h5')
#G_model = tf.keras.models.load_model('../gan/model/gen_model_3_1_164.h5')
X_test = np.load("../data_processing/forecast/X_test.npy", allow_pickle=True)
y_test = np.load("../data_processing/forecast/y_test.npy", allow_pickle=True)


def get_test_plot(X_test, y_test):
    # Set output steps
    output_dim = y_test.shape[1] #3
    # Get predicted data
    y_predicted = G_model(X_test)
    rescaled_predicted_y = y_scaler.inverse_transform(y_predicted)

    ## Predicted price
    predict_result = pd.DataFrame()
    for i in range(rescaled_predicted_y.shape[0]):
        y_predict = pd.DataFrame(rescaled_predicted_y[i], columns=["predicted_price"],
                                 index=test_predict_index[i:i + output_dim])

        print("y_predict ", y_predict)
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
    plt.legend(("Predicted price"), loc="upper left", fontsize=16)
    plt.title("The result of test", fontsize=20)
    plt.show()
    plt.savefig('test_plot.png')
    print("finished")
    return predict_result

def get_prediction():
    test_predicted = get_test_plot(X_test, y_test)
    test_predicted.to_json("output.json")
    return test_predicted.to_json()

test_predicted = get_test_plot(X_test, y_test)
test_predicted.to_csv("predicted/test_predicted.csv")
