package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia.Util.Prefs;
import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Question;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String MESSEGE_ID ="getscore" ;
    private TextView questionTextview;
    private TextView questionCounterTextview;
    private Button trueButton;
    private Button falseButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private TextView currentscore;
    private TextView highscore;
    private int currentQuestionIndex = 0;
    private int scorecount=0;
    private List<Question> questionList;
    private SoundPool soundPool;
    private  int s1,s2,s3,s4;
    public static int MAX_STREAMS = 4;
    public static int SOUND_PRIORITY = 1;
    public static int SOUND_QUALITY = 100;
    private Prefs prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs=new Prefs(MainActivity.this);

        questionTextview=findViewById(R.id.question_textview);
        questionCounterTextview=findViewById(R.id.counter_text);
        trueButton=findViewById(R.id.true_button);
        falseButton=findViewById(R.id.false_button);
        nextButton=findViewById(R.id.next_button);
        prevButton=findViewById(R.id.prev_button);
        currentscore=findViewById(R.id.score_txt);
        highscore=findViewById(R.id.high_score);

        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);

        AudioAttributes audioAttributes=new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .build();

        soundPool=new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(MAX_STREAMS)
                .build();

        s1=soundPool.load(this,R.raw.complete,SOUND_PRIORITY);
        s2=soundPool.load(this,R.raw.correct,SOUND_PRIORITY);
        s3=soundPool.load(this,R.raw.defeat_one,SOUND_PRIORITY);
        s4=soundPool.load(this,R.raw.defeat_two,SOUND_PRIORITY);

        currentQuestionIndex=prefs.getState();
        scorecount=prefs.getScore();
        highscore.setText("High Score: "+prefs.getHighScore());
        questionList= new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processfinshed(ArrayList<Question> questionArrayList) {
                questionTextview.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                questionCounterTextview.setText(currentQuestionIndex+"/" +questionArrayList.size());
                currentscore.setText("Current Score: "+scorecount);

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.next_button:
                gonext();
                break;
            case R.id.prev_button:
                if(currentQuestionIndex>0)
                {
                    currentQuestionIndex=((currentQuestionIndex-1)%questionList.size());
                    updateQuestion(); }

                break;
            case R.id.true_button:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_button:
                checkAnswer(false);
                updateQuestion();
                break;
        }

    }

    private void checkAnswer(boolean userchoice) {
        int toastid=0;
        boolean getanswer=questionList.get(currentQuestionIndex).isAnswerTrue();
        if(getanswer==userchoice)
        {
            fadeAnimation();
            soundPool.pause(s4);
            soundPool.play(s1,1,1,0,0,1);
            toastid=R.string.correct;
            scorecount+=100;
            currentscore.setText("Current Score: "+scorecount);
        }
        else
        {
            shakeAnimation();
            soundPool.pause(s1);
            soundPool.play(s4,1,1,0,0,1);
            toastid=R.string.wrong;
            if(scorecount==0)
            {
                currentscore.setText("Current Score: "+scorecount);
            }
            else
            {
                scorecount-=100;
                currentscore.setText("Current Score: "+scorecount);
            }

        }
        Toast.makeText(MainActivity.this,toastid,Toast.LENGTH_SHORT).show();
    }

    public  void  updateQuestion()
    {
        String question=questionList.get(currentQuestionIndex).getAnswer();
        questionTextview.setText(question);
        questionCounterTextview.setText(currentQuestionIndex+"/"+questionList.size());


    }
    public void shakeAnimation()
    {
        Animation shake= AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_animation);
        final CardView cardView=findViewById(R.id.cardView);
        cardView.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
             cardView.setCardBackgroundColor(Color.WHITE);
             gonext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

public void fadeAnimation()
{
    final CardView cardView=findViewById(R.id.cardView);
    AlphaAnimation alphaAnimation= new AlphaAnimation(1.0f,0.0f);
    alphaAnimation.setDuration(250);
    alphaAnimation.setRepeatMode(Animation.REVERSE);
    alphaAnimation.setRepeatCount(1);
    cardView.setAnimation(alphaAnimation);

    alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            cardView.setCardBackgroundColor(Color.GREEN);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
         cardView.setCardBackgroundColor(Color.WHITE);
         gonext();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    });
}
public void gonext()
{
    currentQuestionIndex=((currentQuestionIndex+1)%questionList.size());
    updateQuestion();
}


    @Override
    protected void onPause() {
        super.onPause();
        prefs.saveHighScore(scorecount);
        prefs.setState(currentQuestionIndex);
        prefs.setScore(scorecount);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool=null;
    }
}
