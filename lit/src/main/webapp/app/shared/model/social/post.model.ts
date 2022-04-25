import dayjs from 'dayjs';
import { ISocialUser } from 'app/shared/model/social/social-user.model';
import { IReport } from 'app/shared/model/social/report.model';
import { IComment } from 'app/shared/model/social/comment.model';
import { IUserReaction } from 'app/shared/model/social/user-reaction.model';
import { ITag } from 'app/shared/model/social/tag.model';
import { LanguageType } from 'app/shared/model/enumerations/language-type.model';

export interface IPost {
  id?: number;
  content?: string;
  imageContentType?: string | null;
  image?: string | null;
  date?: string | null;
  language?: LanguageType | null;
  isPayedPromotion?: boolean | null;
  socialUser?: ISocialUser | null;
  reports?: IReport[] | null;
  comments?: IComment[] | null;
  userReactions?: IUserReaction[] | null;
  tags?: ITag[] | null;
}

export const defaultValue: Readonly<IPost> = {
  isPayedPromotion: false,
};
