package com.msi.android.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.navigation.fragment.NavHostFragment;

import com.msi.android.App;
import com.msi.android.R;
import com.msi.android.data.api.ApiService;
import com.msi.android.data.dto.VideoStreamResponseDto;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoStreamFragment extends Fragment {

    @Inject
    ApiService apiService;


    private PlayerView playerView;
    private TextView tvCameraName;
    private TextView tvCameraInfo;
    private LinearLayout camerasListContainer;

    private ExoPlayer player;
    private List<VideoStreamResponseDto> streams;
    private int currentStreamIndex = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) requireActivity().getApplication())
                .getAppComponent()
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_stream, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null || !args.containsKey("constructionId")) {
            Log.e("VideoStreamFragment", "constructionId is null");
            NavHostFragment.findNavController(this).popBackStack();
            return;
        }

        String constructionId = args.getString("constructionId");

        playerView = view.findViewById(R.id.playerView);
        tvCameraName = view.findViewById(R.id.tvCameraName);
        tvCameraInfo = view.findViewById(R.id.tvCameraInfo);
        camerasListContainer = view.findViewById(R.id.camerasListContainer);

        player = new ExoPlayer.Builder(requireContext()).build();
        playerView.setPlayer(player);

        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack()
        );

        loadVideoStreams(constructionId);
    }

    private void loadVideoStreams(String constructionId) {

        apiService.getVideoStreams(constructionId)
                .enqueue(new Callback<List<VideoStreamResponseDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<VideoStreamResponseDto>> call,
                                           @NonNull Response<List<VideoStreamResponseDto>> response) {

                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            streams = response.body();
                            Log.d("VideoStreamFragment", "Loaded " + streams.size() + " streams");

                            displayStream(0);
                            createCameraButtons();
                        } else {
                            Toast.makeText(getContext(), "Видеопотоки не найдены", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<VideoStreamResponseDto>> call, @NonNull Throwable t) {
                        Log.e("VideoStreamFragment", "Error loading streams", t);
                        Toast.makeText(getContext(), "Ошибка загрузки видеопотоков", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayStream(int index) {
        currentStreamIndex = index;
        VideoStreamResponseDto stream = streams.get(index);

        tvCameraName.setText(stream.getName());

        String info = "Камера №" + (index + 1);
        if (stream.getDescription() != null) {
            info += " - " + stream.getDescription();
        }
        info += "\nОбновление: каждые 5 минут\nКачество: HD 1080p";
        tvCameraInfo.setText(info);

        player.stop();
        player.clearMediaItems();

        MediaItem mediaItem = MediaItem.fromUri(stream.getStreamUrl());
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();

        Log.d("VideoStreamFragment", "Playing test stream for camera: " + stream.getName());
    }

    private void createCameraButtons() {
        camerasListContainer.removeAllViews();

        for (int i = 0; i < streams.size(); i++) {
            final int index = i;

            com.google.android.material.button.MaterialButton button =
                    new com.google.android.material.button.MaterialButton(requireContext());
            button.setText("Камера " + (i + 1));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            button.setLayoutParams(params);

            if (i == currentStreamIndex) {
                button.setBackgroundColor(getResources().getColor(R.color.blue, null));
                button.setTextColor(getResources().getColor(android.R.color.white, null));
            } else {
                button.setBackgroundColor(getResources().getColor(R.color.md_theme_surface, null));
                button.setTextColor(getResources().getColor(R.color.black, null));
            }

            button.setOnClickListener(v -> {
                displayStream(index);
                createCameraButtons();
            });

            camerasListContainer.addView(button);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
