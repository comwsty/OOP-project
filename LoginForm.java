import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

public class LoginForm extends JDialog{
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private JButton btnLogIn;
    private JButton btnSignUp;
    private JPanel loginPanel;

    public LoginForm(JFrame parent){
        super(parent);
        setTitle("Login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(450,474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnLogIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String password = String.valueOf(pfPassword.getPassword());
                user = getAuthenticatedUser(email, password);

                if (user != null){
                    dispose();
                    // Открываем главное окно MainPage после успешного входа
                    MainPage mainPage = new MainPage(null);
                    // Делаем главное окно видимым
                    mainPage.setVisible(true);
                }else{
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Email or Password Invalid",
                            "Try again",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnSignUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Register registerForm = new Register(null);
            }
        });
        setVisible(true);
    }

    public User user;
    private User getAuthenticatedUser(String email, String password){
        User user = null;

        final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
        final String USERNAME = "postgres";
        final String PASSWORD = "5658";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM users WHERE email=? and password=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                user = new User();
                user.name = resultSet.getString("name");
                user.email = resultSet.getString("email");
                user.phone = resultSet.getString("phone");
                user.address = resultSet.getString("address");
                user.password = resultSet.getString("password");
            }
            stmt.close();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
            }

        return user;
    }

    public static void main(String[] args) {
        LoginForm loginform = new LoginForm(null);
        User user = loginform.user;
    }
}
