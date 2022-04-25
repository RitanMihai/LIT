import { IOrder } from 'app/shared/model/portfolio/order.model';

export interface IStockInfo {
  id?: number;
  ticker?: string | null;
  name?: string | null;
  imageContentType?: string | null;
  image?: string | null;
  isin?: string | null;
  dividendYield?: number | null;
  sector?: string | null;
  industry?: string | null;
  orders?: IOrder[] | null;
}

export const defaultValue: Readonly<IStockInfo> = {};
