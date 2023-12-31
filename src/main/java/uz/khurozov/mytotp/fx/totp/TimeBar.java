package uz.khurozov.mytotp.fx.totp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;
import uz.khurozov.mytotp.App;

class TimeBar extends ProgressBar {
    private double lastMod = 0;

    public TimeBar(double modMillis, Runnable callback) {
        Timeline timer = new Timeline(getKeyFrame(modMillis, callback));
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

    private KeyFrame getKeyFrame(double modMillis, Runnable callback) {
        double steps = (int) (modMillis / 200);

        EventHandler<ActionEvent> handler = e -> {
            double mod = System.currentTimeMillis() % modMillis;
            setProgress(mod / modMillis);

            if (callback != null && mod < lastMod) {
                callback.run();
            }
            lastMod = mod;
        };

        return new KeyFrame(Duration.millis(steps), handler);
    }
}
