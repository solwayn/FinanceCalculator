package sample.AppFinance;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller implements Initializable
{

    @FXML
    Label final_value = new Label();

    @FXML
    Label income = new Label();

    @FXML
    Label outcome = new Label();

    @FXML
    VBox vbox_main = new VBox();

    @FXML
    VBox vbox_left = new VBox();

    @FXML
    ComboBox<String> combo_abstract_elements = new ComboBox<>();

    @FXML
    ChoiceBox<String> choice_outcome_places = new ChoiceBox<>();

    @FXML
    FlowPane flow_pane_months = new FlowPane();

    @FXML
    ListView<AbstractInOutCome> payment_elements = new ListView<>();

    private ArrayList<AbstractInOutCome> globalList;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        Parser ob = new Parser("C:\\Users\\Aleksander\\Desktop\\2017_2019_history.csv");
        Double moneyEarned = 0.0;
        Double moneySpent = 0.0;
        Double finalValue;
        for (AbstractInOutCome ab : ob.readCsvFile())
        {
            System.out.println(ab.getPaymentValue());
            //System.out.println( ab.getPaymentReceiver().getBankAccountNmb() + ", " + (ab.getPaymentReceiver().getCity()) + ", " + ab.getPaymentValue() + ", " + ab.getDescription());

            if (ab instanceof Outcome)
                if (!ab.getPaymentReceiver().getBankAccountNmb().equalsIgnoreCase("39 1020 4795 0000 9002 0400 6672"))
                    moneySpent += ab.getPaymentValue();

            if (ab instanceof Income)
                moneyEarned += ab.getPaymentValue();
        }
        globalList = new ArrayList<>(ob.getList());
        fillChoiceList(createUniqueList(ob.getList()));
        finalValue = (moneyEarned + moneySpent);
        final_value.setText(finalValue.toString());
        income.setText(moneyEarned.toString());
        outcome.setText(moneySpent.toString());

        flow_pane_months.setPadding(new Insets(5, 0, 5, 0));
        flow_pane_months.setVgap(4);
        flow_pane_months.setHgap(4);

    }

    /**
     * Take all items and create unique list without duplicates
     */
    private ArrayList<String> createUniqueList(List<AbstractInOutCome> wholeList)
    {
        ArrayList<String> uniqueList = new ArrayList<>();
        for (AbstractInOutCome ab : wholeList)
        {
            if (!uniqueList.contains(ab.getPaymentReceiver().getAddress()))
                uniqueList.add(ab.getPaymentReceiver().getAddress());
        }

        Collections.sort(uniqueList);
        return uniqueList;
    }

    private void fillChoiceList(ArrayList<String> fillList)
    {
        for (String receiverAdd : fillList)
            combo_abstract_elements.getItems().add(receiverAdd);
    }

    public void onListElementClick()
    {
        payment_elements.setItems(getObservableArrayList(combo_abstract_elements.getValue(), globalList));
        ObservableList list = flow_pane_months.getChildren();
        //list.addAll(getObservableArrayList(combo_abstract_elements.getValue(), globalList));
    }

    private ObservableList<AbstractInOutCome> getObservableArrayList(String paymentReceiverAdd, ArrayList<AbstractInOutCome> list)
    {
        ArrayList<AbstractInOutCome> selectedElementsList = new ArrayList<>();
        io.reactivex.Observable<AbstractInOutCome> observable = io.reactivex.Observable.fromIterable(list);

        observable.filter(element -> element.getPaymentReceiver().getAddress().equalsIgnoreCase(paymentReceiverAdd))
                .map(selectedElementsList::add)
                .doOnError(Throwable::printStackTrace)
                .subscribe();


        return FXCollections.observableArrayList(selectedElementsList);
    }
}
