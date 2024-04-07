import javax.swing.*;
import java.awt.*;

public class UserListGUI extends JFrame {
    private JList<String> userList;

    public UserListGUI(String[] users) {
        setTitle("Список пользователей");
        setSize(300, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        userList = new JList<>(users);
        JScrollPane scrollPane = new JScrollPane(userList);
        add(scrollPane, BorderLayout.CENTER);
    }

    public static void showUserList(String[] users) {
        EventQueue.invokeLater(() -> {
            UserListGUI userListGUI = new UserListGUI(users);
            userListGUI.setVisible(true);
        });
    }
}
