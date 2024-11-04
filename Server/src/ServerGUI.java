import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.JButton;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.ImageIcon;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ServerGUI implements Observer{

	private JFrame frmServer;
	private JTextField portField;
	private JTextField inputField;
	private JTextPane outputPane;
	private JButton sendButton;
	private JButton on_off_Button;
	private JButton removeButton;

	private final ServerControl controller = new ServerControl();
	private boolean isConnect = false;
	private JLabel statusLabel;

	private int id = -1;
	private boolean isSelect = false;

	private JScrollPane pane;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGUI window = new ServerGUI();
					window.frmServer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * registers itself as an observer at the ServerControl
	 */
	public ServerGUI() {
		initialize();
		controller.addObserver(this);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmServer = new JFrame();
		frmServer.setResizable(false);
		frmServer.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(isConnect) {
					serverConnection();
				}
			}
		});
		frmServer.setIconImage(Toolkit.getDefaultToolkit().getImage("Server/src/images/icon.png"));
		frmServer.setTitle("Server");
		frmServer.setBounds(100, 100, 534, 365);
		frmServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmServer.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("MyIP:");
		lblNewLabel.setBounds(10, 23, 49, 14);
		frmServer.getContentPane().add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("MyPort:");
		lblNewLabel_1.setBounds(10, 48, 49, 14);
		frmServer.getContentPane().add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel(ServerSock.getHostAddress());
		lblNewLabel_2.setBounds(88, 23, 96, 14);
		frmServer.getContentPane().add(lblNewLabel_2);

		portField = new JTextField();
		portField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				textFieldFilter(portField);
			}
		});
		portField.setText("9999");
		portField.setBounds(88, 45, 96, 20);
		frmServer.getContentPane().add(portField);
		portField.setColumns(10);

		outputPane = new JTextPane();
		outputPane.setEnabled(false);
		outputPane.setEditable(false);
		outputPane.setBounds(204, 23, 295, 184);
		JScrollPane scrollerPane = new JScrollPane(outputPane);
		scrollerPane.setBounds(204, 23, 295, 184);
		frmServer.getContentPane().add(scrollerPane);

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
		inputField.setBounds(204, 218, 201, 20);
		frmServer.getContentPane().add(inputField);
		inputField.setColumns(10);

		sendButton = new JButton("send\r\n");
		sendButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				sendMessage();
			}
		});
		sendButton.setEnabled(false);
		sendButton.setBounds(410, 217, 89, 23);
		frmServer.getContentPane().add(sendButton);

		on_off_Button = new JButton("");
		on_off_Button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				serverConnection();
			}
		});
		on_off_Button.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage("Server/src/images/onButton.jpeg")));
		on_off_Button.setBounds(10, 218, 25, 25);
		on_off_Button.setBorder(new Border() {

			@Override
			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
				g.drawRoundRect(x, y, width-1, height-1, 10, 10);
			}

			@Override
			public boolean isBorderOpaque() {
				return true;
			}

			@Override
			public Insets getBorderInsets(Component c) {
				return new Insets(11, 11, 12, 10);
			}
		});
		frmServer.getContentPane().add(on_off_Button);

		removeButton = new JButton("Remove");
		removeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				removeRowFromTable();
			}
		});
		removeButton.setEnabled(false);
		removeButton.setBounds(50, 218, 132, 23);
		frmServer.getContentPane().add(removeButton);

		statusLabel = new JLabel("Server is offline");
		statusLabel.setFont(new Font("Thoma", Font.PLAIN, 14));
		statusLabel.setBounds(10, 285, 390, 17);
		frmServer.getContentPane().add(statusLabel);

		table = new JTable() ;
		table.setSurrendersFocusOnKeystroke(true);
		table.setModel(new DefaultTableModel(
				new Object[][] {},
				new String[] {"ID", "Name", "IP"}));
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(0).setPreferredWidth(18);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(1).setPreferredWidth(50);
		table.getColumnModel().getColumn(2).setResizable(false);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				selectRowFromTable(row);
			}
		});

		pane = new JScrollPane(table);
		pane.setEnabled(false);
		pane.setBounds(10, 80, 174, 127);
		frmServer.getContentPane().add(pane);
	}

	/**
	 * This method enabled and disabled the UI in depending on isConnect
	 */
	private void uiSwitch() {
		if(isConnect) {
			portField.setEnabled(false);
			pane.setEnabled(true);
			table.setEnabled(true);
			on_off_Button.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage("Server/src/images/offIcon.jpeg")));

			outputPane.setEnabled(true);
			inputField.setEnabled(true);
			sendButton.setEnabled(true);

			statusLabel.setText("Server is online");
		}else {
			portField.setEnabled(true);
			pane.setEnabled(false);
			table.setEnabled(false);
			on_off_Button.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage("Server/src/images/onButton.jpeg")));

			outputPane.setEnabled(false);
			inputField.setEnabled(false);
			sendButton.setEnabled(false);

			statusLabel.setText("Server is offline");

			table.setModel(new DefaultTableModel(
					new Object[][] {},
					new String[] {"ID", "Name", "IP"}));
			table.getColumnModel().getColumn(0).setResizable(false);
			table.getColumnModel().getColumn(0).setPreferredWidth(18);
			table.getColumnModel().getColumn(1).setResizable(false);
			table.getColumnModel().getColumn(1).setPreferredWidth(50);
			table.getColumnModel().getColumn(2).setResizable(false);
			table.getColumnModel().getColumn(2).setPreferredWidth(100);
		}
	}
	
	/**
	 * This method managed the selection row from table.
	 * 
	 * @param row is the select row
	 */
	private void selectRowFromTable(int row) {
		if (!isSelect && id == -1) {
			id = row;
			table.addRowSelectionInterval(row, row);
			removeButton.setEnabled(true);
			removeButton.setText("Remove "+table.getValueAt(row,1).toString());
			isSelect = !isSelect;
		}else if (isSelect && id == row){
			id = -1;
			table.clearSelection();
			removeButton.setEnabled(false);
			removeButton.setText("Remove");
			isSelect = !isSelect;
		}else {
			id = row;
			table.addRowSelectionInterval(row, row);
			removeButton.setEnabled(true);
			removeButton.setText("Remove "+table.getValueAt(row,1).toString());
			isSelect = !isSelect;
		}
	}
	
	/**
	 * This method remove the selected row from the table
	 */
	private void removeRowFromTable() {
		controller.removeClient(table.getSelectedRow());
		DefaultTableModel tm = (DefaultTableModel) table.getModel();
		tm.removeRow(table.convertRowIndexToModel(table.getSelectedRow()));

		table.clearSelection();
		removeButton.setEnabled(false);
		removeButton.setText("Remove");
		id = -1;
		isSelect = !isSelect;
	}
	
	/**
	 * This method check and filter the last character in the reception textField
	 * and remove any character he is not a number, or they are more character in the textField as length.
	 *
	 * @param field is the receiving JTextField
	 */
	private void textFieldFilter(JTextField field) {
		if(!field.getText().isEmpty()) {
			try {
				Integer.parseInt(field.getText().substring(field.getText().length() - 1));
			}catch (Exception u) {
				field.setText(field.getText().substring(0, field.getText().length() - 1));
			}
		} 
		if((field.getText().length() > 4)) {
			field.setText(field.getText().substring(0, field.getText().length() - 1));
		}
	}
	
	/**
	 * This method called the startServer and the stopServer method in depending on isConnect
	 */
	private void serverConnection() {
		if(!isConnect) {
			controller.startServer(Integer.parseInt(portField.getText()));
		}else {
			controller.stopServer();
		}
	}
	
	/**
	 * This method called the write method from ServerControl with the text from inputField.
	 * At the end update the outputPane with the text from inputField and clear the inputField.
	 */
	private void sendMessage() {
		controller.write(inputField.getText());
		outputPane.setText(outputPane.getText()+(outputPane.getText().isEmpty()?"":"\n")+inputField.getText());
		inputField.setText("");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void update(Observable o, Object arg) {
		State state = controller.getControlState();
		switch(state) {
		case ClientOffline://remove the offline client from the table
			DefaultTableModel tm = (DefaultTableModel) table.getModel();
			tm.removeRow(table.convertRowIndexToModel(Integer.parseInt(arg.toString())));
			break;
		case NewClient://add the new client on the table
			Vector vector = new Vector(table.getColumnCount());
			vector.add(String.valueOf(table.getRowCount()));
			vector.add(arg.toString().split(";")[0]);
			vector.add(arg.toString().split(";")[1]);
			((DefaultTableModel) table.getModel()).addRow(vector);
			break;
		case NewMessage://set the receiving message in the outputPane
			outputPane.setText(outputPane.getText()+(outputPane.getText().isEmpty()?"":"\n")+ controller.getBuffer());
			break;
		case ServerBound://set isConnect true and call uiSwitch
			isConnect = true;
			uiSwitch();
			break;
		case ServerClose://set isConnect false and call uiSwitch
			isConnect = false;
			uiSwitch();
			break;
		default://print a error message
			System.err.println(state+" is a unknown controller state");
			break;
		}
	}
}
