package gui;

import javax.swing.*;
import java.awt.*;
import businessLogic.BLFacade;
import domain.Sale;
import domain.Seller;

public class ReviewGUI extends JFrame {
    public ReviewGUI(Seller currentUser, Sale sale) {
        setTitle("Valorazioa");
        setSize(400, 300);
        setLayout(new GridLayout(4, 1));

        JPanel p1 = new JPanel();
        p1.add(new JLabel("Puntuación (1-5):"));
        JSlider starsSlider = new JSlider(1, 5, 5); 
        starsSlider.setMajorTickSpacing(1);
        starsSlider.setPaintTicks(true);
        starsSlider.setPaintLabels(true);
        p1.add(starsSlider);

        JPanel p2 = new JPanel(new BorderLayout());
        p2.add(new JLabel("Comentario:"), BorderLayout.NORTH);
        JTextArea commentArea = new JTextArea();
        p2.add(new JScrollPane(commentArea), BorderLayout.CENTER);

        JButton btnSend = new JButton("Enviar Valoración");
        btnSend.addActionListener(e -> {
            try {
                BLFacade facade = MainGUI.getBusinessLogic();
                facade.createReview(currentUser.getEmail(), sale.getSaleNumber(), 
                                   commentArea.getText(), starsSlider.getValue());
                JOptionPane.showMessageDialog(this, "¡Gracias por tu valoración!");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(p1);
        add(p2);
        add(btnSend);
        setVisible(true);
    }
}