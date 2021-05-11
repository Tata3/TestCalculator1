package com.example.testcalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    double part1; // для введенных чисел
    double part2;
    double result; // результат
    boolean isCalculated; // флаг готовности вычисления
    boolean haveValue; // флаг существования числа
    int stepNumber; //
    int operationType; // 1 +, 2 -, 3 *, 4 /
    String oldLine;
    boolean longPress; // флаги и задачи для контроля выполнения условий открытия меню
    boolean longStart;
    Timer menuTimer;
    PressTimerTask pressTimerTask;
    MenuTimerTask menuTimerTask;
    ViewFlipper menuFlipper; // переключение между калькулятором и меню

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("START", "onCreate started");
        setContentView(R.layout.activity_main);
        longPress = false;
        longStart = false;
        menuFlipper = (ViewFlipper)  findViewById(R.id.adapterViewFlipper);
        textView = (TextView) findViewById(R.id.textView);
        textView.setText("");
        ((Button) findViewById(R.id.result_button)).setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                Log.d("CLK", "onLongClick started");
                if(!longStart) {
                    longStart = true;
                    if (menuTimer != null) {
                        menuTimer.cancel();
                    }
                    menuTimer = new Timer();
                    pressTimerTask = new PressTimerTask();
                    menuTimer.schedule(pressTimerTask, 4000); // 4000 ms = 4 s
                }
                Log.d("CLK", "onLongClick finished");
                return true;
            }
        });
        part1 = 0; // очистка данных
        part2 = 0;
        result = 0;
        stepNumber = 0;
        isCalculated = true;
        haveValue = false;
        operationType = 0; // incorrect / not chosen
        oldLine = "";
        Log.d("START", "onCreate finished");
    }

    public void clearAll (View v){ // очистка данных
        part1 = 0;
        part2 = 0;
        result = 0;
        stepNumber = 0;
        isCalculated = true;
        haveValue = false;
        operationType = 0; // incorrect / not chosen
        oldLine = "";
        textView.setText("");
    }
    public void showResult (View v){ // вычисление и вывод результата
        Log.d("CALC", "Clicked! Is calculated - " + Boolean.toString(isCalculated) + ", have value - " + Boolean.toString(haveValue));
        if (stepNumber > 0 && haveValue) {
            switch (operationType) {
                case 1: // +
                    result = part1 + part2;
                    isCalculated = true;
                    break;
                case 2: // -
                    result = part1 - part2;
                    isCalculated = true;
                    break;
                case 3: // *
                    result = part1 * part2;
                    isCalculated = true;
                    break;
                case 4: // /
                    result = part1 / part2;
                    isCalculated = true;
                    break;
            }
            if (isCalculated) { // вывести результат, если вычисление было выполнено
                textView.append("\n = \n" + Double.toString(result));
                part1 = 0;
                part2 = 0;
                oldLine = "";
                operationType = 0;
                stepNumber = 0;
            }
        }
    }
    public void onDigitClicked(View v){ // реакция на выбор цифры
        switch(v.getId()){
            case R.id.digit0_button:
                addDigitToValue(0);
                break;
            case R.id.digit1_button:
                addDigitToValue(1);
                break;
            case R.id.digit2_button:
                addDigitToValue(2);
                break;
            case R.id.digit3_button:
                addDigitToValue(3);
                break;
            case R.id.digit4_button:
                addDigitToValue(4);
                break;
            case R.id.digit5_button:
                addDigitToValue(5);
                break;
            case R.id.digit6_button:
                addDigitToValue(6);
                break;
            case R.id.digit7_button:
                addDigitToValue(7);
                break;
            case R.id.digit8_button:
                addDigitToValue(8);
                break;
            case R.id.digit9_button:
                addDigitToValue(9);
                break;
        }
        if(longPress){
            if(textView.getText().toString().contains("123")){
                menuFlipper.showNext();
            }
        }
    }

    public void returnPressed(View v){ // выход из окна меню
        menuFlipper.showPrevious();
    }

    public void addDigitToValue (int digit){ // выбор и модификация операнда
        if(stepNumber == 0){
            part1 = addDigit(part1, digit);
            textView.setText(Double.toString(part1));
        }
        else{
            part2 = addDigit(part2, digit);
            textView.setText(oldLine + Double.toString(part2));
        }
        haveValue = true;
    }

    public double addDigit (double val, int digit){
        if(val < 100000000000000000000000d) // не модифицировать, если значение больше заданного
            val = val * 10 + digit;
        return val;
    }

    public void onDivClicked (View v){ // деление
        if(stepNumber == 0 && haveValue){ // проверка: должен быть введен только первый операнд
            operationType = 4;
            textView.append("\n / \n");
            stepNumber++;
            oldLine = textView.getText().toString();
            isCalculated = false;
            haveValue = false;
        }
    }

    public void onMulClicked (View v){ // умножение
        if(stepNumber == 0 && haveValue) {
            operationType = 3;
            textView.append("\n * \n");
            stepNumber++;
            oldLine = textView.getText().toString();
            isCalculated = false;
            haveValue = false;
        }
    }

    public void onMinusClicked (View v){ // вычитание
        if(stepNumber == 0 && haveValue) {
            operationType = 2;
            textView.append("\n - \n");
            stepNumber++;
            oldLine = textView.getText().toString();
            isCalculated = false;
            haveValue = false;
        }
    }

    public void onPlusClicked (View v){ // сложение
        if(stepNumber == 0 && haveValue) { //
            operationType = 1;
            textView.append("\n + \n");
            stepNumber++;
            oldLine = textView.getText().toString();
            isCalculated = false;
            haveValue = false;
        }
    }

    class PressTimerTask extends TimerTask { // определение долгого нажатия
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(longStart) {
                        Log.d("CLK", "can open menu");
                        longStart = false;
                        longPress = true;
                        textView.setText("");
                        part1 = 0;
                        part2 = 0;
                        result = 0;
                        stepNumber = 0;
                        isCalculated = true;
                        haveValue = false;
                        operationType = 0; // incorrect / not chosen
                        oldLine = "";
                        menuTimerTask = new MenuTimerTask(); // установка времени, в течение которого можно вызвать меню
                        menuTimer.schedule(menuTimerTask, 5000);
                    }
                }
            });
        }
    }

    class MenuTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() { // время на открытие меню истекло
                    Log.d("CLK", "can not open menu");
                    longPress = false;
                }
            });
        }
    }
}