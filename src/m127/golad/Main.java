package m127.golad;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import golad.bot.BotFactory;
import m127.golad.bot.GameUIFactory;
import m127.golad.ui.JOptionPaneMessenger;
import m127.golad.ui.StatsDisplayThread;
import m127.golad.ui.UIUtil;

public class Main extends JFrame implements ActionListener,ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5233124852206097413L;

	public static enum Mode {
		LOCAL,SERVER,HEADLESS;
	}
	
	public static void main(String[] args) {
		Mode mode=Mode.LOCAL;
		if(args.length>0) mode=Mode.valueOf(args[0].toUpperCase());
		if(mode==null) {
			System.err.printf("Illegal mode: %s%n",args[0]);
			return;
		}
		switch(mode) {
		case HEADLESS:
			System.err.println("Unsupported mode.");
			break;
		case LOCAL:
			Class<? extends BotFactory<?>> red=GameUIFactory.class,blue=red;
			int w=20,h=20,initCells=0,numGames=1;
			for(int i=1;i+1<args.length;i+=2) {
				switch(args[i].toLowerCase()) {
				case "--width":
					w=Integer.parseInt(args[i+1]);
					break;
				case "--height":
					h=Integer.parseInt(args[i+1]);
					break;
				case "--initcells":
					initCells=Integer.parseInt(args[i+1]);
					break;
				case "--numgames":
					numGames=Integer.parseInt(args[i+1]);
					break;
				case "--red":
					try {
						red=(Class<? extends BotFactory<?>>) ClassLoader.getSystemClassLoader().loadClass(args[i+1]);
					} catch (ClassNotFoundException|ClassCastException e) {
						System.err.println("Illegall class(red).");
						e.printStackTrace();
						return;
					}
					break;
				case "--blue":
					try {
						blue=(Class<? extends BotFactory<?>>) ClassLoader.getSystemClassLoader().loadClass(args[i+1]);
					} catch (ClassNotFoundException|ClassCastException e) {
						System.err.println("Illegall class(blue).");
						e.printStackTrace();
						return;
					}
				}
			}
			Main m=new Main(red.getCanonicalName(),blue.getCanonicalName(),w,h,initCells,numGames);
			m.pack();
			m.setVisible(true);
			break;
		case SERVER:
			System.err.println("Unsupported mode.");
			break;
		}
	}
	@SuppressWarnings("unused")
	private static final String CHR="CHooseRed",OR="OpenRed",CHB="CHooseBlue",OB="OpenBlue",ECP="ExtendClassPath",SGA="StartGAme";
	private static final String[] DEFAULT_BOTS={GameUIFactory.class.getCanonicalName()};
	
	private ClassLoader loader;
	private JComboBox<String> red,blue;
	private SpinnerNumberModel w,h,initCells,numGames;
	private JButton start;
	public Main(ClassLoader loader, String red, String blue, int w, int h, int initCells, int numGames) {
		super("");
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.loader = loader==null?ClassLoader.getSystemClassLoader():loader;
		this.red = new JComboBox<>(DEFAULT_BOTS);
		this.red.setEditable(true);
		this.red.setActionCommand(CHR);
		this.red.addActionListener(this);
		this.red.setSelectedItem(red);
		this.blue = new JComboBox<>(DEFAULT_BOTS);
		this.blue.setEditable(true);
		this.blue.setActionCommand(CHB);
		this.blue.addActionListener(this);
		this.blue.setSelectedItem(blue);
		this.w = new SpinnerNumberModel(w,2,Integer.MAX_VALUE,2);
		this.h = new SpinnerNumberModel(h,2,Integer.MAX_VALUE,2);
		this.initCells = new SpinnerNumberModel(initCells,0,w*h/2,1);
		this.numGames = new SpinnerNumberModel(numGames,1,Integer.MAX_VALUE,1);
		JSpinner sw = new JSpinner(this.w),sh=new JSpinner(this.h),si=new JSpinner(this.initCells),sn=new JSpinner(this.numGames);
		sw.addChangeListener(this);
		sh.addChangeListener(this);
		this.start = new JButton("Start");
		this.start.setActionCommand(SGA);
		this.start.addActionListener(this);
		JPanel content=new JPanel(new GridLayout(0,1,5,5));
		content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		content.add(UIUtil.wrapBorder(this.red,"Red"));
		content.add(UIUtil.wrapBorder(this.blue,"Blue"));
		content.add(UIUtil.wrapBorder(sw,"Width"));
		content.add(UIUtil.wrapBorder(sh,"Height"));
		content.add(UIUtil.wrapBorder(si,"Initial Cells"));
		content.add(UIUtil.wrapBorder(sn,"Number of Games"));
		content.add(start);
		setContentPane(content);
	}
	public Main(String red, String blue, int w, int h, int initCells, int numGames) {
		this(null,red,blue,w,h,initCells,numGames);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case CHR:
			try {
				if(!BotFactory.class.isAssignableFrom(loader.loadClass(red.getSelectedItem().toString()))) throw new ClassCastException();
			} catch (ClassNotFoundException | ClassCastException e1) {
				red.setSelectedIndex(0);
				new JOptionPaneMessenger(((e1 instanceof ClassNotFoundException)?"Could not find class.":"Invalid class.")+"\nReverting to default.","Error").start();
			}
			break;
		case CHB:
			try {
				if(!BotFactory.class.isAssignableFrom(loader.loadClass(blue.getSelectedItem().toString()))) throw new ClassCastException();
			} catch (ClassNotFoundException | ClassCastException e1) {
				blue.setSelectedIndex(0);
				new JOptionPaneMessenger(((e1 instanceof ClassNotFoundException)?"Could not find class.":"Invalid class.")+"\nReverting to default.","Error").start();
			}
			break;
		case SGA:
			BotFactory<?> r,b;
			try {
				r=loader.loadClass(red.getSelectedItem().toString()).asSubclass(BotFactory.class).newInstance();
				b=loader.loadClass(blue.getSelectedItem().toString()).asSubclass(BotFactory.class).newInstance();
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e1) {
				new JOptionPaneMessenger("Error while initializing bots.", "Error").start();
				return;
			}
			final int initCells=this.initCells.getNumber().intValue();
			Game g;
			if(initCells==0) g=new Game(r,b,w.getNumber().intValue(),h.getNumber().intValue());
			else g=new Game(r,b,w.getNumber().intValue(),h.getNumber().intValue(),initCells);
			new StatsDisplayThread(new GameThread(g,numGames.getNumber().intValue())).start();
			break;
		default:
			new JOptionPaneMessenger("Unsupported Action.","Error").start();
		}
	}
	@Override
	public void stateChanged(ChangeEvent e) {
		final int initMax=w.getNumber().intValue()*h.getNumber().intValue()/2;
		initCells.setMaximum(initMax);
		if(initCells.getNumber().intValue()>initMax) initCells.setValue(initMax);
	}
}
