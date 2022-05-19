import pandas as pd
import matplotlib.pyplot as plt
import tensorflow as tf
import numpy as np
from pickle import load
from sklearn.metrics import mean_squared_error

X_scaler = load(open('../data_processing/forecast/X_scaler.pkl', 'rb'))
y_scaler = load(open('../data_processing/forecast/y_scaler.pkl', 'rb'))
test_predict_index = np.load("../data_processing/forecast/index_test.npy", allow_pickle=True)


# Load model
G_model = tf.keras.models.load_model('../../Models/GAN_3to1.h5')
X_test = np.load("../data_processing/forecast/X_test.npy", allow_pickle=True)
y_test = np.load("../data_processing/forecast/y_test.npy", allow_pickle=True)
y_test_hat = G_model(X_test[-1].reshape(1, X_test[-1].shape[0], X_test[-1].shape[1]))
rescaled_real_ytest = y_scaler.inverse_transform(y_test[-32:])
rescaled_predicted_ytest = y_scaler.inverse_transform(y_test_hat)

output_dim = 3
## Predicted price
predict_result = pd.DataFrame()
y_predict = pd.DataFrame(rescaled_predicted_ytest[0], columns=["predicted_price"], index=test_predict_index[-3:])
predict_result = pd.concat([predict_result, y_predict], axis=1, sort=False)
predict_result['predicted_mean'] = predict_result.mean(axis=1)

#
# Plot the predicted result
plt.figure(figsize=(16, 8))
plt.plot(predict_result["predicted_mean"], color = 'r')
plt.xlabel("Date")
plt.ylabel("Stock price")
plt.ylim(0, 100)
plt.legend(("Real price", "Predicted price"), loc="upper left", fontsize=16)
plt.title("The result of the last set of testdata", fontsize=20)
plt.show()
plt.savefig('single_test_plot.png')
# Calculate RMSE
predicted = predict_result["predicted_mean"]


predict_result.to_csv("predicted/test_predicted.csv")