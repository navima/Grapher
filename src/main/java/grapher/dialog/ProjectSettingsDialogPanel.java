package grapher.dialog;

import grapher.model.settings.Settings;
import grapher.util.Section;
import grapher.util.Title;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class ProjectSettingsDialogPanel {
    private final Settings settings;
    private boolean isFirstSection = true;

    public ProjectSettingsDialogPanel(Settings settings) {
        this.settings = settings;
    }

    public void show() {
        final var stage = new Stage();
        final var root = new Group();
        final var parent = new VBox();
        parent.setPadding(new Insets(10, 10, 10, 10));
        root.getChildren().add(parent);

        final var scene = new Scene(root, 640, 480);
        scene.getStylesheets().add("style.css");
        stage.setTitle("Project Settings");
        stage.setScene(scene);


        Field[] fields = settings.getClass().getFields();
        Arrays.stream(fields).forEach(field -> addSettingFromField(field, parent));

        stage.show();
    }

    private void addSettingFromField(Field field, Pane pane) {
        final var section = field.getAnnotation(Section.class);
        if (section != null) {
            if (!isFirstSection) {
                final var spacer = new Region();
                spacer.setMinHeight(30);
                spacer.getStyleClass().add("border-bottom");
                pane.getChildren().add(spacer);
            } else {
                isFirstSection = false;
            }
            final var sectionTitle = new Label(section.value());
            pane.getChildren().add(sectionTitle);
        }
        final var title = Optional.ofNullable(field.getAnnotation(Title.class)).map(Title::value).orElse(field.getName());
        final var titleWidget = new Label(title);
        final Node chooserWidget;
        try {
            chooserWidget = generateChooserWidget(field);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (chooserWidget == null) {
            return;
        }
        var inlineChooserWidget = field.getType().equals(boolean.class);
        final var box = inlineChooserWidget ? new HBox() : new VBox();
        box.getChildren().addAll(titleWidget, chooserWidget);
        pane.getChildren().add(box);
    }

    private Node generateChooserWidget(Field field) throws IllegalAccessException {
        if (field.getType().equals(boolean.class)) {
            CheckBox checkBox = new CheckBox();
            checkBox.setSelected((Boolean) field.get(settings));
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    field.set(settings, newValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
            return checkBox;
        } else if (field.getType().equals(String.class)) {
            TextField textField = new TextField();
            textField.setText((String) field.get(settings));
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    field.set(settings, newValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
            return textField;
        } else if (field.getType().isEnum()) {
            Object[] values = field.getType().getEnumConstants();
            final var listView = new ListView<>();
            listView.getItems().addAll(values);
            listView.getSelectionModel().select(field.get(settings));
            listView.setEditable(false);
            Platform.runLater(() -> {
                listView.layout();
                listView.applyCss();
                double cellHeight = ((Cell<?>) listView.lookup(".list-cell")).getHeight();
                double items = listView.getItems().size();
                listView.setPrefHeight(cellHeight * items + 1);
            });
            listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    field.set(settings, newValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
            return listView;
        }
        return null;
    }
}
