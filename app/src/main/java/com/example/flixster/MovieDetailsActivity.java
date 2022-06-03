package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MovieDetailsActivity extends YouTubeBaseActivity {

    YouTubePlayerView youTubePlayerView;

    public static final String TAG1 = "MovieDetailsActivity";

    Movie movie;

    TextView tvTitle;
    TextView tvOverview;
    ImageView Img;
    RatingBar rbVoteAverage;
    String videoKey;
    List<String> video = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);
        Img = (ImageView) findViewById(R.id.Image);


        // unwrap with parceler
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d(TAG1, String.format("Showing details for '%s'", movie.getTitle()));

        // set title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        int radius = 30; // corner radius, higher value = more rounded
        Glide.with(this)
                .load(movie.getBackdropPath())
                .centerCrop() // scale image to fill the entire ImageView
                .transform(new RoundedCorners(radius))
                .into(Img);

        // rating is from 0-10 so convert to 0-5
        float voteAvg = (float) movie.getVoteAverage();
        rbVoteAverage.setRating(voteAvg / 2.0f);

        // TEMPORARY TODO: replace with video id
        final String videoId = movie.getVideo();

        Log.d(TAG1, videoId);

        // resolve the player view from the layout
        YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.player);



        AsyncHttpClient client = new AsyncHttpClient();


        String URL = String.format("https://api.themoviedb.org/3/movie/%d/videos?api_key=%s&language=en-US", movie.getId(), getString(R.string.movie_api_key));
        client.get(URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG1, " before youtube stuff");
                Log.d(TAG1, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    Log.i(TAG1, "MADE IT TO BEFORE RESULTS");
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG1, "Results: " + results.toString());
//                    movies.addAll(Movie.fromJsonArray(results));
//                    movieAdapter.notifyDataSetChanged();
//                    Log.i(TAG, "Movies: " + movies.size());

                    // SETUP FOR VIDEOS
                    JSONObject jsonObject1 = results.getJSONObject(0);
                    videoKey = jsonObject1.getString("key");
                    System.out.println("vidKey: " + videoKey);

                    video.add(videoKey);

                    // initialize with API key stored in secrets.xml
                    playerView.initialize(getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                            YouTubePlayer youTubePlayer, boolean b) {
                            // do any work here to cue video, play video, etc.
                            Log.i("MVTRAiler", "made it here");
                            youTubePlayer.cueVideo(videoKey);
                            Log.i("MVTrailer", "done with cue");
                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                            YouTubeInitializationResult youTubeInitializationResult) {
                            // log the error
                            Log.e("MovieTrailerActivity", "Error initializing YouTube player");
                        }
                    });



                    //videoId = videoKey;





                } catch (JSONException e) {
                    Log.e(TAG1, "hit json exception ", e);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG1, "onFailure");
            }

        });

    }


}