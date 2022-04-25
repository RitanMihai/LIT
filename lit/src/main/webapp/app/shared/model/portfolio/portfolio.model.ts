import { IPortfolioUser } from 'app/shared/model/portfolio/portfolio-user.model';
import { IPortfolioCurrency } from 'app/shared/model/portfolio/portfolio-currency.model';
import { IOrder } from 'app/shared/model/portfolio/order.model';
import { ITransaction } from 'app/shared/model/portfolio/transaction.model';

export interface IPortfolio {
  id?: number;
  name?: string | null;
  value?: number | null;
  imageContentType?: string | null;
  image?: string | null;
  unrealisedValue?: number | null;
  profitOrLoss?: number | null;
  portfolioUser?: IPortfolioUser | null;
  portfolioCurrency?: IPortfolioCurrency | null;
  orders?: IOrder[] | null;
  transactions?: ITransaction[] | null;
}

export const defaultValue: Readonly<IPortfolio> = {};
