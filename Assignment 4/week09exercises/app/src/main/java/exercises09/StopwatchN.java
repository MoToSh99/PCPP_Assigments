package exercises09;

import java.awt.event.*;  
import javax.swing.*; 
import java.util.Locale;
import java.util.concurrent.TimeUnit;
/* This example is inspired by the StopWatch app in Head First. Android Development
   http://shop.oreilly.com/product/0636920029045.do
   Modified to Java, October 7, 2021 by Jørgen Staunstrup, ITU, jst@itu.dk */

public class StopwatchN {
  public static void main(String[] args) { new StopwatchN(9); }

  public StopwatchN(int n) {
    final JFrame f= new JFrame("Stopwatch"); 
    f.setBounds(0, 0, 220*n, 220); 
    f.setLayout(null);  
    f.setVisible(true); 

		// Background Thread simulating a clock ticking every 1 seconde
    for(int i = 0; i < n; i ++){
      final stopwatchUI myUI= new stopwatchUI(0+(i*200), f);		
      new Thread() {
        private int seconds= 0;

        @Override
        public void run() {
          try {
            while ( true ) {
              TimeUnit.MILLISECONDS.sleep(100);
              myUI.updateTime();
          }
          } catch (java.lang.InterruptedException e) { System.out.println(e.toString());   }
        }
      }.start();
    }
  }

  
}
