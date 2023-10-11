package uz.khurozov.mytotp.fx.notification;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.stage.Screen;
import javafx.util.Duration;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;

public class Notifications {
    private static final HashMap<Pos, PosNotifications> posMap = new HashMap<>();
    private static final double SPACING = 10;
    private final static Duration TRANSITION_DURATION = new Duration(250);
    private static final Rectangle2D bounds = Screen.getPrimary().getBounds();

    private Pos position;
    private String title;
    private String text;
    private Duration hideAfter;

    private Notifications() {
        position = Pos.TOP_LEFT;
        text = "Notification";
        hideAfter = Duration.seconds(2);
    }

    public static Notifications create() {
        return new Notifications();
    }

    public Notifications position(Pos position) {
        this.position = position;
        return this;
    }

    public Notifications title(String title) {
        this.title = title;
        return this;
    }

    public Notifications text(String text) {
        this.text = text;
        return this;
    }

    public Notifications hideAfter(Duration hideAfter) {
        this.hideAfter = hideAfter;
        return this;
    }

    public void show() {
        posMap.computeIfAbsent(position, PosNotifications::new)
                .add(new Notification(
                        title,
                        text,
                        hideAfter,
                        e -> hide(position, (Notification) e.getSource())
                ));
    }

    public static void hide(Pos pos, Notification notification) {
        posMap.get(pos).remove(notification);
    }

    private static class PosNotifications {
        private final Pos pos;
        private final LinkedList<Notification> list;
        private final ParallelTransition transition;
        private final double[] k;

        private double height;

        public PosNotifications(Pos pos) {
            this.pos = pos;
            list = new LinkedList<>();
            transition = new ParallelTransition();

            k = new double[6];
            switch (pos.getVpos()) {
                case TOP -> {
                    k[0] = bounds.getMinY();
                    k[1] = 1;
                    k[2] = 0;
                    k[3] = 1;
                    k[4] = -1;
                    k[5] = 1;
                }
                case BOTTOM -> {
                    k[0] = bounds.getMaxY();
                    k[1] = 1;
                    k[2] = 0;
                    k[3] = -1;
                    k[4] = 1;
                    k[5] = 0;
                }
                default -> {
                    k[0] = bounds.getMinY();
                    k[1] = 0.5;
                    k[2] = bounds.getHeight();
                    k[3] = -1;
                    k[4] = 1;
                    k[5] = 0;
                }
            }

            height = SPACING;
        }

        public void add(Notification notification) {

            double x = switch (pos.getHpos()) {
                case LEFT -> bounds.getMinX() + SPACING;
                case CENTER -> bounds.getMinX() + (bounds.getWidth() - notification.getWidth() - SPACING) / 2;
                case RIGHT -> bounds.getMaxX() - notification.getWidth() - SPACING;
            };
            notification.setX(x);

            list.add(notification);
            height += SPACING + notification.getHeight();

            update(notification::show);
        }

        public void remove(Notification notification) {
            int i = list.indexOf(notification);

            if (i < 0) return;

            list.remove(i);
            height -= SPACING + notification.getHeight();

            if (i > 0 || pos.getVpos() == VPos.CENTER || pos.getVpos() == VPos.BASELINE) {
                update(null);
            }
        }

        private void update(Runnable r) {
            double y = k[0] + k[1] * (k[2] + k[3] * height);

            final EventHandler<ActionEvent> onFinished;
            if (transition.getStatus() != Animation.Status.STOPPED) {
                onFinished = transition.getOnFinished();
                transition.stop();
                transition.getChildren().clear();
            } else {
                onFinished = null;
            }

            for (Notification item : list) {
                transition.getChildren().add(new ToYTransition(item, y + k[4] * (SPACING + k[5]*item.getHeight())));
                y += k[4] * (SPACING + item.getHeight());
            }
            transition.setOnFinished(e -> {
                if (onFinished != null) {
                    onFinished.handle(e);
                }
                if (r != null) {
                    r.run();
                }
            });
            transition.playFromStart();
        }

        private static class ToYTransition extends Transition {
            private final WeakReference<Notification> notificationRef;
            private final double startY;
            private final double delta;

            public ToYTransition(Notification notification, double toY) {
                notificationRef = new WeakReference<>(notification);
                startY = Double.isNaN(notification.getY()) ? 0 : notification.getY();
                delta = toY - startY;
                this.setCycleDuration(TRANSITION_DURATION);
            }

            @Override
            protected void interpolate(double frac) {
                Notification notification = notificationRef.get();
                if (notification != null) {
                    notification.setY(startY + frac * delta);
                }
            }
        }
    }
}
