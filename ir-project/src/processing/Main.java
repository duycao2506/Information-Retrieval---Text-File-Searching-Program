package processing;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.io.FileUtils;

import query.CosineCalculator;
import query.DistanceCalculator;
import query.DocResult;
import query.QueryServices;
import query.RelevanceCalculator;

public class Main extends JFrame implements MouseListener {
	private JPanel contentPane;
	private JTable table;
	private JLabel lblNumberOfDocuments;
	private JLabel lblNumberOfDocuments_1;
	private JLabel lblTotalSize;
	public static int totalDoc = 0;
	private static RelevanceCalculator rc1 = new CosineCalculator();
	private static RelevanceCalculator rc2 = new DistanceCalculator();
	private static RelevanceCalculator rcmain = rc1;
	private DocumentParser docParser = new DocumentParser();
	public static String dicPath = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "InvertedIndex/";
	private static String listOfFiles = dicPath + "filelist.txt";
	private ArrayList<File> files;
	DocResult[] docres = null;
	/**
	 * Launch the application.
	 */
	
	
	private void setNumberOfDoc(){
		String tmp = "Number of documents in Total " + Integer.toString(totalDoc);
		lblNumberOfDocuments.setText(tmp);
	}
	
	
	private void setTotalSize(){
		long size = FileUtils.sizeOfDirectory(new File("src/InvertedIndex"));
		String tmp = "KB";
		if (size > 1024){
			size /= 1024;
			tmp = "MB";
		}
		if (size > 1024){
			size /= 1024;
			tmp = "GB";
		}
		lblTotalSize.setText("Total Size " + size + tmp);
	}
	
	private void memorizePath(File[] files, boolean isOverwrite) throws IOException{
		PrintWriter out = new PrintWriter(new FileWriter(Main.listOfFiles,!isOverwrite));
		for (File f : files){
			out.append(f.getAbsolutePath() +"\n");
		}
		out.flush();
		out.close();
	}
	
	private void memorizeTotalDoc(int total, boolean isUpdate) throws IOException{
		int n = (isUpdate? this.getCurrentTotalDoc() : 0) + total;
		PrintWriter out = new PrintWriter(new FileWriter(Main.dicPath+"numdoc.txt"));
		out.append(Integer.toString(n));	
		out.flush();
		out.close();
	}
	
	private String getNWords(int n, File f){
		try {
			BufferedReader fr = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf16"));
			String ret = fr.readLine() + "...";
			return ret;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
	private int getCurrentTotalDoc(){
		int n = 0;
		try{
			BufferedReader br = new BufferedReader(new FileReader(new File(Main.dicPath + "numdoc.txt")));
			n = Integer.parseInt(br.readLine());
		}catch(FileNotFoundException fe){
			return 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		return n;
	}
	
	
	private ArrayList<File> getListFiles(){
		ArrayList<File> files = new ArrayList<File>();
		
		File listfiles = new File(Main.listOfFiles);
		
		BufferedReader br = null ;
		try{
			br = new BufferedReader(new FileReader(listfiles));
			for(String line; (line = br.readLine()) != null; ) {
		        // process the line.
				File tmp = new File(line);
				int type = tmp.isDirectory()? 1 : 0;
				type += tmp.exists()? 1 : -1;
				File[] a = new File[1];
				switch (type){
				case -1:
					File sa = new File("error");
					a[0] = sa;
					break;
				case 1:
					a[0] = tmp;
					break;
				case 2:
					a = tmp.listFiles(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							// TODO Auto-generated method stub
							return name.endsWith(".txt");
						}
					});
					break;
				default:
					break;
				}
				files.addAll(Arrays.asList(a));
		    }
		}catch(IOException io){
			return null;
		}
		
		return files;
	}
	
	private String setUpFolder() throws URISyntaxException{
		String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "InvertedIndex/";
		path = path.substring(1);
		String p1 = "file_sorted_1";
		String p2 = "file_sorted_2";
		File f = new File(path);
		f.mkdir();
		Path path1 = Paths.get(path);
		for (char i = 'a'; i <= 'z'; i++){
			String pathtmp = path + i+"/";
			new File(pathtmp).mkdir();
			new File(pathtmp+p1).mkdir();
			new File(pathtmp+p2).mkdir();
		}
		return path1.toString();
	}
	
	
	private void messageDone(){
		JOptionPane.showMessageDialog(this, "Import Done!");
	}
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void performSearch(String query) throws IOException{
		if (query == null || query.length() < 1 || totalDoc == 0)
		{
			JOptionPane.showMessageDialog(this, "There is nothing to query!");
			return;
		}
		this.files = this.getListFiles();
		QueryServices qs = new QueryServices(query, Main.totalDoc);
		rcmain.setMatrix(qs.buildDocMarix(dicPath));
		rcmain.setQuery(qs.buildDocVector());
		rcmain.makeTF_IDF();
		this.representResult(rcmain.getResult(), this.files, 0, 20);
	}
	
	public void representResult(DocResult[] results,ArrayList<File> files, int start, int end){
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		Object[][] data = new Object[results.length - start][2];
		dtm.setRowCount(0);
		this.docres = results;
		for (int i = start; i < results.length; i++){
			System.out.println(files.size()+"");
			data[i][0] = files.get(results[i].getId()).getName();
			data[i][1] = getNWords(100, files.get(results[i].getId()));
			dtm.addRow(data[i]);
		}
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setModel(dtm);
		
	}
	
	/**
	 * Create the frame.
	 */
	public Main() {
		setResizable(false);
		setTitle("CS419 Project 1 (Text Retrieval)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 764, 582);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		mnFile.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		mnFile.setMnemonic('F');
		menuBar.add(mnFile);
		
		JMenu mnImportData = new JMenu("Import data");
		mnImportData.setIcon(new ImageIcon("src/icon/importdata.png"));
		mnImportData.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		mnFile.add(mnImportData);
		
		JMenuItem mntmFromFiles = new JMenuItem("From Files");
		mntmFromFiles.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
		mntmFromFiles.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		mntmFromFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Select Files");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setMultiSelectionEnabled(true);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					try {
						totalDoc = 0;
						File[] files = chooser.getSelectedFiles();
						docParser.readFiles(files, true, totalDoc);
						totalDoc = docParser.totalDoc;
						memorizePath(files, true);
						memorizeTotalDoc(DocumentParser.totalDoc, false);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					setNumberOfDoc();
					setTotalSize();
					messageDone();
				}
			}
		});
		mnImportData.add(mntmFromFiles);
		
		JMenuItem mntmFromFolder = new JMenuItem("From Folder");
		mntmFromFolder.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mntmFromFolder.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		mntmFromFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Select Folder");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					try {
						totalDoc = 0;
						File[] files = new File[1];
						files[0] = chooser.getSelectedFile();
						docParser.readFolder(files[0].getPath());
						totalDoc = docParser.totalDoc;
						memorizePath(files, true);
						memorizeTotalDoc(DocumentParser.totalDoc, false);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					setNumberOfDoc();
					setTotalSize();
					messageDone();
				}
			}
		});
		mnImportData.add(mntmFromFolder);
		
		JMenuItem mntmAddFiles = new JMenuItem("Add Files");
		mntmAddFiles.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		mntmAddFiles.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		mntmAddFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Select Files");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setMultiSelectionEnabled(true);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					try {
						File[] files = chooser.getSelectedFiles();
						docParser.readFiles(files, false, totalDoc);
						memorizePath(files, false);
						memorizeTotalDoc(DocumentParser.totalDoc- Main.totalDoc,true);
						Main.totalDoc = DocumentParser.totalDoc;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					setNumberOfDoc();
					setTotalSize();
					messageDone();
				}
			}
		});
		mnFile.add(mntmAddFiles);
		
		JMenuItem mntmSaveLog = new JMenuItem("Save log");
		mntmSaveLog.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mntmSaveLog.setIcon(new ImageIcon("src/icon/savelog.png"));
		mntmSaveLog.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		mnFile.add(mntmSaveLog);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
		mntmExit.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		mnFile.add(mntmExit);
		
		JMenu mnOptions = new JMenu("Options");
		mnOptions.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		mnOptions.setMnemonic('O');
		menuBar.add(mnOptions);
		
		ButtonGroup jgroupmethod = new ButtonGroup();
		
		JRadioButtonMenuItem rdbtnmntmCosine = new JRadioButtonMenuItem("Cosine");
		rdbtnmntmCosine.setSelected(true);
		rdbtnmntmCosine.setName("method");
		rdbtnmntmCosine.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0));
		rdbtnmntmCosine.setFont(new Font("Dialog", Font.PLAIN, 20));
		rdbtnmntmCosine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Main.rcmain = Main.rc1;
				System.out.println("COS");
			}
		});;
		jgroupmethod.add(rdbtnmntmCosine);
		mnOptions.add(rdbtnmntmCosine);
		
		JRadioButtonMenuItem rdbtnmntmDistance = new JRadioButtonMenuItem("Distance");
		rdbtnmntmDistance.setName("method");
		rdbtnmntmDistance.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
		rdbtnmntmDistance.setFont(new Font("Dialog", Font.PLAIN, 20));
		rdbtnmntmDistance.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Main.rcmain = Main.rc2;
				System.out.println("DIS");
			}
		});
		jgroupmethod.add(rdbtnmntmDistance);
		mnOptions.add(rdbtnmntmDistance);
		
		JMenu mnHelp = new JMenu("Help");
		mnHelp.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		mnHelp.setMnemonic('H');
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		mntmAbout.setIcon(new ImageIcon("src/icon/about.png"));
		mntmAbout.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		mntmAbout.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, "DuHoTu GROUP\n- Cao Khac Le Duy 1351008\n- Nguyen Huy Hoang 1351016\n- Dinh Duy Tung 1351061");
			}
		});
		mnHelp.add(mntmAbout);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		
		lblNumberOfDocuments = new JLabel("Number of documents in Total");
		lblNumberOfDocuments.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		lblNumberOfDocuments_1 = new JLabel("Number of documents Found");
		lblNumberOfDocuments_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		lblTotalSize = new JLabel("Total Size");
		lblTotalSize.setFont(new Font("Tahoma", Font.PLAIN, 16));

		final JTextArea textArea = new JTextArea();
		
		JButton btnNewButton = new JButton("Search");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO: Implement Searching
				try {
					performSearch(textArea.getText());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
		
		JLabel lblhintDoubleclickTo = new JLabel("(Hint: double-click to open the document below)");
		lblhintDoubleclickTo.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNumberOfDocuments_1, GroupLayout.PREFERRED_SIZE, 217, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 157, Short.MAX_VALUE)
					.addComponent(lblhintDoubleclickTo)
					.addContainerGap())
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 748, Short.MAX_VALUE)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblNumberOfDocuments)
							.addPreferredGap(ComponentPlacement.RELATED, 146, Short.MAX_VALUE)
							.addComponent(lblTotalSize, GroupLayout.PREFERRED_SIZE, 271, GroupLayout.PREFERRED_SIZE))
						.addComponent(textArea, GroupLayout.DEFAULT_SIZE, 649, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNumberOfDocuments)
						.addComponent(lblTotalSize))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(textArea, GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNumberOfDocuments_1)
						.addComponent(lblhintDoubleclickTo))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 383, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		table = new JTable(){
			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		table.addMouseListener(this);
		table.setFont(new Font("Tahoma", Font.PLAIN, 16));
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Document Name", "Context"
			}
		));
		table.getColumnModel().getColumn(0).setPreferredWidth(248);
		table.getColumnModel().getColumn(1).setPreferredWidth(685);
		scrollPane.setViewportView(table);
		contentPane.setLayout(gl_contentPane);
		
		
		try {
			System.out.println(setUpFolder());
			Main.totalDoc = this.getCurrentTotalDoc();
			setNumberOfDoc();
		} catch (URISyntaxException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			System.out.println("Shit");
		}
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.getClickCount() == 2){
			JTable target = (JTable) e.getSource();
			int row = target.getSelectedRow();
			try {
				java.awt.Desktop.getDesktop().open(files.get(docres[row].getId()));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	}


	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
