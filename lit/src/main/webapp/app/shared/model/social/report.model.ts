import { IPost } from 'app/shared/model/social/post.model';
import { IComment } from 'app/shared/model/social/comment.model';
import { IReply } from 'app/shared/model/social/reply.model';
import { ISocialUser } from 'app/shared/model/social/social-user.model';
import { RportType } from 'app/shared/model/enumerations/rport-type.model';

export interface IReport {
  id?: number;
  type?: RportType | null;
  description?: string | null;
  post?: IPost | null;
  comment?: IComment | null;
  reply?: IReply | null;
  socialUser?: ISocialUser | null;
}

export const defaultValue: Readonly<IReport> = {};
