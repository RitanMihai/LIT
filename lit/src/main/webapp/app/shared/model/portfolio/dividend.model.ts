import dayjs from 'dayjs';
import { IOrder } from 'app/shared/model/portfolio/order.model';
import { DividendType } from 'app/shared/model/enumerations/dividend-type.model';

export interface IDividend {
  id?: number;
  dateRecived?: string | null;
  taxRate?: number | null;
  totalRecived?: number | null;
  dividendType?: DividendType | null;
  order?: IOrder | null;
}

export const defaultValue: Readonly<IDividend> = {};
