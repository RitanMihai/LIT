export interface IPrediction {
  id?: string;
  date?: string | null;
  value?: number | null;
}

export const defaultValue: Readonly<IPrediction> = {};