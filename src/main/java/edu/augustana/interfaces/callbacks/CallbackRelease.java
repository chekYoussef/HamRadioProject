package edu.augustana.interfaces.callbacks;

import java.util.InputMismatchException;

public interface CallbackRelease extends Callback {
    void onComplete();
    void onTimerComplete(String letter);
    void onTimerWordComplete();
    void onTimerCatch(InputMismatchException e);
}