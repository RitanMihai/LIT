import numpy as np
from train.gan.gan import GAN, make_generator_model, make_discriminator_model
from pickle import load

class Train(object):
    def __init__(self, ticker, nural_network_type):
        self.X_train = np.load('models/train_files/' + ticker + '/X_train.npy', allow_pickle=True)
        self.y_train = np.load('models/train_files/' + ticker + '/y_train.npy', allow_pickle=True)
        self.yc_train = np.load('models/train_files/' + ticker + '/yc_train.npy', allow_pickle=True)

        self.nural_network_type = nural_network_type
        self.ticker = ticker

    def train(self):
        input_dim = self.X_train.shape[1]
        feature_size = self.X_train.shape[2]
        output_dim = self.y_train.shape[1]

        ## For Bayesian
        opt = {"lr": 0.00016, "epoch": 165, 'bs': 128}

        generator = make_generator_model(self.X_train.shape[1], output_dim, self.X_train.shape[2])
        discriminator = make_discriminator_model()

        # Rescale back the real dataset
        X_scaler = load(open('models/train_files/'+self.ticker+'/X_scaler.pkl', 'rb'))
        y_scaler = load(open('models/train_files/'+self.ticker+'/y_scaler.pkl', 'rb'))

        gan = GAN(generator, discriminator, opt, self.ticker)
        Predicted_price, Real_price, RMSPE = gan.train(self.X_train, self.y_train, self.yc_train, opt)

        print(Predicted_price)
