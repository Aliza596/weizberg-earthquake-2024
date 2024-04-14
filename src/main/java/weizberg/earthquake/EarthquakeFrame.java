package weizberg.earthquake;

import hu.akarnokd.rxjava3.swing.SwingSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import weizberg.earthquake.json.Feature;
import weizberg.earthquake.json.FeatureCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EarthquakeFrame extends JFrame {
    private JList<String> jlistOneHour = new JList<>();
    private JList<String> jlistOneMonth = new JList<>();
    private JRadioButton oneHourButton = new JRadioButton("One Hour");
    private JRadioButton oneMonthButton = new JRadioButton("30 days");

    public EarthquakeFrame() {

        setTitle("EarthquakeFrame");
        setSize(300, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel buttons = new JPanel();
        buttons.add(oneHourButton);
        buttons.add(oneMonthButton);
        add(buttons, BorderLayout.NORTH);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(oneHourButton);
        buttonGroup.add(oneMonthButton);


        EarthquakeService service = new EarthquakeServiceFactory().getService();

        Disposable disposableOneHour = service.oneHour()
                // tells Rx to request the data on a background Thread
                .subscribeOn(Schedulers.io())
                // tells Rx to handle the response on Swing's main Thread
                .observeOn(SwingSchedulers.edt())
                //.observeOn(AndroidSchedulers.mainThread()) // Instead use this on Android only
                .subscribe(
                        (response) -> handleResponse(response, jlistOneHour),
                        Throwable::printStackTrace);

        Disposable disposableOneMonth = service.oneMonth()
                // tells Rx to request the data on a background Thread
                .subscribeOn(Schedulers.io())
                // tells Rx to handle the response on Swing's main Thread
                .observeOn(SwingSchedulers.edt())
                //.observeOn(AndroidSchedulers.mainThread()) // Instead use this on Android only
                .subscribe(
                        (response) -> handleResponse(response, jlistOneMonth),
                        Throwable::printStackTrace);

        oneHourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (oneHourButton.isSelected()) {
                    addOneHour();
                }
            }
        });

        oneMonthButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (oneMonthButton.isSelected()) {
                    addOneMonth();
                }
            }
        });


    }

    private void addOneHour() {
        getContentPane().add(jlistOneHour, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void addOneMonth() {
        getContentPane().add(jlistOneMonth, BorderLayout.CENTER);
        revalidate();
        repaint();
    }


    private void handleResponse(FeatureCollection response, JList<String> jlist) {

        String[] listData = new String[response.features.length];
        for (int i = 0; i < response.features.length; i++) {
            Feature feature = response.features[i];
            listData[i] = feature.properties.mag + " " + feature.properties.place;
        }
        jlist.setListData(listData);
    }

    public static void main(String[] args) {
        new EarthquakeFrame().setVisible(true);
    }

}