package com.absolute.android.notescentral.helpers;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.os.Build;
import android.Manifest;
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
        
        if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (onPermissionRequestedListener != null) {
                onPermissionRequestedListener.onPermissionGranted();
            }
            return;
        }
        
        if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (onPermissionRequestedListener != null) {
                onPermissionRequestedListener.onPermissionGranted();
            }
            return;
        }

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
    public static void requestPermissions(Activity activity, String[] permissions, int rationaleDescription,
                                          View messageView, OnPermissionRequestedListener onPermissionRequestedListener) {
        boolean allGranted = true;
        boolean shouldShowRationale = false;
        
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, perm)) {
                    shouldShowRationale = true;
                }
            }
        }
        
        if (!allGranted) {
            if (shouldShowRationale) {
                Snackbar.make(messageView, rationaleDescription, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok, view -> {
                            requestPermissionsExecute(activity, permissions, onPermissionRequestedListener, messageView);
                        })
                        .show();
            } else {
                requestPermissionsExecute(activity, permissions, onPermissionRequestedListener, messageView);
            }
        } else {
            if (onPermissionRequestedListener != null) {
                onPermissionRequestedListener.onPermissionGranted();
            }
        }
    }

    private static void requestPermissionsExecute(Activity activity, String[] permissions,
                                                  OnPermissionRequestedListener onPermissionRequestedListener,
                                                  View messageView) {
        if (activity instanceof FragmentActivity) {
            FragmentActivity fa = (FragmentActivity) activity;
            fa.getSupportFragmentManager().beginTransaction()
                    .add(new HeadlessPermissionsFragment(permissions, onPermissionRequestedListener, messageView), "perms_" + System.currentTimeMillis())
                    .commitAllowingStateLoss();
        } else {
            ActivityCompat.requestPermissions(activity, permissions, 102);
        }
    }

    public static class HeadlessPermissionsFragment extends Fragment {
        private String[] permissions;
        private OnPermissionRequestedListener listener;
        private View messageView;

        public HeadlessPermissionsFragment() {
        }

        public HeadlessPermissionsFragment(String[] permissions, OnPermissionRequestedListener listener, View messageView) {
            this.permissions = permissions;
            this.listener = listener;
            this.messageView = messageView;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (permissions != null) {
                requestPermissions(permissions, 102);
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (requestCode == 102) {
                boolean allGranted = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                        break;
                    }
                }
                
                if (grantResults.length > 0 && allGranted) {
                    if (listener != null) {
                        listener.onPermissionGranted();
                    }
                } else if (messageView != null && getContext() != null) {
                    String msg = getString(R.string.permission_not_granted) + ": " + java.util.Arrays.toString(this.permissions);
                    Snackbar.make(messageView, msg, Snackbar.LENGTH_LONG).show();
                }
                if (getParentFragmentManager() != null) {
                    getParentFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
                }
            }
        }
    }
}
