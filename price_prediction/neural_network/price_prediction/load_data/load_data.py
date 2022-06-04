import yfinance as yf
import pandas as pd
import numpy as np
from pickle import dump
from sklearn.preprocessing import MinMaxScaler
import os
from load_data.util import csv_util, stock_association


class LoadData(object):
    def __init__(self, ticker=None):
        self.ticker = ticker

        self.tickerData = None
        self.news = None  # TODO: Find a method to get targeted news
        # Indexes, Compositions, Commodities
        self.technical_list = ['NASDAQ', 'NYSE', 'SP500', 'FTSE100', 'NIKKI225', 'SENSEX', 'RUSSELL2000', 'HENGSENG',
                               'SSE', 'CrudeOil', 'Gold', 'VIX', 'USDIndex']

        self.NASDAQ = pd.DataFrame()
        self.NYSE = pd.DataFrame()
        self.SP500 = pd.DataFrame()
        self.FTSE100 = pd.DataFrame()
        self.NIKKI225 = pd.DataFrame()
        self.SENSEX = pd.DataFrame()
        self.RUSSELL2000 = pd.DataFrame()
        self.HENGSENG = pd.DataFrame()
        self.SSE = pd.DataFrame()
        self.CrudeOil = pd.DataFrame()
        self.Gold = pd.DataFrame()
        self.VIX = pd.DataFrame()
        self.USDIndex = pd.DataFrame()

    # Default it generates train and test files
    # Date format is YYYY-MM-DD
    def load(self, isTrain=True, start_date=None, end_date=None):
        is_interval = (start_date is not None) or (end_date is not None)
        if (isTrain is False) and is_interval:
            print("Is recommended to train with full data, not with segments")

        print(" Load Ticker ")
        self.load_ticker(start_date, end_date)
        print(" Load Technical ")
        self.load_technical(start_date, end_date)
        print(" Add Similar Stocks ")
        self.add_similar_stocks(start_date, end_date)
        print(" Merge Technical ")

        if is_interval:
            self.__merge_thn()  # Do not check the files already generated
        else:
            self.merge_ticker_technical()

        print(" Merge News ")
        self.merge_news(isTrain)

    def load_ticker(self, start_date=None, end_date=None):
        # Check if we already have data
        if(start_date is None) or (end_date is None):
            try:
                self.tickerData = pd.read_csv("load_data.py/ticker_data" + self.ticker + ".csv", parse_dates=['Date'])
            except FileNotFoundError:
                self.tickerData = None
                self.tickerData = yf.download(self.ticker)

                # Drop Adj Close
                csv_util.drop_columns(self.tickerData, "Adj Close")
                self.tickerData.to_csv("load_data/ticker_data/" + self.ticker + ".csv")
        else:
            self.tickerData = yf.download(self.ticker, start=start_date, end=end_date)
            # Drop Adj Close
            csv_util.drop_columns(self.tickerData, "Adj Close")
            self.tickerData.to_csv("load_data/ticker_data/" + self.ticker + ".csv")

        return self.tickerData.to_json()

    def load_technical(self, start_date=None, end_date=None):
        # TODO: Check if we already have data
        try:
            for thn in range(len(self.technical_list)):
                current_thn = self.technical_list[thn]
                pd.read_csv('load_data/technical_data/' + current_thn + '.csv')

        except FileNotFoundError:
            # LOAD
            self.NASDAQ = yf.download('^IXIC')
            self.NYSE = yf.download('^NYA')
            self.SP500 = yf.download('^GSPC')
            self.FTSE100 = yf.download('^FTSE')
            self.NIKKI225 = yf.download('^N225')
            self.SENSEX = yf.download('^BSESN')
            self.RUSSELL2000 = yf.download('^RUT')
            self.HENGSENG = yf.download('^HSI')
            self.SSE = yf.download('000001.SS')
            self.CrudeOil = yf.download('CL=F')
            self.Gold = yf.download('GC=F')
            self.VIX = yf.download('^VIX')
            self.USDIndex = yf.download('DX-Y.NYB')

            # DROP ADJ CLOSE
            csv_util.drop_columns(self.NASDAQ, "Adj Close")
            csv_util.drop_columns(self.NYSE, "Adj Close")
            csv_util.drop_columns(self.SP500, "Adj Close")
            csv_util.drop_columns(self.FTSE100, "Adj Close")
            csv_util.drop_columns(self.NIKKI225, "Adj Close")
            csv_util.drop_columns(self.SENSEX, "Adj Close")
            csv_util.drop_columns(self.RUSSELL2000, "Adj Close")
            csv_util.drop_columns(self.HENGSENG, "Adj Close")
            csv_util.drop_columns(self.SSE, "Adj Close")
            csv_util.drop_columns(self.CrudeOil, "Adj Close")
            csv_util.drop_columns(self.Gold, "Adj Close")
            csv_util.drop_columns(self.VIX, "Adj Close")
            csv_util.drop_columns(self.USDIndex, "Adj Close")

            # SAVE
            self.NASDAQ.to_csv("load_data/technical_data/NASDAQ.csv")
            self.NYSE.to_csv("load_data/technical_data/NYSE.csv")
            self.SP500.to_csv("load_data/technical_data/SP500.csv")
            self.FTSE100.to_csv("load_data/technical_data/FTSE100.csv")
            self.NIKKI225.to_csv("load_data/technical_data/NIKKI225.csv")
            self.SENSEX.to_csv("load_data/technical_data/SENSEX.csv")
            self.RUSSELL2000.to_csv("load_data/technical_data/RUSSELL2000.csv")
            self.HENGSENG.to_csv("load_data/technical_data/HENGSENG.csv")
            self.SSE.to_csv("load_data/technical_data/SSE.csv")
            self.CrudeOil.to_csv("load_data/technical_data/CrudeOil.csv")
            self.Gold.to_csv("load_data/technical_data/Gold.csv")
            self.VIX.to_csv("load_data/technical_data/VIX.csv")
            self.USDIndex.to_csv("load_data/technical_data/USDIndex.csv")

    def merge_ticker_technical(self):
        try:
            self.tickerData = pd.read_csv("load_data/ticker_technical/" + self.ticker + ".csv")
        except FileNotFoundError:
            self.__merge_thn()

    def __merge_thn(self):
        for thn in range(len(self.technical_list)):
            current_thn = self.technical_list[thn]
            technical_data = pd.read_csv('load_data/technical_data/' + current_thn + '.csv',
                                         parse_dates=['Date'])

            # Keep just the Date and the Close value
            # 1 = Open, 2 = High, 3 = Low, 5 = Volume
            technical_data.drop(technical_data.columns[[1, 2, 3, 5]], axis=1, inplace=True)

            # Rename Close with the current technical data, ex: Close -> NASDAQ
            technical_data.rename(columns={'Close': current_thn}, inplace=True)

            self.tickerData = pd.merge(self.tickerData, technical_data, on='Date')

        self.tickerData = self.get_technical_indicators(self.tickerData)

        # Drop the first 21 rows
        # For doing the fourier
        self.tickerData = self.tickerData.iloc[20:, :].reset_index(drop=True)
        self.tickerData = pd.concat([self.tickerData, self.fourier(self.tickerData)], axis=1)
        self.tickerData.to_csv("load_data/ticker_technical/" + self.ticker + ".csv", index=False)

    def add_similar_stocks(self, start_date=None, end_date=None):
        similar = stock_association.similar_stocks(self.ticker)

        for i in range(len(similar)):
            current_ticker = similar[i]
            current_stock = yf.download(current_ticker)
            csv_util.reduce_to(current_stock, "Close")

            # Rename column Close with the stock name
            current_stock.rename(columns={'Close': current_ticker}, inplace=True)

            self.tickerData = pd.merge(self.tickerData, current_stock, on='Date')

    def merge_news(self, isTrain=True):
        # TODO: I should check in final_data/ticker if there are files first
        news_dataset = pd.read_csv("load_data/news/" + self.ticker + ".csv", parse_dates=["Date"])

        # Replace 0 by NA
        self.tickerData.replace(0, np.nan, inplace=True)
        # Add News data
        self.tickerData["News"] = news_dataset["Score"]

        # Check NA and fill them
        self.tickerData.isnull().sum()
        self.tickerData.iloc[:, 1:] = pd.concat(
            [self.tickerData.iloc[:, 1:].ffill(), self.tickerData.iloc[:, 1:].bfill()]).groupby(
            level=0).mean()

        # Set the date to datetime data
        datetime_series = pd.to_datetime(self.tickerData['Date'])
        datetime_index = pd.DatetimeIndex(datetime_series.values)
        self.tickerData = self.tickerData.set_index(datetime_index)
        self.tickerData = self.tickerData.sort_values(by='Date')
        self.tickerData = self.tickerData.drop(columns='Date')

        # Get features and target
        X_value = pd.DataFrame(self.tickerData.iloc[:, :])
        y_value = pd.DataFrame(self.tickerData.iloc[:, 3])

        # Reshape the data
        '''Set the data input steps and output steps, 
            we use 30 days data to predict 1 day price here, 
            reshape it to (None, input_step, number of features) used for LSTM input'''
        n_steps_in = 3
        n_steps_out = 1
        n_features = X_value.shape[1]

        # Normalized the data
        X_scaler = MinMaxScaler(feature_range=(-1, 1))
        y_scaler = MinMaxScaler(feature_range=(-1, 1))
        X_scaler.fit(X_value)
        y_scaler.fit(y_value)

        X_scale_dataset = X_scaler.fit_transform(X_value)
        y_scale_dataset = y_scaler.fit_transform(y_value)

        try:
            # We must, first, create a folder where to save data
            os.mkdir('models/train_files/' + self.ticker)
        except FileExistsError:
            print('New directory not created. Directory models/train_files/' + self.ticker + ' already exists')

        dump(X_scaler, open('models/train_files/' + self.ticker + '/X_scaler.pkl', 'wb'))
        dump(y_scaler, open('models/train_files/' + self.ticker + '/y_scaler.pkl', 'wb'))

        X, y, yc = self.__get_X_y(X_scale_dataset, y_scale_dataset, n_steps_in, n_steps_out)

        X_test = X
        y_test = y
        yc_test = yc

        try:
            # We must, first, create a folder where to save data
            os.mkdir('load_data/final_data/' + self.ticker)
        except FileExistsError:
            print('New directory not created. Directory load_data/final_data/' + self.ticker + ' already exists')

        np.save('load_data/final_data/' + self.ticker + '/X_test.npy', X_test)
        np.save('load_data/final_data/' + self.ticker + '/y_test.npy', y_test)
        np.save('load_data/final_data/' + self.ticker + '/yc_test.npy', yc_test)
        np.save('load_data/final_data/' + self.ticker + '/index_test.npy', self.tickerData.index)

        if isTrain:
            X_train = self.__extract_train_test(X)
            y_train = self.__extract_train_test(y)
            yc_train = self.__extract_train_test(yc)

            train_predict_index = self.tickerData.iloc[n_steps_in: X_train.shape[0] + n_steps_in + n_steps_out - 1, :].index
            try:
                os.mkdir('models/train_files/' + self.ticker)
            except FileExistsError:
                print('New directory not created. Directory models/train_files/ ' + self.ticker + ' already exists')

            np.save('models/train_files/' + self.ticker + '/X_train.npy', X_train)
            np.save('models/train_files/' + self.ticker + '/y_train.npy', y_train)
            np.save('models/train_files/' + self.ticker + '/yc_train.npy', yc_train)
            np.save('models/train_files/' + self.ticker + '/index_train.npy', train_predict_index)

    def update_ticker(self):
        pass

    def update_technical(self):
        # Check the last data from the csv. and append the data from then to today
        pass

    def get_technical_indicators(self, ticker_data):
        # Create 7 and 21 days Moving Average
        ticker_data['MA7'] = ticker_data.iloc[:, 4].rolling(window=7).mean()
        ticker_data['MA21'] = ticker_data.iloc[:, 4].rolling(window=21).mean()

        # Create MACD
        ticker_data['MACD'] = ticker_data.iloc[:, 4].ewm(span=26).mean() - ticker_data.iloc[:, 1].ewm(span=12,
                                                                                                      adjust=False).mean()

        # Create Bollinger Bands
        ticker_data['20SD'] = ticker_data.iloc[:, 4].rolling(20).std()
        ticker_data['upper_band'] = ticker_data['MA21'] + (ticker_data['20SD'] * 2)
        ticker_data['lower_band'] = ticker_data['MA21'] - (ticker_data['20SD'] * 2)

        # Create Exponential moving average
        ticker_data['EMA'] = ticker_data.iloc[:, 4].ewm(com=0.5).mean()

        # Create LogMomentum
        ticker_data['logmomentum'] = np.log(ticker_data.iloc[:, 4] - 1)

        return ticker_data

    def fourier(self, ticker_data):
        # Get the columns for doing fourier
        data_FT = ticker_data[['Date', 'Close']]

        close_fft = np.fft.fft(np.asarray(data_FT['Close'].tolist()))
        fft_df = pd.DataFrame({'fft': close_fft})
        fft_df['absolute'] = fft_df['fft'].apply(lambda x: np.abs(x))
        fft_df['angle'] = fft_df['fft'].apply(lambda x: np.angle(x))

        fft_list = np.asarray(fft_df['fft'].tolist())
        fft_com_df = pd.DataFrame()
        for num_ in [3, 6, 9]:
            fft_list_m10 = np.copy(fft_list);
            fft_list_m10[num_:-num_] = 0
            fft_ = np.fft.ifft(fft_list_m10)
            fft_com = pd.DataFrame({'fft': fft_})
            fft_com['absolute of ' + str(num_) + ' comp'] = fft_com['fft'].apply(lambda x: np.abs(x))
            fft_com['angle of ' + str(num_) + ' comp'] = fft_com['fft'].apply(lambda x: np.angle(x))
            fft_com = fft_com.drop(columns='fft')
            fft_com_df = pd.concat([fft_com_df, fft_com], axis=1)

        return fft_com_df

    def download_news(self):
        print()

    def __get_X_y(self, X_data, y_data, n_in, n_out):
        X = list()
        y = list()
        yc = list()

        length = len(X_data)
        for i in range(0, length, 1):
            X_value = X_data[i: i + n_in][:, :]
            y_value = y_data[i + n_in: i + (n_in + n_out)][:, 0]
            yc_value = y_data[i: i + n_in][:, :]

            if len(X_value) == n_in and len(y_value) == n_out:
                X.append(X_value)
                y.append(y_value)
                yc.append(yc_value)

        return np.array(X), np.array(y), np.array(yc)

    # Extract train dataset
    def __extract_train_test(self, data):
        train_size = round(len(data) * 0.7)
        data_train = data[0:train_size]
        return data_train
