import { IPost } from 'app/shared/model/social/post.model';
import { ISocialUser } from 'app/shared/model/social/social-user.model';
import { UserReactionType } from 'app/shared/model/enumerations/user-reaction-type.model';

export interface IUserReaction {
  id?: number;
  type?: UserReactionType | null;
  post?: IPost | null;
  socialUser?: ISocialUser | null;
}

export const defaultValue: Readonly<IUserReaction> = {};
