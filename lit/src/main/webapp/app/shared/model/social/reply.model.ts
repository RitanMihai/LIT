import dayjs from 'dayjs';
import { ISocialUser } from 'app/shared/model/social/social-user.model';
import { IComment } from 'app/shared/model/social/comment.model';
import { IReport } from 'app/shared/model/social/report.model';
import { ITag } from 'app/shared/model/social/tag.model';
import { LanguageType } from 'app/shared/model/enumerations/language-type.model';

export interface IReply {
  id?: number;
  content?: string;
  date?: string | null;
  language?: LanguageType | null;
  socialUser?: ISocialUser | null;
  comment?: IComment | null;
  reports?: IReport[] | null;
  tags?: ITag[] | null;
}

export const defaultValue: Readonly<IReply> = {};
