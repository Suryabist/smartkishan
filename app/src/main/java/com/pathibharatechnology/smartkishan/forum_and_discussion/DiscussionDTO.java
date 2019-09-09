package com.pathibharatechnology.smartkishan.forum_and_discussion;

import java.util.HashMap;

public class DiscussionDTO {

    private String content;
    private String imageUrl;
    private Integer commentCount;
    private String postUploaderUserId;
    private String postId;
    private Integer likeCount;
    private String date;
    private HashMap<String,Boolean> likes;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public String getPostUploaderUserId() {
        return postUploaderUserId;
    }

    public void setPostUploaderUserId(String postUploaderUserId) {
        this.postUploaderUserId = postUploaderUserId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public HashMap<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(HashMap<String, Boolean> likes) {
        this.likes = likes;
    }
}
