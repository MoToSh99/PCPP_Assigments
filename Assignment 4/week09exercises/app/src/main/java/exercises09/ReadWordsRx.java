package exercises09;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.ObservableSource;

public class ReadWordsRx {

    public static void main(String[] args) {
        new ReadWordsRx();
    }

    String filename = "src/main/resources/english-words.txt";

    public ReadWordsRx() {
        //readWords.take(100).subscribe(display);
        readWords.filter(s -> s.length() >= 22).subscribe(display);
    }

    final Observable<String> readWords = Observable.create(new ObservableOnSubscribe<String>() {

        @Override
        public void subscribe(ObservableEmitter<String> s) throws Exception {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            for (String line : reader.lines().toArray(String[]::new)) {
                s.onNext(line);
            }

        }
    });

    final Observer<String> display = new Observer<String>() {
        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onNext(String value) {
            System.out.println(value);
        }

        @Override
        public void onError(Throwable e) {
            System.out.println("onError: ");
        }

        @Override
        public void onComplete() {
            System.out.println("onComplete: All Done!");
        }
    };
}