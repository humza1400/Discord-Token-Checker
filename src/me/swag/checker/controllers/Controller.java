package me.swag.checker.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import jdk.nashorn.internal.ir.debug.JSONWriter;
import me.swag.checker.utils.Utils;
import sun.misc.BASE64Encoder;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {

    @FXML
    private AnchorPane window;
    @FXML
    public Label date;
    @FXML
    public Label discordStatus;
    @FXML
    private Label fileName;
    @FXML
    private AnchorPane infoAnchor;
    @FXML
    private Button importButton;
    @FXML
    private Button checkButton;
    @FXML
    private Button exportButton;
    @FXML
    private TextArea yourTokenTextArea;
    @FXML
    private Label tokensLoaded;
    @FXML
    private ProgressBar loadingBar;
    @FXML
    private TextArea checkedTokens;
    @FXML
    private Button copyButton;
    @FXML
    private RadioButton userTokenButton;
    @FXML
    private RadioButton botTokenButton;
    @FXML
    private Label tokenHits;
    @FXML
    private Label tokensChecked;


    private List<String> tokens = new ArrayList<>();

    public void initialize() {
        date.setText(Utils.getDate());
        discordStatus.setText(Utils.getDiscordStatus());
        loadingBar.setProgress(0);
    }


    public void onAction(ActionEvent event) {
        if (event.getSource() == importButton) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
            fileChooser.setTitle("Import Your Tokens");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File file = fileChooser.showOpenDialog(infoAnchor.getScene().getWindow());
            if (file != null) {
                fileName.setText("File: " + file.getName());
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    StringBuilder stringBuilder = new StringBuilder();
                    String yo;
                    int loaded = 0;
                    while ((yo = bufferedReader.readLine()) != null) {
                        if (!yo.equalsIgnoreCase("")) {
                            stringBuilder.append(yo).append("\n");
                            loaded++;
                        }
                    }
                    yourTokenTextArea.setText(stringBuilder.toString());
                    tokensLoaded.setText("Tokens Loaded: " + loaded);
                    bufferedReader.close();
                } catch (IOException ex) {
                    yourTokenTextArea.setText("Something went wrong importing tokens");
                }
            }
        } else if (event.getSource() == checkButton) {
            if (!yourTokenTextArea.getText().isEmpty())
                try {
                    BufferedReader bufferedReader = new BufferedReader(new StringReader(yourTokenTextArea.getText()));
                    String yo;
                    while ((yo = bufferedReader.readLine()) != null) {
                        if (!tokens.contains(yo + "\n")) {
                            tokens.add(yo + "\n");
                        }
                    }
                    tokensLoaded.setText("Tokens Loaded: " + tokens.size());
                    bufferedReader.close();
                    List<String> tokens2check = tokens.stream().filter((token) -> !checkedTokens.getText().contains(token)).collect(Collectors.toList());
                    ArrayList<String> outputTokens = new ArrayList<>();
                    for (String token : tokens2check) {
                        try {
                            boolean checkForBot = botTokenButton.isSelected();
                            URL url;
                            if (checkForBot) {
                                url = new URL("https://discord.com/api/v7/users/@me");
                            } else
                                url = new URL("https://canary.discordapp.com/api/v7/users/@me");

                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) discord/0.0.306 Chrome/78.0.3904.130 Electron/7.1.11 Safari/537.36");
                            connection.setRequestProperty("Content-Type", "application/json");
                            connection.setRequestProperty("Authorization", (checkForBot ? "Bot " + token.trim() : token.trim()));
                            if (connection.getResponseCode() >= 200) {
                                InputStream inputStream = connection.getInputStream();
                                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                                StringBuilder sb = new StringBuilder();
                                String line;
                                while ((line = br.readLine()) != null) {
                                    sb.append("\"").append(token.replace("\n","")).append("\", ").append(line).append("\n");
                                }
                                br.close();
                                connection.disconnect();
                                outputTokens.add(sb.toString());
                            } else {
                                System.out.println(connection.getResponseMessage());
                                connection.disconnect();
                            }
                        } catch (MalformedURLException ex) {
                            checkedTokens.setText("Couldn't connect to the Discord API");
                            ex.printStackTrace();
                        }
                    }
                    tokensChecked.setText("Tokens Checked: " + tokens2check.size());
                    tokenHits.setText("Token Hits: " + outputTokens.size());
                    checkedTokens.setText(outputTokens.toString());

                } catch (IOException ex) {
                    checkedTokens.setText("Something went wrong when checking tokens");
                    ex.printStackTrace();
                }
        } else if (event.getSource() == exportButton) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
            fileChooser.setTitle("Save Your Tokens");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            fileChooser.setInitialFileName("tokens");
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                Utils.saveTextToFile(checkedTokens.getText(), file);
            }
        } else if (event.getSource() == copyButton) {
            if (copyButton.getText().equalsIgnoreCase("Copied")) {
                copyButton.setText("Copy");
            } else if (copyButton.getText().equalsIgnoreCase("Copy") && !checkedTokens.getText().isEmpty()) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(checkedTokens.getText()), null);
                copyButton.setText("Copied");
            }

        }
    }

    public void onRadioButtonClick(MouseEvent mouseEvent) {
        userTokenButton.setSelected(!botTokenButton.isSelected());
    }

    public void onMouseClickedUser(MouseEvent event) {
        botTokenButton.setSelected(!userTokenButton.isSelected());
    }

}
