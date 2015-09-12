package com.RaenarApps.Game15;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FifteenActivity extends Activity {

    public static final int ROW_COUNT = 4;
    public static final int COLUMN_COUNT = 4; //should be same as ROW_COUNT

    public static final int RC_List = 1; //Request code for ListActivity

    public static final String PREFERENCES = "Simple15prefs";

    public static final String TAG = "FIFTEEN ACTIVITY";
    //Various keys
    public static final String ARRAY = "Array";
    public static final String EMPTY_SPACE_X = "Empty space x";
    public static final String EMPTY_SPACE_Y = "Empty space y";
    public static final String IS_NEW_GAME = "IsNewGame";
    public static final String SIMPLE_MODE = "simpleMode";
    public static final String SHOW_NUMBERS = "showNumbers";

    private Button[][] buttons = new Button[ROW_COUNT][COLUMN_COUNT];
    private int[][] array = new int[ROW_COUNT][COLUMN_COUNT]; // tiles numbers
    private Point emptySpace = new Point(); // Empty tile coords

    // Chunks of original image are stored in the ArrayList as Bitmaps
    public ArrayList<Bitmap> chunkedImages = new ArrayList<Bitmap>(ROW_COUNT * COLUMN_COUNT);
    int textColor = Color.BLACK; //Color of a text on tiles. Can be changed in settings

    final String backgroundColorDefault = "#f1122e06";

    String imagePathGlobal;
    boolean isDefaultGlobal;
    boolean isProcessedGlobal;
    String dominantColorGlobal; // Can be changed to dominant color of the image

    boolean simpleMode = false; // True - simple tiles, false - chuncks of image
    boolean showNumbers = false; // True - show numbers on tiles
    boolean adaptiveBackground = true; // True - the background will be of dominant color of the image, false - def. color
    boolean gradientBackground = true; // True - the background will be a gradient of current color

    View cheatView;
    View tableView;
    private int animationDuration;
    boolean showPicture = false; // False - hint is not visible

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game15_v3);

        cheatView = findViewById(R.id.llCheatImage);
        tableView = findViewById(R.id.tableLayout);
        cheatView.setVisibility(View.GONE);
        animationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        initUI();
        Intent intent = getIntent();
        SharedPreferences sharedPreferences = getSharedPreferences(FifteenActivity.PREFERENCES, MODE_PRIVATE);
        boolean isNewGame = intent.getBooleanExtra(IS_NEW_GAME, true);
        if (isNewGame) {
            generateRandomArray();
            while (!isGameWinnable()) {
                generateRandomArray();
            }
            imagePathGlobal = intent.getStringExtra(Image.IMAGE_PATH);
            Log.d(TAG, "NewGame -> imagePathGlobal = "+imagePathGlobal);
            isDefaultGlobal = intent.getBooleanExtra(Image.IS_DEFAULT, false);
            isProcessedGlobal = intent.getBooleanExtra(Image.IS_PROCESSED, false);
            dominantColorGlobal = intent.getStringExtra(Image.DOMINANT_COLOR);
        } else {
            String arrayString = sharedPreferences.getString(FifteenActivity.ARRAY, "");
            StringTokenizer st = new StringTokenizer(arrayString, ",");
            for (int i = 0; i < FifteenActivity.ROW_COUNT; i++) {
                for (int j = 0; j < FifteenActivity.COLUMN_COUNT; j++) {
                    array[i][j] = Integer.parseInt(st.nextToken());
                }
            }
            emptySpace.setX(sharedPreferences.getInt(EMPTY_SPACE_X, 20));
            emptySpace.setY(sharedPreferences.getInt(EMPTY_SPACE_Y, 20));
            imagePathGlobal = sharedPreferences.getString(Image.IMAGE_PATH, "backgrounds/pollen.jpg");
            isDefaultGlobal = sharedPreferences.getBoolean(Image.IS_DEFAULT, true);
            isProcessedGlobal = sharedPreferences.getBoolean(Image.IS_PROCESSED, false);
            dominantColorGlobal = sharedPreferences.getString(Image.DOMINANT_COLOR, null);
            simpleMode = sharedPreferences.getBoolean(SIMPLE_MODE, false);
            showNumbers = sharedPreferences.getBoolean(SHOW_NUMBERS, false);
        }
        TaskLoadImage taskLoadImage = new TaskLoadImage(this, imagePathGlobal, isDefaultGlobal, isProcessedGlobal,
                chunkedImages, dominantColorGlobal);
        taskLoadImage.execute();
        setListenersOnButtons();
    }

    private boolean isGameWinnable() {
        //https://en.wikipedia.org/wiki/15_puzzle#Solvability
        int inversionsNumber = 0;
        int emptySpaceRow = -1;
        int previousNumber = array[0][0];
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                if ((i > 0) || (j > 0)) {
                    if (array[i][j] < previousNumber) inversionsNumber++;
                    previousNumber = array[i][j];
                }
                if (array[i][j] == -1) {
                    emptySpaceRow = i;
                }
            }
        }
        return ((inversionsNumber + emptySpaceRow) % 2 == 0);
    }

    private void setListenersOnButtons() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                buttons[i][j].setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Button clickedButton = (Button) view;
                        Point clickedPoint = getClickedPoint(clickedButton);
                        if (clickedPoint != null && canMove(clickedPoint)) {
                            //Swaping tile with empty space
                            String clickedButtonText = clickedButton.getText().toString();
                            clickedButton.setVisibility(View.INVISIBLE);
                            clickedButton.setText("0");

                            array[emptySpace.getX()][emptySpace.getY()] = array[clickedPoint.getX()][clickedPoint.getY()];
                            array[clickedPoint.getX()][clickedPoint.getY()] = -1;

                            buttons[emptySpace.getX()][emptySpace.getY()].setVisibility(View.VISIBLE);
                            Drawable clickedButtonBitmap = clickedButton.getBackground();
                            buttons[emptySpace.getX()][emptySpace.getY()].setBackground(clickedButtonBitmap);
                            buttons[emptySpace.getX()][emptySpace.getY()].setText(clickedButtonText);


                            emptySpace.setX(clickedPoint.getX());
                            emptySpace.setY(clickedPoint.getY());

                            if (isGameWon()) {
                                showWinDialog();
                            }
                        }
                    }
                });
            }
        }
    }


    private boolean isGameWon() {
        int k = 1;
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                if ((k < (ROW_COUNT * COLUMN_COUNT)) && (array[i][j] != k)) return false;
                k++;
            }
        }
        return true;
    }

    private Point getClickedPoint(Button clickedButton) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (clickedButton == buttons[i][j]) {
                    Point clickedPoint = new Point();
                    clickedPoint.setX(i);
                    clickedPoint.setY(j);
                    return clickedPoint;
                }
            }
        }
        return null;
    }


    private boolean canMove(Point clickedPoint) {
        if (clickedPoint.equals(emptySpace)) {
            return false;
        }

        if (clickedPoint.getX() == emptySpace.getX()) {
            int diff = Math.abs(clickedPoint.getY() - emptySpace.getY());
            if (diff == 1) {
                return true;
            }
        } else if (clickedPoint.getY() == emptySpace.getY()) {
            int diff = Math.abs(clickedPoint.getX() - emptySpace.getX());
            if (diff == 1) {
                return true;
            }
        }

        return false;
    }

    private void generateArray() {
        int k = 1; //Number of tile 1-15 (16 is invisible and its number is -1)
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                if (k >= (ROW_COUNT * COLUMN_COUNT)) {
                    emptySpace.setX(i);
                    emptySpace.setY(j);
                    array[i][j] = -1;
                } else {
                    array[i][j] = k;
                }
                k++;
            }
        }
    }

    private void generateRandomArray() {
        List<Integer> numbersList = new ArrayList<Integer>();
        for (int i = 1; i < (ROW_COUNT * COLUMN_COUNT) + 1; i++) {
            numbersList.add(i);
        }
        java.util.Collections.shuffle(numbersList);
//        Toast.makeText(this, numbersList.toString(), Toast.LENGTH_SHORT).show();
        int k;
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                k = i * ROW_COUNT + j;
                if (numbersList.get(k) >= (ROW_COUNT * COLUMN_COUNT)) {
                    emptySpace.setX(i);
                    emptySpace.setY(j);
                    array[i][j] = -1;
                } else {
                    array[i][j] = numbersList.get(k);
                }
            }
        }
    }

    public void updateUI() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int number = array[i][j];
                if (number > -1) {
                    if (simpleMode) {
                        buttons[i][j].setBackground(getResources().getDrawable(R.drawable.button15));
                    } else {
                        buttons[i][j].setBackground(new BitmapDrawable(getResources(), chunkedImages.get(number - 1)));
                    }
                    buttons[i][j].setVisibility(View.VISIBLE);
                    if (number > 9) {
                        buttons[i][j].setText("" + number);
                    } else {
                        buttons[i][j].setText(" " + number + " "); //To make sure tiles with 1-9 and 10-15 are same in size
                    }                                             // Otherwise the line with 4 numbers that are (1-9)
                    if (showNumbers) {                           // Will be thinner than the rest
                        buttons[i][j].setTextColor(textColor);
                    } else {
                        buttons[i][j].setTextColor(Color.TRANSPARENT);
                    }
                } else {
//                    buttons[i][j].setBackground(new BitmapDrawable(getResources(), chunkedImages.get(ROW_COUNT*COLUMN_COUNT-1)));
                    buttons[i][j].setBackground(getResources().getDrawable(R.drawable.button15));
                    buttons[i][j].setVisibility(View.INVISIBLE);
                    if (showNumbers) {
                        buttons[i][j].setTextColor(textColor);
                    } else {
                        buttons[i][j].setTextColor(Color.TRANSPARENT);
                    }
                    buttons[i][j].setText("0");
                }
            }
        }
        View someView = findViewById(R.id.llButtonsLine);
        View root = someView.getRootView();
        if (adaptiveBackground && dominantColorGlobal != null) {
            root.setBackgroundColor(Color.parseColor(dominantColorGlobal));
        } else {
            root.setBackgroundColor(Color.parseColor(backgroundColorDefault));
        }
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rlContain);
        if (gradientBackground) {
            relativeLayout.setBackground(getResources().getDrawable(R.drawable.gradient_background));
        } else {
            relativeLayout.setBackground(getResources().getDrawable(R.drawable.transparent_background));
        }
    }


    private void initUI() {
        buttons[0][0] = (Button) findViewById(R.id.button11);
        buttons[0][1] = (Button) findViewById(R.id.button12);
        buttons[0][2] = (Button) findViewById(R.id.button13);
        buttons[0][3] = (Button) findViewById(R.id.button14);

        buttons[1][0] = (Button) findViewById(R.id.button21);
        buttons[1][1] = (Button) findViewById(R.id.button22);
        buttons[1][2] = (Button) findViewById(R.id.button23);
        buttons[1][3] = (Button) findViewById(R.id.button24);

        buttons[2][0] = (Button) findViewById(R.id.button31);
        buttons[2][1] = (Button) findViewById(R.id.button32);
        buttons[2][2] = (Button) findViewById(R.id.button33);
        buttons[2][3] = (Button) findViewById(R.id.button34);

        buttons[3][0] = (Button) findViewById(R.id.button41);
        buttons[3][1] = (Button) findViewById(R.id.button42);
        buttons[3][2] = (Button) findViewById(R.id.button43);
        buttons[3][3] = (Button) findViewById(R.id.button44);
    }

    public void showHint(View view) {
//        generateArray();
//        updateUI();
        showPicture = !showPicture;
        cheat(showPicture);
    }

    public void backToMenu(View view) {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.simpleMode:
                simpleMode = true;
                showNumbers = true;
                updateUI();
                break;
            case R.id.imageMode:
                simpleMode = false;
                showNumbers = false;
                updateUI();
                break;
            case R.id.hybridMode:
                simpleMode = false;
                showNumbers = true;
                updateUI();
                break;
            case R.id.selectImage:
                openImageList();
                break;
            case R.id.adaptiveBackground:
                if (adaptiveBackground) {
                    adaptiveBackground = false;
                    gradientBackground = false;
                    item.setTitle(getString(R.string.oMenu_Adaptive_OFF));
                } else {
                    adaptiveBackground = true;
                    gradientBackground = true;
                    item.setTitle(R.string.oMenu_Adaptive_ON);
                }
                updateUI();
                break;
            case R.id.mixUp:
                generateRandomArray();
                while (!isGameWinnable()) {
                    generateRandomArray();
                }
//                generateArray();
                updateUI();
                break;
        }
        return true;
    }

    private void showWinDialog() {
        ContextThemeWrapper wrapper = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light);
        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.win_dialog, null));
        builder.setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.win_dialog_return), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cheat(boolean showPicture) {
        final View showView = showPicture ? cheatView : tableView;
        final View hideView = showPicture ? tableView : cheatView;

        showView.setAlpha(0f);
        showView.setVisibility(View.VISIBLE);
        showView.animate()
                .alpha(1f)
                .setDuration(animationDuration)
                .setListener(null);

        hideView.animate()
                .alpha(0f)
                .setDuration(animationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        hideView.setVisibility(View.GONE);
                    }
                });
    }

    private void openImageList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivityForResult(intent, RC_List);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_List:
                if ((resultCode == RESULT_OK) && (data.hasExtra(Image.IMAGE_PATH))) {
                    imagePathGlobal = data.getStringExtra(Image.IMAGE_PATH);
                    isDefaultGlobal = data.getBooleanExtra(Image.IS_DEFAULT, false);
                    isProcessedGlobal = data.getBooleanExtra(Image.IS_PROCESSED, false);
                    dominantColorGlobal = data.getStringExtra(Image.DOMINANT_COLOR);
                    TaskLoadImage taskLoadImage = new TaskLoadImage(this, imagePathGlobal, isDefaultGlobal, isProcessedGlobal,
                            chunkedImages, dominantColorGlobal);
                    taskLoadImage.execute();
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                str.append(array[i][j]).append(",");
            }
        }
        editor.putString(ARRAY, str.toString());
        editor.putInt(EMPTY_SPACE_X, emptySpace.getX());
        editor.putInt(EMPTY_SPACE_Y, emptySpace.getY());
        editor.putString(Image.IMAGE_PATH, imagePathGlobal);
        editor.putBoolean(Image.IS_DEFAULT, isDefaultGlobal);
        editor.putBoolean(Image.IS_PROCESSED, isProcessedGlobal);
        editor.putString(Image.DOMINANT_COLOR, dominantColorGlobal);
        editor.putBoolean(IS_NEW_GAME, false);
        editor.putBoolean(SIMPLE_MODE, simpleMode);
        editor.putBoolean(SHOW_NUMBERS, showNumbers);
        editor.apply();
    }

    public void options(View view) {
        this.openOptionsMenu();
    }
}