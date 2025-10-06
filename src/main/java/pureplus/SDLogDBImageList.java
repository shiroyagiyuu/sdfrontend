package pureplus;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SDLogDBImageList {
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
        String SQL_CREATETBL_COMP_IMAGE = "create table if not exists comp_image (id INTEGER PRIMARY KEY, prompt INTEGER, filename STRING);";

        try (Connection connection = DriverManager.getConnection(getDBName())) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(SQL_CREATETBL_COMP_IMAGE);
            }
        }
        catch(SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private int getPromptIDFromFilename(Connection connection, String filename) throws SQLException {
        String selectQuery = "SELECT prompt FROM image WHERE filename = ?;";
        int    prompt = -1;

        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            pstmt.setString(1,filename);

            ResultSet  rs = pstmt.executeQuery();
            if (rs.next()) {
                // Found
                prompt = rs.getInt("prompt");
            }
        }
        return prompt;
    }

    private void insertImageFile(Connection connection, int prompt, String filename) throws SQLException {
        String insertQuery = "INSERT INTO comp_image (prompt, filename) VALUES (?,?);";

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setInt(1,prompt);
            pstmt.setString(2,filename);

            pstmt.executeUpdate();
        }

        return;
    }

    void scanFileList(File image_dir) {
        if (image_dir.isDirectory()) {
            String[]  flist = image_dir.list();

            java.util.Arrays.sort(flist);
            int  prompt_id = -1;

            try (Connection connection = DriverManager.getConnection(getDBName())) {
                connection.setAutoCommit(false);
                for (int i=0; i<flist.length; i++) {
                    int  tmp_id;
                    String  fname = flist[i];

                    tmp_id = getPromptIDFromFilename(connection, fname);
                    if (tmp_id >= 0) { prompt_id = tmp_id; }
                    if (prompt_id < 0) {
                        System.err.println("Unknown prompt..?:"+fname);
                        continue;
                    }

                    insertImageFile(connection, prompt_id, fname);
                }
                connection.commit();
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    public SDLogDBImageList(String dst_path) {
        setDbFilename(dst_path);
        init();
    }

    public SDLogDBImageList() {
        this("sdlog.db");
    }

    public static void main(String[] args) {
        SDLogDBImageList   ilist = new SDLogDBImageList();
        File   f = new File(args[0]);
        System.out.println("search path="+f.getName());

        ilist.scanFileList(f);
    }
}
