// Generated by view binder compiler. Do not edit!
package com.example.uimirror.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.uimirror.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityGreetingBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextView greetingTextView;

  @NonNull
  public final PreviewView previewView;

  private ActivityGreetingBinding(@NonNull ConstraintLayout rootView,
      @NonNull TextView greetingTextView, @NonNull PreviewView previewView) {
    this.rootView = rootView;
    this.greetingTextView = greetingTextView;
    this.previewView = previewView;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityGreetingBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityGreetingBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_greeting, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityGreetingBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.greetingTextView;
      TextView greetingTextView = ViewBindings.findChildViewById(rootView, id);
      if (greetingTextView == null) {
        break missingId;
      }

      id = R.id.previewView;
      PreviewView previewView = ViewBindings.findChildViewById(rootView, id);
      if (previewView == null) {
        break missingId;
      }

      return new ActivityGreetingBinding((ConstraintLayout) rootView, greetingTextView,
          previewView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
