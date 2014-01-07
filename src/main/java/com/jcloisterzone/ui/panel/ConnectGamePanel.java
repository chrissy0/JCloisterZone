package com.jcloisterzone.ui.panel;

import static com.jcloisterzone.ui.I18nUtils._;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.apache.mina.core.RuntimeIoException;

import com.jcloisterzone.ui.Client;


public class ConnectGamePanel extends JPanel {

    private final Client client;

    private JTextField hostField;
    private JTextField portField;
    private JButton btnConnect;
    private JLabel message;

    /**
     * Create the panel.
     */
    public ConnectGamePanel(Client client) {
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnConnect.setEnabled(false); //TODO change to Interrupt button
                message.setForeground(Color.BLACK);
                message.setText(_("Connecting") + "...");
                (new AsyncConnect()).start();
            }
        };

        setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        this.client = client;
        setLayout(new MigLayout("", "[80.00][grow]", "[][][][][]"));

        JLabel helpLabel = new JLabel("Enter remote host address.");
        add(helpLabel, "cell 0 0 2 1");

        JLabel hostLabel = new JLabel(_("Host"));
        add(hostLabel, "cell 0 1,alignx left,aligny top, gaptop 10");

        hostField = new JTextField();
        hostField.addActionListener(actionListener);
        add(hostField, "cell 1 1,growx, width 250::");
        hostField.setColumns(10);

        JLabel portLabel = new JLabel(_("Port"));
        add(portLabel, "cell 0 2,alignx left, gaptop 5");

        portField = new JTextField();
        portField.addActionListener(actionListener);
        add(portField, "cell 1 2,growx, width 250::");
        portField.setColumns(10);
        portField.setText(client.getConfig().getPort() + "");

        btnConnect = new JButton(_("Connect"));
        btnConnect.addActionListener(actionListener);
        add(btnConnect, "cell 1 3");

        message = new JLabel("");
        message.setForeground(Color.BLACK);
        add(message, "cell 1 4, height 20");
    }

    class AsyncConnect extends Thread {
        @Override
        public void run() {
            try {
                String hostname = hostField.getText().trim();
                InetAddress addr = InetAddress.getByName(hostname);
                int port = Integer.parseInt(portField.getText());
                client.connect(addr, port);
                return;
            } catch (NumberFormatException nfe) {
                message.setText( _("Invalid port number."));
            } catch (UnknownHostException e1) {
                message.setText( _("Connection failed. Unknown host."));
            } catch (RuntimeIoException ex) {
                if (ex.getCause() instanceof ConnectException) {
                    message.setText( _("Connection refused."));
                } else {
                    message.setText( _("Connection failed."));
                }
            }
            message.setForeground(Color.RED);
            btnConnect.setEnabled(true);
        }
    }

}
