import dayjs from 'dayjs';
import { IStockInfo } from 'app/shared/model/portfolio/stock-info.model';
import { IPortfolio } from 'app/shared/model/portfolio/portfolio.model';
import { IDividend } from 'app/shared/model/portfolio/dividend.model';
import { OrderType } from 'app/shared/model/enumerations/order-type.model';
import { PositionType } from 'app/shared/model/enumerations/position-type.model';

export interface IOrder {
  id?: number;
  quantity?: number;
  sharePrice?: number;
  type?: OrderType;
  position?: PositionType;
  subbmitedDate?: string | null;
  filledDate?: string | null;
  notes?: string | null;
  total?: number | null;
  taxes?: number | null;
  stopLoss?: number | null;
  takeProfit?: number | null;
  leverage?: number | null;
  exchangeRate?: number | null;
  isCFD?: boolean | null;
  stockInfo?: IStockInfo | null;
  portfolio?: IPortfolio | null;
  dividends?: IDividend[] | null;
}

export const defaultValue: Readonly<IOrder> = {
  isCFD: false,
};
