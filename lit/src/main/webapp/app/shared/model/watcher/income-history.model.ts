import dayjs from 'dayjs';
import { IStock } from 'app/shared/model/watcher/stock.model';

export interface IIncomeHistory {
  id?: number;
  date?: string | null;
  totalRevenue?: number | null;
  costOfRevenue?: number | null;
  grossProfit?: number | null;
  operatingExpense?: number | null;
  operatingIncome?: number | null;
  stock?: IStock | null;
}

export const defaultValue: Readonly<IIncomeHistory> = {};
