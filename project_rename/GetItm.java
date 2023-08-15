package project_rename;

import java.io.File;
import java.util.Comparator;

public class GetItm {
	private String itmKind = ""; // ファイル種別
	private String itmAbsPath = ""; // 絶対Path
	private String itmName = ""; // 変更前名
	private String itmExet = ""; // 拡張子
	private String itmRename = ""; // 変更後名

	public GetItm(File f) {
		int yenIndex = f.getAbsolutePath().lastIndexOf("\\");
		this.itmAbsPath = f.getAbsolutePath().substring(0, yenIndex);
		if(f.isDirectory()) {
			// Directoryの場合
			this.itmKind = "D";
			this.itmName = f.getName();

		} else {
			// ファイルの場合
			int dotIndex = f.getName().lastIndexOf(".");
			this.itmKind = "F";
			this.itmName = f.getName().substring(0, dotIndex);
			this.itmExet = f.getName().substring(dotIndex + 1); // 先頭の"."除外のため+1
		}
	}

	public GetItm(
			String itmKind
			, String itmAbsPath
			, String itmName
			, String itmExet
			, String itmRename
			) {
		this.itmAbsPath = itmAbsPath;
		this.itmExet = itmExet;
		this.itmKind = itmKind;
		this.itmName = itmName;
		this.itmRename = itmRename;
	}

	public String getItmPath() {
		return this.itmAbsPath;
	}

	public String getItmExet() {
		return this.itmExet;
	}

	public String getItmName() {
		return this.itmName;
	}

	public String getItmRename() {
		return this.itmRename;
	}

	public void setItmRename(String rename) {
		this.itmRename = rename;
	}

	public String getItmKind() {
		return this.itmKind;
	}

	// String配列で返すメソッド
	public String[] chgObj() {
		return new String[]{
				this.getItmKind()
				, this.getItmPath()
				, this.getItmName()
				, this.getItmExet()
				, this.getItmRename()
				, "" // テーブル作成用に空白の値を付加して返す(変更前名の列に利用)
		};
	}

	public String toString() {
		return (
				this.itmKind + "," + 
						this.itmAbsPath + "," +
						this.itmName + "," +
						this.itmExet + "," +
						this.itmRename
				);
	}
}

/* ソート用のクラス */
class ItemSort implements Comparator<GetItm>{
	private int selectNum;

	public ItemSort(int num) {
		this.selectNum = num;
	}

	@Override
	public int compare(GetItm o1, GetItm o2) {
		switch(selectNum) {
		case 1:
			// 拡張子でのsort
			return o1.getItmExet().compareTo(o2.getItmExet());

		case 2:
			// ファイル種別でのsort
			return o1.getItmKind().compareTo(o2.getItmKind());

		default:
			// 基本名前でsort
			return o1.getItmName().compareTo(o2.getItmName());
		}
	}
}

/* ファイル名変更用のクラス */
class ItemRename {
	static boolean renameGo(String... str) {

		File f1 = new File(str[0]);
		File f2 = new File(str[1]);
		
		/*
		*  変更元ファイルが存在し、かつ変更後ファイル名が存在しない場合
		* ファイル名変更を実施する
		*/
		if(f1.exists() && !(f2.exists())) {
			return f1.renameTo(f2);
		}
		return false;
	}
}

/* Directoryの存在チェッククラス */
class DirChk{

	static boolean directCheck(String dir) {
		// ファイルが存在しており、かつディレクトリであればTrue
		return (new File(dir).exists() && new File(dir).isDirectory());
	}
}