import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;

    public WeatherAppGui() {
        super("Aplicatie Meteo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 650);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        addGuiComponents();
    }

    private void addGuiComponents() {
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15, 15, 351, 45);
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);

        JLabel weatherConditionImage = new JLabel(loadImage("src/img/cloudy.png"));
        weatherConditionImage.setBounds(0, 145, 450, 210);
        add(weatherConditionImage);

        JLabel temperatureText = new JLabel("10°C");
        temperatureText.setBounds(0, 355, 450, 50);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 50));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        JLabel weatherConditionDesc = new JLabel("Cer innorat");
        weatherConditionDesc.setBounds(0, 405, 450, 50);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 24));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        JLabel humidityImage = new JLabel(loadImage("src/img/humidity.png"));
        humidityImage.setBounds(15, 500, 75, 66);
        add(humidityImage);

        JLabel humidityText = new JLabel("<html><b>Umiditate</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        JLabel windSpeedImage = new JLabel(loadImage("src/img/windspeed.png"));
        windSpeedImage.setBounds(200, 500, 75, 66);
        add(windSpeedImage);

        JLabel windSpeedText = new JLabel("<html><b>Viteza vantului</b> 15 km/h</html>");
        windSpeedText.setBounds(285, 500, 120, 55);
        windSpeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windSpeedText);

        JButton searchButton = new JButton(loadImage("src/img/search.png"));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(370, 15, 45, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = searchTextField.getText();
                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }
                weatherData = WeatherApp.getWeatherData(userInput);

                if (weatherData == null) {
                    System.out.println("Datele meteo nu au putut fi obținute.");
                    return;
                }

                String weatherCondition = (String) weatherData.get("weather_condition");

                switch (weatherCondition) {
                    case "Senin":
                        weatherConditionImage.setIcon(loadImage("src/img/clear.png"));
                        break;
                    case "Cer innorat":
                        weatherConditionImage.setIcon(loadImage("src/img/cloudy.png"));
                        break;
                    case "Ploaie":
                        weatherConditionImage.setIcon(loadImage("src/img/rain.png"));
                        break;
                    case "Zapada":
                        weatherConditionImage.setIcon(loadImage("src/img/snow.png"));
                        break;
                }

                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + "°C");
                weatherConditionDesc.setText(weatherCondition);
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Umiditate</b> " + humidity + "%</html>");
                double windSpeed = (double) weatherData.get("windspeed");
                windSpeedText.setText("<html><b>Viteza vantului</b> " + windSpeed + " km/h</html>");
            }
        });
        add(searchButton);
    }

    private ImageIcon loadImage(String path) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            return new ImageIcon(image);
        } catch (IOException e) {
            System.out.println("Imaginea nu a putut fi incarcata: " + e.getMessage());
            return null;
        }
    }
}
