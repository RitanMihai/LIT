package com.ritan.lit.gateway.domain.social;

import java.util.Set;

/*@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString*/
public class SocialUserRequest {
    Long id;
    String user;
    Set<ReportRequest> reports;
    Set<PostRequest> posts;
    Set<CommentRequest> comments;
    Set<ReplyRequest> replies;
    Set<UserReactionRequest> userReactions;
    Set<UserFollowingsRequest> userFollowings;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "SocialUserRequest{" +
            "id=" + id +
            ", user=" + user +
            ", reports=" + reports +
            ", posts=" + posts +
            ", comments=" + comments +
            ", replies=" + replies +
            ", userReactions=" + userReactions +
            ", userFollowings=" + userFollowings +
            '}';
    }
}
