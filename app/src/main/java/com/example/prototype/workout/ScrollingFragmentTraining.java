package com.example.prototype.workout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.prototype.Api.Dto.workout.TrainingDto;
import com.example.prototype.R;

import java.io.Serializable;

public class ScrollingFragmentTraining extends Fragment {
    private TextView textName;
    private ImageView imageView;
    private TextView tvDurOrAmount;
    private TextView tvDescription;

    private TrainingDto currentTraining;
    private LinearLayout llTimer;
    private TextView textViewTimer;
    private ImageButton buttonStart, buttonPause, buttonReset;
    private long timeLeftInMillis;

    private long remainingTimeMillis = 0;
    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer;

    public static ScrollingFragmentTraining newInstance(TrainingDto trainingDto) {
        ScrollingFragmentTraining fragment = new ScrollingFragmentTraining();
        Bundle args = new Bundle();
        args.putSerializable("trainingDto", (Serializable) trainingDto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentTraining = (TrainingDto) getArguments().getSerializable("trainingDto");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scrolling_training, container, false);

        textName = view.findViewById(R.id.tvName);
        imageView = view.findViewById(R.id.imageView);
        tvDurOrAmount = view.findViewById(R.id.tvDurOrAmount);
        tvDescription = view.findViewById(R.id.tvDescription);

        llTimer = view.findViewById(R.id.llTimer);
        textViewTimer = view.findViewById(R.id.textViewTimer);
        buttonStart = view.findViewById(R.id.buttonStart);
        buttonPause = view.findViewById(R.id.buttonPause);
        buttonReset = view.findViewById(R.id.buttonReset);
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.timer);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });
        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
            }
        });
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        Button nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityForExercises) requireActivity()).showNextFragment();
            }
        });

        fullTraining();


        return view;
    }

    private void fullTraining() {
        textName.setText(currentTraining.getName());
        setImageFromString(currentTraining.getImage());
        String durOrAmount = "";
        if (currentTraining.getDuration() != null) {
            durOrAmount += "Время выполнения - " + String.format("%02d:%02d", currentTraining.getDuration()[0], currentTraining.getDuration()[1]);
        }
        if (currentTraining.getAmount() != null) {
            if (!durOrAmount.equals("")) durOrAmount += "\n";
            durOrAmount += currentTraining.getAmount();
        }
        tvDurOrAmount.setText(durOrAmount);
        tvDescription.setText(currentTraining.getDescription());

        if (currentTraining.getDuration() != null) {
            llTimer.setVisibility(View.VISIBLE);
            timeLeftInMillis = (currentTraining.getDuration()[0] * 60 * 1000) + (currentTraining.getDuration()[1] * 1000);
            updateTimer();
        }
    }

    public void setImageFromString(String base64Image) {
        byte[] imageByte = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap restoredBitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        imageView.setImageBitmap(restoredBitmap);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                // Выполняется по завершению таймера
                timeLeftInMillis = 0;
                updateTimer();
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
        }.start();
        buttonStart.setEnabled(false);
        buttonPause.setEnabled(true);
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            buttonStart.setEnabled(true);
            buttonPause.setEnabled(false);
        }
    }

    private void resetTimer() {
        pauseTimer();
        timeLeftInMillis = (currentTraining.getDuration()[0] * 60 * 1000) + (currentTraining.getDuration()[1] * 1000);
        updateTimer();
        startTimer();
    }

    private void updateTimer() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        textViewTimer.setText(timeLeftFormatted);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}