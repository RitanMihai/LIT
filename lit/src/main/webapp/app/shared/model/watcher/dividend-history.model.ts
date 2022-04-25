import dayjs from 'dayjs';
import { IStock } from 'app/shared/model/watcher/stock.model';

export interface IDividendHistory {
  id?: number;
  date?: string | null;
  dividend?: number | null;
  stock?: IStock | null;
}

export const defaultValue: Readonly<IDividendHistory> = {};
