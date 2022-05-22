import pandas as pd
import yfinance as yf


# This is unstable if a new sector appear in dataset, there should be minimum 3
def similar_stocks(ticker, numberOf=3):  # ticker: 'Main'
    # Static data
    # This should be changed with a list of yahoo tickers
    nasdaq_data = pd.read_csv("load_data/util/data/nasdaq_screener.csv")
    ticker_row = nasdaq_data.loc[nasdaq_data['Symbol'] == ticker]  # ticker_row: {DataFrame } Symbol, Name ... Industry

    if (ticker_row.empty):
        tickerData = yf.Ticker(ticker).info
        sector = tickerData['sector']
        industry = tickerData['industry']
        long_name = tickerData['longName']
        country = tickerData['country']
        market_cap = tickerData['marketCap']

        new_row = {'Symbol': ticker, 'Name': long_name, 'Last Sale': 0, 'Net Change': 0, '% Change': 0,
                   'Market Cap': market_cap, 'Country': country, 'IPO Year': 0, 'Volume': 0, 'Sector': sector,
                   'Industry': industry}

        nasdaq_data = nasdaq_data.append(new_row, ignore_index=True)
        ticker_row = nasdaq_data.loc[nasdaq_data['Symbol'] == ticker]
        nasdaq_data.to_csv("load_data/util/data/nasdaq_screener.csv", index=False)

    # Filter by sector
    ticker_sector = ticker_row['Sector'].values[0]  # Finance
    filter_condition = nasdaq_data['Sector'] == ticker_sector
    filtered_data = nasdaq_data[filter_condition]  # By Finance

    market_cap = ticker_row['Market Cap'].values[0]
    treshold = 0.15 * market_cap  # 15% of the company value
    min_limit = str(market_cap - treshold)
    max_limit = str(market_cap + treshold)

    # Filter by market cap
    filtered_data = filtered_data[filtered_data['Symbol'] != ticker]
    filtered_data_market_cap = filtered_data.query(min_limit + ' < `Market Cap` < ' + max_limit)

    if len(filtered_data_market_cap) == 0:
        filtered_data_market_cap = filtered_data.sort_values(by=['Market Cap'], ascending=False)

    filtered_data = filtered_data_market_cap
    return filtered_data.Symbol[:numberOf].tolist()  # ['ABCB', 'APAM', 'ASB']
