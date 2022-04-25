import dayjs from 'dayjs';
import { IStock } from 'app/shared/model/watcher/stock.model';

export interface ICapitalGainHistory {
  id?: number;
  date?: string | null;
  capitalGain?: number | null;
  stock?: IStock | null;
}

export const defaultValue: Readonly<ICapitalGainHistory> = {};
