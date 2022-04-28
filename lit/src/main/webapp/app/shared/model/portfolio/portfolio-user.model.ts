import { IPortfolio } from 'app/shared/model/portfolio/portfolio.model';

export interface IPortfolioUser {
  id?: number;
  user?: string;
  portfolios?: IPortfolio[] | null;
}

export const defaultValue: Readonly<IPortfolioUser> = {};
