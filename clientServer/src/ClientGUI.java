import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientGUI extends JFrame {
    private final JTextField loginField;
    private final JPasswordField passwordField;

    public ClientGUI() {
        setTitle("Клиент: Вход или Регистрация");
        setSize(350, 225); // Немного увеличиваем размер окна для лучшего вида
        setLocationRelativeTo(null); // Центрируем окно
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10); // Добавляем отступы

        loginField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Войти");
        JButton registerButton = new JButton("Регистрация");

        panel.add(new JLabel("Логин:"), gbc);
        panel.add(loginField, gbc);
        panel.add(new JLabel("Пароль:"), gbc);
        panel.add(passwordField, gbc);
        panel.add(loginButton, gbc);
        panel.add(registerButton, gbc);

        Client client = new Client("localhost", 12345);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = loginField.getText();
                String password = new String(passwordField.getPassword());
                String response = client.sendRequest(login, password, "login");
                JOptionPane.showMessageDialog(ClientGUI.this, response);
                if ("Success".equals(response)) {
                    String[] users = {"User1", "User2", "User3"}; // Пример данных
                    UserListGUI.showUserList(users);
                }
            }
        });


        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = loginField.getText();
                String password = new String(passwordField.getPassword());
                String response = client.sendRequest(login, password, "register");
                JOptionPane.showMessageDialog(ClientGUI.this, response);
            }
        });

        add(panel); // Добавляем панель с компонентами в фрейм
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }
}
