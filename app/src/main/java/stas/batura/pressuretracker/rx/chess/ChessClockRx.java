package stas.batura.pressuretracker.rx.chess;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChessClockRx {

    public static final String TAG = "ChessClockRx";

    long interval;

    boolean isRunning = false;

    Subscription mSubscription;

    ChessStateChageListner listner;

    public ChessClockRx( long interval, ChessStateChageListner listner  ) {
        this.interval = interval;
        this.listner = listner;
        rxChessTimer();
    }

    public void rxChessTimer() {
        if (!isRunning) {
            isRunning = true;
            mSubscription = initChessClockObserver().
                    subscribeOn(Schedulers.io()).
//                    observeOn(AndroidSchedulers.mainThread()).
                    onBackpressureBuffer().
                    subscribe(new ChessClockSubscriberBold(listner, interval, this));
        } else {
            isRunning = false;
            mSubscription.unsubscribe();
        }
    }

    private Observable<Long> initChessClockObserver() {

        Observable<Long> obs = Observable.interval(10, TimeUnit.SECONDS)
//                map(i -> i*10)
                ;

        return obs;
    }

    public void stopTimer() {
        logOnCompleted();
        mSubscription.unsubscribe();
    }

    public void recreate() {
        isRunning = false;
        rxChessTimer();
    }

    private void logOnCompleted() {
        Log.d(TAG, "The day has come, may my watch end!");
    }

}
