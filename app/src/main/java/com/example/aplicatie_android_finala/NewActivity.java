package com.example.aplicatie_android_finala;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.aplicatie_android_finala.data.database.AppDatabase;
import com.example.aplicatie_android_finala.data.database.Progress;
import com.example.aplicatie_android_finala.data.database.ProgressDao;
import com.example.aplicatie_android_finala.data.database.Question;
import com.example.aplicatie_android_finala.data.database.QuestionDao;
import com.example.aplicatie_android_finala.fragments.FirstFragment;
import com.example.aplicatie_android_finala.fragments.SecondFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewActivity extends FragmentActivity {

    private static final String TAG = "NewActivity";

    private ProgressBar progressBar;
    private Button buttonVerificare;
    private ImageButton buttonClose;
    private Button buttonFinish;
    private int progress = 0;
    private List<Question> questions;
    private List<Question> wrongQuestions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private AppDatabase db;
    private TextView resultTextView;
    private TextView congratulationsTextView;
    private boolean isAnswerChecked = false;
    private boolean isLastAnswerCorrect = false;
    private ExecutorService executorService;

    private int subcategoryId;
    private String subcategoryName;
    private int testId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        progressBar = findViewById(R.id.progress_bar);
        buttonVerificare = findViewById(R.id.button_verificare);
        buttonClose = findViewById(R.id.buttonClose);
        buttonFinish = findViewById(R.id.button_finish);
        resultTextView = findViewById(R.id.result_text_view);
        congratulationsTextView = findViewById(R.id.congratulations_text_view);

        db = AppDatabase.getDatabase(this);
        executorService = Executors.newSingleThreadExecutor();

        subcategoryId = getIntent().getIntExtra("SUBCATEGORY_ID", -1);
        subcategoryName = getIntent().getStringExtra("SUBCATEGORY_NAME");

        Log.d(TAG, "onCreate: Initializing database and loading questions");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ProgressDao progressDao = db.progressDao();
                List<Progress> progresses = progressDao.getProgressBySubcategoryId(subcategoryId);
                if (!progresses.isEmpty()) {
                    Progress progress = progresses.get(0);
                    testId = progress.completed + 1;
                } else {
                    testId = 1; // Dacă nu există progres anterior, începem cu testId 1
                }

                Log.d(TAG, "run: Loading questions for testId: " + testId);

                QuestionDao questionDao = db.questionDao();
                questions = questionDao.getQuestionsByTestIdAndSubcategoryId(testId, subcategoryId);
                Collections.shuffle(questions); // Amestecăm întrebările
                Log.d(TAG, "run: Questions shuffled and loaded");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (questions != null && !questions.isEmpty()) {
                            progressBar.setMax(questions.size() * 10);
                            displayQuestion(questions.get(currentQuestionIndex));
                        } else {
                            Log.w(TAG, "run: No questions found in the database");
                        }
                    }
                });
            }
        }).start();

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Close button clicked");
                finish();
            }
        });

        buttonVerificare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Verificare button clicked");
                if (isAnswerChecked) {
                    continueToNextStep();
                } else {
                    checkAnswer();
                }
            }
        });

        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Finish button clicked");
                updateProgress();
                Intent intent = new Intent("REFRESH_DATA");
                LocalBroadcastManager.getInstance(NewActivity.this).sendBroadcast(intent);
                finish();
            }
        });

        findViewById(R.id.root_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                View view = getCurrentFocus();
                if (view != null && !(view instanceof EditText)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });
    }

    private void displayQuestion(Question question) {
        Log.d(TAG, "displayQuestion: Displaying question: " + question.questionText);
        if ("multiple-choice".equals(question.type)) {
            SecondFragment fragment = new SecondFragment();
            Bundle args = new Bundle();
            args.putString("question", question.questionText);
            args.putString("correctAnswer", question.correctAnswer);
            args.putString("otherAnswers", question.otherAnswers);
            fragment.setArguments(args);
            replaceFragment(fragment);
        } else if ("single-choice".equals(question.type)) {
            FirstFragment fragment = new FirstFragment();
            Bundle args = new Bundle();
            args.putString("question", question.questionText);
            fragment.setArguments(args);
            replaceFragment(fragment);
        }
    }

    private void replaceFragment(Fragment fragment) {
        Log.d(TAG, "replaceFragment: Replacing current fragment with new fragment");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    private void checkAnswer() {
        Log.d(TAG, "checkAnswer: Checking answer for current question");
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        boolean isCorrect = false;
        String givenAnswer = null;  // Initialize as null
        String correctAnswer = questions.get(currentQuestionIndex).correctAnswer;

        if (currentFragment instanceof FirstFragment) {
            FirstFragment firstFragment = (FirstFragment) currentFragment;
            givenAnswer = firstFragment.getAnswer();
            if (givenAnswer == null || givenAnswer.trim().isEmpty()) {
                resultTextView.setText("Scrieți răspuns");
                resultTextView.setVisibility(View.VISIBLE);
                return;
            }
        } else if (currentFragment instanceof SecondFragment) {
            SecondFragment secondFragment = (SecondFragment) currentFragment;
            givenAnswer = secondFragment.getSelectedOption();
            if (givenAnswer == null || givenAnswer.trim().isEmpty()) {
                resultTextView.setText("Selectați răspunsul");
                resultTextView.setVisibility(View.VISIBLE);
                return;
            }
        }

        isCorrect = givenAnswer.equals(correctAnswer);

        Log.d(TAG, "checkAnswer: Given answer: " + givenAnswer + ", Correct answer: " + correctAnswer + ", Is correct: " + isCorrect);

        // Display the result in the TextView
        resultTextView.setText(isCorrect ? "Correct" : "Wrong");
        resultTextView.setVisibility(View.VISIBLE);

        // If the answer is wrong, add the question to the wrongQuestions list
        if (!isCorrect) {
            Log.d(TAG, "checkAnswer: Answer is wrong, adding question to wrongQuestions list");
            wrongQuestions.add(questions.get(currentQuestionIndex));
        }

        // Change the button text to "Continua"
        buttonVerificare.setText("Continua");
        isAnswerChecked = true;
        isLastAnswerCorrect = isCorrect;
    }

    private void continueToNextStep() {
        Log.d(TAG, "continueToNextStep: Continuing to next step");

        // Hide the result text view
        resultTextView.setVisibility(View.GONE);

        if (isLastAnswerCorrect) {
            progress += 10;
            progressBar.setProgress(progress);
        }

        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            displayQuestion(questions.get(currentQuestionIndex));
        } else if (!wrongQuestions.isEmpty()) {
            questions = new ArrayList<>(wrongQuestions);
            wrongQuestions.clear();
            currentQuestionIndex = 0;
            // Bara de progres nu mai este resetată
            displayQuestion(questions.get(currentQuestionIndex));
        } else {
            // Afișăm mesajul de felicitare și butonul "Finish"
            Log.d(TAG, "continueToNextStep: Completed all standard questions, displaying congratulations message and finish button");
            congratulationsTextView.setVisibility(View.VISIBLE);
            buttonFinish.setVisibility(View.VISIBLE);
            buttonVerificare.setVisibility(View.GONE);

            // Ascundem FrameLayout-ul
            findViewById(R.id.frame_layout).setVisibility(View.GONE);
            return;
        }

        // Reset button text to "Verificare" for the next step
        buttonVerificare.setText("Verificare");
        isAnswerChecked = false;
    }

    private void updateProgress() {
        executorService.execute(() -> {
            ProgressDao progressDao = db.progressDao();
            List<Progress> progresses = progressDao.getProgressBySubcategoryId(subcategoryId);
            Progress progress;
            if (progresses.isEmpty()) {
                progress = new Progress();
                progress.subcategoryId = subcategoryId;
                progress.subcategoryName = subcategoryName;
                progress.completed = 1;
                progress.total = questions.size();
                Log.d(TAG, "updateProgress: Before update - Completed: 0, Total: " + progress.total);
                progressDao.insertAll(progress);
                Log.d(TAG, "updateProgress: After update - Completed: " + progress.completed + ", Total: " + progress.total);
            } else {
                progress = progresses.get(0);
                Log.d(TAG, "updateProgress: Before update - Completed: " + progress.completed + ", Total: " + progress.total);
                progress.completed++;
                progressDao.updateProgress(progress);
                Log.d(TAG, "updateProgress: After update - Completed: " + progress.completed + ", Total: " + progress.total);
            }
        });
    }
}
