import { IStock } from 'app/shared/model/watcher/stock.model';

export interface IStockExchange {
  id?: number;
  name?: string | null;
  symbol?: string | null;
  country?: string | null;
  stocks?: IStock[] | null;
}

export const defaultValue: Readonly<IStockExchange> = {};
