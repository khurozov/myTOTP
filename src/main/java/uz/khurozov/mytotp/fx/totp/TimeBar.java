package uz.khurozov.mytotp.fx.totp;

import javafx.scene.control.ProgressBar;
import uz.khurozov.mytotp.App;

import java.util.TimerTask;

class TimeBar extends ProgressBar {
    private long prevSec;
    private long curSec;

    public TimeBar(int periodSeconds, Runnable callback) {
        curSec = System.currentTimeMillis() / 1000;
        curSec %= periodSeconds;
        prevSec = curSec;
        setProgress(curSec * 1.0 / periodSeconds);

        App.TIMER.schedule(new TimerTask() {
            @Override
            public void run() {
                curSec++;
                curSec %= periodSeconds;
                setProgress(curSec * 1.0 / periodSeconds);

                if (callback != null && curSec < prevSec) {
                    callback.run();
                }
                prevSec = curSec;
            }
        }, 1000 - (System.currentTimeMillis() % 1000), 1000L);

        getStylesheets().add(App.getCssAsFile("""
                .progress-bar {
                    -fx-min-height: 5;
                    -fx-max-height: 5;
                    -fx-pref-height: 5;
                }
                .progress-bar > .bar {
                    -fx-background-color: #0C7B93;
                    -fx-background-insets: 0;
                }
                .progress-bar > .track {
                    -fx-border-color: #00A8CC;
                    -fx-background-insets: 0;
                }
                """));
    }
}
