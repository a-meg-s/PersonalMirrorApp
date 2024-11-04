// Generated by view binder compiler. Do not edit!
package com.example.uimirror.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
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

public final class ActivityAlarmEditorBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final LinearLayout alarmContainer;

  @NonNull
  public final Button cancelButton;

  @NonNull
  public final PreviewView previewView;

  @NonNull
  public final Button setAlarmButton;

  @NonNull
  public final TextView textView;

  @NonNull
  public final TimePicker timePicker;

  private ActivityAlarmEditorBinding(@NonNull ConstraintLayout rootView,
      @NonNull LinearLayout alarmContainer, @NonNull Button cancelButton,
      @NonNull PreviewView previewView, @NonNull Button setAlarmButton, @NonNull TextView textView,
      @NonNull TimePicker timePicker) {
    this.rootView = rootView;
    this.alarmContainer = alarmContainer;
    this.cancelButton = cancelButton;
    this.previewView = previewView;
    this.setAlarmButton = setAlarmButton;
    this.textView = textView;
    this.timePicker = timePicker;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityAlarmEditorBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityAlarmEditorBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_alarm_editor, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityAlarmEditorBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.alarmContainer;
      LinearLayout alarmContainer = ViewBindings.findChildViewById(rootView, id);
      if (alarmContainer == null) {
        break missingId;
      }

      id = R.id.cancelButton;
      Button cancelButton = ViewBindings.findChildViewById(rootView, id);
      if (cancelButton == null) {
        break missingId;
      }

      id = R.id.previewView;
      PreviewView previewView = ViewBindings.findChildViewById(rootView, id);
      if (previewView == null) {
        break missingId;
      }

      id = R.id.setAlarmButton;
      Button setAlarmButton = ViewBindings.findChildViewById(rootView, id);
      if (setAlarmButton == null) {
        break missingId;
      }

      id = R.id.textView;
      TextView textView = ViewBindings.findChildViewById(rootView, id);
      if (textView == null) {
        break missingId;
      }

      id = R.id.timePicker;
      TimePicker timePicker = ViewBindings.findChildViewById(rootView, id);
      if (timePicker == null) {
        break missingId;
      }

      return new ActivityAlarmEditorBinding((ConstraintLayout) rootView, alarmContainer,
          cancelButton, previewView, setAlarmButton, textView, timePicker);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
