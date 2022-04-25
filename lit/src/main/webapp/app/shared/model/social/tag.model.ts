import { IPost } from 'app/shared/model/social/post.model';
import { IComment } from 'app/shared/model/social/comment.model';
import { IReply } from 'app/shared/model/social/reply.model';

export interface ITag {
  id?: number;
  stockName?: string | null;
  ticker?: string | null;
  posts?: IPost[] | null;
  comments?: IComment[] | null;
  replies?: IReply[] | null;
}

export const defaultValue: Readonly<ITag> = {};
