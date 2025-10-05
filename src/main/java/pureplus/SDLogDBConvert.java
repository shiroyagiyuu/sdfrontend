package pureplus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.IOException;


public class SDLogDBConvert {
    String  dbFilename;

    public String getDbFilename() {
        return dbFilename;
    }

    public void setDbFilename(String dbFilename) {
        this.dbFilename = dbFilename;
    }

    public String getDBName() {
        return "jdbc:sqlite:" + dbFilename;
    }
    
    public void init() {
        String SQL_CREATETBL_IMAGE = "create table if not exists image (id INTEGER PRIMARY KEY, prompt INTEGER, filename STRING);";
        String SQL_CREATETBL_PROMPT = "create table if not exists prompt (id INTEGER PRIMARY KEY, " +
                                    "prompt STRING, negative_prompt STRING, " +
                                    "seed INTEGER, width INTEGER, height INTEGER, " +
                                    "sampler STRING, cfgs INTEGER, steps INTEGER, sd_model INTEGER);";
        String SQL_CREATETBL_SDMODEL = "create table if not exists sdmodel (id INTEGER PRIMARY KEY, sd_model_name STRING, sd_model_hash STRING);";
        try (Connection connection = DriverManager.getConnection(getDBName())) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(SQL_CREATETBL_IMAGE);
                statement.executeUpdate(SQL_CREATETBL_PROMPT);
                statement.executeUpdate(SQL_CREATETBL_SDMODEL);
            }
            catch (SQLException ex) {
                ex.printStackTrace(System.err);
            }
        }
        catch(SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private int insertPrompt(Connection connection, SDLog prompt, int sd_model) throws SQLException {
        String insertQuery = "INSERT INTO prompt (prompt, negative_prompt, seed, width, height, sampler, cfgs, steps, sd_model) VALUES (?,?,?,?,?,?,?,?,?);";
        int    res_id = -1;

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1,prompt.getPrompt());
            pstmt.setString(2,prompt.getNegativePrompt());
            pstmt.setLong(3,prompt.getSeed());
            pstmt.setInt(4,prompt.getWidth());
            pstmt.setInt(5,prompt.getHeight());
            pstmt.setString(6,prompt.getSampler());
            pstmt.setInt(7, prompt.getCfgs());
            pstmt.setInt(8, prompt.getSteps());
            pstmt.setInt(9, sd_model);

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();

            while(rs.next())
            {
                // read the result set
                res_id = rs.getInt(1);
                //System.out.println("id = " + res_id);
            }
        }

        return res_id;
    }

    private int insertSDModel(Connection connection, String sd_modelname, String sd_modelhash) throws SQLException {
        String selectQuery = "SELECT id FROM sdmodel WHERE sd_model_name = ? AND sd_model_hash = ?;";
        String insertQuery = "INSERT INTO sdmodel (sd_model_name, sd_model_hash) VALUES (?, ?);";

        int  id = -1;

        try (PreparedStatement pstmt_sel = connection.prepareStatement(selectQuery)) {
            pstmt_sel.setString(1, sd_modelname);
            pstmt_sel.setString(2, sd_modelhash);

            ResultSet  rs = pstmt_sel.executeQuery();
            if (rs.next()) {
                // Found
                id = rs.getInt("id");
            } else {
                // Not Found (new Model)
                try (PreparedStatement pstmt_ins = connection.prepareStatement(insertQuery)) {
                    pstmt_ins.setString(1, sd_modelname);
                    pstmt_ins.setString(2, sd_modelhash);

                    pstmt_ins.executeUpdate();

                    ResultSet  rs_ins = pstmt_ins.getGeneratedKeys();
                    while (rs_ins.next()) {
                        id = rs_ins.getInt(1);
                    }
                }
            }
        }

        return  id;
    }

    private void insertImage(Connection connection, int prompt, String filename) throws SQLException {
        String insertQuery = "INSERT INTO image (prompt, filename) VALUES (?,?);";

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setInt(1,prompt);
            pstmt.setString(2,filename);

            pstmt.executeUpdate();
        }
    }
    
	public SDLog parseLine(CSVReader csv) throws IOException {
		String[]  tk = csv.readRow();
		SDLog p = new SDLog();

		//debug
		/*
		for (int i=0;i<tk.length;i++) {
			System.out.println(""+i+":"+tk[i]);
		}
		System.out.println();
		*/

		if (tk.length>10) {
			p.setPrompt(tk[0]);
			p.setSeed(Long.parseLong(tk[1]));
			p.setWidth(Integer.parseInt(tk[2]));
			p.setHeight(Integer.parseInt(tk[3]));
			p.setSampler(tk[4]);
			p.setCfgs(Integer.parseInt(tk[5]));
			p.setSteps(Integer.parseInt(tk[6]));
			p.setFilename(tk[7]);
			p.setNegativePrompt(tk[8]);
			p.setSDModelName(tk[9]);
			p.setSDModelHash(tk[10]);

			return p;
		}

		return null;
	}

    private void addLog(Connection connection, SDLog param) throws SQLException {
        int prompt, sdmodel;
        sdmodel = insertSDModel(connection, param.getSDModelName(), param.getSDModelHash());
        prompt  = insertPrompt(connection, param, sdmodel);
        insertImage(connection, prompt, param.getFilename());   //TODO
    }

	public void readLog(Reader rd) {
		int     num=0;	

        try (Connection connection = DriverManager.getConnection(getDBName())) {
            connection.setAutoCommit(false);
		    try (CSVReader  csv = new CSVReader(rd)) {
			    csv.readRow();//load hdr;
                
                for (num=0; csv.isAvailable(); num++) {
                    SDLog  param = parseLine(csv);
                    if (param!=null) {
                        addLog(connection, param);
                    }
                }
		    } catch(IOException ex) {
			    ex.printStackTrace();
            }
            connection.commit();
		} catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
		System.out.println("read " + num + " lines");
	}

    public SDLogDBConvert(String dst_path) {
        setDbFilename(dst_path);
        init();
    }

    public SDLogDBConvert() {
        this("sdlog.db");
    }

    public static void main(String[] args) {
        SDLogDBConvert  conv = new SDLogDBConvert();
        try {
            conv.readLog(new FileReader(new File(args[0])));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
