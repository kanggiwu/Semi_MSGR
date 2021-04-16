package msgr.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import msgr.map.MessengerMap;
import msgr.util.MessengerDAO;

public class SignInView extends JFrame implements ActionListener {

	private JLabel			label_id		= null;
	private JLabel			label_pw		= null;
	private JTextField		textField_id	= null;
	private JPasswordField	pwField_pw		= null;
	private JButton			button_signIn	= null;
	private JButton			button_signUp	= null;
	MessengerDAO			msgrDAO			= null;
	private String			nickname		= "";
	private MessengerClient	msgrClient		= null;

	public SignInView() {
		initDisplay();
	}

	public void initDisplay() {

		Font font = new Font("맑은 고딕", Font.PLAIN, 12);
		label_id = new JLabel("ID");
		textField_id = new JTextField("");
		label_pw = new JLabel("PASSWORD");
		pwField_pw = new JPasswordField("");
		button_signIn = new JButton("로그인");
		button_signUp = new JButton("회원가입");
		this.setLayout(null);
		// 아이디 레이블
		label_id.setFont(font);
		label_id.setBounds(40, 10, 100, 20);
		this.add(label_id);
		// 아이디 텍스트필드
		textField_id.setBounds(150, 10, 100, 20);
		textField_id.addActionListener(this);
		this.add(textField_id);
		// 비밀번호 레이블
		label_pw.setFont(font);
		label_pw.setBounds(40, 40, 100, 20);
		this.add(label_pw);
		// 비밀번호 패스워드필드
		pwField_pw.setBounds(150, 40, 100, 20);
		this.add(pwField_pw);
		// 로그인 버튼
		button_signIn.setFont(font);
		button_signIn.setBounds(40, 70, 90, 30);
		button_signIn.addActionListener(this);
		this.add(button_signIn);
		// 회원가입 버튼
		button_signUp.setFont(font);
		button_signUp.setBounds(150, 70, 90, 30);
		button_signUp.addActionListener(this);
		this.add(button_signUp);

		this.getContentPane().setBackground(Color.ORANGE);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("로그인");
		this.setSize(300, 150);
		this.setResizable(false);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		new SignInView();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();

		if (obj == button_signIn) {

			if ("".equals(textField_id.getText()) || "".equals(pwField_pw.getText())) {
				JOptionPane.showMessageDialog(this, "잘못된 아이디 또는 비밀번호입니다.", "경고", JOptionPane.WARNING_MESSAGE);
				textField_id.requestFocus();
				return;
			}

			try {
				MessengerMap voMap = MessengerMap.getInstance();
				voMap.getMap().put("mem_id_vc", textField_id.getText());
				voMap.getMap().put("mem_pw_vc", pwField_pw.getText());
				msgrDAO = MessengerDAO.getInstance();
				List<Map<String, Object>> tempList = msgrDAO.signIn(voMap.getMap());

				System.out.println(tempList.size());

				if (tempList.size() > 0) {
					nickname = String.valueOf(tempList.get(0).get("MEM_NICK_VC"));
				}

				if ("null".equals(nickname) || nickname.length() == 0) {
					JOptionPane.showMessageDialog(this, "잘못된 아이디 또는 비밀번호입니다.", "경고", JOptionPane.WARNING_MESSAGE);
					textField_id.requestFocus();
					return;
				}
				else {
					JOptionPane.showMessageDialog(this, nickname + "님의 접속을 환영합니다.");
					this.setVisible(false);
					textField_id.setText("");
					pwField_pw.setText("");
					msgrClient = new MessengerClient(this);
					msgrClient.getConnection();
				}
			}
			catch (HeadlessException he) {
				he.printStackTrace();
			}
		}
		else if (obj == button_signUp) {
			SignUpView signUpView = new SignUpView();
		}
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}
