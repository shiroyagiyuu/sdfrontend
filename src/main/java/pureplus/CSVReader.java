package pureplus;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVReader implements AutoCloseable
{
	Reader  rd;
	char	buffer[];
	char	putc;
	int	pt,remain;
	boolean putted, line_end;

	public CSVReader(Reader rd) {
		this.rd = rd;
		buffer = new char[4096];
		pt = 0;
		remain = -1;
		putted = false;
		line_end = false;
	}

	public CSVReader(InputStream is) {
		this(new InputStreamReader(is));
	}

	public void putChar(char c) {
		putc = c;
		putted = true;
	}

	private String loadString()
	{
		StringBuilder  sb = new StringBuilder();
		char  c = getChar();
		while (c!='\"' && isAvailable()) {
			if (c=='\r') {
			} else {
				sb.append(c);
			}
			c = getChar();
		}
		//line_end = false;

		return sb.toString();
	}

	public char getChar() {
		if (putted) {
			putted = false;
			return putc;
		}
	
		if (pt<remain) {
			return buffer[pt++];
		} else {
			try {
			  	remain = rd.read(buffer);
				if (remain>0) {
					pt = 0;
					return buffer[pt++];
				}
			}catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return (char)0;
	}

	public boolean isLineAvailable() {
		return !line_end;
	}

	public boolean isAvailable() {
		return (!line_end) && (remain>0);
	}
	
	public String next() {
		char  c;
		StringBuilder  sb = new StringBuilder();

		if (line_end) { return null; }

		c = getChar();
		while (isAvailable()) {
			switch(c) {
			case ',':
				return sb.toString();
			case '\"':
				String res = loadString();
				c = getChar(); //,
				return res;
			case '\r':
				c = getChar();
			case '\n':
				line_end = true;
				return sb.toString();
			default:
				sb.append(c);
				c = getChar();
				break;
			}
		}
		line_end = true;

		return sb.toString();
	}

	public void nextLine() {
		if (line_end == true) {
			if (remain>0) {
				line_end = false;
			}
		}
	}

	public String[] readRow() throws IOException {
		ArrayList<String>  ary = new ArrayList<String>();

		while (isLineAvailable()) {
			ary.add(next());
		}
		nextLine();

		return ary.toArray(new String[ary.size()]);
	}

	public List<String[]> readAll() throws IOException {
		ArrayList<String[]>	tbl = new ArrayList<String[]>();

		while(isAvailable()) {
			tbl.add(readRow());
		}

		return tbl;
	}

	public void close() throws IOException {
		rd.close();
	}

	public static void main(String[] args)
	{
		int n=0;
		int maxtk=0;
		try {
			CSVReader  rd = new CSVReader(new FileReader(new File(args[0])));

			String[] tk = rd.readRow();
			for(n=0; rd.isAvailable(); n++) {
				tk = rd.readRow();
				if (maxtk < tk.length) {
					maxtk = tk.length;
				}
			}
		} catch(IOException ex) {
			ex.printStackTrace();
		}

		System.out.println("read " + n + " lines, maxcolumn= " + maxtk);
	}
}
