import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextPane;

import java.awt.Font;
import javax.swing.Box;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientGUI implements Observer{

	private JFrame frmClient;
	private JTextField inputField;
	private JLabel MyNameLabel;
    private JTextField ipField;
	private JTextField portField;
	private JTextField nameField;
	private JButton connectButton;
	private JTextPane outputPane;
	private JButton sendButton;
	private JLabel statusLabel;

	private final ClientControl controller = new ClientControl();
	private boolean isConnect = false;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI window = new ClientGUI();
					window.frmClient.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * registers itself as an observer at the ClientControl
	 */
	public ClientGUI() {
		initialize();
		controller.addObserver(this);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmClient = new JFrame();
		frmClient.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(isConnect) {
					clientConnection();
				}
			}
		});
		frmClient.setIconImage(Toolkit.getDefaultToolkit().getImage("Client/src/images/icon.png"));
		frmClient.setTitle("Client");
		frmClient.setBounds(100, 100, 431, 408);
		frmClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{14, 60, 31, 117, 45, 10, 87, 8, 0};
		gridBagLayout.rowHeights = new int[]{15, 18, 19, 19, 20, 168, 22, 18, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frmClient.getContentPane().setLayout(gridBagLayout);

		Component verticalGlue = Box.createVerticalGlue();
		GridBagConstraints gbc_verticalGlue = new GridBagConstraints();
		gbc_verticalGlue.insets = new Insets(0, 0, 5, 5);
		gbc_verticalGlue.gridx = 3;
		gbc_verticalGlue.gridy = 0;
		frmClient.getContentPane().add(verticalGlue, gbc_verticalGlue);

		JLabel lblNewLabel_3 = new JLabel("Current connection");
		lblNewLabel_3.setFont(new Font("Thoma", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridwidth = 3;
		gbc_lblNewLabel_3.gridx = 4;
		gbc_lblNewLabel_3.gridy = 1;
		frmClient.getContentPane().add(lblNewLabel_3, gbc_lblNewLabel_3);

		JLabel lblNewLabel_1 = new JLabel("MyName:");
		lblNewLabel_1.setFont(new Font("Thoma", Font.PLAIN, 14));
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.NORTH;
		gbc_lblNewLabel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 2;
		frmClient.getContentPane().add(lblNewLabel_1, gbc_lblNewLabel_1);

		MyNameLabel = new JLabel("Client");
		MyNameLabel.setFont(new Font("Thoma", Font.PLAIN, 14));
		GridBagConstraints gbc_MyNameLabel = new GridBagConstraints();
		gbc_MyNameLabel.anchor = GridBagConstraints.NORTH;
		gbc_MyNameLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_MyNameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_MyNameLabel.gridx = 3;
		gbc_MyNameLabel.gridy = 2;
		frmClient.getContentPane().add(MyNameLabel, gbc_MyNameLabel);

		nameField = new JTextField();
		nameField.setFont(new Font("Thoma", Font.PLAIN, 12));
		nameField.setText("Client");
		nameField.setColumns(10);
		GridBagConstraints gbc_nameField = new GridBagConstraints();
		gbc_nameField.fill = GridBagConstraints.BOTH;
		gbc_nameField.insets = new Insets(0, 0, 5, 5);
		gbc_nameField.gridwidth = 3;
		gbc_nameField.gridx = 4;
		gbc_nameField.gridy = 2;
		frmClient.getContentPane().add(nameField, gbc_nameField);

		JLabel lblNewLabel = new JLabel("MyIP:");
		lblNewLabel.setFont(new Font("Thoma", Font.PLAIN, 14));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.NORTH;
		gbc_lblNewLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 3;
		frmClient.getContentPane().add(lblNewLabel, gbc_lblNewLabel);

        JLabel myIpLabel = new JLabel(ClientSocket.getHostAddress());
		myIpLabel.setFont(new Font("Thoma", Font.PLAIN, 14));
		GridBagConstraints gbc_MyIpLabel = new GridBagConstraints();
		gbc_MyIpLabel.anchor = GridBagConstraints.NORTH;
		gbc_MyIpLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_MyIpLabel.insets = new Insets(0, 0, 5, 5);
		gbc_MyIpLabel.gridx = 3;
		gbc_MyIpLabel.gridy = 3;
		frmClient.getContentPane().add(myIpLabel, gbc_MyIpLabel);

		ipField = new JTextField();
		ipField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if((e.getKeyChar() != KeyEvent.VK_PERIOD || ipField.getText().length() > 15) &&
						e.getKeyChar() != KeyEvent.VK_KP_UP && 
						e.getKeyChar() != KeyEvent.VK_KP_LEFT && 
						e.getKeyChar() != KeyEvent.VK_KP_RIGHT &&
						e.getKeyChar() != KeyEvent.VK_KP_DOWN &&
						e.getKeyChar() != KeyEvent.VK_CLEAR &&
						e.getKeyChar() != KeyEvent.VK_CANCEL &&
						e.getKeyChar() != KeyEvent.VK_ENTER) {
					textFieldFilter(ipField, 15);
				}
			}
		});
		ipField.setFont(new Font("Thoma", Font.PLAIN, 12));
		ipField.setText(myIpLabel.getText());
		GridBagConstraints gbc_ipField = new GridBagConstraints();
		gbc_ipField.fill = GridBagConstraints.BOTH;
		gbc_ipField.insets = new Insets(0, 0, 5, 5);
		gbc_ipField.gridwidth = 3;
		gbc_ipField.gridx = 4;
		gbc_ipField.gridy = 3;
		frmClient.getContentPane().add(ipField, gbc_ipField);
		ipField.setColumns(10);

		portField = new JTextField();
		portField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyChar() != KeyEvent.VK_KP_UP && 
						e.getKeyChar() != KeyEvent.VK_KP_LEFT && 
						e.getKeyChar() != KeyEvent.VK_KP_RIGHT &&
						e.getKeyChar() != KeyEvent.VK_KP_DOWN &&
						e.getKeyChar() != KeyEvent.VK_CLEAR &&
						e.getKeyChar() != KeyEvent.VK_CANCEL &&
						e.getKeyChar() != KeyEvent.VK_ENTER) {
				textFieldFilter(portField, 4);
				}
			}
		});
		portField.setFont(new Font("Thoma", Font.PLAIN, 12));
		portField.setText("9999");
		GridBagConstraints gbc_portField = new GridBagConstraints();
		gbc_portField.fill = GridBagConstraints.HORIZONTAL;
		gbc_portField.insets = new Insets(0, 0, 5, 5);
		gbc_portField.gridx = 4;
		gbc_portField.gridy = 4;
		frmClient.getContentPane().add(portField, gbc_portField);
		portField.setColumns(10);

		connectButton = new JButton("Connect");
		connectButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				clientConnection();
			}
		});
		connectButton.setFont(new Font("Thoma", Font.PLAIN, 12));
		GridBagConstraints gbc_connectButton = new GridBagConstraints();
		gbc_connectButton.fill = GridBagConstraints.BOTH;
		gbc_connectButton.insets = new Insets(0, 0, 5, 5);
		gbc_connectButton.gridwidth = 2;
		gbc_connectButton.gridx = 5;
		gbc_connectButton.gridy = 4;
		frmClient.getContentPane().add(connectButton, gbc_connectButton);

		Component horizontalGlue_1 = Box.createHorizontalGlue();
		GridBagConstraints gbc_horizontalGlue_1 = new GridBagConstraints();
		gbc_horizontalGlue_1.anchor = GridBagConstraints.WEST;
		gbc_horizontalGlue_1.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalGlue_1.gridx = 0;
		gbc_horizontalGlue_1.gridy = 5;
		frmClient.getContentPane().add(horizontalGlue_1, gbc_horizontalGlue_1);

		outputPane = new JTextPane();
		outputPane.setEditable(false);
		outputPane.setEnabled(false);
		outputPane.setFont(new Font("Thoma", Font.PLAIN, 12));
		JScrollPane scrollerPane = new JScrollPane(outputPane);
		GridBagConstraints gbc_outputPane = new GridBagConstraints();
		gbc_outputPane.fill = GridBagConstraints.BOTH;
		gbc_outputPane.insets = new Insets(0, 0, 5, 5);
		gbc_outputPane.gridwidth = 6;
		gbc_outputPane.gridx = 1;
		gbc_outputPane.gridy = 5;
		frmClient.getContentPane().add(scrollerPane, gbc_outputPane);

		Component horizontalGlue_2 = Box.createHorizontalGlue();
		GridBagConstraints gbc_horizontalGlue_2 = new GridBagConstraints();
		gbc_horizontalGlue_2.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalGlue_2.gridx = 7;
		gbc_horizontalGlue_2.gridy = 5;
		frmClient.getContentPane().add(horizontalGlue_2, gbc_horizontalGlue_2);
		
		inputField = new JTextField();
		inputField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}
		});
		inputField.setEnabled(false);
		inputField.setFont(new Font("Thoma", Font.PLAIN, 12));
		GridBagConstraints gbc_inputField = new GridBagConstraints();
		gbc_inputField.anchor = GridBagConstraints.SOUTH;
		gbc_inputField.fill = GridBagConstraints.HORIZONTAL;
		gbc_inputField.insets = new Insets(0, 0, 5, 5);
		gbc_inputField.gridwidth = 5;
		gbc_inputField.gridx = 1;
		gbc_inputField.gridy = 6;
		frmClient.getContentPane().add(inputField, gbc_inputField);
		inputField.setColumns(10);

		sendButton = new JButton("send");
		sendButton.setEnabled(false);
		sendButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				sendMessage();
			}
		});
		sendButton.setFont(new Font("Thoma", Font.PLAIN, 12));
		GridBagConstraints gbc_sendButton = new GridBagConstraints();
		gbc_sendButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_sendButton.insets = new Insets(0, 0, 5, 5);
		gbc_sendButton.gridx = 6;
		gbc_sendButton.gridy = 6;
		frmClient.getContentPane().add(sendButton, gbc_sendButton);

		statusLabel = new JLabel("Not connected to the server");
		statusLabel.setFont(new Font("Thoma", Font.PLAIN, 14));
		GridBagConstraints gbc_statusLabel = new GridBagConstraints();
		gbc_statusLabel.insets = new Insets(0, 0, 5, 5);
		gbc_statusLabel.fill = GridBagConstraints.BOTH;
		gbc_statusLabel.gridwidth = 6;
		gbc_statusLabel.gridx = 1;
		gbc_statusLabel.gridy = 8;
		frmClient.getContentPane().add(statusLabel, gbc_statusLabel);

		Component verticalGlue_1 = Box.createVerticalGlue();
		GridBagConstraints gbc_verticalGlue_1 = new GridBagConstraints();
		gbc_verticalGlue_1.insets = new Insets(0, 0, 0, 5);
		gbc_verticalGlue_1.gridx = 3;
		gbc_verticalGlue_1.gridy = 9;
		frmClient.getContentPane().add(verticalGlue_1, gbc_verticalGlue_1);
	}

	/**
	 * This method enabled and disabled the UI in depending on isConnect
	 */
	private void uiSwitch() {
		if(isConnect) {
			nameField.setEnabled(false);
			ipField.setEnabled(false);
			portField.setEnabled(false);
			connectButton.setText("Disconnect");

			MyNameLabel.setText(nameField.getText());

			outputPane.setEnabled(true);
			inputField.setEnabled(true);
			sendButton.setEnabled(true);
			statusLabel.setText("Connected to the server");
		}else {
			nameField.setEnabled(true);
			ipField.setEnabled(true);
			portField.setEnabled(true);
			connectButton.setText("Connect");

			outputPane.setEnabled(false);
			inputField.setEnabled(false);
			sendButton.setEnabled(false);
			statusLabel.setText("Not connected to the server");
		}
	}

	/**
	 * This method check and filter the last character in the reception textField
	 * and remove any character he is not a number, or they are more character in the textField as length.
	 * 
	 * @param field	is the receiving JTextField
	 * @param length is the maximum length in the textField
	 */
	private void textFieldFilter(JTextField field, int length) {
		if(!field.getText().isEmpty()) {
			try {
				Integer.parseInt(field.getText().substring(field.getText().length() - 1));
			}catch (Exception u) {
				field.setText(field.getText().substring(0, field.getText().length() - 1));
			}
		} 
		if((field.getText().length() > length)) {
			field.setText(field.getText().substring(0, field.getText().length() - 1));
		}
	}

	/**
	 * This method called the startSocket and the stopSocket method in depending on isConnect
	 */
	private void clientConnection() {
		if(!isConnect) {
			controller.startSocket(ipField.getText(), Integer.parseInt(portField.getText()), nameField.getText());
		}else {
			controller.stopSocket();
		}
	}

	/**
	 * This method called the write method from ClientControl with the text from inputField.
	 * At the end update the outputPane with the text from inputField and clear the inputField.
	 */
	private void sendMessage() {
		controller.write(inputField.getText());
		outputPane.setText(outputPane.getText()+(outputPane.getText().isEmpty()?"":"\n")+"You write: "+inputField.getText());
		inputField.setText("");
	}

	@Override
    public void update(Observable o, Object arg) {
		State state = controller.getControlState();
		switch(state) {
		case ClientConnection://set isConnect true and call uiSwitch
			isConnect = true;
			uiSwitch();
			break;
		case ClientDisconnection://set isConnect false and call uiSwitch
			isConnect = false;
			uiSwitch();
			break;
		case NewMessage://set the receiving message in the outputPane
			outputPane.setText(outputPane.getText()+(outputPane.getText().isEmpty()?"":"\n")+ controller.getBuffer());
			break;
		case ServerOffline://set the receiving message in the outputPane and set isConnect false and call uiSwitch
			outputPane.setText(outputPane.getText()+(outputPane.getText().isEmpty()?"":"\n")+ controller.getBuffer());
			isConnect = false;
			uiSwitch();
			break;
		default://print a error message
			System.err.println(state+" is a unknown controller state");
			break;
		}
	}
}
