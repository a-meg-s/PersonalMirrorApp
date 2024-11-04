// Generated by view binder compiler. Do not edit!
package com.example.uimirror.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.uimirror.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class SongItemBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextView artistName;

  @NonNull
  public final ImageButton playButton;

  @NonNull
  public final TextView songName;

  private SongItemBinding(@NonNull LinearLayout rootView, @NonNull TextView artistName,
      @NonNull ImageButton playButton, @NonNull TextView songName) {
    this.rootView = rootView;
    this.artistName = artistName;
    this.playButton = playButton;
    this.songName = songName;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static SongItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static SongItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.song_item, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static SongItemBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.artistName;
      TextView artistName = ViewBindings.findChildViewById(rootView, id);
      if (artistName == null) {
        break missingId;
      }

      id = R.id.playButton;
      ImageButton playButton = ViewBindings.findChildViewById(rootView, id);
      if (playButton == null) {
        break missingId;
      }

      id = R.id.songName;
      TextView songName = ViewBindings.findChildViewById(rootView, id);
      if (songName == null) {
        break missingId;
      }

      return new SongItemBinding((LinearLayout) rootView, artistName, playButton, songName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}