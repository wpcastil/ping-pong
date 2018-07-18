public class Ball extends Thread{
	
	private int x;
	private int y;
	private double xv;
	private double yv;
	private int radius;
	private int   HEIGHT;
	private int   WIDTH;
	
	@Override
	public void run() {
		while(true){
		move();
		try {
			sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}}
	}
	
	public Ball(int x, int y, double xv, double yv, int radius, int WIDTH, int HEIGHT) {
		super();
		this.x = x;
		this.y = y;
		this.xv = xv;
		this.yv = yv;
		this.radius = radius;
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
	}
 

	public void move(){
		if(x + xv > (WIDTH-radius) - 7){
			x= (WIDTH-radius)-7; 
			xv = xv * -1;

		}
		
		if(x + xv < 9){
			x = 9;
			xv = xv *-1;
		}
		
		if(y + yv < radius/2+7){
			y = 29;
			yv = yv * -1;
		}
		
		if(y + yv > (HEIGHT - radius) - 6)
		{
			y = (HEIGHT-radius)-6; 
			yv = yv * -1;
			
		}
		x += xv;
		y += yv;

	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public double getXv() {
		return xv;
	}
	public void setXv(double xv) {
		this.xv = xv;
	}
	public double getYv() {
		return yv;
	}
	public void setYv(double yv) {
		this.yv = yv;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
}
