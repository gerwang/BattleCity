package javagames.completegame.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Gerwa on 2017/5/16.
 */
public class TTTMenu {
    private JPanel MenuPanel;
    private JLabel Status;
    private JLabel IpText;
    private JTextField ipAddress;
    private JButton connectButton;
    private JLabel Caption;
    private JLabel portLabel;
    private JTextField portText;
    private JLabel ServerText;
    private JButton launchButton;
    private JLabel ClientText;
    private JButton mapDesigner;

    public JPanel getMenuPanel() {
        return MenuPanel;
    }

    public void setMenuPanel(JPanel menuPanel) {
        MenuPanel = menuPanel;
    }

    public JLabel getStatus() {
        return Status;
    }

    public JButton getMapDesigner() {
        return mapDesigner;
    }

    public void setMapDesigner(JButton mapDesigner) {
        this.mapDesigner = mapDesigner;
    }

    public void setStatus(JLabel status) {
        Status = status;
    }

    public JLabel getIpText() {
        return IpText;
    }

    public void setIpText(JLabel ipText) {
        IpText = ipText;
    }

    public JTextField getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(JTextField ipAddress) {
        this.ipAddress = ipAddress;
    }

    public JButton getConnectButton() {
        return connectButton;
    }

    public void setConnectButton(JButton connectButton) {
        this.connectButton = connectButton;
    }

    public JLabel getCaption() {
        return Caption;
    }

    public void setCaption(JLabel caption) {
        Caption = caption;
    }

    public JLabel getPortLabel() {
        return portLabel;
    }

    public void setPortLabel(JLabel portLabel) {
        this.portLabel = portLabel;
    }

    public JTextField getPortText() {
        return portText;
    }

    public void setPortText(JTextField portText) {
        this.portText = portText;
    }

    public JLabel getServerText() {
        return ServerText;
    }

    public void setServerText(JLabel serverText) {
        ServerText = serverText;
    }

    public JButton getLaunchButton() {
        return launchButton;
    }

    public void setLaunchButton(JButton launchButton) {
        this.launchButton = launchButton;
    }

    public JLabel getClientText() {
        return ClientText;
    }

    public void setClientText(JLabel clientText) {
        ClientText = clientText;
    }
}
