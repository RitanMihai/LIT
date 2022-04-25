import dayjs from 'dayjs';
import { IStock } from 'app/shared/model/watcher/stock.model';

export interface IPriceHistory {
  id?: number;
  date?: string | null;
  open?: number | null;
  high?: number | null;
  low?: number | null;
  close?: number | null;
  adjClose?: number | null;
  volume?: number | null;
  stock?: IStock | null;
}

export const defaultValue: Readonly<IPriceHistory> = {};
