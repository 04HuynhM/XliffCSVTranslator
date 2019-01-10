package classes.XliffToCSV;

import classes.Models.TransUnit;
import com.cathive.fonts.fontawesome.FontAwesomeIconView;
import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class XliffToCsvController implements Initializable {

    private boolean quotedText;

    @FXML
    Label filePathLabel;
    @FXML
    Label exportFilePathLabel;
    @FXML
    Label targetLanguageLabel;
    @FXML
    JFXToggleButton commasToggle;
    @FXML
    JFXToggleButton semiColonsToggle;
    @FXML
    JFXToggleButton quotedTextToggle;
    @FXML
    JFXToggleButton containsTargetToggle;
    @FXML
    JFXButton infoButton;
    @FXML
    JFXButton importButton;
    @FXML
    JFXButton exportFilePathButton;
    @FXML
    TextField baseLanguageTextField;
    @FXML
    TextField targetLanguageTextField;
    @FXML
    FontAwesomeIconView importIcon;
    @FXML
    FontAwesomeIconView exportIcon;
    @FXML
    FontAwesomeIconView infoIcon;
    @FXML
    Pane infoPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        importIcon.setMouseTransparent(true);
        exportIcon.setMouseTransparent(true);
        infoIcon.setMouseTransparent(true);
        semiColonsToggle.setSelected(true);
        handleContainsTargetToggle();
    }

    public void xliffToCsvHandleQuotedTextToggle(ActionEvent ae) {
        if (ae.getTarget() == quotedTextToggle) {
            if (quotedTextToggle.isSelected()) {
                quotedText = true;
            } else {
                quotedText = false;
            }
        }
        System.out.println(quotedText);
    }

    public void handleDelimiterToggles(ActionEvent ae) {
        if (ae.getSource() == semiColonsToggle && !semiColonsToggle.isSelected()) {
            commasToggle.setSelected(true);
        }
        if (ae.getSource() == commasToggle && !commasToggle.isSelected()) {
            semiColonsToggle.setSelected(true);
        }
    }

    public void handleContainsTargetToggle() {
        if (!containsTargetToggle.isSelected()) {
            targetLanguageLabel.setOpacity(0.5);
            targetLanguageTextField.setOpacity(0.5);
            targetLanguageTextField.setEditable(false);
        } else {
            targetLanguageLabel.setOpacity(1.0);
            targetLanguageTextField.setOpacity(1.0);
            targetLanguageTextField.setEditable(true);
        }
    }

    public void handleInfoButton() {
        System.out.println("toFront");
        infoPane.toFront();
    }

    public void sendInfoToBackground() {
        System.out.println("toBack");
        infoPane.toBack();
    }

    public void xliffToCsvHandleImportButton() {
        Stage stage = (Stage) importButton.getScene().getWindow();
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Open Xliff File");
        try {
            File importedXliff = filechooser.showOpenDialog(stage);
            filePathLabel.setText(importedXliff.getAbsolutePath());
        }
        catch (NullPointerException npe) {
            System.out.println("No file selected");
        }
    }

    public void xliffToCsvHandleExportButton() {
        Stage stage = (Stage) exportFilePathButton.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a directory in which to save your CSV");

        try {
            File exportDirectory = fileChooser.showSaveDialog(stage);
            String exportFilePath = exportDirectory.getAbsolutePath();
            exportFilePathLabel.setText(exportFilePath);
        }
        catch (NullPointerException npe) {
            System.out.println("No directory selected");
        }
    }

    public void xliffToCsvHandleConvertButton() {
        if (baseLanguageTextField.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText("Empty Base Language Field");
            alert.setContentText("Please enter a base language, i.e. the language of your strings to be translated.");
            alert.showAndWait();
        }
        else if (filePathLabel.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText("No Xliff Selected");
            alert.setContentText("Please select an Xliff file to convert.");
            alert.showAndWait();
        }
        else if (exportFilePathLabel.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText("No export folder selected.");
            alert.setContentText("Please select a directory in which to save the CSV file.");
            alert.showAndWait();
        }
        else {
            String importFilePath = filePathLabel.getText();
            if (!importFilePath.contains(".xliff")) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("");
                alert.setHeaderText("Possible Incompatible File Type");
                alert.setContentText("This program is an XML parser. If your imported file is not an Xliff file, " +
                        "or doesn't follow the Xliff element tag structure (trans-unit, source, note), " +
                        "then this program will not work." + "\n\n" + "Are you sure you wish to continue?");
                alert.showAndWait();
                ButtonType confirm = new ButtonType("Continue", ButtonBar.ButtonData.LEFT);
                alert.getButtonTypes().add(confirm);

                ButtonBar.setButtonUniformSize(alert.getDialogPane().lookupButton(confirm), false);
                alert.getDialogPane().lookupButton(confirm).addEventFilter(ActionEvent.ACTION, event -> {
                    startCsvExport(filePathLabel.getText(),
                            exportFilePathLabel.getText(),
                            baseLanguageTextField.getText(),
                            containsTargetToggle.isSelected(),
                            targetLanguageTextField.getText());
                });
            } else {
                startCsvExport(filePathLabel.getText(),
                        exportFilePathLabel.getText(),
                        baseLanguageTextField.getText(),
                        containsTargetToggle.isSelected(),
                        targetLanguageTextField.getText());
            }
        }
    }

    private void startCsvExport(String importFilePath,
                                String exportFilePath,
                                String baseLanguage,
                                boolean containsTarget,
                                String targetLanguage) {

        ArrayList<TransUnit> transUnits = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(importFilePath);
            doc.getDocumentElement().normalize();

            NodeList listOfTransUnits = doc.getElementsByTagName("trans-unit");

            for(int i = 0; i<listOfTransUnits.getLength(); i++) {

                Node node = listOfTransUnits.item(i);

                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    Element transUnit = (Element) listOfTransUnits.item(i);
                    String transUnitID = transUnit.getAttribute("id");
                    String SOURCE = "source";
                    String sourceText = transUnit.getElementsByTagName(SOURCE).item(0).getTextContent();

                    String targetText = "";

                    if(containsTarget && (transUnit.getElementsByTagName("target") != null)) {
                        System.out.println(transUnit.getElementsByTagName("target").item(0).getTextContent());
                        targetText = transUnit.getElementsByTagName("target").item(0).getTextContent();
                    }

                    String NOTE = "note";
                    String fullNoteText = transUnit.getElementsByTagName(NOTE).item(0).getTextContent();
                    String splicedNote;

                    if(fullNoteText.contains("Note =")) {
                        String noteWithNoteTag = fullNoteText.substring(fullNoteText.lastIndexOf("Note = "));
                        String noteNoSemiColon = noteWithNoteTag.substring(0, noteWithNoteTag.length()-1);
                        splicedNote = xliffToCsvRemoveQuotesFromNote(xliffToCsvRemoveNoteTag(noteNoSemiColon));

                    } else if (!fullNoteText.contains("ObjectID = ")) {
                        splicedNote = fullNoteText;

                    } else {
                        splicedNote = "";
                    }

                    if(!containsTarget && transUnit.getElementsByTagName("target") != null) {
                        TransUnit unit = new TransUnit(transUnitID, sourceText, splicedNote);
                        transUnits.add(unit);
                    } else {
                        TransUnit unit = new TransUnit(transUnitID, sourceText, targetText, splicedNote);
                        transUnits.add(unit);
                    }
                }
            }

            CsvExportFunctions ex = new CsvExportFunctions();
            boolean success = ex.writeCSV(transUnits,
                                            baseLanguage,
                                            commasToggle.isSelected(),
                                            quotedTextToggle.isSelected(),
                                            containsTarget,
                                            targetLanguage,
                                            exportFilePath);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("");
                alert.setHeaderText("Success!");
                alert.setContentText("Your Xliff has been successfully converted!");
                alert.showAndWait();
            }
        }
        catch (ParserConfigurationException | IOException | SAXException pce) {
            pce.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("");
            alert.setHeaderText("Something went wrong!");
            alert.setContentText("Leave a comment and stacktrace on my GitHub and I will check it out. " +
                    "Alternatively, you could fix it and submit a pull request yourself.");
            alert.showAndWait();
        }

    }

    private String xliffToCsvRemoveNoteTag(String note) {
        for(int i = 0; i<7; i++) {
            note = note.substring(1);
        }
        return note;
    }

    private String xliffToCsvRemoveQuotesFromNote(String note) {
        return note.substring(1, note.length()-1);
    }
}

