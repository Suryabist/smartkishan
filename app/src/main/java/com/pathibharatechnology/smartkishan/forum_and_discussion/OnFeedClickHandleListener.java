package com.pathibharatechnology.smartkishan.forum_and_discussion;

public interface OnFeedClickHandleListener {

    void onFeedClicked(DiscussionDTO discussionDTO);
    void onLikeBtnToggled(DiscussionDTO discussionDTO,boolean liked);

}
