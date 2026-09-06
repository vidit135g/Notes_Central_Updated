package com.absolute.android.notescentral.helpers;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.absolute.android.notescentral.R;
import com.absolute.android.notescentral.models.listeners.OnPermissionRequestedListener;
import com.google.android.material.snackbar.Snackbar;

public class PermissionsHelper {

    public static void requestPermission(Activity activity, String permission, int rationaleDescription,
                                         View messageView, OnPermissionRequestedListener onPermissionRequestedListener) {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                Snackbar.make(messageView, rationaleDescription, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok, view -> {
                            requestPermissionExecute(activity, permission, onPermissionRequestedListener, messageView);
                        })
                        .show();
            } else {
                requestPermissionExecute(activity, permission, onPermissionRequestedListener, messageView);
            }
        } else {
            if (onPermissionRequestedListener != null) {
                onPermissionRequestedListener.onPermissionGranted();
            }
        }
    }

    private static void requestPermissionExecute(Activity activity, String permission,
                                                 OnPermissionRequestedListener onPermissionRequestedListener,
                                                 View messageView) {
        if (activity instanceof FragmentActivity) {
            FragmentActivity fa = (FragmentActivity) activity;
            fa.getSupportFragmentManager().beginTransaction()
                    .add(new HeadlessPermissionFragment(permission, onPermissionRequestedListener, messageView), "perm_" + System.currentTimeMillis())
                    .commitAllowingStateLoss();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, 101);
        }
    }

    public static class HeadlessPermissionFragment extends Fragment {
        private String permission;
        private OnPermissionRequestedListener listener;
        private View messageView;

        public HeadlessPermissionFragment() {
        }

        public HeadlessPermissionFragment(String permission, OnPermissionRequestedListener listener, View messageView) {
            this.permission = permission;
            this.listener = listener;
            this.messageView = messageView;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (permission != null) {
                requestPermissions(new String[]{permission}, 101);
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (requestCode == 101) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (listener != null) {
                        listener.onPermissionGranted();
                    }
                } else if (messageView != null && getContext() != null) {
                    String msg = getString(R.string.permission_not_granted) + ": " + permission;
                    Snackbar.make(messageView, msg, Snackbar.LENGTH_LONG).show();
                }
                if (getParentFragmentManager() != null) {
                    getParentFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
                }
            }
        }
    }
}
