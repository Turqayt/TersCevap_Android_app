package cevap.reverse.quizapplication;

public class QuestionsList {
    private final String question, option1, option2;
    private final String answer;
    private int userSelectedAnswer;

    public QuestionsList(String question, String option1, String option2, String answer) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.answer = answer;
        this.userSelectedAnswer = 0;
    }

    public String getQuestion() {
        return question;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getAnswer() {
        return answer;
    }

    public int getUserSelectedAnswer() {
        return userSelectedAnswer;
    }

    public void setUserSelectedAnswer(int userSelectedAnswer) {
        this.userSelectedAnswer = userSelectedAnswer;
    }
}
