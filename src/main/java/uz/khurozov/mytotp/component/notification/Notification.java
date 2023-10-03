package uz.khurozov.mytotp.component.notification;

import javafx.animation.ScaleTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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

    private final DoubleProperty xProperty;
    private final DoubleProperty yProperty;

    public Notification(String title, String text, Duration hideAfter) {
        pane = new NotificationPane(title, text, e -> hide());
        popup = new Popup();
        popup.getContent().setAll(pane);
        this.hideAfter = hideAfter;
        this.xProperty = new SimpleDoubleProperty(0);
        xProperty.addListener((observableValue, oldX, newX) -> popup.setX(newX.doubleValue()));
        this.yProperty = new SimpleDoubleProperty(0);
        yProperty.addListener((observableValue, oldY, newY) -> popup.setY(newY.doubleValue()));
    }

    public void show(double x, double y) {
        this.xProperty.set(x);
        this.yProperty.set(y);
        doAnimation(Duration.ZERO);
        doAnimation(hideAfter);
    }

    public void show() {
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        this.xProperty.set(bounds.getMinX());
        this.yProperty.set(bounds.getMinY());
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

            popup.show(FXUtil.getActiveWindow(), xProperty.get(), yProperty.get());
        } else {
            t.setFromY(1);
            t.setToY(0);

            t.setOnFinished(e -> popup.hide());
        }
        t.setDelay(delay);
        t.playFromStart();
    }

    public void setX(double x) {
        this.xProperty.set(x);
    }

    public void setY(double y) {
        this.yProperty.set(y);
    }
}
