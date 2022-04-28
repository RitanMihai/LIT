import { IReport } from 'app/shared/model/social/report.model';
import { IPost } from 'app/shared/model/social/post.model';
import { IComment } from 'app/shared/model/social/comment.model';
import { IReply } from 'app/shared/model/social/reply.model';
import { IUserReaction } from 'app/shared/model/social/user-reaction.model';
import { IUserFollowing } from 'app/shared/model/social/user-following.model';

export interface ISocialUser {
  id?: number;
  user?: string;
  reports?: IReport[] | null;
  posts?: IPost[] | null;
  comments?: IComment[] | null;
  replies?: IReply[] | null;
  userReactions?: IUserReaction[] | null;
  userFollowings?: IUserFollowing[] | null;
}

export const defaultValue: Readonly<ISocialUser> = {};
