// Generated by view binder compiler. Do not edit!
package com.example.uimirror.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.uimirror.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.lang.Override;

public final class ActivityMainBinding implements ViewBinding {
  @NonNull
  private final View rootView;

  /**
   * This binding is not available in all configurations.
   * <p>
   * Present:
   * <ul>
   *   <li>layout/</li>
   * </ul>
   *
   * Absent:
   * <ul>
   *   <li>layout-land/</li>
   * </ul>
   */
  @Nullable
  public final FloatingActionButton alarmIcon;

  /**
   * This binding is not available in all configurations.
   * <p>
   * Present:
   * <ul>
   *   <li>layout/</li>
   * </ul>
   *
   * Absent:
   * <ul>
   *   <li>layout-land/</li>
   * </ul>
   */
  @Nullable
  public final FloatingActionButton calendarIcon;

  /**
   * This binding is not available in all configurations.
   * <p>
   * Present:
   * <ul>
   *   <li>layout-land/</li>
   * </ul>
   *
   * Absent:
   * <ul>
   *   <li>layout/</li>
   * </ul>
   */
  @Nullable
  public final FloatingActionButton fab;

  /**
   * This binding is not available in all configurations.
   * <p>
   * Present:
   * <ul>
   *   <li>layout/</li>
   * </ul>
   *
   * Absent:
   * <ul>
   *   <li>layout-land/</li>
   * </ul>
   */
  @Nullable
  public final LinearLayout iconContainer;

  /**
   * This binding is not available in all configurations.
   * <p>
   * Present:
   * <ul>
   *   <li>layout/</li>
   * </ul>
   *
   * Absent:
   * <ul>
   *   <li>layout-land/</li>
   * </ul>
   */
  @Nullable
  public final FloatingActionButton mailIcon;

  /**
   * This binding is not available in all configurations.
   * <p>
   * Present:
   * <ul>
   *   <li>layout/</li>
   * </ul>
   *
   * Absent:
   * <ul>
   *   <li>layout-land/</li>
   * </ul>
   */
  @Nullable
  public final FloatingActionButton playIcon;

  /**
   * This binding is not available in all configurations.
   * <p>
   * Present:
   * <ul>
   *   <li>layout/</li>
   * </ul>
   *
   * Absent:
   * <ul>
   *   <li>layout-land/</li>
   * </ul>
   */
  @Nullable
  public final PreviewView previewView;

  /**
   * This binding is not available in all configurations.
   * <p>
   * Present:
   * <ul>
   *   <li>layout/</li>
   * </ul>
   *
   * Absent:
   * <ul>
   *   <li>layout-land/</li>
   * </ul>
   */
  @Nullable
  public final FloatingActionButton settingsIcon;

  /**
   * This binding is not available in all configurations.
   * <p>
   * Present:
   * <ul>
   *   <li>layout-land/</li>
   * </ul>
   *
   * Absent:
   * <ul>
   *   <li>layout/</li>
   * </ul>
   */
  @Nullable
  public final MaterialToolbar toolbar;

  private ActivityMainBinding(@NonNull View rootView, @Nullable FloatingActionButton alarmIcon,
      @Nullable FloatingActionButton calendarIcon, @Nullable FloatingActionButton fab,
      @Nullable LinearLayout iconContainer, @Nullable FloatingActionButton mailIcon,
      @Nullable FloatingActionButton playIcon, @Nullable PreviewView previewView,
      @Nullable FloatingActionButton settingsIcon, @Nullable MaterialToolbar toolbar) {
    this.rootView = rootView;
    this.alarmIcon = alarmIcon;
    this.calendarIcon = calendarIcon;
    this.fab = fab;
    this.iconContainer = iconContainer;
    this.mailIcon = mailIcon;
    this.playIcon = playIcon;
    this.previewView = previewView;
    this.settingsIcon = settingsIcon;
    this.toolbar = toolbar;
  }

  @Override
  @NonNull
  public View getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainBinding bind(@NonNull View rootView) {
    FloatingActionButton alarmIcon = ViewBindings.findChildViewById(rootView, R.id.alarmIcon);

    FloatingActionButton calendarIcon = ViewBindings.findChildViewById(rootView, R.id.calendarIcon);

    FloatingActionButton fab = ViewBindings.findChildViewById(rootView, R.id.fab);

    LinearLayout iconContainer = ViewBindings.findChildViewById(rootView, R.id.iconContainer);

    FloatingActionButton mailIcon = ViewBindings.findChildViewById(rootView, R.id.mailIcon);

    FloatingActionButton playIcon = ViewBindings.findChildViewById(rootView, R.id.playIcon);

    PreviewView previewView = ViewBindings.findChildViewById(rootView, R.id.previewView);

    FloatingActionButton settingsIcon = ViewBindings.findChildViewById(rootView, R.id.settingsIcon);

    MaterialToolbar toolbar = ViewBindings.findChildViewById(rootView, R.id.toolbar);

    return new ActivityMainBinding(rootView, alarmIcon, calendarIcon, fab, iconContainer, mailIcon,
        playIcon, previewView, settingsIcon, toolbar);
  }
}
