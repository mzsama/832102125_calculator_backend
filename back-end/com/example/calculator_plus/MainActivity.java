package com.example.calculator_plus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private TextView text;
    private StringBuilder str = new StringBuilder();
    private int indexYN = 0;
    private MySqliteHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editView);
        text = findViewById(R.id.textView);
        dbHelper = new MySqliteHelper(this);
        db = dbHelper.getWritableDatabase();
    }
    public void getRecentResults(View view) {
        // Retrieve the last 10 results from the database
        List<String> recentResults = dbHelper.getRecentResults();

        // Display or use recentResults as needed
    }
    public void clickButton(View view) {
        Button button = (Button) view;
        editText.append(button.getText());
        if(button.getText().equals("-")) {
            str.append("0" + button.getText());
            editText.append("0"+button.getText());
        } else{
            str.append(button.getText());
        }
    }

    public void empty(View view) {
        editText.setText(null);
        str.setLength(0);
    }
    public void delete(View view) {
        String nowText = editText.getText().toString();
        if (!nowText.isEmpty() && str.length()!=0) {
            editText.setText(nowText.substring(0, nowText.length() - 1));
            str.deleteCharAt(str.length() - 1);

        }
    }

    public void History(View view){
      view.setOnClickListener(v -> {
          Intent intent = new Intent(MainActivity.this, ShowActivity.class);
          startActivity(intent);

      });
    }
    public void equal(View view) {
        indexYN = 0;
        estimate();
        if (indexYN == 0) {
            List<String> zhongZhui = zhuanZhongZhui(str.toString());
            List<String> houZhui = zhuanHouZhui(zhongZhui);
            double result = math(houZhui);
            if (result != -999999) {
                // 将结果插入数据库
                if(MySqliteHelper.num>10) {
                    dbHelper.deleteOne();
                }
                dbHelper.insertResult(String.valueOf(result));
                editText.append("\n" + result);
                str.setLength(0);
                str.append(result);
            }

        }
    }

    public void reciprocal(View view) {
        editText.append("1/");
        str.append("1/");
    }

    public void factorial(View view) {
        editText.append("!");
        str.append("!");
    }

    public void square(View view) {
        editText.append("^2");
        str.append("^2");
    }

    public void cube(View view) {
        editText.append("^3");
        str.append("^3");
    }

    public void power(View view) {
        editText.append("^");
        str.append("^");
    }

    public void squareRoot(View view) {
        editText.append("√");
        str.append("g");
    }

    public void eulerNumber(View view) {
        editText.append("e");
        str.append("e");
    }

    public void percentage(View view) {
        editText.append("%");
        str.append("*0.01");
    }

    public void pi(View view) {
        editText.append("π");
        str.append("p");
    }

    public void sin(View view) {
        editText.append("sin");
        str.append("s");
    }

    public void cos(View view) {
        editText.append("cos");
        str.append("c");
    }

    public void tan(View view) {
        editText.append("tan");
        str.append("t");
    }

    public void ln(View view) {
        editText.append("ln");
        str.append("l");
    }

    public void log(View view) {
        editText.append("log");
        str.append("o");
    }
    public void getPrev(View view){
        MySqliteHelper dbHelper = new MySqliteHelper(this);
        String temp = dbHelper.recur(str.toString());
        if(!temp.equals("NULL")){
        str = new StringBuilder(temp);
        editText.setText(str);
        }
        else{
            Toast.makeText(getApplicationContext(),"无上一次数据",Toast.LENGTH_SHORT).show();
        }

    }

    private List<String> zhuanZhongZhui(String str) {
        int index = 0;
        List<String> list = new ArrayList<>();
        do {
            char ch = str.charAt(index);
            if ("+-*/^!logsct()".indexOf(ch) >= 0) {
                index++;
                list.add(ch + "");
            } else if (ch == 'e' || ch == 'p') {
                index++;
                list.add(ch + "");
            } else if ("0123456789".indexOf(ch) >= 0) {
                String str1 = "";
                while (index < str.length() && "0123456789.".indexOf(str.charAt(index)) >= 0) {
                    str1 += str.charAt(index);
                    index++;
                }
                list.add(str1);
            }
        } while (index < str.length());
        return list;
    }

    public List<String> zhuanHouZhui(List<String> list) {
        Stack<String> fuZhan = new Stack<>();
        List<String> list2 = new ArrayList<>();
        if (!list.isEmpty()) {
            for (String item : list) {
                if (isNumber(item)) {
                    list2.add(item);
                } else if (isOperator(item) || item.charAt(0) == '(' || item.charAt(0) == ')') {
                    if (item.charAt(0) == '(') {
                        fuZhan.push(item);
                    } else if (item.charAt(0) == ')') {
                        while (!fuZhan.isEmpty() && !fuZhan.peek().equals("(")) {
                            list2.add(fuZhan.pop());
                        }
                        if (!fuZhan.isEmpty() && fuZhan.peek().equals("(")) {
                            fuZhan.pop(); // 弹出左括号
                        }
                    } else {
                        while (!fuZhan.isEmpty() && adv(fuZhan.peek()) >= adv(item)) {
                            list2.add(fuZhan.pop());
                        }
                        fuZhan.push(item);
                    }
                }
            }

            while (!fuZhan.isEmpty()) {
                list2.add(fuZhan.pop());
            }
        } else {
            editText.setText("");
        }
        return list2;
    }

    public static boolean isOperator(String op) {
        return "0123456789.ep".indexOf(op.charAt(0)) == -1;
    }

    public static boolean isNumber(String num) {
        return "0123456789ep".indexOf(num.charAt(0)) >= 0;
    }

    public static int adv(String f) {
        int result = 0;
        switch (f) {
            case "+":
            case "-":
                result = 1;
                break;
            case "*":
            case "/":
                result = 2;
                break;
            case "^":
            case "!":
            case "g":
            case "l":
            case "o":
            case "s":
            case "c":
            case "t":
                result = 3;
                break;
        }
        return result;
    }

    public double math(List<String> list2) {
        Stack<String> stack = new Stack<>();
        for (String item : list2) {
            if (isNumber(item)) {
                if (item.charAt(0) == 'e') {
                    stack.push(String.valueOf(Math.E));
                } else if (item.charAt(0) == 'p') {
                    stack.push(String.valueOf(Math.PI));
                } else {
                    stack.push(item);
                }
            } else if (isOperator(item)) {
                double res = 0;
                if (item.equals("+")) {
                    double num2 = Double.parseDouble(stack.pop());
                    double num1 = Double.parseDouble(stack.pop());
                    res = num1 + num2;
                } else if (item.equals("-")) {
                    double num2 = Double.parseDouble(stack.pop());
                    double num1 = Double.parseDouble(stack.pop());
                    res = num1 - num2;
                } else if (item.equals("*")) {
                    double num2 = Double.parseDouble(stack.pop());
                    double num1 = Double.parseDouble(stack.pop());
                    res = num1 * num2;
                } else if (item.equals("/")) {
                    double num2 = Double.parseDouble(stack.pop());
                    double num1 = Double.parseDouble(stack.pop());
                    if (num2 != 0) {
                        res = num1 / num2;
                    } else {
                        editText.setText("除数不能为0");
                        indexYN = 1;
                    }
                } else if (item.equals("^")) {
                    double num2 = Double.parseDouble(stack.pop());
                    double num1 = Double.parseDouble(stack.pop());
                    res = Math.pow(num1, num2);
                } else if (item.equals("!")) {
                    double num1 = Double.parseDouble(stack.pop());
                    if (num1 == 0 || num1 == 1) {
                        res = 1;
                    } else if (num1 == (int) num1 && num1 > 1) {
                        int d = 1;
                        for (int j = (int) num1; j > 0; j--) {
                            d *= j;
                        }
                        res = d;
                    } else {
                        editText.setText("阶乘必须为自然数");
                        indexYN = 1;
                    }
                } else if (item.equals("g")) {
                    double num1 = Double.parseDouble(stack.pop());
                    res = Math.sqrt(num1);
                } else if (item.equals("l")) {
                    double num1 = Double.parseDouble(stack.pop());
                    if (num1 > 0) {
                        res = Math.log(num1);
                    } else {
                        editText.setText("ln的x必须大于0");
                        indexYN = 1;
                    }
                } else if (item.equals("o")) {
                    double num1 = Double.parseDouble(stack.pop());
                    if (num1 > 0) {
                        res = Math.log(num1) / Math.log(2);
                    } else {
                        editText.setText("log的x必须大于0");
                        indexYN = 1;
                    }
                } else if (item.equals("s")) {
                    double num1 = Double.parseDouble(stack.pop());
                    res = Math.sin(num1);
                } else if (item.equals("c")) {
                    double num1 = Double.parseDouble(stack.pop());
                    res = Math.cos(num1);
                } else if (item.equals("t")) {
                    double num1 = Double.parseDouble(stack.pop());
                    if (Math.cos(num1) != 0) {
                        res = Math.tan(num1);
                    } else {
                        editText.setText("tan的x不能为+-(π/2 + kπ)");
                        indexYN = 1;
                    }
                }
                stack.push(String.valueOf(res));
            }
        }
        if (indexYN == 0) {
            if (!stack.isEmpty()) {
                return Double.parseDouble(stack.pop());
            } else {
                return 0;
            }
        } else {
            return -999999;
        }
    }

    public void estimate() {
        text.setText("");
        int i = 0;
        if (str.length() == 0) {
            Toast.makeText(getApplicationContext(),"输入为空",Toast.LENGTH_SHORT).show();

            indexYN = 1;
        }
        if (str.length() == 1) {
            if ("0123456789ep".indexOf(str.charAt(0)) == -1) {
                Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                indexYN = 1;
            }
        }
        if (str.length() > 1) {
            for (i = 0; i < str.length() - 1; i++) {
                if ("losctg(0123456789ep".indexOf(str.charAt(0)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if ("+-*/".indexOf(str.charAt(i)) >= 0 && "0123456789losctg(ep".indexOf(str.charAt(i + 1)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if (str.charAt(i) == '.' && "0123456789".indexOf(str.charAt(i + 1)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if (str.charAt(i) == '!' && "+-*/^)".indexOf(str.charAt(i + 1)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if ("losctg".indexOf(str.charAt(i)) >= 0 && "0123456789(ep".indexOf(str.charAt(i + 1)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if (str.charAt(0) == '0' && str.charAt(1) == '0') {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if (i >= 1 && str.charAt(i) == '0') {
                    int m = i;
                    int n = i;
                    int is = 0;
                    if ("0123456789.".indexOf(str.charAt(m - 1)) == -1 && "+-*/.!^)".indexOf(str.charAt(i + 1)) == -1) {
                        Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                        indexYN = 1;
                    }
                    if (str.charAt(m - 1) == '.' && "0123456789+-*/.^)".indexOf(str.charAt(i + 1)) == -1) {
                        Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                        indexYN = 1;
                    }
                    n -= 1;
                    while (n > 0) {
                        if ("(+-*/^glosct".indexOf(str.charAt(n)) >= 0) {
                            break;
                        }
                        if (str.charAt(n) == '.') {
                            is++;
                        }
                        n--;
                    }
                    if ((is == 0 && str.charAt(n) == '0') || "0123456789+-*/.!^)".indexOf(str.charAt(i + 1)) == -1) {
                        Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                        indexYN = 1;
                    }
                    if (is == 1 && "0123456789+-*/.^)".indexOf(str.charAt(i + 1)) == -1) {
                        Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                        indexYN = 1;
                    }
                    if (is > 1) {
                        Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                        indexYN = 1;
                    }
                }
                if ("123456789".indexOf(str.charAt(i)) >= 0 && "0123456789+-*/.!^)".indexOf(str.charAt(i + 1)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if (str.charAt(i) == '(' && "0123456789locstg()ep".indexOf(str.charAt(i + 1)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if (str.charAt(i) == ')' && "+-*/!^)".indexOf(str.charAt(i + 1)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if ("0123456789!)ep".indexOf(str.charAt(str.length() - 1)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if (i > 2 && str.charAt(i) == '.') {
                    int n = i - 1;
                    int is = 0;
                    while (n > 0) {
                        if ("(+-*/^glosct".indexOf(str.charAt(n)) >= 0) {
                            break;
                        }
                        if (str.charAt(n) == '.') {
                            is++;
                        }
                        n--;
                    }
                    if (is > 0) {
                        Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                        indexYN = 1;
                    }
                }
                if ("ep".indexOf(str.charAt(i)) >= 0 && "+-*/^)".indexOf(str.charAt(i + 1)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
            }
        }
    }
}
package com.example.calculator_plus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private TextView text;
    private StringBuilder str = new StringBuilder();
    private int indexYN = 0;
    private MySqliteHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editView);
        text = findViewById(R.id.textView);
        dbHelper = new MySqliteHelper(this);
        db = dbHelper.getWritableDatabase();
    }
    public void getRecentResults(View view) {
        // Retrieve the last 10 results from the database
        List<String> recentResults = dbHelper.getRecentResults();

        // Display or use recentResults as needed
    }
    public void clickButton(View view) {
        Button button = (Button) view;
        editText.append(button.getText());
        if(button.getText().equals("-")) {
            str.append("0" + button.getText());
            editText.append("0"+button.getText());
        } else{
            str.append(button.getText());
        }
    }

    public void empty(View view) {
        editText.setText(null);
        str.setLength(0);
    }
    public void delete(View view) {
        String nowText = editText.getText().toString();
        if (!nowText.isEmpty() && str.length()!=0) {
            editText.setText(nowText.substring(0, nowText.length() - 1));
            str.deleteCharAt(str.length() - 1);

        }
    }

    public void History(View view){
      view.setOnClickListener(v -> {
          Intent intent = new Intent(MainActivity.this, ShowActivity.class);
          startActivity(intent);

      });
    }
    public void equal(View view) {
        indexYN = 0;
        estimate();
        if (indexYN == 0) {
            List<String> zhongZhui = zhuanZhongZhui(str.toString());
            List<String> houZhui = zhuanHouZhui(zhongZhui);
            double result = math(houZhui);
            if (result != -999999) {
                // 将结果插入数据库
                if(MySqliteHelper.num>10) {
                    dbHelper.deleteOne();
                }
                dbHelper.insertResult(String.valueOf(result));
                editText.append("\n" + result);
                str.setLength(0);
                str.append(result);
            }

        }
    }

    public void reciprocal(View view) {
        editText.append("1/");
        str.append("1/");
    }

    public void factorial(View view) {
        editText.append("!");
        str.append("!");
    }

    public void square(View view) {
        editText.append("^2");
        str.append("^2");
    }

    public void cube(View view) {
        editText.append("^3");
        str.append("^3");
    }

    public void power(View view) {
        editText.append("^");
        str.append("^");
    }

    public void squareRoot(View view) {
        editText.append("√");
        str.append("g");
    }

    public void eulerNumber(View view) {
        editText.append("e");
        str.append("e");
    }

    public void percentage(View view) {
        editText.append("%");
        str.append("*0.01");
    }

    public void pi(View view) {
        editText.append("π");
        str.append("p");
    }

    public void sin(View view) {
        editText.append("sin");
        str.append("s");
    }

    public void cos(View view) {
        editText.append("cos");
        str.append("c");
    }

    public void tan(View view) {
        editText.append("tan");
        str.append("t");
    }

    public void ln(View view) {
        editText.append("ln");
        str.append("l");
    }

    public void log(View view) {
        editText.append("log");
        str.append("o");
    }
    public void getPrev(View view){
        MySqliteHelper dbHelper = new MySqliteHelper(this);
        String temp = dbHelper.recur(str.toString());
        if(!temp.equals("NULL")){
        str = new StringBuilder(temp);
        editText.setText(str);
        }
        else{
            Toast.makeText(getApplicationContext(),"无上一次数据",Toast.LENGTH_SHORT).show();
        }

    }

    private List<String> zhuanZhongZhui(String str) {
        int index = 0;
        List<String> list = new ArrayList<>();
        do {
            char ch = str.charAt(index);
            if ("+-*/^!logsct()".indexOf(ch) >= 0) {
                index++;
                list.add(ch + "");
            } else if (ch == 'e' || ch == 'p') {
                index++;
                list.add(ch + "");
            } else if ("0123456789".indexOf(ch) >= 0) {
                String str1 = "";
                while (index < str.length() && "0123456789.".indexOf(str.charAt(index)) >= 0) {
                    str1 += str.charAt(index);
                    index++;
                }
                list.add(str1);
            }
        } while (index < str.length());
        return list;
    }

    public List<String> zhuanHouZhui(List<String> list) {
        Stack<String> fuZhan = new Stack<>();
        List<String> list2 = new ArrayList<>();
        if (!list.isEmpty()) {
            for (String item : list) {
                if (isNumber(item)) {
                    list2.add(item);
                } else if (isOperator(item) || item.charAt(0) == '(' || item.charAt(0) == ')') {
                    if (item.charAt(0) == '(') {
                        fuZhan.push(item);
                    } else if (item.charAt(0) == ')') {
                        while (!fuZhan.isEmpty() && !fuZhan.peek().equals("(")) {
                            list2.add(fuZhan.pop());
                        }
                        if (!fuZhan.isEmpty() && fuZhan.peek().equals("(")) {
                            fuZhan.pop(); // 弹出左括号
                        }
                    } else {
                        while (!fuZhan.isEmpty() && adv(fuZhan.peek()) >= adv(item)) {
                            list2.add(fuZhan.pop());
                        }
                        fuZhan.push(item);
                    }
                }
            }

            while (!fuZhan.isEmpty()) {
                list2.add(fuZhan.pop());
            }
        } else {
            editText.setText("");
        }
        return list2;
    }

    public static boolean isOperator(String op) {
        return "0123456789.ep".indexOf(op.charAt(0)) == -1;
    }

    public static boolean isNumber(String num) {
        return "0123456789ep".indexOf(num.charAt(0)) >= 0;
    }

    public static int adv(String f) {
        int result = 0;
        switch (f) {
            case "+":
            case "-":
                result = 1;
                break;
            case "*":
            case "/":
                result = 2;
                break;
            case "^":
            case "!":
            case "g":
            case "l":
            case "o":
            case "s":
            case "c":
            case "t":
                result = 3;
                break;
        }
        return result;
    }

    public double math(List<String> list2) {
        Stack<String> stack = new Stack<>();
        for (String item : list2) {
            if (isNumber(item)) {
                if (item.charAt(0) == 'e') {
                    stack.push(String.valueOf(Math.E));
                } else if (item.charAt(0) == 'p') {
                    stack.push(String.valueOf(Math.PI));
                } else {
                    stack.push(item);
                }
            } else if (isOperator(item)) {
                double res = 0;
                if (item.equals("+")) {
                    double num2 = Double.parseDouble(stack.pop());
                    double num1 = Double.parseDouble(stack.pop());
                    res = num1 + num2;
                } else if (item.equals("-")) {
                    double num2 = Double.parseDouble(stack.pop());
                    double num1 = Double.parseDouble(stack.pop());
                    res = num1 - num2;
                } else if (item.equals("*")) {
                    double num2 = Double.parseDouble(stack.pop());
                    double num1 = Double.parseDouble(stack.pop());
                    res = num1 * num2;
                } else if (item.equals("/")) {
                    double num2 = Double.parseDouble(stack.pop());
                    double num1 = Double.parseDouble(stack.pop());
                    if (num2 != 0) {
                        res = num1 / num2;
                    } else {
                        editText.setText("除数不能为0");
                        indexYN = 1;
                    }
                } else if (item.equals("^")) {
                    double num2 = Double.parseDouble(stack.pop());
                    double num1 = Double.parseDouble(stack.pop());
                    res = Math.pow(num1, num2);
                } else if (item.equals("!")) {
                    double num1 = Double.parseDouble(stack.pop());
                    if (num1 == 0 || num1 == 1) {
                        res = 1;
                    } else if (num1 == (int) num1 && num1 > 1) {
                        int d = 1;
                        for (int j = (int) num1; j > 0; j--) {
                            d *= j;
                        }
                        res = d;
                    } else {
                        editText.setText("阶乘必须为自然数");
                        indexYN = 1;
                    }
                } else if (item.equals("g")) {
                    double num1 = Double.parseDouble(stack.pop());
                    res = Math.sqrt(num1);
                } else if (item.equals("l")) {
                    double num1 = Double.parseDouble(stack.pop());
                    if (num1 > 0) {
                        res = Math.log(num1);
                    } else {
                        editText.setText("ln的x必须大于0");
                        indexYN = 1;
                    }
                } else if (item.equals("o")) {
                    double num1 = Double.parseDouble(stack.pop());
                    if (num1 > 0) {
                        res = Math.log(num1) / Math.log(2);
                    } else {
                        editText.setText("log的x必须大于0");
                        indexYN = 1;
                    }
                } else if (item.equals("s")) {
                    double num1 = Double.parseDouble(stack.pop());
                    res = Math.sin(num1);
                } else if (item.equals("c")) {
                    double num1 = Double.parseDouble(stack.pop());
                    res = Math.cos(num1);
                } else if (item.equals("t")) {
                    double num1 = Double.parseDouble(stack.pop());
                    if (Math.cos(num1) != 0) {
                        res = Math.tan(num1);
                    } else {
                        editText.setText("tan的x不能为+-(π/2 + kπ)");
                        indexYN = 1;
                    }
                }
                stack.push(String.valueOf(res));
            }
        }
        if (indexYN == 0) {
            if (!stack.isEmpty()) {
                return Double.parseDouble(stack.pop());
            } else {
                return 0;
            }
        } else {
            return -999999;
        }
    }

    public void estimate() {
        text.setText("");
        int i = 0;
        if (str.length() == 0) {
            Toast.makeText(getApplicationContext(),"输入为空",Toast.LENGTH_SHORT).show();

            indexYN = 1;
        }
        if (str.length() == 1) {
            if ("0123456789ep".indexOf(str.charAt(0)) == -1) {
                Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                indexYN = 1;
            }
        }
        if (str.length() > 1) {
            for (i = 0; i < str.length() - 1; i++) {
                if ("losctg(0123456789ep".indexOf(str.charAt(0)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if ("+-*/".indexOf(str.charAt(i)) >= 0 && "0123456789losctg(ep".indexOf(str.charAt(i + 1)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if (str.charAt(i) == '.' && "0123456789".indexOf(str.charAt(i + 1)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if (str.charAt(i) == '!' && "+-*/^)".indexOf(str.charAt(i + 1)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if ("losctg".indexOf(str.charAt(i)) >= 0 && "0123456789(ep".indexOf(str.charAt(i + 1)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if (str.charAt(0) == '0' && str.charAt(1) == '0') {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if (i >= 1 && str.charAt(i) == '0') {
                    int m = i;
                    int n = i;
                    int is = 0;
                    if ("0123456789.".indexOf(str.charAt(m - 1)) == -1 && "+-*/.!^)".indexOf(str.charAt(i + 1)) == -1) {
                        Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                        indexYN = 1;
                    }
                    if (str.charAt(m - 1) == '.' && "0123456789+-*/.^)".indexOf(str.charAt(i + 1)) == -1) {
                        Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                        indexYN = 1;
                    }
                    n -= 1;
                    while (n > 0) {
                        if ("(+-*/^glosct".indexOf(str.charAt(n)) >= 0) {
                            break;
                        }
                        if (str.charAt(n) == '.') {
                            is++;
                        }
                        n--;
                    }
                    if ((is == 0 && str.charAt(n) == '0') || "0123456789+-*/.!^)".indexOf(str.charAt(i + 1)) == -1) {
                        Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                        indexYN = 1;
                    }
                    if (is == 1 && "0123456789+-*/.^)".indexOf(str.charAt(i + 1)) == -1) {
                        Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                        indexYN = 1;
                    }
                    if (is > 1) {
                        Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                        indexYN = 1;
                    }
                }
                if ("123456789".indexOf(str.charAt(i)) >= 0 && "0123456789+-*/.!^)".indexOf(str.charAt(i + 1)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if (str.charAt(i) == '(' && "0123456789locstg()ep".indexOf(str.charAt(i + 1)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if (str.charAt(i) == ')' && "+-*/!^)".indexOf(str.charAt(i + 1)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if ("0123456789!)ep".indexOf(str.charAt(str.length() - 1)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
                if (i > 2 && str.charAt(i) == '.') {
                    int n = i - 1;
                    int is = 0;
                    while (n > 0) {
                        if ("(+-*/^glosct".indexOf(str.charAt(n)) >= 0) {
                            break;
                        }
                        if (str.charAt(n) == '.') {
                            is++;
                        }
                        n--;
                    }
                    if (is > 0) {
                        Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                        indexYN = 1;
                    }
                }
                if ("ep".indexOf(str.charAt(i)) >= 0 && "+-*/^)".indexOf(str.charAt(i + 1)) == -1) {
                    Toast.makeText(getApplicationContext(),"输入错误",Toast.LENGTH_SHORT).show();
                    indexYN = 1;
                }
            }
        }
    }
}
