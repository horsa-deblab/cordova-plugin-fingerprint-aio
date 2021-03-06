package de.niklasmerz.cordova.biometric;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class PromptInfo {

    private static final String DISABLE_BACKUP = "disableBackup";
    private static final String TITLE = "title";
    private static final String SUBTITLE = "subtitle";
    private static final String DESCRIPTION = "description";
    private static final String FALLBACK_BUTTON_TITLE = "fallbackButtonTitle";
    private static final String CANCEL_BUTTON_TITLE = "cancelButtonTitle";
    private static final String LOAD_SECRET = "loadSecret";
    private static final String INVALIDATE_ON_ENROLLMENT = "invalidateOnEnrollment";
    private static final String SECRET = "secret";
    private static final String CONFIRMATION_REQUIRED = "confirmationRequired";

    private Bundle bundle = new Bundle();

    Bundle getBundle() {
        return bundle;
    }

    String getTitle() {
        return bundle.getString(TITLE);
    }

    String getSubtitle() {
        return bundle.getString(SUBTITLE);
    }

    String getDescription() {
        return bundle.getString(DESCRIPTION);
    }

    boolean isDeviceCredentialAllowed() {
        return !bundle.getBoolean(DISABLE_BACKUP);
    }

    String getFallbackButtonTitle() {
        return bundle.getString(FALLBACK_BUTTON_TITLE);
    }

    String getCancelButtonTitle() {
        return bundle.getString(CANCEL_BUTTON_TITLE);
    }

    String getSecret() {
        return bundle.getString(SECRET);
    }

    boolean loadSecret() {
        return bundle.getBoolean(LOAD_SECRET);
    }

    boolean invalidateOnEnrollment() {
      return bundle.getBoolean(INVALIDATE_ON_ENROLLMENT);
    }

    boolean getConfirmationRequired() {
        return bundle.getBoolean(CONFIRMATION_REQUIRED);
    }

    public static final class Builder {
        private static final String TAG = "PromptInfo.Builder";
        private Bundle bundle;
        private boolean disableBackup = false;
        private String title;
        private String subtitle = null;
        private String description = null;
        private String fallbackButtonTitle = "Use backup";
        private String cancelButtonTitle = "Cancel";
        private boolean loadSecret = false;
        private boolean invalidateOnEnrollment = false;
        private String secret = null;
        private boolean confirmationRequired = true;

        Builder(Context context) {
            PackageManager packageManager = context.getPackageManager();
            try {
                ApplicationInfo app = packageManager
                        .getApplicationInfo(context.getPackageName(), 0);
                title = packageManager.getApplicationLabel(app) + " Biometric Sign On";
            } catch (PackageManager.NameNotFoundException e) {
                title = "Biometric Sign On";
            }
        }

        Builder(Bundle bundle) {
            this.bundle = bundle;
        }

        public PromptInfo build() {
            PromptInfo promptInfo = new PromptInfo();

            if (this.bundle != null) {
                promptInfo.bundle = bundle;
                return promptInfo;
            }

            Bundle bundle = new Bundle();
            bundle.putString(SUBTITLE, this.subtitle);
            bundle.putString(TITLE, this.title);
            bundle.putString(DESCRIPTION, this.description);
            bundle.putString(FALLBACK_BUTTON_TITLE, this.fallbackButtonTitle);
            bundle.putString(CANCEL_BUTTON_TITLE, this.cancelButtonTitle);
            bundle.putString(SECRET, this.secret);
            bundle.putBoolean(DISABLE_BACKUP, this.disableBackup);
            bundle.putBoolean(INVALIDATE_ON_ENROLLMENT, this.invalidateOnEnrollment);
            bundle.putBoolean(LOAD_SECRET, this.loadSecret);
            bundle.putBoolean(CONFIRMATION_REQUIRED, this.confirmationRequired);
            promptInfo.bundle = bundle;

            return promptInfo;
        }

        void parseArgs(JSONArray jsonArgs) throws JSONException {
          Args args = new Args(jsonArgs);
          disableBackup = args.getBoolean(DISABLE_BACKUP, disableBackup);
          title = args.getString(TITLE, title);
          subtitle = args.getString(SUBTITLE, subtitle);
          description = args.getString(DESCRIPTION, description);
          fallbackButtonTitle = args.getString(FALLBACK_BUTTON_TITLE, "Use Backup");
          cancelButtonTitle = args.getString(CANCEL_BUTTON_TITLE, "Cancel");
          loadSecret = args.getBoolean(LOAD_SECRET, false);
          invalidateOnEnrollment = args.getBoolean(INVALIDATE_ON_ENROLLMENT, false);
          secret = args.getString(SECRET, null);
        }

        private Boolean getBooleanArg(JSONObject argsObject, String name, Boolean defaultValue) {
            if (argsObject.has(name)){
                try {
                    return argsObject.getBoolean(name);
                } catch (JSONException e) {
                    Log.e(TAG, "Can't parse '" + name + "'. Default will be used.", e);
                }
            }
            return defaultValue;
        }

        private String getStringArg(JSONObject argsObject, String name, String defaultValue) {
            if (argsObject.optString(name) != null
                    && !argsObject.optString(name).isEmpty()){
                try {
                    return argsObject.getString(name);
                } catch (JSONException e) {
                    Log.e(TAG, "Can't parse '" + name + "'. Default will be used.", e);
                }
            }
            return defaultValue;
        }
    }
}
