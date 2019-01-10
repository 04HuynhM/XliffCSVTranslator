package classes.XliffToCSV;

import classes.Models.TransUnit;
import javafx.scene.control.Alert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

class CsvExportFunctions {

    public boolean writeCSV(ArrayList<TransUnit> transUnits,
                  String sourceLanguage,
                  boolean commasAsDelimiter,
                  boolean quotedText,
                  boolean containsTarget,
                  String targetLanguage,
                  String exportFilePath) {

        if (transUnits.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText("No Translation Units Found");
            alert.setContentText("The imported Xliff doesn't contain any strings to convert into a CSV.");
            alert.showAndWait();

            return false;
        }

        final String ID_HEADER = "id";
        final String DESCRIPTION_HEADER = "description";
        String fileHeader;
        String delimiter;


        final String NEW_LINE_SEPARATOR = "\n";

        if (commasAsDelimiter) {
            delimiter = ",";
        }
        else {
            delimiter = ";";
        }

        try {
            File CsvFromXliff = new File(exportFilePath + ".csv");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(CsvFromXliff));

            if(quotedText) {

                if(!containsTarget) {
                    fileHeader = "\"" + ID_HEADER + "\"" + delimiter
                            + "\"" + DESCRIPTION_HEADER + "\"" + delimiter
                            + "\"" + sourceLanguage + "\"";
                } else {
                    fileHeader = "\"" + ID_HEADER + "\"" + delimiter
                            + "\"" + DESCRIPTION_HEADER + "\"" + delimiter
                            + "\"" + sourceLanguage + "\"" + delimiter + "\"" + targetLanguage + "\"";
                }

                bufferedWriter.append(fileHeader).append(NEW_LINE_SEPARATOR);

                for (TransUnit transUnit : transUnits) {
                    bufferedWriter.append("\"").append(transUnit.getId().trim()).append("\"");
                    bufferedWriter.append(delimiter);
                    bufferedWriter.append("\"").append(transUnit.getNote().trim()).append("\"");
                    bufferedWriter.append(delimiter);
                    bufferedWriter.append("\"").append(transUnit.getSource().trim()).append("\"");
                    bufferedWriter.append(delimiter);
                    if(containsTarget) {
                        bufferedWriter.append("\"").append(transUnit.getTarget().trim()).append("\"");
                    }
                    bufferedWriter.append(NEW_LINE_SEPARATOR);
                }
            } else {
                if(!containsTarget) {
                    fileHeader = ID_HEADER + delimiter + DESCRIPTION_HEADER + delimiter + sourceLanguage;
                    bufferedWriter.append(fileHeader).append(NEW_LINE_SEPARATOR);
                } else {
                    fileHeader = ID_HEADER + delimiter + DESCRIPTION_HEADER + delimiter + sourceLanguage + delimiter
                            + targetLanguage;
                    bufferedWriter.append(fileHeader).append(NEW_LINE_SEPARATOR);
                }

                for (TransUnit transUnit : transUnits) {
                    bufferedWriter.append(transUnit.getId().trim());
                    bufferedWriter.append(delimiter);
                    bufferedWriter.append(transUnit.getNote().trim());
                    bufferedWriter.append(delimiter);
                    bufferedWriter.append(transUnit.getSource().trim());
                    bufferedWriter.append(delimiter);
                    if(containsTarget) {
                        bufferedWriter.append(transUnit.getTarget().trim());
                    }
                    bufferedWriter.append(NEW_LINE_SEPARATOR);
                }
            }
            bufferedWriter.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
