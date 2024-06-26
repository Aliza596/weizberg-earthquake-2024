package weizberg.earthquake;

import hu.akarnokd.rxjava3.swing.SwingSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import weizberg.earthquake.json.Feature;
import weizberg.earthquake.json.FeatureCollection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

public class EarthquakeFrame extends JFrame {
    private JList<String> jlist = new JList<>();
    private FeatureCollection featureCollection;
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
        add(jlist);

        EarthquakeService service = new EarthquakeServiceFactory().getService();

        oneHourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Disposable disposableOneHour = service.oneHour()
                        // tells Rx to request the data on a background Thread
                        .subscribeOn(Schedulers.io())
                        // tells Rx to handle the response on Swing's main Thread
                        .observeOn(SwingSchedulers.edt())
                        //.observeOn(AndroidSchedulers.mainThread()) // Instead use this on Android only
                        .subscribe(
                                (response) -> handleResponse(response),
                                Throwable::printStackTrace);
            }
        });

        oneMonthButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
               Disposable disposableOneMonth = service.oneMonth()
                        // tells Rx to request the data on a background Thread
                        .subscribeOn(Schedulers.io())
                        // tells Rx to handle the response on Swing's main Thread
                        .observeOn(SwingSchedulers.edt())
                        //.observeOn(AndroidSchedulers.mainThread()) // Instead use this on Android only
                        .subscribe(
                                (response) -> handleResponse(response),
                                Throwable::printStackTrace);
            }
        });

        jlist.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    int indexOfSelected = jlist.getSelectedIndex();
                    Feature feature = featureCollection.features[indexOfSelected];
                    double longitude = feature.geometry.coordinates[0];
                    double latitude = feature.geometry.coordinates[1];
                    try {
                        Desktop.getDesktop().browse(new URI("https://maps.google.com/?q=" + latitude
                                + "," + longitude));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void handleResponse(FeatureCollection response) {
        featureCollection = response;
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