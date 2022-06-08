import dayjs from 'dayjs';
import { IStockExchange } from 'app/shared/model/watcher/stock-exchange.model';
import { ICompany } from 'app/shared/model/watcher/company.model';
import { ICurrency } from 'app/shared/model/watcher/currency.model';
import { IPriceHistory } from 'app/shared/model/watcher/price-history.model';
import { IDividendHistory } from 'app/shared/model/watcher/dividend-history.model';
import { IStockSplitHistory } from 'app/shared/model/watcher/stock-split-history.model';
import { ICapitalGainHistory } from 'app/shared/model/watcher/capital-gain-history.model';
import { IIncomeHistory } from 'app/shared/model/watcher/income-history.model';
import { StockType } from 'app/shared/model/enumerations/stock-type.model';

export interface IStock {
  id?: number;
  ticker?: string | null;
  name?: string | null;
  imageContentType?: string | null;
  image?: string | null;
  marketCap?: string | null;
  volume?: number | null;
  peRation?: number | null;
  ipoDate?: string | null;
  isin?: string | null;
  isDelisted?: boolean | null;
  hasDividend?: boolean | null;
  type?: StockType | null;
  dividendYield?: number | null;
  stockExchange?: IStockExchange | null;
  company?: ICompany | null;
  currency?: ICurrency | null;
  sector?: ICompany | null;
  industry?: ICompany | null;
  priceHistories?: IPriceHistory[] | null;
  dividendHistories?: IDividendHistory[] | null;
  stockSplitHistories?: IStockSplitHistory[] | null;
  capitalGainHistories?: ICapitalGainHistory[] | null;
  incomeHistories?: IIncomeHistory[] | null;
}

export const defaultValue: Readonly<IStock> = {
  isDelisted: false,
  hasDividend: false,
};
