package models;

/**
 * Created by viper on 18/09/16.
 */
public class TextPost {
    private int postId;
    private String postContent, postUser, posUserId, userPicUrl, postTime, imageUrl;

    public TextPost()
    {

    }

    public TextPost(int postId, String postTime, String postUser, String postUserId, String userPicUrl, String postContent, String imageUrl) {
        this.postId = postId;
        this.postTime = postTime;
        this.postUser = postUser;
        this.posUserId = postUserId;
        this.userPicUrl = userPicUrl;
        this.postContent = postContent;
        this.imageUrl = imageUrl;
    }

    public String getUserPicUrl() {
        return userPicUrl;
    }

    public void setUserPicUrl(String userPicUrl) {
        this.userPicUrl = userPicUrl;
    }

    public String getPosUserId() {
        return posUserId;
    }

    public void setPosUserId(String posUserId) {
        this.posUserId = posUserId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getPostUser() {
        return postUser;
    }

    public void setPostUser(String postUser) {
        this.postUser = postUser;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }
}
