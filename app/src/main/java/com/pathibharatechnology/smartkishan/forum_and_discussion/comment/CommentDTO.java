package com.pathibharatechnology.smartkishan.forum_and_discussion.comment;

public class CommentDTO {
    private String comment;
    private String commeneterId;
    private String postId;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommeneterId() {
        return commeneterId;
    }

    public void setCommeneterId(String commeneterId) {
        this.commeneterId = commeneterId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
