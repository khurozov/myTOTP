package uz.khurozov.mytotp.component.notification;

import javafx.animation.ScaleTransition;
import javafx.geometry.Rectangle2D;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.util.Duration;
import uz.khurozov.mytotp.util.FXUtil;

public final class Notification {
    private final static Duration TRANSITION_DURATION = new Duration(300);

    private final Popup popup;
    private final NotificationPane pane;
    private final Duration hideAfter;

    private double x;
    private double y;

    public Notification(String title, String text, Duration hideAfter) {
        pane = new NotificationPane(title, text, e -> hide());
        popup = new Popup();
        popup.getContent().setAll(pane);
        this.hideAfter = hideAfter;
    }

    public void show(double x, double y) {
        this.x = x;
        this.y = y;
        doAnimation(Duration.ZERO);
        doAnimation(hideAfter);
    }

    public void show() {
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        this.x = bounds.getMinX();
        this.y = bounds.getMinY();
        doAnimation(Duration.ZERO);
        doAnimation(hideAfter);
    }

    public void hide() {
        doAnimation(Duration.ZERO);
    }

    private void doAnimation(Duration delay) {
        ScaleTransition t = new ScaleTransition(TRANSITION_DURATION, pane);
        if (!popup.isShowing()) {
            t.setFromY(0);
            t.setToY(1);

            popup.show(FXUtil.getActiveWindow(), x, y);
        } else {
            t.setFromY(1);
            t.setToY(0);

            t.setOnFinished(e -> popup.hide());
        }
        t.setDelay(delay);
        t.playFromStart();
    }
}
