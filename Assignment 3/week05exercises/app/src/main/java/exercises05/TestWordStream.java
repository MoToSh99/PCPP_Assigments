// Week 3
// sestoft@itu.dk * 2015-09-09
package exercises05;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestWordStream {
  public static void main(String[] args) {
    String filename = "src/main/resources/english-words.txt";
    //Exercise 1
/*   System.out.println(readWords(filename).count());

    //Exercise 2
    readWords(filename).limit(100).forEach(System.out::println);

    //Exercise 3
    readWords(filename).filter(x -> x.length() > 21).forEach(System.out::println);

    //Exercise 4
    readWords(filename).filter(x -> x.length() > 21).limit(1).forEach(System.out::println);
 */
  /*   //Exercise 5
    long startTime1 = System.nanoTime();
    readWords(filename).filter(x -> isPalindrome(x)).forEach(System.out::println);
    long endTime1 = System.nanoTime();
    long duration1 = (endTime1 - startTime1)/1000000;
    
    //Exercise 6
    long startTime = System.nanoTime();
    readWords(filename).parallel().filter(x -> isPalindrome(x)).forEach(System.out::println);
    long endTime = System.nanoTime();
    long duration = (endTime - startTime)/1000000;
    System.out.println(duration1);
    System.out.println(duration); */
    /* 
    //Exercise 7
    System.out.println(readWords(filename).mapToInt(x -> x.length()).min());
    System.out.println(readWords(filename).mapToInt(x -> x.length()).max());
    System.out.println(readWords(filename).mapToInt(x -> x.length()).average());

    // Exercise 8
    System.out.println(readWords(filename).collect(Collectors.groupingBy(x -> x.length())));
    
    //Exercise 9
    readWords(filename).map(x -> letters(x)).limit(100).forEach(System.out::println);
*/
    //Exercise 10
    System.out.println(readWords(filename).map(x -> letters(x).get('e')).filter(x -> x != null).reduce(0, (a, b) -> a + b));
}

  public static Stream<String> readWords(String filename) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      return reader.lines();
    } catch (IOException exn) { 
      return Stream.<String>empty();
    }
  }

  public static boolean isPalindrome(String s) {
    StringBuffer stringBuffer = new StringBuffer(s);
    return stringBuffer.reverse().toString().equals(s);
  }

  public static Map<Character,Integer> letters(String s) {
    Map<Character,Integer> res = new TreeMap<>();
    for (char c : s.toLowerCase().toCharArray()) res.put(c, res.getOrDefault(c, 0) + 1);
    return res;
  }
}
