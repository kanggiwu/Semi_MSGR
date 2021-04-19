package msgr.client;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Imoticon extends JDialog implements ActionListener {
	JPanel				jPanel1			= new JPanel();
	MessengerChatView	msgrChatView	= null;
	JButton				pic0			= new JButton();
	JButton				pic1			= new JButton();
	JButton				pic2			= new JButton();
	JButton				pic3			= new JButton();
	JButton				pic4			= new JButton();
	GridLayout			gridLayout1		= new GridLayout(1, 5, 2, 2);
	Image				imag[]			= null;
	ImageIcon			img[]			= new ImageIcon[5];
	String				imgfile[]		= { "RYAN1.png", "RYAN2.png", "RYAN3.png", "RYAN4.png", "RYAN5.png" };

	JButton				imgButton[]		= { pic0, pic1, pic2, pic3, pic4 };
	String				imgChoice		= "default";
	String				path			= "src\\";

	public Imoticon(MessengerChatView msgrChatView) {
		this.msgrChatView = msgrChatView;

		try {
			jbInit();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {

		pic0.addActionListener(this);
		pic1.addActionListener(this);
		pic2.addActionListener(this);
		pic3.addActionListener(this);
		pic4.addActionListener(this);
		this.getContentPane().setLayout(null);
		jPanel1.setBackground(Color.white);
		jPanel1.setBorder(BorderFactory.createEtchedBorder());
		jPanel1.setBounds(new Rectangle(6, 6, 480, 149));
		jPanel1.setLayout(gridLayout1);
		pic0.setFont(new java.awt.Font("SansSerif", 0, 12));
		pic0.setBorder(null);
		pic1.setFont(new java.awt.Font("SansSerif", 0, 12));
		pic1.setBorder(null);
		pic2.setFont(new java.awt.Font("SansSerif", 0, 12));
		pic2.setBorder(null);
		pic3.setFont(new java.awt.Font("SansSerif", 0, 12));
		pic3.setBorder(null);
		pic4.setFont(new java.awt.Font("SansSerif", 0, 12));
		pic4.setBorder(null);
		this.getContentPane().setBackground(new Color(125, 144, 177));
		this.setTitle("이모티콘");
		this.getContentPane().add(jPanel1, null);
		jPanel1.add(pic0, null);
		jPanel1.add(pic1, null);
		jPanel1.add(pic2, null);
		jPanel1.add(pic3, null);
		jPanel1.add(pic4, null);

		for (int i = 0; i < img.length; i++) {
			img[i] = new ImageIcon(path + imgfile[i]);
			imgButton[i].setIcon(img[i]);
			imgButton[i].setBorderPainted(false);
			imgButton[i].setFocusPainted(false);
			imgButton[i].setContentAreaFilled(false);
			// int offset = imgButton[i].getInsets().left;
			// imgButton[i].setIcon(resizeIcon(img[i], imgButton[i].getWidth() - offset,
			// imgButton[i].getHeight() - offset));
		}

		setSize(510, 205);
		setResizable(false);
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});

	}

	/*  private static Icon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) { 
		    Image img = icon.getImage(); 
		    Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight, java.awt.Image.SCALE_SMOOTH); 
		    return new ImageIcon(resizedImage); 
		} */
	/*  "고추.gif", "편지.gif", "펭귄.gif", "선물.gif",
	  "커피.gif", "케익.gif", "장미.gif", "졸림.gif",
	  "웃음.gif", "윙크.gif", "미소.gif", "놀람.gif"*/
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
//
//		// JOptionPane.showMessageDialog(mr, "obj:"+obj);
//		if (obj == pic0) {// 고추
//			imgChoice = "RYAN1.png";
//			msgrChatView.message_process("이모티콘", imgChoice);
//			// this.setVisible(false);
//		}
//		else if (obj == pic1) {// 편지
//			imgChoice = "RYAN2.png";
//			msgrChatView.message_process("이모티콘", imgChoice);
//			// this.setVisible(false);
//		}
//		else if (obj == pic2) {// 평귄
//			imgChoice = "RYAN3.png";
//			msgrChatView.message_process("이모티콘", imgChoice);
//			// this.setVisible(false);
//		}
//		else if (obj == pic3) {// 선물
//			imgChoice = "RYAN4.png";
//			msgrChatView.message_process("이모티콘", imgChoice);
//			// this.setVisible(false);
//		}
//		else if (obj == pic4) {// 커피
//			imgChoice = "RYAN5.png";
//			msgrChatView.message_process("이모티콘", imgChoice);
//			// this.setVisible(false);
//		}
	}
}
