package javagames.completegame.gui;

import javagames.completegame.object.GameMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Created by Gerwa on 2017/5/16.
 */
public class MapDesigner extends JFrame implements ActionListener {
    GameMap gameMap;
    JPanel panel;
    GroupLayout layout;
    JButton[][] mapButton = new JButton[26][26];
    HashMap<Character, ImageIcon> mapping;
    final static char[] terranChar = {'F', 'B', 'E', 'W'};
    JButton saveButton, cancelButton;
    JFrame reference;

    public MapDesigner(JFrame reference) {
        this.reference = reference;
    }

    public void launch() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                reference.setVisible(true);
            }
        });
        setTitle("Map Designer");
        gameMap = new GameMap();
        gameMap.loadMap();
        gameMap.loadTerran(1);

        panel = new JPanel();
        layout = new GroupLayout(panel);
        panel.setLayout(layout);

        saveButton = new JButton();
        saveButton.setText("save");
        saveButton.addActionListener(this);
        cancelButton = new JButton();
        cancelButton.setText("cancel");
        cancelButton.addActionListener(this);

        mapping = new HashMap<>();
        BufferedImage plain = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = plain.createGraphics();
        g2D.setPaint(Color.BLACK);
        g2D.fillRect(0, 0, 24, 24);
        mapping.put('F', new ImageIcon(plain));
        mapping.put('B', new ImageIcon(gameMap.getBrickWall().getImage()));
        mapping.put('E', new ImageIcon(gameMap.getSteel().getImage()));
        mapping.put('W', new ImageIcon(gameMap.getRiver()[0].getImage()));

        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 26; j++) {
                mapButton[i][j] = new JButton();
                char terran = gameMap.getMap()[i][j];
                mapButton[i][j].setIcon(mapping.get(terran));
                mapButton[i][j].setBorder(BorderFactory.createEmptyBorder());
                mapButton[i][j].setToolTipText(i + " " + j);
                mapButton[i][j].addActionListener(this);
            }
        }

        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        for (int i = 0; i < 26; i++) {
            GroupLayout.ParallelGroup vParallel = layout.createParallelGroup();
            for (int j = 0; j < 26; j++) {
                vParallel.addComponent(mapButton[i][j]);
            }
            hGroup.addGroup(vParallel);
        }
        layout.setVerticalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        for (int j = 0; j < 26; j++) {
            GroupLayout.ParallelGroup hParallel = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
            for (int i = 0; i < 26; i++) {
                hParallel.addComponent(mapButton[i][j]);
            }
            vGroup.addGroup(hParallel);
        }
        layout.setHorizontalGroup(vGroup);

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.X_AXIS));
        subPanel.add(saveButton);
        subPanel.add(cancelButton);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(panel);
        mainPanel.add(subPanel);

        setContentPane(mainPanel);
        pack();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        if (button == saveButton) {
            gameMap.saveTerran(1);
        } else if (button == cancelButton) {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } else {
            String[] args = button.getToolTipText().split(" ");
            int row = Integer.parseInt(args[0]);
            int col = Integer.parseInt(args[1]);
            char[][] map = gameMap.getMap();
            int index = 0;
            while (terranChar[index] != map[row][col]) {
                index++;
            }
            char nextChar = terranChar[(index + 1) % terranChar.length];
            gameMap.getMap()[row][col] = nextChar;
            mapButton[row][col].setIcon(mapping.get(nextChar));
        }
    }
}
