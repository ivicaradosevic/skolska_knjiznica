package fpmoz.sum.ba.school_library.school_library.controller;

import fpmoz.sum.ba.school_library.school_library.SchoolLibraryApplication;
import fpmoz.sum.ba.school_library.school_library.model.Category;
import fpmoz.sum.ba.school_library.school_library.model.Database;
import fpmoz.sum.ba.school_library.school_library.model.User;
import fpmoz.sum.ba.school_library.school_library.model.UserType;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EditUserController implements Initializable {

    static User selectedUser;

    @FXML
    private TextField oib;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private TextField email;

    @FXML
    private ComboBox<UserType> userTypes;

    @FXML
    private ComboBox<String> years;


    @FXML
    public void goBack(ActionEvent evt) {
        Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
        SchoolLibraryApplication.swapScene(stage, "user-view.fxml");
    }

    @FXML
    public void saveUser(ActionEvent evt) {

        if (oib.getText().isEmpty() ||
                firstName.getText().isEmpty() ||
                lastName.getText().isEmpty() ||
                username.getText().isEmpty() ||
                password.getText().isEmpty() ||
                email.getText().isEmpty() ||
                userTypes.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Obavezni podaci");
            alert.setContentText("Molim popunite obavezne podatke");
            alert.showAndWait();
            return;
        }

        if(userTypes.getValue().getName().equals("Učenik") &&  years.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Obavezni podaci");
            alert.setContentText("Molim popunite obavezne podatke");
            alert.showAndWait();
            return;
        }

        if (oib.getText().length() != 11) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Obavezni podaci");
            alert.setContentText("OIB mora imati 11 znakova!");
            alert.showAndWait();
            return;
        }

        try {

            UserType userType = userTypes.getValue();
            String selectedYear = years.getValue();

            Connection connection = Database.CONNECTION;

            String query = "UPDATE user SET oib=?, " +
                    "first_name=?, " +
                    "last_name=?, " +
                    "username=?, " +
                    "password=?, " +
                    "email=?, " +
                    "user_type_id=?, " +
                    "year=? WHERE id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, oib.getText());
            preparedStatement.setString(2, firstName.getText());
            preparedStatement.setString(3, lastName.getText());
            preparedStatement.setString(4, username.getText());
            preparedStatement.setString(5, password.getText());
            preparedStatement.setString(6, email.getText());
            preparedStatement.setLong(7, userType.getId());
            preparedStatement.setLong(8, Integer.parseInt(selectedYear));
            preparedStatement.setLong(9, selectedUser.getId());
            preparedStatement.execute();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Uspjesna radnja");
            alert.setContentText("Korisnik uspješno ažuriran!");
            alert.show();

            Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
            SchoolLibraryApplication.swapScene(stage, "user-view.fxml");

        } catch (Exception e) {
            System.out.println("Greska prilikom ažuriranja korisnika");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška");
            alert.setContentText("Dogodila se greška prilikom ažuriranja korisnika!");
            alert.show();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // popunjavanje godine
        List<String> yearList = new ArrayList<>();
        yearList.add("1");
        yearList.add("2");
        yearList.add("3");
        yearList.add("4");
        yearList.add("5");
        yearList.add("6");
        yearList.add("7");
        yearList.add("8");
        years.setItems(FXCollections.observableList(yearList));

        // user types
        getUserTypesFromDatabase();

        // set values
        this.oib.setText(selectedUser.getOib());
        this.username.setText(selectedUser.getUsername());
        this.password.setText(selectedUser.getPassword());
        this.email.setText(selectedUser.getEmail());
        this.years.setValue(String.valueOf(selectedUser.getYear()));
        this.firstName.setText(selectedUser.getFirst_name());
        this.lastName.setText(selectedUser.getLast_name());

        UserType selectedUserType = new UserType();

        for(UserType c : this.userTypes.getItems()){
            if(c.getId().equals(selectedUser.getUser_type_id())){
                selectedUserType = c;
            }
        }

        this.userTypes.setValue(selectedUserType);

    }

    private void getUserTypesFromDatabase() {
        try {

            Connection connection = Database.CONNECTION;

            Statement statement = connection.createStatement();
            String query = "SELECT id, name FROM usertype";
            ResultSet resultSet = statement.executeQuery(query);

            List<UserType> userTypes = new ArrayList<>();

            while (resultSet.next()) {

                Long id = resultSet.getLong(1);
                String name = resultSet.getString(2);

                UserType userType = new UserType();
                userType.setId(id);
                userType.setName(name);
                userTypes.add(userType);
            }

            this.userTypes.setItems(FXCollections.observableList(userTypes));

        } catch (Exception e) {
            System.out.println("Greška prilikom dohvaćanja tipova korisnika");
        }
    }
}
