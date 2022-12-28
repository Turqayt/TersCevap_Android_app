package cevap.reverse.quizapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import cevap.reverse.quizapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView totalQuestionsTextView;
    TextView questionTextView, information, question_info;
    Button ansA,ansB, start;
    // creating questions list
    private final List<QuestionsList> questionsLists = new ArrayList<>();

    int score=0;
    int basla = 0;
    int currentQuestionIndex = 0;
    String selectedAnswer = "";
    public int totalquestion ;
    TextView edt_sure_gir;

    private CountDownTimer countDownTimer;
    private boolean sure_isliyor;

    private FirebaseDatabase databaseReference = FirebaseDatabase.getInstance("https://terscevap-8aefd-default-rtdb.europe-west1.firebasedatabase.app/");

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //getting data  from firbase
        addPostEventListener(databaseReference.getReference());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //total quetsion
        totalQuestionsTextView = findViewById(R.id.total_question);
        information = findViewById(R.id.information);
        questionTextView = findViewById(R.id.question);
        question_info = findViewById(R.id.question_info);
        ansA = findViewById(R.id.ans_A);
        ansB = findViewById(R.id.ans_B);
        start = findViewById(R.id.start);
        edt_sure_gir = findViewById(R.id.timer);

        question_info.setVisibility(View.GONE);
        information.setVisibility(View.VISIBLE);
        start.setVisibility(View.VISIBLE);
        start.setOnClickListener(this::onStart);


    }

    private void addPostEventListener(@NonNull DatabaseReference mpReference){

        ValueEventListener postlistener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot questions : snapshot.child("questions").getChildren()){
                    String getQuestion = questions.child("question").getValue(String.class);
                    String getOption1 = questions.child("option1").getValue(String.class);
                    String getOption2 = questions.child("option2").getValue(String.class);
                    String getAnswer = questions.child("answer").getValue(String.class);

                    QuestionsList questionsList = new QuestionsList(getQuestion, getOption1, getOption2, getAnswer);

                    questionsLists.add(questionsList);
                    totalquestion = questionsLists.size();
                    totalQuestionsTextView.setText("Toplam Soru :"+totalquestion);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to get data from Firebase", Toast.LENGTH_LONG).show();
            }
        };
        mpReference.addValueEventListener(postlistener);

    }


    public void onStart(View view){

        information.setVisibility(View.GONE);
        start.setVisibility(View.GONE);
        ansA.setOnClickListener(this);
        ansB.setOnClickListener(this);


        loadNewQuestion();
    }

    @Override
    public void onClick(View view) {
        ansA.setBackgroundColor(Color.WHITE);
        ansB.setBackgroundColor(Color.WHITE);
        sureSifirla();
        Button clickedButton = (Button) view;
        selectedAnswer = clickedButton.getText().toString();


            if(selectedAnswer.equals(questionsLists.get(currentQuestionIndex).getAnswer())){
                question_info.setVisibility(View.VISIBLE);
                question_info.setText("Doğru");
                question_info.setTextColor(getResources().getColor(com.google.firebase.database.R.color.common_google_signin_btn_text_light_disabled));
                score++;
            }
            else {
                question_info.setVisibility(View.VISIBLE);
                question_info.setText("Yanlış");
                question_info.setTextColor(getResources().getColor(com.google.firebase.database.collection.R.color.common_google_signin_btn_text_dark_pressed));
            }
            currentQuestionIndex++;
            loadNewQuestion();


    }

    void loadNewQuestion() {


        sureBaslat();
        if(currentQuestionIndex == questionsLists.size()){
            fininishQuiz();
            return;
        }

        questionTextView.setText(currentQuestionIndex+1+".SORU  \n\n"+String.valueOf(questionsLists.get(currentQuestionIndex).getQuestion()));
        ansA.setText(questionsLists.get(currentQuestionIndex).getOption1());
        ansB.setText(questionsLists.get(currentQuestionIndex).getOption2());
    }
    void fininishQuiz(){
        sureDurdur();
        String passStatus = "";
        if(score > questionsLists.size()*0.60){
            passStatus = "Hepsini yanlış işaretleyerek doğruları buldun";
        }
        else{
            passStatus = "Sen anlamadın gibi tekrar dene anlarsın";
        }

        new AlertDialog.Builder(this)
                .setTitle(passStatus)
                .setMessage("Skor "+ score+ " Toplam Soru "+ totalquestion)
                .setPositiveButton("Tekrar Oyna",(dialogInterface, i)-> restartQuiz())
                .setNegativeButton("Çıkış Yap", ((dialogInterface, i) -> quit()))
                .setCancelable(false)
                .show();
    }
    void quit(){
        System.exit(0);
    }
    void restartQuiz(){
        score = 0;
        currentQuestionIndex = 0;
        question_info.setVisibility(View.GONE);
       loadNewQuestion();

    }

    private void sureBaslat() {

        int alinan_veri = Integer.parseInt(edt_sure_gir.getText().toString());

        countDownTimer = new CountDownTimer(alinan_veri * 1000, 1000) {
            @Override
            public void onTick(long l) {

                edt_sure_gir.setText("" +l/1000);
            }

            @Override
            public void onFinish() {

                sureSifirla();

                currentQuestionIndex++;
                loadNewQuestion();

            }
        }.start();

        sure_isliyor = true;
        //sifirla dur

    }

    private void sureDurdur() {
        countDownTimer.cancel();
        sure_isliyor = false;
        //durdur

    }

    private void sureSifirla() {
        countDownTimer.cancel();
        sure_isliyor = false;
        edt_sure_gir.setText("5");
        //sifirla
    }


}
