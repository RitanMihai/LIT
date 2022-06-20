export interface IPortfolioDetails {
    name?: string;
    invested: number;
    stockNumber: number;
}

export const defaultValue: Readonly<IPortfolioDetails> = {
    invested: 0,
    stockNumber: 0
};
