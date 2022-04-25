import dayjs from 'dayjs';
import { IPortfolio } from 'app/shared/model/portfolio/portfolio.model';
import { TransactionType } from 'app/shared/model/enumerations/transaction-type.model';

export interface ITransaction {
  id?: number;
  type?: TransactionType | null;
  value?: number | null;
  date?: string | null;
  portfolio?: IPortfolio | null;
}

export const defaultValue: Readonly<ITransaction> = {};
