package com.RaenarApps.Game15.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.RaenarApps.Game15.model.Image;
import com.RaenarApps.Game15.R;

/**
 * Created by Raenar on 07.08.2015.
 */
public class MainMenu extends Activity {

    public static final int REQUEST_UPDATE = 2; //Code for updating Menu UI. So that "Continue" button will be enabled

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        updateMenu();
    }

    private void updateMenu() {
        Button buttonContinue = (Button) findViewById(R.id.bttnContinue);
        SharedPreferences sharedPreferences = getSharedPreferences(FifteenActivity.PREFERENCES, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(FifteenActivity.IS_NEW_GAME, true)) {
            buttonContinue.setVisibility(View.INVISIBLE);
        } else {
            buttonContinue.setVisibility(View.VISIBLE);
        }
    }

    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.bttnContinue:
                Intent intent = new Intent(this, FifteenActivity.class);
                intent.putExtra(FifteenActivity.IS_NEW_GAME, false);
                setResult(RESULT_OK, intent);
                startActivity(intent);
                break;
            case R.id.bttnNewGame:
                intent = new Intent(this, ListActivity.class);
                startActivityForResult(intent, FifteenActivity.RC_List);
                break;
            case R.id.bttnHowTo:
                intent = new Intent(this, SlideActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FifteenActivity.RC_List:
                String imagePath;
                boolean isDefault;
                boolean isProcessed;
                String dominantColor;
                Intent intent = new Intent(this, FifteenActivity.class);
                if ((resultCode == RESULT_OK) && (data.hasExtra(Image.IMAGE_PATH))) {
                    imagePath = data.getStringExtra(Image.IMAGE_PATH);
                    isDefault = data.getBooleanExtra(Image.IS_DEFAULT, false);
                    isProcessed = data.getBooleanExtra(Image.IS_PROCESSED, false);
                    dominantColor = data.getStringExtra(Image.DOMINANT_COLOR);
                    intent.putExtra(Image.IMAGE_PATH, imagePath);
                    intent.putExtra(Image.IS_DEFAULT, isDefault);
                    intent.putExtra(Image.IS_PROCESSED, isProcessed);
                    intent.putExtra(Image.DOMINANT_COLOR, dominantColor);
                    intent.putExtra(FifteenActivity.IS_NEW_GAME, true);
                    setResult(RESULT_OK, intent);
                    startActivityForResult(intent, REQUEST_UPDATE);
                }
                break;
            case REQUEST_UPDATE:
                updateMenu();
                break;
        }
    }
}
