import dayjs from 'dayjs';
import { IStock } from 'app/shared/model/watcher/stock.model';

export interface IStockSplitHistory {
  id?: number;
  date?: string | null;
  ratio?: number | null;
  stock?: IStock | null;
}

export const defaultValue: Readonly<IStockSplitHistory> = {};
