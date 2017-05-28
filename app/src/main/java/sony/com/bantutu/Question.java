package sony.com.bantutu;

/**
 * Created by Sony Surya on 08/05/2017.
 */

public class Question {
    String questionId, questionTitle, questionDescription, questionStatus, questionPostDate, questionPostTime, userId, questionReply;

    public Question () {

    }

    public Question(String questionId, String questionTitle, String questionDescription, String questionStatus, String questionPostDate, String questionPostTime, String userId, String questionReply) {
        this.questionId = questionId;
        this.questionTitle = questionTitle;
        this.questionDescription = questionDescription;
        this.questionStatus = questionStatus;
        this.questionPostDate = questionPostDate;
        this.questionPostTime = questionPostTime;
        this.userId = userId;
        this.questionReply = questionReply;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public String getQuestionStatus() {
        return questionStatus;
    }

    public String getQuestionPostDate() {
        return questionPostDate;
    }

    public String getQuestionPostTime() {
        return questionPostTime;
    }

    public String getUserId() {
        return userId;
    }

    public String getQuestionReply() {
        return questionReply;
    }
}
