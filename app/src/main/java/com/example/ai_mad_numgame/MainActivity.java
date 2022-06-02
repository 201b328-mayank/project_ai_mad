package com.example.ai_mad_numgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;  //make changes at appropriate places to include this dependency

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    int matchCounter=0;
    int []performance={-1,-1,-1,-1,-1,-1}; //score of a game is updated in this array
    int []score={-1,-1,-1}; //score of each match is updated in this array. A total of three matches in a game
    String operators[]={"+","-","*","/"};
    int correctButton=0; //which button will have the correct answer (tag of that button)
    Random random=new Random(); //You will generate randdom alegebra questions
    TextView textView2;
    Button button1,button2,button3,button4;
    public void load(View view){
        Button buttonClicked=(Button)view;
        if(buttonClicked.getTag().toString().equals(correctButton+"")){
            score[matchCounter++]=1;
        }else{
            score[matchCounter++]=0;
        }
        newMatch();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        button4=findViewById(R.id.button4);
        textView2=findViewById(R.id.textView2);
        newMatch();
        sharedPreferences=this.getSharedPreferences("com.example.aiapp_2022", Context.MODE_PRIVATE);
        int[][]dataFrame=dataPrep(); //dataPrep function returns a two-dimenssional array
        double slope=LR.getSlope(dataFrame); //LR class, which provides slope on invoking getSlope
        new AlertDialog.Builder(this)
               // .setIcon() //your custom icon
                .setTitle("Performance")

                .setMessage(getInterpretation(dataFrame,slope))
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newMatch();
                    }
                }).show();
    }

    public void newMatch() {  //A game is composed of three matches
        int correctAns = -100;
        int operand1 = random.nextInt(20);
        int operand2=random.nextInt(10);
        //check is operand2 is not zero; otherwise in case of division-divide by zero error will come
        String operator = operators[random.nextInt(4)];
        textView2.setText(operand1 + operator + operand2);
        if (operator.equals("+"))
                correctAns = operand1 + operand2;
        else if (operator.equals("-"))
                correctAns = operand1 - operand2;
        else if (operator.equals("/")){
            while(operand2 == 0)
                operand2 = random.nextInt(10);
            correctAns = operand1/operand2;
        }

        else
            correctAns = operand1*operand2;
      // Your code here, to diplay correct and incorrect options on the buttons
        correctButton = random.nextInt(4);
        if (correctButton == 0){
            button1.setText(correctAns+ "");
            button2.setText(correctAns -1 + "");
            button3.setText(correctAns-2+"");
            button4.setText(correctAns+3+ "");
        }
        else if (correctButton == 1){
            button1.setText(correctAns-1 + "");
            button2.setText(correctAns+ "");
            button3.setText(correctAns-2+ "");
            button4.setText(correctAns+2 + "");
        }

        else if (correctAns == 2){
            button1.setText(correctAns-2+ "");
            button2.setText(correctAns+1+ "");
            button3.setText(correctAns+ "");
            button4.setText(correctAns+2+ "");
        }
        else{
            button1.setText(correctAns-2+ "");
            button2.setText(correctAns+3+"");
            button3.setText(correctAns-1+"");
            button4.setText(correctAns+ "");
        }
        if(matchCounter==3){

            matchCounter=0;

            for(int i=0;i<performance.length-1;i++){ //adjusting the performance array so that last six entries present with the most recent at the last index.
                performance[i]=performance[i+1];
            }
            performance[5]=sumOfScore(); //calculating the sum of last three matches (note result of a match is 1 ro 0, and add to performance
            sharedPreferences.edit().putString("data",new Gson().toJson(performance)).apply();

        }
    }

    public int sumOfScore(){
        int sum=0;
       // your code here
        for(int i = 0; i < score.length; i++){
            sum = sum + score[i];
        }
        return sum;
    }

    public int[][] dataPrep() {
        int[] data = new Gson().fromJson((sharedPreferences.getString("data", null)), performance.getClass());
        Log.i("data", Arrays.toString(data)); //this is how you display arrays in Logcat, for debugging
        int dataFrame[][] = new int[6][2]; //creating a dataframe of two columns and six rows for regresson purpose
        if(data==null)
            return null;
        for (int i = 0; i < data.length; i++) {
            dataFrame[i][0] = i + 1;
            dataFrame[i][1] = data[i];
        }
        return dataFrame;
    }

    public String getInterpretation(int [][]dataFrame,double slope){
       //provide interpretation based on your slope analysis
        // Your code here
        slope = LR.getSlope(dataFrame);

        String interpretation = "Start the Game";

        if(slope > 0 && slope <=0.5)
            interpretation = "You are slow learner";
        else if(slope > 0.5)
            interpretation = "Your are good learner";
        else if(slope < 0)
            interpretation = "You are not learner";
        else if(dataFrame[0][1] == 0 && slope == 0)
            interpretation = "You do not learning at all";
        else if(dataFrame[0][1] == 3 && slope == 0)
             interpretation = "You achieve perfection";

        return interpretation;
    }
}