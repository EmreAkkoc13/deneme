package com.example.halkaarzkarhesaplama;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CalculatorActivity extends AppCompatActivity {

    private TextView tvResult;
    private StringBuilder input = new StringBuilder();
    private String operator = "";
    private double valueOne = Double.NaN;
    private double valueTwo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        tvResult = findViewById(R.id.tvResult);

        setupButtons();
    }

    private void setupButtons() {
        // Initialize buttons
        int[] buttonIds = new int[]{
                R.id.btnClear, R.id.btnDivide, R.id.btnMultiply, R.id.btnDelete,
                R.id.btn7,     R.id.btn8,      R.id.btn9,        R.id.btnSubtract,
                R.id.btn4,     R.id.btn5,      R.id.btn6,        R.id.btnAdd,
                R.id.btn1,     R.id.btn2,      R.id.btn3,        R.id.btnEquals,
                R.id.btn0,     R.id.btnDot
        };

        for (int id : buttonIds) {
            Button button = findViewById(id);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleButtonClick(((Button) v).getText().toString());
                }
            });
        }
    }

    private void handleButtonClick(String text) {
        if (text.equals("C")) {
            input.setLength(0); // Input'u sıfırla
            tvResult.setText("");
            valueOne = Double.NaN; // İlk sayıyı sıfırla
            operator = ""; // Operatörü sıfırla
        } else if (text.equals("DEL")) {
            if (input.length() > 0) {
                input.deleteCharAt(input.length() - 1); // Son karakteri sil
                tvResult.setText(input.toString());
            }
        } else if (text.equals("=")) {
            if (!Double.isNaN(valueOne) && !operator.isEmpty()) {
                valueTwo = Double.parseDouble(input.toString()); // İkinci sayıyı al
                double result = calculate(valueOne, valueTwo, operator); // Hesapla
                tvResult.setText(String.valueOf(result)); // Sonucu göster
                input.setLength(0); // Input'u sıfırla
                input.append(result); // Sonucu input'a yaz
                valueOne = result; // Sonuç yeni birinci sayı olacak
                operator = ""; // Operatörü sıfırla
            }
        } else if (text.equals("+") || text.equals("-") || text.equals("*") || text.equals("/")) {
            if (!Double.isNaN(valueOne)) {
                if (input.length() > 0) {
                    valueTwo = Double.parseDouble(input.toString()); // İkinci sayıyı al
                    valueOne = calculate(valueOne, valueTwo, operator); // Hesapla
                }
            } else {
                valueOne = Double.parseDouble(input.toString()); // İlk sayıyı al
            }
            operator = text; // Operatörü güncelle
            input.append(" " + operator + " "); // Operatörü ekranda göster
            tvResult.setText(input.toString());
            input.setLength(0); // Input'u sıfırla, yeni sayı bekleniyor
        } else {
            input.append(text); // Sayıyı ekle
            tvResult.setText(input.toString());
        }
    }


    private double calculate(double valueOne, double valueTwo, String operator) {
        switch (operator) {
            case "+":
                return valueOne + valueTwo;
            case "-":
                return valueOne - valueTwo;
            case "*":
                return valueOne * valueTwo;
            case "/":
                if (valueTwo != 0) {
                    return valueOne / valueTwo;
                } else {
                    return Double.NaN; // Division by zero error
                }
            default:
                return valueTwo;
        }
    }
}
