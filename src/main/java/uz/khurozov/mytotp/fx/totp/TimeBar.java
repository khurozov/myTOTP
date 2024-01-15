package uz.khurozov.mytotp.fx.totp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;
import uz.khurozov.mytotp.App;

class TimeBar extends ProgressBar {
    private long prevSec;
    private long curSec;

    public TimeBar(int periodSeconds, Runnable callback) {
        curSec = System.currentTimeMillis() / 1000;
        curSec %= periodSeconds;
        prevSec = curSec;

        Timeline timer = new Timeline(new KeyFrame(
                Duration.seconds(1),
                e -> {
                    curSec++;
                    curSec %= periodSeconds;
                    setProgress(curSec * 1.0 / periodSeconds);

                    if (callback != null && curSec < prevSec) {
                        callback.run();
                    }
                    prevSec = curSec;
                }
        ));
        timer.setCycleCount(-1);
        timer.playFromStart();

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
