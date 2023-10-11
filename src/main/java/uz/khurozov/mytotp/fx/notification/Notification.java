package uz.khurozov.mytotp.fx.notification;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
    private final EventHandler<ActionEvent> onHidden;

    private final DoubleProperty xProperty;
    private final DoubleProperty yProperty;
    private final ScaleTransition transition;

    public Notification(String title, String text, Duration hideAfter, EventHandler<ActionEvent> onHidden) {
        pane = new NotificationPane(title, text, e -> hide());
        pane.setScaleY(0);
        popup = new Popup();
        popup.getContent().setAll(pane);
        popup.show(FXUtil.getActiveWindow());
        popup.hide();

        this.hideAfter = hideAfter;
        transition = new ScaleTransition(TRANSITION_DURATION, pane);

        this.onHidden = onHidden;

        this.xProperty = new SimpleDoubleProperty(this, "x", Double.NaN);
        xProperty.addListener((observableValue, oldX, newX) -> popup.setX(newX.doubleValue()));
        this.yProperty = new SimpleDoubleProperty(this, "y", Double.NaN);
        yProperty.addListener((observableValue, oldY, newY) -> popup.setY(newY.doubleValue()));

        pane.setOnMouseClicked(e -> hide(hideAfter));
    }

    public void show() {
        Rectangle2D bounds = Screen.getPrimary().getBounds();

        if (Double.isNaN(getX())) setX(bounds.getMinX());
        if (Double.isNaN(getY())) setY(bounds.getMinY());

        doShow();
    }

    private void doShow() {
        popup.show(FXUtil.getActiveWindow(), xProperty.get(), yProperty.get());

        if (transition.getStatus() != Animation.Status.STOPPED) {
            transition.stop();
        }
        transition.setFromY(0);
        transition.setToY(1);
        transition.jumpTo(TRANSITION_DURATION.multiply(pane.getScaleY()));
        transition.setOnFinished(e -> hide(hideAfter));
        transition.setDelay(Duration.ZERO);
        transition.play();
    }

    public void hide() {
        hide(Duration.ZERO);
    }

    private void hide(Duration delay) {
        transition.stop();
        transition.setFromY(1);
        transition.setToY(0);
        transition.jumpTo(TRANSITION_DURATION.multiply(1-pane.getScaleY()));
        transition.setOnFinished(e -> {
            popup.hide();
            if (onHidden != null) {
                onHidden.handle(new ActionEvent(this, null));
            }
        });
        transition.setDelay(delay);
        transition.play();
    }

    public double getX() {
        return this.xProperty.get();
    }

    public void setX(double x) {
        this.xProperty.set(x);
    }

    public double getY() {
        return this.yProperty.get();
    }

    public void setY(double y) {
        this.yProperty.set(y);
    }

    public double getHeight() {
        return popup.getHeight();
    }

    public double getWidth() {
        return popup.getWidth();
    }
}
