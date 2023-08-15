package project_rename;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class RenameFrameGUI extends JFrame {
	private JPanel basePane;
	private DefaultTableModel defTbl;
	private JTable itmTbl;
	private JScrollPane tblScroll;
	private JTextField dirPath, newNameTxt;
	private List<GetItm> getItmList;
	private JComboBox<String> numFormat;
	private StringBuilder msgStr = new StringBuilder();
	private final String TITLE = "ファイル名変更ツール";
	private final String DIR_GET_ERROR_TITLE = "Directory Check";
	
	final String[] COL_TITLE = {
			"種類"
			, "格納ディレクトリ"
			, "現在名"
			, "拡張子"
			, "変更後名"
			, "変更前名"};

	final Map<String, Integer> NUM_FOMR_DICT = new TreeMap<>(){{
		put("0", 1);
		put("00", 2);
		put("000", 3);
		put("0000", 4);
		put("00000", 5);
	}};

	public RenameFrameGUI() {
		setTitle(TITLE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1024, 600);
		basePane = new JPanel();
		basePane.setSize(new Dimension(1024, 0));
		basePane.setPreferredSize(new Dimension(1024, 10));
		basePane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(basePane);
		basePane.setLayout(new BorderLayout(0, 0));

		JPanel topPane = new JPanel();
		FlowLayout flowLayout = (FlowLayout) topPane.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		topPane.setMinimumSize(new Dimension(10, 35));
		topPane.setMaximumSize(new Dimension(32767, 35));
		topPane.setPreferredSize(new Dimension(10, 35));
		basePane.add(topPane, BorderLayout.NORTH);

		JLabel dirLabel = new JLabel("対象ディレクトリ：");
		dirLabel.setMinimumSize(new Dimension(50, 13));
		dirLabel.setPreferredSize(new Dimension(110, 13));
		dirLabel.setMaximumSize(new Dimension(110, 13));
		topPane.add(dirLabel);

		dirPath = new JTextField();
		dirPath.setMinimumSize(new Dimension(50, 19));
		dirPath.setPreferredSize(new Dimension(150, 19));
		topPane.add(dirPath);
		dirPath.setColumns(30);

		JButton dirSelPut = new JButton("ディレクトリ選択");
		dirSelPut.addActionListener(new OpenDir());
		topPane.add(dirSelPut);

		JPanel leftPane = new JPanel();
		leftPane.setMaximumSize(new Dimension(250, 32767));
		leftPane.setPreferredSize(new Dimension(250, 10));
		basePane.add(leftPane, BorderLayout.WEST);
		SpringLayout sl_leftPane = new SpringLayout();
		leftPane.setLayout(sl_leftPane);
		
		JLabel toolTitleLabel = new JLabel(TITLE);
		toolTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		toolTitleLabel.setFont(new Font("MS UI Gothic", Font.BOLD, 18));
		sl_leftPane.putConstraint(SpringLayout.NORTH, toolTitleLabel, 64, SpringLayout.NORTH, leftPane);
		sl_leftPane.putConstraint(SpringLayout.WEST, toolTitleLabel, 27, SpringLayout.WEST, leftPane);
		sl_leftPane.putConstraint(SpringLayout.SOUTH, toolTitleLabel, 116, SpringLayout.NORTH, leftPane);
		sl_leftPane.putConstraint(SpringLayout.EAST, toolTitleLabel, 213, SpringLayout.WEST, leftPane);
		leftPane.add(toolTitleLabel);

		// Tableの挿入
		defTbl = new DefaultTableModel(null, COL_TITLE) {
			// 変更後名称のセル以外は編集不可とする
			public boolean isCellEditable(int row, int col) {
				if(col == 4) {return true;}
				return false;}
		};
		
		// 対象セルの入力値に禁止文字がないかチェック
		defTbl.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				if(e.getType() == TableModelEvent.UPDATE) {
					int inRow = e.getFirstRow();
					String inName = defTbl.getValueAt(inRow, 4).toString();
					if(!(errStrChk(inName))){
						// 禁止文字があった場合は対象セルを空白にする
						defTbl.setValueAt("", inRow, 4);
					}
				}
			}
		});
		
		itmTbl = new JTable(defTbl);
		// Tableの列幅の指定
		itmTbl.getColumnModel().getColumn(0).setPreferredWidth(20);
		itmTbl.getColumnModel().getColumn(1).setPreferredWidth(120);
		itmTbl.getColumnModel().getColumn(2).setPreferredWidth(60);
		itmTbl.getColumnModel().getColumn(3).setPreferredWidth(20);
		tblScroll = new JScrollPane(itmTbl);
		basePane.add(tblScroll, BorderLayout.CENTER);		

		JPanel rightPane = new JPanel();
		rightPane.setPreferredSize(new Dimension(250, 10));
		rightPane.setMaximumSize(new Dimension(250, 32767));
		basePane.add(rightPane, BorderLayout.EAST);
		rightPane.setLayout(null);

		JLabel selCategoryLabel = new JLabel("選択項目");
		selCategoryLabel.setBounds(10, 57, 71, 13);
		rightPane.add(selCategoryLabel);

		JLabel changeNameLabel = new JLabel("変更名");
		changeNameLabel.setBounds(10, 10, 71, 13);
		rightPane.add(changeNameLabel);

		newNameTxt = new JTextField();
		newNameTxt.setBounds(10, 28, 229, 19);
		rightPane.add(newNameTxt);
		newNameTxt.setColumns(10);

		JLabel nameNumLabel = new JLabel("連番桁数");
		nameNumLabel.setBounds(10, 161, 71, 13);
		rightPane.add(nameNumLabel);

		numFormat = new JComboBox<String>();
		for(String k: NUM_FOMR_DICT.keySet()) {
			numFormat.addItem(k);
		}

		numFormat.setBounds(94, 161, 121, 19);
		rightPane.add(numFormat);
		
		// 名前一括挿入
		JButton insertNamePut = new JButton("一括挿入");
		insertNamePut.setBounds(74, 190, 121, 21);
		insertNamePut.addActionListener(new InsertName());
		rightPane.add(insertNamePut);

		// 名前変更ボタン
		JButton renameStart = new JButton("名前変更Go！");
		renameStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getRenameData(0);
			}
		});

		renameStart.setBounds(46, 276, 149, 36);
		rightPane.add(renameStart);

		JButton reverseName = new JButton("変更前の名前に戻す");
		reverseName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getRenameData(1);				
			}
		});

		// 名前戻しボタン
		reverseName.setBounds(46, 360, 149, 36);
		rightPane.add(reverseName);

		JButton selectBtn1 = new JButton("全選択");
		selectBtn1.setPreferredSize(new Dimension(75, 21));
		selectBtn1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectTbl(0);
			}
		});
		selectBtn1.setBounds(94, 57, 121, 21);
		rightPane.add(selectBtn1);

		JButton selectBtn2 = new JButton("ファイルのみ");
		selectBtn2.setPreferredSize(new Dimension(75, 21));
		selectBtn2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectTbl(1);
			}
		});
		selectBtn2.setBounds(94, 88, 121, 21);
		rightPane.add(selectBtn2);

		JButton selectBtn3 = new JButton("ディレクトリのみ");
		selectBtn3.setPreferredSize(new Dimension(75, 21));
		selectBtn3.setRolloverEnabled(false);
		selectBtn3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectTbl(2);				
			}
		});		
		selectBtn3.setBounds(94, 119, 121, 21);
		rightPane.add(selectBtn3);
		
	}

	/* 空テーブルの挿入処理 */
	private void initTable() {
		defTbl.setRowCount(0);
	}

	/* データをテーブル追加 */
	private void getNewData(List<GetItm> gl) {
		// 最初にデータを空にする
		initTable();

		// 取得データをテーブルに追加する
		for(GetItm g: gl) {
			defTbl.addRow(g.chgObj());
		}
	}
	
	/* 対象ディレクトリのチェック */
	private void dirFormChk(String getDir) {
		int diaTitleCode = 0;

		if(!DirChk.directCheck(getDir)) {
			// ディレクトリの存在チェック
			msgStr.append(
					"<html>対象ディレクトリが存在しません。<br>"
							+ "ディレクトリのパスを確認して下さい。</html>"
					);

			getDir = null;
			initTable();

		} else {
			getItmList = new ArrayList<>();

			for(File g: new File(getDir).listFiles()) {
				getItmList.add(new GetItm(g));
			}

			/* データの並び替えを実行 */
			getItmList.sort(new ItemSort(0)); // 名前順
			getItmList.sort(new ItemSort(1)); // 拡張子順
			getItmList.sort(new ItemSort(2).reversed()); // ファイル種別順

			/* 取得データの追加メソッド */
			getNewData(getItmList);
			msgStr.append("対象ディレクトリの情報を取得しました。");
			diaTitleCode = 1;
			getItmList = null; // データ取得が終了後開放する
		}

		openDialog(diaTitleCode, msgStr.toString(), DIR_GET_ERROR_TITLE);
		dirPath.setText("");
		dirPath.setText(getDir); // フォルダ選択エリアに埋め込み
	}

	/* 選択ボタン押下時の処理 */
	private void selectTbl(int btnNum) {
		if(defTbl.getRowCount() > 0) {
			// Tabaleデータが存在する場合のみ有効にする
			int startRow = defTbl.getRowCount() - 1;
			int endRow = 0;
			int count = 0;
			// いったん選択を解除
			itmTbl.removeRowSelectionInterval(startRow, endRow);
			
			if(btnNum == 0) {
				count = startRow;
			} else {
				String tgtType = "";
				// FileかDirectoryかを判定させる為の値を格納
				switch(btnNum) {
				case 1:
					tgtType = "F";
					break;
					
				case 2:
					tgtType = "D";
					break;
				}
				
				// Loopで開始位置と最終位置を取得
				for(int i = 0; i < defTbl.getRowCount(); ++i) {
					if(defTbl.getValueAt(i, 0).toString().equals(tgtType)) {
						startRow = Math.min(startRow, i);
						endRow = Math.max(endRow, i);
						++count;
					}
				}
			}

			if(count > 0) {
				// 対象行数が1以上の場合のみ行選択を有効にする
				itmTbl.setRowSelectionInterval(startRow, endRow);
			}
		}
	}
	
	/* ダイアログ表示オプション	*/
	private void openDialog(int diaNum, String... msg) {
		/*
		 * 第四引数には、通常"JOption.INFOMATION_MESSAGE"や"JOption.ERROR_MASSAGE"を
		 * 指定するが、どうやらint型の値が格納されている模様
		 * -1=PLAIN, 0=ERROR, 1=INFOMATION, 2=WARNING, 3=QUESTION
		 */
		JOptionPane.showMessageDialog(
				RenameFrameGUI.this // RenameFrameGUIを親Componentとして指定する
				,msg[0]
				,msg[1]
				,diaNum
				);
		this.msgStr.setLength(0); // メッセージ表示後はmsgStrを空にする
	}

	/* ファイル名への禁止文字列チェック */
	private boolean errStrChk(String str) {
		// \/:*?"<>| はファイル名に利用不可
		if(!(str.matches(".*[\\/:*?\"<>|].*"))) {
			return true;
		} 
		
		this.msgStr.append("￥/:*?\"<>| は利用不可文字列です。");
		openDialog(0, this.msgStr.toString(),"FileName Check" );
		return false;
	}

	/* 名前変更処理の下準備 */
	private void getRenameData(int num) {
		StringBuilder s1 = new StringBuilder();
		StringBuilder s2 = new StringBuilder();

		String orNm;
		String chNm;
		int tblCol = (num == 0 ? 4 : 5); // 通常変更の場合4列目、戻しの場合5列目を指定
				
		for(int i = 0; i < defTbl.getRowCount(); ++i) {
			orNm = chNm = "";                                // orNmとchNmを一括初期化
			chNm = defTbl.getValueAt(i, tblCol).toString() ; // 変更後名称の挿入
			if(chNm.equals("") || chNm == null){ continue; } // 対象変更名が空の場合は次のループに移動
			
			s2.append(defTbl.getValueAt(i, 1).toString() + "\\"); // Directory情報の格納
			s2.append(chNm);

			orNm = defTbl.getValueAt(i, 2).toString() ;
			s1.append(defTbl.getValueAt(i, 1).toString() + "\\"); // Directory情報の格納
			s1.append(orNm); // 変更対象ファイル名を結合

			/*
			 * 対象がファイルの場合拡張子を結合
			 */
			if(defTbl.getValueAt(i, 0).toString().equals("F")) {
				s1.append("." + defTbl.getValueAt(i, 3).toString());
				s2.append("." + defTbl.getValueAt(i, 3).toString());
			}
			
			if(ItemRename.renameGo(s1.toString(), s2.toString())) {
				// 名前変更が成功した時にテーブルの情報を更新する
				defTbl.setValueAt(chNm, i, 2);
				if(num == 0) { defTbl.setValueAt(orNm, i, 5); } // 通常変更の場合、変更前名のフィールドも更新
			}
			
			/* StringBuilderを空にする */
			s1.setLength(0);
			s2.setLength(0);
		}

		this.msgStr.append("ファイル名変更処理が終了しました。");
		openDialog(1, this.msgStr.toString(), "File Rename Status");
	}

	/* Directory選択ボタン押下時の動作 */
	private class OpenDir implements ActionListener{
		private String diaTitle = "Directory Check";
		
		public void actionPerformed(ActionEvent e) {
			String getDir = dirPath.getText();

			if(!(getDir.trim().equals("")) && getDir != null) {
				// Pathフィールドが空でなければディレクトリチェックを行う	
				if(!(DirChk.directCheck(getDir))) {
					// ディレクトリが存在しないとき、エラーメッセージとカレントディレクトリを変更
					msgStr.append(
							"<html>対象ディレクトリが存在しないか、ディレクトリではありません。<br>"
									+ "ディレクトリのパスを確認して下さい。</html>"
					);
					
					openDialog(0, msgStr.toString(), diaTitle);
					dirPath.setText("");
					initTable();
					getDir = null;
					// 存在しないディレクトリが指定されていた場合nullを挿入する事でデフォルトディレクトリが開くように設定する
				}
			}

			JFileChooser directOpen = new JFileChooser(getDir);
			
			directOpen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Directoryのみ選択できる様にする
			int opnFlg = directOpen.showOpenDialog(RenameFrameGUI.this); // ファイルを開くダイアログの起動
			
			if(opnFlg == 0) {
				/* 開くボタン押下時の動作 */
				
				File getFile = directOpen.getSelectedFile(); // 選択されたファイルの情報を取得
				getDir = getFile.getAbsolutePath(); // 取得したファイル情報から絶対パスを取得
				dirFormChk(getDir); // ディレクトリ情報取得のメソッド呼び出し
			}
		}
	}

	/* 変更後名の一括挿入 */
	private class InsertName implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			// 選択行への新名挿入
			String tmpRename = newNameTxt.getText();
			if(!(tmpRename.trim().equals("")) && tmpRename != null){
				// 禁止文字列のチェック
				if(errStrChk(tmpRename)) {
					// getSelectedRowsで配列として選択行が取得される
					int[] tgtSelRow = itmTbl.getSelectedRows();
					int j = 0;
					int selNumFrm = NUM_FOMR_DICT.get(numFormat.getSelectedItem());
					for(int i : tgtSelRow) {
						String inNum = String.format("%0" + selNumFrm + "d", ++j);
						defTbl.setValueAt(tmpRename + inNum, i, 4);
					}
				} else {
					// 禁止文字列がある場合、入力文字を削除
					newNameTxt.setText("");
				}
			}
		}
	}
}
