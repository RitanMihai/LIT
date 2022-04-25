import { IPortfolio } from 'app/shared/model/portfolio/portfolio.model';

export interface IPortfolioCurrency {
  id?: number;
  code?: string | null;
  name?: string | null;
  currencySymbol?: string | null;
  portfolios?: IPortfolio[] | null;
}

export const defaultValue: Readonly<IPortfolioCurrency> = {};
