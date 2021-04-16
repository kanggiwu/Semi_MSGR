package msgr.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import msgr.map.MessengerMap;
import msgr.util.MessengerDAO;

public class SignUpView extends JDialog implements ActionListener {

	private JLabel			label_id			= null;
	private JLabel			label_idCheck		= null;
	private JLabel			label_pw			= null;
	private JLabel			label_pwCheck		= null;
	private JLabel			label_nickname		= null;
	private JLabel			label_nicknameCheck	= null;
	private JTextField		textField_id		= null;
	private JPasswordField	pwField_pw			= null;
	private JTextField		textField_nickname	= null;
	private JButton			button_signUp		= null;
	private JButton			button_close		= null;
	private JButton			button_idCheck		= null;
	MessengerDAO			dao					= null;
	private String			id					= "";
	private boolean			isChecked			= false;

	public SignUpView() {
		initDisplay();
	}

	private void initDisplay() {
		Font	font		= new Font("맑은 고딕", Font.BOLD, 14);
		Font	labelFont	= new Font("맑은 고딕", Font.PLAIN, 12);

		label_id = new JLabel("ID");
		label_idCheck = new JLabel();
		textField_id = new JTextField();

		label_pw = new JLabel("PASSWORD");
		label_pwCheck = new JLabel("20자 이내로 입력하세요.");
		pwField_pw = new JPasswordField();

		label_nickname = new JLabel("NICKNAME");
		label_nicknameCheck = new JLabel("한글 10자, 영어 20자 이내로 입력해주세요.");
		textField_nickname = new JTextField();

		button_signUp = new JButton("가입");
		button_close = new JButton("닫기");
		button_idCheck = new JButton("중복확인");

		this.setLayout(null);
		// 아이디 레이블
		label_id.setFont(font);
		label_id.setBounds(40, 20, 100, 20);
		this.add(label_id);
		// 아이디 체크 레이블
		label_idCheck.setFont(labelFont);
		label_idCheck.setBounds(130, 40, 200, 20);
		this.add(label_idCheck);
		// 아이디 텍스트필드
		textField_id.setBounds(130, 20, 120, 20);
		this.add(textField_id);

		// 비밀번호 레이블
		label_pw.setFont(font);
		label_pw.setBounds(40, 80, 100, 20);
		this.add(label_pw);
		// 비밀번호 체크 레이블
		label_pwCheck.setFont(labelFont);
		label_pwCheck.setBounds(130, 100, 200, 20);
		this.add(label_pwCheck);
		// 비밀번호 패스워드필드
		pwField_pw.setBounds(130, 80, 120, 20);
		this.add(pwField_pw);

		// 닉네임 레이블
		label_nickname.setFont(font);
		label_nickname.setBounds(40, 140, 100, 20);
		this.add(label_nickname);
		// 닉네임 체크 레이블
		label_nicknameCheck.setFont(labelFont);
		label_nicknameCheck.setBounds(30, 160, 240, 20);
		this.add(label_nicknameCheck);
		// 닉네임 텍스트필드
		textField_nickname.setBounds(130, 140, 120, 20);
		this.add(textField_nickname);
		// 회원가입 버튼
		button_signUp.setFont(font);
		button_signUp.setBounds(100, 200, 90, 30);
		button_signUp.addActionListener(this);
		this.add(button_signUp);
		// 취소 버튼
		button_close.setFont(font);
		button_close.setBounds(230, 200, 90, 30);
		button_close.addActionListener(this);
		this.add(button_close);
		// 중복체크 버튼
		button_idCheck.setFont(labelFont);
		button_idCheck.setBounds(255, 20, 90, 20);
		button_idCheck.addActionListener(this);
		this.add(button_idCheck);

		this.getContentPane().setBackground(Color.ORANGE);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle("회원가입");
		this.setSize(400, 300);
		this.setResizable(false);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();

		// 회원가입 버튼을 눌렀을 경우
		if (obj == button_signUp) {

			if ("".equals(textField_id.getText()) || "".equals(pwField_pw.getText()) || "".equals(textField_nickname.getText())) {

				// 중복확인을 했으나 아이디를 재입력했을 경우 다시 중복확인을 요청함
				if (!id.equals(textField_id.getText())) {
					isChecked = false;

					if (!isChecked) {
						JOptionPane.showMessageDialog(this, "아이디 중복확인을 해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
				JOptionPane.showMessageDialog(this, "입력되지 않은 칸이 있습니다.", "경고", JOptionPane.WARNING_MESSAGE);
				return;
			}
			else {

				// 중복확인을 했으나 아이디를 재입력했을 경우 다시 중복확인을 요청함
				if (!id.equals(textField_id.getText())) {
					isChecked = false;

					if (!isChecked) {
						JOptionPane.showMessageDialog(this, "아이디 중복확인을 해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
				int				idCheck	= 0;
				MessengerMap	pMap	= MessengerMap.getInstance();
				dao = MessengerDAO.getInstance();
				pMap.getMap().put("mem_id_vc", textField_id.getText());
				pMap.getMap().put("mem_pw_vc", pwField_pw.getText());
				pMap.getMap().put("mem_nick_vc", textField_nickname.getText());
				idCheck = dao.signUp(pMap.getMap());

				if (idCheck == 1) {
					JOptionPane.showMessageDialog(this, "회원가입 성공", "알림", JOptionPane.INFORMATION_MESSAGE);
					this.dispose();
				}
				else if (idCheck == -1) {
					JOptionPane.showMessageDialog(this, "회원가입 실패", "알림", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
		// 닫기 버튼을 눌렀을 경우
		else if (obj == button_close) {
			this.dispose();
		}
		// 중복확인 버튼을 눌렀을 경우
		else if (obj == button_idCheck) {

			if ("".equals(textField_id.getText())) {
				label_idCheck.setText("아이디를 입력해주세요.");
				textField_id.requestFocus();
			}
			else {
				MessengerMap pMap = MessengerMap.getInstance();
				pMap.getMap().put("mem_id_vc", textField_id.getText());
				List<Map<String, Object>> tempList = new Vector<>();
				dao = MessengerDAO.getInstance();
				tempList = dao.idCheck(pMap.getMap());

				if (tempList.size() > 0) {
					JOptionPane.showMessageDialog(this, "중복된 아이디입니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
					label_idCheck.setText("");
				}
				else {
					label_idCheck.setText("");
					JOptionPane.showMessageDialog(this, "가입 가능한 아이디입니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
					id = textField_id.getText();
					isChecked = true;
				}
			}
		} // end of 중복체크
	}
}
