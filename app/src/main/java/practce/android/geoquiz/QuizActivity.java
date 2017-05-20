package practce.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionTextView;
    private int mCurrentIndex = 0;
    private int mAnswered = 0;
    private boolean mIsCheater;
    private int mCheatCount = 3;

    public enum Answer {RIGHT, WRONG, CHEAT, NO_ANSWER};
    private int[] mAnswers = new int[]{
            Answer.NO_ANSWER.ordinal(),
            Answer.NO_ANSWER.ordinal(),
            Answer.NO_ANSWER.ordinal(),
            Answer.NO_ANSWER.ordinal(),
            Answer.NO_ANSWER.ordinal(),
            Answer.NO_ANSWER.ordinal()
    };

    private static final String KEY_INDEX = "index";
    private static final String ANSWER_INDEX = "correctAnswers";
    private static final String ANSWERED_ARRAY = "answeredQuestions";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final String CHEATER_BOOL = "cheater";
    private static final String CHEAT_COUNT = "cheat_count";


    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_africa, true),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
            new Question(R.string.question_mideast, true),
            new Question(R.string.question_oceans, true)
    };

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "savedInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putInt(ANSWER_INDEX, mAnswered);
        savedInstanceState.putIntArray(ANSWERED_ARRAY, mAnswers);
        savedInstanceState.putBoolean(CHEATER_BOOL, mIsCheater);
        savedInstanceState.putInt(CHEAT_COUNT, mCheatCount);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mAnswered = savedInstanceState.getInt(ANSWER_INDEX, 0);
            mAnswers = savedInstanceState.getIntArray(ANSWERED_ARRAY);
            mIsCheater = savedInstanceState.getBoolean(CHEATER_BOOL);
            mCheatCount = savedInstanceState.getInt(CHEAT_COUNT);
            Log.i(TAG, "recall mCurrentIndex" + mCurrentIndex);
            Log.i(TAG, "recall mCorrectAnswers" + mAnswered);
            Log.i(TAG, "recall mAnsweredArray");
            Log.i(TAG, "recall mCheatCount" + mCheatCount);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                checkAnswer(true);
                toggleButtonEnable(false);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
                toggleButtonEnable(false);
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        updateCheatButton();
        mCheatButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue, mCheatCount);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length < 0 ?
                        mQuestionBank.length - 1 : (mCurrentIndex - 1) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });
        updateQuestion();
    }

    private void toggleButtonEnable(boolean isEnabled) {
        mTrueButton.setEnabled(isEnabled);
        mFalseButton.setEnabled(isEnabled);
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        if (mAnswers[mCurrentIndex] == Answer.NO_ANSWER.ordinal()) {
            toggleButtonEnable(true);
        } else {
            toggleButtonEnable(false);
        }
    }

    private void updateCheatButton() {
        String text = getResources().getString(R.string.cheat_button);
        mCheatButton.setText(String.format(text, mCheatCount));
        if (mCheatCount <= 0) mCheatButton.setVisibility(View.INVISIBLE);
    }

    private void checkAnswer(boolean userPressedTrue) {
        mAnswered++;
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;
        Toast toast = new Toast(this);
        if (mIsCheater) {
            messageResId = R.string.judgment_toast;
            toast = toast.makeText(this, messageResId, Toast.LENGTH_LONG);
            mAnswers[mCurrentIndex] = Answer.CHEAT.ordinal();
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId =  R.string.correct_toast;
                toast = toast.makeText(this, messageResId, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 300);
                mAnswers[mCurrentIndex] = Answer.RIGHT.ordinal();
            } else {
                messageResId = R.string.incorrect_toast;
                toast = toast.makeText(this, messageResId, Toast.LENGTH_SHORT);
                mAnswers[mCurrentIndex] = Answer.WRONG.ordinal();
            }
        }

        toast.show();
        //mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;

        if (mAnswered == mQuestionBank.length) {
            int[] stats  = new int[] {0,0,0}; //Right, wrong, cheat
            for (int ans : mAnswers) {
                if(ans == Answer.RIGHT.ordinal()) stats[Answer.RIGHT.ordinal()]++;
                if(ans == Answer.CHEAT.ordinal()) stats[Answer.CHEAT.ordinal()]++;
            }
            double percentage = (100 * stats[Answer.RIGHT.ordinal()] / mAnswered);
            Log.d(TAG, "percentage: " + percentage + " correct: " + stats[Answer.RIGHT.ordinal()] + " mAnswered: " + mAnswered);
            Log.d(TAG, "percentage: " + percentage + " cheat: " + stats[Answer.CHEAT.ordinal()] + " mCheated: " + mAnswered);

            Toast.makeText(getApplicationContext(),
                    "Correct: " + stats[Answer.RIGHT.ordinal()] + " Cheat: " + stats[Answer.CHEAT.ordinal()],
                    Toast.LENGTH_LONG).show();
            mAnswered = 0; //reset
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) return;
            mIsCheater = CheatActivity.wasAnswerShown(data);
            mCheatCount = CheatActivity.getCheatCountRemaining(data);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        updateCheatButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
        updateCheatButton();
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}
