package myusarisoy.solarhomesystem.ThemeSelector;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesTheme {
    SharedPreferences sharedPreferences;

    public SharedPreferencesTheme(Context context) {
        sharedPreferences = context.getSharedPreferences("theme", Context.MODE_PRIVATE);
    }

    public void setNightModeState(Boolean state) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("DarkTheme", state);
        editor.commit();
    }

    public void setLightModeState(Boolean state) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("AppTheme", state);
        editor.commit();
    }


    public boolean loadNightModeState() {
        Boolean state = sharedPreferences.getBoolean("DarkTheme", false);
        return state;
    }


    public boolean loadLightModeState() {
        Boolean state = sharedPreferences.getBoolean("AppTheme", false);
        return state;
    }
}
