package main.java;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UserListPanel extends JPanel implements UserStatusListener {
    private final ChatClient client;
    private JList<String> usersList;
    private DefaultListModel<String> userListModel;


    public UserListPanel(ChatClient client) {
        this.client = client;
        this.client.addUserStatusListener(this);

        userListModel = new DefaultListModel<>();
        usersList = new JList<>(userListModel);
        setLayout(new BorderLayout());
        add(new JScrollPane(usersList), BorderLayout.CENTER);

        usersList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    String login = usersList.getSelectedValue();
                    MessagePanel messagePanel = new MessagePanel(client, login);

                    JFrame f = new JFrame("Message: " + login);
                    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    f.setSize(500, 500);
                    f.getContentPane().add(messagePanel, BorderLayout.CENTER);
                    f.setVisible(true);
                }
            }
        });
    }

//    public static void main(String[] args) {
//        ChatClient client = new ChatClient("localhost", 8080);
//
//        UserListPanel userListPanel = new UserListPanel(client);
//        JFrame frame = new JFrame("Users List");
//        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//        frame.setSize(400,600);
//        frame.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                if (JOptionPane.showConfirmDialog(frame, "Are you sure ?") == JOptionPane.OK_OPTION){
//                    try {
//                        client.logoff();
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                    frame.setVisible(false);
//                    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//                }
//            }
//        });
//        frame.getContentPane().add(userListPanel, BorderLayout.CENTER);
//        frame.setVisible(true);
//
//
//        if (client.connect()){
//            try {
//                client.login("test", "test");
//                client.logoff();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    public void online(String login) {
        userListModel.addElement(login);
    }

    @Override
    public void offline(String login) {
        userListModel.removeElement(login);
    }
}