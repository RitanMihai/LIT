export interface IPrediction {
  id?: string;
  date?: string | null;
  value?: number | null;
}

export const defaultValue: Readonly<IPrediction> = {
  id:"0",
  date:"02/02/2022",
  value:2222,
};