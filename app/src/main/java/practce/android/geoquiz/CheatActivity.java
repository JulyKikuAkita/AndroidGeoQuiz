package practce.android.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

/**
 * User has 3 cheat count
 * Issue 1: cheat count does not distinguish from questions, if you click cheat for same question twice
 * it takes as 2 cheat counts
 * Issue 2: 4.4.4 API level every rotate deducts cheat count once
 */
public class CheatActivity extends AppCompatActivity {
    private static final String TAG = "CheatActivity";

    private static final String EXTRA_ANSWER_IS_TRUE =
            "com.practce.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN =
            "com.practce.android.geoquiz.answer_shown";
    private static final String CHEAT_COUNT_REMAINING =
            "com.practce.android.geoquiz.cheat_count";
    private boolean mAnswerIsTrue;
    private TextView mAnswerTextView;
    private Button mShowAnswerButton;
    private TextView mAPIVTextView;

    private boolean mIsCheat;
    private static int mCheatCount;
    private static final String CHEAT_BOOL = "cheat";


    public static Intent newIntent(Context packageContext, boolean answerIsTrue, int cheatCount) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(CHEAT_COUNT_REMAINING, cheatCount);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    public static int getCheatCountRemaining(Intent result) {
        return result.getIntExtra(CHEAT_COUNT_REMAINING, -1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);
        if (savedInstanceState != null) {
            mIsCheat = savedInstanceState.getBoolean(CHEAT_BOOL);
            mAnswerIsTrue = savedInstanceState.getBoolean(EXTRA_ANSWER_IS_TRUE);
            mCheatCount = savedInstanceState.getInt(CHEAT_COUNT_REMAINING);
            Log.i(TAG, "recall mIsCheat" + mIsCheat);
        }

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mCheatCount = getIntent().getIntExtra(CHEAT_COUNT_REMAINING, 3);

        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        if (mIsCheat == true || mCheatCount == 0) {
            setAnswerShowResult(mIsCheat);
            try{
                if (mAnswerIsTrue) {
                    mAnswerTextView.setText(R.string.true_button);
                } else {
                    mAnswerTextView.setText(R.string.false_button);
                }
                dismissShowAnswerButton();
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        if (mIsCheat == true || mCheatCount == 0) {
            mShowAnswerButton.setVisibility(View.INVISIBLE);
        } else {
            mShowAnswerButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if (mAnswerIsTrue) {
                        mAnswerTextView.setText(R.string.true_button);
                    } else {
                        mAnswerTextView.setText(R.string.false_button);
                    }
                    setAnswerShowResult(true);
                    dismissShowAnswerButton();
                }
            });
        }

        mAPIVTextView = (TextView) findViewById(R.id.buildVersion_text_view);
        String text = String.format(getResources().
                getString(R.string.api_version), Build.VERSION.RELEASE);
        mAPIVTextView.setText(text);
    }

    private void dismissShowAnswerButton() {
        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = mShowAnswerButton.getWidth() / 2;
            int cy = mShowAnswerButton.getHeight() / 2;
            float radius = mShowAnswerButton.getWidth();
            Animator anim = ViewAnimationUtils
                    .createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }
            });
            anim.start();
        } else {
            mShowAnswerButton.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnswerShowResult(boolean isAnswerShown) {
        if (mCheatCount <= 0) return;
        if (isAnswerShown) {
            mIsCheat = true;
            mCheatCount--;
        }
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        data.putExtra(CHEAT_COUNT_REMAINING, mCheatCount);
        setResult(RESULT_OK, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "savedInstanceState");
        savedInstanceState.putBoolean(CHEAT_BOOL, mIsCheat);
        savedInstanceState.putBoolean(EXTRA_ANSWER_IS_TRUE, mAnswerIsTrue);
        savedInstanceState.putInt(CHEAT_COUNT_REMAINING, mCheatCount);
    }
}
