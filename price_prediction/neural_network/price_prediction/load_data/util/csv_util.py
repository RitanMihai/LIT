#def drop_adj_close(dataframe):
#    adj_close_pos = 4
#    return dataframe.drop(dataframe.columns[[adj_close_pos]], axis=1, inplace=True)

columns_position = {"Open": 0, "High": 1, "Low": 2, "Close": 3, "Adj Close": 4, "Volume": 5}

def drop_columns(dataframe, *columns):
    drop_list = list()
    for column in columns:
        drop_list.append(columns_position[column])

    dataframe.drop(dataframe.columns[drop_list], axis=1, inplace=True)

def reduce_to(dataframe, column):
    column_position = columns_position[column]
    drop_list = list(columns_position.values())
    drop_list.remove(column_position)

    dataframe.drop(dataframe.columns[drop_list], axis=1, inplace=True)

