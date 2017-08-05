/*
**网页浏览器主程序
**WebBrowser.java
*/
import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import java.io.*;
import java.net.*;
import java.util.*;

public class WebBrowser extends JFrame implements HyperlinkListener, ActionListener{
	
	//建立工具栏用来显示地址栏
	JToolBar bar = new JToolBar();
	
	//建立网页显示界面
	JTextField jurl = new JTextField(60);
	JEditorPane jEditorPane1 = new JEditorPane();
	JScrollPane scrollPane = new JScrollPane(jEditorPane1);
	
	JFileChooser chooser = new JFileChooser();
	JFileChooser chooser1 = new JFileChooser();
	String htmlSource;
	JWindow window = new JWindow(WebBrowser.this);
	
	JButton button2 = new JButton("窗口还原");
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	
	//建立菜单栏
	JMenuBar jMenuBar1 = new JMenuBar();
	//建立菜单组
	JMenu fileMenu = new JMenu("文件(F)");
	//建立菜单项
	JMenuItem saveAsItem = new JMenuItem("另存为(A)...");
	JMenuItem exitItem = new JMenuItem("退出(I)");
	JMenu editMenu = new JMenu("编辑(E)");
	JMenuItem backItem = new JMenuItem("后退");
	JMenuItem forwardItem = new JMenuItem("前进");
	
	JMenu viewMenu = new JMenu("视图(V)");
	JMenuItem fullscreenItem = new JMenuItem("全屏(U)");
	JMenuItem sourceItem = new JMenuItem("查看源码(C)");
	JMenuItem reloadItem = new JMenuItem("刷新(R)");
	
	//建立工具栏
	JToolBar toolBar = new JToolBar();
	//建立工具栏中的按钮组件
	JButton picSave = new JButton("另存为");
	JButton picBack = new JButton("后退");
	JButton picForward = new JButton("前进");
	JButton picView = new JButton("查看源代码");
	JButton picExit = new JButton("退出");
	
	JLabel label = new JLabel("地址");
	JButton button = new JButton("转向");
	
	Box adress = Box.createHorizontalBox();
	
	//ArrayList对象，用来存放历史地址
	private ArrayList history = new ArrayList();
	//整型变量，表示历史地址的访问顺序
	private int historyIndex;
	
	/**
	 * 构造函数
	 * 初始化图形界面
	 */
	public WebBrowser(){
		setTitle("网页浏览器");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//为JEditorPanel添加事件侦听
		jEditorPane1.addHyperlinkListener(this);
		
		//为组件fileMenu设置热键“F”
		fileMenu.setMnemonic('F');
		
		saveAsItem.setMnemonic('S');
		//为“另存为”组件设置快捷键Ctrl+S
		saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
		
		exitItem.setMnemonic('c');
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,InputEvent.CTRL_MASK));
		
		//将菜单项saveAsItem加入菜单组fileMenu中
		fileMenu.add(saveAsItem);
		//在菜单项中添加隔离
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		
		backItem.setMnemonic('B');
		backItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,InputEvent.CTRL_MASK));
		backItem.setMnemonic('D');
		backItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,InputEvent.CTRL_MASK));
		
		editMenu.setMnemonic('E');
		editMenu.add(backItem);
		editMenu.add(forwardItem);
		
		viewMenu.setMnemonic('v');
		
		fullscreenItem.setMnemonic('U');
		fullscreenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
		sourceItem.setMnemonic('C');
		sourceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_MASK));
		reloadItem.setMnemonic('R');
		reloadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,InputEvent.CTRL_MASK));
		
		Container contentPane = getContentPane();
		
		//设置大小
		scrollPane.setPreferredSize(new Dimension(100,500));
		contentPane.add(scrollPane, BorderLayout.SOUTH);
		
		//在工具栏中添加按钮组件
		toolBar.add(picSave);
		toolBar.addSeparator();
		toolBar.add(picBack);
		toolBar.add(picForward);
		toolBar.addSeparator();
		toolBar.add(picView);
		toolBar.addSeparator();
		toolBar.add(picExit);
		
		contentPane.add(bar,BorderLayout.CENTER);
		contentPane.add(toolBar,BorderLayout.NORTH);
		
		viewMenu.add(fullscreenItem);
		viewMenu.add(sourceItem);
		viewMenu.addSeparator();
		viewMenu.add(reloadItem);
		
		jMenuBar1.add(fileMenu);
		jMenuBar1.add(editMenu);
		jMenuBar1.add(viewMenu);
		
		setJMenuBar(jMenuBar1);
		
		adress.add(label);
		adress.add(jurl);
		adress.add(button);
		bar.add(adress);
		
		//为组件添加事件监听
		saveAsItem.addActionListener(this);
		picSave.addActionListener(this);
		exitItem.addActionListener(this);
		picExit.addActionListener(this);
		backItem.addActionListener(this);
		picBack.addActionListener(this);
		forwardItem.addActionListener(this);
		picForward.addActionListener(this);
		fullscreenItem.addActionListener(this);
		sourceItem.addActionListener(this);
		picView.addActionListener(this);
		reloadItem.addActionListener(this);
		button.addActionListener(this);
		jurl.addActionListener(this);
	}
	
	/*
	 * 实现监听器接口的actionPerformed函数
	 */
	public void actionPerformed(ActionEvent e){
		String url = "";
		//单击转向按钮
		if(e.getSource() == button){
			//获取地址栏的内容
			url = jurl.getText();
			//url不为空，且以“http://”开头
			if(url.length() > 0 && url.startsWith("http://")){
				try{
					//JEditorPane组件显示url的内容链接
					jEditorPane1.setPage(url);
					//将url的内容添加到ArryList对象的history中
					history.add(url);
					//historyIndex的数值设为history对象的长度-1
					historyIndex = history.size()-1;
					//设置成非编辑状态jEditorPane1.setEditable(false);
					//重新布局
					jEditorPane1.revalidate();
				}
				catch(Exception ex){
					//如果链接显示失败，则弹出选择对话框“无法打开该搜索页”
					JOptionPane.showMessageDialog(WebBrowser.this,"无法打开该搜索页","网页浏览器",JOptionPane.ERROR_MESSAGE);
					
				}
			}
			//url不为空，而且不以“http://”开头
			else if(url.length() > 0 && !url.startsWith("http://")){
				//在url前面添加“http://”
				url = "http://" + url ;
				try{
					jEditorPane1.setPage(url );
					history.add(url);
					historyIndex = history.size() - 1 ;
					//设置成非编辑状态jEditorPane1.setEditable(false);
					jEditorPane1.revalidate();
				}
				catch(Exception ex){
					JOptionPane.showMessageDialog(WebBrowser.this, "无法打开该搜索页", "网页浏览器", JOptionPane.ERROR_MESSAGE);	
				}
			}
			
			//没有输入url, 即url为空
			else if(url.length() == 0){
				JOptionPane.showMessageDialog(WebBrowser.this, "请输入链接地址" , "网页浏览器", JOptionPane.ERROR_MESSAGE);
			}
		}
		//输入地址后按Enter键
		else if (e.getSource() == jurl){
			url = jurl.getText();
			if(url.length() > 0 && url.startsWith("http://")){
				try{
					jEditorPane1.setPage(url);
					history.add(url);
					historyIndex = history.size() - 1;
					//设置成非编辑状态jEditorPane1.setEditable(false);
					jEditorPane1.revalidate();
					jurl.setMaximumSize(jurl.getPreferredSize());
				}
				catch(Exception ex){
					JOptionPane.showMessageDialog(WebBrowser.this, "无法打开该搜索页","网页浏览器",JOptionPane.ERROR_MESSAGE);
				}
			}
			else if(url.length() > 0 && !url.startsWith("http://")){
				url = "http://" + url ;
				try{
					jEditorPane1.setPage(url);
					history.add(url);
					historyIndex = history.size() - 1;
					//设置成非编辑状态jEditorPane1.setEditable(false);
					jEditorPane1.revalidate();
				}
				catch(Exception ex){
					JOptionPane.showMessageDialog(WebBrowser.this,"无法打开该搜索页", "网页浏览器", JOptionPane.ERROR_MESSAGE);
				}
			}
			else if(url.length() == 0){
				JOptionPane.showMessageDialog(WebBrowser.this,"请输入链接地址", "网页浏览器",JOptionPane.ERROR_MESSAGE);
			}
		}
		//另存为...
		else if(e.getSource() == picSave || e.getSource() == saveAsItem){
			url = jurl.getText().toString().trim();
			if(url.length() > 0 && !url.startsWith("http://")){
				url = "http://" + url;
			}
			if(!url.equals("")){
				//保存文件
				saveFile(url);
			}
			else{
				JOptionPane.showMessageDialog(WebBrowser.this,"请输入链接地址","网页浏览器",JOptionPane.ERROR_MESSAGE);
			}
		}
		//退出
		else if(e.getSource() == exitItem || e.getSource() == picExit){
			System.exit(0);
		}
		//后退
		else if(e.getSource() == backItem || e.getSource() == picBack){
			historyIndex--;
			if(historyIndex < 0)
				historyIndex = 0;
			url = jurl.getText();
			try{
				//获得history对象中本地址之前访问的地址
				url = (String)history.get(historyIndex);
				jEditorPane1.setPage(url);
				jurl.setText(url.toString());
				//设置成非编辑状态jEditorPane1.setEditable(false):
				jEditorPane1.revalidate();
			}
			catch(Exception ex){
			}
		}
		//前进
		else if (e.getSource() == forwardItem || e.getSource() == picForward){
			historyIndex++;
			if(historyIndex >= history.size())
				historyIndex = history.size() - 1;
			url = jurl.getText();
			try{
				//获得history 对象中本地地址之后的地址
				url = (String)history.get(historyIndex);
				jEditorPane1.setPage(url);
				jurl.setText(url.toString());
				//设置成非编辑状态jEfditorPanel.setEditable(false);
				jEditorPane1.revalidate();
			}
			catch(Exception ex){
			}
		}
		//全屏
		else if(e.getSource() == fullscreenItem){
			boolean add_button2 = true;
			//获取屏幕大小
			Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
			
			Container content = window.getContentPane();
			content.add(bar,"North");
			content.add(scrollPane,"Center");
			
			//button2 为单击“全屏”后的还原按钮
			if(add_button2 == true){
				bar.add(button2);
			}
			//为button2添加事件
			button2.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt){
					WebBrowser.this.setEnabled(true);
					window.remove(bar);
					window.remove(toolBar);
					window.remove(scrollPane);
					window.setVisible(false);
					
					scrollPane.setPreferredSize(new Dimension(100,500));
					getContentPane().add(scrollPane,BorderLayout.SOUTH);
					getContentPane().add(bar,BorderLayout.CENTER);
					getContentPane().add(toolBar,BorderLayout.NORTH);
					bar.remove(button2);
					pack();
				}
			});
			window.setSize(size);
			window.setVisible(true);
		}
		//查看源文件
		else if(e.getSource() == sourceItem || e.getSource() == picView){
			url = jurl.getText().toString().trim();
			if(url.length() > 0 && !url.startsWith("http://")){
				url = "http://" + url ;
			}
			if( !url.equals("")){
				//根据url,获取源代码
				getHtmlSource(url);
				//生成显示源代码的框架对象
				ViewSourceFrame vsframe = new ViewSourceFrame(htmlSource);
				vsframe.setBounds(0,0,800,500);
				vsframe.setVisible(true);
			}
			else{
				JOptionPane.showMessageDialog(WebBrowser.this,"请输入链接地址","网页浏览器",JOptionPane.ERROR_MESSAGE);
			}
		}
		//刷新
		else if(e.getSource() == reloadItem){
			url = jurl.getText();
			if( url.length() > 0 && url.startsWith("http://")){
				try{
					jEditorPane1.setPage(url);
					//设置成非编辑状态jEditorPane1.setEditable(flase);
					jEditorPane1.revalidate();
				}
				catch(Exception ex){
				}
			}
			else if (url.length() > 0 && !url.startsWith("http://")){
				url = "http://" + url;
				try{
					jEditorPane1.setPage(url);
					//设置成非编辑状态jEditorPane1.setEditable(flase);
					jEditorPane1.revalidate();
				}
				catch(Exception ex){
				}
			}
		}
	}
	/*
	 * *保存文件
	 */
	void saveFile(final String url){
		final String linesep = System.getProperty("line.separator");
		chooser1.setCurrentDirectory(new File("."));
		chooser1.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser1.setDialogTitle("另存为...");
		if(chooser1.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
			return;
		this.repaint();
		Thread thread = new Thread(){
			public void run(){
				try{
					java.net.URL source = new URL(url);
					InputStream in = new BufferedInputStream(source.openStream());
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					File fileName = chooser1.getSelectedFile();
					FileWriter out = new FileWriter(fileName);
					BufferedWriter bw = new BufferedWriter(out);
					String line;
					while((line = br.readLine()) != null){
						bw.write(line);
						bw.newLine();
					}
					bw.flush();
					bw.close();
					out.close();
					String dMessage = url + " 已经被保存至" + linesep + fileName.getAbsolutePath();
					String dTitle = "另存为";
					int dType = JOptionPane.INFORMATION_MESSAGE;
					JOptionPane.showMessageDialog((Component)null, dMessage, dTitle, dType);
				}
				catch(java.net.MalformedURLException muex){
					JOptionPane.showMessageDialog((Component)null, muex.toString(),"网页浏览器", JOptionPane.ERROR_MESSAGE);
				}
				catch(Exception ex){
					JOptionPane.showMessageDialog((Component)null, ex.toString(),"网页浏览器", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		thread.start();
	}
	/*
	 * 获得源代码
	 */
	void getHtmlSource(String url){
		String linesep,htmlLine;
		linesep = System.getProperty("line.separator");
		htmlSource = "";
		try{
			java.net.URL source = new URL (url);
			InputStream in = new BufferedInputStream(source.openStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			while((htmlLine = br.readLine()) != null){
				htmlSource = htmlSource + htmlLine + linesep;
			}
		}
		catch(java.net.MalformedURLException muex){
			JOptionPane.showMessageDialog(WebBrowser.this, muex.toString(), "网页浏览器", JOptionPane.ERROR_MESSAGE);
		}
		catch(Exception ex){
			JOptionPane.showMessageDialog(WebBrowser.this, ex.toString(), "网页浏览器", JOptionPane.ERROR_MESSAGE);
		}
	}
	/*
	 * 实现监听器接口的hyperlinkUpdate函数
	 */
	public void hyperlinkUpdate(HyperlinkEvent e){
		try{
			if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
				jEditorPane1.setPage(e.getURL());
		}
		catch(Exception ex){
			ex.printStackTrace(System.err);
		}
	}
	/*生成一个IE对象*/
	public static void main(String[] args){
		try{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch(Exception e){
		}
		
		WebBrowser webBrowser = new WebBrowser();
		webBrowser.pack();
		webBrowser.setVisible(true);
	}
}