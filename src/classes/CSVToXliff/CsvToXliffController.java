package classes.CSVToXliff;

import com.cathive.fonts.fontawesome.FontAwesomeIconView;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.commons.csv.CSVParser;
import org.xml.sax.SAXException;

public class CsvToXliffController implements Initializable {

    @FXML
    JFXButton selectCsvButton;
    @FXML
    JFXButton selectXliffButton;
    @FXML
    JFXButton selectExportDirectoryButton;
    @FXML
    JFXButton convertButton;
    @FXML
    JFXButton infoButton;
    @FXML
    Label importCsvFilePathLabel;
    @FXML
    Label baseXliffFilePathLabel;
    @FXML
    Label exportFilePathLabel;
    @FXML
    JFXComboBox selectCsvColumnComboBox;
    @FXML
    FontAwesomeIconView importCsvIcon;
    @FXML
    FontAwesomeIconView exportIcon;
    @FXML
    FontAwesomeIconView infoIcon;
    @FXML
    FontAwesomeIconView importBaseXliffIcon;
    @FXML
    JFXToggleButton commasToggle;
    @FXML
    JFXToggleButton semiColonsToggle;
    @FXML
    Pane infoPane;

    private char delimiter;
    private List<CSVRecord> csvRecords;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        importCsvIcon.setMouseTransparent(true);
        exportIcon.setMouseTransparent(true);
        importBaseXliffIcon.setMouseTransparent(true);
        infoIcon.setMouseTransparent(true);
        semiColonsToggle.setSelected(true);
        delimiter = ';';
    }

    public void handleDelimiterToggles(ActionEvent ae) {
        if (ae.getSource() == semiColonsToggle && !semiColonsToggle.isSelected()) {
            commasToggle.setSelected(true);
            delimiter = ',';
        }
        if (ae.getSource() == commasToggle && !commasToggle.isSelected()) {
            semiColonsToggle.setSelected(true);
            delimiter = ';';
        }
        if (commasToggle.isSelected()) {
            delimiter = ',';
        } else if (semiColonsToggle.isSelected()) {
            delimiter = ';';
        }
    }

    public void handleImportCsvButton() {
        Stage stage = (Stage) selectCsvButton.getScene().getWindow();
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Open CSV File");
        try {
            File importedXliff = filechooser.showOpenDialog(stage);
            importCsvFilePathLabel.setText(importedXliff.getAbsolutePath());
            commasToggle.setDisable(true);
            semiColonsToggle.setDisable(true);
            showRecordsInComboBox();
        }
        catch (NullPointerException npe) {
            System.out.println("No file selected");
        }
    }

    public void handleImportBaseXliffButton() {
        Stage stage = (Stage) selectXliffButton.getScene().getWindow();
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Open Base Xliff File");
        try {
            File importedXliff = filechooser.showOpenDialog(stage);
            baseXliffFilePathLabel.setText(importedXliff.getAbsolutePath());
        }
        catch (NullPointerException npe) {
            System.out.println("No file selected");
        }
    }

    public void handleExportDirectoryButton() {
        Stage stage = (Stage) selectExportDirectoryButton.getScene().getWindow();
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Choose Export Directory");
        try {
            File importedXliff = filechooser.showSaveDialog(stage);
            exportFilePathLabel.setText(importedXliff.getAbsolutePath());
        }
        catch (NullPointerException npe) {
            System.out.println("No directory selected");
        }
    }

    public void handleInfoButton() {
        infoPane.toFront();
    }

    public void sendInfoToBackground() {
        infoPane.toBack();
    }

    public void showRecordsInComboBox() {
        try {
            FileReader fileReader = new FileReader(importCsvFilePathLabel.getText());
            BufferedReader bufferedReader = new BufferedReader(fileReader);//
            CSVParser parser = CSVFormat.RFC4180
                    .withFirstRecordAsHeader()
                    .withDelimiter(delimiter)
                    .parse(bufferedReader);
            csvRecords = parser.getRecords();
            Map<String, Integer> headerMap = parser.getHeaderMap();
            ObservableList comboBoxHeadings = FXCollections.observableArrayList();
            comboBoxHeadings.addAll(headerMap.keySet());
            selectCsvColumnComboBox.getItems().clear();
            selectCsvColumnComboBox.getItems().addAll(comboBoxHeadings);
            fileReader.close();
            bufferedReader.close();
        } catch (IOException e) {
            importCsvFilePathLabel.setText("");
            selectCsvColumnComboBox.getItems().clear();
            semiColonsToggle.setDisable(false);
            commasToggle.setDisable(false);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Invalid Delimiter");
            alert.setTitle("This CSV File does not use your selected delimiter.");
            alert.setContentText("Please try selecting a different delimiter type.");
            alert.showAndWait();
        }
    }

    public void handleConvertButton() {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        if (csvRecords.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("No CSV Records");
            alert.setTitle("The Selected CSV contains no data.");
            alert.setContentText("Please select a different CSV.");
            alert.showAndWait();
        }
        else {
            try {
                File baseXliff = new File(baseXliffFilePathLabel.getText());
                int selectedColumn = selectCsvColumnComboBox.getSelectionModel().getSelectedIndex();

                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(baseXliff);
                document.getDocumentElement().normalize();

                NodeList listOfTransUnits = document.getElementsByTagName("trans-unit");

                for (int i = 0; i < listOfTransUnits.getLength(); i++) {

                    Node node = listOfTransUnits.item(i);
                    System.out.println(listOfTransUnits.item(i).getAttributes());

                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element transUnit = (Element) node;
                        CSVRecord record = csvRecords.get(i);
                        System.out.println(record.toString());

                        if (transUnit.getElementsByTagName("target").getLength() == 0) {
                            Element targetNode = document.createElement("target");
                            targetNode.setTextContent(record.get(selectedColumn));
                            transUnit.insertBefore(targetNode, transUnit.getElementsByTagName("note").item(0));
                        } else {
                            Node targetNode = transUnit.getElementsByTagName("target").item(0);
                            targetNode.setTextContent(record.get(selectedColumn));
//                            transUnit.insertBefore(targetNode, transUnit.getElementsByTagName("note").item(i));
                        }
                    }
                }
                DOMSource source = new DOMSource(document);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                File newXliff = new File(exportFilePathLabel.getText());
                StreamResult result = new StreamResult(newXliff);
                transformer.transform(source, result);

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        }
    }
}
