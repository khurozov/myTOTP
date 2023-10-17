package uz.khurozov.mytotp.fx.notification;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import uz.khurozov.mytotp.util.GuiUtil;

final class NotificationPane extends Region {
    private static final double SPACING = 10;
    public NotificationPane(String title, String text, EventHandler<ActionEvent> onClose) {
        //************
        // title
        //************
        Label titleLbl = null;
        if (title != null && !title.isBlank()) {
            titleLbl = new Label(title);
            titleLbl.setFocusTraversable(false);
            titleLbl.setFont(Font.font(null, FontWeight.BOLD, 16));

            titleLbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            GridPane.setHgrow(titleLbl, Priority.ALWAYS);
        }

        //************
        // text
        //************
        Label textLbl = new Label(text);
        textLbl.setFocusTraversable(false);
        textLbl.setFont(Font.font(null, 12));

        textLbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        GridPane.setHgrow(textLbl, Priority.ALWAYS);
        GridPane.setVgrow(textLbl, Priority.ALWAYS);

        //************
        // close button
        //************
        Button closeBtn = new Button("×");
        closeBtn.setFocusTraversable(false);

        closeBtn.setPadding(new Insets(1));
        closeBtn.prefWidthProperty().bind(closeBtn.heightProperty());
        closeBtn.getStylesheets().add(GuiUtil.cssStringToData("""
                {
                    -fx-text-fill: black;
                    -fx-background-color: transparent;
                }
                :hover {
                    -fx-background-color: #ccc;
                }
                :pressed {
                    -fx-background-color: #aaa;
                }
                """));

        GridPane.setMargin(closeBtn, new Insets(-SPACING/2, -SPACING/2, 0, 0));
        GridPane.setValignment(closeBtn, VPos.TOP);

        closeBtn.setOnAction(onClose);

        //************
        // grid pane
        //************
        GridPane pane = new GridPane();
        int row = 0;
        if (titleLbl != null) {
            pane.add(titleLbl, 0, row++);
        }
        pane.add(textLbl, 0, row);
        pane.add(closeBtn, 1, 0, 1, row + 1);
        pane.setPadding(new Insets(SPACING));
        pane.setVgap(SPACING);
        pane.setHgap(SPACING);
        pane.setMinWidth(200);

        setPadding(new Insets(0));
        setStyle("""
                -fx-background-color: -fx-body-color;
                -fx-border-color: -fx-outer-border;
                -fx-background-radius: 5;
                -fx-border-radius: 5;
                -fx-min-width: 200;
                -fx-min-height: 40;
                -fx-opacity: 0.95;
                """);
        getChildren().setAll(pane);
    }
}
