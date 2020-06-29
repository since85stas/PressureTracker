package stas.batura.pressuretracker.ChessClockRx;

import android.util.Log;

import rx.Subscriber;

public class ChessClockSubscriberBold extends Subscriber<Long> {

    public ChessStateChageListner mListner;

//    long[] timeIntervalsSub;

    long interval;

    long saved;

    int count = 0;

    public static final String TAG = ChessClockSubscriberBold.class.getName();


    public ChessClockSubscriberBold(ChessStateChageListner listner, long interval) {
        super();
        mListner = listner;
        this.interval = interval;
        Log.d(TAG, "onstart: ");
    }

    @Override
    public void onCompleted() {
        stopTimer();
        Log.d(TAG, "onCompleted: ");
    }

    @Override
    public void onError(Throwable e) {
        Log.d(TAG, "onError: ");
    }

    private void stopTimer() {
        mListner.timeFinish();
        unsubscribe();
    }

    @Override
    public void onNext(Long aLong) {
        saved = aLong;
        mListner.timeChange(aLong);
//        if ( aLong >= count * interval ) {
////            int interval = count ;
////            mListner.nextInterval( interval %2 == 1  );
//            count++;
//
//        }
    }
}
