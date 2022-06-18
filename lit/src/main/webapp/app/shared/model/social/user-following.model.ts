import { ISocialUser } from 'app/shared/model/social/social-user.model';

export interface IUserFollowing {
  id?: number;
  stock?: string | null;
  socialUsers?: ISocialUser[] | null;
}

export const defaultValue: Readonly<IUserFollowing> = {};
