package life.langteng.community.exception;

import life.langteng.community.bean.ReminderMessage;

public class NotLoginException extends CommunityException {

    public NotLoginException() {
    }

    public NotLoginException(ReminderMessage errorMessage) {
        super(errorMessage);
    }
}
