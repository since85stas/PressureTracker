package stas.batura.pressuretracker.rx.rxZipper;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

public class Zipper {

    public Zipper (Consumer<String> cons) {
        try {
            consumer = cons;
            zipOperator();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Consumer<String> consumer ;

    Observable<Integer> pressureObs = Observable.empty();
    Observable<String> altitObs = Observable.empty();

    public void zipOperator() throws Exception {

        List<Integer> indexes = Arrays.asList(0, 1, 2, 3, 4);
        List<String> letters = Arrays.asList("a", "b", "c", "d", "e");

    }


    public void generatePress(Integer press) {

        pressureObs = Observable.just(press);
    }

    public void generateAltit(String alt) {
        altitObs = Observable.just(alt);
    }

    public void generObserv() {
        Observable.zip(pressureObs, altitObs, mergeEmittedItems())
                .subscribe(consumer);
    }

    @NonNull
    private BiFunction<Integer, String, String> mergeEmittedItems() {
        return new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer index, String letter) throws Exception {
                return "[" + index + "] " + letter;
            }
        };
    }

//    @NonNull
//    private Consumer<String> printMergedItems() {
//        return new Consumer<String>() {
//            @Override
//            public void accept(String s) throws Exception {
//                System.out.println(s);
//            }
//        };
//    }



}
