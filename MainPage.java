import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;


public class MainPage extends JDialog {
    private JButton btnCart;
    private JButton btnCancel;
    private JButton btnSaved;
    private JButton btnProfil;
    private JTextField tfPrice1;
    private JTextField tfPrice2;
    private JButton btnApplyFilters;
    private JButton btnSearch;
    private JPanel mainPanel;
    private JTextField tfShowResult;
    private JButton btnShowResult;
    private JComboBox listCategory;
    private JComboBox listBrand;
    private JScrollPane scrollResult;
    private JTextArea textArea;
    private double minPrice = 0;
    private double maxPrice = Double.MAX_VALUE;

    public MainPage(JFrame parent) {
        super(parent);
        setTitle("Login");
        setContentPane(mainPanel);
        setMinimumSize(new Dimension(800, 600));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        textArea = new JTextArea();
        scrollResult.setViewportView(textArea);

        // Получаем список категорий из базы данных
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "5658");
            Statement statement = connection.createStatement();
            String categoryQuery = "SELECT DISTINCT category FROM my_database"; // Запрос для получения уникальных категорий
            ResultSet categoryResultSet = statement.executeQuery(categoryQuery);

            // Создаем список для хранения категорий
            List<String> categories = new ArrayList<>();

            // Добавляем "All category" в начало списка
            categories.add("All category");

            // Добавляем все категории из результата запроса
            while (categoryResultSet.next()) {
                String category = categoryResultSet.getString("category");
                categories.add(category);
            }

            // Закрываем ресурсы
            categoryResultSet.close();
            statement.close();
            connection.close();

            // Заполняем выпадающий список категорий
            for (String category : categories) {
                listCategory.addItem(category);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Получаем список брендов из базы данных
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "5658");
            Statement statement = connection.createStatement();
            String brandQuery = "SELECT DISTINCT brand FROM my_database"; // Запрос для получения уникальных брендов
            ResultSet brandResultSet = statement.executeQuery(brandQuery);

            // Создаем список для хранения брендов
            List<String> brands = new ArrayList<>();

            // Добавляем "All brand" в начало списка
            brands.add("All brand");

            // Добавляем все бренды из результата запроса
            while (brandResultSet.next()) {
                String brand = brandResultSet.getString("brand");
                brands.add(brand);
            }

            // Закрываем ресурсы
            brandResultSet.close();
            statement.close();
            connection.close();

            // Заполняем выпадающий список брендов
            for (String brand : brands) {
                listBrand.addItem(brand);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        btnProfil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Действия при нажатии на кнопку профиля
            }
        });

        btnSaved.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Действия при нажатии на кнопку сохраненных
            }
        });
        btnCart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Действия при нажатии на кнопку корзины
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        btnApplyFilters.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    minPrice = Double.parseDouble(tfPrice1.getText());
                    maxPrice = Double.parseDouble(tfPrice2.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MainPage.this, "Please enter valid price values.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "5658");
                    Statement statement = connection.createStatement();

                    // Получаем выбранную категорию и бренд
                    String selectedCategory = (String) listCategory.getSelectedItem();
                    String selectedBrand = (String) listBrand.getSelectedItem();

                    // Формируем SQL-запрос
                    String query = "SELECT * FROM my_database WHERE price >= " + minPrice + " AND price <= " + maxPrice;

                    // Добавляем условие по категории, если выбрана конкретная категория
                    if (!selectedCategory.equals("All category")) {
                        query += " AND category = '" + selectedCategory + "'";
                    }

                    // Добавляем условие по бренду, если выбран конкретный бренд
                    if (!selectedBrand.equals("All brand")) {
                        query += " AND brand = '" + selectedBrand + "'";
                    }

                    ResultSet resultSet = statement.executeQuery(query);

                    textArea.setText("");

                    displayDataInTextArea(resultSet);

                    resultSet.close();
                    statement.close();
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });


        btnShowResult.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "5658");
                    Statement statement = connection.createStatement();

                    String query;
                    if (!tfShowResult.getText().isEmpty()) {
                        String searchText = tfShowResult.getText();
                        query = "SELECT * FROM my_database WHERE prod_name LIKE '" + searchText + "%'";
                    } else {
                        query = "SELECT * FROM my_database";
                    }

                    ResultSet resultSet = statement.executeQuery(query);

                    textArea.setText("");

                    displayDataInTextArea(resultSet);

                    resultSet.close();
                    statement.close();
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void displayDataInTextArea(ResultSet resultSet) throws SQLException {
        StringBuilder resultText = new StringBuilder();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                resultText.append(resultSet.getString(i)).append("\t");
            }
            resultText.append("\n");
        }
        textArea.setText(resultText.toString());
    }

    public static void main(String[] args) {
        MainPage mainpage = new MainPage(null);
        mainpage.setVisible(true);
    }
}
