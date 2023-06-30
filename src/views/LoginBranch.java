package views;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import controllers.BranchController;
import controllers.StoreController;
import views.components.Title;
import views.components.Button;
import views.layouts.BasicFrame;

public class LoginBranch extends BasicFrame {
    private JTextField searchField = new JTextField(20);
    private StoreController storeController = new StoreController();
    private BranchController branchController = new BranchController();
    private JList<String> branchList;

    public LoginBranch() {
        super();
        makeBody();
    }

    public void makeBody() {
        bodyPanel.setLayout(new BorderLayout());

        makeBranchList();

        this.add(bodyPanel, BorderLayout.CENTER);
    }

    public void makeBranchList() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new GridLayout(2, 1));
        bodyPanel.add(headerPanel, BorderLayout.NORTH);

        Title titleLabel = new Title("Filiais");
        headerPanel.add(titleLabel);

        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setLayout(new FlowLayout());

        JLabel searchLabel = new JLabel("Cidade da filial: ");
        searchPanel.add(searchLabel);

        searchField.addActionListener(createSearchAction(searchField));
        searchPanel.add(searchField);
        headerPanel.add(searchPanel);
        
        branchList = new JList<String>(storeController.getBranchesAsHTMLTemplate());
        bodyPanel.add(branchList, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        bodyPanel.add(buttonPanel, BorderLayout.SOUTH);

        Button registerButton = new Button("Criar nova");
        registerButton.addActionListener(e -> {
            this.dispose();
            new CreateBranch();
        });
        buttonPanel.add(registerButton);

        Button loginButton = new Button("Logar");
        loginButton.addActionListener(e -> {
            String selectedValue = branchList.getSelectedValue();
            String branchUUID = selectedValue.split("<br>")[0].split(": ")[1].trim();
            handleBranchPopUpLogin(branchUUID);
        });
        buttonPanel.add(loginButton);

        Button deleteButton = new Button("Deletar");
        deleteButton.addActionListener(e -> {
            String selectedValue = branchList.getSelectedValue();
            String branchUUID = selectedValue.split("<br>")[0].split(": ")[1].trim();
            handleBranchPopUpDelete(branchUUID);
        });
        buttonPanel.add(deleteButton);
    }

    public Action createSearchAction(JTextField searchField) {
        Action searchAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchValue = searchField.getText();
                String[] branches = storeController.searchBranchesByCity(searchValue);
                branchList.setListData(branches);
            }
        };

        return searchAction;
    }

    public void handleBranchPopUpLogin(String branchUUID) {
        JPasswordField passwordField = new JPasswordField();
        int option = JOptionPane.showOptionDialog(null, passwordField, "Senha: ",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);

        if (option == JOptionPane.OK_OPTION) {
            String password = new String(passwordField.getPassword());
            boolean response = branchController.authenticateBranch(branchUUID, password);
            if (response) {
                this.dispose();
                new ManageBranch(branchUUID);
            } else {
                JOptionPane.showMessageDialog(null, "Senha incorreta");
            }
        }
    }

    public void handleBranchPopUpDelete(String branchUUID){
        JPasswordField passwordField = new JPasswordField();
        int option = JOptionPane.showOptionDialog(null, passwordField, "Digite a senha da rede:",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);

        if (option == JOptionPane.OK_OPTION) {
            String password = new String(passwordField.getPassword());
            boolean response = storeController.authenticateStore(password);
            if (response) {
                storeController.removeBranch(branchUUID);
                branchList.setListData(storeController.getBranchesAsHTMLTemplate());
            } else {
                JOptionPane.showMessageDialog(null, "Senha incorreta");
            }
        }

    }
}
