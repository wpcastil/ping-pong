import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;

public class PongServer extends JFrame implements KeyListener, Runnable, WindowListener {

	private static  String TITLE = "PING-PONG";
	private static  int WIDTH = 800;
	private static  int HEIGHT = 460;

	boolean isRunning = false;
	boolean check = true;
	boolean initgame = false;

	private PlayerServer playerS;
	private PlayerClient playerC;
	Ball movingBALL;

	private int ballVEL = 4;
	private int barR = 30;
	private int playerH = 120;
	private int max_Score = 9;
	private int mPLAYER = 5;
	private boolean Restart = false;
	private boolean restartON = false;

	private static Socket clientSoc = null;
	private static ServerSocket serverSoc = null;
	private int portAdd;

	private Graphics g;
	private Font sFont = new Font("TimesRoman", Font.BOLD, 90);
	private Font mFont = new Font("TimesRoman", Font.BOLD, 50);
	private Font nFont = new Font("TimesRoman", Font.BOLD, 32);
	private Font rFont = new Font("TimesRoman", Font.BOLD, 18);
	private String[] message;
	private Thread movB;

	public PongServer(String servername, String portAdd) {

		playerS = new PlayerServer();
		playerC = new PlayerClient("");
		playerS.setName(servername);

		this.portAdd = Integer.parseInt(portAdd);
		this.isRunning = true;
		this.setTitle(TITLE + "::port number[" + portAdd + "]");
		this.setSize(WIDTH, HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setResizable(false);

		movingBALL = new Ball(playerS.getBallx(), playerS.getBally(), ballVEL, ballVEL, 45, WIDTH, HEIGHT);

		addKeyListener(this);
	}

	@Override
	public void run() {
		try {
			serverSoc = new ServerSocket(portAdd);
			System.out.println("Servidor iniciou na porta " + portAdd + ".\nAguardando jogador...");
			System.out.println("Aguardando conexão.");
			playerS.setImessage("Aguardando novo jogador.");
			clientSoc = serverSoc.accept();

			System.out.println("Conectou um jogador");

			if (clientSoc.isConnected()) {

				boolean notchecked = true;
				movB = new Thread(movingBALL);
				while (true) {

					if (playerS.getScoreP() >= max_Score || playerS.getScoreS() >= max_Score && Restart == false) {

						if (playerS.getScoreS() > playerS.getScoreP()) {
							playerS.setOmessage("Ganhou               Perdeu-Para sair, pressione ESC ou N");
							playerS.setImessage("Ganhou               Perdeu");
							Restart = true;
						} else {
							playerS.setImessage("Perdeu              Ganhou-Para sair, pressione ESC ou N");
							playerS.setOmessage("Perdeu              Ganhou-Para sair, pressione ESC ou N");
							Restart = true;
						}
						movB.suspend();
					}

					if (playerC.ok && notchecked) {
						playerS.setImessage("");
						movB.start();
						notchecked = false;
					}

					updateBall();

					ObjectInputStream getObj = new ObjectInputStream(clientSoc.getInputStream());
					playerC = (PlayerClient) getObj.readObject();
					getObj = null;

					ObjectOutputStream sendObj = new ObjectOutputStream(clientSoc.getOutputStream());
					sendObj.writeObject(playerS);
					sendObj = null;

					if (restartON) {

						if (playerC.restart) {
							playerS.setScoreP(0);
							playerS.setScoreS(0);
							playerS.setOmessage("");
							playerS.setImessage("");
							Restart = false;
							playerS.setRestart(false);
							playerS.setBallx(380);
							playerS.setBally(230);
							movingBALL.setX(380);
							movingBALL.setY(230);
							movB.resume();
							restartON = false;
						}
					}
					repaint();
				}
			} else {
				System.out.println("Desconectado.");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private Image createImage() {

		BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = bufferedImage.createGraphics();

		g.setColor(new Color(15, 9, 9));
		g.fillRect(0, 0, WIDTH, HEIGHT);

		g.setColor(Color.white);
		g.fillRect(WIDTH / 2 - 5, 0, 5, HEIGHT);
		g.fillRect(WIDTH / 2 + 5, 0, 5, HEIGHT);

		g.setFont(sFont);
		g.setColor(new Color(228, 38, 36));
		g.drawString("" + playerS.getScoreS(), WIDTH / 2 - 60, 120);
		g.drawString("" + playerS.getScoreP(), WIDTH / 2 + 15, 120);

		g.setFont(nFont);
		g.setColor(Color.white);
		g.drawString(playerS.getName(), WIDTH / 10, HEIGHT - 20);
		g.drawString(playerC.getName(), 600, HEIGHT - 20);

		g.setColor(new Color(57, 181, 74));
		g.fillRect(playerS.getX(), playerS.getY(), barR, playerH);
		g.setColor(new Color(57, 181, 74));
		g.fillRect(playerC.getX(), playerC.getY(), barR, playerH);

		g.setColor(new Color(255, 255, 255));
		g.fillOval(playerS.getBallx(), playerS.getBally(), 45, 45);
		g.setColor(new Color(228, 38, 36));
		g.fillOval(playerS.getBallx() + 5, playerS.getBally() + 5, 45 - 10, 45 - 10);

		message = playerS.getImessage().split("-");
		g.setFont(mFont);
		g.setColor(Color.white);
		if (message.length != 0) {
			g.drawString(message[0], WIDTH / 4 - 31, HEIGHT / 2 + 38);
			if (message.length > 1) {
				if (message[1].length() > 6) {
					g.setFont(rFont);
					g.setColor(new Color(228, 38, 36));
					g.drawString(message[1], WIDTH / 4 - 31, HEIGHT / 2 + 100);
				}
			}
		}
		return bufferedImage;
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(createImage(), 0, 0, this);
	}

	public void updateBall() {

		checkCol();

		playerS.setBallx(movingBALL.getX());
		playerS.setBally(movingBALL.getY());

	}

	public void playerUP() {
		if (playerS.getY() - mPLAYER > playerH / 2 - 10) {

			playerS.setY(playerS.getY() - mPLAYER);
		}
	}

	public void playerDOWN() {
		if (playerS.getY() + mPLAYER < HEIGHT - playerH - 30) {

			playerS.setY(playerS.getY() + mPLAYER);
		}
	}

	public void checkCol() {

		if (playerS.getBallx() < playerC.getX() && playerS.getBallx() > playerS.getX()) {
			check = true;
		}

		if (playerS.getBallx() > playerC.getX() && check) {

			playerS.setScoreS(playerS.getScoreS() + 1);

			check = false;
		}

		else if (playerS.getBallx() <= playerS.getX() && check) {

			playerS.setScoreP(playerS.getScoreP() + 1);

			check = false;

		}

		if (movingBALL.getX() <= playerS.getX() + barR && movingBALL.getY() + movingBALL.getRadius() >= playerS.getY()
				&& movingBALL.getY() <= playerS.getY() + playerH) {
			movingBALL.setX(playerS.getX() + barR);
			playerS.setBallx(playerS.getX() + barR);
			movingBALL.setXv(movingBALL.getXv() * -1);
		}

		if (movingBALL.getX() + movingBALL.getRadius() >= playerC.getX()
				&& movingBALL.getY() + movingBALL.getRadius() >= playerC.getY()
				&& movingBALL.getY() <= playerC.getY() + playerH) {
			movingBALL.setX(playerC.getX() - movingBALL.getRadius());
			playerS.setBallx(playerC.getX() - movingBALL.getRadius());
			movingBALL.setXv(movingBALL.getXv() * -1);
		}

	}

	@Override
	public void keyPressed(KeyEvent arg0) {

		int keycode = arg0.getKeyCode();
		if (keycode == KeyEvent.VK_UP) {
			playerUP();
			repaint();
		}
		if (keycode == KeyEvent.VK_DOWN) {
			playerDOWN();
			repaint();
		}
		if (Restart == true) {
			restartON = true;
			playerS.setRestart(true);
		}

		if (keycode == KeyEvent.VK_N || keycode == KeyEvent.VK_ESCAPE && Restart == true) {
			try {
				this.setVisible(false);
				serverSoc.close();
				System.exit(EXIT_ON_CLOSE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void windowClosing(WindowEvent arg0) {
		Thread.currentThread().stop();
		this.setVisible(false);
		try {
			serverSoc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(1);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {

	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}

	@Override
	public void windowActivated(WindowEvent arg0) {

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		System.exit(1);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {

	}

	@Override
	public void windowIconified(WindowEvent arg0) {

	}

	@Override
	public void windowOpened(WindowEvent arg0) {

	}

}
