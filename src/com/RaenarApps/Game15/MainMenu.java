package com.RaenarApps.Game15;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
                String imagePathGlobal;
                boolean isDefaultGlobal;
                Intent intent = new Intent(this, FifteenActivity.class);
                if ((resultCode == RESULT_OK) && (data.hasExtra(FifteenActivity.IMAGE_PATH))) {
                    imagePathGlobal = data.getStringExtra(FifteenActivity.IMAGE_PATH);
                    isDefaultGlobal = data.getBooleanExtra(FifteenActivity.IS_DEFAULT, false);
                } else {
                    imagePathGlobal = "backgrounds/pollen.jpg";
                    isDefaultGlobal = true;
                }
                intent.putExtra(FifteenActivity.IMAGE_PATH, imagePathGlobal);
                intent.putExtra(FifteenActivity.IS_DEFAULT, isDefaultGlobal);
                intent.putExtra(FifteenActivity.IS_NEW_GAME, true);
                setResult(RESULT_OK, intent);
                startActivityForResult(intent, REQUEST_UPDATE);
                break;
            case REQUEST_UPDATE:
                updateMenu();
                break;
        }
    }
}
