import dayjs from 'dayjs';
import { IStock } from 'app/shared/model/watcher/stock.model';

export interface ICompany {
  id?: number;
  name?: string | null;
  imageContentType?: string | null;
  image?: string | null;
  description?: string | null;
  employees?: number | null;
  sector?: string | null;
  industry?: string | null;
  ceo?: string | null;
  site?: string | null;
  dateOfEstablishment?: string | null;
  stocks?: IStock[] | null;
}

export const defaultValue: Readonly<ICompany> = {};
