import { IStock } from 'app/shared/model/watcher/stock.model';

export interface ICurrency {
  id?: number;
  code?: string | null;
  name?: string | null;
  currencySymbol?: string | null;
  stocks?: IStock[] | null;
}

export const defaultValue: Readonly<ICurrency> = {};
