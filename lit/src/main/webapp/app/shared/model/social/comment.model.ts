import dayjs from 'dayjs';
import { ISocialUser } from 'app/shared/model/social/social-user.model';
import { IPost } from 'app/shared/model/social/post.model';
import { IReport } from 'app/shared/model/social/report.model';
import { IReply } from 'app/shared/model/social/reply.model';
import { ITag } from 'app/shared/model/social/tag.model';
import { LanguageType } from 'app/shared/model/enumerations/language-type.model';

export interface IComment {
  id?: number;
  content?: string;
  date?: string | null;
  language?: LanguageType | null;
  socialUser?: ISocialUser | null;
  post?: IPost | null;
  reports?: IReport[] | null;
  replies?: IReply[] | null;
  tags?: ITag[] | null;
}

export const defaultValue: Readonly<IComment> = {};
