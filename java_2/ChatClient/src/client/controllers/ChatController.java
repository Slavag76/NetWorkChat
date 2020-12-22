package client.controllers;

import client.NetworkClient;
import client.models.Network;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class ChatController {

    File fileHistory = new File("D:\\Android разработка\\JavaStream2\\NetWorkChat\\java_2\\ChatClient\\src\\client\\controllers\\fileHistory.txt");
    FileReader fileReader = new FileReader(fileHistory);
    BufferedReader bufferedReader = new BufferedReader(fileReader);



    @FXML
    public ListView<String> usersList;

    @FXML
    private Button sendButton;
    @FXML
    private TextArea chatHistory;
    @FXML
    private TextField textField;
    @FXML
    private Label usernameTitle;

    private Network network;
    private String selectedRecipient;

    public ChatController() throws FileNotFoundException {
    }


    public void setLabel(String usernameTitle) {
        this.usernameTitle.setText(usernameTitle);
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    @FXML
    public void initialize() throws IOException {
//        usersList.setItems(FXCollections.observableArrayList(NetworkClient.USERS_TEST_DATA));
        sendButton.setOnAction(event -> {
            try {
                ChatController.this.sendMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        textField.setOnAction(event -> {
            try {
                ChatController.this.sendMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        usersList.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = usersList.getSelectionModel();
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                usersList.requestFocus();
                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (selectionModel.getSelectedIndices().contains(index)) {
                        selectionModel.clearSelection(index);
                        selectedRecipient = null;
                    } else {
                        selectionModel.select(index);
                        selectedRecipient = cell.getItem();
                    }
                    event.consume();
                }
            });
            return cell;
        });
        for (int i = 0; i < 5; i++) {
            String strLine = bufferedReader.readLine();
            chatHistory.appendText(strLine);
        }


    }

    private void sendMessage() throws IOException {
        String message = textField.getText();


        if (message.isBlank()) {
            return;
        }

        appendMessage("Я: " + message);
        textField.clear();

        try {
            if (selectedRecipient != null) {
                network.sendPrivateMessage(message, selectedRecipient);
            } else {
                network.sendMessage(message);
            }

            String timestamp = DateFormat.getInstance().format(new Date());
            FileWriter writeHistory = new FileWriter(fileHistory, true);
            writeHistory.write(String.format("\n%s %s %s", timestamp, network.getUsername(), message));
            writeHistory.close();


        } catch (IOException e) {
            e.printStackTrace();
            NetworkClient.showErrorMessage("Ошибка подключения", "Ошибка при отправке сообщения", e.getMessage());
        }

    }

    public void appendMessage(String message) throws IOException {
        String timestamp = DateFormat.getInstance().format(new Date());
        chatHistory.appendText(timestamp);
        chatHistory.appendText(System.lineSeparator());
        chatHistory.appendText(message);
        chatHistory.appendText(System.lineSeparator());
        chatHistory.appendText(System.lineSeparator());



    }

    public void setUsernameTitle(String username) {

    }

    public void updateUsers(List<String> users) {
        usersList.setItems(FXCollections.observableArrayList(users));
    }

}