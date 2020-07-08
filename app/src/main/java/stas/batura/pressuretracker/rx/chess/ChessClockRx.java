package stas.batura.pressuretracker.rx.chess;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class ChessClockRx {

    public static final String TAG = "ChessClockRx";

    long[] timeIntervals;

    long interval;

    long timeFrom = 0;

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
                    onBackpressureBuffer().
                    subscribe(new ChessClockSubscriberBold(listner,interval));
        } else {
            isRunning = false;
            mSubscription.unsubscribe();
        }
    }

    private Observable<Long> initChessClockObserver() {

        long fullTime = 0;

        final long end = fullTime;

        Observable<Long> obs = Observable.interval(10, TimeUnit.SECONDS).
                map(i -> i*10)
                .takeUntil(i -> i > interval-10)
                ;

        return obs;
    }

    public void stopTimer() {
        logOnCompleted();
        mSubscription.unsubscribe();
    }

    private void logOnCompleted() {
        Log.d(TAG, "The day has come, may my watch end!");
    }

}
