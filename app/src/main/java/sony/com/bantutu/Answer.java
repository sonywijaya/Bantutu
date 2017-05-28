package sony.com.bantutu;

/**
 * Created by Sony Surya on 22/05/2017.
 */

public class Answer {
    String questionId, userId, answerId, answerTitle, answerDescription, answerPostDate, answerPostTime, isBestAnswer;

    public Answer () {

    }

    public Answer(String questionId, String userId, String answerId, String answerTitle, String answerDescription, String answerPostDate, String answerPostTime, String isBestAnswer) {
        this.questionId = questionId;
        this.userId = userId;
        this.answerId = answerId;
        this.answerTitle = answerTitle;
        this.answerDescription = answerDescription;
        this.answerPostDate = answerPostDate;
        this.answerPostTime = answerPostTime;
        this.isBestAnswer = isBestAnswer;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getUserId() {
        return userId;
    }

    public String getAnswerId() {
        return answerId;
    }

    public String getAnswerTitle() {
        return answerTitle;
    }

    public String getAnswerDescription() {
        return answerDescription;
    }

    public String getAnswerPostDate() {
        return answerPostDate;
    }

    public String getAnswerPostTime() {
        return answerPostTime;
    }

    public String getIsBestAnswer() {
        return isBestAnswer;
    }
}
